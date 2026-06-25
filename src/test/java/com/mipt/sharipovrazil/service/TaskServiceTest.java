package com.mipt.sharipovrazil.service;

import com.mipt.sharipovrazil.dto.TaskCreateDto;
import com.mipt.sharipovrazil.dto.TaskResponseDto;
import com.mipt.sharipovrazil.dto.TaskUpdateDto;
import com.mipt.sharipovrazil.exception.TaskNotFoundException;
import com.mipt.sharipovrazil.mapper.TaskMapper;
import com.mipt.sharipovrazil.model.Priority;
import com.mipt.sharipovrazil.model.Task;
import com.mipt.sharipovrazil.repository.TaskRepository;
import com.mipt.sharipovrazil.scope.PrototypeScopedBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private PrototypeScopedBean prototypeScopedBean;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskCreateDto createDto;
    private TaskUpdateDto updateDto;
    private TaskResponseDto responseDto;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setCompleted(false);
        task.setDueDate(LocalDate.of(2025, 12, 31));
        task.setPriority(Priority.HIGH);

        createDto = new TaskCreateDto();
        createDto.setTitle("Test Task");
        createDto.setDescription("Test Description");
        createDto.setDueDate(LocalDate.of(2025, 12, 31));
        createDto.setPriority(Priority.HIGH);

        updateDto = new TaskUpdateDto();
        updateDto.setTitle("Updated Task");
        updateDto.setCompleted(true);

        responseDto = new TaskResponseDto();
        responseDto.setId(1L);
        responseDto.setTitle("Test Task");
        responseDto.setDescription("Test Description");
    }

    @Test
    void getAllTasks_ShouldReturnListOfTaskResponseDto() {
        when(taskRepository.findAll()).thenReturn(List.of(task));
        when(taskMapper.toResponseDto(task)).thenReturn(responseDto);

        List<TaskResponseDto> result = taskService.getAllTasks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(taskRepository).findAll();
    }

    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTaskResponseDto() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toResponseDto(task)).thenReturn(responseDto);

        TaskResponseDto result = taskService.getTaskById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(taskRepository).findById(1L);
    }

    @Test
    void getTaskById_WhenTaskDoesNotExist_ShouldThrowTaskNotFoundException() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(999L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Task not found with id: 999");
    }

    @Test
    void createTask_ShouldSaveAndReturnTask() {
        when(taskMapper.toEntity(createDto)).thenReturn(task);

        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toResponseDto(task)).thenReturn(responseDto);

        TaskResponseDto result = taskService.createTask(createDto);

        assertThat(result).isNotNull();
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTask_WhenTaskExists_ShouldUpdateAndReturnTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toResponseDto(task)).thenReturn(responseDto);

        TaskResponseDto result = taskService.updateTask(1L, updateDto);

        assertThat(result).isNotNull();
        verify(taskMapper).updateEntity(updateDto, task);
        verify(taskRepository).save(task);
    }

    @Test
    void updateTask_WhenTaskDoesNotExist_ShouldThrowTaskNotFoundException() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(999L, updateDto))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void deleteTask_WhenTaskExists_ShouldDelete() {
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        taskService.deleteTask(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    void deleteTask_WhenTaskDoesNotExist_ShouldThrowTaskNotFoundException() {
        when(taskRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> taskService.deleteTask(999L))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void getTotalCount_ShouldReturnNumberOfTasks() {
        when(taskRepository.count()).thenReturn(2L);

        long count = taskService.getTotalCount();

        assertThat(count).isEqualTo(2L);
    }
}