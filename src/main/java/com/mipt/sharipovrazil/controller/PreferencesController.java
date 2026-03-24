package com.mipt.sharipovrazil.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
public class PreferencesController {

    private static final String VIEW_PREFERENCE_COOKIE_NAME = "viewPreference";
    private static final String DEFAULT_VIEW_MODE = "detailed";

    @Operation(summary = "Получить текущую настройку отображения")
    @GetMapping("/view")
    public ResponseEntity<String> getViewPreference(@CookieValue(name = VIEW_PREFERENCE_COOKIE_NAME, required = false) String viewMode) {
        String mode = viewMode != null ? viewMode : DEFAULT_VIEW_MODE;
        return ResponseEntity.ok(mode);
    }

    @Operation(summary = "Установить настройку отображения")
    @PostMapping("/view")
    public ResponseEntity<Void> setViewPreference(
            @RequestParam String mode,
            HttpServletResponse response) {

        Cookie cookie = new Cookie(VIEW_PREFERENCE_COOKIE_NAME, mode);
        cookie.setPath("/");
        cookie.setMaxAge(30 * 24 * 60 * 60);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }
}