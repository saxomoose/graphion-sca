package io.github.saxomoose.sca.web.controllers;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
import io.github.saxomoose.sca.web.enums.SessionEnum;
import io.github.saxomoose.sca.web.exceptions.LogicException;
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
        final Pageable pageable) 
    {
        // final Long tenantId = ((Tenant) httpSession.getAttribute(Session.TENANT.toString()).id();
        final var tenantId = 1L;
        final Page<Document> documentPage = documentService.documentsByTenant(tenantId, pageable);
        if (documentPage.isEmpty()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
        }
        final ModelAndView modelAndView = new ModelAndView("documents/index");
        modelAndView.addObject("documentPage", documentPage);
        final Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            modelAndView.addObject("notifications", inputFlashMap.get("notifications"));
        }
        return modelAndView;
    }

    @GetMapping(
        value = "/files/{rawFileUUID}",
        produces = MediaType.APPLICATION_PDF_VALUE)
    // @ResponseBody
    public ResponseEntity<Resource> serveFile(
        @PathVariable final String rawFileUUID,
        @RequestParam final Boolean download) throws Exception 
    {
        final Tenant tenant = (Tenant) httpSession.getAttribute(SessionEnum.TENANT.toString());
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
        final ModelAndView modelAndView = new ModelAndView("documents/create");
        final Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            modelAndView.addObject("notifications", inputFlashMap.get("notifications"));
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
        for (final Part part : parts) {
            if (part.getName().equals("fileSize")) {
                final byte[] bytes = part.getInputStream().readAllBytes();
                clientSideFileSize = Long.parseLong(new String(bytes, StandardCharsets.UTF_8));
                continue;
            } else if (part.getName().equals("filename")) {
                rawFilename =
                        new String(part.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                continue;
            } else if (part.getName().equals("file")) {
                validationService.documentFileFormat(part.getContentType());
                validationService.documentFileSize(part.getSize(), clientSideFileSize);
                rawFileInputStream = part.getInputStream();
            }
        }
        Objects.requireNonNull(clientSideFileSize);
        Objects.requireNonNull(rawFilename);
        Objects.requireNonNull(rawFileInputStream);
        // TODO validate fileInputStream
        final var tenant = (Tenant) httpSession.getAttribute(SessionEnum.TENANT.toString());
        final String filename = validationService.documentFilename(rawFilename);
        documentService.create(tenant, ((User) request.getUserPrincipal()).id(), filename, clientSideFileSize, rawFileInputStream);
        redirectAttributes.addFlashAttribute(
            "notifications",
            List.of(new Notification(Notification.Type.SUCCESS, "document.create", "document.create")));
        return new RedirectView("/documents");
    }

    // Exception handling
    @ExceptionHandler(value = {LogicException.class,})
    public RedirectView handle(
        final LogicException businessLogicException,
        final HttpServletRequest request,
        final RedirectAttributes redirectAttributes) 
    {
        final String path = URI.create(request.getHeader("referer")).getPath();
        redirectAttributes.addFlashAttribute(
            "notifications", 
            List.of(
                new Notification(
                    businessLogicException.notification().type(),
                    businessLogicException.notification().header(),
                    businessLogicException.notification().body())));
        return new RedirectView(path);
    }
}
