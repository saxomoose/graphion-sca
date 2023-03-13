package io.github.saxomoose.sca.web.controllers;

import io.github.saxomoose.sca.web.dtos.Notification;
import io.github.saxomoose.sca.web.enums.DatabaseConstraint;
import io.github.saxomoose.sca.web.enums.NotificationType;
import io.github.saxomoose.sca.web.exceptions.*;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.services.DocumentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
public class DocumentController {
    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/documents")
    public ModelAndView index(
            final HttpServletRequest request
    ) {
        final var tenantId = ((Tenant) request.getSession().getAttribute("tenant")).getId();
        final var documents = documentService.getDocuments(tenantId);
        final var modelAndView = new ModelAndView("documents/index");
        modelAndView.addObject("documents", documents);

        final var inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            modelAndView.addObject("notifications", inputFlashMap.get("notifications"));
        }
        return modelAndView;
    }



    @GetMapping("/documents/create")
    public ModelAndView create(
            final HttpServletRequest request
    ) {
        final var documentDTO = new io.github.saxomoose.sca.web.dtos.Document();
        final var modelAndView = new ModelAndView("documents/create");
        modelAndView.addObject("document", documentDTO);

        final var inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            modelAndView.addObject("notifications", inputFlashMap.get("notifications"));
        }
        return modelAndView;
    }

    @PostMapping("/documents")
    public RedirectView store(
            @Valid @ModelAttribute final io.github.saxomoose.sca.web.dtos.Document documentDTO,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes
    ) throws ValidationException, IllegalArgumentException, ObjectStorageException {
        final var redirectView = new RedirectView();
        if (!bindingResult.hasErrors()) {
            documentService.create(documentDTO, documentDTO.getFile());
        } else {
            throw new ValidationException(bindingResult);
        }
        redirectView.setUrl("/documents");
        redirectAttributes.addFlashAttribute(
                "notifications",
                List.of(new Notification(NotificationType.SUCCESS, "notification.success", "documents.create.success")));
        return redirectView;
    }

    @GetMapping("/files/{tenant}/{file}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(
            @PathVariable final Integer tenant,
            @PathVariable final UUID file,
            @RequestParam final Boolean download
    ) throws ObjectStorageException {
        final var document = documentService.getDocument(tenant, file);
        final var inputStreamResource = documentService.getFile(tenant, document.getPath());
        return ResponseEntity.ok()
                .headers(httpHeaders -> {
                    httpHeaders.setContentType(MediaType.APPLICATION_PDF);
                    if (download) {
                        httpHeaders.setContentDisposition(ContentDisposition
                                .attachment()
                                .filename(String.format("%s.pdf", document.getFilename()))
                                .build());
                    }
                })
                .body(inputStreamResource);
    }

    @ExceptionHandler(value = {
            ValidationException.class,
            IllegalArgumentException.class,
            ObjectStorageException.class
    })
    public RedirectView handle(
            final Exception exception,
            final RedirectAttributes redirectAttributes
    ) throws IllegalStateException {
        final var notifications = new ArrayList<Notification>();
        switch (exception) {
            case ValidationException validationException -> {
                for (final var fieldError : validationException.getBindingResult().getFieldErrors()) {
                    notifications.add(new Notification(
                            NotificationType.ERROR,
                            "notification.error.validation",
                            Objects.requireNonNull(fieldError.getCodes())[0]));
                }
            }
            case IllegalArgumentException illegalArgumentException -> {
                switch (illegalArgumentException.getCause()) {
                    case UnsupportedFileFormatException ignored ->
                            notifications.add(new Notification(
                                    NotificationType.ERROR,
                                    "notification.error.validation",
                                    "unsupportedfileformat.document.file"));
                    case InvalidFileException ignored ->
                            notifications.add(new Notification(
                                    NotificationType.ERROR,
                                    "notification.error.validation",
                                    "empty.document.file"));
                    case DatabaseConstraintViolationException databaseConstraintViolationException -> {
                        if (databaseConstraintViolationException.getDatabaseConstraint() == DatabaseConstraint.unique_constraint_document_filename) {
                            notifications.add(new Notification(
                                    NotificationType.ERROR,
                                    "notification.error.validation",
                                    "duplicate.document.file"));
                        }
                    }
                    default -> throw new IllegalStateException(illegalArgumentException);
                }
            }
            case ObjectStorageException ignored ->
                    notifications.add(new Notification(
                            NotificationType.ERROR,
                            "notification.error.objectstorage",
                            "TODO"));
            default -> throw new IllegalStateException(exception);
        }
        final var redirectView = new RedirectView("/documents/create");
        redirectAttributes.addFlashAttribute("notifications", notifications);
        return redirectView;
    }
}
