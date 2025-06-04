package naneishvili.aleksandre.tasktrackerapi.service;

import naneishvili.aleksandre.tasktrackerapi.dto.request.TaskCreateRequest;
import naneishvili.aleksandre.tasktrackerapi.dto.request.TaskUpdateRequest;
import naneishvili.aleksandre.tasktrackerapi.dto.response.TaskResponse;
import naneishvili.aleksandre.tasktrackerapi.enums.Priority;
import naneishvili.aleksandre.tasktrackerapi.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {

    TaskResponse createTask(TaskCreateRequest request, String userEmail);

    TaskResponse updateTask(Long taskId, TaskUpdateRequest request, String userEmail);

    void deleteTask(Long taskId, String userEmail);

    TaskResponse getTaskById(Long taskId, String userEmail);

    TaskResponse assignTaskToUser(Long taskId, Long userId, String userEmail);

    TaskResponse updateTaskStatus(Long taskId, TaskStatus status, String userEmail);

    Page<TaskResponse> getTasksByProject(Long projectId, TaskStatus status, Priority priority,
                                         Pageable pageable, String userEmail);

    Page<TaskResponse> getTasksByAssignedUser(Long userId, TaskStatus status, Priority priority,
                                              Pageable pageable, String userEmail);

    Page<TaskResponse> getMyAssignedTasks(TaskStatus status, Priority priority,
                                          Pageable pageable, String userEmail);
}