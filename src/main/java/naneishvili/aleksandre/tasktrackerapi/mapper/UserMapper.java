package naneishvili.aleksandre.tasktrackerapi.mapper;

import naneishvili.aleksandre.tasktrackerapi.dto.request.UserRegistrationRequest;
import naneishvili.aleksandre.tasktrackerapi.dto.response.UserResponse;
import naneishvili.aleksandre.tasktrackerapi.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "ownedProjects", ignore = true)
    @Mapping(target = "assignedTasks", ignore = true)
    User toEntity(UserRegistrationRequest request);

    UserResponse toResponse(User user);
}