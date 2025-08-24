package com.teacollection.teacollection_backend.repository;

import com.teacollection.teacollection_backend.Truck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Truck entity operations.
 * Provides CRUD operations and custom queries for truck management.
 */
@Repository
public interface TruckRepository extends JpaRepository<Truck, Long> {

    /**
     * Find all trucks by status
     * @param status Truck status (IDLE, EN_ROUTE, RETURNING)
     * @return List of trucks with the specified status
     */
    List<Truck> findByStatus(String status);

    /**
     * Find all idle trucks available for assignment
     * @return List of idle trucks
     */
    default List<Truck> findIdleTrucks() {
        return findByStatus("IDLE");
    }

    /**
     * Find trucks with available capacity above a certain threshold
     * @param minAvailableCapacity Minimum available capacity required
     * @return List of trucks with sufficient available capacity
     */
    @Query("SELECT t FROM Truck t WHERE (t.maxCapacity - t.currentLoad) >= :minAvailableCapacity")
    List<Truck> findTrucksWithAvailableCapacity(@Param("minAvailableCapacity") double minAvailableCapacity);

    /**
     * Find trucks by capacity range
     * @param minCapacity Minimum capacity
     * @param maxCapacity Maximum capacity
     * @return List of trucks within the capacity range
     */
    List<Truck> findByMaxCapacityBetween(double minCapacity, double maxCapacity);

    /**
     * Find trucks that are underloaded (below a certain percentage of capacity)
     * @param capacityPercentage Percentage of capacity threshold (0.0 to 1.0)
     * @return List of underloaded trucks
     */
    @Query("SELECT t FROM Truck t WHERE (t.currentLoad / t.maxCapacity) < :capacityPercentage")
    List<Truck> findUnderloadedTrucks(@Param("capacityPercentage") double capacityPercentage);

    /**
     * Find trucks that are near capacity (above a certain percentage)
     * @param capacityPercentage Percentage of capacity threshold (0.0 to 1.0)
     * @return List of trucks near capacity
     */
    @Query("SELECT t FROM Truck t WHERE (t.currentLoad / t.maxCapacity) >= :capacityPercentage")
    List<Truck> findTrucksNearCapacity(@Param("capacityPercentage") double capacityPercentage);

    /**
     * Find trucks by status and available capacity
     * @param status Truck status
     * @param minAvailableCapacity Minimum available capacity
     * @return List of trucks matching both criteria
     */
    @Query("SELECT t FROM Truck t WHERE t.status = :status " +
           "AND (t.maxCapacity - t.currentLoad) >= :minAvailableCapacity")
    List<Truck> findByStatusAndAvailableCapacity(
            @Param("status") String status,
            @Param("minAvailableCapacity") double minAvailableCapacity
    );

    /**
     * Count trucks by status
     * @param status Truck status
     * @return Count of trucks with the specified status
     */
    long countByStatus(String status);

    /**
     * Find trucks that can accommodate a specific weight
     * @param weight Weight to be accommodated
     * @return List of trucks that can carry the specified weight
     */
    @Query("SELECT t FROM Truck t WHERE (t.maxCapacity - t.currentLoad) >= :weight")
    List<Truck> findTrucksThatCanAccommodateWeight(@Param("weight") double weight);

    /**
     * Find trucks by current load range
     * @param minLoad Minimum current load
     * @param maxLoad Maximum current load
     * @return List of trucks within the load range
     */
    @Query("SELECT t FROM Truck t WHERE t.currentLoad BETWEEN :minLoad AND :maxLoad")
    List<Truck> findByCurrentLoadBetween(
            @Param("minLoad") double minLoad,
            @Param("maxLoad") double maxLoad
    );

    /**
     * Find trucks that are fully loaded
     * @return List of fully loaded trucks
     */
    @Query("SELECT t FROM Truck t WHERE t.currentLoad >= t.maxCapacity")
    List<Truck> findFullyLoadedTrucks();

    /**
     * Find trucks that are empty or nearly empty
     * @param maxLoadThreshold Maximum load threshold to consider as empty
     * @return List of empty or nearly empty trucks
     */
    @Query("SELECT t FROM Truck t WHERE t.currentLoad <= :maxLoadThreshold")
    List<Truck> findEmptyTrucks(@Param("maxLoadThreshold") double maxLoadThreshold);
}
