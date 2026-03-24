package com.mipt.sharipovrazil.mapper;

import com.mipt.sharipovrazil.dto.TaskCreateDto;
import com.mipt.sharipovrazil.dto.TaskResponseDto;
import com.mipt.sharipovrazil.dto.TaskUpdateDto;
import com.mipt.sharipovrazil.model.Priority;
import com.mipt.sharipovrazil.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TaskMapperTest {

    private TaskMapper taskMapper;

    @BeforeEach
    void setUp() {
        taskMapper = Mappers.getMapper(TaskMapper.class);
    }

    @Test
    void toEntity_ShouldMapTaskCreateDtoToTask() {
        // given
        TaskCreateDto createDto = new TaskCreateDto();
        createDto.setTitle("Test Task");
        createDto.setDescription("Test Description");
        createDto.setDueDate(LocalDate.of(2025, 12, 31));
        createDto.setPriority(Priority.HIGH);
        createDto.setTags(Set.of("tag1", "tag2"));

        // when
        Task task = taskMapper.toEntity(createDto);

        // then
        assertThat(task).isNotNull();
        assertThat(task.getId()).isNull();
        assertThat(task.getTitle()).isEqualTo("Test Task");
        assertThat(task.getDescription()).isEqualTo("Test Description");
        assertThat(task.getDueDate()).isEqualTo(LocalDate.of(2025, 12, 31));
        assertThat(task.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(task.getTags()).containsExactlyInAnyOrder("tag1", "tag2");
        assertThat(task.isCompleted()).isFalse();
        assertThat(task.getCreatedAt()).isNotNull();
    }

    @Test
    void updateEntity_ShouldUpdateOnlyNonNullFields() {
        // given
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setCompleted(false);
        existingTask.setDueDate(LocalDate.of(2024, 1, 1));
        existingTask.setPriority(Priority.LOW);
        existingTask.setTags(Set.of("old"));

        TaskUpdateDto updateDto = new TaskUpdateDto();
        updateDto.setTitle("New Title");
        updateDto.setPriority(Priority.MEDIUM);
        // description, completed, dueDate, tags not set

        // when
        taskMapper.updateEntity(updateDto, existingTask);

        // then
        assertThat(existingTask.getTitle()).isEqualTo("New Title");
        assertThat(existingTask.getDescription()).isEqualTo("Old Description");
        assertThat(existingTask.isCompleted()).isFalse();
        assertThat(existingTask.getDueDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(existingTask.getPriority()).isEqualTo(Priority.MEDIUM);
        assertThat(existingTask.getTags()).containsExactly("old");
    }

    @Test
    void toResponseDto_ShouldMapTaskToTaskResponseDto() {
        // given
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setCompleted(true);
        task.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));
        task.setDueDate(LocalDate.of(2025, 12, 31));
        task.setPriority(Priority.HIGH);
        task.setTags(Set.of("tag1", "tag2"));

        // when
        TaskResponseDto responseDto = taskMapper.toResponseDto(task);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getId()).isEqualTo(1L);
        assertThat(responseDto.getTitle()).isEqualTo("Test Task");
        assertThat(responseDto.getDescription()).isEqualTo("Test Description");
        assertThat(responseDto.isCompleted()).isTrue();
        assertThat(responseDto.getCreatedAt()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(responseDto.getDueDate()).isEqualTo(LocalDate.of(2025, 12, 31));
        assertThat(responseDto.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(responseDto.getTags()).containsExactlyInAnyOrder("tag1", "tag2");
    }
}