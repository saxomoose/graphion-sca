package io.github.saxomoose.sca.web.services;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.model.message.Message;
import io.github.saxomoose.sca.web.models.Closing;
import io.github.saxomoose.sca.web.models.User;
import io.github.saxomoose.sca.web.utils.Mail;

@Service
public class MailService {
    private final MailgunMessagesApi mailgunMessagesApi;
    private final String domain;
    private final String from;
    private final TemplateEngine templateEngine;

    @Autowired
    public MailService(
        Environment environment, 
        TemplateEngine templateEngine, 
        MailgunMessagesApi mailgunMessagesApi)
    {
        this.mailgunMessagesApi = mailgunMessagesApi;
        this.domain = environment.getProperty("mailgun.domain");
        this.from = environment.getRequiredProperty("mailgun.from");
        this.templateEngine = templateEngine;
    }

    public void sendClosingInvites(final Closing closing, final User closingOrganizer, final List<User> recipients)
    {
        final String body = mailBody(
                Map.of(
                    "closingOrganizer", closingOrganizer,
                    "closing", closing
                )
        );
        final Mail mail = new Mail(
            String.format("%s - %s", "Closing invitation", closing.name()),
            body);
        for (final User user : recipients) {
            sendMails(Map.of(user, mail));
        }
    }

    public void sendMails(Map<User, Mail> userMails) {
        List<Message> messages = userMails.entrySet().stream()
            .map(userMail -> Message.builder()
                .from(from)
                .to(userMail.getKey().email())
                .subject(userMail.getValue().subject())
                .html(userMail.getValue().body())
                .build())
            .toList();

        messages.forEach(message -> mailgunMessagesApi.sendMessage(domain, message));
    }

    public String mailBody(final Map<String, Object> arguments)
    {
        // Prepare the evaluation Context
        final Context thymeleafContext = new Context();
        thymeleafContext.setVariables(arguments);
        return templateEngine.process("mail/closing-invite", thymeleafContext);

    }
}
