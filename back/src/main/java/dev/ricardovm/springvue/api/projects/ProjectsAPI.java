package dev.ricardovm.springvue.api.projects;

import dev.ricardovm.springvue.api.infra.exception.BadRequestException;
import dev.ricardovm.springvue.api.infra.exception.NotFoundException;
import dev.ricardovm.springvue.domain.projects.Project;
import dev.ricardovm.springvue.domain.projects.ProjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/projects")
public class ProjectsAPI {

    @Autowired
    private ProjectsService projectsService;

    @GetMapping
    public List<ProjectDTO> findAll() {
        return ProjectDTO.ofProjects(projectsService.findAll());
    }

    @GetMapping("/{id}")
    public ProjectDTO get(@NotNull @PathVariable("id") Long id) {
        return projectsService.findById(id)
                .map(ProjectDTO::ofProjectWithUsers)
                .orElseThrow(NotFoundException::new);
    }

    @PostMapping
    public ProjectDTO create(@NotNull @RequestBody ProjectDTO projectDTO) {
        if (projectDTO.getName() == null) {
            throw new BadRequestException();
        }

        var project = new Project(projectDTO.getName());
        var savedProject = projectsService.save(project);

        return ProjectDTO.ofProject(savedProject);
    }

    @PutMapping("/{id}")
    public ProjectDTO update(
            @NotNull @PathVariable("id") Long id,
            @NotNull @RequestBody ProjectDTO projectDTO) {
        return projectsService.findById(id)
                .map(project -> applyChanges(project, projectDTO))
                .map(projectsService::save)
                .map(ProjectDTO::ofProjectWithUsers)
                .orElseThrow(NotFoundException::new);
    }

    @DeleteMapping("/{id}")
    public void delete(@NotNull @PathVariable("id") Long id) {
        try {
            projectsService.delete(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException();
        }
    }

    private Project applyChanges(Project project, ProjectDTO projectDTO) {
        Objects.requireNonNull(project, "project cannot be null");
        Objects.requireNonNull(projectDTO, "projectDTO cannot be null");

        project.setName(projectDTO.getName());

        return project;
    }
}
