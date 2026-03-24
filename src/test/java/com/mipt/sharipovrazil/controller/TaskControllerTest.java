package com.mipt.sharipovrazil.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mipt.sharipovrazil.dto.TaskCreateDto;
import com.mipt.sharipovrazil.dto.TaskResponseDto;
import com.mipt.sharipovrazil.dto.TaskUpdateDto;
import com.mipt.sharipovrazil.exception.TaskNotFoundException;
import com.mipt.sharipovrazil.model.Priority;
import com.mipt.sharipovrazil.scope.RequestScopedBean;
import com.mipt.sharipovrazil.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private RequestScopedBean requestScopedBean;

    private TaskResponseDto responseDto;
    private TaskCreateDto createDto;
    private TaskUpdateDto updateDto;

    @BeforeEach
    void setUp() {
        responseDto = new TaskResponseDto();
        responseDto.setId(1L);
        responseDto.setTitle("Test Task");
        responseDto.setDescription("Test Description");
        responseDto.setCompleted(false);
        responseDto.setCreatedAt(LocalDateTime.now());
        responseDto.setDueDate(LocalDate.of(2025, 12, 31));
        responseDto.setPriority(Priority.HIGH);
        responseDto.setTags(Set.of("test"));

        createDto = new TaskCreateDto();
        createDto.setTitle("Test Task");
        createDto.setDescription("Test Description");
        createDto.setDueDate(LocalDate.of(2025, 12, 31));
        createDto.setPriority(Priority.HIGH);
        createDto.setTags(Set.of("test"));

        updateDto = new TaskUpdateDto();
        updateDto.setTitle("Updated Task");
        updateDto.setCompleted(true);
    }

    @Test
    void getAllTasks_ShouldReturnListOfTasks() throws Exception {
        // given
        when(taskService.getAllTasks()).thenReturn(List.of(responseDto));
        when(taskService.getTotalCount()).thenReturn(1L);

        // when & then
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(header().string("X-API-Version", "2.0.0"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Test Task"));
    }

    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTask() throws Exception {
        // given
        when(taskService.getTaskById(1L)).thenReturn(responseDto);

        // when & then
        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void getTaskById_WhenTaskDoesNotExist_ShouldReturn404() throws Exception {
        // given
        when(taskService.getTaskById(999L)).thenThrow(new TaskNotFoundException("Task not found"));

        // when & then
        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void createTask_WithValidData_ShouldReturnCreatedTask() throws Exception {
        when(taskService.createTask(any(TaskCreateDto.class))).thenReturn(responseDto);

        createDto.setDueDate(LocalDate.now().plusDays(1));

        String requestBody = objectMapper.writeValueAsString(createDto);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void createTask_WithTitleTooShort_ShouldReturn400() throws Exception {
        // given
        createDto.setTitle("ab");

        // when & then
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTask_WithBlankTitle_ShouldReturn400() throws Exception {
        // given
        createDto.setTitle("");

        // when & then
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTask_WithNullPriority_ShouldReturn400() throws Exception {
        // given
        createDto.setPriority(null);

        // when & then
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTask_WithPastDueDate_ShouldReturn400() throws Exception {
        // given
        createDto.setDueDate(LocalDate.of(2020, 1, 1));

        // when & then
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTask_WithValidData_ShouldReturnUpdatedTask() throws Exception {
        // given
        when(taskService.updateTask(eq(1L), any(TaskUpdateDto.class))).thenReturn(responseDto);

        // when & then
        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void deleteTask_WhenTaskExists_ShouldReturn204() throws Exception {
        // given
        doNothing().when(taskService).deleteTask(1L);

        // when & then
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }
}