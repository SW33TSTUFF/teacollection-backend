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
        printInitialState(trucks, suppliers, depot);

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
        truck1.setMaxCapacity(100.0); // 100kg capacity
        truck1.setCurrentLoad(0.0);
        truck1.setStatus("IDLE");
        truck1.setDepotLatitude(6.2173037);
        truck1.setDepotLongitude(80.2564385);

        Truck truck2 = new Truck();
        truck2.setId(2L);
        truck2.setMaxCapacity(80.0); // 80kg capacity
        truck2.setCurrentLoad(0.0);
        truck2.setStatus("IDLE");
        truck2.setDepotLatitude(6.2173037);
        truck2.setDepotLongitude(80.2564385);

        Truck truck3 = new Truck();
        truck3.setId(3L);
        truck3.setMaxCapacity(60.0); // 60kg capacity
        truck3.setCurrentLoad(0.0);
        truck3.setStatus("IDLE");
        truck3.setDepotLatitude(6.2173037);
        truck3.setDepotLongitude(80.2564385);

        return Arrays.asList(truck1, truck2, truck3);
    }

    private List<Supplier> createRealisticSuppliers() {
        // Create suppliers with real coordinates from Sri Lanka
        Supplier supplier1 = new Supplier();
        supplier1.setId(1L);
        supplier1.setLatitude(6.222312);
        supplier1.setLongitude(80.246281);
        supplier1.setHarvestWeight(40.0); // 40kg
        supplier1.setReady(true);

        Supplier supplier2 = new Supplier();
        supplier2.setId(2L);
        supplier2.setLatitude(6.203033);
        supplier2.setLongitude(80.249292);
        supplier2.setHarvestWeight(30.0); // 30kg
        supplier2.setReady(true);

        Supplier supplier3 = new Supplier();
        supplier3.setId(3L);
        supplier3.setLatitude(6.271066);
        supplier3.setLongitude(80.237452);
        supplier3.setHarvestWeight(50.0); // 50kg
        supplier3.setReady(true);

        Supplier supplier4 = new Supplier();
        supplier4.setId(4L);
        supplier4.setLatitude(6.219420);
        supplier4.setLongitude(80.314617);
        supplier4.setHarvestWeight(20.0); // 20kg
        supplier4.setReady(true);

        Supplier supplier5 = new Supplier();
        supplier5.setId(5L);
        supplier5.setLatitude(6.189892);
        supplier5.setLongitude(80.304716);
        supplier5.setHarvestWeight(33.0); // 33kg
        supplier5.setReady(true);

        return Arrays.asList(supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    private Depot createDepot() {
        Depot depot = new Depot();
        depot.setId(1L);
        depot.setLatitude(6.2173037);
        depot.setLongitude(80.2564385);
        return depot;
    }

    private void printInitialState(List<Truck> trucks, List<Supplier> suppliers, Depot depot) {
        System.out.println("Trucks:");
        trucks.forEach(truck -> 
            System.out.println("  Truck " + truck.getId() + ": Capacity " + truck.getMaxCapacity() + "kg"));

        System.out.println("\nSuppliers:");
        suppliers.forEach(supplier -> 
            System.out.println("  Supplier " + supplier.getId() + ": " + supplier.getHarvestWeight() + 
                             "kg at (" + String.format("%.6f", supplier.getLatitude()) + ", " + 
                             String.format("%.6f", supplier.getLongitude()) + ")"));
        
        System.out.println("\nDepot: (" + String.format("%.6f", depot.getLatitude()) + ", " + 
                          String.format("%.6f", depot.getLongitude()) + ")");
    }

    private void printOptimizedSolution(TeaLeafSolution solution) {
        System.out.println("Optimization Score: " + solution.getScore());
        System.out.println("Hard Score: " + solution.getScore().getHardScore() + " (0 = no violations)");
        System.out.println("Soft Score: " + solution.getScore().getSoftScore() + " (lower = better)");
    
        System.out.println("\nOptimized Routes:");
        
        // Display routes per truck in proper order
        for (Truck truck : solution.getTrucks()) {
            List<SupplierAssignment> truckAssignments = solution.getSupplierAssignments().stream()
                .filter(a -> a.isAssigned() && a.getTruck().equals(truck))
                .toList();
                
            if (truckAssignments.isEmpty()) continue;
            
            // Find the route order by following the chain
            List<SupplierAssignment> route = buildRoute(truckAssignments);
            
            System.out.println("  Truck " + truck.getId() + " route:");
            double totalDistance = 0.0;
            SupplierAssignment previous = null;
            
            for (int i = 0; i < route.size(); i++) {
                SupplierAssignment assignment = route.get(i);
                System.out.println("    " + (i + 1) + ". Supplier " + assignment.getSupplierId() + 
                                 " (Load: " + assignment.getHarvestWeight() + "kg)");
                
                if (previous != null) {
                    double distance = calculateDistance(previous, assignment);
                    totalDistance += distance;
                    System.out.println("       Distance from previous: " + String.format("%.1f", distance/1000) + "km");
                }
                previous = assignment;
            }
            
            if (route.size() > 0) {
                System.out.println("    Total route distance: " + String.format("%.1f", totalDistance/1000) + "km");
            }
        }
    
        // Calculate total load per truck
        System.out.println("\nTruck Load Summary:");
        solution.getTrucks().forEach(truck -> {
            double totalLoad = solution.getSupplierAssignments().stream()
                    .filter(a -> a.isAssigned() && a.getTruck().equals(truck))
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
        
        // Find the starting assignment (no previous stop or previous stop is a truck)
        SupplierAssignment current = assignments.stream()
            .filter(a -> a.getPreviousStop() == null || a.getPreviousStop() instanceof Truck)
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
                .filter(a -> a.getPreviousStop() != null && 
                            a.getPreviousStop().equals(currentFinal))
                .findFirst()
                .orElse(null);
        }
        
        return route;
    }

    private double calculateDistance(SupplierAssignment s1, SupplierAssignment s2) {
        Location loc1 = s1.getLocation();
        Location loc2 = s2.getLocation();
        
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
        
        return earthRadius * c * 1000; // Return in meters
    }
    
    private void verifySolution(TeaLeafSolution solution) {
        // Verify all suppliers are assigned
        assertTrue(solution.getSupplierAssignments().stream().allMatch(SupplierAssignment::isAssigned),
                "All suppliers should be assigned");

        // Verify truck capacity constraints
        solution.getTrucks().forEach(truck -> {
            double totalLoad = solution.getSupplierAssignments().stream()
                    .filter(a -> a.isAssigned() && a.getTruck().equals(truck))
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
