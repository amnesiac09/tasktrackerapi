package naneishvili.aleksandre.tasktrackerapi.service;

import naneishvili.aleksandre.tasktrackerapi.dto.request.ProjectCreateRequest;
import naneishvili.aleksandre.tasktrackerapi.dto.request.ProjectUpdateRequest;
import naneishvili.aleksandre.tasktrackerapi.dto.response.ProjectResponse;
import naneishvili.aleksandre.tasktrackerapi.entity.Project;

import java.util.List;

public interface ProjectService {

    ProjectResponse createProject(ProjectCreateRequest request, String ownerEmail);

    ProjectResponse updateProject(Long projectId, ProjectUpdateRequest request, String userEmail);

    void deleteProject(Long projectId, String userEmail);

    ProjectResponse getProjectById(Long projectId, String userEmail);

    List<ProjectResponse> getProjectsByOwner(String ownerEmail);

    List<ProjectResponse> getAllProjects(String userEmail);

    Project findEntityById(Long projectId);
}