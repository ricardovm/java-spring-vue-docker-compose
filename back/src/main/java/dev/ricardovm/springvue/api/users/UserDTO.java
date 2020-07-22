package dev.ricardovm.springvue.api.users;

import dev.ricardovm.springvue.api.projects.ProjectDTO;
import dev.ricardovm.springvue.domain.users.Level;
import dev.ricardovm.springvue.domain.users.User;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/* DTO */
public class UserDTO {

    /* Factory method */
    public static UserDTO ofUser(User user) {
        return new UserDTO(user);
    }

    /* Factory method */
    public static UserDTO ofUserWithProjectsAndDevelopers(User user) {
        var dto = new UserDTO(user);
        dto.projects = ProjectDTO.ofProjects(user.getProjects());
        dto.developers = UserDTO.ofUsers(user.getDevelopers());

        return dto;
    }

    /* Factory method */
    public static List<UserDTO> ofUsers(Collection<User> users) {
        return users.stream()
                .map(UserDTO::ofUser)
                .collect(Collectors.toList());
    }

    private Long id;
    private String name;
    private Level level;
    private UserDTO supervisor;
    private List<UserDTO> developers;

    private List<ProjectDTO> projects;

    public UserDTO() {

    }

    private UserDTO(User user) {
        Objects.requireNonNull(user, "user cannot be null");

        this.id = user.getId();
        this.name = user.getName();
        this.level = user.getLevel();

        user.getSupervisor()
                .map(UserDTO::new)
                .ifPresent(supervisor -> this.supervisor = supervisor);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public Level getLevel() {
        return level;
    }

    void setLevel(Level level) {
        this.level = level;
    }

    public UserDTO getSupervisor() {
        return supervisor;
    }

    public List<ProjectDTO> getProjects() {
        return projects;
    }

    public List<UserDTO> getDevelopers() {
        return developers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(id, userDTO.id) &&
                Objects.equals(name, userDTO.name) &&
                level == userDTO.level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, level);
    }
}
