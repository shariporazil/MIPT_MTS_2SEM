package com.mipt.sharipovrazil.controller;

import com.mipt.sharipovrazil.model.Task;
import com.mipt.sharipovrazil.service.TaskService;
import com.mipt.sharipovrazil.service.TaskStatisticsService;
import com.mipt.sharipovrazil.scope.RequestScopedBean;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskStatisticsService statisticsService;
    private final RequestScopedBean requestScopedBean;

    @Autowired
    public TaskController(TaskService taskService,
            TaskStatisticsService statisticsService,
            RequestScopedBean requestScopedBean) {
        this.taskService = taskService;
        this.statisticsService = statisticsService;
        this.requestScopedBean = requestScopedBean;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        System.out.println("Request ID: " + requestScopedBean.getRequestId());
        System.out.println("Время начала: " + requestScopedBean.getFormattedStartTime());

        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        Task updatedTask = taskService.updateTask(id, task);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistics")
    public ResponseEntity<String> getStatistics() {
        statisticsService.compareRepositories();
        return ResponseEntity.ok("Статистика выведена в лог");
    }

    @GetMapping("/cache")
    public ResponseEntity<Map<Long, Task>> getCache() {
        return ResponseEntity.ok(taskService.getTaskCache());
    }
}