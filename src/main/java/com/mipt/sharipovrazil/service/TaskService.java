package com.mipt.sharipovrazil.service;

import com.mipt.sharipovrazil.model.Task;
import com.mipt.sharipovrazil.repository.TaskRepository;
import com.mipt.sharipovrazil.scope.PrototypeScopedBean;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Сервис для управления задачами.
 *
 * <p>Содержит бизнес-логику приложения для работы с задачами.
 * Взаимодействует с репозиторием для доступа к данным и
 * с prototype-бином для генерации идентификаторов.</p>
 *
 * <p>Демонстрирует жизненный цикл бина через аннотации
 * {@link PostConstruct} и {@link PreDestroy}, а также
 * внедрение зависимостей через конструктор.</p>
 */

@Service
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final PrototypeScopedBean prototypeScopedBean;
    private final Map<Long, Task> taskCache = new ConcurrentHashMap<>();

    @Autowired
    public TaskService(TaskRepository taskRepository, PrototypeScopedBean prototypeScopedBean) {
        this.taskRepository = taskRepository;
        this.prototypeScopedBean = prototypeScopedBean;
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

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task not found with id: " + id));
    }

    public Task createTask(Task task) {
        if (task == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task cannot be null");
        }
        task.setId(prototypeScopedBean.generateId());
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task task) {
        if (task == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task cannot be null");
        }
        if (!taskRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: " + id);
        }
        task.setId(id);
        return taskRepository.update(task);
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    public Map<Long, Task> getTaskCache() {
        return taskCache;
    }
}