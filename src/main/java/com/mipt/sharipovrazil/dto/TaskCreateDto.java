package com.mipt.sharipovrazil.dto;

import com.mipt.sharipovrazil.dto.validation.OnCreate;
import com.mipt.sharipovrazil.model.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Schema(description = "DTO для создания задачи")
public class TaskCreateDto {

    @Schema(description = "Заголовок задачи", example = "Изучить Spring")
    @NotBlank(groups = OnCreate.class)
    @Size(min = 3, max = 100, groups = OnCreate.class)
    private String title;

    @Schema(description = "Описание задачи", example = "Понять DI и AOP")
    @Size(max = 500)
    private String description;

    @Schema(description = "Срок выполнения", example = "2025-12-31")
    @FutureOrPresent(message = "Due date must be in the present or future", groups = OnCreate.class)
    private LocalDate dueDate;  // может быть null

    @Schema(description = "Приоритет", example = "HIGH")
    @NotNull(groups = OnCreate.class)
    private Priority priority;

    @Schema(description = "Теги задачи", example = "[\"учеба\", \"spring\"]")
    @Size(max = 5)
    private Set<String> tags;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }
}