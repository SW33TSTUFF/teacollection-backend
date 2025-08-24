package com.teacollection.teacollection_backend.service;

import com.teacollection.teacollection_backend.*;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OptimizationService {
    
    private final SolverFactory<TeaLeafSolution> solverFactory;
    
    public OptimizationService() {
        // Configure the solver
        SolverConfig solverConfig = new SolverConfig()
                .withSolutionClass(TeaLeafSolution.class)
                .withEntityClasses(SupplierAssignment.class)
                .withConstraintProviderClass(TeaCollectionConstraintProvider.class)
                .withTerminationSpentLimit(java.time.Duration.ofMinutes(5));
        
        this.solverFactory = SolverFactory.create(solverConfig);
    }
    
    public TeaLeafSolution optimizeTeaCollection(List<Truck> trucks, List<Supplier> suppliers, Depot depot) {
        // Create supplier assignments from suppliers
        List<SupplierAssignment> supplierAssignments = suppliers.stream()
                .map(SupplierAssignment::new)
                .collect(java.util.stream.Collectors.toList());
        
        // Create the problem
        TeaLeafSolution problem = new TeaLeafSolution(trucks, supplierAssignments, depot);
        
        // Solve the problem
        Solver<TeaLeafSolution> solver = solverFactory.buildSolver();
        TeaLeafSolution solution = solver.solve(problem);
        
        return solution;
    }
    
    public TeaLeafSolution solveAsync(List<Truck> trucks, List<Supplier> suppliers, Depot depot) {
        // Create supplier assignments from suppliers
        List<SupplierAssignment> supplierAssignments = suppliers.stream()
                .map(SupplierAssignment::new)
                .collect(java.util.stream.Collectors.toList());
        
        // Create the problem
        TeaLeafSolution problem = new TeaLeafSolution(trucks, supplierAssignments, depot);
        
        // Start solving asynchronously
        Solver<TeaLeafSolution> solver = solverFactory.buildSolver();
        solver.solve(problem); // For now, using synchronous solve
        
        // In a real implementation, you'd want to use a proper async approach
        // with a callback or return a Future/CompletableFuture
        return problem;
    }
}
