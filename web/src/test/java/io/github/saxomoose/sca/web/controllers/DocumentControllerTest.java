package io.github.saxomoose.sca.web.controllers;

import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.services.DocumentService;
import io.github.saxomoose.sca.web.services.TenantService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DocumentController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WithMockUser(roles="ADMIN")
public class DocumentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DocumentService documentService;
    @MockBean
    private TenantService tenantService;
    @Autowired
    private HttpSession httpSession;

    @BeforeAll
    public void setUp() {
        final var tenant = Tenant.of("sca");
        httpSession.setAttribute("tenant", tenant);
    }


//    @Test
//    public void createFile() {
//        final var part = new MockMultipartHttpServletRequest()
//        this.mockMvc.perform(multipart(HttpMethod.POST, "/documents").part())
//    }

//    @Test
//    public void serveFile() throws Exception {
//        this.mockMvc.perform(get("/files/e3d704f3-542b-34a6-a1eb-ed70dc0efe13"))
//                .andExpect(status().isOk())
//                .andExpect(content().string(containsString("Hello, World")));
//    }
}
