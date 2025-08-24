# Tea Collection Backend API Documentation

## Base URL
```
http://localhost:8080/api
```

---

## 📋 Supplier Management API

**Base Path:** `/api/suppliers`

### **GET** `/api/suppliers`
Get all suppliers
- **Response:** List of all suppliers
- **Example:** `curl http://localhost:8080/api/suppliers`

### **GET** `/api/suppliers/{id}`
Get supplier by ID
- **Parameters:** `id` (Long) - Supplier ID
- **Response:** Supplier object or 404 if not found
- **Example:** `curl http://localhost:8080/api/suppliers/1`

### **POST** `/api/suppliers`
Create a new supplier
- **Request Body:** Supplier object
- **Response:** Created supplier with generated ID
- **Example:**
```bash
curl -X POST http://localhost:8080/api/suppliers \
  -H "Content-Type: application/json" \
  -d '{
    "latitude": 40.7128,
    "longitude": -74.006,
    "harvestWeight": 150.5,
    "isReady": true,
    "availableFrom": "2024-01-15T08:00:00",
    "availableUntil": "2024-01-15T18:00:00"
  }'
```

### **PUT** `/api/suppliers/{id}`
Update an existing supplier
- **Parameters:** `id` (Long) - Supplier ID
- **Request Body:** Updated supplier object
- **Response:** Updated supplier
- **Example:**
```bash
curl -X PUT http://localhost:8080/api/suppliers/1 \
  -H "Content-Type: application/json" \
  -d '{
    "latitude": 40.7128,
    "longitude": -74.006,
    "harvestWeight": 200.0,
    "isReady": false,
    "availableFrom": "2024-01-15T08:00:00",
    "availableUntil": "2024-01-15T18:00:00"
  }'
```

### **DELETE** `/api/suppliers/{id}`
Delete a supplier
- **Parameters:** `id` (Long) - Supplier ID
- **Response:** 204 No Content if successful
- **Example:** `curl -X DELETE http://localhost:8080/api/suppliers/1`

### **GET** `/api/suppliers/ready`
Get all ready suppliers
- **Response:** List of suppliers ready for collection
- **Example:** `curl http://localhost:8080/api/suppliers/ready`

### **GET** `/api/suppliers/status/{isReady}`
Get suppliers by availability status
- **Parameters:** `isReady` (boolean) - Ready status
- **Response:** List of suppliers with specified status
- **Example:** `curl http://localhost:8080/api/suppliers/status/true`

### **GET** `/api/suppliers/nearby`
Get suppliers within a certain radius
- **Query Parameters:**
  - `latitude` (double) - Center latitude
  - `longitude` (double) - Center longitude
  - `radiusKm` (double) - Radius in kilometers
- **Response:** List of suppliers within the radius
- **Example:** `curl "http://localhost:8080/api/suppliers/nearby?latitude=40.7128&longitude=-74.006&radiusKm=10"`

### **GET** `/api/suppliers/weight`
Get suppliers with harvest weight above threshold
- **Query Parameters:** `minWeight` (double) - Minimum harvest weight
- **Response:** List of suppliers meeting weight criteria
- **Example:** `curl "http://localhost:8080/api/suppliers/weight?minWeight=100"`

### **PATCH** `/api/suppliers/{id}/status`
Update supplier readiness status
- **Parameters:** `id` (Long) - Supplier ID
- **Query Parameters:** `isReady` (boolean) - New readiness status
- **Response:** Updated supplier
- **Example:** `curl -X PATCH "http://localhost:8080/api/suppliers/1/status?isReady=true"`

### **GET** `/api/suppliers/becoming-available`
Get suppliers becoming available soon
- **Query Parameters:** `minutesAhead` (int, default: 30) - Minutes ahead to look
- **Response:** List of suppliers becoming available soon
- **Example:** `curl "http://localhost:8080/api/suppliers/becoming-available?minutesAhead=60"`

### **GET** `/api/suppliers/count`
Get supplier count by status
- **Query Parameters:** `isReady` (boolean) - Ready status
- **Response:** Count of suppliers with specified status
- **Example:** `curl "http://localhost:8080/api/suppliers/count?isReady=true"`

### **GET** `/api/suppliers/available-in-window`
Get suppliers available in a time window
- **Query Parameters:**
  - `startTime` (LocalDateTime) - Start of availability window
  - `endTime` (LocalDateTime) - End of availability window
