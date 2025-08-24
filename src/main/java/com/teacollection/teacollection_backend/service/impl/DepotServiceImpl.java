package com.teacollection.teacollection_backend.service.impl;

import com.teacollection.teacollection_backend.Depot;
import com.teacollection.teacollection_backend.repository.DepotRepository;
import com.teacollection.teacollection_backend.service.DepotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DepotServiceImpl implements DepotService {

    private final DepotRepository depotRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Depot> getAllDepots() {
        log.info("Retrieving all depots");
        return depotRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Depot getDepotById(Long id) {
        log.info("Retrieving depot with ID: {}", id);
        return depotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Depot not found with ID: " + id));
    }

    @Override
    public Depot createDepot(Depot depot) {
        log.info("Creating new depot at coordinates ({}, {})", depot.getLatitude(), depot.getLongitude());
        
        // Validate depot data
        if (depot.getLatitude() < -90 || depot.getLatitude() > 90) {
            throw new RuntimeException("Latitude must be between -90 and 90 degrees");
        }
        
        if (depot.getLongitude() < -180 || depot.getLongitude() > 180) {
            throw new RuntimeException("Longitude must be between -180 and 180 degrees");
        }
        
        Depot savedDepot = depotRepository.save(depot);
        log.info("Created depot with ID: {}", savedDepot.getId());
        return savedDepot;
    }

    @Override
    public Depot updateDepot(Long id, Depot depot) {
        log.info("Updating depot with ID: {}", id);
        
        if (!depotRepository.existsById(id)) {
            throw new RuntimeException("Depot not found with ID: " + id);
        }
        
        // Validate depot data
        if (depot.getLatitude() < -90 || depot.getLatitude() > 90) {
            throw new RuntimeException("Latitude must be between -90 and 90 degrees");
        }
        
        if (depot.getLongitude() < -180 || depot.getLongitude() > 180) {
            throw new RuntimeException("Longitude must be between -180 and 180 degrees");
        }
        
        depot.setId(id);
        Depot updatedDepot = depotRepository.save(depot);
        log.info("Updated depot with ID: {}", id);
        return updatedDepot;
    }

    @Override
    public void deleteDepot(Long id) {
        log.info("Deleting depot with ID: {}", id);
        
        if (!depotRepository.existsById(id)) {
            throw new RuntimeException("Depot not found with ID: " + id);
        }
        
        depotRepository.deleteById(id);
        log.info("Deleted depot with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Depot> getActiveDepots() {
        log.info("Retrieving all depots (no active status field in current entity)");
        return depotRepository.findAll(); // All depots are considered active
    }

    @Override
    @Transactional(readOnly = true)
    public List<Depot> getDepotsByCapacity(double minCapacity) {
        log.info("Capacity field not available in current Depot entity");
        return List.of(); // Return empty list as capacity is not implemented
    }

    @Override
    public Depot updateDepotStatus(Long id, boolean isActive) {
        log.info("Active status field not available in current Depot entity");
        throw new RuntimeException("Active status functionality not implemented");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Depot> getDepotsNearby(double latitude, double longitude, double radiusKm) {
        log.info("Retrieving depots within {}km radius from ({}, {})", radiusKm, latitude, longitude);
        return depotRepository.findDepotsWithinRadius(latitude, longitude, radiusKm);
    }
}
