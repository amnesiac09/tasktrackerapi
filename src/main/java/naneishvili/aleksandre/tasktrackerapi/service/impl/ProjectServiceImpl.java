package naneishvili.aleksandre.tasktrackerapi.service.impl;

import naneishvili.aleksandre.tasktrackerapi.dto.request.ProjectCreateRequest;
import naneishvili.aleksandre.tasktrackerapi.dto.request.ProjectUpdateRequest;
import naneishvili.aleksandre.tasktrackerapi.dto.response.ProjectResponse;
import naneishvili.aleksandre.tasktrackerapi.entity.Project;
import naneishvili.aleksandre.tasktrackerapi.entity.User;
import naneishvili.aleksandre.tasktrackerapi.enums.Role;
import naneishvili.aleksandre.tasktrackerapi.exception.ResourceNotFoundException;
import naneishvili.aleksandre.tasktrackerapi.exception.UnauthorizedException;
import naneishvili.aleksandre.tasktrackerapi.mapper.ProjectMapper;
import naneishvili.aleksandre.tasktrackerapi.repository.ProjectRepository;
import naneishvili.aleksandre.tasktrackerapi.service.ProjectService;
import naneishvili.aleksandre.tasktrackerapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private UserService userService;

    @Override
    public ProjectResponse createProject(ProjectCreateRequest request, String ownerEmail) {
        User owner = userService.findByEmail(ownerEmail);

        if (owner.getRole() != Role.MANAGER && owner.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Only MANAGER and ADMIN can create projects");
        }

        Project project = projectMapper.toEntity(request);
        project.setOwner(owner);

        Project savedProject = projectRepository.save(project);
        return projectMapper.toResponse(savedProject);
    }

    @Override
    public ProjectResponse updateProject(Long projectId, ProjectUpdateRequest request, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Project project = findEntityById(projectId);

        if (user.getRole() == Role.ADMIN ||
                (user.getRole() == Role.MANAGER && project.getOwner().getId().equals(user.getId()))) {

            projectMapper.updateEntity(request, project);
            Project updatedProject = projectRepository.save(project);

            return projectMapper.toResponse(updatedProject);
        } else {
            throw new UnauthorizedException("You don't have permission to update this project");
        }
    }

    @Override
    public void deleteProject(Long projectId, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Project project = findEntityById(projectId);

        if (user.getRole() == Role.ADMIN ||
                (user.getRole() == Role.MANAGER && project.getOwner().getId().equals(user.getId()))) {

            projectRepository.delete(project);
        } else {
            throw new UnauthorizedException("You don't have permission to delete this project");
        }
    }

    @Override
    public ProjectResponse getProjectById(Long projectId, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Project project = findEntityById(projectId);

        if (user.getRole() == Role.ADMIN ||
                (user.getRole() == Role.MANAGER && project.getOwner().getId().equals(user.getId()))) {

            return projectMapper.toResponse(project);
        } else {
            throw new UnauthorizedException("You don't have permission to view this project");
        }
    }

    @Override
    public List<ProjectResponse> getProjectsByOwner(String ownerEmail) {
        User owner = userService.findByEmail(ownerEmail);
        List<Project> projects = projectRepository.findByOwner(owner);

        return projects.stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectResponse> getAllProjects(String userEmail) {
        User user = userService.findByEmail(userEmail);

        if (user.getRole() == Role.ADMIN) {
            return projectRepository.findAll()
                    .stream()
                    .map(projectMapper::toResponse)
                    .collect(Collectors.toList());
        } else if (user.getRole() == Role.MANAGER) {
            return getProjectsByOwner(userEmail);
        } else {
            throw new UnauthorizedException("You don't have permission to view projects");
        }
    }

    @Override
    public Project findEntityById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
    }
}