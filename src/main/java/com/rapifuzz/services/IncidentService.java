package com.rapifuzz.services;

import com.rapifuzz.entities.Incident;
import com.rapifuzz.entities.User;

import java.util.List;
import java.util.Optional;

public interface IncidentService {

    Incident createIncident(String reporterName, String details, String priority, User user);

    Incident updateIncident(Long incidentId, String details, String priority, String status, User user);

    void deleteIncident(Long incidentId, User user);

    Optional<Incident> findById(Long id);

    Optional<Incident> findByIncidentId(String incidentId);

    List<Incident> findAll();

    void save(Incident incident);
}
