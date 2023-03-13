package io.github.saxomoose.sca.web.controllers;

import io.github.saxomoose.sca.web.services.DocumentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WithMockUser(roles="ADMIN")
public class DocumentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;

    @Test
    public void serveFile() throws Exception {
        this.mockMvc.perform(get("/files/e3d704f3-542b-34a6-a1eb-ed70dc0efe13"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello, World")));
    }
}
