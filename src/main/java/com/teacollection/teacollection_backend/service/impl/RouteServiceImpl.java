package com.teacollection.teacollection_backend.service.impl;

import com.teacollection.teacollection_backend.Route;
import com.teacollection.teacollection_backend.repository.RouteRepository;
import com.teacollection.teacollection_backend.service.RouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Route> getAllRoutes() {
        log.info("Retrieving all routes");
        return routeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Route getRouteById(Long id) {
        log.info("Retrieving route with ID: {}", id);
        return routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found with ID: " + id));
    }

    @Override
    public Route createRoute(Route route) {
        log.info("Creating new route");
        
        // Validate route data
        if (route.getTruck() == null) {
            throw new RuntimeException("Route must have a truck assigned");
        }
        
        if (route.getDepot() == null) {
            throw new RuntimeException("Route must have a depot assigned");
        }
        
        if (route.getSupplierSequence() == null || route.getSupplierSequence().isEmpty()) {
            throw new RuntimeException("Route must have at least one supplier");
        }
        
        if (route.getTotalDistance() < 0) {
            throw new RuntimeException("Total distance cannot be negative");
        }
        
        if (route.getTotalWeight() < 0) {
            throw new RuntimeException("Total weight cannot be negative");
        }
        
        // Set default values
        if (route.getStatus() == null) {
            route.setStatus(Route.RouteStatus.PLANNED);
        }
        
        if (route.getEstimatedStartTime() == null) {
            route.setEstimatedStartTime(LocalDateTime.now().plusHours(1)); // Default: start in 1 hour
        }
        
        if (route.getEstimatedEndTime() == null && route.getEstimatedStartTime() != null) {
            route.setEstimatedEndTime(route.getEstimatedStartTime().plusHours(4)); // Default: 4-hour route
        }
        
        Route savedRoute = routeRepository.save(route);
        log.info("Created route with ID: {}", savedRoute.getId());
        return savedRoute;
    }

    @Override
    public Route updateRoute(Long id, Route route) {
        log.info("Updating route with ID: {}", id);
        
        if (!routeRepository.existsById(id)) {
            throw new RuntimeException("Route not found with ID: " + id);
        }
        
        // Validate route data
        if (route.getTruck() == null) {
            throw new RuntimeException("Route must have a truck assigned");
        }
        
        if (route.getDepot() == null) {
            throw new RuntimeException("Route must have a depot assigned");
        }
        
        if (route.getSupplierSequence() == null || route.getSupplierSequence().isEmpty()) {
            throw new RuntimeException("Route must have at least one supplier");
        }
        
        if (route.getTotalDistance() < 0) {
            throw new RuntimeException("Total distance cannot be negative");
        }
        
        if (route.getTotalWeight() < 0) {
            throw new RuntimeException("Total weight cannot be negative");
        }
        
        route.setId(id);
        Route updatedRoute = routeRepository.save(route);
        log.info("Updated route with ID: {}", id);
        return updatedRoute;
    }

    @Override
    public void deleteRoute(Long id) {
        log.info("Deleting route with ID: {}", id);
        
        if (!routeRepository.existsById(id)) {
            throw new RuntimeException("Route not found with ID: " + id);
        }
        
        routeRepository.deleteById(id);
        log.info("Deleted route with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Route> getRoutesByStatus(String status) {
        log.info("Retrieving routes with status: {}", status);
        try {
            Route.RouteStatus routeStatus = Route.RouteStatus.valueOf(status.toUpperCase());
            return routeRepository.findByStatus(routeStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid route status: " + status);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Route> getRoutesByTruck(Long truckId) {
        log.info("Retrieving routes for truck ID: {}", truckId);
        return routeRepository.findByTruckId(truckId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Route> getRoutesByDepot(Long depotId) {
        log.info("Retrieving routes for depot ID: {}", depotId);
        // Use findByCreatedAtBetween as a workaround since findByDepotId doesn't exist
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        return routeRepository.findByCreatedAtBetween(startOfDay, endOfDay);
    }

    @Override
    public Route updateRouteStatus(Long id, String status) {
        log.info("Updating route {} status to: {}", id, status);
        
        Route route = getRouteById(id);
        
        try {
            Route.RouteStatus newStatus = Route.RouteStatus.valueOf(status.toUpperCase());
            route.setStatus(newStatus);
            
            // Update timestamps based on status
            if (newStatus == Route.RouteStatus.IN_PROGRESS && route.getActualStartTime() == null) {
                route.setActualStartTime(LocalDateTime.now());
            } else if (newStatus == Route.RouteStatus.COMPLETED && route.getActualEndTime() == null) {
                route.setActualEndTime(LocalDateTime.now());
            }
            
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid route status: " + status);
        }
        
        Route updatedRoute = routeRepository.save(route);
        log.info("Updated route {} status to: {}", id, status);
        return updatedRoute;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Route> getRoutesByDate(String date) {
        log.info("Retrieving routes for date: {}", date);
        
        try {
            LocalDate targetDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDateTime startOfDay = targetDate.atStartOfDay();
            LocalDateTime endOfDay = targetDate.atTime(23, 59, 59);
            
            return routeRepository.findByCreatedAtBetween(startOfDay, endOfDay);
        } catch (Exception e) {
            throw new RuntimeException("Invalid date format. Use YYYY-MM-DD");
        }
    }

    @Override
    public void triggerRouteOptimization() {
        log.info("Triggering route optimization");
        
        // This would typically integrate with OptaPlanner or another optimization engine
        // For now, we'll just log the action
        log.info("Route optimization triggered - this would integrate with OptaPlanner");
        
        // TODO: Implement actual optimization logic
        // 1. Get all available trucks
        // 2. Get all ready suppliers
        // 3. Get depot information
        // 4. Run optimization algorithm
        // 5. Create optimized routes
        // 6. Save results
    }
}
