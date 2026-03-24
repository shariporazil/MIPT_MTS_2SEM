package com.mipt.sharipovrazil.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PreferencesController.class)
class PreferencesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getViewPreference_WhenCookieExists_ShouldReturnCookieValue() throws Exception {
        // when & then
        mockMvc.perform(get("/api/preferences/view")
                        .cookie(new jakarta.servlet.http.Cookie("viewPreference", "compact")))
                .andExpect(status().isOk())
                .andExpect(content().string("compact"));
    }

    @Test
    void getViewPreference_WhenCookieDoesNotExist_ShouldReturnDefault() throws Exception {
        // when & then
        mockMvc.perform(get("/api/preferences/view"))
                .andExpect(status().isOk())
                .andExpect(content().string("detailed"));
    }

    @Test
    void setViewPreference_WithDetailedMode_ShouldSetCookie() throws Exception {
        // when & then
        mockMvc.perform(post("/api/preferences/view")
                        .param("mode", "detailed"))
                .andExpect(status().isOk())
                .andExpect(cookie().value("viewPreference", "detailed"));
    }
}