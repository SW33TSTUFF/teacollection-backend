package com.teacollection.teacollection_backend.controller;

import com.teacollection.teacollection_backend.Truck;
import com.teacollection.teacollection_backend.service.TruckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Truck management operations.
 * Handles CRUD operations, load updates, and truck queries.
 */
@RestController
@RequestMapping({"/api/trucks", "/api/trucks/"})
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TruckController {

    private final TruckService truckService;

    /**
     * Get all trucks
     * @return List of all trucks
     */
    @GetMapping
    public ResponseEntity<List<Truck>> getAllTrucks() {
        try {
            List<Truck> trucks = truckService.getAllTrucks();
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
            Truck truck = truckService.getTruckById(id);
            log.info("Retrieved truck with ID: {}", id);
            return ResponseEntity.ok(truck);
        } catch (RuntimeException e) {
            log.warn("Truck not found with ID: {}", id);
            return ResponseEntity.notFound().build();
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
            Truck savedTruck = truckService.createTruck(truck);
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
            Truck updatedTruck = truckService.updateTruck(id, truck);
            log.info("Updated truck with ID: {}", id);
            return ResponseEntity.ok(updatedTruck);
        } catch (RuntimeException e) {
            log.warn("Truck not found for update with ID: {}", id);
            return ResponseEntity.notFound().build();
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
            truckService.deleteTruck(id);
            log.info("Deleted truck with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.warn("Truck not found for deletion with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting truck with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all available trucks
     * @return List of available trucks
     */
    @GetMapping("/available")
    public ResponseEntity<List<Truck>> getAvailableTrucks() {
        try {
            List<Truck> availableTrucks = truckService.getAvailableTrucks();
            log.info("Retrieved {} available trucks", availableTrucks.size());
            return ResponseEntity.ok(availableTrucks);
        } catch (Exception e) {
            log.error("Error retrieving available trucks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get trucks with capacity above threshold
     * @param minCapacity Minimum capacity
     * @return List of trucks meeting capacity criteria
     */
    @GetMapping("/capacity/{minCapacity}")
    public ResponseEntity<List<Truck>> getTrucksByCapacity(@PathVariable double minCapacity) {
        try {
            List<Truck> trucks = truckService.getTrucksByCapacity(minCapacity);
            log.info("Retrieved {} trucks with capacity >= {}", trucks.size(), minCapacity);
            return ResponseEntity.ok(trucks);
        } catch (Exception e) {
            log.error("Error retrieving trucks by capacity", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get trucks with fuel level above threshold
     * @param minFuelLevel Minimum fuel level
     * @return List of trucks meeting fuel criteria
     */
    @GetMapping("/fuel/{minFuelLevel}")
    public ResponseEntity<List<Truck>> getTrucksByFuelLevel(@PathVariable double minFuelLevel) {
        try {
            List<Truck> trucks = truckService.getTrucksByFuelLevel(minFuelLevel);
            log.info("Retrieved {} trucks with fuel level >= {}", trucks.size(), minFuelLevel);
            return ResponseEntity.ok(trucks);
        } catch (Exception e) {
            log.error("Error retrieving trucks by fuel level", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update truck current load
     * @param id Truck ID
     * @param load New current load
     * @return Updated truck
     */
    @PatchMapping("/{id}/load")
    public ResponseEntity<Truck> updateTruckLoad(@PathVariable Long id, @RequestParam double load) {
        try {
            Truck updatedTruck = truckService.updateTruckLoad(id, load);
            log.info("Updated truck {} load to: {}", id, load);
            return ResponseEntity.ok(updatedTruck);
        } catch (RuntimeException e) {
            log.warn("Truck not found for load update with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating truck load with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update truck fuel level
     * @param id Truck ID
     * @param fuelLevel New fuel level
     * @return Updated truck
     */
    @PatchMapping("/{id}/fuel")
    public ResponseEntity<Truck> updateTruckFuel(@PathVariable Long id, @RequestParam double fuelLevel) {
        try {
            Truck updatedTruck = truckService.updateTruckFuel(id, fuelLevel);
            log.info("Updated truck {} fuel level to: {}", id, fuelLevel);
            return ResponseEntity.ok(updatedTruck);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                log.warn("Truck not found for fuel update with ID: {}", id);
                return ResponseEntity.notFound().build();
            } else {
                log.warn("Fuel level functionality not implemented: {}", e.getMessage());
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error updating truck fuel with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update truck availability status
     * @param id Truck ID
     * @param isAvailable New availability status
     * @return Updated truck
     */
    @PatchMapping("/{id}/availability")
    public ResponseEntity<Truck> updateTruckAvailability(@PathVariable Long id, @RequestParam boolean isAvailable) {
        try {
            Truck updatedTruck = truckService.updateTruckAvailability(id, isAvailable);
            log.info("Updated truck {} availability to: {}", id, isAvailable);
            return ResponseEntity.ok(updatedTruck);
        } catch (RuntimeException e) {
            log.warn("Truck not found for availability update with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating truck availability with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get trucks within a certain radius
     * @param latitude Center latitude
     * @param longitude Center longitude
     * @param radiusKm Radius in kilometers
     * @return List of trucks within the radius
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<Truck>> getTrucksNearby(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radiusKm) {
        try {
            List<Truck> nearbyTrucks = truckService.getTrucksNearby(latitude, longitude, radiusKm);
            log.info("Retrieved {} trucks within {}km radius", nearbyTrucks.size(), radiusKm);
            return ResponseEntity.ok(nearbyTrucks);
        } catch (Exception e) {
            log.error("Error retrieving nearby trucks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get truck count by availability
     * @param isAvailable Availability status
     * @return Count of trucks with specified status
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getTruckCount(@RequestParam boolean isAvailable) {
        try {
            long count = truckService.getTruckCount(isAvailable);
            log.info("Count of trucks with availability {}: {}", isAvailable, count);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error counting trucks by availability: {}", isAvailable, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
