package com.mipt.sharipovrazil.controller;

import com.mipt.sharipovrazil.dto.TaskResponseDto;
import com.mipt.sharipovrazil.service.FavoritesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FavoritesController.class)
class FavoritesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FavoritesService favoritesService;

    private TaskResponseDto responseDto;

    @BeforeEach
    void setUp() {
        responseDto = new TaskResponseDto();
        responseDto.setId(1L);
        responseDto.setTitle("Test Task");
    }

    @Test
    void addToFavorites_ShouldReturn200() throws Exception {
        // given
        doNothing().when(favoritesService).addToFavorites(eq(1L), any());

        // when & then
        mockMvc.perform(post("/api/favorites/1"))
                .andExpect(status().isOk());
    }

    @Test
    void removeFromFavorites_ShouldReturn204() throws Exception {
        // given
        doNothing().when(favoritesService).removeFromFavorites(eq(1L), any());

        // when & then
        mockMvc.perform(delete("/api/favorites/1"))
                .andExpect(status().isNoContent());
    }
}