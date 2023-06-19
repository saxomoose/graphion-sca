package io.github.saxomoose.sca.web.config;

import java.util.Locale;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class WebApplicationConfiguration implements WebMvcConfigurer {
    @Bean
    public LocaleResolver localeResolver() 
    {
        final SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(Locale.US);
        return sessionLocaleResolver;
    }

    @Bean
	public CookieSerializer cookieSerializer() 
    {
		DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setSameSite("Strict");
		return serializer;
	}
}
