package naneishvili.aleksandre.tasktrackerapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProjectUpdateRequest {

    @NotBlank(message = "Project name is required")
    @Size(max = 255, message = "Project name should not exceed 255 characters")
    private String name;

    @Size(max = 1000, message = "Description should not exceed 1000 characters")
    private String description;
}