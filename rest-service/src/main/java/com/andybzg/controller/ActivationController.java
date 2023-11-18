package com.andybzg.controller;

import com.andybzg.service.UserActivationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class ActivationController {

    private final UserActivationService userActivationService;

    public ActivationController(UserActivationService userActivationService) {
        this.userActivationService = userActivationService;
    }

    //TODO implement different scenarios
    @GetMapping("/activation")
    public ResponseEntity<?> activation(@RequestParam("id") String id) {
        boolean response = userActivationService.activation(id);
        if (response) {
            return ResponseEntity.ok().body("Successfully registered");
        }
        return ResponseEntity.internalServerError().build();
    }
}
