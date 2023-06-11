package io.github.saxomoose.sca.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;

@Configuration
public class MailConfiguration {

    @Bean
    public MailgunMessagesApi mailgunClient(final Environment environment)
    {
        return MailgunClient
            .config(environment.getProperty("mailgun.api-key"))
            .createApi(MailgunMessagesApi.class);
    }
}
