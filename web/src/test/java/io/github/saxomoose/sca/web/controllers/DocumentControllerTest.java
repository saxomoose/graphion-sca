package io.github.saxomoose.sca.web.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.models.User;
import io.github.saxomoose.sca.web.repositories.db.DocumentRepository;
import io.github.saxomoose.sca.web.repositories.db.TenantRepository;
import io.github.saxomoose.sca.web.repositories.db.UserRepository;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
// @Tag("target")
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
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        tenant = tenantRepository.select();
        user = userRepository.select();
    }

    @Test
    public void create() throws Exception {
        final var filePart = new MockPart("file", file());
        filePart.getHeaders().setContentType(MediaType.APPLICATION_PDF);
        final var test =
                this.mockMvc
                        .perform(multipart("/documents")
                                .part(new MockPart("_csrf", "".getBytes()),
                                        new MockPart("fileSize", "31599".getBytes()),
                                        new MockPart("filename",
                                                "20230308-test-document".getBytes()),
                                        filePart)
                                .with(csrf()).with(user(user)).sessionAttr("tenant", tenant))
                        .andExpect(status().is3xxRedirection());
    }

    private byte[] file() throws IOException {
        return new ClassPathResource("minio/20230308-test-document.pdf").getInputStream()
                .readAllBytes();
    }

    @Test
    public void serveFile() throws Exception {
        final var rawFileUUID = UUID.nameUUIDFromBytes("20230308-seed-document".getBytes());
        this.mockMvc
                .perform(get("/files/{tenantId}/{rawFileUUID}", 1, rawFileUUID)
                        .queryParam("download", "true").with(user(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition",
                        "attachment; filename=\"20230308-seed-document.pdf\""));

        // .andExpect(content().string(containsString("Hello, World")));
    }
}
