package com.teacollection.teacollection_backend;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;

@PlanningEntity
public class SupplierAssignment {
    
    private Supplier supplier;
    private Truck assignedTruck;
    private SupplierAssignment previousAssignment; // The previous supplier assignment in the route
    
    // Default constructor required by OptaPlanner
    public SupplierAssignment() {
    }
    
    public SupplierAssignment(Supplier supplier) {
        this.supplier = supplier;
    }
    
    @PlanningVariable(valueRangeProviderRefs = {"truckRange"})
    public Truck getAssignedTruck() {
        return assignedTruck;
    }
    
    public void setAssignedTruck(Truck assignedTruck) {
        this.assignedTruck = assignedTruck;
    }
    
    @PlanningVariable(valueRangeProviderRefs = {"supplierAssignmentRange"})
    public SupplierAssignment getPreviousAssignment() {
        return previousAssignment;
    }
    
    public void setPreviousAssignment(SupplierAssignment previousAssignment) {
        this.previousAssignment = previousAssignment;
    }
    
    public Supplier getSupplier() {
        return supplier;
    }
    
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
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
    
    public Long getSupplierId() {
        return supplier != null ? supplier.getId() : null;
    }
}
