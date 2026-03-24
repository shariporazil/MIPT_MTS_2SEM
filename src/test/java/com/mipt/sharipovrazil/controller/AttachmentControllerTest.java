package com.mipt.sharipovrazil.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mipt.sharipovrazil.dto.AttachmentResponseDto;
import com.mipt.sharipovrazil.exception.AttachmentNotFoundException;
import com.mipt.sharipovrazil.exception.TaskNotFoundException;
import com.mipt.sharipovrazil.service.AttachmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AttachmentController.class)
class AttachmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AttachmentService attachmentService;

    private AttachmentResponseDto responseDto;
    private MockMultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        responseDto = new AttachmentResponseDto();
        responseDto.setId(1L);
        responseDto.setFileName("test.txt");
        responseDto.setSize(100L);
        responseDto.setUploadedAt(LocalDateTime.now());

        multipartFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Test content".getBytes()
        );
    }

    @Test
    void uploadAttachment_WhenTaskExists_ShouldReturn201() throws Exception {
        // given
        when(attachmentService.storeAttachment(eq(1L), any())).thenReturn(responseDto);

        // when & then
        mockMvc.perform(multipart("/api/tasks/1/attachments")
                        .file(multipartFile))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fileName").value("test.txt"));
    }

    @Test
    void uploadAttachment_WhenTaskDoesNotExist_ShouldReturn404() throws Exception {
        // given
        when(attachmentService.storeAttachment(eq(999L), any()))
                .thenThrow(new TaskNotFoundException("Task not found"));

        // when & then
        mockMvc.perform(multipart("/api/tasks/999/attachments")
                        .file(multipartFile))
                .andExpect(status().isNotFound());
    }

    @Test
    void uploadAttachment_WithEmptyFile_ShouldReturn400() throws Exception {
        // given
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.txt",
                "text/plain",
                new byte[0]
        );

        when(attachmentService.storeAttachment(eq(1L), any()))
                .thenThrow(new IllegalArgumentException("File is empty"));

        // when & then
        mockMvc.perform(multipart("/api/tasks/1/attachments")
                        .file(emptyFile))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadAttachment_WithFileExceedingSizeLimit_ShouldReturn400() throws Exception {
        // given
        byte[] largeContent = new byte[11 * 1024 * 1024];
        MockMultipartFile largeFile = new MockMultipartFile(
                "file",
                "large.txt",
                "text/plain",
                largeContent
        );

        when(attachmentService.storeAttachment(eq(1L), any()))
                .thenThrow(new IllegalArgumentException("File size exceeds limit"));

        // when & then
        mockMvc.perform(multipart("/api/tasks/1/attachments")
                        .file(largeFile))
                .andExpect(status().isBadRequest());
    }

    @Test
    void downloadAttachment_WhenExists_ShouldReturnFile() throws Exception {
        // given
        com.mipt.sharipovrazil.model.TaskAttachment attachment = new com.mipt.sharipovrazil.model.TaskAttachment();
        attachment.setFileName("test.txt");
        attachment.setContentType("text/plain");

        when(attachmentService.getAttachment(1L)).thenReturn(attachment);
        when(attachmentService.loadAsResource(1L))
                .thenReturn(new InputStreamResource(new ByteArrayInputStream("content".getBytes())));

        // when & then
        mockMvc.perform(get("/api/attachments/1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.txt\""));
    }

    @Test
    void downloadAttachment_WhenDoesNotExist_ShouldReturn404() throws Exception {
        // given
        when(attachmentService.getAttachment(999L))
                .thenThrow(new AttachmentNotFoundException("Attachment not found"));

        // when & then
        mockMvc.perform(get("/api/attachments/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAttachment_WhenExists_ShouldReturn204() throws Exception {
        // given
        doNothing().when(attachmentService).deleteAttachment(1L);

        // when & then
        mockMvc.perform(delete("/api/attachments/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAttachment_WhenDoesNotExist_ShouldReturn404() throws Exception {
        // given
        doThrow(new AttachmentNotFoundException("Attachment not found"))
                .when(attachmentService).deleteAttachment(999L);

        // when & then
        mockMvc.perform(delete("/api/attachments/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTaskAttachments_ShouldReturnList() throws Exception {
        // given
        when(attachmentService.getAttachmentsByTaskId(1L)).thenReturn(List.of(responseDto));

        // when & then
        mockMvc.perform(get("/api/tasks/1/attachments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}