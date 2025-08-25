package com.teacollection.teacollection_backend;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class OptaPlannerUnitTest {

    @Test
    public void testOptimizationWithoutSpring() {
        // Create realistic tea collection scenario
        List<Truck> trucks = createRealisticTrucks();
        List<Supplier> suppliers = createRealisticSuppliers();
        Depot depot = createDepot();

        System.out.println("=== BEFORE OPTIMIZATION ===");
        printInitialState(trucks, suppliers);

        // Create supplier assignments
        List<SupplierAssignment> assignments = suppliers.stream()
                .map(SupplierAssignment::new)
                .toList();

        // Create the problem
        TeaLeafSolution problem = new TeaLeafSolution(trucks, assignments, depot);

        // Configure and create solver manually (no Spring context needed)
        SolverConfig solverConfig = new SolverConfig()
                .withSolutionClass(TeaLeafSolution.class)
                .withEntityClasses(SupplierAssignment.class)
                .withConstraintProviderClass(TeaCollectionConstraintProvider.class)
                .withTerminationSpentLimit(java.time.Duration.ofSeconds(10));

        SolverFactory<TeaLeafSolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<TeaLeafSolution> solver = solverFactory.buildSolver();

        // Solve the problem
        TeaLeafSolution solution = solver.solve(problem);

        System.out.println("\n=== AFTER OPTIMIZATION ===");
        printOptimizedSolution(solution);

        // Verify the solution
        verifySolution(solution);
    }

    private List<Truck> createRealisticTrucks() {
        Truck truck1 = new Truck();
        truck1.setId(1L);
        truck1.setMaxCapacity(2000.0); // 2 tons
        truck1.setCurrentLoad(0.0);
        truck1.setStatus("IDLE");

        Truck truck2 = new Truck();
        truck2.setId(2L);
        truck2.setMaxCapacity(1500.0); // 1.5 tons
        truck2.setCurrentLoad(0.0);
        truck2.setStatus("IDLE");

        Truck truck3 = new Truck();
        truck3.setId(3L);
        truck3.setMaxCapacity(1000.0); // 1 ton
        truck3.setCurrentLoad(0.0);
        truck3.setStatus("IDLE");

        return Arrays.asList(truck1, truck2, truck3);
    }

    private List<Supplier> createRealisticSuppliers() {
        // Create suppliers in a realistic geographic pattern
        Supplier supplier1 = new Supplier();
        supplier1.setId(1L);
        supplier1.setLatitude(1.0);
        supplier1.setLongitude(1.0);
        supplier1.setHarvestWeight(800.0); // 800kg
        supplier1.setReady(true);

        Supplier supplier2 = new Supplier();
        supplier2.setId(2L);
        supplier2.setLatitude(2.0);
        supplier2.setLongitude(1.0);
        supplier2.setHarvestWeight(600.0); // 600kg
        supplier2.setReady(true);

        Supplier supplier3 = new Supplier();
        supplier3.setId(3L);
        supplier3.setLatitude(1.0);
        supplier3.setLongitude(2.0);
        supplier3.setHarvestWeight(400.0); // 400kg
        supplier3.setReady(true);

        Supplier supplier4 = new Supplier();
        supplier4.setId(4L);
        supplier4.setLatitude(3.0);
        supplier4.setLongitude(2.0);
        supplier4.setHarvestWeight(1200.0); // 1.2 tons
        supplier4.setReady(true);

        Supplier supplier5 = new Supplier();
        supplier5.setId(5L);
        supplier5.setLatitude(2.0);
        supplier5.setLongitude(3.0);
        supplier5.setHarvestWeight(900.0); // 900kg
        supplier5.setReady(true);

        return Arrays.asList(supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    private Depot createDepot() {
        Depot depot = new Depot();
        depot.setId(1L);
        depot.setLatitude(0.0);
        depot.setLongitude(0.0);
        return depot;
    }

    private void printInitialState(List<Truck> trucks, List<Supplier> suppliers) {
        System.out.println("Trucks:");
        trucks.forEach(truck -> 
            System.out.println("  Truck " + truck.getId() + ": Capacity " + truck.getMaxCapacity() + "kg"));

        System.out.println("\nSuppliers:");
        suppliers.forEach(supplier -> 
            System.out.println("  Supplier " + supplier.getId() + ": " + supplier.getHarvestWeight() + 
                             "kg at (" + supplier.getLatitude() + ", " + supplier.getLongitude() + ")"));
    }

    private void printOptimizedSolution(TeaLeafSolution solution) {
        System.out.println("Optimization Score: " + solution.getScore());
        System.out.println("Hard Score: " + solution.getScore().getHardScore() + " (0 = no violations)");
        System.out.println("Soft Score: " + solution.getScore().getSoftScore() + " (higher = better)");
    
        System.out.println("\nOptimized Routes:");
        
        // Display routes per truck in proper order
        for (Truck truck : solution.getTrucks()) {
            List<SupplierAssignment> truckAssignments = solution.getSupplierAssignments().stream()
                .filter(a -> a.isAssigned() && a.getAssignedTruck().equals(truck))
                .toList();
                
            if (truckAssignments.isEmpty()) continue;
            
            // Find the route order by following the chain
            List<SupplierAssignment> route = buildRoute(truckAssignments);
            
            System.out.println("  Truck " + truck.getId() + " route:");
            for (int i = 0; i < route.size(); i++) {
                SupplierAssignment assignment = route.get(i);
                System.out.println("    " + (i + 1) + ". Supplier " + assignment.getSupplierId() + 
                                 " (Load: " + assignment.getHarvestWeight() + "kg)");
            }
        }
    
        // Calculate total load per truck
        System.out.println("\nTruck Load Summary:");
        solution.getTrucks().forEach(truck -> {
            double totalLoad = solution.getSupplierAssignments().stream()
                    .filter(a -> a.isAssigned() && a.getAssignedTruck().equals(truck))
                    .mapToDouble(SupplierAssignment::getHarvestWeight)
                    .sum();
            
            if (totalLoad > 0) {
                System.out.println("  Truck " + truck.getId() + ": " + totalLoad + 
                                 "kg / " + truck.getMaxCapacity() + "kg (" + 
                                 String.format("%.1f", (totalLoad/truck.getMaxCapacity())*100) + "%)");
            }
        });
    }
    
    private List<SupplierAssignment> buildRoute(List<SupplierAssignment> assignments) {
        List<SupplierAssignment> route = new ArrayList<>();
        
        // Find the starting assignment (no previous assignment)
        SupplierAssignment current = assignments.stream()
            .filter(a -> a.getPreviousAssignment() == null)
            .findFirst()
            .orElse(null);
        
        if (current == null) {
            // If no clear start, just return the assignments as-is
            return assignments;
        }
        
        // Follow the chain
        while (current != null) {
            route.add(current);
            final SupplierAssignment currentFinal = current;
            current = assignments.stream()
                .filter(a -> a.getPreviousAssignment() != null && 
                            a.getPreviousAssignment().equals(currentFinal))
                .findFirst()
                .orElse(null);
        }
        
        return route;
    }
    private void verifySolution(TeaLeafSolution solution) {
        // Verify all suppliers are assigned
        assertTrue(solution.getSupplierAssignments().stream().allMatch(SupplierAssignment::isAssigned),
                "All suppliers should be assigned");

        // Verify truck capacity constraints
        solution.getTrucks().forEach(truck -> {
            double totalLoad = solution.getSupplierAssignments().stream()
                    .filter(a -> a.isAssigned() && a.getAssignedTruck().equals(truck))
                    .mapToDouble(SupplierAssignment::getHarvestWeight)
                    .sum();
            
            assertTrue(totalLoad <= truck.getMaxCapacity(),
                    "Truck " + truck.getId() + " capacity exceeded: " + totalLoad + " > " + truck.getMaxCapacity());
        });

        // Verify score is reasonable
        assertNotNull(solution.getScore());
        assertTrue(solution.getScore().getHardScore() >= 0, "Hard score should be non-negative");
        
        System.out.println("\n✅ All constraints verified successfully!");
    }
}
