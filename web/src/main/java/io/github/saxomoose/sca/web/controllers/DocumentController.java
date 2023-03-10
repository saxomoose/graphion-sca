package io.github.saxomoose.sca.web.controllers;

import io.github.saxomoose.sca.web.dtos.Notification;
import io.github.saxomoose.sca.web.enums.DatabaseConstraint;
import io.github.saxomoose.sca.web.enums.NotificationType;
import io.github.saxomoose.sca.web.exceptions.DatabaseConstraintViolationException;
import io.github.saxomoose.sca.web.exceptions.NullOrEmptyFileException;
import io.github.saxomoose.sca.web.exceptions.UnsupportedFileFormatException;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.services.DocumentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
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
    ) {
        final var redirectView = new RedirectView();
        if (!bindingResult.hasErrors()) {
            try {
                documentService.create(documentDTO, documentDTO.getFile());
            } catch (IllegalArgumentException e) {
                if (e.getCause() instanceof UnsupportedFileFormatException) {
                    bindingResult.rejectValue("file", "unsupportedfileformat");
                } else if (e.getCause() instanceof NullOrEmptyFileException) {
                    bindingResult.rejectValue("file", "empty");
                } else if (e.getCause() instanceof DatabaseConstraintViolationException) {
                    if (((DatabaseConstraintViolationException) e.getCause()).getDatabaseConstraint()
                            == DatabaseConstraint.unique_constraint_document_friendly_filename) {
                        bindingResult.rejectValue("friendlyFilename", "duplicate");
                    }
                }
            }
            if (!bindingResult.hasErrors()) {
                redirectView.setUrl("/documents");
                redirectAttributes.addFlashAttribute(
                        "notifications",
                        List.of(new Notification(NotificationType.SUCCESS, "notification.success", "documents.create.success")));
            }
        }

        if (bindingResult.hasErrors()) {
            redirectView.setUrl("/documents/create");
            final var validationErrors = new ArrayList<Notification>();
            for (final var fieldError : bindingResult.getFieldErrors()) {
                //noinspection DataFlowIssue
                validationErrors.add(new Notification(NotificationType.ERROR, "notification.error.validation", fieldError.getCodes()[0]));
            }
            redirectAttributes.addFlashAttribute("notifications", validationErrors);
        }
        return redirectView;
    }

    @GetMapping("/files/{tenant}/{file}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(
            @PathVariable Integer tenant,
            @PathVariable UUID file) {

        final var inputStreamResource = documentService.getFile(tenant, file);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(inputStreamResource);
    }
}
