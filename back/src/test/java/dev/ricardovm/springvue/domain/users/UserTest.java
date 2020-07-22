package dev.ricardovm.springvue.domain.users;

import dev.ricardovm.springvue.domain.projects.Project;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {

    @Test
    public void addProject() {
        var user = new User("Ricardo", Level.SUPERVISOR);
        var project = new Project("Test Project");

        assertTrue(user.getProjects().isEmpty());

        user.addProject(project);

        assertEquals(1, user.getProjects().size());
        assertEquals(project, user.getProjects().stream().findFirst().get());
    }

    @Test
    public void removeProject() {
        var user = new User("Ricardo", Level.SUPERVISOR);
        var project = new Project("Test Project");

        user.addProject(project);
        assertEquals(1, user.getProjects().size());

        user.removeProject(project);
        assertTrue(user.getProjects().isEmpty());
    }
}
