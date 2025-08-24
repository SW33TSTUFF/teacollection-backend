package com.teacollection.teacollection_backend.controller;

import com.teacollection.teacollection_backend.Depot;
import com.teacollection.teacollection_backend.service.DepotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Depot management operations.
 * Handles CRUD operations and depot queries.
 */
@RestController
@RequestMapping({"/api/depots", "/api/depots/"})
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DepotController {

    private final DepotService depotService;

    /**
     * Get all depots
     * @return List of all depots
     */
    @GetMapping
    public ResponseEntity<List<Depot>> getAllDepots() {
        try {
            List<Depot> depots = depotService.getAllDepots();
            log.info("Retrieved {} depots", depots.size());
            return ResponseEntity.ok(depots);
        } catch (Exception e) {
            log.error("Error retrieving depots", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get depot by ID
     * @param id Depot ID
     * @return Depot if found, 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Depot> getDepotById(@PathVariable Long id) {
        try {
            Depot depot = depotService.getDepotById(id);
            log.info("Retrieved depot with ID: {}", id);
            return ResponseEntity.ok(depot);
        } catch (RuntimeException e) {
            log.warn("Depot not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error retrieving depot with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a new depot
     * @param depot Depot data
     * @return Created depot with generated ID
     */
    @PostMapping
    public ResponseEntity<Depot> createDepot(@RequestBody Depot depot) {
        try {
            Depot savedDepot = depotService.createDepot(depot);
            log.info("Created new depot with ID: {}", savedDepot.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedDepot);
        } catch (RuntimeException e) {
            log.warn("Validation error creating depot: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error creating depot", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update an existing depot
     * @param id Depot ID
     * @param depot Updated depot data
     * @return Updated depot
     */
    @PutMapping("/{id}")
    public ResponseEntity<Depot> updateDepot(@PathVariable Long id, @RequestBody Depot depot) {
        try {
            Depot updatedDepot = depotService.updateDepot(id, depot);
            log.info("Updated depot with ID: {}", id);
            return ResponseEntity.ok(updatedDepot);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                log.warn("Depot not found for update with ID: {}", id);
                return ResponseEntity.notFound().build();
            } else {
                log.warn("Validation error updating depot: {}", e.getMessage());
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error updating depot with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a depot
     * @param id Depot ID
     * @return 204 No Content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepot(@PathVariable Long id) {
        try {
            depotService.deleteDepot(id);
            log.info("Deleted depot with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.warn("Depot not found for deletion with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting depot with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all active depots
     * @return List of active depots
     */
    @GetMapping("/active")
    public ResponseEntity<List<Depot>> getActiveDepots() {
        try {
            List<Depot> activeDepots = depotService.getActiveDepots();
            log.info("Retrieved {} active depots", activeDepots.size());
            return ResponseEntity.ok(activeDepots);
        } catch (Exception e) {
            log.error("Error retrieving active depots", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get depots with capacity above threshold
     * @param minCapacity Minimum capacity
     * @return List of depots meeting capacity criteria
     */
    @GetMapping("/capacity/{minCapacity}")
    public ResponseEntity<List<Depot>> getDepotsByCapacity(@PathVariable double minCapacity) {
        try {
            List<Depot> depots = depotService.getDepotsByCapacity(minCapacity);
            log.info("Retrieved {} depots with capacity >= {}", depots.size(), minCapacity);
            return ResponseEntity.ok(depots);
        } catch (Exception e) {
            log.error("Error retrieving depots by capacity", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update depot active status
     * @param id Depot ID
     * @param isActive New active status
     * @return Updated depot
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Depot> updateDepotStatus(@PathVariable Long id, @RequestParam boolean isActive) {
        try {
            Depot updatedDepot = depotService.updateDepotStatus(id, isActive);
            log.info("Updated depot {} active status to: {}", id, isActive);
            return ResponseEntity.ok(updatedDepot);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                log.warn("Depot not found for status update with ID: {}", id);
                return ResponseEntity.notFound().build();
            } else {
                log.warn("Active status functionality not implemented: {}", e.getMessage());
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error updating depot status with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get depots within a certain radius
     * @param latitude Center latitude
     * @param longitude Center longitude
     * @param radiusKm Radius in kilometers
     * @return List of depots within the radius
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<Depot>> getDepotsNearby(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radiusKm) {
        try {
            List<Depot> nearbyDepots = depotService.getDepotsNearby(latitude, longitude, radiusKm);
            log.info("Retrieved {} depots within {}km radius", nearbyDepots.size(), radiusKm);
            return ResponseEntity.ok(nearbyDepots);
        } catch (Exception e) {
            log.error("Error retrieving nearby depots", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
