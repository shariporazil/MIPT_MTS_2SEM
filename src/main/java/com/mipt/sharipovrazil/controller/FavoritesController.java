package com.mipt.sharipovrazil.controller;

import com.mipt.sharipovrazil.dto.TaskResponseDto;
import com.mipt.sharipovrazil.service.FavoritesService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoritesController {

    private final FavoritesService favoritesService;

    public FavoritesController(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    @Operation(summary = "Добавить задачу в избранное")
    @PostMapping("/{taskId}")
    public ResponseEntity<Void> addToFavorites(@PathVariable Long taskId, HttpSession session) {
        favoritesService.addToFavorites(taskId, session);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удалить задачу из избранного")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> removeFromFavorites(@PathVariable Long taskId, HttpSession session) {
        favoritesService.removeFromFavorites(taskId, session);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить список избранных задач")
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getFavorites(HttpSession session) {
        List<TaskResponseDto> favorites = favoritesService.getFavoriteTasks(session);
        return ResponseEntity.ok(favorites);
    }
}