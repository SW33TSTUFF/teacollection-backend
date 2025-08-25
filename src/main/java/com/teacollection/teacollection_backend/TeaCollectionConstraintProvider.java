package com.teacollection.teacollection_backend;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.Joiners;

public class TeaCollectionConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                // Hard constraints
                truckCapacityConstraint(constraintFactory),
                allSuppliersMustBeVisitedConstraint(constraintFactory),
                
                // Soft constraints
                minimizeTotalDistanceConstraint(constraintFactory),
                minimizeTruckUsageConstraint(constraintFactory)
        };
    }

    // Hard constraint: Truck capacity must not be exceeded
    private Constraint truckCapacityConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(SupplierAssignment.class)
                .filter(SupplierAssignment::isAssigned)
                .groupBy(SupplierAssignment::getTruck, 
                         ConstraintCollectors.sum(assignment -> (int) assignment.getHarvestWeight()))
                .filter((truck, totalWeight) -> totalWeight > truck.getMaxCapacity())
                .penalize(HardSoftScore.ONE_HARD, 
                         (truck, totalWeight) -> (int)(totalWeight - truck.getMaxCapacity()))
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

    // Soft constraint: Minimize total distance traveled between consecutive stops
    private Constraint minimizeTotalDistanceConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(SupplierAssignment.class)
                .filter(assignment -> assignment.isAssigned())
                .penalize(HardSoftScore.ONE_SOFT, 
                          assignment -> (int)(assignment.getDistanceFromPrevious() * 1000)) // Convert km to meters
                .asConstraint("Distance between consecutive stops");
    }

    // Soft constraint: Minimize number of trucks used
    private Constraint minimizeTruckUsageConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Truck.class)
                .ifNotExists(SupplierAssignment.class, 
                            Joiners.equal(truck -> truck, SupplierAssignment::getTruck))
                .reward(HardSoftScore.ONE_SOFT, truck -> 100)
                .asConstraint("Reward unused trucks");
    }
}