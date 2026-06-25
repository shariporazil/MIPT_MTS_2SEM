package com.mipt.sharipovrazil.external;

import com.mipt.sharipovrazil.dto.task.TaskRequest;
import com.mipt.sharipovrazil.dto.task.TaskResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/external/v1")
public class ExternalApiController {

    private final Map<Long, TaskResponse> tasks = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong();

    @PostMapping("/tasks")
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest request) {
        Long id = idSequence.incrementAndGet();
        TaskResponse task = new TaskResponse(
                id,
                request.getTitle(),
                request.getDescription(),
                Boolean.TRUE.equals(request.getCompleted())
        );
        tasks.put(id, task);
        return ResponseEntity.created(URI.create("/external/v1/tasks/" + id)).body(task);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<?> getTask(@PathVariable Long id) {
        TaskResponse task = tasks.get(id);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "External task not found: " + id));
        }
        return ResponseEntity.ok(task);
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskResponse>> getTasks(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Integer limit) {
        var stream = tasks.values().stream()
                .sorted(Comparator.comparing(TaskResponse::getId));

        if (completed != null) {
            stream = stream.filter(task -> task.isCompleted() == completed);
        }
        if (limit != null && limit >= 0) {
            stream = stream.limit(limit);
        }
        return ResponseEntity.ok(stream.toList());
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<ProblemDetail> deleteTask(@PathVariable Long id) {
        if (tasks.remove(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "External task not found: " + id));
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unstable")
    public ResponseEntity<?> unstable(@RequestParam(defaultValue = "500") String mode) throws InterruptedException {
        return switch (mode) {
            case "timeout" -> {
                Thread.sleep(2000);
                yield ResponseEntity.ok(Map.of("message", "slow response"));
            }
            case "429" -> ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header(HttpHeaders.RETRY_AFTER, "2")
                    .body(ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, "Too many requests"));
            case "html" -> ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .contentType(MediaType.TEXT_HTML)
                    .body("<html><body>External gateway error</body></html>");
            case "500" -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "External server error"));
            default -> ResponseEntity.ok(Map.of("message", "stable response"));
        };
    }
}