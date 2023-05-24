package io.github.saxomoose.sca.web.controllers;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;
import io.github.saxomoose.sca.web.exceptions.BusinessLogicException;
import io.github.saxomoose.sca.web.models.Document;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.models.User;
import io.github.saxomoose.sca.web.services.DocumentService;
import io.github.saxomoose.sca.web.services.TenantService;
import io.github.saxomoose.sca.web.services.ValidationService;
import io.github.saxomoose.sca.web.utils.Notification;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@Controller
public class DocumentController {
    private final DocumentService documentService;
    private final HttpSession httpSession;
    private final TenantService tenantService;
    private final ValidationService validationService;

    @Autowired
    public DocumentController(
        DocumentService documentService,
        HttpSession httpSession,
        TenantService tenantService,
        ValidationService validationService) 
    {
        this.documentService = documentService;
        this.httpSession = httpSession;
        this.tenantService = tenantService;
        this.validationService = validationService;
    }

    // Read
    @GetMapping("/documents")
    public ModelAndView index(
        final HttpServletRequest request,
        Pageable pageable) 
    {
        // final var tenantId = ((Tenant) httpSession.getAttribute("tenant")).id();
        final var tenantId = 1L;
        final Page<Document> documentPage = documentService.documentsByTenant(tenantId, pageable);
        if (documentPage.isEmpty()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
        }
        final var modelAndView = new ModelAndView("documents/index");
        modelAndView.addObject("documentPage", documentPage);
        final var inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            modelAndView.addObject("notifications", inputFlashMap.get("notifications"));
        }
        return modelAndView;
    }

    @GetMapping(value = "/files/{rawFileUUID}", produces = "application/pdf")
    // @ResponseBody
    public ResponseEntity<Resource> serveFile(
        @PathVariable final String rawFileUUID,
        @RequestParam final Boolean download) throws Exception 
    {
        final var tenant = ((Tenant) httpSession.getAttribute("tenant"));
        final UUID fileUUID = validationService.uuid(rawFileUUID);
        // TODO validate tenantId (uuid)
        final String filename = documentService.documentByTenantAndFileUUID(tenant.id(), fileUUID).filename();
        final Resource inputStreamResource = new InputStreamResource(documentService.fileAsStream(tenant.bucket(), fileUUID));
        return ResponseEntity.ok().headers(httpHeaders -> {
            httpHeaders.setContentType(MediaType.APPLICATION_PDF);
            if (download) {
                httpHeaders
                    .setContentDisposition(ContentDisposition.attachment()
                    .filename(String.format("%s.pdf", filename)).build());
            }
        }).body(inputStreamResource);
    }

    // Create
    @GetMapping("/documents/create")
    public ModelAndView create(final HttpServletRequest request) 
    {
        final var modelAndView = new ModelAndView("documents/create");
        final var inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            modelAndView.addObject(
                "notifications", inputFlashMap.get("notifications"));
        }
        return modelAndView;
    }

    @PostMapping(value = "/documents", consumes = "multipart/form-data")
    public RedirectView store(
        final HttpServletRequest request,
        final RedirectAttributes redirectAttributes) throws Exception 
    {
        final Collection<Part> parts = request.getParts();
        if (parts.size() != 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        String rawFilename = null;
        Long clientSideFileSize = null;
        InputStream rawFileInputStream = null;
        for (Part part : parts) {
            if (part.getName().equals("fileSize")) {
                final var bytes = part.getInputStream().readAllBytes();
                if (bytes.length == 0) {
                    throw new BusinessLogicException("document.file.empty");
                }
                clientSideFileSize = Long.parseLong(new String(bytes, StandardCharsets.UTF_8));
            } else if (part.getName().equals("filename")) {
                rawFilename =
                        new String(part.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            } else if (part.getName().equals("file")) {
                if (!part.getContentType().equals(MediaType.APPLICATION_PDF.toString())) {
                    throw new BusinessLogicException("document.file.unsupported_file_format");
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
        final String filename = validationService.documentArgs(rawFilename);
        documentService.create(tenant, ((User) request.getUserPrincipal()).id(), filename, clientSideFileSize, rawFileInputStream);
        redirectAttributes.addFlashAttribute(
            "notifications",
            List.of(new Notification(
                Notification.Type.SUCCESS, 
                "notification.success",
                "documents.create.success")));
        return new RedirectView("/documents");
    }

    // Exception handling
    @ExceptionHandler(value = {BusinessLogicException.class,})
    public RedirectView handle(
        final BusinessLogicException businessLogicException,
        final HttpServletRequest request,
        final RedirectAttributes redirectAttributes) 
    {
        final String path = URI.create(request.getHeader("referer")).getPath();
        redirectAttributes.addFlashAttribute(
            "notifications", 
            List.of(
                new Notification(
                Notification.Type.ERROR,
                "notification.error.validation",
                businessLogicException.messageKey())));
        return new RedirectView(path);
    }
}