- **Response:** List of available suppliers in the time window
- **Example:** `curl "http://localhost:8080/api/suppliers/available-in-window?startTime=2024-01-15T08:00:00&endTime=2024-01-15T18:00:00"`

---

## 🚛 Truck Management API

**Base Path:** `/api/trucks`

### **GET** `/api/trucks`
Get all trucks
- **Response:** List of all trucks
- **Example:** `curl http://localhost:8080/api/trucks`

### **GET** `/api/trucks/{id}`
Get truck by ID
- **Parameters:** `id` (Long) - Truck ID
- **Response:** Truck object or 404 if not found
- **Example:** `curl http://localhost:8080/api/trucks/1`

### **POST** `/api/trucks`
Create a new truck
- **Request Body:** Truck object
- **Response:** Created truck with generated ID
- **Example:**
```bash
curl -X POST http://localhost:8080/api/trucks \
  -H "Content-Type: application/json" \
  -d '{
    "capacity": 1000.0,
    "currentLoad": 0.0,
    "fuelLevel": 85.5,
    "isAvailable": true,
    "locationLatitude": 40.7128,
    "locationLongitude": -74.006
  }'
```

### **PUT** `/api/trucks/{id}`
Update an existing truck
- **Parameters:** `id` (Long) - Truck ID
- **Request Body:** Updated truck object
- **Response:** Updated truck
- **Example:**
```bash
curl -X PUT http://localhost:8080/api/trucks/1 \
  -H "Content-Type: application/json" \
  -d '{
    "capacity": 1000.0,
    "currentLoad": 150.5,
    "fuelLevel": 80.0,
    "isAvailable": false,
    "locationLatitude": 40.7128,
    "locationLongitude": -74.006
  }'
```

### **DELETE** `/api/trucks/{id}`
Delete a truck
- **Parameters:** `id` (Long) - Truck ID
- **Response:** 204 No Content if successful
- **Example:** `curl -X DELETE http://localhost:8080/api/trucks/1`

### **GET** `/api/trucks/available`
Get all available trucks
- **Response:** List of available trucks
- **Example:** `curl http://localhost:8080/api/trucks/available`

### **GET** `/api/trucks/capacity/{minCapacity}`
Get trucks with capacity above threshold
- **Parameters:** `minCapacity` (double) - Minimum capacity
- **Response:** List of trucks meeting capacity criteria
- **Example:** `curl http://localhost:8080/api/trucks/capacity/500`

### **GET** `/api/trucks/fuel/{minFuelLevel}`
Get trucks with fuel level above threshold
- **Parameters:** `minFuelLevel` (double) - Minimum fuel level
- **Response:** List of trucks meeting fuel criteria
- **Example:** `curl http://localhost:8080/api/trucks/fuel/50`

### **PATCH** `/api/trucks/{id}/load`
Update truck current load
- **Parameters:** `id` (Long) - Truck ID
- **Query Parameters:** `load` (double) - New current load
- **Response:** Updated truck
- **Example:** `curl -X PATCH "http://localhost:8080/api/trucks/1/load?load=150.5"`

### **PATCH** `/api/trucks/{id}/fuel`
Update truck fuel level
- **Parameters:** `id` (Long) - Truck ID
- **Query Parameters:** `fuelLevel` (double) - New fuel level
- **Response:** Updated truck
- **Example:** `curl -X PATCH "http://localhost:8080/api/trucks/1/fuel?fuelLevel=75.0"`

### **PATCH** `/api/trucks/{id}/availability`
Update truck availability status
- **Parameters:** `id` (Long) - Truck ID
- **Query Parameters:** `isAvailable` (boolean) - New availability status
- **Response:** Updated truck
- **Example:** `curl -X PATCH "http://localhost:8080/api/trucks/1/availability?isAvailable=false"`

### **GET** `/api/trucks/nearby`
Get trucks within a certain radius
- **Query Parameters:**
  - `latitude` (double) - Center latitude
  - `longitude` (double) - Center longitude
  - `radiusKm` (double) - Radius in kilometers
- **Response:** List of trucks within the radius
- **Example:** `curl "http://localhost:8080/api/trucks/nearby?latitude=40.7128&longitude=-74.006&radiusKm=5"`

