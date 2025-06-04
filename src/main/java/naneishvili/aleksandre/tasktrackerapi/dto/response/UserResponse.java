package naneishvili.aleksandre.tasktrackerapi.dto.response;

import naneishvili.aleksandre.tasktrackerapi.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private Role role;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}