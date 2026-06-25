package com.mipt.sharipovrazil.service;

import com.mipt.sharipovrazil.dto.AttachmentResponseDto;
import com.mipt.sharipovrazil.exception.AttachmentNotFoundException;
import com.mipt.sharipovrazil.exception.TaskNotFoundException;
import com.mipt.sharipovrazil.model.Task;
import com.mipt.sharipovrazil.model.TaskAttachment;
import com.mipt.sharipovrazil.repository.AttachmentRepository;
import com.mipt.sharipovrazil.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private TaskRepository taskRepository;

    private AttachmentService attachmentService;

    @TempDir
    Path tempDir;

    private Task task;
    private TaskAttachment attachment;
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() throws IOException {
        // Создаем сервис вручную с tempDir
        attachmentService = new AttachmentService(
                tempDir.toString(),
                attachmentRepository,
                taskRepository
        );

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");

        attachment = new TaskAttachment();
        attachment.setId(1L);
        attachment.setTaskId(1L);
        attachment.setFileName("test.txt");
        attachment.setStoredFileName("test-uuid.txt");
        attachment.setContentType("text/plain");
        attachment.setSize(100L);

        multipartFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Test content".getBytes()
        );
    }

    @Test
    void storeAttachment_WhenTaskExists_ShouldSaveFileAndMetadata() throws IOException {
        // given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(attachmentRepository.save(any(TaskAttachment.class))).thenReturn(attachment);

        // when
        AttachmentResponseDto result = attachmentService.storeAttachment(1L, multipartFile);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getFileName()).isEqualTo("test.txt");
        verify(attachmentRepository).save(any(TaskAttachment.class));
    }

    @Test
    void storeAttachment_WhenTaskDoesNotExist_ShouldThrowTaskNotFoundException() {
        // given
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> attachmentService.storeAttachment(999L, multipartFile))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void getAttachment_WhenExists_ShouldReturnAttachment() {
        // given
        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(attachment));

        // when
        TaskAttachment result = attachmentService.getAttachment(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getAttachment_WhenDoesNotExist_ShouldThrowAttachmentNotFoundException() {
        // given
        when(attachmentRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> attachmentService.getAttachment(999L))
                .isInstanceOf(AttachmentNotFoundException.class);
    }

    @Test
    void deleteAttachment_WhenExists_ShouldDeleteFileAndMetadata() throws IOException {
        // given
        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(attachment));
        doNothing().when(attachmentRepository).deleteById(1L);

        // when
        attachmentService.deleteAttachment(1L);

        // then
        verify(attachmentRepository).deleteById(1L);
    }

    @Test
    void getAttachmentsByTaskId_ShouldReturnList() {
        // given
        when(attachmentRepository.findByTaskId(1L)).thenReturn(List.of(attachment));

        // when
        List<AttachmentResponseDto> result = attachmentService.getAttachmentsByTaskId(1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFileName()).isEqualTo("test.txt");
    }
}