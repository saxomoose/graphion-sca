package io.github.saxomoose.sca.web.controllers;

import io.github.saxomoose.sca.web.utils.Notification;
import io.github.saxomoose.sca.web.enums.NotificationType;
import io.github.saxomoose.sca.web.exceptions.BusinessLogicException;
import io.github.saxomoose.sca.web.exceptions.ObjectStorageException;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.services.DocumentService;
import io.github.saxomoose.sca.web.services.ObjectStorageService;
import io.github.saxomoose.sca.web.services.TenantService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
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
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Controller
public class DocumentController {
    private final HttpSession httpSession;
    private final TenantService tenantService;
    private final DocumentService documentService;
    private final ObjectStorageService objectStorageService;

    @Autowired
    public DocumentController(HttpSession httpSession, TenantService tenantService, DocumentService documentService, ObjectStorageService objectStorageService) {
        this.httpSession = httpSession;
        this.tenantService = tenantService;
        this.documentService = documentService;
        this.objectStorageService = objectStorageService;
    }

    // Read
    @GetMapping("/documents")
    public ModelAndView index(
            final HttpServletRequest request
    ) {
        final var tenantId = ((Tenant) httpSession.getAttribute("tenant")).id();
        final var documents = documentService.read(tenantId);
        final var modelAndView = new ModelAndView("documents/index");
        modelAndView.addObject("documents", documents);
        final var inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            modelAndView.addObject("notifications", inputFlashMap.get("notifications"));
        }
        return modelAndView;
    }

    @GetMapping(
            value = "/files/{tenantId}/{rawFileUUID}",
            produces = "application/pdf")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(
            @PathVariable final Long tenantId,
            @PathVariable final String rawFileUUID,
            @RequestParam final Boolean download
    ) throws BusinessLogicException, ObjectStorageException  {
        final var path = documentService.convertToValidArgs(rawFileUUID);
        // TODO validate tenantId (uuid)
        final var tenant = tenantService.readByTenantId(tenantId);
        final var document = documentService.read(tenant.id(), path);
        final Resource inputStreamResource = new InputStreamResource(objectStorageService.readAsStream(tenant.bucket(), document.path()));
        return ResponseEntity.ok()
                .headers(httpHeaders -> {
                    httpHeaders.setContentType(MediaType.APPLICATION_PDF);
                    if (download) {
                        httpHeaders.setContentDisposition(ContentDisposition
                                .attachment()
                                .filename(String.format("%s.pdf", document.filename()))
                                .build());
                    }
                })
                .body(inputStreamResource);
    }

    // Create
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
    ) throws BusinessLogicException, IOException, ObjectStorageException, ServletException {
        final var parts = request.getParts();
        if (parts.size() != 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        String rawFilename = null;
        Long clientSideFileSize = null;
        InputStream rawFileInputStream = null;
        for (var part: parts) {
            if (part.getName().equals("fileSize")) {
                final var bytes = part.getInputStream().readAllBytes();
                if (bytes.length == 0) {
                    throw new BusinessLogicException("document.file.empty");
                }
                clientSideFileSize = Long.parseLong(new String(bytes, StandardCharsets.UTF_8));
            } else if (part.getName().equals("filename")) {
                rawFilename = new String(part.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            } else if (part.getName().equals("file")) {
                if (!part.getContentType().equals(MediaType.APPLICATION_PDF.toString())) {
                    throw new BusinessLogicException("document.file.unsupportedfileformat");
                } else if (part.getSize() != clientSideFileSize) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
                rawFileInputStream = part.getInputStream();
            }
        }
        Objects.requireNonNull(clientSideFileSize);
        Objects.requireNonNull(rawFilename);
        Objects.requireNonNull(rawFileInputStream);
        // TODO validate fileInputStream
        final var tenant = (Tenant) httpSession.getAttribute("tenant");
        final var tuple = documentService.converToValidArgs(tenant.id(), rawFilename);
        objectStorageService.storeFile(tenant.bucket(), tuple.path(), clientSideFileSize, rawFileInputStream);
        documentService.create(tenant.id(), tuple.filename(), tuple.path());
        redirectAttributes.addFlashAttribute(
                "notifications",
                List.of(new Notification(NotificationType.SUCCESS, "notification.success", "documents.create.success")));
        return new RedirectView("/documents");
    }

    // Exception handling
    @ExceptionHandler(value = {
            BusinessLogicException.class,
            ObjectStorageException.class
    })
    public RedirectView handle(
            final HttpServletRequest request,
            final Exception exception,
            final RedirectAttributes redirectAttributes
    ) throws Exception {
        List<Notification> notifications;
        switch (exception) {
            case BusinessLogicException businessLogicException -> notifications = List.of(new Notification(
                    NotificationType.ERROR,
                    "notification.error.validation",
                    businessLogicException.messageKey())
            );
            case ObjectStorageException objectStorageException -> notifications = List.of(new Notification(
                    NotificationType.ERROR,
                    "notification.error",
                    objectStorageException.messageKey())
            );
            default -> throw exception;
        }
        redirectAttributes.addFlashAttribute("notifications", notifications);
        return new RedirectView("/documents/create");
    }
}
