package com.mipt.sharipovrazil.repository;

import com.mipt.sharipovrazil.model.Task;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
/**
 * Реализация репозитория задач в оперативной памяти.
 *
 * <p>Это основной репозиторий приложения, помеченный аннотацией {@link Primary}.
 * Хранит задачи в потокобезопасной коллекции {@link ConcurrentHashMap}
 * и генерирует идентификаторы с помощью {@link AtomicLong}.</p>
 *
 * <p>Аннотация {@link Repository} указывает Spring, что этот класс является
 * компонентом репозитория и должен быть автоматически обнаружен при сканировании.</p>
 *
 * <p>Аннотация {@link Primary} гарантирует, что если есть несколько бинов
 * типа {@link TaskRepository}, этот бин будет выбран по умолчанию
 * при автоматическом внедрении.</p>
 */
@Repository
@Primary
public class InMemoryTaskRepository implements TaskRepository {
    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Task save(Task task) {
        long id = idGenerator.getAndIncrement();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    @Override
    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public List<Task> findAll() {
        return List.copyOf(tasks.values());
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