# Vehicle Insurance Management System
Sistema de gestión de vehículos y seguros vehiculares. Incluye script SQL único para crear y poblar la base en MySQL/MariaDB, y una app Java/JDBC con patrón DAO–Service–Main.
## 📋 Descripción del Dominio

Este proyecto implementa una relación **1→1 unidireccional** entre las entidades:
- **Vehicle (A)**: Vehículo con sus datos principales
- **InsuranceVehicle (B)**: Seguro vehicular asociado

Un vehículo puede tener **0 o 1 seguro**, y cada seguro está asociado a **exactamente 1 vehículo**.

## 🛠️ Requisitos

- **Java**: 21 o superior
- **MySQL**: 8.0 o superior
- **Xampp** Para levantar el puerto SQL
- **Workbench o phpMyAdmin para ejecutar el script SQL**
- **JDBC Driver**: MySQL Connector/J 8.0+

## 🗄️ Base de datos
- **Archivo**: script SQL único (ejecutar por bloques tal como está indicado en comentarios).

- **Crear el esquema**: vehiculos_db, tablas, FKs, triggers (baja lógica), CTE de carga masiva, vistas, usuarios/roles y pruebas de concurrencia
## 📦 Estructura del Proyecto
```
└── trabajo-practico-integrador-P2/
        ├── pom.xml
        └── src/
            ├── README.md
            ├── main/
               ├── java/
               │   └── com/
               │       └── mycompany/
               │           └── trabajo/
               │               └── practico/
               │                   └── integrador/
               │                       └── p2/
               │                           ├── AppMenu.java
               │                           ├── TrabajoPracticoIntegradorP2.java
               │                           ├── config/
               │                           │   └── DatabaseConnection.java
               │                           ├── daos/
               │                           │   ├── GenericDao.java
               │                           │   ├── InsuranceVehicleDao.java
               │                           │   └── VehicleDao.java
               │                           ├── entities/
               │                           │   ├── InsuranceVehicle.java
               │                           │   ├── Vehicle.java
               │                           │   └── enums/
               │                           │       └── CoverType.java
               │                           ├── exceptions/
               │                           │   ├── DatabaseException.java
               │                           │   ├── DuplicateEntityException.java
               │                           │   └── ValidationException.java
               │                           └── services/
               │                               ├── GenericService.java
               │                               ├── InsuranceVehicleService.java
               │                               └── VehicleService.java
               └── resources/
                   └── database.properties

```

## 🚀 Instalación y Ejecución

### 1. Abrí MySQL Workbench (o phpMyAdmin en XAMPP).
### 2. Pegá el script y ejecutá por bloques (las secciones están numeradas).
### 3.Verificá con las consultas de la sección 10 del script (SHOW TABLES, DESCRIBE, etc.).
### 4. Configurar Credenciales

Editar `resources/database.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/vehiculos_db
db.user=root
db.password=TU_PASSWORD_AQUI
```

### 3. Compilar y Ejecutar
```bash
# Compilar
javac -d bin -cp "lib/*" src/**/*.java

# Ejecutar
java -cp "bin:lib/*" main.Main
```

## 📊 Modelo de Datos

### Tabla Vehicle
- `vehicle_id` (PK)
- `is_active` (BOOLEAN) (baja lógica)
- `domain` (VARCHAR(10) UNIQUE)
- `brand` (VARCHAR(50))
- `model` (VARCHAR(50))
- `year` (INT)
- `chasis_number` (VARCHAR(50) UNIQUE) 
- `insurance_vehicle_id` (FK UNIQUE → 1:1)

### Tabla InsuranceVehicle
- `insurance_vehicle_id` (PK)
- `is_active` (BOOLEAN) (baja lógica) ← Garantiza 1→1
- `insurance_name` (VARCHAR(80))
- `policy_number` (VARCHAR(50) UNIQUE)
- `cover` (ENUM: RC, TERCEROS, TODO_RIESGO)
- `expiration_date` (DATE)

## ✨ Funcionalidades

### Gestión de Vehículos
- ✅ Crear vehículo (con o sin seguro)
- ✅ Listar todos los vehículos
- ✅ Ver detalles por ID
- ✅ Actualizar vehículo
- ✅ Eliminar vehículo (baja lógica)

### Gestión de Seguros
- ✅ Crear seguro
- ✅ Listar todos los seguros
- ✅ Ver detalles por ID
- ✅ Actualizar seguro
- ✅ Eliminar seguro (baja lógica)

### Búsquedas
- ✅ Buscar vehículo por patente
- ✅ Buscar seguro por número de póliza
- ✅ Buscar seguro por ID de vehículo
### Funcionalidades (app Java/JDBC)
- ✅CRUD de vehículos y seguros (con baja lógica isActive)
- ✅Transacciones con commit/rollback en Services
- ✅Relación 1:1 garantizada por FK UNIQUE y validaciones en Services
- ✅Menú de consola con mensajes claros de éxito/errores
## 🔒 Validaciones Implementadas

- Campos obligatorios (patente, marca, modelo, aseguradora, etc.)
- Unicidad de patente y número de póliza
- Formato de fecha válido
- Restricción 1→1 (un vehículo no puede tener más de un seguro)
- Año del vehículo entre 1900 y año actual + 1
- Fecha de vencimiento no puede ser en el pasado

## 🔄 Manejo de Transacciones

Todas las operaciones críticas usan transacciones:
```java
conn.setAutoCommit(false);
try {
    // Operaciones...
    conn.commit();
} catch (Exception e) {
    conn.rollback();
    throw e;
}
```

## 👥 Integrantes del Equipo

1. **[Juan Pablo Rivero]** - Desarrollo de entidades, DAOs y transacciones
2. **[Mauricio Rios]** - Desarrollo de servicios insuranceService
3. **[Nahuel Riveros]** - Desarrollo del menú y validaciones
4. **[Brian Rios]** - Documentación, scripts SQL servicios insuranceVehicleService

## 🎥 Video Demostrativo

[Enlace al video en YouTube/Google Drive]

## 📝 Licencia

Este proyecto es parte del Trabajo Final Integrador de Programación 2 - UTN.