### **GET** `/api/trucks/count`
Get truck count by availability
- **Query Parameters:** `isAvailable` (boolean) - Availability status
- **Response:** Count of trucks with specified status
- **Example:** `curl "http://localhost:8080/api/trucks/count?isAvailable=true"`

---

## 🏢 Depot Management API

**Base Path:** `/api/depots`

### **GET** `/api/depots`
Get all depots
- **Response:** List of all depots
- **Example:** `curl http://localhost:8080/api/depots`

### **GET** `/api/depots/{id}`
Get depot by ID
- **Parameters:** `id` (Long) - Depot ID
- **Response:** Depot object or 404 if not found
- **Example:** `curl http://localhost:8080/api/depots/1`

### **POST** `/api/depots`
Create a new depot
- **Request Body:** Depot object
- **Response:** Created depot with generated ID
- **Example:**
```bash
curl -X POST http://localhost:8080/api/depots \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Central Processing Center",
    "latitude": 40.7128,
    "longitude": -74.006,
    "capacity": 5000.0,
    "isActive": true
  }'
```

### **PUT** `/api/depots/{id}`
Update an existing depot
- **Parameters:** `id` (Long) - Depot ID
- **Request Body:** Updated depot object
- **Response:** Updated depot
- **Example:**
```bash
curl -X PUT http://localhost:8080/api/depots/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Central Processing Center",
    "latitude": 40.7128,
    "longitude": -74.006,
    "capacity": 6000.0,
    "isActive": true
  }'
```

### **DELETE** `/api/depots/{id}`
Delete a depot
- **Parameters:** `id` (Long) - Depot ID
- **Response:** 204 No Content if successful
- **Example:** `curl -X DELETE http://localhost:8080/api/depots/1`

### **GET** `/api/depots/active`
Get all active depots
- **Response:** List of active depots
- **Example:** `curl http://localhost:8080/api/depots/active`

### **GET** `/api/depots/capacity/{minCapacity}`
Get depots with capacity above threshold
- **Parameters:** `minCapacity` (double) - Minimum capacity
- **Response:** List of depots meeting capacity criteria
- **Example:** `curl http://localhost:8080/api/depots/capacity/1000`

### **PATCH** `/api/depots/{id}/status`
Update depot active status
- **Parameters:** `id` (Long) - Depot ID
- **Query Parameters:** `isActive` (boolean) - New active status
- **Response:** Updated depot
- **Example:** `curl -X PATCH "http://localhost:8080/api/depots/1/status?isActive=false"`

### **GET** `/api/depots/nearby`
Get depots within a certain radius
- **Query Parameters:**
  - `latitude` (double) - Center latitude
  - `longitude` (double) - Center longitude
  - `radiusKm` (double) - Radius in kilometers
- **Response:** List of depots within the radius
- **Example:** `curl "http://localhost:8080/api/depots/nearby?latitude=40.7128&longitude=-74.006&radiusKm=20"`

---

## 🛣️ Route Management API

**Base Path:** `/api/routes`

### **GET** `/api/routes`
Get all routes
- **Response:** List of all routes
- **Example:** `curl http://localhost:8080/api/routes`

### **GET** `/api/routes/{id}`
Get route by ID
- **Parameters:** `id` (Long) - Route ID
- **Response:** Route object or 404 if not found
- **Example:** `curl http://localhost:8080/api/routes/1`

### **POST** `/api/routes`
Create a new route
- **Request Body:** Route object
- **Response:** Created route with generated ID
- **Example:**
```bash
curl -X POST http://localhost:8080/api/routes \
  -H "Content-Type: application/json" \
  -d '{
    "truck": {"id": 1},
    "supplierSequence": [{"id": 1}, {"id": 2}],
    "depot": {"id": 1},
    "totalDistance": 25.5,
    "totalWeight": 350.7,
    "status": "PLANNED",
    "estimatedStartTime": "2024-01-15T08:00:00",
    "estimatedEndTime": "2024-01-15T12:00:00"
  }'
```

