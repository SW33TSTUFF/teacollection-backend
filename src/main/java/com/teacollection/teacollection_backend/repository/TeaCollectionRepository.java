package com.teacollection.teacollection_backend.repository;

import com.teacollection.teacollection_backend.Supplier;
import com.teacollection.teacollection_backend.Truck;
import com.teacollection.teacollection_backend.Route;
import com.teacollection.teacollection_backend.Depot;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Comprehensive repository interface providing system-wide operations and statistics
 * for the Tea Collection Optimization System.
 */
@Repository
public interface TeaCollectionRepository {

    /**
     * Get system statistics overview
     * @return Map containing key system metrics
     */
    @Query("SELECT " +
           "COUNT(DISTINCT s) as totalSuppliers, " +
           "COUNT(DISTINCT t) as totalTrucks, " +
           "COUNT(DISTINCT r) as totalRoutes, " +
           "COUNT(DISTINCT d) as totalDepots " +
           "FROM Supplier s, Truck t, Route r, Depot d")
    Map<String, Object> getSystemStatistics();

    /**
     * Get operational statistics for a specific time period
     * @param startTime Start of time period
     * @param endTime End of time period
     * @return Map containing operational metrics
     */
    @Query("SELECT " +
           "COUNT(r) as routesInPeriod, " +
           "SUM(r.totalWeight) as totalWeightCollected, " +
           "SUM(r.totalDistance) as totalDistanceTraveled, " +
           "AVG(r.totalWeight) as avgRouteWeight, " +
           "AVG(r.totalDistance) as avgRouteDistance " +
           "FROM Route r " +
           "WHERE r.createdAt BETWEEN :startTime AND :endTime")
    Map<String, Object> getOperationalStatistics(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * Get supplier readiness statistics
     * @return Map containing supplier readiness metrics
     */
    @Query("SELECT " +
           "COUNT(s) as totalSuppliers, " +
           "SUM(CASE WHEN s.isReady = true THEN 1 ELSE 0 END) as readySuppliers, " +
           "SUM(CASE WHEN s.isReady = false THEN 1 ELSE 0 END) as notReadySuppliers, " +
           "SUM(s.harvestWeight) as totalHarvestWeight " +
           "FROM Supplier s")
    Map<String, Object> getSupplierReadinessStatistics();

    /**
     * Get truck utilization statistics
     * @return Map containing truck utilization metrics
     */
    @Query("SELECT " +
           "COUNT(t) as totalTrucks, " +
           "SUM(CASE WHEN t.status = 'IDLE' THEN 1 ELSE 0 END) as idleTrucks, " +
           "SUM(CASE WHEN t.status = 'EN_ROUTE' THEN 1 ELSE 0 END) as enRouteTrucks, " +
           "SUM(CASE WHEN t.status = 'RETURNING' THEN 1 ELSE 0 END) as returningTrucks, " +
           "AVG(t.currentLoad / t.maxCapacity) as avgCapacityUtilization " +
           "FROM Truck t")
    Map<String, Object> getTruckUtilizationStatistics();

    /**
     * Get route efficiency statistics
     * @return Map containing route efficiency metrics
     */
    @Query("SELECT " +
           "COUNT(r) as totalRoutes, " +
           "AVG(r.totalWeight / NULLIF(r.totalDistance, 0)) as avgEfficiency, " +
           "MIN(r.totalWeight / NULLIF(r.totalDistance, 0)) as minEfficiency, " +
           "MAX(r.totalWeight / NULLIF(r.totalDistance, 0)) as maxEfficiency " +
           "FROM Route r " +
           "WHERE r.totalDistance > 0")
    Map<String, Object> getRouteEfficiencyStatistics();

    /**
     * Find suppliers that are ready and have sufficient weight for efficient collection
     * @param minWeight Minimum weight threshold for efficient collection
     * @param maxDistance Maximum distance from depot for consideration
     * @return List of suppliers meeting efficiency criteria
     */
    @Query("SELECT s FROM Supplier s " +
           "WHERE s.isReady = true " +
           "AND s.harvestWeight >= :minWeight " +
           "AND EXISTS (SELECT d FROM Depot d " +
           "           WHERE SQRT(POWER(s.latitude - d.latitude, 2) + " +
           "                       POWER(s.longitude - d.longitude, 2)) <= :maxDistance)")
    List<Supplier> findEfficientCollectionSuppliers(
            @Param("minWeight") double minWeight,
            @Param("maxDistance") double maxDistance
    );

    /**
     * Find trucks that are optimal for a given weight and distance
     * @param weight Weight to be transported
     * @param maxDistance Maximum distance for the route
     * @return List of trucks optimal for the given criteria
     */
    @Query("SELECT t FROM Truck t " +
           "WHERE t.maxCapacity >= :weight " +
           "AND t.status = 'IDLE' " +
           "ORDER BY (t.maxCapacity - :weight) ASC, t.currentLoad ASC")
    List<Truck> findOptimalTrucksForWeight(
            @Param("weight") double weight,
            @Param("maxDistance") double maxDistance
    );

    /**
     * Get daily collection summary
     * @param date Date for summary
     * @return Map containing daily collection metrics
     */
    @Query("SELECT " +
           "COUNT(r) as routesCompleted, " +
           "SUM(r.totalWeight) as totalWeightCollected, " +
           "SUM(r.totalDistance) as totalDistanceTraveled, " +
           "COUNT(DISTINCT r.truck) as trucksUsed " +
           "FROM Route r " +
           "WHERE DATE(r.actualEndTime) = DATE(:date) " +
           "AND r.status = 'COMPLETED'")
    Map<String, Object> getDailyCollectionSummary(@Param("date") LocalDateTime date);

    /**
     * Find suppliers that will become available soon and can be grouped efficiently
     * @param currentTime Current time
     * @param timeWindowMinutes Time window in minutes to look ahead
     * @param minGroupWeight Minimum weight for efficient grouping
     * @return List of suppliers that can be grouped efficiently
     */
    @Query("SELECT s FROM Supplier s " +
           "WHERE s.isReady = false " +
           "AND s.availableFrom BETWEEN :currentTime " +
           "AND :currentTime + INTERVAL :timeWindowMinutes MINUTE " +
           "AND s.harvestWeight >= :minGroupWeight " +
           "ORDER BY s.availableFrom ASC")
    List<Supplier> findSuppliersForEfficientGrouping(
            @Param("currentTime") LocalDateTime currentTime,
            @Param("timeWindowMinutes") int timeWindowMinutes,
            @Param("minGroupWeight") double minGroupWeight
    );

    /**
     * Get system health metrics
     * @return Map containing system health indicators
     */
    @Query("SELECT " +
           "COUNT(CASE WHEN t.status = 'IDLE' THEN 1 END) as idleTrucks, " +
           "COUNT(CASE WHEN s.isReady = true THEN 1 END) as readySuppliers, " +
           "COUNT(CASE WHEN r.status = 'IN_PROGRESS' THEN 1 END) as activeRoutes, " +
           "COUNT(CASE WHEN r.status = 'PLANNED' THEN 1 END) as plannedRoutes " +
           "FROM Truck t, Supplier s, Route r")
    Map<String, Object> getSystemHealthMetrics();
}
