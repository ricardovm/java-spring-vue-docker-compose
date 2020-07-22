package dev.ricardovm.springvue.domain.users;

import dev.ricardovm.springvue.domain.projects.Project;
import dev.ricardovm.springvue.domain.projects.ProjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    @Autowired
    private UsersRepository repository;

    @Autowired
    private ProjectsService projectsService;

    public List<User> findAll() {
        return repository.findAll(Sort.by("name"));
    }

    public List<User> findAll(Level level) {
        return repository.findByLevel(level, Sort.by("name"));
    }

    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    public User save(User user) {
        return repository.saveAndFlush(user);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public User addProject(User user, Long projectId) {
        var project = findProject(projectId);

        user.addProject(project);

        return save(user);
    }

    public User removeProject(User user, Long projectId) {
        var project = findProject(projectId);

        user.removeProject(project);

        return save(user);
    }

    private Project findProject(Long projectId) {
        return projectsService.findById(projectId)
                .orElseThrow(InvalidParameterException::new);
    }
}
