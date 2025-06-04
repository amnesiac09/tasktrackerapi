package naneishvili.aleksandre.tasktrackerapi.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private UserResponse owner;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}