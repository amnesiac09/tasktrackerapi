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
import naneishvili.aleksandre.tasktrackerapi.service.TaskService;
import naneishvili.aleksandre.tasktrackerapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Override
    public TaskResponse createTask(TaskCreateRequest request, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Project project = projectService.findEntityById(request.getProjectId());

        if (user.getRole() != Role.ADMIN &&
                (user.getRole() != Role.MANAGER || !project.getOwner().getId().equals(user.getId()))) {
            throw new UnauthorizedException("You don't have permission to create tasks for this project");
        }

        Task task = taskMapper.toEntity(request);
        task.setProject(project);

        if (request.getAssignedUserId() != null) {
            User assignedUser = userService.findById(request.getAssignedUserId());
            task.setAssignedUser(assignedUser);
        }

        Task savedTask = taskRepository.save(task);
        return taskMapper.toResponse(savedTask);
    }

    @Override
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Task task = findEntityById(taskId);

        if (!canUserAccessTask(user, task)) {
            throw new UnauthorizedException("You don't have permission to update this task");
        }

        if (user.getRole() == Role.USER && task.getAssignedUser() != null &&
                task.getAssignedUser().getId().equals(user.getId())) {

            if (request.getStatus() != null) {
                task.setStatus(request.getStatus());
            }
        } else {
            taskMapper.updateEntity(request, task);

            if (request.getAssignedUserId() != null) {
                User assignedUser = userService.findById(request.getAssignedUserId());
                task.setAssignedUser(assignedUser);
            }
        }

        Task updatedTask = taskRepository.save(task);
        return taskMapper.toResponse(updatedTask);
    }

    @Override
    public void deleteTask(Long taskId, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Task task = findEntityById(taskId);

        if (user.getRole() != Role.ADMIN &&
                !task.getProject().getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedException("You don't have permission to delete this task");
        }

        taskRepository.delete(task);
    }

    @Override
    public TaskResponse getTaskById(Long taskId, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Task task = findEntityById(taskId);

        if (!canUserAccessTask(user, task)) {
            throw new UnauthorizedException("You don't have permission to view this task");
        }

        return taskMapper.toResponse(task);
    }

    @Override
    public TaskResponse assignTaskToUser(Long taskId, Long userId, String userEmail) {
        User user = userService.findByEmail(userEmail);

        if (user.getRole() != Role.MANAGER && user.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Only MANAGER and ADMIN can assign tasks");
        }

        Task task = findEntityById(taskId);

        if (user.getRole() == Role.MANAGER &&
                !task.getProject().getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only assign tasks from your own projects");
        }

        User assignedUser = userService.findById(userId);
        task.setAssignedUser(assignedUser);
        Task updatedTask = taskRepository.save(task);

        return taskMapper.toResponse(updatedTask);
    }

    @Override
    public TaskResponse updateTaskStatus(Long taskId, TaskStatus status, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Task task = findEntityById(taskId);

        if (task.getAssignedUser() == null ||
                !task.getAssignedUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Only assigned user can update task status");
        }

        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);

        return taskMapper.toResponse(updatedTask);
    }

    @Override
    public Page<TaskResponse> getTasksByProject(Long projectId, TaskStatus status, Priority priority,
                                                Pageable pageable, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Project project = projectService.findEntityById(projectId);

        if (user.getRole() != Role.ADMIN &&
                (user.getRole() != Role.MANAGER || !project.getOwner().getId().equals(user.getId()))) {
            throw new UnauthorizedException("You don't have permission to view tasks for this project");
        }

        Page<Task> tasks;
        if (status != null) {
            tasks = taskRepository.findTasksByProjectIdAndStatus(projectId, status, pageable);
        } else if (priority != null) {
            tasks = taskRepository.findTasksByProjectIdAndPriority(projectId, priority, pageable);
        } else {
            tasks = taskRepository.findTasksByProjectId(projectId, pageable);
        }

        return tasks.map(taskMapper::toResponse);
    }

    @Override
    public Page<TaskResponse> getTasksByAssignedUser(Long userId, TaskStatus status, Priority priority,
                                                     Pageable pageable, String userEmail) {
        User user = userService.findByEmail(userEmail);

        if (user.getRole() != Role.ADMIN && !user.getId().equals(userId)) {
            throw new UnauthorizedException("You can only view your own assigned tasks");
        }

        Page<Task> tasks;
        if (status != null) {
            tasks = taskRepository.findTasksByAssignedUserIdAndStatus(userId, status, pageable);
        } else if (priority != null) {
            tasks = taskRepository.findTasksByAssignedUserIdAndPriority(userId, priority, pageable);
        } else {
            tasks = taskRepository.findTasksByAssignedUserId(userId, pageable);
        }

        return tasks.map(taskMapper::toResponse);
    }

    @Override
    public Page<TaskResponse> getMyAssignedTasks(TaskStatus status, Priority priority,
                                                 Pageable pageable, String userEmail) {
        User user = userService.findByEmail(userEmail);
        return getTasksByAssignedUser(user.getId(), status, priority, pageable, userEmail);
    }


    private Task findEntityById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
    }

    private boolean canUserAccessTask(User user, Task task) {
        if (user.getRole() == Role.ADMIN) {
            return true;
        }

        if (task.getProject().getOwner().getId().equals(user.getId())) {
            return true;
        }

        if (task.getAssignedUser() != null && task.getAssignedUser().getId().equals(user.getId())) {
            return true;
        }

        return false;
    }
}