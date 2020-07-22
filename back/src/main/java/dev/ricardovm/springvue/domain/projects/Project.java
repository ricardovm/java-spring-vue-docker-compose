package dev.ricardovm.springvue.domain.projects;

import dev.ricardovm.springvue.domain.users.Level;
import dev.ricardovm.springvue.domain.users.User;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "projects")
    private Set<User> users = new HashSet<>();

    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();

    public Project() {

    }

    public Project(String name) {
        this.name = name;
    }

    @PrePersist
    @PreUpdate
    public void onSave() {
        updatedAt = Instant.now();
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

    public Set<User> getDevelopers() {
        return users.stream()
                .filter(user -> user.getLevel() == Level.DEVELOPER)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<User> getLeaders() {
        var supervisors = users.stream()
                .filter(user -> user.getLevel() == Level.SUPERVISOR);

        var supervisorsFromUsers = users.stream()
                .filter(user -> user.getLevel() == Level.DEVELOPER && user.getSupervisor().isPresent())
                .map(user -> user.getSupervisor().get());

        return Stream.concat(supervisors, supervisorsFromUsers)
                .collect(Collectors.toUnmodifiableSet());
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
        Project project = (Project) o;
        return Objects.equals(id, project.id) &&
                Objects.equals(name, project.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
