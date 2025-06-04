package naneishvili.aleksandre.tasktrackerapi.config;

import naneishvili.aleksandre.tasktrackerapi.entity.Project;
import naneishvili.aleksandre.tasktrackerapi.entity.Task;
import naneishvili.aleksandre.tasktrackerapi.entity.User;
import naneishvili.aleksandre.tasktrackerapi.enums.Priority;
import naneishvili.aleksandre.tasktrackerapi.enums.Role;
import naneishvili.aleksandre.tasktrackerapi.enums.TaskStatus;
import naneishvili.aleksandre.tasktrackerapi.repository.ProjectRepository;
import naneishvili.aleksandre.tasktrackerapi.repository.TaskRepository;
import naneishvili.aleksandre.tasktrackerapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            initializeData();
        }
    }

    private void initializeData() {
        User admin = createUser("admin@test.com", Role.ADMIN);
        User manager1 = createUser("manager1@test.com", Role.MANAGER);
        User manager2 = createUser("manager2@test.com", Role.MANAGER);
        User user1 = createUser("user1@test.com", Role.USER);
        User user2 = createUser("user2@test.com", Role.USER);
        User user3 = createUser("user3@test.com", Role.USER);

        Project project1 = createProject("E-Commerce Platform", "Building a modern e-commerce solution", manager1);
        Project project2 = createProject("Mobile App Development", "Creating a cross-platform mobile application", manager1);
        Project project3 = createProject("Data Analytics Dashboard", "Business intelligence dashboard", manager2);
        Project project4 = createProject("Marketing Website", "Company marketing website redesign", manager2);

        createTask("Setup Database Schema", "Design and implement database structure", project1, user1, TaskStatus.DONE, Priority.HIGH);
        createTask("User Authentication System", "Implement JWT-based authentication", project1, user1, TaskStatus.IN_PROGRESS, Priority.HIGH);
        createTask("Product Catalog API", "Create REST API for product management", project1, user2, TaskStatus.TODO, Priority.MEDIUM);
        createTask("Shopping Cart Feature", "Implement shopping cart functionality", project1, null, TaskStatus.TODO, Priority.MEDIUM);
        createTask("Payment Integration", "Integrate payment gateway", project1, user2, TaskStatus.TODO, Priority.HIGH);

        createTask("UI/UX Design", "Create app mockups and designs", project2, user3, TaskStatus.DONE, Priority.MEDIUM);
        createTask("Login Screen Implementation", "Develop login/register screens", project2, user1, TaskStatus.IN_PROGRESS, Priority.HIGH);
        createTask("Profile Management", "User profile CRUD operations", project2, user2, TaskStatus.TODO, Priority.LOW);
        createTask("Push Notifications", "Implement push notification system", project2, null, TaskStatus.TODO, Priority.MEDIUM);

        createTask("Data Model Design", "Design data warehouse schema", project3, user1, TaskStatus.DONE, Priority.HIGH);
        createTask("ETL Pipeline", "Build data extraction and transformation pipeline", project3, user2, TaskStatus.IN_PROGRESS, Priority.HIGH);
        createTask("Dashboard Frontend", "Create interactive dashboard UI", project3, user3, TaskStatus.TODO, Priority.MEDIUM);
        createTask("Report Generation", "Automated report generation feature", project3, null, TaskStatus.TODO, Priority.LOW);

        createTask("Homepage Design", "Design modern homepage layout", project4, user3, TaskStatus.DONE, Priority.MEDIUM);
        createTask("Content Management", "Implement CMS for content updates", project4, user1, TaskStatus.IN_PROGRESS, Priority.MEDIUM);
        createTask("SEO Optimization", "Optimize website for search engines", project4, user2, TaskStatus.TODO, Priority.LOW);
        createTask("Contact Form", "Create contact form with validation", project4, null, TaskStatus.TODO, Priority.LOW);
    }

    private User createUser(String email, Role role) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(role);
        return userRepository.save(user);
    }

    private Project createProject(String name, String description, User owner) {
        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        project.setOwner(owner);
        return projectRepository.save(project);
    }

    private Task createTask(String title, String description, Project project, User assignedUser, TaskStatus status, Priority priority) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setProject(project);
        task.setAssignedUser(assignedUser);
        task.setStatus(status);
        task.setPriority(priority);
        return taskRepository.save(task);
    }
}