package com.mipt.sharipovrazil.repository;

import com.mipt.sharipovrazil.model.Priority;
import com.mipt.sharipovrazil.model.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

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
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCompletedAndPriority(boolean completed, Priority priority);

    @Query("""
            SELECT t
            FROM Task t
            WHERE t.dueDate IS NOT NULL
              AND t.dueDate BETWEEN :startDate AND :endDate
            """)
    List<Task> findTasksDueBetween(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @EntityGraph(attributePaths = "attachments")
    @Query("SELECT t FROM Task t")
    List<Task> findAllWithAttachments();
}