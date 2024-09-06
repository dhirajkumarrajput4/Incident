package com.rapifuzz.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String incidentId;  // e.g., RMG123452022

    @Column(nullable = false)
    private String reporterName;

    @Column(nullable = false)
    private String details;  // Incident details

    @Column(nullable = false)
    private LocalDateTime reportedDate;  // Incident reported date and time

    @Column(nullable = false)
    private String priority;  // High, Medium, Low

    @Column(nullable = false)
    private String status;  // Open, In progress, Closed

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

}