### **PUT** `/api/routes/{id}`
Update an existing route
- **Parameters:** `id` (Long) - Route ID
- **Request Body:** Updated route object
- **Response:** Updated route
- **Example:**
```bash
curl -X PUT http://localhost:8080/api/routes/1 \
  -H "Content-Type: application/json" \
  -d '{
    "truck": {"id": 1},
    "supplierSequence": [{"id": 1}, {"id": 2}, {"id": 3}],
    "depot": {"id": 1},
    "totalDistance": 35.2,
    "totalWeight": 525.8,
    "status": "IN_PROGRESS",
    "estimatedStartTime": "2024-01-15T08:00:00",
    "estimatedEndTime": "2024-01-15T14:00:00"
  }'
```

### **DELETE** `/api/routes/{id}`
Delete a route
- **Parameters:** `id` (Long) - Route ID
- **Response:** 204 No Content if successful
- **Example:** `curl -X DELETE http://localhost:8080/api/routes/1`

### **GET** `/api/routes/status/{status}`
Get routes by status
- **Parameters:** `status` (String) - Route status (PLANNED, IN_PROGRESS, COMPLETED, CANCELLED, FAILED)
- **Response:** List of routes with specified status
- **Example:** `curl http://localhost:8080/api/routes/status/PLANNED`

### **GET** `/api/routes/truck/{truckId}`
Get routes for a specific truck
- **Parameters:** `truckId` (Long) - Truck ID
- **Response:** List of routes for the truck
- **Example:** `curl http://localhost:8080/api/routes/truck/1`

### **GET** `/api/routes/depot/{depotId}`
Get routes for a specific depot
- **Parameters:** `depotId` (Long) - Depot ID
- **Response:** List of routes for the depot
- **Example:** `curl http://localhost:8080/api/routes/depot/1`

### **PATCH** `/api/routes/{id}/status`
Update route status
- **Parameters:** `id` (Long) - Route ID
- **Query Parameters:** `status` (String) - New route status
- **Response:** Updated route
- **Example:** `curl -X PATCH "http://localhost:8080/api/routes/1/status?status=IN_PROGRESS"`

### **GET** `/api/routes/date/{date}`
Get routes for a specific date
- **Parameters:** `date` (String) - Date in YYYY-MM-DD format
- **Response:** List of routes for the date
- **Example:** `curl http://localhost:8080/api/routes/date/2024-01-15`

### **GET** `/api/routes/optimization/trigger`
Trigger route optimization
- **Response:** Optimization result
- **Example:** `curl -X GET http://localhost:8080/api/routes/optimization/trigger`

---

## 🔧 Optimization API

**Base Path:** `/api/optimization`

### **POST** `/api/optimization/calculate`
Calculate optimal routes
- **Request Body:** Optimization parameters
- **Response:** Optimization result with routes
- **Example:**
```bash
curl -X POST http://localhost:8080/api/optimization/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "depotId": 1,
    "truckIds": [1, 2, 3],
    "supplierIds": [1, 2, 3, 4, 5],
    "constraints": {
      "maxRouteTime": 480,
      "maxRouteDistance": 100,
      "preferCloserSuppliers": true
    }
  }'
```

### **GET** `/api/optimization/status/{jobId}`
Get optimization job status
- **Parameters:** `jobId` (String) - Optimization job ID
- **Response:** Job status and progress
- **Example:** `curl http://localhost:8080/api/optimization/status/abc123`

### **POST** `/api/optimization/cancel/{jobId}`
Cancel optimization job
- **Parameters:** `jobId` (String) - Optimization job ID
- **Response:** Cancellation confirmation
- **Example:** `curl -X POST http://localhost:8080/api/optimization/cancel/abc123`

---

## 📊 Data Models

### **Supplier Object:**
```json
{
  "id": 1,
  "latitude": 40.7128,
  "longitude": -74.006,
  "harvestWeight": 150.5,
  "isReady": true,
  "availableFrom": "2024-01-15T08:00:00",
  "availableUntil": "2024-01-15T18:00:00"
}
```

### **Truck Object:**
```json
{
  "id": 1,
  "capacity": 1000.0,
  "currentLoad": 0.0,
  "fuelLevel": 85.5,
  "isAvailable": true,
  "locationLatitude": 40.7128,
  "locationLongitude": -74.006
}
```

### **Depot Object:**
```json
{
  "id": 1,
  "name": "Central Processing Center",
  "latitude": 40.7128,
  "longitude": -74.006,
  "capacity": 5000.0,
  "isActive": true
}
```

