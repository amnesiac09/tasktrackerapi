package naneishvili.aleksandre.tasktrackerapi.repository;

import naneishvili.aleksandre.tasktrackerapi.entity.Task;
import naneishvili.aleksandre.tasktrackerapi.enums.Priority;
import naneishvili.aleksandre.tasktrackerapi.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findTasksByProjectId(Long projectId, Pageable pageable);
    Page<Task> findTasksByAssignedUserId(Long userId, Pageable pageable);

    Page<Task> findTasksByProjectIdAndStatus(Long projectId, TaskStatus status, Pageable pageable);
    Page<Task> findTasksByProjectIdAndPriority(Long projectId, Priority priority, Pageable pageable);

    Page<Task> findTasksByAssignedUserIdAndStatus(Long userId, TaskStatus status, Pageable pageable);
    Page<Task> findTasksByAssignedUserIdAndPriority(Long userId, Priority priority, Pageable pageable);
}
