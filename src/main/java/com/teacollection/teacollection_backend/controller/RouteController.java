package com.teacollection.teacollection_backend.controller;

import com.teacollection.teacollection_backend.Route;
import com.teacollection.teacollection_backend.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Route management operations.
 * Handles CRUD operations, status updates, and route optimization.
 */
@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RouteController {

    private final RouteRepository routeRepository;

    /**
     * Get all routes
     * @return List of all routes
     */
    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes() {
        try {
            List<Route> routes = routeRepository.findAll();
            log.info("Retrieved {} routes", routes.size());
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            log.error("Error retrieving routes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get route by ID
     * @param id Route ID
     * @return Route if found, 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable Long id) {
        try {
            Optional<Route> route = routeRepository.findById(id);
            if (route.isPresent()) {
                log.info("Retrieved route with ID: {}", id);
                return ResponseEntity.ok(route.get());
            } else {
                log.warn("Route not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error retrieving route with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a new route
     * @param route Route data
     * @return Created route with generated ID
     */
    @PostMapping
    public ResponseEntity<Route> createRoute(@RequestBody Route route) {
        try {
            // Set default values if not provided
            if (route.getStatus() == null) {
                route.setStatus(Route.RouteStatus.PLANNED);
            }
            if (route.getTotalWeight() == 0.0) {
                route.setTotalWeight(0.0);
            }
            if (route.getTotalDistance() == 0.0) {
                route.setTotalDistance(0.0);
            }
            
            Route savedRoute = routeRepository.save(route);
            log.info("Created new route with ID: {}", savedRoute.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRoute);
        } catch (Exception e) {
            log.error("Error creating route", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update an existing route
     * @param id Route ID
     * @param route Updated route data
     * @return Updated route
     */
    @PutMapping("/{id}")
    public ResponseEntity<Route> updateRoute(@PathVariable Long id, @RequestBody Route route) {
        try {
            if (!routeRepository.existsById(id)) {
                log.warn("Route not found for update with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            route.setId(id);
            Route updatedRoute = routeRepository.save(route);
            log.info("Updated route with ID: {}", id);
            return ResponseEntity.ok(updatedRoute);
        } catch (Exception e) {
            log.error("Error updating route with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a route
     * @param id Route ID
     * @return 204 No Content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        try {
            if (!routeRepository.existsById(id)) {
                log.warn("Route not found for deletion with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            routeRepository.deleteById(id);
            log.info("Deleted route with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting route with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get routes by status
     * @param status Route status
     * @return List of routes with specified status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Route>> getRoutesByStatus(@PathVariable Route.RouteStatus status) {
        try {
            List<Route> routes = routeRepository.findByStatus(status);
            log.info("Retrieved {} routes with status: {}", routes.size(), status);
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            log.error("Error retrieving routes by status: {}", status, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get active routes (PLANNED or IN_PROGRESS)
     * @return List of active routes
     */
    @GetMapping("/active")
    public ResponseEntity<List<Route>> getActiveRoutes() {
        try {
            List<Route> activeRoutes = routeRepository.findActiveRoutes();
            log.info("Retrieved {} active routes", activeRoutes.size());
            return ResponseEntity.ok(activeRoutes);
        } catch (Exception e) {
            log.error("Error retrieving active routes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get routes by truck ID
     * @param truckId Truck ID
     * @return List of routes assigned to the truck
     */
    @GetMapping("/truck/{truckId}")
    public ResponseEntity<List<Route>> getRoutesByTruckId(@PathVariable Long truckId) {
        try {
            List<Route> routes = routeRepository.findByTruckId(truckId);
            log.info("Retrieved {} routes for truck ID: {}", routes.size(), truckId);
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            log.error("Error retrieving routes by truck ID: {}", truckId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get routes by supplier ID
     * @param supplierId Supplier ID
     * @return List of routes containing the supplier
     */
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<Route>> getRoutesBySupplierId(@PathVariable Long supplierId) {
        try {
            List<Route> routes = routeRepository.findBySupplierId(supplierId);
            log.info("Retrieved {} routes containing supplier ID: {}", routes.size(), supplierId);
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            log.error("Error retrieving routes by supplier ID: {}", supplierId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get routes ready to start
     * @return List of routes ready to start
     */
    @GetMapping("/ready-to-start")
    public ResponseEntity<List<Route>> getRoutesReadyToStart() {
        try {
            List<Route> routes = routeRepository.findRoutesReadyToStart(LocalDateTime.now());
            log.info("Retrieved {} routes ready to start", routes.size());
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            log.error("Error retrieving routes ready to start", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get overdue routes
     * @return List of overdue routes
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<Route>> getOverdueRoutes() {
        try {
            List<Route> routes = routeRepository.findOverdueRoutes(LocalDateTime.now());
            log.info("Retrieved {} overdue routes", routes.size());
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            log.error("Error retrieving overdue routes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update route status
     * @param id Route ID
     * @param status New status
     * @return Updated route
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Route> updateRouteStatus(@PathVariable Long id, @RequestParam Route.RouteStatus status) {
        try {
            Optional<Route> routeOpt = routeRepository.findById(id);
            if (routeOpt.isEmpty()) {
                log.warn("Route not found for status update with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            Route route = routeOpt.get();
            route.setStatus(status);
            
            // Set actual start/end times based on status
            if (status == Route.RouteStatus.IN_PROGRESS && route.getActualStartTime() == null) {
                route.setActualStartTime(LocalDateTime.now());
            } else if (status == Route.RouteStatus.COMPLETED && route.getActualEndTime() == null) {
                route.setActualEndTime(LocalDateTime.now());
            }
            
            Route updatedRoute = routeRepository.save(route);
            log.info("Updated route {} status to: {}", id, status);
            return ResponseEntity.ok(updatedRoute);
        } catch (Exception e) {
            log.error("Error updating route status with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get routes by weight range
     * @param minWeight Minimum total weight
     * @param maxWeight Maximum total weight
     * @return List of routes within the weight range
     */
    @GetMapping("/weight-range")
    public ResponseEntity<List<Route>> getRoutesByWeightRange(
            @RequestParam double minWeight,
            @RequestParam double maxWeight) {
        try {
            List<Route> routes = routeRepository.findByTotalWeightBetween(minWeight, maxWeight);
            log.info("Retrieved {} routes with weight between {} and {}", routes.size(), minWeight, maxWeight);
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            log.error("Error retrieving routes by weight range", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get routes by distance range
     * @param minDistance Minimum total distance
     * @param maxDistance Maximum total distance
     * @return List of routes within the distance range
     */
    @GetMapping("/distance-range")
    public ResponseEntity<List<Route>> getRoutesByDistanceRange(
            @RequestParam double minDistance,
            @RequestParam double maxDistance) {
        try {
            List<Route> routes = routeRepository.findByTotalDistanceBetween(minDistance, maxDistance);
            log.info("Retrieved {} routes with distance between {} and {}", routes.size(), minDistance, maxDistance);
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            log.error("Error retrieving routes by distance range", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get routes that can accommodate additional weight
     * @param additionalWeight Additional weight to be accommodated
     * @return List of routes that can carry additional weight
     */
    @GetMapping("/can-accommodate-weight")
    public ResponseEntity<List<Route>> getRoutesWithAvailableCapacity(@RequestParam double additionalWeight) {
        try {
            List<Route> routes = routeRepository.findRoutesWithAvailableCapacity(additionalWeight);
            log.info("Retrieved {} routes that can accommodate weight: {}", routes.size(), additionalWeight);
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            log.error("Error retrieving routes by available capacity", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get route count by status
     * @param status Route status
     * @return Count of routes with specified status
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getRouteCount(@RequestParam Route.RouteStatus status) {
        try {
            long count = routeRepository.countByStatus(status);
            log.info("Count of routes with status {}: {}", status, count);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error counting routes by status: {}", status, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get top routes by weight
     * @param limit Maximum number of routes to return
     * @return List of routes ordered by total weight (descending)
     */
    @GetMapping("/top-by-weight")
    public ResponseEntity<List<Route>> getTopRoutesByWeight(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<Route> routes = routeRepository.findTopRoutesByWeight(limit);
            log.info("Retrieved top {} routes by weight", routes.size());
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            log.error("Error retrieving top routes by weight", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get top routes by distance
     * @param limit Maximum number of routes to return
     * @return List of routes ordered by total distance (descending)
     */
    @GetMapping("/top-by-distance")
    public ResponseEntity<List<Route>> getTopRoutesByDistance(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<Route> routes = routeRepository.findTopRoutesByDistance(limit);
            log.info("Retrieved top {} routes by distance", routes.size());
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            log.error("Error retrieving top routes by distance", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get routes that need optimization
     * @param efficiencyThreshold Efficiency threshold (weight/distance ratio)
     * @return List of routes that need optimization
     */
    @GetMapping("/needing-optimization")
    public ResponseEntity<List<Route>> getRoutesNeedingOptimization(@RequestParam(defaultValue = "0.5") double efficiencyThreshold) {
        try {
            List<Route> routes = routeRepository.findRoutesNeedingOptimization(efficiencyThreshold);
            log.info("Retrieved {} routes needing optimization (threshold: {})", routes.size(), efficiencyThreshold);
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            log.error("Error retrieving routes needing optimization", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
