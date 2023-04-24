package io.github.saxomoose.sca.web.config;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

@Configuration
public class DevConfiguration {
    public DevConfiguration(final TemplateEngine templateEngine) throws IOException {
        var sourceRoot = new ClassPathResource("application.yml").getFile().getParentFile();
        while (sourceRoot.listFiles((dir, name) -> name.equals("build.gradle.kts")).length != 1) {
            sourceRoot = sourceRoot.getParentFile();
        }
        final var fileTemplateResolver = new FileTemplateResolver();
        fileTemplateResolver.setPrefix(sourceRoot.getPath() + "/src/main/resources/templates/");
        fileTemplateResolver.setSuffix(".html");
        fileTemplateResolver.setCacheable(false);
        fileTemplateResolver.setCharacterEncoding("UTF-8");
        fileTemplateResolver.setCheckExistence(true);

        templateEngine.setTemplateResolver(fileTemplateResolver);
    }
}
