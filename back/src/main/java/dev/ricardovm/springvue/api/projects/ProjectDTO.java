package dev.ricardovm.springvue.api.projects;

import dev.ricardovm.springvue.api.users.UserDTO;
import dev.ricardovm.springvue.domain.projects.Project;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/* DTO */
public class ProjectDTO {

    /* Factory method */
    public static ProjectDTO ofProject(Project project) {
        return new ProjectDTO(project);
    }

    /* Factory method */
    public static ProjectDTO ofProjectWithUsers(Project project) {
        var dto = new ProjectDTO(project);
        dto.developers = UserDTO.ofUsers(project.getDevelopers());
        dto.leaders = UserDTO.ofUsers(project.getLeaders());

        return dto;
    }

    /* Factory method */
    public static List<ProjectDTO> ofProjects(Collection<Project> projects) {
        return projects.stream()
                .map(ProjectDTO::ofProject)
                .collect(Collectors.toList());
    }

    private Long id;
    private String name;
    private List<UserDTO> developers;
    private List<UserDTO> leaders;

    public ProjectDTO() {

    }

    private ProjectDTO(Project project) {
        Objects.requireNonNull(project, "project cannot be null");

        this.id = project.getId();
        this.name = project.getName();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<UserDTO> getDevelopers() {
        return developers;
    }

    public List<UserDTO> getLeaders() {
        return leaders;
    }
}
