package com.teacollection.teacollection_backend;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;

@PlanningEntity
public class SupplierVisit {
    
    private Supplier supplier;
    private Truck assignedTruck;
    private int visitOrder; // The order in which this supplier is visited by the assigned truck
    
    // Default constructor required by OptaPlanner
    public SupplierVisit() {
    }
    
    public SupplierVisit(Supplier supplier) {
        this.supplier = supplier;
    }
    
    @PlanningVariable(valueRangeProviderRefs = {"truckRange"})
    public Truck getAssignedTruck() {
        return assignedTruck;
    }
    
    public void setAssignedTruck(Truck assignedTruck) {
        this.assignedTruck = assignedTruck;
    }
    
    public Supplier getSupplier() {
        return supplier;
    }
    
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
    
    public int getVisitOrder() {
        return visitOrder;
    }
    
    public void setVisitOrder(int visitOrder) {
        this.visitOrder = visitOrder;
    }
    
    // Helper methods for constraint evaluation
    public boolean isAssigned() {
        return assignedTruck != null;
    }
    
    public double getHarvestWeight() {
        return supplier != null ? supplier.getHarvestWeight() : 0.0;
    }
    
    public double getLatitude() {
        return supplier != null ? supplier.getLatitude() : 0.0;
    }
    
    public double getLongitude() {
        return supplier != null ? supplier.getLongitude() : 0.0;
    }
}
