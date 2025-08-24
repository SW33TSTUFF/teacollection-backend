package com.teacollection.teacollection_backend;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing a route assigned to a truck.
 * Contains the sequence of suppliers to visit and route metadata.
 */
@Entity
@Data
@Table(name = "routes")
public class Route {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truck_id", nullable = false)
    private Truck truck;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "route_suppliers",
        joinColumns = @JoinColumn(name = "route_id"),
        inverseJoinColumns = @JoinColumn(name = "supplier_id")
    )
    @OrderColumn(name = "visit_order")
    private List<Supplier> supplierSequence;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "depot_id", nullable = false)
    private Depot depot;
    
    private double totalDistance;
    
    private double totalWeight;
    
    @Enumerated(EnumType.STRING)
    private RouteStatus status;
    
    private LocalDateTime estimatedStartTime;
    
    private LocalDateTime estimatedEndTime;
    
    private LocalDateTime actualStartTime;
    
    private LocalDateTime actualEndTime;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Enum representing the status of a route
     */
    public enum RouteStatus {
        PLANNED,        // Route is planned but not yet started
        IN_PROGRESS,    // Route is currently being executed
        COMPLETED,      // Route has been completed successfully
        CANCELLED,      // Route has been cancelled
        FAILED          // Route failed to complete
    }
}
