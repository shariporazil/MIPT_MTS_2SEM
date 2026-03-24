package com.mipt.sharipovrazil.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "DTO для ответа с информацией о вложении")
public class AttachmentResponseDto {

    @Schema(description = "ID вложения")
    private Long id;

    @Schema(description = "Оригинальное имя файла")
    private String fileName;

    @Schema(description = "Размер файла в байтах")
    private long size;

    @Schema(description = "Дата загрузки")
    private LocalDateTime uploadedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}