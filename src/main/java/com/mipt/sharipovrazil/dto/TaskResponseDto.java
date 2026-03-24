package com.mipt.sharipovrazil.dto;

import com.mipt.sharipovrazil.model.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Set;

@Schema(description = "DTO для ответа с данными задачи")
public class TaskResponseDto {

    @Schema(description = "ID задачи", example = "1")
    private Long id;

    @Schema(description = "Заголовок задачи", example = "Изучить Spring")
    private String title;

    @Schema(description = "Описание задачи")
    private String description;

    @Schema(description = "Статус выполнения")
    private boolean completed;

    @Schema(description = "Дата создания")
    private LocalDateTime createdAt;

    @Schema(description = "Срок выполнения")
    private LocalDate dueDate;

    @Schema(description = "Приоритет")
    private Priority priority;

    @Schema(description = "Теги задачи")
    private Set<String> tags;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }
}