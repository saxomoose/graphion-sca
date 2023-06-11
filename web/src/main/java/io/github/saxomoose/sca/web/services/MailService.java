package io.github.saxomoose.sca.web.services;

import java.util.Arrays;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.model.message.Message;
import com.mailgun.model.message.MessageResponse;

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

    public void sendMail()
    {
        final Message message = Message.builder()
                .from(from)
                .to("mathieu.tulpinck@gmail.com")
                .subject("test-subject")
                .html(mailBody())
                .build();

        final MessageResponse messageResponse = mailgunMessagesApi.sendMessage(domain, message);
    }

    public String mailBody()
    {
        // Prepare the evaluation Context
        final Context thymeleafContext = new Context();
        thymeleafContext.setVariable("name", "Test name");
        thymeleafContext.setVariable("subscriptionDate", new Date());
        thymeleafContext.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
        return templateEngine.process("mail/email-simple", thymeleafContext);

    }
}
