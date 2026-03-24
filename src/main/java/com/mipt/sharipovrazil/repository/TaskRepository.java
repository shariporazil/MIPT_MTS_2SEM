package com.mipt.sharipovrazil.repository;

import com.mipt.sharipovrazil.model.Task;
import java.util.List;
import java.util.Optional;
/**
 * Интерфейс репозитория для управления задачами.
 *
 * <p>Определяет контракт для CRUD операций с задачами.
 * Реализации этого интерфейса должны обеспечивать
 * сохранение, поиск, обновление и удаление задач.</p>
 *
 * <p>Интерфейс следует паттерну Repository, абстрагируя
 * слой доступа к данным от бизнес-логики.</p>
 */

public interface TaskRepository {
    Task save(Task task);
    Optional<Task> findById(Long id);
    List<Task> findAll();
    Task update(Task task);
    void deleteById(Long id);
    boolean existsById(Long id);
}