package com.teacollection.teacollection_backend;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

import java.util.Objects;

public class TeaCollectionConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                // Hard constraints
                truckCapacityConstraint(constraintFactory),
                allSuppliersMustBeVisitedConstraint(constraintFactory),
                
                // Soft constraints
                minimizeTotalDistanceConstraint(constraintFactory),
                minimizeTruckUsageConstraint(constraintFactory),
                balanceLoadAcrossTrucksConstraint(constraintFactory)
        };
    }

    // Hard constraint: Truck capacity must not be exceeded
    private Constraint truckCapacityConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(SupplierAssignment.class)
                .filter(assignment -> assignment.isAssigned())
                .join(SupplierAssignment.class, 
                      Joiners.equal(SupplierAssignment::getAssignedTruck))
                .filter((assignment1, assignment2) -> {
                    if (assignment1 == assignment2) return false;
                    double totalLoad = assignment1.getHarvestWeight() + assignment2.getHarvestWeight();
                    return totalLoad > assignment1.getAssignedTruck().getMaxCapacity();
                })
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Truck capacity exceeded");
    }

    // Hard constraint: All suppliers must be visited
    private Constraint allSuppliersMustBeVisitedConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(SupplierAssignment.class)
                .filter(assignment -> !assignment.isAssigned())
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Unassigned supplier");
    }

    // Soft constraint: Minimize total distance traveled
    private Constraint minimizeTotalDistanceConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(SupplierAssignment.class)
                .filter(assignment -> assignment.isAssigned() && assignment.getPreviousAssignment() != null)
                .penalize(HardSoftScore.ONE_SOFT, 
                          assignment -> calculateDistance(assignment, assignment.getPreviousAssignment()))
                .asConstraint("Minimize total distance");
    }

    // Soft constraint: Minimize number of trucks used
    private Constraint minimizeTruckUsageConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Truck.class)
                .join(SupplierAssignment.class, 
                      Joiners.equal((truck) -> truck, SupplierAssignment::getAssignedTruck))
                .ifExists(SupplierAssignment.class, 
                          Joiners.equal((truck, assignment) -> truck, SupplierAssignment::getAssignedTruck))
                .penalize(HardSoftScore.ONE_SOFT, 
                          (truck, assignment) -> 1)
                .asConstraint("Minimize truck usage");
    }

    // Soft constraint: Balance load across trucks
    private Constraint balanceLoadAcrossTrucksConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(SupplierAssignment.class)
                .filter(assignment -> assignment.isAssigned())
                .join(SupplierAssignment.class, 
                      Joiners.equal(SupplierAssignment::getAssignedTruck))
                .filter((assignment1, assignment2) -> assignment1 != assignment2)
                .penalize(HardSoftScore.ONE_SOFT, 
                          (assignment1, assignment2) -> 
                              (int) Math.abs(assignment1.getHarvestWeight() - assignment2.getHarvestWeight()))
                .asConstraint("Balance load across trucks");
    }

    // Helper method to calculate distance between two points
    private int calculateDistance(SupplierAssignment assignment1, SupplierAssignment assignment2) {
        double lat1 = assignment1.getLatitude();
        double lon1 = assignment1.getLongitude();
        double lat2 = assignment2.getLatitude();
        double lon2 = assignment2.getLongitude();
        
        // Simple Euclidean distance calculation (can be replaced with more accurate formula)
        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;
        return (int) Math.sqrt(deltaLat * deltaLat + deltaLon * deltaLon) * 100; // Scale factor
    }


}
