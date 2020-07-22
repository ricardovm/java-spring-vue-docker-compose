package dev.ricardovm.springvue.domain.projects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectsService {

    @Autowired
    private ProjectsRepository repository;

    public List<Project> findAll() {
        return repository.findAll(Sort.by("name"));
    }

    public Optional<Project> findById(Long id) {
        return repository.findById(id);
    }

    public Project save(Project project) {
        return repository.saveAndFlush(project);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
