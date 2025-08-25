package com.teacollection.teacollection_backend.controller;

import com.teacollection.teacollection_backend.*;
import com.teacollection.teacollection_backend.service.OptimizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/optimization")
@CrossOrigin(origins = "*")
public class OptimizationController {

    @Autowired
    private OptimizationService optimizationService;

    @PostMapping("/optimize")
    public ResponseEntity<TeaLeafSolution> optimizeRoutes() {
        // Create test data for demonstration
        List<Truck> trucks = createTestTrucks();
        List<Supplier> suppliers = createTestSuppliers();
        Depot depot = createTestDepot();

        // Run optimization
        TeaLeafSolution solution = optimizationService.optimizeTeaCollection(trucks, suppliers, depot);
        
        return ResponseEntity.ok(solution);
    }

    @PostMapping("/optimize-custom")
    public ResponseEntity<TeaLeafSolution> optimizeCustomRoutes(@RequestBody OptimizationRequest request) {
        // Convert request to domain objects
        List<Truck> trucks = request.getTrucks();
        List<Supplier> suppliers = request.getSuppliers();
        Depot depot = request.getDepot();

        // Run optimization
        TeaLeafSolution solution = optimizationService.optimizeTeaCollection(trucks, suppliers, depot);
        
        return ResponseEntity.ok(solution);
    }

    @GetMapping("/test-data")
    public ResponseEntity<Map<String, Object>> getTestData() {
        List<Truck> trucks = createTestTrucks();
        List<Supplier> suppliers = createTestSuppliers();
        Depot depot = createTestDepot();

        return ResponseEntity.ok(Map.of(
            "trucks", trucks,
            "suppliers", suppliers,
            "depot", depot
        ));
    }

    private List<Truck> createTestTrucks() {
        Truck truck1 = new Truck();
        truck1.setId(1L);
        truck1.setMaxCapacity(2000.0);
        truck1.setCurrentLoad(0.0);
        truck1.setStatus("IDLE");

        Truck truck2 = new Truck();
        truck2.setId(2L);
        truck2.setMaxCapacity(1500.0);
        truck2.setCurrentLoad(0.0);
        truck2.setStatus("IDLE");

        Truck truck3 = new Truck();
        truck3.setId(3L);
        truck3.setMaxCapacity(1000.0);
        truck3.setCurrentLoad(0.0);
        truck3.setStatus("IDLE");

        return Arrays.asList(truck1, truck2, truck3);
    }

    private List<Supplier> createTestSuppliers() {
        Supplier supplier1 = new Supplier();
        supplier1.setId(1L);
        supplier1.setLatitude(1.0);
        supplier1.setLongitude(1.0);
        supplier1.setHarvestWeight(800.0);
        supplier1.setReady(true);

        Supplier supplier2 = new Supplier();
        supplier2.setId(2L);
        supplier2.setLatitude(2.0);
        supplier2.setLongitude(1.0);
        supplier2.setHarvestWeight(600.0);
        supplier2.setReady(true);

        Supplier supplier3 = new Supplier();
        supplier3.setId(3L);
        supplier3.setLatitude(1.0);
        supplier3.setLongitude(2.0);
        supplier3.setHarvestWeight(400.0);
        supplier3.setReady(true);

        Supplier supplier4 = new Supplier();
        supplier4.setId(4L);
        supplier4.setLatitude(3.0);
        supplier4.setLongitude(2.0);
        supplier4.setHarvestWeight(1200.0);
        supplier4.setReady(true);

        Supplier supplier5 = new Supplier();
        supplier5.setId(5L);
        supplier5.setLatitude(2.0);
        supplier5.setLongitude(3.0);
        supplier5.setHarvestWeight(900.0);
        supplier5.setReady(true);

        return Arrays.asList(supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    private Depot createTestDepot() {
        Depot depot = new Depot();
        depot.setId(1L);
        depot.setLatitude(0.0);
        depot.setLongitude(0.0);
        return depot;
    }

    // Request DTO for custom optimization
    public static class OptimizationRequest {
        private List<Truck> trucks;
        private List<Supplier> suppliers;
        private Depot depot;

        // Getters and setters
        public List<Truck> getTrucks() { return trucks; }
        public void setTrucks(List<Truck> trucks) { this.trucks = trucks; }
        
        public List<Supplier> getSuppliers() { return suppliers; }
        public void setSuppliers(List<Supplier> suppliers) { this.suppliers = suppliers; }
        
        public Depot getDepot() { return depot; }
        public void setDepot(Depot depot) { this.depot = depot; }
    }
}
