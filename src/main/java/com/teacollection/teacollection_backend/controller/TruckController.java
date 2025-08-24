package com.teacollection.teacollection_backend.controller;

import com.teacollection.teacollection_backend.Truck;
import com.teacollection.teacollection_backend.repository.TruckRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.validation.annotation.Validated;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Truck management operations.
 * Handles CRUD operations, status updates, and capacity management.
 */
@RestController
@RequestMapping("/api/trucks")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TruckController {

    private final TruckRepository truckRepository;

    /**
     * Get all trucks
     * @return List of all trucks
     */
    @GetMapping
    public ResponseEntity<List<Truck>> getAllTrucks() {
        try {
            List<Truck> trucks = truckRepository.findAll();
            log.info("Retrieved {} trucks", trucks.size());
            return ResponseEntity.ok(trucks);
        } catch (Exception e) {
            log.error("Error retrieving trucks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get truck by ID
     * @param id Truck ID
     * @return Truck if found, 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Truck> getTruckById(@PathVariable Long id) {
        try {
            Optional<Truck> truck = truckRepository.findById(id);
            if (truck.isPresent()) {
                log.info("Retrieved truck with ID: {}", id);
                return ResponseEntity.ok(truck.get());
            } else {
                log.warn("Truck not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error retrieving truck with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a new truck
     * @param truck Truck data
     * @return Created truck with generated ID
     */
    @PostMapping
    public ResponseEntity<Truck> createTruck(@RequestBody Truck truck) {
        try {
            // Set default values if not provided
            if (truck.getStatus() == null) {
                truck.setStatus("IDLE");
            }
            if (truck.getCurrentLoad() == 0.0) {
                truck.setCurrentLoad(0.0);
            }
            
            Truck savedTruck = truckRepository.save(truck);
            log.info("Created new truck with ID: {}", savedTruck.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTruck);
        } catch (Exception e) {
            log.error("Error creating truck", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update an existing truck
     * @param id Truck ID
     * @param truck Updated truck data
     * @return Updated truck
     */
    @PutMapping("/{id}")
    public ResponseEntity<Truck> updateTruck(@PathVariable Long id, @RequestBody Truck truck) {
        try {
            if (!truckRepository.existsById(id)) {
                log.warn("Truck not found for update with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            truck.setId(id);
            Truck updatedTruck = truckRepository.save(truck);
            log.info("Updated truck with ID: {}", id);
            return ResponseEntity.ok(updatedTruck);
        } catch (Exception e) {
            log.error("Error updating truck with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a truck
     * @param id Truck ID
     * @return 204 No Content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTruck(@PathVariable Long id) {
        try {
            if (!truckRepository.existsById(id)) {
                log.warn("Truck not found for deletion with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            truckRepository.deleteById(id);
            log.info("Deleted truck with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting truck with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get trucks by status
     * @param status Truck status (IDLE, EN_ROUTE, RETURNING)
     * @return List of trucks with specified status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Truck>> getTrucksByStatus(@PathVariable String status) {
        try {
            List<Truck> trucks = truckRepository.findByStatus(status);
            log.info("Retrieved {} trucks with status: {}", trucks.size(), status);
            return ResponseEntity.ok(trucks);
        } catch (Exception e) {
            log.error("Error retrieving trucks by status: {}", status, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all idle trucks
     * @return List of idle trucks
     */
    @GetMapping("/idle")
    public ResponseEntity<List<Truck>> getIdleTrucks() {
        try {
            List<Truck> idleTrucks = truckRepository.findIdleTrucks();
            log.info("Retrieved {} idle trucks", idleTrucks.size());
            return ResponseEntity.ok(idleTrucks);
        } catch (Exception e) {
            log.error("Error retrieving idle trucks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get trucks with available capacity above threshold
     * @param minAvailableCapacity Minimum available capacity required
     * @return List of trucks with sufficient available capacity
     */
    @GetMapping("/available-capacity")
    public ResponseEntity<List<Truck>> getTrucksWithAvailableCapacity(@RequestParam double minAvailableCapacity) {
        try {
            List<Truck> trucks = truckRepository.findTrucksWithAvailableCapacity(minAvailableCapacity);
            log.info("Retrieved {} trucks with available capacity >= {}", trucks.size(), minAvailableCapacity);
            return ResponseEntity.ok(trucks);
        } catch (Exception e) {
            log.error("Error retrieving trucks by available capacity", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get trucks by capacity range
     * @param minCapacity Minimum capacity
     * @param maxCapacity Maximum capacity
     * @return List of trucks within the capacity range
     */
    @GetMapping("/capacity-range")
    public ResponseEntity<List<Truck>> getTrucksByCapacityRange(
            @RequestParam double minCapacity,
            @RequestParam double maxCapacity) {
        try {
            List<Truck> trucks = truckRepository.findByMaxCapacityBetween(minCapacity, maxCapacity);
            log.info("Retrieved {} trucks with capacity between {} and {}", trucks.size(), minCapacity, maxCapacity);
            return ResponseEntity.ok(trucks);
        } catch (Exception e) {
            log.error("Error retrieving trucks by capacity range", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get underloaded trucks
     * @param capacityPercentage Percentage of capacity threshold (0.0 to 1.0)
     * @return List of underloaded trucks
     */
    @GetMapping("/underloaded")
    public ResponseEntity<List<Truck>> getUnderloadedTrucks(@RequestParam(defaultValue = "0.5") double capacityPercentage) {
        try {
            List<Truck> trucks = truckRepository.findUnderloadedTrucks(capacityPercentage);
            log.info("Retrieved {} underloaded trucks (threshold: {})", trucks.size(), capacityPercentage);
            return ResponseEntity.ok(trucks);
        } catch (Exception e) {
            log.error("Error retrieving underloaded trucks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get trucks near capacity
     * @param capacityPercentage Percentage of capacity threshold (0.0 to 1.0)
     * @return List of trucks near capacity
     */
    @GetMapping("/near-capacity")
    public ResponseEntity<List<Truck>> getTrucksNearCapacity(@RequestParam(defaultValue = "0.8") double capacityPercentage) {
        try {
            List<Truck> trucks = truckRepository.findTrucksNearCapacity(capacityPercentage);
            log.info("Retrieved {} trucks near capacity (threshold: {})", trucks.size(), capacityPercentage);
            return ResponseEntity.ok(trucks);
        } catch (Exception e) {
            log.error("Error retrieving trucks near capacity", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update truck status
     * @param id Truck ID
     * @param status New status
     * @return Updated truck
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Truck> updateTruckStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Optional<Truck> truckOpt = truckRepository.findById(id);
            if (truckOpt.isEmpty()) {
                log.warn("Truck not found for status update with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            Truck truck = truckOpt.get();
            truck.setStatus(status);
            Truck updatedTruck = truckRepository.save(truck);
            
            log.info("Updated truck {} status to: {}", id, status);
            return ResponseEntity.ok(updatedTruck);
        } catch (Exception e) {
            log.error("Error updating truck status with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update truck load
     * @param id Truck ID
     * @param currentLoad New current load
     * @return Updated truck
     */
    @PatchMapping("/{id}/load")
    public ResponseEntity<Truck> updateTruckLoad(@PathVariable Long id, @RequestParam double currentLoad) {
        try {
            Optional<Truck> truckOpt = truckRepository.findById(id);
            if (truckOpt.isEmpty()) {
                log.warn("Truck not found for load update with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            Truck truck = truckOpt.get();
            if (currentLoad > truck.getMaxCapacity()) {
                log.warn("Load {} exceeds max capacity {} for truck {}", currentLoad, truck.getMaxCapacity(), id);
                return ResponseEntity.badRequest().build();
            }
            
            truck.setCurrentLoad(currentLoad);
            Truck updatedTruck = truckRepository.save(truck);
            
            log.info("Updated truck {} load to: {}", id, currentLoad);
            return ResponseEntity.ok(updatedTruck);
        } catch (Exception e) {
            log.error("Error updating truck load with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get trucks that can accommodate a specific weight
     * @param weight Weight to be accommodated
     * @return List of trucks that can carry the specified weight
     */
    @GetMapping("/can-accommodate")
    public ResponseEntity<List<Truck>> getTrucksThatCanAccommodateWeight(@RequestParam double weight) {
        try {
            List<Truck> trucks = truckRepository.findTrucksThatCanAccommodateWeight(weight);
            log.info("Retrieved {} trucks that can accommodate weight: {}", trucks.size(), weight);
            return ResponseEntity.ok(trucks);
        } catch (Exception e) {
            log.error("Error retrieving trucks by weight accommodation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get truck count by status
     * @param status Truck status
     * @return Count of trucks with specified status
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getTruckCount(@RequestParam String status) {
        try {
            long count = truckRepository.countByStatus(status);
            log.info("Count of trucks with status {}: {}", status, count);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error counting trucks by status: {}", status, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get fully loaded trucks
     * @return List of fully loaded trucks
     */
    @GetMapping("/fully-loaded")
    public ResponseEntity<List<Truck>> getFullyLoadedTrucks() {
        try {
            List<Truck> trucks = truckRepository.findFullyLoadedTrucks();
            log.info("Retrieved {} fully loaded trucks", trucks.size());
            return ResponseEntity.ok(trucks);
        } catch (Exception e) {
            log.error("Error retrieving fully loaded trucks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get empty trucks
     * @param maxLoadThreshold Maximum load threshold to consider as empty
     * @return List of empty or nearly empty trucks
     */
    @GetMapping("/empty")
    public ResponseEntity<List<Truck>> getEmptyTrucks(@RequestParam(defaultValue = "0.1") double maxLoadThreshold) {
        try {
            List<Truck> trucks = truckRepository.findEmptyTrucks(maxLoadThreshold);
            log.info("Retrieved {} empty trucks (threshold: {})", trucks.size(), maxLoadThreshold);
            return ResponseEntity.ok(trucks);
        } catch (Exception e) {
            log.error("Error retrieving empty trucks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
