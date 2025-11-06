# Vehicle Insurance Management System

Sistema de gestión de vehículos y seguros vehiculares desarrollado en Java con JDBC y MySQL.

## 📋 Descripción del Dominio

Este proyecto implementa una relación **1→1 unidireccional** entre las entidades:
- **Vehicle (A)**: Vehículo con sus datos principales
- **InsuranceVehicle (B)**: Seguro vehicular asociado

Un vehículo puede tener **0 o 1 seguro**, y cada seguro está asociado a **exactamente 1 vehículo**.

## 🛠️ Requisitos

- **Java**: 21 o superior
- **MySQL**: 8.0 o superior
- **JDBC Driver**: MySQL Connector/J 8.0+

## 📦 Estructura del Proyecto
```
src/
├── config/          # Conexión a base de datos
├── entities/        # Clases de dominio (Vehicle, InsuranceVehicle, CoverType)
├── dao/             # Data Access Objects (patrón DAO)
├── service/         # Lógica de negocio y transacciones
├── exception/       # Excepciones personalizadas
└── main/            # Punto de entrada y menú de consola

resources/
└── database.properties  # Configuración de BD

sql/
├── create_database.sql  # Script de creación
└── insert_data.sql      # Datos de prueba
```

## 🚀 Instalación y Ejecución

### 1. Crear la Base de Datos
```bash
mysql -u root -p < sql/create_database.sql
mysql -u root -p < sql/insert_data.sql
```

### 2. Configurar Credenciales

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
- `is_active` (BOOLEAN)
- `plate` (VARCHAR(10) UNIQUE)
- `brand` (VARCHAR(50))
- `model` (VARCHAR(50))
- `year` (INT)
- `chassis_number` (VARCHAR(50) UNIQUE)

### Tabla InsuranceVehicle
- `id` (PK)
- `is_active` (BOOLEAN)
- `vehicle_id` (FK UNIQUE) ← Garantiza 1→1
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

1. **[Juan Pablo]** - Desarrollo de entidades, DAOs y transacciones
2. **[Mauricio Rios]** - Desarrollo de servicios insuranceService
3. **[Nahuel Riveros]** - Desarrollo del menú y validaciones
4. **[Brian Rios]** - Documentación, scripts SQL servicios insuranceVehicleService

## 🎥 Video Demostrativo

[Enlace al video en YouTube/Google Drive]

## 📝 Licencia

Este proyecto es parte del Trabajo Final Integrador de Programación 2 - UTN.