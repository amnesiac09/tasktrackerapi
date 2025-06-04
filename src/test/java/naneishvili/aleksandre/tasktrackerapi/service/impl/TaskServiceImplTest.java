package naneishvili.aleksandre.tasktrackerapi.service.impl;

import naneishvili.aleksandre.tasktrackerapi.dto.request.TaskCreateRequest;
import naneishvili.aleksandre.tasktrackerapi.dto.request.TaskUpdateRequest;
import naneishvili.aleksandre.tasktrackerapi.dto.response.TaskResponse;
import naneishvili.aleksandre.tasktrackerapi.entity.Project;
import naneishvili.aleksandre.tasktrackerapi.entity.Task;
import naneishvili.aleksandre.tasktrackerapi.entity.User;
import naneishvili.aleksandre.tasktrackerapi.enums.Priority;
import naneishvili.aleksandre.tasktrackerapi.enums.Role;
import naneishvili.aleksandre.tasktrackerapi.enums.TaskStatus;
import naneishvili.aleksandre.tasktrackerapi.exception.ResourceNotFoundException;
import naneishvili.aleksandre.tasktrackerapi.exception.UnauthorizedException;
import naneishvili.aleksandre.tasktrackerapi.mapper.TaskMapper;
import naneishvili.aleksandre.tasktrackerapi.repository.TaskRepository;
import naneishvili.aleksandre.tasktrackerapi.service.ProjectService;
import naneishvili.aleksandre.tasktrackerapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private UserService userService;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private TaskServiceImpl taskService;

    private User adminUser;
    private User managerUser;
    private User regularUser;
    private User anotherUser;
    private Project testProject;
    private Task testTask;
    private TaskCreateRequest createRequest;
    private TaskUpdateRequest updateRequest;
    private TaskResponse taskResponse;
    private Pageable pageable;

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

        regularUser = new User();
        regularUser.setId(3L);
        regularUser.setEmail("user@example.com");
        regularUser.setRole(Role.USER);

        anotherUser = new User();
        anotherUser.setId(4L);
        anotherUser.setEmail("another@example.com");
        anotherUser.setRole(Role.USER);

        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setOwner(managerUser);

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setProject(testProject);
        testTask.setAssignedUser(regularUser);
        testTask.setStatus(TaskStatus.TODO);
        testTask.setPriority(Priority.MEDIUM);

        createRequest = new TaskCreateRequest();
        createRequest.setTitle("New Task");
        createRequest.setDescription("New Description");
        createRequest.setProjectId(1L);
        createRequest.setAssignedUserId(3L);
        createRequest.setStatus(TaskStatus.TODO);
        createRequest.setPriority(Priority.HIGH);

        updateRequest = new TaskUpdateRequest();
        updateRequest.setTitle("Updated Task");
        updateRequest.setDescription("Updated Description");
        updateRequest.setStatus(TaskStatus.IN_PROGRESS);
        updateRequest.setPriority(Priority.LOW);
        updateRequest.setAssignedUserId(4L);

        taskResponse = new TaskResponse();
        taskResponse.setId(1L);
        taskResponse.setTitle("Test Task");
        taskResponse.setDescription("Test Description");

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void createTask_AsAdmin_Success() {
        when(userService.findByEmail("admin@example.com")).thenReturn(adminUser);
        when(projectService.findEntityById(1L)).thenReturn(testProject);
        when(userService.findById(3L)).thenReturn(regularUser);
        when(taskMapper.toEntity(createRequest)).thenReturn(testTask);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(taskMapper.toResponse(testTask)).thenReturn(taskResponse);

        TaskResponse result = taskService.createTask(createRequest, "admin@example.com");

        assertThat(result).isNotNull();
        verify(taskRepository).save(testTask);
    }

    @Test
    void createTask_AsManager_Success() {
        when(userService.findByEmail("manager@example.com")).thenReturn(managerUser);
        when(projectService.findEntityById(1L)).thenReturn(testProject);
        when(userService.findById(3L)).thenReturn(regularUser);
        when(taskMapper.toEntity(createRequest)).thenReturn(testTask);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(taskMapper.toResponse(testTask)).thenReturn(taskResponse);

        TaskResponse result = taskService.createTask(createRequest, "manager@example.com");

        assertThat(result).isNotNull();
        verify(taskRepository).save(testTask);
    }

    @Test
    void createTask_WithoutAssignedUser_Success() {
        createRequest.setAssignedUserId(null);
        when(userService.findByEmail("manager@example.com")).thenReturn(managerUser);
        when(projectService.findEntityById(1L)).thenReturn(testProject);
        when(taskMapper.toEntity(createRequest)).thenReturn(testTask);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(taskMapper.toResponse(testTask)).thenReturn(taskResponse);

        TaskResponse result = taskService.createTask(createRequest, "manager@example.com");

        assertThat(result).isNotNull();
        verify(userService, never()).findById(anyLong());
        verify(taskRepository).save(testTask);
    }

    @Test
    void createTask_AsNonOwnerManager_ThrowsUnauthorizedException() {
        User anotherManager = new User();
        anotherManager.setId(5L);
        anotherManager.setRole(Role.MANAGER);

        when(userService.findByEmail("another@example.com")).thenReturn(anotherManager);
        when(projectService.findEntityById(1L)).thenReturn(testProject);

        assertThatThrownBy(() -> taskService.createTask(createRequest, "another@example.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("don't have permission");
    }

    @Test
    void createTask_AsUser_ThrowsUnauthorizedException() {
        when(userService.findByEmail("user@example.com")).thenReturn(regularUser);
        when(projectService.findEntityById(1L)).thenReturn(testProject);

        assertThatThrownBy(() -> taskService.createTask(createRequest, "user@example.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("don't have permission");
    }

    @Test
    void updateTask_AsAdmin_Success() {
        when(userService.findByEmail("admin@example.com")).thenReturn(adminUser);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(userService.findById(4L)).thenReturn(anotherUser);
        when(taskRepository.save(testTask)).thenReturn(testTask);
        when(taskMapper.toResponse(testTask)).thenReturn(taskResponse);

        TaskResponse result = taskService.updateTask(1L, updateRequest, "admin@example.com");

        assertThat(result).isNotNull();
        verify(taskMapper).updateEntity(updateRequest, testTask);
        verify(taskRepository).save(testTask);
    }

    @Test
    void updateTask_AsProjectOwner_Success() {
        when(userService.findByEmail("manager@example.com")).thenReturn(managerUser);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(userService.findById(4L)).thenReturn(anotherUser);
        when(taskRepository.save(testTask)).thenReturn(testTask);
        when(taskMapper.toResponse(testTask)).thenReturn(taskResponse);

        TaskResponse result = taskService.updateTask(1L, updateRequest, "manager@example.com");

        assertThat(result).isNotNull();
        verify(taskMapper).updateEntity(updateRequest, testTask);
        verify(taskRepository).save(testTask);
    }

    @Test
    void updateTask_AsAssignedUser_OnlyStatusUpdate() {
        when(userService.findByEmail("user@example.com")).thenReturn(regularUser);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(testTask)).thenReturn(testTask);
        when(taskMapper.toResponse(testTask)).thenReturn(taskResponse);

        TaskResponse result = taskService.updateTask(1L, updateRequest, "user@example.com");

        assertThat(result).isNotNull();
        verify(taskMapper, never()).updateEntity(updateRequest, testTask);
        verify(taskRepository).save(testTask);
    }

    @Test
    void updateTask_AsUnauthorizedUser_ThrowsException() {
        when(userService.findByEmail("another@example.com")).thenReturn(anotherUser);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        assertThatThrownBy(() -> taskService.updateTask(1L, updateRequest, "another@example.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("don't have permission");
    }

    @Test
    void deleteTask_AsAdmin_Success() {
        when(userService.findByEmail("admin@example.com")).thenReturn(adminUser);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        taskService.deleteTask(1L, "admin@example.com");

        verify(taskRepository).delete(testTask);
    }

    @Test
    void deleteTask_AsProjectOwner_Success() {
        when(userService.findByEmail("manager@example.com")).thenReturn(managerUser);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        taskService.deleteTask(1L, "manager@example.com");

        verify(taskRepository).delete(testTask);
    }

    @Test
    void deleteTask_AsUnauthorizedUser_ThrowsException() {
        when(userService.findByEmail("user@example.com")).thenReturn(regularUser);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        assertThatThrownBy(() -> taskService.deleteTask(1L, "user@example.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("don't have permission");
    }

    @Test
    void getTaskById_AsAdmin_Success() {
        when(userService.findByEmail("admin@example.com")).thenReturn(adminUser);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskMapper.toResponse(testTask)).thenReturn(taskResponse);

        TaskResponse result = taskService.getTaskById(1L, "admin@example.com");

        assertThat(result).isNotNull();
    }

    @Test
    void getTaskById_AsProjectOwner_Success() {
        when(userService.findByEmail("manager@example.com")).thenReturn(managerUser);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskMapper.toResponse(testTask)).thenReturn(taskResponse);

        TaskResponse result = taskService.getTaskById(1L, "manager@example.com");

        assertThat(result).isNotNull();
    }

    @Test
    void getTaskById_AsAssignedUser_Success() {
        when(userService.findByEmail("user@example.com")).thenReturn(regularUser);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskMapper.toResponse(testTask)).thenReturn(taskResponse);

        TaskResponse result = taskService.getTaskById(1L, "user@example.com");

        assertThat(result).isNotNull();
    }

    @Test
    void getTaskById_AsUnauthorizedUser_ThrowsException() {
        when(userService.findByEmail("another@example.com")).thenReturn(anotherUser);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        assertThatThrownBy(() -> taskService.getTaskById(1L, "another@example.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("don't have permission");
    }

    @Test
    void getTaskById_TaskNotFound_ThrowsException() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(999L, "admin@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found");
    }

    @Test
    void assignTaskToUser_AsAdmin_Success() {
        when(userService.findByEmail("admin@example.com")).thenReturn(adminUser);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(userService.findById(4L)).thenReturn(anotherUser);
        when(taskRepository.save(testTask)).thenReturn(testTask);
        when(taskMapper.toResponse(testTask)).thenReturn(taskResponse);

        TaskResponse result = taskService.assignTaskToUser(1L, 4L, "admin@example.com");

        assertThat(result).isNotNull();
        verify(taskRepository).save(testTask);
    }

    @Test
    void assignTaskToUser_AsProjectOwner_Success() {
        when(userService.findByEmail("manager@example.com")).thenReturn(managerUser);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(userService.findById(4L)).thenReturn(anotherUser);
        when(taskRepository.save(testTask)).thenReturn(testTask);
        when(taskMapper.toResponse(testTask)).thenReturn(taskResponse);

        TaskResponse result = taskService.assignTaskToUser(1L, 4L, "manager@example.com");

        assertThat(result).isNotNull();
        verify(taskRepository).save(testTask);
    }

    @Test
    void assignTaskToUser_AsNonOwnerManager_ThrowsException() {
        User anotherManager = new User();
        anotherManager.setId(5L);
        anotherManager.setRole(Role.MANAGER);

        when(userService.findByEmail("another@example.com")).thenReturn(anotherManager);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        assertThatThrownBy(() -> taskService.assignTaskToUser(1L, 4L, "another@example.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("can only assign tasks from your own projects");
    }

    @Test
    void assignTaskToUser_AsUser_ThrowsException() {
        when(userService.findByEmail("user@example.com")).thenReturn(regularUser);

        assertThatThrownBy(() -> taskService.assignTaskToUser(1L, 4L, "user@example.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Only MANAGER and ADMIN can assign tasks");
    }

    @Test
    void updateTaskStatus_AsAssignedUser_Success() {
        when(userService.findByEmail("user@example.com")).thenReturn(regularUser);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(testTask)).thenReturn(testTask);
        when(taskMapper.toResponse(testTask)).thenReturn(taskResponse);

        TaskResponse result = taskService.updateTaskStatus(1L, TaskStatus.DONE, "user@example.com");

        assertThat(result).isNotNull();
        verify(taskRepository).save(testTask);
    }

    @Test
    void updateTaskStatus_TaskNotAssigned_ThrowsException() {
        testTask.setAssignedUser(null);
        when(userService.findByEmail("user@example.com")).thenReturn(regularUser);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        assertThatThrownBy(() -> taskService.updateTaskStatus(1L, TaskStatus.DONE, "user@example.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Only assigned user can update task status");
    }

    @Test
    void updateTaskStatus_AsNonAssignedUser_ThrowsException() {
        when(userService.findByEmail("another@example.com")).thenReturn(anotherUser);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        assertThatThrownBy(() -> taskService.updateTaskStatus(1L, TaskStatus.DONE, "another@example.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Only assigned user can update task status");
    }

    @Test
    void getTasksByProject_AsAdmin_Success() {
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(testTask));
        when(userService.findByEmail("admin@example.com")).thenReturn(adminUser);
        when(projectService.findEntityById(1L)).thenReturn(testProject);
        when(taskRepository.findTasksByProjectId(1L, pageable)).thenReturn(taskPage);
        when(taskMapper.toResponse(testTask)).thenReturn(taskResponse);

        Page<TaskResponse> result = taskService.getTasksByProject(1L, null, null, pageable, "admin@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getTasksByProject_WithStatusFilter_Success() {
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(testTask));
        when(userService.findByEmail("manager@example.com")).thenReturn(managerUser);
        when(projectService.findEntityById(1L)).thenReturn(testProject);
        when(taskRepository.findTasksByProjectIdAndStatus(1L, TaskStatus.TODO, pageable)).thenReturn(taskPage);
        when(taskMapper.toResponse(testTask)).thenReturn(taskResponse);

        Page<TaskResponse> result = taskService.getTasksByProject(1L, TaskStatus.TODO, null, pageable, "manager@example.com");

        assertThat(result).isNotNull();
        verify(taskRepository).findTasksByProjectIdAndStatus(1L, TaskStatus.TODO, pageable);
    }

    @Test
    void getTasksByProject_WithPriorityFilter_Success() {
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(testTask));
        when(userService.findByEmail("manager@example.com")).thenReturn(managerUser);
        when(projectService.findEntityById(1L)).thenReturn(testProject);
        when(taskRepository.findTasksByProjectIdAndPriority(1L, Priority.HIGH, pageable)).thenReturn(taskPage);
        when(taskMapper.toResponse(testTask)).thenReturn(taskResponse);

        Page<TaskResponse> result = taskService.getTasksByProject(1L, null, Priority.HIGH, pageable, "manager@example.com");

        assertThat(result).isNotNull();
        verify(taskRepository).findTasksByProjectIdAndPriority(1L, Priority.HIGH, pageable);
    }

    @Test
    void getTasksByProject_AsUnauthorizedUser_ThrowsException() {
        User anotherManager = new User();
        anotherManager.setId(5L);
        anotherManager.setRole(Role.MANAGER);

        when(userService.findByEmail("another@example.com")).thenReturn(anotherManager);
        when(projectService.findEntityById(1L)).thenReturn(testProject);

        assertThatThrownBy(() -> taskService.getTasksByProject(1L, null, null, pageable, "another@example.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("don't have permission");
    }

    @Test
    void getTasksByAssignedUser_AsAdmin_Success() {
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(testTask));
        when(userService.findByEmail("admin@example.com")).thenReturn(adminUser);
        when(taskRepository.findTasksByAssignedUserId(3L, pageable)).thenReturn(taskPage);
        when(taskMapper.toResponse(testTask)).thenReturn(taskResponse);

        Page<TaskResponse> result = taskService.getTasksByAssignedUser(3L, null, null, pageable, "admin@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getTasksByAssignedUser_AsOwnUser_Success() {
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(testTask));
        when(userService.findByEmail("user@example.com")).thenReturn(regularUser);
        when(taskRepository.findTasksByAssignedUserId(3L, pageable)).thenReturn(taskPage);
        when(taskMapper.toResponse(testTask)).thenReturn(taskResponse);

        Page<TaskResponse> result = taskService.getTasksByAssignedUser(3L, null, null, pageable, "user@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getTasksByAssignedUser_WithStatusFilter_Success() {
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(testTask));
        when(userService.findByEmail("user@example.com")).thenReturn(regularUser);
        when(taskRepository.findTasksByAssignedUserIdAndStatus(3L, TaskStatus.TODO, pageable)).thenReturn(taskPage);
        when(taskMapper.toResponse(testTask)).thenReturn(taskResponse);

        Page<TaskResponse> result = taskService.getTasksByAssignedUser(3L, TaskStatus.TODO, null, pageable, "user@example.com");

        assertThat(result).isNotNull();
        verify(taskRepository).findTasksByAssignedUserIdAndStatus(3L, TaskStatus.TODO, pageable);
    }

    @Test
    void getTasksByAssignedUser_WithPriorityFilter_Success() {
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(testTask));
        when(userService.findByEmail("user@example.com")).thenReturn(regularUser);
        when(taskRepository.findTasksByAssignedUserIdAndPriority(3L, Priority.HIGH, pageable)).thenReturn(taskPage);
        when(taskMapper.toResponse(testTask)).thenReturn(taskResponse);

        Page<TaskResponse> result = taskService.getTasksByAssignedUser(3L, null, Priority.HIGH, pageable, "user@example.com");

        assertThat(result).isNotNull();
        verify(taskRepository).findTasksByAssignedUserIdAndPriority(3L, Priority.HIGH, pageable);
    }

    @Test
    void getTasksByAssignedUser_AsUnauthorizedUser_ThrowsException() {
        when(userService.findByEmail("another@example.com")).thenReturn(anotherUser);

        assertThatThrownBy(() -> taskService.getTasksByAssignedUser(3L, null, null, pageable, "another@example.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("You can only view your own assigned tasks");
    }

    @Test
    void getMyAssignedTasks_Success() {
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(testTask));
        when(userService.findByEmail("user@example.com")).thenReturn(regularUser);
        when(taskRepository.findTasksByAssignedUserId(3L, pageable)).thenReturn(taskPage);
        when(taskMapper.toResponse(testTask)).thenReturn(taskResponse);

        Page<TaskResponse> result = taskService.getMyAssignedTasks(null, null, pageable, "user@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }
}