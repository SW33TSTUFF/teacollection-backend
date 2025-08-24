package com.teacollection.teacollection_backend.controller;

import com.teacollection.teacollection_backend.Depot;
import com.teacollection.teacollection_backend.repository.DepotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Depot management operations.
 * Handles CRUD operations and location-based queries.
 */
@RestController
@RequestMapping("/api/depots")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DepotController {

    private final DepotRepository depotRepository;

    /**
     * Get all depots
     * @return List of all depots
     */
    @GetMapping
    public ResponseEntity<List<Depot>> getAllDepots() {
        try {
            List<Depot> depots = depotRepository.findAll();
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
            Optional<Depot> depot = depotRepository.findById(id);
            if (depot.isPresent()) {
                log.info("Retrieved depot with ID: {}", id);
                return ResponseEntity.ok(depot.get());
            } else {
                log.warn("Depot not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
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
            Depot savedDepot = depotRepository.save(depot);
            log.info("Created new depot with ID: {}", savedDepot.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedDepot);
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
            if (!depotRepository.existsById(id)) {
                log.warn("Depot not found for update with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            depot.setId(id);
            Depot updatedDepot = depotRepository.save(depot);
            log.info("Updated depot with ID: {}", id);
            return ResponseEntity.ok(updatedDepot);
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
            if (!depotRepository.existsById(id)) {
                log.warn("Depot not found for deletion with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            depotRepository.deleteById(id);
            log.info("Deleted depot with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting depot with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Find depot by exact coordinates
     * @param latitude Depot latitude
     * @param longitude Depot longitude
     * @return Depot if found, 404 if not found
     */
    @GetMapping("/coordinates")
    public ResponseEntity<Depot> getDepotByCoordinates(
            @RequestParam double latitude,
            @RequestParam double longitude) {
        try {
            Optional<Depot> depot = depotRepository.findByLatitudeAndLongitude(latitude, longitude);
            if (depot.isPresent()) {
                log.info("Retrieved depot at coordinates: {}, {}", latitude, longitude);
                return ResponseEntity.ok(depot.get());
            } else {
                log.warn("Depot not found at coordinates: {}, {}", latitude, longitude);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error retrieving depot by coordinates", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Find depots within a certain radius
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
            List<Depot> nearbyDepots = depotRepository.findDepotsWithinRadius(latitude, longitude, radiusKm);
            log.info("Retrieved {} depots within {}km radius", nearbyDepots.size(), radiusKm);
            return ResponseEntity.ok(nearbyDepots);
        } catch (Exception e) {
            log.error("Error retrieving nearby depots", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Find the closest depot to given coordinates
     * @param latitude Target latitude
     * @param longitude Target longitude
     * @return Closest depot if found, 404 if no depots exist
     */
    @GetMapping("/closest")
    public ResponseEntity<Depot> getClosestDepot(
            @RequestParam double latitude,
            @RequestParam double longitude) {
        try {
            Optional<Depot> depot = depotRepository.findClosestDepot(latitude, longitude);
            if (depot.isPresent()) {
                log.info("Retrieved closest depot to coordinates: {}, {}", latitude, longitude);
                return ResponseEntity.ok(depot.get());
            } else {
                log.warn("No depots found for closest calculation");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error finding closest depot", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Find depots in a rectangular area
     * @param minLatitude Minimum latitude
     * @param maxLatitude Maximum latitude
     * @param minLongitude Minimum longitude
     * @param maxLongitude Maximum longitude
     * @return List of depots within the rectangular area
     */
    @GetMapping("/area")
    public ResponseEntity<List<Depot>> getDepotsInArea(
            @RequestParam double minLatitude,
            @RequestParam double maxLatitude,
            @RequestParam double minLongitude,
            @RequestParam double maxLongitude) {
        try {
            List<Depot> depots = depotRepository.findDepotsInRectangularArea(
                    minLatitude, maxLatitude, minLongitude, maxLongitude);
            log.info("Retrieved {} depots in rectangular area", depots.size());
            return ResponseEntity.ok(depots);
        } catch (Exception e) {
            log.error("Error retrieving depots in area", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Find depots by latitude range
     * @param minLatitude Minimum latitude
     * @param maxLatitude Maximum latitude
     * @return List of depots within the latitude range
     */
    @GetMapping("/latitude-range")
    public ResponseEntity<List<Depot>> getDepotsByLatitudeRange(
            @RequestParam double minLatitude,
            @RequestParam double maxLatitude) {
        try {
            List<Depot> depots = depotRepository.findByLatitudeBetween(minLatitude, maxLatitude);
            log.info("Retrieved {} depots in latitude range: {} to {}", depots.size(), minLatitude, maxLatitude);
            return ResponseEntity.ok(depots);
        } catch (Exception e) {
            log.error("Error retrieving depots by latitude range", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Find depots by longitude range
     * @param minLongitude Minimum longitude
     * @param maxLongitude Maximum longitude
     * @return List of depots within the longitude range
     */
    @GetMapping("/longitude-range")
    public ResponseEntity<List<Depot>> getDepotsByLongitudeRange(
            @RequestParam double minLongitude,
            @RequestParam double maxLongitude) {
        try {
            List<Depot> depots = depotRepository.findByLongitudeBetween(minLongitude, maxLongitude);
            log.info("Retrieved {} depots in longitude range: {} to {}", depots.size(), minLongitude, maxLongitude);
            return ResponseEntity.ok(depots);
        } catch (Exception e) {
            log.error("Error retrieving depots by longitude range", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Find depots by approximate location with tolerance
     * @param latitude Target latitude
     * @param longitude Target longitude
     * @param tolerance Tolerance in degrees
     * @return List of depots within tolerance
     */
    @GetMapping("/approximate")
    public ResponseEntity<List<Depot>> getDepotsByApproximateLocation(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "0.01") double tolerance) {
        try {
            List<Depot> depots = depotRepository.findDepotsByApproximateLocation(latitude, longitude, tolerance);
            log.info("Retrieved {} depots near coordinates: {}, {} (tolerance: {})", 
                    depots.size(), latitude, longitude, tolerance);
            return ResponseEntity.ok(depots);
        } catch (Exception e) {
            log.error("Error retrieving depots by approximate location", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Count depots in a specific region
     * @param minLatitude Minimum latitude
     * @param maxLatitude Maximum latitude
     * @param minLongitude Minimum longitude
     * @param maxLongitude Maximum longitude
     * @return Count of depots in the region
     */
    @GetMapping("/count-in-region")
    public ResponseEntity<Long> countDepotsInRegion(
            @RequestParam double minLatitude,
            @RequestParam double maxLatitude,
            @RequestParam double minLongitude,
            @RequestParam double maxLongitude) {
        try {
            long count = depotRepository.countDepotsInRegion(minLatitude, maxLatitude, minLongitude, maxLongitude);
            log.info("Count of depots in region: {}", count);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error counting depots in region", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
