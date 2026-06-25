package com.mipt.sharipovrazil.service;

import com.mipt.sharipovrazil.dto.TaskCreateDto;
import com.mipt.sharipovrazil.dto.TaskResponseDto;
import com.mipt.sharipovrazil.dto.TaskUpdateDto;
import com.mipt.sharipovrazil.exception.TaskIdNotFoundException;
import com.mipt.sharipovrazil.exception.TaskNotFoundException;
import com.mipt.sharipovrazil.mapper.TaskMapper;
import com.mipt.sharipovrazil.model.Task;
import com.mipt.sharipovrazil.repository.TaskRepository;
import com.mipt.sharipovrazil.scope.PrototypeScopedBean;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Сервис для управления задачами.
 *
 * <p>Содержит бизнес-логику приложения для работы с задачами.
 * Взаимодействует с репозиторием для доступа к данным и с prototype-бином для генерации
 * идентификаторов.</p>
 *
 * <p>Демонстрирует жизненный цикл бина через аннотации
 * {@link PostConstruct} и {@link PreDestroy}, а также внедрение зависимостей через
 * конструктор.</p>
 */
@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final PrototypeScopedBean prototypeScopedBean;
    private final TaskMapper taskMapper;
    private final Map<Long, Task> taskCache = new ConcurrentHashMap<>();

    @Autowired
    public TaskService(TaskRepository taskRepository, PrototypeScopedBean prototypeScopedBean,
            TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.prototypeScopedBean = prototypeScopedBean;
        this.taskMapper = taskMapper;
    }

    @PostConstruct
    public void init() {
        logger.info("Инициализация кэша задач");
        List<Task> allTasks = taskRepository.findAll();
        for (int i = 0; i < Math.min(5, allTasks.size()); i++) {
            Task task = allTasks.get(i);
            taskCache.put(task.getId(), task);
        }
        logger.info("Кэш инициализирован с {} задачами", taskCache.size());
    }

    @PreDestroy
    public void destroy() {
        logger.info("Очистка ресурсов. В кэше {} задач", taskCache.size());
        logger.info("Статистика сохранена в файл");
    }

    public List<TaskResponseDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(taskMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public TaskResponseDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
        return taskMapper.toResponseDto(task);
    }

    public Task getTaskByIdOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }

    @Transactional
    public TaskResponseDto createTask(TaskCreateDto createDto) {
        Task task = taskMapper.toEntity(createDto);
        Task savedTask = taskRepository.save(task);
        return taskMapper.toResponseDto(savedTask);
    }

    @Transactional
    public TaskResponseDto updateTask(Long id, TaskUpdateDto updateDto) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));

        taskMapper.updateEntity(updateDto, existingTask);
        Task updatedTask = taskRepository.save(existingTask);

        taskCache.put(id, updatedTask);

        return taskMapper.toResponseDto(updatedTask);
    }

    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
        taskCache.remove(id);
    }

    public Map<Long, Task> getTaskCache() {
        return taskCache;
    }

    public long getTotalCount() {
        return taskRepository.count();
    }

    public List<Task> getTasksDueWithin7Days() {
        LocalDate now = LocalDate.now();
        return taskRepository.findTasksDueBetween(now, now.plusDays(7));
    }

    public List<Task> getAllTasksWithAttachments() {
        return taskRepository.findAllWithAttachments();
    }

    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED,
            rollbackFor = TaskIdNotFoundException.class
    )
    public void bulkCompleteTasks(List<Long> ids) {
        for (Long taskId : ids) {
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new TaskIdNotFoundException(taskId));
            task.setCompleted(true);
        }
        logger.info("Bulk completed {} tasks", ids.size());
    }
}