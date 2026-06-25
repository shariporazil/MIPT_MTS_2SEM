package com.mipt.sharipovrazil.service;

import com.mipt.sharipovrazil.exception.TaskNotFoundException;
import com.mipt.sharipovrazil.model.Priority;
import com.mipt.sharipovrazil.model.Task;
import com.mipt.sharipovrazil.model.TaskAttachment;
import com.mipt.sharipovrazil.repository.AttachmentRepository;
import com.mipt.sharipovrazil.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AttachmentServiceTest {

    @TempDir
    Path tempDir;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task("Test Task", "Description");
        task.setPriority(Priority.HIGH);
        task = taskRepository.save(task);
    }

    @Test
    void storeAttachment_ShouldSaveFileAndMetadata() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "content".getBytes()
        );

        var response = attachmentService.storeAttachment(task.getId(), file);

        assertNotNull(response);
        assertEquals("test.txt", response.getFileName());
        assertEquals(7L, response.getSize());

        List<TaskAttachment> attachments = attachmentRepository.findByTask_Id(task.getId());
        assertEquals(1, attachments.size());
    }

    @Test
    void storeAttachment_WhenTaskNotFound_ShouldThrowException() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "content".getBytes()
        );

        assertThrows(TaskNotFoundException.class,
                () -> attachmentService.storeAttachment(999L, file));
    }

    @Test
    void deleteAttachment_ShouldRemoveFileAndMetadata() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "content".getBytes()
        );

        var response = attachmentService.storeAttachment(task.getId(), file);
        Long attachmentId = response.getId();

        Optional<TaskAttachment> before = attachmentRepository.findById(attachmentId);
        assertTrue(before.isPresent());

        attachmentService.deleteAttachment(attachmentId);

        Optional<TaskAttachment> after = attachmentRepository.findById(attachmentId);
        assertTrue(after.isEmpty());
    }

    @Test
    void getAttachmentsByTaskId_ShouldReturnAttachments() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile(
                "file",
                "test1.txt",
                "text/plain",
                "content1".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "file",
                "test2.txt",
                "text/plain",
                "content2".getBytes()
        );

        attachmentService.storeAttachment(task.getId(), file1);
        attachmentService.storeAttachment(task.getId(), file2);

        var attachments = attachmentService.getAttachmentsByTaskId(task.getId());

        assertEquals(2, attachments.size());
    }
}