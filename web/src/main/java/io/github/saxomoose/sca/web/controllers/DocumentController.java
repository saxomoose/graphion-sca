package io.github.saxomoose.sca.web.controllers;

import io.github.saxomoose.sca.web.dtos.Notification;
import io.github.saxomoose.sca.web.enums.NotificationType;
import io.github.saxomoose.sca.web.exceptions.BusinessLogicException;
import io.github.saxomoose.sca.web.exceptions.ObjectStorageException;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.services.DocumentService;
import io.github.saxomoose.sca.web.services.ObjectStorageService;
import io.github.saxomoose.sca.web.services.TenantService;
import io.github.saxomoose.sca.web.services.ValidationService;
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
    private final ValidationService validationService;
    private final TenantService tenantService;
    private final DocumentService documentService;
    private final ObjectStorageService objectStorageService;

    @Autowired
    public DocumentController(HttpSession httpSession, ValidationService validationService, TenantService tenantService, DocumentService documentService, ObjectStorageService objectStorageService) {
        this.httpSession = httpSession;
        this.validationService = validationService;
        this.tenantService = tenantService;
        this.documentService = documentService;
        this.objectStorageService = objectStorageService;
    }

    @GetMapping("/documents")
    public ModelAndView index(
            final HttpServletRequest request
    ) {
        final var tenantId = ((Tenant) httpSession.getAttribute("tenant")).getId();
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
            ) throws BusinessLogicException, IOException, ObjectStorageException, ServletException {
        final var parts = request.getParts();
        if (parts.size() != 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        String filename = null;
        Long clientSideFileSize = null;
        InputStream fileInputStream = null;
        for (var part: parts) {
            if (part.getName().equals("fileSize")) {
                final var bytes = part.getInputStream().readAllBytes();
                if (bytes.length == 0) {
                    throw new BusinessLogicException("document.file.empty");
                }
                clientSideFileSize = Long.parseLong(new String(bytes, StandardCharsets.UTF_8));
            } else if (part.getName().equals("filename")) {
                filename = new String(part.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            } else if (part.getName().equals("file")) {
                if (!part.getContentType().equals(MediaType.APPLICATION_PDF.toString())) {
                    throw new BusinessLogicException("document.file.unsupportedfileformat");
                } else if (part.getSize() != clientSideFileSize) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
                fileInputStream = part.getInputStream();
            }
        }
        Objects.requireNonNull(clientSideFileSize);
        Objects.requireNonNull(filename);
        Objects.requireNonNull(fileInputStream);
        final var tenant = (Tenant) httpSession.getAttribute("tenant");
        final var fileUUID = validationService.validateFilename(tenant.getId(), filename);
        final var path = documentService.generatePath(fileUUID);
        objectStorageService.storeFile(tenant.getBucket(), path, clientSideFileSize, fileInputStream);
        documentService.insert(tenant.getId(), filename, path);
        redirectAttributes.addFlashAttribute(
                "notifications",
                List.of(new Notification(NotificationType.SUCCESS, "notification.success", "documents.create.success")));
        return new RedirectView("/documents");
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
        final var fileUUID = validationService.validateUUID(rawFileUUID);
        // validate tenantId (uuid)
        final var tenant = tenantService.getTenant(tenantId);
        final var document = documentService.getDocument(tenant.getId(), fileUUID);
        final var inputStreamResource = new InputStreamResource(objectStorageService.getObjectAsStream(tenant.getBucket(), document.getPath()));
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
            default -> throw exception;
        }
        redirectAttributes.addFlashAttribute("notifications", notifications);
        return new RedirectView("/documents/create");
    }
}
