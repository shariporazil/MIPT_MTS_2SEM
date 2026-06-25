package com.mipt.sharipovrazil.api;

import com.mipt.sharipovrazil.dto.task.TaskRequest;
import com.mipt.sharipovrazil.dto.task.TaskResponse;
import com.mipt.sharipovrazil.service.TaskGatewayService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskGatewayController {

    private final TaskGatewayService taskGatewayService;

    public TaskGatewayController(TaskGatewayService taskGatewayService) {
        this.taskGatewayService = taskGatewayService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskGatewayService.createTask(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskGatewayService.getTask(id));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(taskGatewayService.getTasks(completed, limit));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskGatewayService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unstable")
    public ResponseEntity<String> callUnstable(@RequestParam(defaultValue = "500") String mode) {
        return ResponseEntity.ok(taskGatewayService.callUnstable(mode));
    }
}