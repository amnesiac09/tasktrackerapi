package naneishvili.aleksandre.tasktrackerapi.dto.request;

import naneishvili.aleksandre.tasktrackerapi.enums.Priority;
import naneishvili.aleksandre.tasktrackerapi.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskUpdateRequest {

    @NotBlank(message = "Task title is required")
    @Size(max = 255, message = "Title should not exceed 255 characters")
    private String title;

    @Size(max = 1000, message = "Description should not exceed 1000 characters")
    private String description;

    private TaskStatus status;

    private LocalDate dueDate;

    private Priority priority;

    private Long assignedUserId;
}