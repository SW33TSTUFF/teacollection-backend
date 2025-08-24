package com.teacollection.teacollection_backend.service.impl;

import com.teacollection.teacollection_backend.Truck;
import com.teacollection.teacollection_backend.repository.TruckRepository;
import com.teacollection.teacollection_backend.service.TruckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TruckServiceImpl implements TruckService {

    private final TruckRepository truckRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Truck> getAllTrucks() {
        log.info("Retrieving all trucks");
        return truckRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Truck getTruckById(Long id) {
        log.info("Retrieving truck with ID: {}", id);
        return truckRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Truck not found with ID: " + id));
    }

    @Override
    public Truck createTruck(Truck truck) {
        log.info("Creating new truck");
        
        // Validate truck data
        if (truck.getCurrentLoad() > truck.getMaxCapacity()) {
            throw new RuntimeException("Current load cannot exceed max capacity");
        }
        
        Truck savedTruck = truckRepository.save(truck);
        log.info("Created truck with ID: {}", savedTruck.getId());
        return savedTruck;
    }

    @Override
    public Truck updateTruck(Long id, Truck truck) {
        log.info("Updating truck with ID: {}", id);
        
        if (!truckRepository.existsById(id)) {
            throw new RuntimeException("Truck not found with ID: " + id);
        }
        
        // Validate truck data
        if (truck.getCurrentLoad() > truck.getMaxCapacity()) {
            throw new RuntimeException("Current load cannot exceed max capacity");
        }
        
        truck.setId(id);
        Truck updatedTruck = truckRepository.save(truck);
        log.info("Updated truck with ID: {}", id);
        return updatedTruck;
    }

    @Override
    public void deleteTruck(Long id) {
        log.info("Deleting truck with ID: {}", id);
        
        if (!truckRepository.existsById(id)) {
            throw new RuntimeException("Truck not found with ID: " + id);
        }
        
        truckRepository.deleteById(id);
        log.info("Deleted truck with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Truck> getAvailableTrucks() {
        log.info("Retrieving available trucks");
        return truckRepository.findIdleTrucks();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Truck> getTrucksByCapacity(double minCapacity) {
        log.info("Retrieving trucks with max capacity >= {}", minCapacity);
        return truckRepository.findByMaxCapacityBetween(minCapacity, Double.MAX_VALUE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Truck> getTrucksByFuelLevel(double minFuelLevel) {
        log.info("Fuel level not available in current Truck entity");
        return List.of(); // Return empty list as fuel level is not implemented
    }

    @Override
    public Truck updateTruckLoad(Long id, double load) {
        log.info("Updating truck {} load to: {}", id, load);
        
        Truck truck = getTruckById(id);
        
        if (load > truck.getMaxCapacity()) {
            throw new RuntimeException("Load cannot exceed truck max capacity");
        }
        
        truck.setCurrentLoad(load);
        Truck updatedTruck = truckRepository.save(truck);
        log.info("Updated truck {} load to: {}", id, load);
        return updatedTruck;
    }

    @Override
    public Truck updateTruckFuel(Long id, double fuelLevel) {
        log.info("Fuel level not available in current Truck entity");
        throw new RuntimeException("Fuel level functionality not implemented");
    }

    @Override
    public Truck updateTruckAvailability(Long id, boolean isAvailable) {
        log.info("Updating truck {} status to: {}", id, isAvailable ? "IDLE" : "EN_ROUTE");
        
        Truck truck = getTruckById(id);
        truck.setStatus(isAvailable ? "IDLE" : "EN_ROUTE");
        
        Truck updatedTruck = truckRepository.save(truck);
        log.info("Updated truck {} status to: {}", id, truck.getStatus());
        return updatedTruck;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Truck> getTrucksNearby(double latitude, double longitude, double radiusKm) {
        log.info("Location-based queries not available in current Truck entity");
        return List.of(); // Return empty list as location is not implemented
    }

    @Override
    @Transactional(readOnly = true)
    public long getTruckCount(boolean isAvailable) {
        log.info("Counting trucks with status: {}", isAvailable ? "IDLE" : "EN_ROUTE");
        String status = isAvailable ? "IDLE" : "EN_ROUTE";
        return truckRepository.countByStatus(status);
    }
}
