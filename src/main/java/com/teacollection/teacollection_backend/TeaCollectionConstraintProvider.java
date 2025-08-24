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
                .forEach(SupplierVisit.class)
                .filter(visit -> visit.isAssigned())
                .join(SupplierVisit.class, 
                      Joiners.equal(SupplierVisit::getAssignedTruck))
                .filter((visit1, visit2) -> {
                    if (visit1 == visit2) return false;
                    double totalLoad = visit1.getHarvestWeight() + visit2.getHarvestWeight();
                    return totalLoad > visit1.getAssignedTruck().getMaxCapacity();
                })
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Truck capacity exceeded");
    }

    // Hard constraint: All suppliers must be visited
    private Constraint allSuppliersMustBeVisitedConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(SupplierVisit.class)
                .filter(visit -> !visit.isAssigned())
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Unassigned supplier");
    }

    // Soft constraint: Minimize total distance traveled
    private Constraint minimizeTotalDistanceConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(SupplierVisit.class)
                .filter(visit -> visit.isAssigned())
                .join(SupplierVisit.class, 
                      Joiners.equal(SupplierVisit::getAssignedTruck),
                      Joiners.lessThan(SupplierVisit::getVisitOrder))
                .penalize(HardSoftScore.ONE_SOFT, 
                          (visit1, visit2) -> calculateDistance(visit1, visit2))
                .asConstraint("Minimize total distance");
    }

    // Soft constraint: Minimize number of trucks used
    private Constraint minimizeTruckUsageConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Truck.class)
                .join(SupplierVisit.class, 
                      Joiners.equal((truck) -> truck, SupplierVisit::getAssignedTruck))
                .ifExists(SupplierVisit.class, 
                          Joiners.equal((truck, visit) -> truck, SupplierVisit::getAssignedTruck))
                .penalize(HardSoftScore.ONE_SOFT, 
                          (truck, visit) -> 1)
                .asConstraint("Minimize truck usage");
    }

    // Soft constraint: Balance load across trucks
    private Constraint balanceLoadAcrossTrucksConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(SupplierVisit.class)
                .filter(visit -> visit.isAssigned())
                .join(SupplierVisit.class, 
                      Joiners.equal(SupplierVisit::getAssignedTruck))
                .filter((visit1, visit2) -> visit1 != visit2)
                .penalize(HardSoftScore.ONE_SOFT, 
                          (visit1, visit2) -> 
                              (int) Math.abs(visit1.getHarvestWeight() - visit2.getHarvestWeight()))
                .asConstraint("Balance load across trucks");
    }

    // Helper method to calculate distance between two points
    private int calculateDistance(SupplierVisit visit1, SupplierVisit visit2) {
        double lat1 = visit1.getLatitude();
        double lon1 = visit1.getLongitude();
        double lat2 = visit2.getLatitude();
        double lon2 = visit2.getLongitude();
        
        // Simple Euclidean distance calculation (can be replaced with more accurate formula)
        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;
        return (int) Math.sqrt(deltaLat * deltaLat + deltaLon * deltaLon) * 100; // Scale factor
    }


}
