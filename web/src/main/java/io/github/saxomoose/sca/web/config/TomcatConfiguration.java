package io.github.saxomoose.sca.web.config;

import org.apache.catalina.Context;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfiguration {
    @Bean
    public TomcatServletWebServerFactory tomcatFactory() 
    {
        final TomcatServletWebServerFactory tomcat = 
            new TomcatServletWebServerFactory() {
                @Override
                protected void postProcessContext(Context context) {
                    context.setAllowCasualMultipartParsing(true);
                }
            };
        // tomcat.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
        //     if ((connector.getProtocolHandler() instanceof AbstractHttp11Protocol<?>)) {
        //         ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(-1);
        //     }
        // });
        return tomcat;
    }
}

