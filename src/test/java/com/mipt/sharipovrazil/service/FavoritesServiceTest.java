package com.mipt.sharipovrazil.service;

import com.mipt.sharipovrazil.dto.TaskResponseDto;
import com.mipt.sharipovrazil.mapper.TaskMapper;
import com.mipt.sharipovrazil.model.Task;
import com.mipt.sharipovrazil.repository.TaskRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoritesServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private HttpSession session;

    @InjectMocks
    private FavoritesService favoritesService;

    private Task task1;
    private Task task2;
    private TaskResponseDto responseDto1;
    private TaskResponseDto responseDto2;

    @BeforeEach
    void setUp() {
        task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task 1");

        task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");

        responseDto1 = new TaskResponseDto();
        responseDto1.setId(1L);
        responseDto1.setTitle("Task 1");

        responseDto2 = new TaskResponseDto();
        responseDto2.setId(2L);
        responseDto2.setTitle("Task 2");
    }

    @Test
    void addToFavorites_ShouldAddTaskIdToExistingSession() {
        // given
        Set<Long> favorites = new HashSet<>();
        favorites.add(2L);
        when(session.getAttribute("favoriteTaskIds")).thenReturn(favorites);

        // when
        favoritesService.addToFavorites(1L, session);

        // then
        verify(session, times(1)).setAttribute(eq("favoriteTaskIds"), any(Set.class));
    }

    @Test
    void addToFavorites_WhenSessionHasNoFavorites_ShouldCreateNewSet() {
        // given
        when(session.getAttribute("favoriteTaskIds")).thenReturn(null);

        // when
        favoritesService.addToFavorites(1L, session);

        // then
        verify(session, times(1)).setAttribute(eq("favoriteTaskIds"), any(Set.class));
    }

    @Test
    void removeFromFavorites_ShouldRemoveTaskId() {
        // given
        Set<Long> favorites = new HashSet<>();
        favorites.add(1L);
        favorites.add(2L);
        when(session.getAttribute("favoriteTaskIds")).thenReturn(favorites);

        // when
        favoritesService.removeFromFavorites(1L, session);

        // then
        verify(session, times(1)).setAttribute(eq("favoriteTaskIds"), any(Set.class));
    }

    @Test
    void getFavoriteTasks_ShouldReturnListOfFavoriteTasks() {
        // given
        Set<Long> favorites = new HashSet<>();
        favorites.add(1L);
        favorites.add(2L);
        when(session.getAttribute("favoriteTaskIds")).thenReturn(favorites);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        when(taskRepository.findById(2L)).thenReturn(Optional.of(task2));
        when(taskMapper.toResponseDto(task1)).thenReturn(responseDto1);
        when(taskMapper.toResponseDto(task2)).thenReturn(responseDto2);

        // when
        List<TaskResponseDto> result = favoritesService.getFavoriteTasks(session);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("id").containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void getFavoriteTasks_WhenTaskNotFound_ShouldSkipMissingTask() {
        // given
        Set<Long> favorites = new HashSet<>();
        favorites.add(1L);
        favorites.add(999L);
        when(session.getAttribute("favoriteTaskIds")).thenReturn(favorites);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());
        when(taskMapper.toResponseDto(task1)).thenReturn(responseDto1);

        // when
        List<TaskResponseDto> result = favoritesService.getFavoriteTasks(session);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void isFavorite_WhenTaskInFavorites_ShouldReturnTrue() {
        // given
        Set<Long> favorites = new HashSet<>();
        favorites.add(1L);
        favorites.add(2L);
        when(session.getAttribute("favoriteTaskIds")).thenReturn(favorites);

        // when
        boolean result = favoritesService.isFavorite(1L, session);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void isFavorite_WhenTaskNotInFavorites_ShouldReturnFalse() {
        // given
        Set<Long> favorites = new HashSet<>();
        favorites.add(1L);
        favorites.add(2L);
        when(session.getAttribute("favoriteTaskIds")).thenReturn(favorites);

        // when
        boolean result = favoritesService.isFavorite(999L, session);

        // then
        assertThat(result).isFalse();
    }
}