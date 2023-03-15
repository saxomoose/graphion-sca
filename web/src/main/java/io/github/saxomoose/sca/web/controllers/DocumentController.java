package io.github.saxomoose.sca.web.controllers;

import io.github.saxomoose.sca.web.dtos.Notification;
import io.github.saxomoose.sca.web.enums.DatabaseConstraint;
import io.github.saxomoose.sca.web.enums.NotificationType;
import io.github.saxomoose.sca.web.exceptions.DatabaseConstraintViolationException;
import io.github.saxomoose.sca.web.exceptions.ObjectStorageException;
import io.github.saxomoose.sca.web.exceptions.ValidationException;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.services.DocumentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
        final var modelAndView = new ModelAndView("documents/create");
        final var inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            modelAndView.addObject("notifications", inputFlashMap.get("notifications"));
        }
        return modelAndView;
    }

    @PostMapping(
            value = "/documents",
            consumes = "multipart/form-data"
    )
    public RedirectView store(
            final HttpServletRequest request,
            final RedirectAttributes redirectAttributes
            ) throws IOException, IllegalArgumentException, ObjectStorageException, ServletException, ValidationException {
        final var parts = request.getParts();
        io.github.saxomoose.sca.web.dtos.Document document = null;
        InputStream inputStream = null;
        for (var part: parts) {
            if (part.getName().equals("filename")) {
                final var filename = new String(part.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                document = io.github.saxomoose.sca.web.dtos.Document.of(filename);
            } else if (part.getName().equals("file")) {
                if (!part.getContentType().equals(MediaType.APPLICATION_PDF.toString())) {
                    throw new ValidationException("document.file.unsupportedfileformat");
                } else if (part.getSize() == 0) {
                    throw new ValidationException("document.file.empty");
                }
                inputStream = part.getInputStream();
            }
        }
        if (document != null && inputStream != null) {
            documentService.create(document, inputStream);
        }
        redirectAttributes.addFlashAttribute(
                "notifications",
                List.of(new Notification(NotificationType.SUCCESS, "notification.success", "documents.create.success")));
        return new RedirectView("/documents");
    }

    @GetMapping(
            value = "/files/{tenant}/{file}",
            produces = "application/pdf")
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
            IllegalArgumentException.class,
            ObjectStorageException.class
    })
    public RedirectView handle(
            final HttpServletRequest request,
            final Exception exception,
            final RedirectAttributes redirectAttributes
    ) throws Exception {
        if (!request.getServletPath().equals("/documents")) {
            throw exception;
        }
        List<Notification> notifications = null;
        switch (exception) {
            case IllegalArgumentException illegalArgumentException -> {
                final var headerKey = "notification.error.validation";
                switch (illegalArgumentException.getCause()) {
                    case ValidationException validationException -> notifications = List.of(new Notification(
                            NotificationType.ERROR,
                            headerKey,
                            validationException.getMessageKey())
                    );
                    case DatabaseConstraintViolationException databaseConstraintViolationException -> {
                        if (databaseConstraintViolationException.getDatabaseConstraint() == DatabaseConstraint.unique_constraint_document_filename) {
                            notifications = List.of(new Notification(
                                    NotificationType.ERROR,
                                    headerKey,
                                    "document.filename.duplicate")
                            );
                        }
                    }
                    default -> throw new IllegalStateException(illegalArgumentException);
                }
            }
            case ObjectStorageException ignored -> notifications = List.of(new Notification(
                    NotificationType.ERROR,
                    "notification.error",
                    "TODO")
            );
            default -> throw new IllegalStateException(exception);
        }
        if (notifications != null) {
            redirectAttributes.addFlashAttribute("notifications", notifications);
        }
        return new RedirectView("/documents/create");
    }
}
