package naneishvili.aleksandre.tasktrackerapi.mapper;

import naneishvili.aleksandre.tasktrackerapi.dto.request.ProjectCreateRequest;
import naneishvili.aleksandre.tasktrackerapi.dto.request.ProjectUpdateRequest;
import naneishvili.aleksandre.tasktrackerapi.dto.response.ProjectResponse;
import naneishvili.aleksandre.tasktrackerapi.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ProjectMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    Project toEntity(ProjectCreateRequest request);

    ProjectResponse toResponse(Project project);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    void updateEntity(ProjectUpdateRequest request, @MappingTarget Project project);
}