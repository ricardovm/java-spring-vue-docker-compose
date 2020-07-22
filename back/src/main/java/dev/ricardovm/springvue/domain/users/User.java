package dev.ricardovm.springvue.domain.users;

import dev.ricardovm.springvue.domain.projects.Project;

import javax.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Level level;

    @ManyToOne
    @JoinColumn(name = "supervisor_id")
    private User supervisor;

    @ManyToMany
    @JoinTable(name = "projects_users",
            inverseJoinColumns = @JoinColumn(name = "project_id"),
            joinColumns = @JoinColumn(name = "user_id"))
    private Set<Project> projects = new HashSet<>();

    @OneToMany(mappedBy = "supervisor")
    private Set<User> developers;

    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();

    public User() {

    }

    public User(String name, Level level) {
        this.name = name;
        this.level = level;
    }

    @PrePersist
    @PreUpdate
    public void onSave() {
        updatedAt = Instant.now();
    }

    public void addProject(Project project) {
        Objects.requireNonNull(project, "project cannot be null");
        projects.add(project);
    }

    public void removeProject(Project project) {
        Objects.requireNonNull(project, "project cannot be null");
        projects.removeIf(p -> Objects.equals(p.getId(), project.getId()));
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Optional<User> getSupervisor() {
        return Optional.ofNullable(supervisor);
    }

    public void setSupervisor(User supervisor) {
        this.supervisor = supervisor;
    }

    public Set<Project> getProjects() {
        return Collections.unmodifiableSet(projects);
    }

    public Set<User> getDevelopers() {
        return Collections.unmodifiableSet(developers);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(name, user.name) &&
                level == user.level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, level);
    }
}
