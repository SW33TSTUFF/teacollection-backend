package com.teacollection.teacollection_backend;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.domain.solution.PlanningScore;

import java.util.List;

@PlanningSolution
public class TeaLeafSolution {
    
    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "truckRange")
    private List<Truck> trucks;
    
    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "supplierAssignmentRange")
    private List<SupplierAssignment> supplierAssignments;
    
    @ProblemFactProperty
    private Depot depot;
    
    @PlanningScore
    private HardSoftScore score;
    
    // Default constructor required by OptaPlanner
    public TeaLeafSolution() {
    }
    
    // Constructor with all fields
    public TeaLeafSolution(List<Truck> trucks, List<SupplierAssignment> supplierAssignments, Depot depot) {
        this.trucks = trucks;
        this.supplierAssignments = supplierAssignments;
        this.depot = depot;
    }
    
    // Getters and setters
    public List<Truck> getTrucks() {
        return trucks;
    }
    
    public void setTrucks(List<Truck> trucks) {
        this.trucks = trucks;
    }
    
    public List<SupplierAssignment> getSupplierAssignments() {
        return supplierAssignments;
    }
    
    public void setSupplierAssignments(List<SupplierAssignment> supplierAssignments) {
        this.supplierAssignments = supplierAssignments;
    }
    
    public Depot getDepot() {
        return depot;
    }
    
    public void setDepot(Depot depot) {
        this.depot = depot;
    }
    
    public HardSoftScore getScore() {
        return score;
    }
    
    public void setScore(HardSoftScore score) {
        this.score = score;
    }
}
