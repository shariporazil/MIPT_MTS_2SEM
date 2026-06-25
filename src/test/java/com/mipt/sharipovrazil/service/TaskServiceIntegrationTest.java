package com.mipt.sharipovrazil.service;

import com.mipt.sharipovrazil.exception.TaskIdNotFoundException;
import com.mipt.sharipovrazil.model.Priority;
import com.mipt.sharipovrazil.model.Task;
import com.mipt.sharipovrazil.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TaskServiceIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void bulkCompleteTasks_ShouldRollbackWhenMissingIdPresent() {
        Task first = new Task("First", "Desc");
        first.setPriority(Priority.LOW);
        Task second = new Task("Second", "Desc");
        second.setPriority(Priority.MEDIUM);

        Task savedFirst = taskRepository.save(first);
        Task savedSecond = taskRepository.save(second);

        TaskIdNotFoundException exception = assertThrows(
                TaskIdNotFoundException.class,
                () -> taskService.bulkCompleteTasks(List.of(savedFirst.getId(), 999999L, savedSecond.getId()))
        );

        assertEquals(999999L, exception.getMissingId());

        Task afterFirst = taskRepository.findById(savedFirst.getId()).orElseThrow();
        Task afterSecond = taskRepository.findById(savedSecond.getId()).orElseThrow();
        assertFalse(afterFirst.isCompleted());
        assertFalse(afterSecond.isCompleted());
    }

    @Test
    void bulkCompleteTasks_ShouldCompleteAllWhenAllExist() {
        Task first = new Task("First", "Desc");
        first.setPriority(Priority.LOW);
        Task second = new Task("Second", "Desc");
        second.setPriority(Priority.MEDIUM);

        Task savedFirst = taskRepository.save(first);
        Task savedSecond = taskRepository.save(second);

        taskService.bulkCompleteTasks(List.of(savedFirst.getId(), savedSecond.getId()));

        Task afterFirst = taskRepository.findById(savedFirst.getId()).orElseThrow();
        Task afterSecond = taskRepository.findById(savedSecond.getId()).orElseThrow();
        assertTrue(afterFirst.isCompleted());
        assertTrue(afterSecond.isCompleted());
    }
}