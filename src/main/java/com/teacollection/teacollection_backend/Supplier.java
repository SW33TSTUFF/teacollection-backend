package com.teacollection.teacollection_backend;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data; // From the Lombok dependency
import java.time.LocalDateTime;

@Entity // Tells Spring this is a database entity
@Data   // Lombok annotation to auto-generate getters, setters, etc.
public class Supplier {
    @Id // Marks this field as the primary key
    private Long id;
    private double latitude;
    private double longitude;
    private double harvestWeight;
    private boolean isReady;
    private LocalDateTime availableFrom;
    private LocalDateTime availableUntil;
}