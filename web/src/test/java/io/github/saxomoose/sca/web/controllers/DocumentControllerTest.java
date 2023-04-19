package io.github.saxomoose.sca.web.controllers;

import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.models.User;
import io.github.saxomoose.sca.web.repositories.db.DocumentRepository;
import io.github.saxomoose.sca.web.repositories.db.TenantRepository;
import io.github.saxomoose.sca.web.repositories.db.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("target")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DocumentControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DocumentRepository documentRepository;
    private User user;
    private Tenant tenant;


    @BeforeAll
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        tenant = tenantRepository.select();
        user = userRepository.select();
    }

    @Test
    @Order(1)
    public void create() throws Exception {
        final var filePart = new MockPart("file", file());
        filePart.getHeaders().setContentType(MediaType.APPLICATION_PDF);
        this.mockMvc
                .perform(
                multipart("/documents").part(
                        new MockPart("_csrf", "".getBytes()),
                        new MockPart("fileSize", "31599".getBytes()),
                        new MockPart("filename", "20230308-test-document".getBytes()),
                        filePart
                        ).with(csrf())
                        .with(user(user))
                        .sessionAttr("tenant", tenant))
                .andExpect(status().is3xxRedirection());
    }

    private byte[] file() throws IOException {
        return new ClassPathResource("minio/20230308-test-document.pdf").getInputStream().readAllBytes();
    }


    @Test
    @Order(2)
    public void serveFile() throws Exception {
        final var rawFileUUID = UUID.nameUUIDFromBytes("20230308-test-document".getBytes());
//        final var test = UriComponentsBuilder.fromUriString("/files/{tenantId}/{rawFileUUID}").buildAndExpand(1, rawFileUUID).encode().toUri();
        this.mockMvc
                .perform(
                        get("/files/{tenantId}/{rawFileUUID}", 1, rawFileUUID)
                        .queryParam("download", "true")
                        .with(user(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
//                .andExpect(content().string(containsString("Hello, World")));
    }
}
