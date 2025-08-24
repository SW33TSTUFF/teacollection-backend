package com.teacollection.teacollection_backend.repository;

import com.teacollection.teacollection_backend.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Supplier entity operations.
 * Provides CRUD operations and custom queries for supplier management.
 */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    /**
     * Find all suppliers that are ready for collection
     * @return List of ready suppliers
     */
    List<Supplier> findByIsReadyTrue();

    /**
     * Find suppliers by availability status within a time range
     * @param startTime Start of availability window
     * @param endTime End of availability window
     * @return List of available suppliers in the time window
     */
    @Query("SELECT s FROM Supplier s WHERE s.availableFrom <= :endTime AND s.availableUntil >= :startTime")
    List<Supplier> findAvailableSuppliersInTimeWindow(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * Find suppliers by location within a certain radius (approximate)
     * @param latitude Center latitude
     * @param longitude Center longitude
     * @param radiusKm Radius in kilometers
     * @return List of suppliers within the radius
     */
    @Query("SELECT s FROM Supplier s WHERE " +
           "SQRT(POWER(s.latitude - :latitude, 2) + POWER(s.longitude - :longitude, 2)) <= :radiusKm")
    List<Supplier> findSuppliersWithinRadius(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radiusKm") double radiusKm
    );

    /**
     * Find suppliers with harvest weight above a certain threshold
     * @param minWeight Minimum harvest weight
     * @return List of suppliers with sufficient harvest weight
     */
    List<Supplier> findByHarvestWeightGreaterThanEqual(double minWeight);

    /**
     * Find suppliers that are ready and have harvest weight above threshold
     * @param minWeight Minimum harvest weight
     * @return List of ready suppliers with sufficient weight
     */
    List<Supplier> findByIsReadyTrueAndHarvestWeightGreaterThanEqual(double minWeight);

    /**
     * Find suppliers by status and availability time
     * @param isReady Ready status
     * @param currentTime Current time to check availability
     * @return List of suppliers matching criteria
     */
    @Query("SELECT s FROM Supplier s WHERE s.isReady = :isReady " +
           "AND s.availableFrom <= :currentTime AND s.availableUntil >= :currentTime")
    List<Supplier> findSuppliersByStatusAndAvailability(
            @Param("isReady") boolean isReady,
            @Param("currentTime") LocalDateTime currentTime
    );

    /**
     * Count suppliers by ready status
     * @param isReady Ready status
     * @return Count of suppliers with the specified status
     */
    long countByIsReady(boolean isReady);

    /**
     * Find suppliers that will become available soon (within specified minutes)
     * @param currentTime Current time
     * @param minutesAhead Minutes ahead to look for availability
     * @return List of suppliers becoming available soon
     */
    @Query("SELECT s FROM Supplier s WHERE s.isReady = false " +
           "AND s.availableFrom BETWEEN :currentTime AND :futureTime")
    List<Supplier> findSuppliersBecomingAvailableSoon(
            @Param("currentTime") LocalDateTime currentTime,
            @Param("futureTime") LocalDateTime futureTime
    );
}
