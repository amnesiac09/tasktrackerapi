package naneishvili.aleksandre.tasktrackerapi.controller;

import naneishvili.aleksandre.tasktrackerapi.dto.request.ProjectCreateRequest;
import naneishvili.aleksandre.tasktrackerapi.dto.request.ProjectUpdateRequest;
import naneishvili.aleksandre.tasktrackerapi.dto.response.ProjectResponse;
import naneishvili.aleksandre.tasktrackerapi.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectCreateRequest request,
                                                         Authentication authentication) {
        String userEmail = authentication.getName();
        ProjectResponse projectResponse = projectService.createProject(request, userEmail);
        return new ResponseEntity<>(projectResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects(Authentication authentication) {
        String userEmail = authentication.getName();
        List<ProjectResponse> projects = projectService.getAllProjects(userEmail);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id,
                                                          Authentication authentication) {
        String userEmail = authentication.getName();
        ProjectResponse projectResponse = projectService.getProjectById(id, userEmail);
        return ResponseEntity.ok(projectResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id,
                                                         @Valid @RequestBody ProjectUpdateRequest request,
                                                         Authentication authentication) {
        String userEmail = authentication.getName();
        ProjectResponse projectResponse = projectService.updateProject(id, request, userEmail);
        return ResponseEntity.ok(projectResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id,
                                              Authentication authentication) {
        String userEmail = authentication.getName();
        projectService.deleteProject(id, userEmail);
        return ResponseEntity.noContent().build();
    }
}