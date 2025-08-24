package com.teacollection.teacollection_backend;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OptaPlannerIntegrationTest {

    @Test
    public void testOptaPlannerSetup() {
        // Create test data
        Truck truck1 = new Truck();
        truck1.setId(1L);
        truck1.setMaxCapacity(1000.0);
        truck1.setCurrentLoad(0.0);
        truck1.setStatus("IDLE");

        Truck truck2 = new Truck();
        truck2.setId(2L);
        truck2.setMaxCapacity(800.0);
        truck2.setCurrentLoad(0.0);
        truck2.setStatus("IDLE");

        Supplier supplier1 = new Supplier();
        supplier1.setId(1L);
        supplier1.setLatitude(1.0);
        supplier1.setLongitude(1.0);
        supplier1.setHarvestWeight(200.0);
        supplier1.setReady(true);

        Supplier supplier2 = new Supplier();
        supplier2.setId(2L);
        supplier2.setLatitude(2.0);
        supplier2.setLongitude(2.0);
        supplier2.setHarvestWeight(300.0);
        supplier2.setReady(true);

        Depot depot = new Depot();
        depot.setId(1L);
        depot.setLatitude(0.0);
        depot.setLongitude(0.0);

        // Create supplier visits
        SupplierVisit visit1 = new SupplierVisit(supplier1);
        SupplierVisit visit2 = new SupplierVisit(supplier2);

        // Create the problem
        TeaLeafSolution problem = new TeaLeafSolution(
                Arrays.asList(truck1, truck2),
                Arrays.asList(visit1, visit2),
                depot
        );

        // Configure and create solver
        SolverConfig solverConfig = new SolverConfig()
                .withSolutionClass(TeaLeafSolution.class)
                .withEntityClasses(SupplierVisit.class)
                .withConstraintProviderClass(TeaCollectionConstraintProvider.class)
                .withTerminationSpentLimit(java.time.Duration.ofSeconds(10));

        SolverFactory<TeaLeafSolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<TeaLeafSolution> solver = solverFactory.buildSolver();

        // Solve the problem
        TeaLeafSolution solution = solver.solve(problem);

        // Verify the solution
        assertNotNull(solution);
        assertNotNull(solution.getScore());
        assertTrue(solution.getScore().getHardScore() >= 0); // Should not have hard constraint violations
        
        // Verify all suppliers are assigned
        assertTrue(solution.getSupplierVisits().stream().allMatch(SupplierVisit::isAssigned));
        
        // Verify truck capacity constraints
        solution.getSupplierVisits().forEach(visit -> {
            if (visit.isAssigned()) {
                Truck truck = visit.getAssignedTruck();
                double totalLoad = solution.getSupplierVisits().stream()
                        .filter(v -> v.isAssigned() && v.getAssignedTruck().equals(truck))
                        .mapToDouble(SupplierVisit::getHarvestWeight)
                        .sum();
                assertTrue(totalLoad <= truck.getMaxCapacity(), 
                    "Truck " + truck.getId() + " capacity exceeded: " + totalLoad + " > " + truck.getMaxCapacity());
            }
        });
    }
}
