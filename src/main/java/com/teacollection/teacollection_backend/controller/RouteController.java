package com.teacollection.teacollection_backend.controller;

import com.teacollection.teacollection_backend.Route;
import com.teacollection.teacollection_backend.service.RouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Route management operations.
 * Handles CRUD operations, status updates, and route queries.
 */
@RestController
@RequestMapping({"/api/routes", "/api/routes/"})
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RouteController {

    private final RouteService routeService;

    /**
     * Get all routes
     * @return List of all routes
     */
    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes() {
        try {
            List<Route> routes = routeService.getAllRoutes();
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
            Route route = routeService.getRouteById(id);
            log.info("Retrieved route with ID: {}", id);
            return ResponseEntity.ok(route);
        } catch (RuntimeException e) {
            log.warn("Route not found with ID: {}", id);
            return ResponseEntity.notFound().build();
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
            Route savedRoute = routeService.createRoute(route);
            log.info("Created new route with ID: {}", savedRoute.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRoute);
        } catch (RuntimeException e) {
            log.warn("Validation error creating route: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
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
            Route updatedRoute = routeService.updateRoute(id, route);
            log.info("Updated route with ID: {}", id);
            return ResponseEntity.ok(updatedRoute);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                log.warn("Route not found for update with ID: {}", id);
                return ResponseEntity.notFound().build();
            } else {
                log.warn("Validation error updating route: {}", e.getMessage());
                return ResponseEntity.badRequest().build();
            }
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
            routeService.deleteRoute(id);
            log.info("Deleted route with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.warn("Route not found for deletion with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting route with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get routes by status
     * @param status Route status (PLANNED, IN_PROGRESS, COMPLETED, CANCELLED, FAILED)
     * @return List of routes with specified status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Route>> getRoutesByStatus(@PathVariable String status) {
        try {
            List<Route> routes = routeService.getRoutesByStatus(status);
            log.info("Retrieved {} routes with status: {}", routes.size(), status);
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            log.error("Error retrieving routes by status: {}", status, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get routes for a specific truck
     * @param truckId Truck ID
     * @return List of routes for the truck
     */
    @GetMapping("/truck/{truckId}")
    public ResponseEntity<List<Route>> getRoutesByTruck(@PathVariable Long truckId) {
        try {
            List<Route> routes = routeService.getRoutesByTruck(truckId);
            log.info("Retrieved {} routes for truck ID: {}", routes.size(), truckId);
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            log.error("Error retrieving routes for truck ID: {}", truckId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get routes for a specific depot
     * @param depotId Depot ID
     * @return List of routes for the depot
     */
    @GetMapping("/depot/{depotId}")
    public ResponseEntity<List<Route>> getRoutesByDepot(@PathVariable Long depotId) {
        try {
            List<Route> routes = routeService.getRoutesByDepot(depotId);
            log.info("Retrieved {} routes for depot ID: {}", routes.size(), depotId);
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            log.error("Error retrieving routes for depot ID: {}", depotId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update route status
     * @param id Route ID
     * @param status New route status
     * @return Updated route
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Route> updateRouteStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Route updatedRoute = routeService.updateRouteStatus(id, status);
            log.info("Updated route {} status to: {}", id, status);
            return ResponseEntity.ok(updatedRoute);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                log.warn("Route not found for status update with ID: {}", id);
                return ResponseEntity.notFound().build();
            } else {
                log.warn("Validation error updating route status: {}", e.getMessage());
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error updating route status with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get routes for a specific date
     * @param date Date in YYYY-MM-DD format
     * @return List of routes for the date
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<List<Route>> getRoutesByDate(@PathVariable String date) {
        try {
            List<Route> routes = routeService.getRoutesByDate(date);
            log.info("Retrieved {} routes for date: {}", routes.size(), date);
            return ResponseEntity.ok(routes);
        } catch (RuntimeException e) {
            log.warn("Invalid date format: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error retrieving routes for date: {}", date, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Trigger route optimization
     * @return Optimization result
     */
    @GetMapping("/optimization/trigger")
    public ResponseEntity<String> triggerRouteOptimization() {
        try {
            routeService.triggerRouteOptimization();
            log.info("Route optimization triggered successfully");
            return ResponseEntity.ok("Route optimization triggered successfully");
        } catch (Exception e) {
            log.error("Error triggering route optimization", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
