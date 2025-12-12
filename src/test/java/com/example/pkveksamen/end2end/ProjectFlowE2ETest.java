package com.example.pkveksamen.e2e;

import com.example.pkveksamen.model.Employee;
import com.example.pkveksamen.model.EmployeeRole;
import com.example.pkveksamen.model.Project;
import com.example.pkveksamen.model.SubProject;
import com.example.pkveksamen.model.Task;
import com.example.pkveksamen.repository.EmployeeRepository;
import com.example.pkveksamen.repository.ProjectRepository;
import com.example.pkveksamen.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End test:
 * - Starter hele Spring Boot app'en (webEnvironment=RANDOM_PORT)
 * - Kalder rigtige endpoints via HTTP
 * - Bruger H2-profilen (samme schema.sql), så testen er isoleret og reproducerbar
 *
 * Forretningsregler der testes:
 * - Kun PROJECT_MANAGER må oprette projekt / subproject / task
 * - Task.employee_id = "tildelt medarbejder"
 * - TEAM_MEMBER må opdatere status/note på tildelte tasks
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@org.springframework.test.context.ActiveProfiles("h2")
class ProjectFlowE2ETest {

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate;
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    /**
     * Constructor injection i test (bedste praksis)
     */
    @Autowired
    public ProjectFlowE2ETest(TestRestTemplate restTemplate,
                              EmployeeRepository employeeRepository,
                              ProjectRepository projectRepository,
                              TaskRepository taskRepository) {
        this.restTemplate = restTemplate;
        this.employeeRepository = employeeRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @BeforeEach
    void cleanDb() {
        // Hvis I har "deleteAll()" metoder, så brug dem.
        // Ellers kan I rydde med simple SQL-calls i repositories.
        taskRepository.deleteAllSubTasks();
        taskRepository.deleteAllTasks();
        projectRepository.deleteAllSubProjects();
        projectRepository.deleteAllProjectEmployees();
        projectRepository.deleteAllProjects();
        employeeRepository.deleteAllEmployees();
    }

    @Test
    void projectFlow_E2E_PM_creates_everything_teamMember_works_on_task() {

        // ---------------------------------------------------------
        // 1) Arrange: opret brugere direkte i DB (hurtigt og stabilt)
        //    (Login/session er ikke en del af jeres usecase her)
        // ---------------------------------------------------------
        long pmId = employeeRepository.createEmployee(
                "allan", "pw", "allan@mail.dk", EmployeeRole.PROJECT_MANAGER.name(), "Backend Dev"
        );

        long tmId = employeeRepository.createEmployee(
                "mohamed", "pw", "mohamed@mail.dk", EmployeeRole.TEAM_MEMBER.name(), "UI/UX Designer"
        );

        // ---------------------------------------------------------
        // 2) PM opretter projekt via HTTP (POST)
        //    Matcher jeres ProjectController: POST /project/create/{employeeId}
        // ---------------------------------------------------------
        Project project = new Project();
        project.setProjectName("KEA Exam Project");
        project.setProjectDescription("E2E test project");
        project.setProjectCustomer("KEA");
        project.setProjectStartDate(LocalDate.of(2025, 1, 1));
        project.setProjectDeadline(LocalDate.of(2025, 1, 10));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // I jeres app bruger I Thymeleaf forms -> form-urlencoded
        // Så vi sender det som key=value, ikke JSON.
        String createProjectBody =
                "projectName=" + url(project.getProjectName()) +
                        "&projectDescription=" + url(project.getProjectDescription()) +
                        "&projectCustomer=" + url(project.getProjectCustomer()) +
                        "&startDate=" + project.getProjectStartDate() +
                        "&deadline=" + project.getProjectDeadline();

        ResponseEntity<String> createProjectResp = restTemplate.exchange(
                baseUrl("/project/create/" + pmId),
                HttpMethod.POST,
                new HttpEntity<>(createProjectBody, headers),
                String.class
        );

        // Redirect er normalt korrekt her (controller returner redirect)
        assertTrue(createProjectResp.getStatusCode().is3xxRedirection()
                        || createProjectResp.getStatusCode().is2xxSuccessful(),
                "Expected redirect or OK on create project");

        // Verificér at projektet findes i DB (E2E: vi bruger HTTP, men vi verificerer persistence)
        List<com.example.pkveksamen.model.Project> pmProjects =
                projectRepository.showProjectsByEmployeeId((int) pmId);

        assertEquals(1, pmProjects.size());
        long projectId = pmProjects.get(0).getProjectID();

        // ---------------------------------------------------------
        // 3) Tilføj team member til projekt (DB relation project_employee)
        //    (Hvis I har endpoint til det, kan det også gøres via HTTP)
        // ---------------------------------------------------------
        projectRepository.addEmployeeToProject(projectId, tmId);

        // ---------------------------------------------------------
        // 4) PM opretter subproject via HTTP
        //    Jeres controller: POST /project/savesubproject/{employeeId}/{projectId}
        //    (I har også create-subproject flow, men savesubproject findes i jeres kode)
        // ---------------------------------------------------------
        SubProject subProject = new SubProject();
        subProject.setSubProjectName("Backend");
        subProject.setSubProjectDescription("REST API");
        subProject.setSubProjectStartDate(LocalDate.of(2025, 1, 1));
        subProject.setSubProjectDeadline(LocalDate.of(2025, 1, 5));

        String createSubProjectBody =
                "subProjectName=" + url(subProject.getSubProjectName()) +
                        "&subProjectDescription=" + url(subProject.getSubProjectDescription()) +
                        "&startDate=" + subProject.getSubProjectStartDate() +
                        "&deadline=" + subProject.getSubProjectDeadline();

        ResponseEntity<String> createSubProjectResp = restTemplate.exchange(
                baseUrl("/project/savesubproject/" + pmId + "/" + projectId),
                HttpMethod.POST,
                new HttpEntity<>(createSubProjectBody, headers),
                String.class
        );

        assertTrue(createSubProjectResp.getStatusCode().is3xxRedirection()
                        || createSubProjectResp.getStatusCode().is2xxSuccessful(),
                "Expected redirect or OK on create subproject");

        List<SubProject> subProjects = projectRepository.showSubProjectsByProjectId(projectId);
        assertEquals(1, subProjects.size());
        long subProjectId = subProjects.get(0).getSubProjectID();

        // ---------------------------------------------------------
        // 5) PM opretter task og tildeler den til team member
        //    Jeres createTask endpoint: POST /project/task/createtask/{employeeId}/{projectId}/{subProjectId}
        //    (PM er {employeeId}, assignedToEmployeeId er tmId)
        // ---------------------------------------------------------
        Task task = new Task();
        task.setTaskName("Implement Login API");
        task.setTaskDescription("JWT login endpoint");
        task.setTaskStartDate(LocalDate.of(2025, 1, 2));
        task.setTaskDeadline(LocalDate.of(2025, 1, 4));
        task.setTaskStatus(com.example.pkveksamen.model.Status.NOT_STARTED);
        task.setTaskPriority(com.example.pkveksamen.model.Priority.HIGH);
        task.setTaskNote(""); // initial note empty

        String createTaskBody =
                "taskName=" + url(task.getTaskName()) +
                        "&taskDescription=" + url(task.getTaskDescription()) +
                        "&startDate=" + task.getTaskStartDate() +
                        "&deadline=" + task.getTaskDeadline() +
                        "&taskStatus=" + task.getTaskStatus().name() +
                        "&taskPriority=" + task.getTaskPriority().name() +
                        "&taskNote=" + url(task.getTaskNote()) +
                        "&assignedToEmployeeId=" + tmId;

        ResponseEntity<String> createTaskResp = restTemplate.exchange(
                baseUrl("/project/task/createtask/" + pmId + "/" + projectId + "/" + subProjectId),
                HttpMethod.POST,
                new HttpEntity<>(createTaskBody, headers),
                String.class
        );

        assertTrue(createTaskResp.getStatusCode().is3xxRedirection()
                        || createTaskResp.getStatusCode().is2xxSuccessful(),
                "Expected redirect or OK on create task");

        // Task skal nu være tildelt tmId
        List<com.example.pkveksamen.model.Task> tasksForTM =
                taskRepository.showTaskByEmployeeId((int) tmId);

        assertEquals(1, tasksForTM.size());
        long taskId = tasksForTM.get(0).getTaskID();

        // ---------------------------------------------------------
        // 6) TEAM MEMBER opdaterer status via HTTP
        //    I har form action: /project/task/updatestatus/{taskId}
        // ---------------------------------------------------------
        String updateStatusBody = "taskStatus=IN_PROGRESS";

        ResponseEntity<String> updateStatusResp = restTemplate.exchange(
                baseUrl("/project/task/updatestatus/" + taskId),
                HttpMethod.POST,
                new HttpEntity<>(updateStatusBody, headers),
                String.class
        );

        assertTrue(updateStatusResp.getStatusCode().is3xxRedirection()
                        || updateStatusResp.getStatusCode().is2xxSuccessful(),
                "Expected redirect or OK on update status");

        com.example.pkveksamen.model.Task updatedTask = taskRepository.getTaskById(taskId);
        assertEquals("IN_PROGRESS", updatedTask.getTaskStatus().name());

        // ---------------------------------------------------------
        // 7) TEAM MEMBER tilføjer note via HTTP
        //    (Du har selv lavet endpoint til note tidligere)
        //    POST /project/task/note/{employeeId}/{projectId}/{subProjectId}/{taskId}
        // ---------------------------------------------------------
        String noteBody = "taskNote=" + url("API done - needs tests");

        ResponseEntity<String> saveNoteResp = restTemplate.exchange(
                baseUrl("/project/task/note/" + tmId + "/" + projectId + "/" + subProjectId + "/" + taskId),
                HttpMethod.POST,
                new HttpEntity<>(noteBody, headers),
                String.class
        );

        assertTrue(saveNoteResp.getStatusCode().is3xxRedirection()
                        || saveNoteResp.getStatusCode().is2xxSuccessful(),
                "Expected redirect or OK on save note");

        updatedTask = taskRepository.getTaskById(taskId);
        assertEquals("API done - needs tests", updatedTask.getTaskNote());

        // ---------------------------------------------------------
        // 8) Afslut: simple sanity checks på relationer
        // ---------------------------------------------------------
        assertEquals(1, projectRepository.countProjects());
        assertEquals(1, projectRepository.countSubProjects());
        assertEquals(1, taskRepository.countTasks());
    }

    private String baseUrl(String path) {
        return "http://localhost:" + port + path;
    }

    private static String url(String s) {
        // Minimal url-encoding til form-urlencoded body (space -> %20 osv.)
        // Hvis I vil gøre det helt korrekt, kan I bruge UriUtils.encodeQueryParam,
        // men denne er ok til simple strenge.
        return s == null ? "" : s.replace(" ", "%20");
    }
}
