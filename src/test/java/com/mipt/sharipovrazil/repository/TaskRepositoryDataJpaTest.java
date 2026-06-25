package com.mipt.sharipovrazil.repository;

import com.mipt.sharipovrazil.model.Priority;
import com.mipt.sharipovrazil.model.Task;
import com.mipt.sharipovrazil.model.TaskAttachment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryDataJpaTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Test
    void findByCompletedAndPriority_ShouldReturnFilteredTasks() {
        Task matching = new Task("A", "Desc");
        matching.setPriority(Priority.LOW);
        matching.setCompleted(true);

        Task notMatching = new Task("B", "Desc");
        notMatching.setPriority(Priority.MEDIUM);
        notMatching.setCompleted(true);

        taskRepository.save(matching);
        taskRepository.save(notMatching);

        List<Task> result = taskRepository.findByCompletedAndPriority(true, Priority.LOW);

        assertEquals(1, result.size());
        assertEquals("A", result.get(0).getTitle());
    }

    @Test
    void findTasksDueBetween_ShouldReturnTasksForNext7Days() {
        Task dueSoon = new Task("Soon", "Desc");
        dueSoon.setPriority(Priority.MEDIUM);
        dueSoon.setDueDate(LocalDate.now().plusDays(3));

        Task dueLate = new Task("Late", "Desc");
        dueLate.setPriority(Priority.HIGH);
        dueLate.setDueDate(LocalDate.now().plusDays(20));

        taskRepository.save(dueSoon);
        taskRepository.save(dueLate);

        List<Task> result = taskRepository.findTasksDueBetween(
                LocalDate.now(),
                LocalDate.now().plusDays(7)
        );

        assertEquals(1, result.size());
        assertEquals("Soon", result.get(0).getTitle());
    }

    @Test
    void saveTaskWithAttachment_ShouldWorkCorrectly() {
        Task task = new Task("Task", "Desc");
        task.setPriority(Priority.LOW);
        Task savedTask = taskRepository.save(task);

        TaskAttachment attachment = new TaskAttachment();
        attachment.setTask(savedTask);
        attachment.setFileName("file.txt");
        attachment.setStoredFileName("stored-file.txt");
        attachment.setContentType("text/plain");
        attachment.setSize(10L);

        attachmentRepository.save(attachment);

        List<TaskAttachment> attachments = attachmentRepository.findByTask_Id(savedTask.getId());
        assertEquals(1, attachments.size());
        assertTrue(taskRepository.findById(savedTask.getId()).isPresent());
    }

    @Test
    void deleteTask_ShouldCascadeDeleteAttachments() {
        Task task = new Task("Task", "Desc");
        task.setPriority(Priority.LOW);
        Task savedTask = taskRepository.save(task);

        TaskAttachment attachment = new TaskAttachment();
        attachment.setTask(savedTask);
        attachment.setFileName("file.txt");
        attachment.setStoredFileName("stored-file.txt");
        attachment.setContentType("text/plain");
        attachment.setSize(10L);

        savedTask.getAttachments().add(attachment);

        taskRepository.save(savedTask);

        taskRepository.deleteById(savedTask.getId());

        List<TaskAttachment> attachments = attachmentRepository.findByTask_Id(savedTask.getId());
        assertTrue(attachments.isEmpty());
    }
}