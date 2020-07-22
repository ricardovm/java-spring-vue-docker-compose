package dev.ricardovm.springvue.api.projects;

import dev.ricardovm.springvue.domain.projects.Project;
import dev.ricardovm.springvue.domain.projects.ProjectsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProjectsAPITest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProjectsRepository projectsRepository;

    private MockMvc mockMvc;

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setupMockMvc() {
        mockMvc = webAppContextSetup(context).build();
    }

    @Test
    void findAll() throws Exception {
        projectsRepository.save(new Project("A Test Project"));

        mockMvc.perform(get("http://localhost:" + port + "/projects"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("A Test Project")))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name", is("A Test Project")));
    }
}
