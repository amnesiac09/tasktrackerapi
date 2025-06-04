package naneishvili.aleksandre.tasktrackerapi.dto.response;

import naneishvili.aleksandre.tasktrackerapi.enums.Priority;
import naneishvili.aleksandre.tasktrackerapi.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDate dueDate;
    private Priority priority;
    private ProjectResponse project;
    private UserResponse assignedUser;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}