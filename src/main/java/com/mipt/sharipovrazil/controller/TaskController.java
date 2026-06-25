package com.mipt.sharipovrazil.controller;

import com.mipt.sharipovrazil.dto.TaskCreateDto;
import com.mipt.sharipovrazil.dto.TaskResponseDto;
import com.mipt.sharipovrazil.dto.TaskUpdateDto;
import com.mipt.sharipovrazil.dto.validation.OnCreate;
import com.mipt.sharipovrazil.dto.validation.OnUpdate;
import com.mipt.sharipovrazil.service.TaskService;
import com.mipt.sharipovrazil.scope.RequestScopedBean;
import com.mipt.sharipovrazil.model.Task;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Управление задачами")
public class TaskController {

    private final TaskService taskService;
    private final RequestScopedBean requestScopedBean;

    @Value("${app.api.version:2.0.0}")
    private String apiVersion;

    public TaskController(TaskService taskService, RequestScopedBean requestScopedBean) {
        this.taskService = taskService;
        this.requestScopedBean = requestScopedBean;
    }

    private <T> ResponseEntity<T> addCommonHeaders(ResponseEntity<T> response) {
        return ResponseEntity
                .status(response.getStatusCode())
                .headers(response.getHeaders())
                .header("X-API-Version", apiVersion)
                .body(response.getBody());
    }

    @Operation(summary = "Получить все задачи", description = "Возвращает список всех задач")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
        System.out.println("Request ID: " + requestScopedBean.getRequestId());
        System.out.println("Время начала: " + requestScopedBean.getFormattedStartTime());

        List<TaskResponseDto> tasks = taskService.getAllTasks();
        long totalCount = taskService.getTotalCount();

        return addCommonHeaders(ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(totalCount))
                .body(tasks));
    }

    @Operation(summary = "Получить задачу по ID")
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(
            @PathVariable @Parameter(description = "ID задачи") Long id) {
        TaskResponseDto task = taskService.getTaskById(id);
        return addCommonHeaders(ResponseEntity.ok(task));
    }

    @Operation(summary = "Создать новую задачу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Задача успешно создана"),
            @ApiResponse(responseCode = "400", description = "Неверные входные данные")
    })
    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(
            @Validated(OnCreate.class) @Valid @RequestBody TaskCreateDto createDto) {
        TaskResponseDto createdTask = taskService.createTask(createDto);
        return addCommonHeaders(ResponseEntity.status(HttpStatus.CREATED).body(createdTask));
    }

    @Operation(summary = "Обновить задачу")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable Long id,
            @Validated(OnUpdate.class) @Valid @RequestBody TaskUpdateDto updateDto) {
        TaskResponseDto updatedTask = taskService.updateTask(id, updateDto);
        return addCommonHeaders(ResponseEntity.ok(updatedTask));
    }

    @Operation(summary = "Удалить задачу")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return addCommonHeaders(ResponseEntity.noContent().build());
    }

    @Operation(summary = "Получить кэш задач", hidden = true)
    @GetMapping("/cache")
    public ResponseEntity<Map<Long, Task>> getCache() {
        return ResponseEntity.ok(taskService.getTaskCache());
    }
}