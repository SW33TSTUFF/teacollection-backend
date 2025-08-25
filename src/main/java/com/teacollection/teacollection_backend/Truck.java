package com.teacollection.teacollection_backend;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;

@Entity
@Data
public class Truck implements RouteStop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double maxCapacity;
    private double currentLoad;
    private String status; // IDLE, EN_ROUTE, RETURNING
    
    // Depot location - this represents where the truck starts and returns to
    private double depotLatitude;
    private double depotLongitude;
    
    @Override
    public Location getLocation() {
        return new Location(depotLatitude, depotLongitude);
    }
    
    @Override
    public Truck getTruck() {
        return this; // A truck is its own truck
    }
}
