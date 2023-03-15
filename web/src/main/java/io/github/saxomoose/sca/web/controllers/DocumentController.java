package io.github.saxomoose.sca.web.controllers;

import io.github.saxomoose.sca.web.dtos.Notification;
import io.github.saxomoose.sca.web.enums.NotificationType;
import io.github.saxomoose.sca.web.exceptions.BusinessLogicException;
import io.github.saxomoose.sca.web.exceptions.ObjectStorageException;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.services.DocumentService;
import io.github.saxomoose.sca.web.services.TenantService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
public class DocumentController {
    private final TenantService tenantService;
    private final DocumentService documentService;


    @Autowired
    public DocumentController(TenantService tenantService, DocumentService documentService) {
        this.tenantService = tenantService;
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
            final Principal principal,
            final HttpServletRequest request,
            final RedirectAttributes redirectAttributes
            ) throws IOException, IllegalArgumentException, ObjectStorageException, ServletException, BusinessLogicException {
        final var parts = request.getParts();
        if (parts.size() != 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        io.github.saxomoose.sca.web.dtos.Document document = null;
        Long clientSideFileSize = null;
        InputStream fileInputStream = null;
        for (var part: parts) {
            if (part.getName().equals("fileSize")) {
                final var bytes = part.getInputStream().readAllBytes();
                if (bytes.length == 0) {
                    throw new BusinessLogicException("document.file.empty");
                }
                clientSideFileSize = ByteBuffer.wrap(bytes).getLong();
            } else if (part.getName().equals("filename")) {
                final var filename = new String(part.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                document = io.github.saxomoose.sca.web.dtos.Document.of(filename);
            } else if (part.getName().equals("file")) {
                Objects.requireNonNull(clientSideFileSize);
                if (!part.getContentType().equals(MediaType.APPLICATION_PDF.toString())) {
                    throw new BusinessLogicException("document.file.unsupportedfileformat");
                } else if (part.getSize() != clientSideFileSize) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
                fileInputStream = part.getInputStream();
            }
        }
        Objects.requireNonNull(clientSideFileSize);
        Objects.requireNonNull(document);
        Objects.requireNonNull(fileInputStream);
        final var tenant = tenantService.getTenantByUsername(principal.getName());
        documentService.create(tenant, document, clientSideFileSize, fileInputStream);
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
            final Principal principal,
            @PathVariable final UUID file,
            @RequestParam final Boolean download
    ) throws ObjectStorageException {
        final var tenant = tenantService.getTenantByUsername(principal.getName());
        final var document = documentService.getDocument(tenant.getId(), file);
        final var inputStreamResource = documentService.getFile(tenant.getBucket(), document.getPath());
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
            BusinessLogicException.class,
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
        List<Notification> notifications;
        switch (exception) {
            case BusinessLogicException businessLogicException -> notifications = List.of(new Notification(
                    NotificationType.ERROR,
                    "notification.error.validation",
                    businessLogicException.getMessageKey())
            );
            case ObjectStorageException objectStorageException -> notifications = List.of(new Notification(
                    NotificationType.ERROR,
                    "notification.error",
                    objectStorageException.getMessageKey())
            );
            default -> throw new IllegalStateException(exception);
        }
        redirectAttributes.addFlashAttribute("notifications", notifications);
        return new RedirectView("/documents/create");
    }
}
