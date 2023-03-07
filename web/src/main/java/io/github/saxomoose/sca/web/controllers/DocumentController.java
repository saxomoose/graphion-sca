package io.github.saxomoose.sca.web.controllers;

import io.github.saxomoose.sca.web.dtos.Notification;
import io.github.saxomoose.sca.web.enums.NotificationType;
import io.github.saxomoose.sca.web.exceptions.UniqueConstraintViolationException;
import io.github.saxomoose.sca.web.services.DocumentService;
import io.github.saxomoose.sca.web.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DocumentController {
    private final DocumentService documentService;
    private final UserService userService;

    @Autowired
    public DocumentController(DocumentService documentService, UserService userService) {
        this.documentService = documentService;
        this.userService = userService;
    }

    @GetMapping("/documents")
    public ModelAndView index(
            final Principal principal,
            final HttpServletRequest request) {
        final var user = userService.getUser(principal.getName());
        final var documents = documentService.getDocuments(user.getTenant());
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
            final Principal principal,
            @RequestParam("file")
            final MultipartFile file,
            @Valid @ModelAttribute final io.github.saxomoose.sca.web.dtos.Document documentDTO,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes
    ) {
        final var redirectView = new RedirectView();
        if (!bindingResult.hasErrors()) {
            final var user = userService.getUser(principal.getName());
            try {
                documentService.create(documentDTO, file,user.getTenant());
            } catch (UniqueConstraintViolationException e) {
                bindingResult.rejectValue("path", "duplicate");
            }
            if (!bindingResult.hasErrors()) {
                redirectView.setUrl("/documents");
                redirectAttributes.addFlashAttribute("notifications", List.of(new Notification(NotificationType.SUCCESS, "notification.success", "documents.create.success")));
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
}
