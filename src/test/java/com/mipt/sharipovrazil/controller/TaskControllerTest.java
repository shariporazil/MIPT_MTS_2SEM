package com.mipt.sharipovrazil.controller;

import com.mipt.sharipovrazil.model.Task;
import com.mipt.sharipovrazil.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TaskControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TaskRepository taskRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/tasks";
    }


    @Test
    void getAllTasks_ShouldReturnListOfTasks() {
        ResponseEntity<Task[]> response = restTemplate.getForEntity(baseUrl, Task[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void getTaskById_WithValidId_ShouldReturnTask() {
        Task newTask = new Task(null, "Test Task", "Description", false);
        ResponseEntity<Task> createResponse = restTemplate.postForEntity(baseUrl, newTask, Task.class);
        Long createdId = createResponse.getBody().getId();

        ResponseEntity<Task> response = restTemplate.getForEntity(baseUrl + "/" + createdId, Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Test Task");
    }

    @Test
    void createTask_WithValidData_ShouldCreateTask() {
        Task newTask = new Task(null, "New Task", "New Description", false);

        ResponseEntity<Task> response = restTemplate.postForEntity(baseUrl, newTask, Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("New Task");
    }

    @Test
    void updateTask_WithValidId_ShouldUpdateTask() {
        Task newTask = new Task(null, "Original Title", "Original Description", false);
        ResponseEntity<Task> createResponse = restTemplate.postForEntity(baseUrl, newTask, Task.class);
        Long taskId = createResponse.getBody().getId();

        Task updatedTask = new Task(taskId, "Updated Title", "Updated Description", true);
        HttpEntity<Task> requestEntity = new HttpEntity<>(updatedTask);
        ResponseEntity<Task> response = restTemplate.exchange(
                baseUrl + "/" + taskId,
                HttpMethod.PUT,
                requestEntity,
                Task.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTitle()).isEqualTo("Updated Title");
        assertThat(response.getBody().isCompleted()).isTrue();
    }

    @Test
    void deleteTask_WithValidId_ShouldDeleteTask() {
        Task newTask = new Task(null, "Task to Delete", "Will be deleted", false);
        ResponseEntity<Task> createResponse = restTemplate.postForEntity(baseUrl, newTask, Task.class);
        Long taskId = createResponse.getBody().getId();

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + taskId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }



    @Test
    void getTaskById_WithInvalidId_ShouldReturn404() {
        ResponseEntity<Map> response = restTemplate.getForEntity(baseUrl + "/99999", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createTask_WithNullBody_ShouldReturn400() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateTask_WithInvalidId_ShouldReturn404() {
        Task task = new Task(99999L, "Test", "Test", false);

        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/99999",
                HttpMethod.PUT,
                new HttpEntity<>(task),
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteTask_WithInvalidId_ShouldReturn404() {
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/99999",
                HttpMethod.DELETE,
                null,
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createTask_WithEmptyTitle_ShouldStillCreate() {
        Task taskWithEmptyTitle = new Task(null, "", "Description", false);

        ResponseEntity<Task> response = restTemplate.postForEntity(baseUrl, taskWithEmptyTitle, Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getTitle()).isEmpty();
    }

    @Test
    void updateTask_WithNullBody_ShouldReturn400() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/1",
                HttpMethod.PUT,
                requestEntity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}