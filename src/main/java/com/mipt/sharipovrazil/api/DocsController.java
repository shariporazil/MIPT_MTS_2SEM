package com.mipt.sharipovrazil.api;

import com.mipt.sharipovrazil.dto.common.MessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class DocsController {

    @GetMapping("/docs")
    public ResponseEntity<MessageResponse> docs() {
        return ResponseEntity.ok(new MessageResponse("Protected documentation for users with READ_PRIVILEGE"));
    }
}