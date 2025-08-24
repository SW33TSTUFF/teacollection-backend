package com.teacollection.teacollection_backend.service;

import com.teacollection.teacollection_backend.Supplier;
import java.time.LocalDateTime;
import java.util.List;

public interface SupplierService {
    
    List<Supplier> getAllSuppliers();
    
    Supplier getSupplierById(Long id);
    
    Supplier createSupplier(Supplier supplier);
    
    Supplier updateSupplier(Long id, Supplier supplier);
    
    void deleteSupplier(Long id);
    
    List<Supplier> getReadySuppliers();
    
    List<Supplier> getSuppliersByStatus(boolean isReady);
    
    List<Supplier> getSuppliersNearby(double latitude, double longitude, double radiusKm);
    
    List<Supplier> getSuppliersByWeight(double minWeight);
    
    Supplier updateSupplierStatus(Long id, boolean isReady);
    
    List<Supplier> getSuppliersBecomingAvailable(int minutesAhead);
    
    long getSupplierCount(boolean isReady);
    
    List<Supplier> getSuppliersAvailableInWindow(LocalDateTime startTime, LocalDateTime endTime);
}
