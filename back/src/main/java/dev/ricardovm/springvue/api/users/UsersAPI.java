package dev.ricardovm.springvue.api.users;

import dev.ricardovm.springvue.api.infra.exception.BadRequestException;
import dev.ricardovm.springvue.api.infra.exception.NotFoundException;
import dev.ricardovm.springvue.api.projects.ProjectDTO;
import dev.ricardovm.springvue.domain.users.Level;
import dev.ricardovm.springvue.domain.users.User;
import dev.ricardovm.springvue.domain.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("users")
public class UsersAPI {

    @Autowired
    private UsersService usersService;

    @GetMapping
    public List<UserDTO> findAll(@RequestParam(value = "level", required = false) Level level) {
        var users = level == null
                ? usersService.findAll()
                : usersService.findAll(level);
        return UserDTO.ofUsers(users);
    }

    @GetMapping("{id}")
    public UserDTO get(@NotNull @PathVariable("id") Long id) {
        return usersService.findById(id)
                .map(UserDTO::ofUserWithProjectsAndDevelopers)
                .orElseThrow(NotFoundException::new);
    }

    @PostMapping
    public UserDTO create(@NotNull @RequestBody UserDTO userDTO) {
        if (userDTO.getName() == null
                || userDTO.getLevel() == null) {
            throw new BadRequestException();
        }

        var user = new User(userDTO.getName(), userDTO.getLevel());
        setSupervisor(user, userDTO.getSupervisor());

        var savedUser = usersService.save(user);

        return UserDTO.ofUser(savedUser);
    }

    @PutMapping("{id}")
    public UserDTO update(
            @NotNull @PathVariable("id") Long id,
            @NotNull @RequestBody UserDTO userDTO) {
        return usersService.findById(id)
                .map(user -> applyChanges(user, userDTO))
                .map(usersService::save)
                .map(UserDTO::ofUserWithProjectsAndDevelopers)
                .orElseGet(() -> {
                    throw new NotFoundException();
                });
    }

    @DeleteMapping("{id}")
    public void delete(@NotNull @PathVariable("id") Long id) {
        try {
            usersService.delete(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException();
        }
    }

    @PostMapping("{id}/projects")
    public UserDTO addProject(
            @NotNull @PathVariable("id") Long id,
            @NotNull @RequestBody ProjectDTO projectDTO) {

        try {
            return usersService.findById(id)
                    .map(user -> usersService.addProject(user, projectDTO.getId()))
                    .map(UserDTO::ofUserWithProjectsAndDevelopers)
                    .orElseThrow(NotFoundException::new);
        } catch (InvalidParameterException e) {
            throw new BadRequestException();
        }
    }

    @DeleteMapping("{id}/projects/{projectId}")
    public UserDTO removeProject(
            @NotNull @PathVariable("id") Long id,
            @NotNull @PathVariable("projectId") Long projectId) {

        try {
            return usersService.findById(id)
                    .map(user -> usersService.removeProject(user, projectId))
                    .map(UserDTO::ofUserWithProjectsAndDevelopers)
                    .orElseThrow(NotFoundException::new);
        } catch (InvalidParameterException e) {
            throw new BadRequestException();
        }
    }

    private User applyChanges(User user, UserDTO userDTO) {
        Objects.requireNonNull(user, "user cannot be null");
        Objects.requireNonNull(userDTO, "userDTO cannot be null");

        user.setName(userDTO.getName());
        user.setLevel(userDTO.getLevel());
        setSupervisor(user, userDTO.getSupervisor());

        return user;
    }

    private void setSupervisor(User user, UserDTO supervisor) {
        if (supervisor == null || supervisor.getId() == null) {
            user.setSupervisor(null);
        } else {
            usersService.findById(supervisor.getId())
                    .ifPresentOrElse(
                            user::setSupervisor,
                            () -> { throw new BadRequestException(); });
        }
    }
}
