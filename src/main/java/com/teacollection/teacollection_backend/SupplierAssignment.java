package com.teacollection.teacollection_backend;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;

@PlanningEntity
public class SupplierAssignment implements RouteStop {

    private Supplier supplier;

    // PLANNING VARIABLE: Defines the route structure.
    // Can be a Truck (start of route) or another SupplierAssignment.
    @PlanningVariable(valueRangeProviderRefs = {"truckRange", "supplierAssignmentRange"},
                      graphType = PlanningVariableGraphType.CHAINED)
    private RouteStop previousStop;

    // Constructors
    public SupplierAssignment() {}

    public SupplierAssignment(Supplier supplier) {
        this.supplier = supplier;
    }

    // --- Delegate methods to Supplier ---
    public Long getSupplierId() { return supplier.getId(); }
    public double getLatitude() { return supplier.getLatitude(); }
    public double getLongitude() { return supplier.getLongitude(); }
    public double getHarvestWeight() { return supplier.getHarvestWeight(); }
    
    @Override
    public Location getLocation() {
        return new Location(supplier.getLatitude(), supplier.getLongitude());
    }

    // Helper method to get the assigned truck by traversing the chain
    @Override
    public Truck getTruck() {
        if (previousStop == null) {
            return null;
        }
        return previousStop.getTruck();
    }
    
    // Check if the assignment is part of a route
    public boolean isAssigned() {
        return previousStop != null;
    }
    
    // Helper method to calculate distance from previous stop
    public double getDistanceFromPrevious() {
        if (previousStop == null) {
            return 0.0;
        }
        return calculateDistance(previousStop.getLocation(), this.getLocation());
    }
    
    // Calculate distance between two locations using Haversine formula
    private double calculateDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.getLatitude());
        double lon1 = Math.toRadians(loc1.getLongitude());
        double lat2 = Math.toRadians(loc2.getLatitude());
        double lon2 = Math.toRadians(loc2.getLongitude());
        
        double earthRadius = 6371; // Earth's radius in kilometers
        
        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return earthRadius * c; // Return distance in kilometers
    }

    // Getters and Setters
    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
    public RouteStop getPreviousStop() { return previousStop; }
    public void setPreviousStop(RouteStop previousStop) { this.previousStop = previousStop; }
}
