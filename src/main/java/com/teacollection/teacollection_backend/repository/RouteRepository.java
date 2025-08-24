package com.teacollection.teacollection_backend.repository;

import com.teacollection.teacollection_backend.Route;
import com.teacollection.teacollection_backend.Truck;
import com.teacollection.teacollection_backend.Supplier;
import com.teacollection.teacollection_backend.Depot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Route entity operations.
 * Provides CRUD operations and custom queries for route management.
 */
@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    /**
     * Find all routes by status
     * @param status Route status
     * @return List of routes with the specified status
     */
    List<Route> findByStatus(Route.RouteStatus status);

    /**
     * Find routes by truck
     * @param truck Truck entity
     * @return List of routes assigned to the truck
     */
    List<Route> findByTruck(Truck truck);

    /**
     * Find routes by truck ID
     * @param truckId Truck ID
     * @return List of routes assigned to the truck
     */
    List<Route> findByTruckId(Long truckId);

    /**
     * Find active routes (PLANNED or IN_PROGRESS)
     * @return List of active routes
     */
    @Query("SELECT r FROM Route r WHERE r.status IN ('PLANNED', 'IN_PROGRESS')")
    List<Route> findActiveRoutes();

    /**
     * Find routes by depot
     * @param depot Depot entity
     * @return List of routes starting from the depot
     */
    List<Route> findByDepot(Depot depot);

    /**
     * Find routes that contain a specific supplier
     * @param supplier Supplier entity
     * @return List of routes containing the supplier
     */
    @Query("SELECT r FROM Route r JOIN r.supplierSequence s WHERE s = :supplier")
    List<Route> findBySupplier(@Param("supplier") Supplier supplier);

    /**
     * Find routes by supplier ID
     * @param supplierId Supplier ID
     * @return List of routes containing the supplier
     */
    @Query("SELECT r FROM Route r JOIN r.supplierSequence s WHERE s.id = :supplierId")
    List<Route> findBySupplierId(@Param("supplierId") Long supplierId);

    /**
     * Find routes by estimated start time range
     * @param startTime Start of time range
     * @param endTime End of time range
     * @return List of routes in the time range
     */
    List<Route> findByEstimatedStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Find routes by total weight range
     * @param minWeight Minimum total weight
     * @param maxWeight Maximum total weight
     * @return List of routes within the weight range
     */
    List<Route> findByTotalWeightBetween(double minWeight, double maxWeight);

    /**
     * Find routes by total distance range
     * @param minDistance Minimum total distance
     * @param maxDistance Maximum total distance
     * @return List of routes within the distance range
     */
    List<Route> findByTotalDistanceBetween(double minDistance, double maxDistance);

    /**
     * Find routes that can accommodate additional weight
     * @param additionalWeight Additional weight to be accommodated
     * @return List of routes that can carry additional weight
     */
    @Query("SELECT r FROM Route r WHERE (r.truck.maxCapacity - r.totalWeight) >= :additionalWeight")
    List<Route> findRoutesWithAvailableCapacity(@Param("additionalWeight") double additionalWeight);

    /**
     * Find routes by truck and status
     * @param truck Truck entity
     * @param status Route status
     * @return List of routes matching both criteria
     */
    List<Route> findByTruckAndStatus(Truck truck, Route.RouteStatus status);

    /**
     * Find routes by truck ID and status
     * @param truckId Truck ID
     * @param status Route status
     * @return List of routes matching both criteria
     */
    List<Route> findByTruckIdAndStatus(Long truckId, Route.RouteStatus status);

    /**
     * Find routes that are ready to start (PLANNED status and past estimated start time)
     * @param currentTime Current time
     * @return List of routes ready to start
     */
    @Query("SELECT r FROM Route r WHERE r.status = 'PLANNED' AND r.estimatedStartTime <= :currentTime")
    List<Route> findRoutesReadyToStart(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Find routes that are overdue (IN_PROGRESS and past estimated end time)
     * @param currentTime Current time
     * @return List of overdue routes
     */
    @Query("SELECT r FROM Route r WHERE r.status = 'IN_PROGRESS' AND r.estimatedEndTime < :currentTime")
    List<Route> findOverdueRoutes(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Count routes by status
     * @param status Route status
     * @return Count of routes with the specified status
     */
    long countByStatus(Route.RouteStatus status);

    /**
     * Count routes by truck
     * @param truck Truck entity
     * @return Count of routes assigned to the truck
     */
    long countByTruck(Truck truck);

    /**
     * Find routes created within a time period
     * @param startTime Start of time period
     * @param endTime End of time period
     * @return List of routes created in the time period
     */
    List<Route> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Find routes with the highest total weight
     * @param limit Maximum number of routes to return
     * @return List of routes ordered by total weight (descending)
     */
    @Query("SELECT r FROM Route r ORDER BY r.totalWeight DESC")
    List<Route> findTopRoutesByWeight(@Param("limit") int limit);

    /**
     * Find routes with the longest total distance
     * @param limit Maximum number of routes to return
     * @return List of routes ordered by total distance (descending)
     */
    @Query("SELECT r FROM Route r ORDER BY r.totalDistance DESC")
    List<Route> findTopRoutesByDistance(@Param("limit") int limit);

    /**
     * Find routes that need optimization (PLANNED routes with low efficiency)
     * @param efficiencyThreshold Efficiency threshold (weight/distance ratio)
     * @return List of routes that need optimization
     */
    @Query("SELECT r FROM Route r WHERE r.status = 'PLANNED' " +
           "AND (r.totalWeight / NULLIF(r.totalDistance, 0)) < :efficiencyThreshold")
    List<Route> findRoutesNeedingOptimization(@Param("efficiencyThreshold") double efficiencyThreshold);
}
