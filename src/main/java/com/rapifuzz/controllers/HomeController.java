package com.rapifuzz.controllers;

import com.rapifuzz.entities.Incident;
import com.rapifuzz.entities.User;
import com.rapifuzz.services.IncidentService;
import com.rapifuzz.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/home/")
@CrossOrigin(origins = "http://localhost:3000")
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @GetMapping("/test")
    public ResponseEntity<String> getMessage() {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("method called ");
    }

    @PostMapping("/create")
    public ResponseEntity<Incident> createIncident(@RequestParam String details, @RequestParam String priority, Authentication authentication) {
        Optional<User> user = userService.findByEmail(authentication.getName());
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        // Create the incident
        Incident incident = incidentService.createIncident(user.get().getName(), details, priority, user.get());
        return ResponseEntity.ok(incident);
    }

    @GetMapping("/incident/{id}")
    public ResponseEntity<?> getIncident(@PathVariable Long id, Authentication authentication) {
        Optional<User> userOptional = userService.findByEmail(authentication.getName());
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Incident incident = userOptional.get().getIncidents().stream()
                .filter(inci -> inci.getId().equals(id))
                .findFirst().orElseThrow(() -> new RuntimeException("No incident found with this id {}"));
        return ResponseEntity.ok(incident);
    }

    @GetMapping("/incidents")
    public ResponseEntity<?> getIncidents(Authentication authentication) {
        Optional<User> userOptional = userService.findByEmail(authentication.getName());
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Incident> incidents = userOptional.get().getIncidents();
        return ResponseEntity.ok(incidents);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Incident> updateIncident(
            @PathVariable Long id,
            @RequestParam String details,
            @RequestParam String priority,
            @RequestParam String status,
            Authentication authentication) {
        Optional<User> user = userService.findByEmail(authentication.getName());
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Incident updatedIncident = incidentService.updateIncident(id, details, priority, status, user.get());
        return ResponseEntity.ok(updatedIncident);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteIncident(@PathVariable Long id, Authentication authentication) {
        Optional<User> user = userService.findByEmail(authentication.getName());
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        incidentService.deleteIncident(id, user.get());
        return ResponseEntity.ok().build();
    }


}
