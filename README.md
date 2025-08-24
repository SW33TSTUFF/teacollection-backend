# Tea Leaf Collection Optimization System

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-green.svg)](https://spring.io/projects/spring-boot)
[![OptaPlanner](https://img.shields.io/badge/OptaPlanner-10.1.0-blue.svg)](https://www.optaplanner.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Implementation Guide](#implementation-guide)
- [API Documentation](#api-documentation)
- [Development Workflow](#development-workflow)
- [Testing](#testing)
- [Deployment](#deployment)
- [Contributing](#contributing)

## 🎯 Project Overview

### Goal
Design and implement a system that efficiently collects tea leaves from multiple suppliers, minimizing truck overloading, underloading, and idle trips. Trucks start and end at the factory (depot), and supplier availability is dynamic.

### Key Challenges
- **Truck Overloading**: Ensure capacity is never exceeded
- **Truck Underloading**: Avoid dispatching trucks for very small amounts if avoidable
- **Dynamic Supplier Availability**: Suppliers may become ready after initial planning
- **Route Optimization**: Minimize travel distance/time

### Solution
A Spring Boot web application with OptaPlanner for route optimization and dynamic dispatch logic. Frontend dashboard for visualization and real-time monitoring.

## 🛠️ Technology Stack

| Layer | Technology | Purpose |
|-------|------------|---------|
| **Frontend** | React.js  | Dashboard showing trucks, suppliers, routes on a map |
| **Backend** | Java Spring Boot 3.5.5 | REST API, business logic, dynamic dispatch |
| **Database** | PostgreSQL | Stores suppliers, trucks, routes, depot |
| **Routing Solver** | OptaPlanner 10.1.0 | CVRP optimization with constraints |
| **Maps & Visualization** | Google Maps API / Leaflet | Display routes, compute distances (optional: OSRM) |
| **Build Tool** | Maven | Dependency management and build automation |
| **Java Version** | Java 21 | Latest LTS version with modern features |

## 🏗️ Architecture

### Recommended Approach: Monolithic Layered Architecture

#### Layers

**Presentation Layer:**
- React/Vue dashboard
- Map visualization of trucks/routes
- Tables with supplier statuses and loads

**Application Layer (Spring Boot Services):**
- `SupplierService` → manage supplier availability
- `TruckService` → track truck capacities, status, and location
- `RouteService` → manage routes in DB
- `SolverService` → wrap OptaPlanner for route optimization
- `DispatchScheduler` → implements dispatch rules (waiting thresholds, capacity checks)

**Domain Layer:**
- Entities: `Supplier`, `Truck`, `Route`, `Depot`
- OptaPlanner Planning Entities & Variables

**Persistence Layer:**
- Supplier, Truck, Route, Depot tables
- Spring Data JPA for ORM

**Integration Layer:**
- Maps API for distance calculation
- Optional GPS integration for trucks

### Dynamic Workflow

1. Suppliers update availability → database updated
2. Candidate suppliers pool generated → OptaPlanner solver triggered
3. Routes calculated and trucks assigned
4. Trucks dispatched based on dispatch rules
5. Frontend map updates in real-time
6. Loop repeats dynamically when new suppliers become ready

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6+
- PostgreSQL 15+
- IDE (IntelliJ IDEA, Eclipse, or VS Code with Java extensions)

### Quick Start

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd teacollection-backend
   ```

2. **Set up database**
   ```bash
   # Create PostgreSQL database
   createdb teacollection
   ```

3. **Configure application properties**
   ```bash
   # Edit src/main/resources/application.properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/teacollection
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - Backend API: http://localhost:8080
   - API Documentation: http://localhost:8080/swagger-ui.html (if Swagger is added)

## 📚 Implementation Guide

### Step 1: Project Setup ✅

The project is already set up with:
- Spring Boot 3.5.5
- Spring Data JPA
- PostgreSQL Driver
- Lombok
- OptaPlanner 10.1.0

### Step 2: Database Design ✅

Core entities are already defined:
- `Supplier(id, latitude, longitude, harvestWeight, isReady, availableFrom, availableUntil)`
- `Truck(id, maxCapacity, currentLoad, status)`
- `Depot(latitude, longitude)`

**Next**: Create the `Route` entity and implement repositories.

### Step 3: Domain Modeling ✅

Basic entities are implemented:
- `Supplier` → Planning entity for OptaPlanner
- `Truck` → Vehicle with capacity constraints
- `Depot` → Central location for trucks

**Next**: Model Planning Entities and Variables for OptaPlanner.

### Step 4: Distance/Travel Time Calculation

Implement a `DistanceMatrixService`:
```java
@Service
public class DistanceMatrixService {
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Implement Haversine formula or integrate with Google Maps API/OSRM
    }
}
```

### Step 5: Route Optimization with OptaPlanner

Define Constraints:
- **Hard Constraints:**
  - Truck capacity ≤ max
  - Supplier visited exactly once
  - Route starts/ends at depot
- **Soft Constraints:**
  - Minimize total travel distance
  - Avoid underloaded trucks (optional)

### Step 6: REST API Endpoints

Implement these endpoints:
- `GET/POST/PUT /suppliers` → supplier info
- `GET /trucks` → truck status
- `GET /routes` → optimized routes
- `POST /optimize` → trigger solver

### Step 7: Dynamic Updates & Active Monitoring

Event-driven updates:
```java
@EventListener
public void handleSupplierReady(SupplierReadyEvent event) {
    supplierService.updateStatus(event.getSupplierId(), true);
    solverService.recalculateRoutes();
    dispatchService.evaluateDispatch();
}
```

### Step 8: Dispatch Logic

Dispatch Rules:
- Dispatch immediately if truck near capacity
- Wait X minutes for more suppliers if truck underloaded
- Assign new ready suppliers dynamically to idle trucks
- Update truck status: `IDLE` → `EN_ROUTE` → `RETURNING`

### Step 9: Frontend Dashboard

Map showing:
- Depot, trucks, suppliers
- Dynamic updates for trucks en route
- Table view with supplier details and truck assignments

## 🔌 API Documentation

### Core Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/suppliers` | Get all suppliers |
| `POST` | `/suppliers` | Create new supplier |
| `PUT` | `/suppliers/{id}` | Update supplier |
| `GET` | `/trucks` | Get all trucks |
| `GET` | `/routes` | Get optimized routes |
| `POST` | `/optimize` | Trigger route optimization |

### Data Models

#### Supplier
```json
{
  "id": 1,
  "latitude": 40.7128,
  "longitude": -74.0060,
  "harvestWeight": 150.5,
  "isReady": true,
  "availableFrom": "2024-01-15T08:00:00",
  "availableUntil": "2024-01-15T18:00:00"
}
```

#### Truck
```json
{
  "id": 1,
  "maxCapacity": 1000.0,
  "currentLoad": 0.0,
  "status": "IDLE"
}
```

## 🔄 Development Workflow

### Event-Driven Architecture

1. **Supplier Ready Event** → Triggers route recalculation
2. **Route Optimization** → OptaPlanner generates new routes
3. **Dispatch Evaluation** → Dispatch rules applied
4. **Frontend Update** → Real-time map and table updates

### Scheduled Tasks

Backup polling mechanism:
```java
@Scheduled(fixedRate = 30000) // Every 30 seconds
public void checkForNewSuppliers() {
    // Check database for new ready suppliers
    // Trigger route recalculation if needed
}
```

## 🧪 Testing

### Testing Strategy

1. **Unit Tests**: Test individual services and components
2. **Integration Tests**: Test API endpoints and database operations
3. **OptaPlanner Tests**: Validate constraint satisfaction
4. **Performance Tests**: Test with increasing dataset sizes

### Test Data

Start with small datasets:
- 2-3 trucks
- 5-10 suppliers
- Validate constraints and optimization

Gradually scale up to test performance.

## 🚀 Deployment

### Production Considerations

- **Environment Variables**: Use environment variables for database credentials
- **Health Checks**: Implement health check endpoints
- **Monitoring**: Add logging and metrics
- **Scaling**: Consider horizontal scaling for high load

### Docker Support

```dockerfile
FROM openjdk:21-jdk-slim
COPY target/teacollection-backend-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🤝 Contributing

### Development Setup

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

### Code Style

- Follow Java coding conventions
- Use meaningful variable and method names
- Add comprehensive documentation
- Include unit tests for new features

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

For questions and support:
- Create an issue in the repository
- Contact the development team
- Check the documentation and examples

## 🔮 Future Enhancements

- **Predictive Analytics**: ML model for supplier availability prediction
- **Real-time GPS**: Live truck tracking and route updates
- **WebSocket Integration**: Real-time frontend updates
- **Mobile App**: Native mobile application for field workers
- **Advanced Analytics**: Performance metrics and optimization insights

---

**Happy Coding! 🚛🍃**
