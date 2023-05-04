package io.github.saxomoose.sca.web.config;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class WebApplicationConfiguration implements WebMvcConfigurer {
    @Bean
    public MessageSource messageSource() {
        final var messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public LocaleResolver localeResolver() {
        final var sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(Locale.US);
        // sessionLocaleResolver.setLocaleAttributeName("session.current.locale");
        return sessionLocaleResolver;
    }

    @Bean
	public CookieSerializer cookieSerializer() {
		DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setSameSite("Strict");
		return serializer;
	}

    // @Bean
    // public LocaleChangeInterceptor localeChangeInterceptor() {
    // LocaleChangeInterceptor localeChangeInterceptor = new
    // LocaleChangeInterceptor();
    // localeChangeInterceptor.setParamName("language");
    // return localeChangeInterceptor;
    // }
    //
    // @Override
    // public void addInterceptors(InterceptorRegistry registry) {
    // registry.addInterceptor(localeChangeInterceptor());
    // }
}
