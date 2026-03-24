package com.mipt.sharipovrazil.service;

import com.mipt.sharipovrazil.dto.TaskResponseDto;
import com.mipt.sharipovrazil.mapper.TaskMapper;
import com.mipt.sharipovrazil.model.Task;
import com.mipt.sharipovrazil.repository.TaskRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FavoritesService {

    private static final String FAVORITES_SESSION_KEY = "favoriteTaskIds";

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public FavoritesService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    @SuppressWarnings("unchecked")
    private Set<Long> getFavoriteIds(HttpSession session) {
        Set<Long> favorites = (Set<Long>) session.getAttribute(FAVORITES_SESSION_KEY);
        if (favorites == null) {
            favorites = new HashSet<>();
            // Не вызываем setAttribute здесь!
        }
        return favorites;
    }

    public void addToFavorites(Long taskId, HttpSession session) {
        Set<Long> favorites = getFavoriteIds(session);
        // Проверяем, есть ли уже атрибут в сессии
        if (session.getAttribute(FAVORITES_SESSION_KEY) == null) {
            // Создаем новый Set и сохраняем в сессию
            Set<Long> newFavorites = new HashSet<>();
            newFavorites.add(taskId);
            session.setAttribute(FAVORITES_SESSION_KEY, newFavorites);
        } else {
            // Обновляем существующий Set
            favorites.add(taskId);
            session.setAttribute(FAVORITES_SESSION_KEY, favorites);
        }
    }

    public void removeFromFavorites(Long taskId, HttpSession session) {
        Set<Long> favorites = getFavoriteIds(session);
        favorites.remove(taskId);
        session.setAttribute(FAVORITES_SESSION_KEY, favorites);
    }

    public List<TaskResponseDto> getFavoriteTasks(HttpSession session) {
        Set<Long> favoriteIds = getFavoriteIds(session);
        return favoriteIds.stream()
                .map(taskRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(taskMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public boolean isFavorite(Long taskId, HttpSession session) {
        return getFavoriteIds(session).contains(taskId);
    }
}