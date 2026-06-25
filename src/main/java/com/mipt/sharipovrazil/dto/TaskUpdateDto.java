package com.mipt.sharipovrazil.dto;

import com.mipt.sharipovrazil.dto.validation.DueDateNotBeforeCreation;
import com.mipt.sharipovrazil.dto.validation.OnUpdate;
import com.mipt.sharipovrazil.model.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Schema(description = "DTO для обновления задачи")
public class TaskUpdateDto {

    @Schema(description = "Заголовок задачи", example = "Изучить Spring Boot")
    @Size(min = 3, max = 100, groups = OnUpdate.class)
    private String title;

    @Schema(description = "Описание задачи")
    @Size(max = 500)
    private String description;

    @Schema(description = "Статус выполнения")
    private Boolean completed;

    @Schema(description = "Срок выполнения")
    @DueDateNotBeforeCreation(groups = OnUpdate.class)
    private LocalDate dueDate;

    @Schema(description = "Приоритет")
    private Priority priority;

    @Schema(description = "Теги задачи")
    @Size(max = 5)
    private Set<String> tags;

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}