package com.teacollection.teacollection_backend.service;

import com.teacollection.teacollection_backend.Route;
import java.time.LocalDate;
import java.util.List;

public interface RouteService {
    
    List<Route> getAllRoutes();
    
    Route getRouteById(Long id);
    
    Route createRoute(Route route);
    
    Route updateRoute(Long id, Route route);
    
    void deleteRoute(Long id);
    
    List<Route> getRoutesByStatus(String status);
    
    List<Route> getRoutesByTruck(Long truckId);
    
    List<Route> getRoutesByDepot(Long depotId);
    
    Route updateRouteStatus(Long id, String status);
    
    List<Route> getRoutesByDate(String date);
    
    void triggerRouteOptimization();
}
