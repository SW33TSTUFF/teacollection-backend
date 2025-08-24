package com.teacollection.teacollection_backend.service;

import com.teacollection.teacollection_backend.Depot;
import java.util.List;

public interface DepotService {
    
    List<Depot> getAllDepots();
    
    Depot getDepotById(Long id);
    
    Depot createDepot(Depot depot);
    
    Depot updateDepot(Long id, Depot depot);
    
    void deleteDepot(Long id);
    
    List<Depot> getActiveDepots();
    
    List<Depot> getDepotsByCapacity(double minCapacity);
    
    Depot updateDepotStatus(Long id, boolean isActive);
    
    List<Depot> getDepotsNearby(double latitude, double longitude, double radiusKm);
}
