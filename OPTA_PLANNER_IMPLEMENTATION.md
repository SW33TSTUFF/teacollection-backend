# OptaPlanner Implementation for Tea Collection Optimization

This document describes the implementation of the OptaPlanner domain model and constraint rules for optimizing tea collection routes.

## Overview

The implementation uses OptaPlanner to solve the Vehicle Routing Problem (VRP) for tea collection, where:
- Multiple trucks need to visit multiple suppliers
- Each supplier has a specific harvest weight
- Trucks have capacity constraints
- The goal is to minimize total distance while respecting all constraints

## Core Classes

### 1. TeaLeafSolution.java
The main planning solution class that holds all problem data:
- **@PlanningSolution**: Marks this class as the solution container
- **@ProblemFactCollectionProperty**: Lists of trucks (fixed data)
- **@PlanningEntityCollectionProperty**: List of suppliers (assignable entities)
- **@ProblemFactProperty**: Depot location (fixed data)
- **@PlanningScore**: Solution score (HardSoftScore)

### 2. Domain Classes Structure
**Truck.java**: Pure JPA entity for truck data
**Supplier.java**: Pure JPA entity for supplier data
**SupplierAssignment.java**: 
- **@PlanningEntity**: Marks this class as assignable by OptaPlanner
- **@PlanningVariable**: The truck assigned to this supplier assignment
- **@PlanningVariable**: The previous supplier assignment in the route sequence

### 3. TeaCollectionConstraintProvider.java
Defines all business rules and scoring:
- **Hard Constraints**: Must be satisfied (e.g., truck capacity limits)
- **Soft Constraints**: Should be optimized (e.g., minimize distance)

## Constraint Rules

### Hard Constraints
1. **Truck Capacity**: No truck can exceed its maximum capacity
2. **All Suppliers Visited**: Every supplier must be assigned to a truck

### Soft Constraints
1. **Minimize Total Distance**: Reduce the total distance traveled by all trucks
2. **Minimize Truck Usage**: Use as few trucks as possible
3. **Balance Load**: Distribute load evenly across trucks

## Usage

### Basic Optimization
```java
@Autowired
private OptimizationService optimizationService;

List<Truck> trucks = // ... get trucks from repository
List<Supplier> suppliers = // ... get suppliers from repository
Depot depot = // ... get depot from repository

TeaLeafSolution solution = optimizationService.optimizeTeaCollection(trucks, suppliers, depot);

// Access the optimized solution
solution.getSupplierAssignments().forEach(assignment -> {
    if (assignment.isAssigned()) {
        System.out.println("Supplier " + assignment.getSupplierId() + " assigned to Truck " + assignment.getAssignedTruck().getId());
        if (assignment.getPreviousAssignment() != null) {
            System.out.println("  Previous supplier: " + assignment.getPreviousAssignment().getSupplierId());
        }
    }
});
```

### Solver Configuration
The solver is configured with:
- 5-minute time limit for optimization
- HardSoftScore for constraint evaluation
- Constraint streams API for rule definition

## Testing

Run the integration test to verify the OptaPlanner setup:
```bash
mvn test -Dtest=OptaPlannerIntegrationTest
```

## Dependencies

The implementation requires:
- `optaplanner-core` (version 10.1.0)
- `optaplanner-spring-boot-starter` (version 10.1.0)

## Next Steps

1. **Route Sequencing**: Implement proper visit ordering within each truck's route
2. **Time Windows**: Add supplier availability time constraints
3. **Multiple Depots**: Support for multiple depot locations
4. **Real-time Updates**: Handle dynamic changes during optimization
5. **Performance Tuning**: Optimize solver parameters for production use

## Performance Considerations

- The current implementation uses a 5-minute time limit
- For production, consider using longer time limits or termination criteria
- Monitor memory usage with large problem sizes
- Consider using OptaPlanner's cloud balancing features for very large problems
