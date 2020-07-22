package dev.ricardovm.springvue.api.users;

import dev.ricardovm.springvue.domain.projects.Project;
import dev.ricardovm.springvue.domain.projects.ProjectsRepository;
import dev.ricardovm.springvue.domain.users.Level;
import dev.ricardovm.springvue.domain.users.User;
import dev.ricardovm.springvue.domain.users.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.Objects;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsersAPITest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProjectsRepository projectsRepository;

    @Autowired
    private UsersRepository usersRepository;

    private MockMvc mockMvc;

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setupMockMvc() {
        mockMvc = webAppContextSetup(context).build();
    }

    @Test
    public void findAll() throws Exception {
        var project = projectsRepository.save(new Project("User Test Project"));
        var supervisor = usersRepository.save(new User("Ricardo", Level.SUPERVISOR));
        var developer = new User("Fulano", Level.DEVELOPER);
        developer.setSupervisor(supervisor);
        developer.addProject(project);
        usersRepository.save(developer);

        mockMvc.perform(get("http://localhost:" + port + "/users/" + developer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Fulano")))
                .andExpect(jsonPath("$.supervisor.name", is("Ricardo")))
                .andExpect(jsonPath("$.projects").isArray())
                .andExpect(jsonPath("$.projects", hasSize(1)))
                .andExpect(jsonPath("$.projects[0].name", is("User Test Project")));
    }

    @Test
    public void create() throws Exception {
        var userName = "Ricardo Supervisor " + UUID.randomUUID().toString();

        mockMvc.perform(post("http://localhost:" + port + "/users").contentType(APPLICATION_JSON)
                .content("{\"name\":\"" + userName + "\",\"level\":\"SUPERVISOR\"}"))
                .andExpect(status().isOk());

        var createdUser = usersRepository.findAll()
                .stream()
                .filter(u -> Objects.equals(u.getName(), userName))
                .findFirst();

        assertTrue(createdUser.isPresent());
    }
}
