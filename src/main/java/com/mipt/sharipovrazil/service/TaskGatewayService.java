package com.mipt.sharipovrazil.service;

import com.mipt.sharipovrazil.client.ExternalTaskClient;
import com.mipt.sharipovrazil.dto.task.TaskRequest;
import com.mipt.sharipovrazil.dto.task.TaskResponse;
import com.mipt.sharipovrazil.exception.ExternalApiException;
import com.mipt.sharipovrazil.exception.ExternalTaskNotFoundException;
import com.mipt.sharipovrazil.exception.RateLimitException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskGatewayService {

    private final ExternalTaskClient externalTaskClient;

    public TaskGatewayService(ExternalTaskClient externalTaskClient) {
        this.externalTaskClient = externalTaskClient;
    }

    @RateLimiter(name = "externalService", fallbackMethod = "createTaskRateLimitFallback")
    @CircuitBreaker(name = "externalService", fallbackMethod = "createTaskFallback")
    public TaskResponse createTask(TaskRequest request) {
        return externalTaskClient.createTask(request);
    }

    @RateLimiter(name = "externalService", fallbackMethod = "getTaskRateLimitFallback")
    @CircuitBreaker(name = "externalService", fallbackMethod = "getTaskFallback")
    public TaskResponse getTask(Long id) {
        return externalTaskClient.getTask(id);
    }

    @RateLimiter(name = "externalService", fallbackMethod = "getTasksRateLimitFallback")
    @CircuitBreaker(name = "externalService", fallbackMethod = "getTasksFallback")
    public List<TaskResponse> getTasks(Boolean completed, Integer limit) {
        return externalTaskClient.getTasks(completed, limit);
    }

    @RateLimiter(name = "externalService", fallbackMethod = "deleteTaskRateLimitFallback")
    @CircuitBreaker(name = "externalService", fallbackMethod = "deleteTaskFallback")
    public void deleteTask(Long id) {
        externalTaskClient.deleteTask(id);
    }

    @RateLimiter(name = "externalService", fallbackMethod = "callUnstableRateLimitFallback")
    @CircuitBreaker(name = "externalService", fallbackMethod = "callUnstableFallback")
    public String callUnstable(String mode) {
        return externalTaskClient.callUnstable(mode);
    }

    // === CircuitBreaker Fallbacks ===

    public TaskResponse createTaskFallback(TaskRequest request, Throwable throwable) {
        rethrowControlledException(throwable);
        return new TaskResponse(-1L, "Fallback task", "External API is temporarily unavailable", false);
    }

    public TaskResponse getTaskFallback(Long id, Throwable throwable) {
        rethrowControlledException(throwable);
        return new TaskResponse(id, "Fallback task", "External API is temporarily unavailable", false);
    }

    public List<TaskResponse> getTasksFallback(Boolean completed, Integer limit, Throwable throwable) {
        rethrowControlledException(throwable);
        return List.of();
    }

    public void deleteTaskFallback(Long id, Throwable throwable) {
        rethrowControlledException(throwable);
        throw new ExternalApiException("External API delete is temporarily unavailable", throwable);
    }

    public String callUnstableFallback(String mode, Throwable throwable) {
        if (throwable instanceof RateLimitException) {
            throw (RateLimitException) throwable;
        }
        return "Graceful degradation: external API is temporarily unavailable";
    }

    // === RateLimiter Fallbacks ===

    public TaskResponse createTaskRateLimitFallback(TaskRequest request, RequestNotPermitted exception) {
        throw new RateLimitException("Gateway rate limit exceeded");
    }

    public TaskResponse getTaskRateLimitFallback(Long id, RequestNotPermitted exception) {
        throw new RateLimitException("Gateway rate limit exceeded");
    }

    public List<TaskResponse> getTasksRateLimitFallback(Boolean completed, Integer limit, RequestNotPermitted exception) {
        throw new RateLimitException("Gateway rate limit exceeded");
    }

    public void deleteTaskRateLimitFallback(Long id, RequestNotPermitted exception) {
        throw new RateLimitException("Gateway rate limit exceeded");
    }

    public String callUnstableRateLimitFallback(String mode, RequestNotPermitted exception) {
        throw new RateLimitException("Gateway rate limit exceeded");
    }

    private void rethrowControlledException(Throwable throwable) {
        if (throwable instanceof ExternalTaskNotFoundException) {
            throw (ExternalTaskNotFoundException) throwable;
        }
        if (throwable instanceof RateLimitException) {
            throw (RateLimitException) throwable;
        }
        if (throwable instanceof RequestNotPermitted) {
            throw new RateLimitException("Gateway rate limit exceeded");
        }
    }
}