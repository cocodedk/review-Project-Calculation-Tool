package com.example.pkveksamen.repository;

import com.example.pkveksamen.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class H2RepositoryIntegrationTest {

    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    H2RepositoryIntegrationTest(EmployeeRepository employeeRepository,
                                ProjectRepository projectRepository,
                                TaskRepository taskRepository,
                                JdbcTemplate jdbcTemplate) {
        this.employeeRepository = employeeRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        jdbcTemplate.execute("TRUNCATE TABLE sub_task");
        jdbcTemplate.execute("TRUNCATE TABLE task");
        jdbcTemplate.execute("TRUNCATE TABLE sub_project");
        jdbcTemplate.execute("TRUNCATE TABLE project_employee");
        jdbcTemplate.execute("TRUNCATE TABLE project");
        jdbcTemplate.execute("TRUNCATE TABLE employee_role");
        jdbcTemplate.execute("TRUNCATE TABLE employee");
        jdbcTemplate.execute("TRUNCATE TABLE role");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");

        jdbcTemplate.execute("ALTER TABLE employee ALTER COLUMN employee_id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE role ALTER COLUMN role_id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE project ALTER COLUMN project_id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE sub_project ALTER COLUMN sub_project_id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE task ALTER COLUMN task_id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE sub_task ALTER COLUMN sub_task_id RESTART WITH 1");
    }

    @Nested
    @DisplayName("Employee Repository Tests")
    class EmployeeRepositoryTests {

        @Test
        @DisplayName("Skal oprette employee med AlphaRole")
        void shouldCreateEmployeeWithAlphaRole() {
            employeeRepository.createEmployee(
                    "junes.mohamed",
                    "password123",
                    "junes@alphasolutions.dk",
                    EmployeeRole.TEAM_MEMBER.getDisplayName(),
                    AlphaRole.Developer.getDisplayName()
            );

            List<Employee> employees = employeeRepository.getAllEmployees();
            assertThat(employees).hasSize(1);

            Employee employee = employees.get(0);
            assertThat(employee.getUsername()).isEqualTo("junes.mohamed");
            assertThat(employee.getEmail()).isEqualTo("junes@alphasolutions.dk");
            assertThat(employee.getRole()).isEqualTo(EmployeeRole.TEAM_MEMBER);
            assertThat(employee.getAlphaRoles()).contains(AlphaRole.Developer);
        }

        @Test
        @DisplayName("Skal validere login korrekt")
        void shouldValidateLoginCorrectly() {
            employeeRepository.createEmployee(
                    "mohamed.ali",
                    "secure_password",
                    "mohamed@alphasolutions.dk",
                    EmployeeRole.TEAM_MEMBER.getDisplayName(),
                    AlphaRole.BackendDeveloper.getDisplayName()
            );

            Integer validId = employeeRepository.validateLogin("mohamed.ali", "secure_password");
            assertThat(validId).isGreaterThan(0);

            Integer invalidId = employeeRepository.validateLogin("mohamed.ali", "wrong_password");
            assertThat(invalidId).isEqualTo(0);
        }
        @Nested
        @DisplayName("Project Repository Tests")
        class ProjectRepositoryTests {

            @Test
            @DisplayName("Skal oprette projekt")
            void shouldCreateProject() {
                Integer employeeId = createTestEmployee();

                projectRepository.createProject(
                        "E-commerce Platform",
                        "Build online shop",
                        LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31),
                        "Acme Corp",
                        employeeId
                );

                List<Project> projects = projectRepository.showProjectsByEmployeeId(employeeId);
                assertThat(projects).hasSize(1);
                assertThat(projects.get(0).getProjectName()).isEqualTo("E-commerce Platform");
            }

            @Test
            @DisplayName("Skal finde projekt by ID")
            void shouldFindProjectById() {
                Integer employeeId = createTestEmployee();
                projectRepository.createProject(
                        "Test Project", "Description",
                        LocalDate.now(), LocalDate.now().plusDays(30),
                        "Customer", employeeId
                );

                List<Project> projects = projectRepository.showProjectsByEmployeeId(employeeId);
                long projectId = projects.get(0).getProjectID();

                Project project = projectRepository.getProjectById(projectId);

                assertThat(project).isNotNull();
                assertThat(project.getProjectName()).isEqualTo("Test Project");
            }

            // ... (resten af Project tests)
        }

        @Nested
        @DisplayName("SubProject Repository Tests")
        class SubProjectRepositoryTests {

            @Test
            @DisplayName("Skal oprette subproject")
            void shouldCreateSubProject() {
                long projectId = createTestProject();

                SubProject subProject = new SubProject();
                subProject.setSubProjectName("Authentication Module");
                subProject.setSubProjectDescription("User login system");
                subProject.setSubProjectStartDate(LocalDate.of(2024, 2, 1));
                subProject.setSubProjectDeadline(LocalDate.of(2024, 3, 31));
                subProject.setSubProjectDuration(40);

                projectRepository.saveSubProject(subProject, projectId);

                List<SubProject> subProjects = projectRepository.showSubProjectsByProjectId(projectId);
                assertThat(subProjects).hasSize(1);
                assertThat(subProjects.get(0).getSubProjectName()).isEqualTo("Authentication Module");
            }

            // ... (resten af SubProject tests)
        }




    }


}

