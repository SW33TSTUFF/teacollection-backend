package com.teacollection.teacollection_backend.controller;

import com.teacollection.teacollection_backend.Supplier;
import com.teacollection.teacollection_backend.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Supplier management operations.
 * Handles CRUD operations, availability updates, and supplier queries.
 */
@RestController
@RequestMapping({"/api/suppliers", "/api/suppliers/"})
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class SupplierController {

    private final SupplierRepository supplierRepository;

    /**
     * Get all suppliers
     * @return List of all suppliers
     */
    @GetMapping
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        try {
            List<Supplier> suppliers = supplierRepository.findAll();
            log.info("Retrieved {} suppliers", suppliers.size());
            return ResponseEntity.ok(suppliers);
        } catch (Exception e) {
            log.error("Error retrieving suppliers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get supplier by ID
     * @param id Supplier ID
     * @return Supplier if found, 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long id) {
        try {
            Optional<Supplier> supplier = supplierRepository.findById(id);
            if (supplier.isPresent()) {
                log.info("Retrieved supplier with ID: {}", id);
                return ResponseEntity.ok(supplier.get());
            } else {
                log.warn("Supplier not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error retrieving supplier with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a new supplier
     * @param supplier Supplier data
     * @return Created supplier with generated ID
     */
    @PostMapping
    public ResponseEntity<Supplier> createSupplier(@RequestBody Supplier supplier) {
        try {
            // Set default values if not provided
            if (supplier.getAvailableFrom() == null) {
                supplier.setAvailableFrom(LocalDateTime.now());
            }
            if (supplier.getAvailableUntil() == null) {
                supplier.setAvailableUntil(LocalDateTime.now().plusHours(8)); // Default 8-hour window
            }
            
            Supplier savedSupplier = supplierRepository.save(supplier);
            log.info("Created new supplier with ID: {}", savedSupplier.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedSupplier);
        } catch (Exception e) {
            log.error("Error creating supplier", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update an existing supplier
     * @param id Supplier ID
     * @param supplier Updated supplier data
     * @return Updated supplier
     */
    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplier) {
        try {
            if (!supplierRepository.existsById(id)) {
                log.warn("Supplier not found for update with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            supplier.setId(id);
            Supplier updatedSupplier = supplierRepository.save(supplier);
            log.info("Updated supplier with ID: {}", id);
            return ResponseEntity.ok(updatedSupplier);
        } catch (Exception e) {
            log.error("Error updating supplier with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a supplier
     * @param id Supplier ID
     * @return 204 No Content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        try {
            if (!supplierRepository.existsById(id)) {
                log.warn("Supplier not found for deletion with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            supplierRepository.deleteById(id);
            log.info("Deleted supplier with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting supplier with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all ready suppliers
     * @return List of suppliers ready for collection
     */
    @GetMapping("/ready")
    public ResponseEntity<List<Supplier>> getReadySuppliers() {
        try {
            List<Supplier> readySuppliers = supplierRepository.findByIsReadyTrue();
            log.info("Retrieved {} ready suppliers", readySuppliers.size());
            return ResponseEntity.ok(readySuppliers);
        } catch (Exception e) {
            log.error("Error retrieving ready suppliers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get suppliers by availability status
     * @param isReady Ready status
     * @return List of suppliers with specified status
     */
    @GetMapping("/status/{isReady}")
    public ResponseEntity<List<Supplier>> getSuppliersByStatus(@PathVariable boolean isReady) {
        try {
            List<Supplier> suppliers = supplierRepository.findSuppliersByStatusAndAvailability(isReady, LocalDateTime.now());
            log.info("Retrieved {} suppliers with status: {}", suppliers.size(), isReady);
            return ResponseEntity.ok(suppliers);
        } catch (Exception e) {
            log.error("Error retrieving suppliers by status: {}", isReady, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get suppliers within a certain radius
     * @param latitude Center latitude
     * @param longitude Center longitude
     * @param radiusKm Radius in kilometers
     * @return List of suppliers within the radius
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<Supplier>> getSuppliersNearby(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radiusKm) {
        try {
            List<Supplier> nearbySuppliers = supplierRepository.findSuppliersWithinRadius(latitude, longitude, radiusKm);
            log.info("Retrieved {} suppliers within {}km radius", nearbySuppliers.size(), radiusKm);
            return ResponseEntity.ok(nearbySuppliers);
        } catch (Exception e) {
            log.error("Error retrieving nearby suppliers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get suppliers with harvest weight above threshold
     * @param minWeight Minimum harvest weight
     * @return List of suppliers meeting weight criteria
     */
    @GetMapping("/weight")
    public ResponseEntity<List<Supplier>> getSuppliersByWeight(@RequestParam double minWeight) {
        try {
            List<Supplier> suppliers = supplierRepository.findByHarvestWeightGreaterThanEqual(minWeight);
            log.info("Retrieved {} suppliers with weight >= {}", suppliers.size(), minWeight);
            return ResponseEntity.ok(suppliers);
        } catch (Exception e) {
            log.error("Error retrieving suppliers by weight", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update supplier readiness status
     * @param id Supplier ID
     * @param isReady New readiness status
     * @return Updated supplier
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Supplier> updateSupplierStatus(@PathVariable Long id, @RequestParam boolean isReady) {
        try {
            Optional<Supplier> supplierOpt = supplierRepository.findById(id);
            if (supplierOpt.isEmpty()) {
                log.warn("Supplier not found for status update with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            Supplier supplier = supplierOpt.get();
            supplier.setReady(isReady);
            Supplier updatedSupplier = supplierRepository.save(supplier);
            
            log.info("Updated supplier {} status to: {}", id, isReady);
            return ResponseEntity.ok(updatedSupplier);
        } catch (Exception e) {
            log.error("Error updating supplier status with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get suppliers becoming available soon
     * @param minutesAhead Minutes ahead to look for availability
     * @return List of suppliers becoming available soon
     */
    @GetMapping("/becoming-available")
    public ResponseEntity<List<Supplier>> getSuppliersBecomingAvailable(@RequestParam(defaultValue = "30") int minutesAhead) {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime futureTime = currentTime.plusMinutes(minutesAhead);
            
            List<Supplier> suppliers = supplierRepository.findSuppliersBecomingAvailableSoon(currentTime, futureTime);
            log.info("Retrieved {} suppliers becoming available within {} minutes", suppliers.size(), minutesAhead);
            return ResponseEntity.ok(suppliers);
        } catch (Exception e) {
            log.error("Error retrieving suppliers becoming available", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get supplier count by status
     * @param isReady Ready status
     * @return Count of suppliers with specified status
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getSupplierCount(@RequestParam boolean isReady) {
        try {
            long count = supplierRepository.countByIsReady(isReady);
            log.info("Count of suppliers with status {}: {}", isReady, count);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error counting suppliers by status: {}", isReady, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get suppliers available in a time window
     * @param startTime Start of availability window
     * @param endTime End of availability window
     * @return List of available suppliers in the time window
     */
    @GetMapping("/available-in-window")
    public ResponseEntity<List<Supplier>> getSuppliersAvailableInWindow(
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {
        try {
            List<Supplier> suppliers = supplierRepository.findAvailableSuppliersInTimeWindow(startTime, endTime);
            log.info("Retrieved {} suppliers available in time window", suppliers.size());
            return ResponseEntity.ok(suppliers);
        } catch (Exception e) {
            log.error("Error retrieving suppliers in time window", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
