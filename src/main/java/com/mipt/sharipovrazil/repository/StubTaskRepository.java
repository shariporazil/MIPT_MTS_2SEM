package com.mipt.sharipovrazil.repository;

import com.mipt.sharipovrazil.model.Task;
import java.util.*;
/**
 * Заглушечная реализация репозитория задач с фиксированными данными.
 *
 * <p>Эта реализация используется для демонстрационных целей и тестирования.
 * При создании инициализируется несколькими предопределенными задачами.</p>
 *
 * <p>В отличие от {@link InMemoryTaskRepository}, этот класс не помечен
 * аннотацией {@link org.springframework.stereotype.Repository},
 * а создается как бин через Java-конфигурацию для демонстрации
 * различных способов создания бинов в Spring.</p>
 */
public class StubTaskRepository implements TaskRepository {
    private final Map<Long, Task> tasks = new HashMap<>();
    private long currentId = 1;

    public StubTaskRepository() {
        save(new Task(null, "Изучить Spring", "Понять DI и AOP", false));
        save(new Task(null, "Написать код", "Сделать все эндпоинты", false));
        save(new Task(null, "Сделать тесты", "Покрыть код тестми", true));
        save(new Task(null, "Сдать домашку", "Получить 9 баллов", false));
    }

    @Override
    public Task save(Task task) {
        task.setId(currentId++);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task update(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void deleteById(Long id) {
        tasks.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return tasks.containsKey(id);
    }
}