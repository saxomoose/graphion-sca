package io.github.saxomoose.sca.web.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("target")
public class SignatureControllerTest {
    private MockMvc mockMVC;
    @Autowired
    private WebApplicationContext context;
    
    @BeforeAll
    public void setUp() 
    {
        mockMVC = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    public void dataToSign() throws Exception {
        final var rawFileUUID = UUID.nameUUIDFromBytes("20230308-seed-document".getBytes());
        this.mockMVC
            .perform(post("/pki/sign/dtbsr/{rawFileUUID}", rawFileUUID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ClassPathResource("json/pki-sign-dtbsr.json").getInputStream().readAllBytes())
                .with(csrf()));
    }
    
}
