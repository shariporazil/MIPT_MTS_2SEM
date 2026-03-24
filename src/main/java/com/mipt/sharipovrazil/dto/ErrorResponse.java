package com.mipt.sharipovrazil.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Map;

@Schema(description = "Стандартный формат ошибки")
public class ErrorResponse {

    @Schema(description = "Время возникновения ошибки")
    private Instant timestamp;

    @Schema(description = "HTTP статус код")
    private int status;

    @Schema(description = "Краткое описание ошибки")
    private String error;

    @Schema(description = "Детальное сообщение для клиента")
    private String message;

    @Schema(description = "Путь запроса")
    private String path;

    @Schema(description = "Дополнительные детали")
    private Map<String, Object> details;

    public ErrorResponse() {
        this.timestamp = Instant.now();
    }

    public ErrorResponse(int status, String error, String message, String path) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    // Getters and Setters
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
}