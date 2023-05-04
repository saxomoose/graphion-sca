package io.github.saxomoose.sca.web.config;

import org.apache.catalina.Context;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfiguration {
    @Bean
    public TomcatServletWebServerFactory tomcatFactory() {
        return new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                context.setAllowCasualMultipartParsing(true);
                final var cookieProcessor = new Rfc6265CookieProcessor();
                cookieProcessor.setSameSiteCookies("strict");
                context.setCookieProcessor(cookieProcessor);
            }
        };
    }
}
