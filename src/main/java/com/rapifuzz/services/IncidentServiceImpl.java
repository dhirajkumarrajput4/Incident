package com.rapifuzz.services;

import com.rapifuzz.dao.IncidentRepository;
import com.rapifuzz.entities.Incident;
import com.rapifuzz.entities.User;
import com.rapifuzz.exception.IncidentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IncidentServiceImpl implements IncidentService {

    @Autowired
    private IncidentRepository incidentRepository;

    @Override
    public Incident createIncident(String reporterName, String details, String priority, User user) {
        Incident incident = new Incident();
        incident.setIncidentId(generateUniqueIncidentId());
        incident.setReporterName(reporterName);
        incident.setDetails(details);
        incident.setReportedDate(LocalDateTime.now());
        incident.setPriority(priority);
        incident.setStatus("Open");  // Default status is "Open"
        incident.setUser(user);

        return incidentRepository.save(incident);
    }


    public Incident updateIncident(Long incidentId, String details, String priority, String status, User user) {
        Optional<Incident> optionalIncident = incidentRepository.findById(incidentId);

        if (optionalIncident.isPresent()) {
            Incident incident = optionalIncident.get();

            if (!incident.getUser().getId().equals(user.getId())) {
                throw new IncidentException("Unauthorized to update this incident.");
            }
            if (incident.getStatus().equals("Closed")) {
                throw new IncidentException("Cannot update a closed incident.");
            }

            incident.setDetails(details);
            incident.setPriority(priority);
            incident.setStatus(status);

            return incidentRepository.save(incident);
        } else {
            throw new IncidentException("Incident not found.");
        }
    }

    public void deleteIncident(Long incidentId, User user) {
        Optional<Incident> optionalIncident = incidentRepository.findById(incidentId);

        if (optionalIncident.isPresent()) {
            Incident incident = optionalIncident.get();

            if (!incident.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized to delete this incident.");
            }
            incidentRepository.delete(incident);
        } else {
            throw new RuntimeException("Incident not found.");
        }
    }

    @Override
    public Optional<Incident> findById(Long id) {
        return this.incidentRepository.findById(id);
    }

    @Override
    public Optional<Incident> findByIncidentId(String incidentId) {
        return this.incidentRepository.findByIncidentId(incidentId);
    }

    @Override
    public List<Incident> findAll() {
        return this.incidentRepository.findAll();
    }

    @Override
    public void save(Incident incident) {
        this.incidentRepository.save(incident);
    }

    public String generateUniqueIncidentId() {
        String incidentId;
        do {
            int randomNum = (int) (Math.random() * 90000) + 10000;
            incidentId = "RMG" + randomNum + LocalDateTime.now().getYear();  // Format: RMG123452022
        } while (incidentRepository.findByIncidentId(incidentId).isPresent());  // Ensure the ID is unique
        return incidentId;
    }

}
