package com.teacollection.teacollection_backend.service.impl;

import com.teacollection.teacollection_backend.Supplier;
import com.teacollection.teacollection_backend.repository.SupplierRepository;
import com.teacollection.teacollection_backend.service.SupplierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> getAllSuppliers() {
        log.info("Retrieving all suppliers");
        return supplierRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Supplier getSupplierById(Long id) {
        log.info("Retrieving supplier with ID: {}", id);
        return supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + id));
    }

    @Override
    public Supplier createSupplier(Supplier supplier) {
        log.info("Creating new supplier");
        
        // Set default values if not provided
        if (supplier.getAvailableFrom() == null) {
            supplier.setAvailableFrom(LocalDateTime.now());
        }
        if (supplier.getAvailableUntil() == null) {
            supplier.setAvailableUntil(LocalDateTime.now().plusHours(8)); // Default 8-hour window
        }
        
        Supplier savedSupplier = supplierRepository.save(supplier);
        log.info("Created supplier with ID: {}", savedSupplier.getId());
        return savedSupplier;
    }

    @Override
    public Supplier updateSupplier(Long id, Supplier supplier) {
        log.info("Updating supplier with ID: {}", id);
        
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Supplier not found with ID: " + id);
        }
        
        supplier.setId(id);
        Supplier updatedSupplier = supplierRepository.save(supplier);
        log.info("Updated supplier with ID: {}", id);
        return updatedSupplier;
    }

    @Override
    public void deleteSupplier(Long id) {
        log.info("Deleting supplier with ID: {}", id);
        
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Supplier not found with ID: " + id);
        }
        
        supplierRepository.deleteById(id);
        log.info("Deleted supplier with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> getReadySuppliers() {
        log.info("Retrieving ready suppliers");
        return supplierRepository.findByIsReadyTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> getSuppliersByStatus(boolean isReady) {
        log.info("Retrieving suppliers with status: {}", isReady);
        return supplierRepository.findSuppliersByStatusAndAvailability(isReady, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> getSuppliersNearby(double latitude, double longitude, double radiusKm) {
        log.info("Retrieving suppliers within {}km radius from ({}, {})", radiusKm, latitude, longitude);
        return supplierRepository.findSuppliersWithinRadius(latitude, longitude, radiusKm);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> getSuppliersByWeight(double minWeight) {
        log.info("Retrieving suppliers with harvest weight >= {}", minWeight);
        return supplierRepository.findByHarvestWeightGreaterThanEqual(minWeight);
    }

    @Override
    public Supplier updateSupplierStatus(Long id, boolean isReady) {
        log.info("Updating supplier {} status to: {}", id, isReady);
        
        Supplier supplier = getSupplierById(id);
        supplier.setReady(isReady);
        
        Supplier updatedSupplier = supplierRepository.save(supplier);
        log.info("Updated supplier {} status to: {}", id, isReady);
        return updatedSupplier;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> getSuppliersBecomingAvailable(int minutesAhead) {
        log.info("Retrieving suppliers becoming available within {} minutes", minutesAhead);
        
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime futureTime = currentTime.plusMinutes(minutesAhead);
        
        return supplierRepository.findSuppliersBecomingAvailableSoon(currentTime, futureTime);
    }

    @Override
    @Transactional(readOnly = true)
    public long getSupplierCount(boolean isReady) {
        log.info("Counting suppliers with status: {}", isReady);
        return supplierRepository.countByIsReady(isReady);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> getSuppliersAvailableInWindow(LocalDateTime startTime, LocalDateTime endTime) {
        log.info("Retrieving suppliers available in time window: {} to {}", startTime, endTime);
        return supplierRepository.findAvailableSuppliersInTimeWindow(startTime, endTime);
    }
}