### **Route Object:**
```json
{
  "id": 1,
  "truck": {"id": 1},
  "supplierSequence": [{"id": 1}, {"id": 2}],
  "depot": {"id": 1},
  "totalDistance": 25.5,
  "totalWeight": 350.7,
  "status": "PLANNED",
  "estimatedStartTime": "2024-01-15T08:00:00",
  "estimatedEndTime": "2024-01-15T12:00:00",
  "createdAt": "2024-01-15T07:00:00",
  "updatedAt": "2024-01-15T07:00:00"
}
```

---

## 🚀 Quick Start Examples

### **Create Test Data:**
```bash
# Create 5 suppliers
curl -X POST http://localhost:8080/api/suppliers -H "Content-Type: application/json" -d '{"latitude": 40.7128, "longitude": -74.006, "harvestWeight": 150.5, "isReady": true, "availableFrom": "2024-01-15T08:00:00", "availableUntil": "2024-01-15T18:00:00"}'
curl -X POST http://localhost:8080/api/suppliers -H "Content-Type: application/json" -d '{"latitude": 40.7589, "longitude": -73.9851, "harvestWeight": 200.0, "isReady": true, "availableFrom": "2024-01-15T09:00:00", "availableUntil": "2024-01-15T17:00:00"}'
curl -X POST http://localhost:8080/api/suppliers -H "Content-Type: application/json" -d '{"latitude": 40.7505, "longitude": -73.9934, "harvestWeight": 175.3, "isReady": false, "availableFrom": "2024-01-15T10:00:00", "availableUntil": "2024-01-15T19:00:00"}'
curl -X POST http://localhost:8080/api/suppliers -H "Content-Type: application/json" -d '{"latitude": 40.7829, "longitude": -73.9654, "harvestWeight": 120.8, "isReady": true, "availableFrom": "2024-01-15T07:00:00", "availableUntil": "2024-01-15T16:00:00"}'
curl -X POST http://localhost:8080/api/suppliers -H "Content-Type: application/json" -d '{"latitude": 40.7549, "longitude": -73.9840, "harvestWeight": 300.2, "isReady": true, "availableFrom": "2024-01-15T06:00:00", "availableUntil": "2024-01-15T20:00:00"}'

# Create 4 trucks
curl -X POST http://localhost:8080/api/trucks -H "Content-Type: application/json" -d '{"capacity": 1000.0, "currentLoad": 0.0, "fuelLevel": 85.5, "isAvailable": true, "locationLatitude": 40.7128, "locationLongitude": -74.006}'
curl -X POST http://localhost:8080/api/trucks -H "Content-Type: application/json" -d '{"capacity": 800.0, "currentLoad": 0.0, "fuelLevel": 92.3, "isAvailable": true, "locationLatitude": 40.7589, "locationLongitude": -73.9851}'
curl -X POST http://localhost:8080/api/trucks -H "Content-Type: application/json" -d '{"capacity": 1200.0, "currentLoad": 0.0, "fuelLevel": 78.9, "isAvailable": true, "locationLatitude": 40.7505, "locationLongitude": -73.9934}'
curl -X POST http://localhost:8080/api/trucks -H "Content-Type: application/json" -d '{"capacity": 600.0, "currentLoad": 0.0, "fuelLevel": 95.1, "isAvailable": true, "locationLatitude": 40.7829, "locationLongitude": -73.9654}'

# Create 1 depot
curl -X POST http://localhost:8080/api/depots -H "Content-Type: application/json" -d '{"name": "Central Processing Center", "latitude": 40.7128, "longitude": -74.006, "capacity": 5000.0, "isActive": true}'
```

### **Verify Data:**
```bash
curl http://localhost:8080/api/suppliers
curl http://localhost:8080/api/trucks
curl http://localhost:8080/api/depots
```

---

## 📝 Notes

- All endpoints return JSON responses
- HTTP status codes follow REST conventions (200 OK, 201 Created, 404 Not Found, etc.)
- CORS is enabled for all origins (`@CrossOrigin(origins = "*")`)
- All timestamps are in ISO-8601 format (YYYY-MM-DDTHH:mm:ss)
- Coordinates use decimal degrees (latitude: -90 to 90, longitude: -180 to 180)
- Weights and capacities are in kilograms
- Distances are in kilometers
- Fuel levels are percentages (0-100)
