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
import naneishvili.aleksandre.tasktrackerapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private User adminUser;
    private User managerUser;
    private User anotherManager;
    private User regularUser;
    private Project testProject;
    private Project anotherProject;
    private ProjectCreateRequest createRequest;
    private ProjectUpdateRequest updateRequest;
    private ProjectResponse projectResponse;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setEmail("admin@example.com");
        adminUser.setRole(Role.ADMIN);

        managerUser = new User();
        managerUser.setId(2L);
        managerUser.setEmail("manager@example.com");
        managerUser.setRole(Role.MANAGER);

        anotherManager = new User();
        anotherManager.setId(3L);
        anotherManager.setEmail("another@example.com");
        anotherManager.setRole(Role.MANAGER);

        regularUser = new User();
        regularUser.setId(4L);
        regularUser.setEmail("user@example.com");
        regularUser.setRole(Role.USER);

        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        testProject.setOwner(managerUser);

        anotherProject = new Project();
        anotherProject.setId(2L);
        anotherProject.setName("Another Project");
        anotherProject.setDescription("Another Description");
        anotherProject.setOwner(anotherManager);

        createRequest = new ProjectCreateRequest();
        createRequest.setName("New Project");
        createRequest.setDescription("New Description");

        updateRequest = new ProjectUpdateRequest();
        updateRequest.setName("Updated Project");
        updateRequest.setDescription("Updated Description");

        projectResponse = new ProjectResponse();
        projectResponse.setId(1L);
        projectResponse.setName("Test Project");
        projectResponse.setDescription("Test Description");
    }

    @Test
    void createProject_AsAdmin_Success() {
        when(userService.findByEmail("admin@example.com")).thenReturn(adminUser);
        when(projectMapper.toEntity(createRequest)).thenReturn(testProject);
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);
        when(projectMapper.toResponse(testProject)).thenReturn(projectResponse);

        ProjectResponse result = projectService.createProject(createRequest, "admin@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Project");
        verify(projectRepository).save(testProject);
    }

    @Test
    void createProject_AsManager_Success() {
        when(userService.findByEmail("manager@example.com")).thenReturn(managerUser);
        when(projectMapper.toEntity(createRequest)).thenReturn(testProject);
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);
        when(projectMapper.toResponse(testProject)).thenReturn(projectResponse);

        ProjectResponse result = projectService.createProject(createRequest, "manager@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Project");
        verify(projectRepository).save(testProject);
    }

    @Test
    void createProject_AsUser_ThrowsUnauthorizedException() {
        when(userService.findByEmail("user@example.com")).thenReturn(regularUser);

        assertThatThrownBy(() -> projectService.createProject(createRequest, "user@example.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Only MANAGER and ADMIN can create projects");
    }

    @Test
    void updateProject_AsAdmin_Success() {
        when(userService.findByEmail("admin@example.com")).thenReturn(adminUser);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(testProject)).thenReturn(testProject);
        when(projectMapper.toResponse(testProject)).thenReturn(projectResponse);

        ProjectResponse result = projectService.updateProject(1L, updateRequest, "admin@example.com");

        assertThat(result).isNotNull();
        verify(projectMapper).updateEntity(updateRequest, testProject);
        verify(projectRepository).save(testProject);
    }

    @Test
    void updateProject_AsOwner_Success() {
        when(userService.findByEmail("manager@example.com")).thenReturn(managerUser);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(testProject)).thenReturn(testProject);
        when(projectMapper.toResponse(testProject)).thenReturn(projectResponse);

        ProjectResponse result = projectService.updateProject(1L, updateRequest, "manager@example.com");

        assertThat(result).isNotNull();
        verify(projectMapper).updateEntity(updateRequest, testProject);
        verify(projectRepository).save(testProject);
    }

    @Test
    void updateProject_AsNonOwnerManager_ThrowsUnauthorizedException() {
        when(userService.findByEmail("another@example.com")).thenReturn(anotherManager);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        assertThatThrownBy(() -> projectService.updateProject(1L, updateRequest, "another@example.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("don't have permission");
    }

    @Test
    void updateProject_AsUser_ThrowsUnauthorizedException() {
        when(userService.findByEmail("user@example.com")).thenReturn(regularUser);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        assertThatThrownBy(() -> projectService.updateProject(1L, updateRequest, "user@example.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("don't have permission");
    }

    @Test
    void deleteProject_AsAdmin_Success() {
        when(userService.findByEmail("admin@example.com")).thenReturn(adminUser);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        projectService.deleteProject(1L, "admin@example.com");

        verify(projectRepository).delete(testProject);
    }

    @Test
    void deleteProject_AsOwner_Success() {
        when(userService.findByEmail("manager@example.com")).thenReturn(managerUser);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        projectService.deleteProject(1L, "manager@example.com");

        verify(projectRepository).delete(testProject);
    }

    @Test
    void deleteProject_AsNonOwnerManager_ThrowsUnauthorizedException() {
        when(userService.findByEmail("another@example.com")).thenReturn(anotherManager);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        assertThatThrownBy(() -> projectService.deleteProject(1L, "another@example.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("don't have permission");
    }

    @Test
    void deleteProject_AsUser_ThrowsUnauthorizedException() {
        when(userService.findByEmail("user@example.com")).thenReturn(regularUser);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        assertThatThrownBy(() -> projectService.deleteProject(1L, "user@example.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("don't have permission");
    }

    @Test
    void getProjectById_AsAdmin_Success() {
        when(userService.findByEmail("admin@example.com")).thenReturn(adminUser);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(projectMapper.toResponse(testProject)).thenReturn(projectResponse);

        ProjectResponse result = projectService.getProjectById(1L, "admin@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getProjectById_AsOwner_Success() {
        when(userService.findByEmail("manager@example.com")).thenReturn(managerUser);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(projectMapper.toResponse(testProject)).thenReturn(projectResponse);

        ProjectResponse result = projectService.getProjectById(1L, "manager@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getProjectById_AsNonOwnerManager_ThrowsUnauthorizedException() {
        when(userService.findByEmail("another@example.com")).thenReturn(anotherManager);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        assertThatThrownBy(() -> projectService.getProjectById(1L, "another@example.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("don't have permission");
    }

    @Test
    void getProjectById_AsUser_ThrowsUnauthorizedException() {
        when(userService.findByEmail("user@example.com")).thenReturn(regularUser);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        assertThatThrownBy(() -> projectService.getProjectById(1L, "user@example.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("don't have permission");
    }

    @Test
    void getProjectsByOwner_ReturnsProjects() {
        List<Project> projects = Arrays.asList(testProject);
        when(userService.findByEmail("manager@example.com")).thenReturn(managerUser);
        when(projectRepository.findByOwner(managerUser)).thenReturn(projects);
        when(projectMapper.toResponse(testProject)).thenReturn(projectResponse);

        List<ProjectResponse> result = projectService.getProjectsByOwner("manager@example.com");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Project");
    }

    @Test
    void getProjectsByOwner_NoProjects_ReturnsEmptyList() {
        when(userService.findByEmail("manager@example.com")).thenReturn(managerUser);
        when(projectRepository.findByOwner(managerUser)).thenReturn(Collections.emptyList());

        List<ProjectResponse> result = projectService.getProjectsByOwner("manager@example.com");

        assertThat(result).isEmpty();
    }

    @Test
    void getAllProjects_AsAdmin_ReturnsAllProjects() {
        List<Project> allProjects = Arrays.asList(testProject, anotherProject);
        when(userService.findByEmail("admin@example.com")).thenReturn(adminUser);
        when(projectRepository.findAll()).thenReturn(allProjects);
        when(projectMapper.toResponse(testProject)).thenReturn(projectResponse);
        when(projectMapper.toResponse(anotherProject)).thenReturn(new ProjectResponse());

        List<ProjectResponse> result = projectService.getAllProjects("admin@example.com");

        assertThat(result).hasSize(2);
        verify(projectRepository).findAll();
    }

    @Test
    void getAllProjects_AsManager_ReturnsOwnProjects() {
        List<Project> ownProjects = Arrays.asList(testProject);
        when(userService.findByEmail("manager@example.com")).thenReturn(managerUser);
        when(projectRepository.findByOwner(managerUser)).thenReturn(ownProjects);
        when(projectMapper.toResponse(testProject)).thenReturn(projectResponse);

        List<ProjectResponse> result = projectService.getAllProjects("manager@example.com");

        assertThat(result).hasSize(1);
        verify(projectRepository).findByOwner(managerUser);
        verify(projectRepository, never()).findAll();
    }

    @Test
    void getAllProjects_AsUser_ThrowsUnauthorizedException() {
        when(userService.findByEmail("user@example.com")).thenReturn(regularUser);

        assertThatThrownBy(() -> projectService.getAllProjects("user@example.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("don't have permission");
    }

    @Test
    void findEntityById_ProjectExists_ReturnsProject() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        Project result = projectService.findEntityById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findEntityById_ProjectNotExists_ThrowsResourceNotFoundException() {
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.findEntityById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Project not found");
    }
}