package com.teacollection.teacollection_backend;

// An interface to represent any stop in a route, either the depot (represented by the Truck) or a supplier.
public interface RouteStop {
    Location getLocation();
    Truck getTruck(); // Helps to identify which route the stop belongs to
}
