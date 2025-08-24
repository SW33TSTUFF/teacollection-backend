package com.teacollection.teacollection_backend.repository;

import com.teacollection.teacollection_backend.Depot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Depot entity operations.
 * Provides CRUD operations and custom queries for depot management.
 */
@Repository
public interface DepotRepository extends JpaRepository<Depot, Long> {

    /**
     * Find depot by coordinates (exact match)
     * @param latitude Depot latitude
     * @param longitude Depot longitude
     * @return Optional containing depot if found
     */
    Optional<Depot> findByLatitudeAndLongitude(double latitude, double longitude);

    /**
     * Find depots within a certain radius of given coordinates
     * @param latitude Center latitude
     * @param longitude Center longitude
     * @param radiusKm Radius in kilometers
     * @return List of depots within the radius
     */
    @Query("SELECT d FROM Depot d WHERE " +
           "SQRT(POWER(d.latitude - :latitude, 2) + POWER(d.longitude - :longitude, 2)) <= :radiusKm")
    List<Depot> findDepotsWithinRadius(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radiusKm") double radiusKm
    );

    /**
     * Find depots by latitude range
     * @param minLatitude Minimum latitude
     * @param maxLatitude Maximum latitude
     * @return List of depots within the latitude range
     */
    List<Depot> findByLatitudeBetween(double minLatitude, double maxLatitude);

    /**
     * Find depots by longitude range
     * @param minLongitude Minimum longitude
     * @param maxLongitude Maximum longitude
     * @return List of depots within the longitude range
     */
    List<Depot> findByLongitudeBetween(double minLongitude, double maxLongitude);

    /**
     * Find depots in a rectangular area
     * @param minLatitude Minimum latitude
     * @param maxLatitude Maximum latitude
     * @param minLongitude Minimum longitude
     * @param maxLongitude Maximum longitude
     * @return List of depots within the rectangular area
     */
    @Query("SELECT d FROM Depot d WHERE " +
           "d.latitude BETWEEN :minLatitude AND :maxLatitude " +
           "AND d.longitude BETWEEN :minLongitude AND :maxLongitude")
    List<Depot> findDepotsInRectangularArea(
            @Param("minLatitude") double minLatitude,
            @Param("maxLatitude") double maxLatitude,
            @Param("minLongitude") double minLongitude,
            @Param("maxLongitude") double maxLongitude
    );

    /**
     * Find the closest depot to given coordinates
     * @param latitude Target latitude
     * @param longitude Target longitude
     * @return Optional containing the closest depot
     */
    @Query("SELECT d FROM Depot d ORDER BY " +
           "SQRT(POWER(d.latitude - :latitude, 2) + POWER(d.longitude - :longitude, 2)) ASC")
    Optional<Depot> findClosestDepot(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude
    );

    /**
     * Find depots by approximate location (with tolerance)
     * @param latitude Target latitude
     * @param longitude Target longitude
     * @param tolerance Tolerance in degrees
     * @return List of depots within tolerance
     */
    @Query("SELECT d FROM Depot d WHERE " +
           "ABS(d.latitude - :latitude) <= :tolerance " +
           "AND ABS(d.longitude - :longitude) <= :tolerance")
    List<Depot> findDepotsByApproximateLocation(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("tolerance") double tolerance
    );

    /**
     * Count depots in a specific region
     * @param minLatitude Minimum latitude
     * @param maxLatitude Maximum latitude
     * @param minLongitude Minimum longitude
     * @param maxLongitude Maximum longitude
     * @return Count of depots in the region
     */
    @Query("SELECT COUNT(d) FROM Depot d WHERE " +
           "d.latitude BETWEEN :minLatitude AND :maxLatitude " +
           "AND d.longitude BETWEEN :minLongitude AND :maxLongitude")
    long countDepotsInRegion(
            @Param("minLatitude") double minLatitude,
            @Param("maxLatitude") double maxLatitude,
            @Param("minLongitude") double minLongitude,
            @Param("maxLongitude") double maxLongitude
    );
}
