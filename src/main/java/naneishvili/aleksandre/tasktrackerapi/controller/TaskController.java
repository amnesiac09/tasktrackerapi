package naneishvili.aleksandre.tasktrackerapi.controller;

import naneishvili.aleksandre.tasktrackerapi.dto.request.TaskCreateRequest;
import naneishvili.aleksandre.tasktrackerapi.dto.request.TaskUpdateRequest;
import naneishvili.aleksandre.tasktrackerapi.dto.response.TaskResponse;
import naneishvili.aleksandre.tasktrackerapi.enums.Priority;
import naneishvili.aleksandre.tasktrackerapi.enums.TaskStatus;
import naneishvili.aleksandre.tasktrackerapi.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskCreateRequest request,
                                                   Authentication authentication) {
        String userEmail = authentication.getName();
        TaskResponse taskResponse = taskService.createTask(request, userEmail);
        return new ResponseEntity<>(taskResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id,
                                                    Authentication authentication) {
        String userEmail = authentication.getName();
        TaskResponse taskResponse = taskService.getTaskById(id, userEmail);
        return ResponseEntity.ok(taskResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id,
                                                   @Valid @RequestBody TaskUpdateRequest request,
                                                   Authentication authentication) {
        String userEmail = authentication.getName();
        TaskResponse taskResponse = taskService.updateTask(id, request, userEmail);
        return ResponseEntity.ok(taskResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id,
                                           Authentication authentication) {
        String userEmail = authentication.getName();
        taskService.deleteTask(id, userEmail);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/assign/{userId}")
    public ResponseEntity<TaskResponse> assignTaskToUser(@PathVariable Long id,
                                                         @PathVariable Long userId,
                                                         Authentication authentication) {
        String userEmail = authentication.getName();
        TaskResponse taskResponse = taskService.assignTaskToUser(id, userId, userEmail);
        return ResponseEntity.ok(taskResponse);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(@PathVariable Long id,
                                                         @RequestParam TaskStatus status,
                                                         Authentication authentication) {
        String userEmail = authentication.getName();
        TaskResponse taskResponse = taskService.updateTaskStatus(id, status, userEmail);
        return ResponseEntity.ok(taskResponse);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<Page<TaskResponse>> getTasksByProject(@PathVariable Long projectId,
                                                                @RequestParam(required = false) TaskStatus status,
                                                                @RequestParam(required = false) Priority priority,
                                                                Pageable pageable,
                                                                Authentication authentication) {
        String userEmail = authentication.getName();
        Page<TaskResponse> tasks = taskService.getTasksByProject(projectId, status, priority, pageable, userEmail);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<TaskResponse>> getTasksByAssignedUser(@PathVariable Long userId,
                                                                     @RequestParam(required = false) TaskStatus status,
                                                                     @RequestParam(required = false) Priority priority,
                                                                     Pageable pageable,
                                                                     Authentication authentication) {
        String userEmail = authentication.getName();
        Page<TaskResponse> tasks = taskService.getTasksByAssignedUser(userId, status, priority, pageable, userEmail);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/my-tasks")
    public ResponseEntity<Page<TaskResponse>> getMyAssignedTasks(@RequestParam(required = false) TaskStatus status,
                                                                 @RequestParam(required = false) Priority priority,
                                                                 Pageable pageable,
                                                                 Authentication authentication) {
        String userEmail = authentication.getName();
        Page<TaskResponse> tasks = taskService.getMyAssignedTasks(status, priority, pageable, userEmail);
        return ResponseEntity.ok(tasks);
    }
}