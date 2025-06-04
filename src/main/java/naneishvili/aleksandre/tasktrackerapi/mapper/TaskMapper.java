package naneishvili.aleksandre.tasktrackerapi.mapper;

import naneishvili.aleksandre.tasktrackerapi.dto.request.TaskCreateRequest;
import naneishvili.aleksandre.tasktrackerapi.dto.request.TaskUpdateRequest;
import naneishvili.aleksandre.tasktrackerapi.dto.response.TaskResponse;
import naneishvili.aleksandre.tasktrackerapi.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ProjectMapper.class})
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignedUser", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    Task toEntity(TaskCreateRequest request);

    TaskResponse toResponse(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignedUser", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    void updateEntity(TaskUpdateRequest request, @MappingTarget Task task);
}