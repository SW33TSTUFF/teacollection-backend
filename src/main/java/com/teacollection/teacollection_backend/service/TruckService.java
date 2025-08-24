package com.teacollection.teacollection_backend.service;

import com.teacollection.teacollection_backend.Truck;
import java.util.List;

public interface TruckService {
    
    List<Truck> getAllTrucks();
    
    Truck getTruckById(Long id);
    
    Truck createTruck(Truck truck);
    
    Truck updateTruck(Long id, Truck truck);
    
    void deleteTruck(Long id);
    
    List<Truck> getAvailableTrucks();
    
    List<Truck> getTrucksByCapacity(double minCapacity);
    
    List<Truck> getTrucksByFuelLevel(double minFuelLevel);
    
    Truck updateTruckLoad(Long id, double load);
    
    Truck updateTruckFuel(Long id, double fuelLevel);
    
    Truck updateTruckAvailability(Long id, boolean isAvailable);
    
    List<Truck> getTrucksNearby(double latitude, double longitude, double radiusKm);
    
    long getTruckCount(boolean isAvailable);
}
