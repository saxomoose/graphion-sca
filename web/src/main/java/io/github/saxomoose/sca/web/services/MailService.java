package io.github.saxomoose.sca.web.services;

import org.springframework.stereotype.Service;
import com.mailgun.api.v3.MailgunMessagesApi;

@Service
public class MailService {
    private final MailgunMessagesApi mailgunMessagesApi;

    public MailService(MailgunMessagesApi mailgunMessagesApi)
    {
        this.mailgunMessagesApi = mailgunMessagesApi;
    }

    
}
