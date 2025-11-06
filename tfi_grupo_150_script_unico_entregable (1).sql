-- =============================================================
-- TFI – Bases de Datos II – Grupo 150 – Vehículo–Seguro
-- =============================================================
/* =============================================================
   !!EJECUTAR POR BLOQUES!!
   ============================================================= */
/* =============================================================
   1) CREACIÓN DE ESQUEMA Y TABLAS
   ============================================================= */
DROP DATABASE IF EXISTS vehiculos_db;
CREATE DATABASE vehiculos_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE vehiculos_db;

CREATE TABLE insurance_vehicle (
  insurance_vehicle_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  isActive BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Baja lógica',
  insurance_name VARCHAR(80) NOT NULL COMMENT 'máx. 80 caracteres',
  policy_number VARCHAR(50) NOT NULL UNIQUE COMMENT 'máx. 50 caracteres',
  cover ENUM('RC','Contra terceros','Todo_riesgo') NOT NULL COMMENT 'Tipo de cobertura',
  expire_date DATE NOT NULL COMMENT 'Fecha de vencimiento'
) ENGINE=InnoDB;

CREATE TABLE vehicle (
  vehicle_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  isActive BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Baja lógica',
  domain VARCHAR(10) NOT NULL UNIQUE COMMENT 'máx. 10 caracteres',
  brand VARCHAR(50) NOT NULL COMMENT 'máx. 50 caracteres',
  model VARCHAR(50) NOT NULL COMMENT 'máx. 50 caracteres',
  year INT NOT NULL COMMENT 'Rango válido 1900 - año actual',
  chasis_number VARCHAR(50) UNIQUE COMMENT 'máx. 50 caracteres',
  insurance_vehicle_id BIGINT UNIQUE COMMENT 'Relación 1:1',
  CONSTRAINT fk_vehicle_insurance FOREIGN KEY (insurance_vehicle_id)
    REFERENCES insurance_vehicle(insurance_vehicle_id)
    ON UPDATE CASCADE
) ENGINE=InnoDB;

/* =============================================================
   2) TRIGGERS DE VALIDACIÓN Y BLOQUEO DE BORRADO FÍSICO
   ============================================================= */

DROP TRIGGER IF EXISTS trg_vehicle_year_check_insert;
DROP TRIGGER IF EXISTS trg_vehicle_year_check_update;
DROP TRIGGER IF EXISTS trg_insurance_delete;
DROP TRIGGER IF EXISTS trg_vehicle_delete;

DELIMITER //
CREATE TRIGGER trg_vehicle_year_check_insert
BEFORE INSERT ON vehicle
FOR EACH ROW
BEGIN
    IF NEW.year < 1900 OR NEW.year > YEAR(CURDATE()) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El año debe estar entre 1900 y el año actual';
    END IF;
END;//

CREATE TRIGGER trg_vehicle_year_check_update
BEFORE UPDATE ON vehicle
FOR EACH ROW
BEGIN
    IF NEW.year < 1900 OR NEW.year > YEAR(CURDATE()) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El año debe estar entre 1900 y el año actual';
    END IF;
END;//
DELIMITER ;

DELIMITER $$
CREATE TRIGGER trg_vehicle_delete
BEFORE DELETE ON vehicle
FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Prohibido DELETE físico en vehicle. Use UPDATE isActive=FALSE para baja lógica.';
END$$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER trg_insurance_delete
BEFORE DELETE ON insurance_vehicle
FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Prohibido DELETE físico en insurance_vehicle. Use UPDATE isActive=FALSE para baja lógica.';
END$$
DELIMITER ;

/* =============================================================
   3) INSERCIÓN DE DATOS DE PRUEBA
   ============================================================= 
   Nota: Se omiten las inserciones manuales iniciales para evitar
   conflictos de claves primarias y foráneas.
   La carga masiva genera datos de prueba válidos automáticamente.
   */
/* =============================================================
   4) CARGA MASIVA (CTE RECURSIVA)
   ============================================================= */
SET @@cte_max_recursion_depth = 100000;
INSERT INTO insurance_vehicle (isActive, insurance_name, policy_number, cover, expire_date)
SELECT TRUE, CONCAT('Aseguradora ', 'La Caja'), CONCAT('POL', n),
       ELT(FLOOR(1 + (RAND()*3)), 'RC','Contra terceros','Todo_riesgo'),
       DATE_ADD(CURDATE(), INTERVAL (n MOD 365) DAY)
FROM (
  WITH RECURSIVE nums AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM nums WHERE n < 100000
  )
  SELECT n FROM nums
) seq;

INSERT INTO vehicle (isActive, domain, brand, model, year, chasis_number, insurance_vehicle_id)
SELECT TRUE, CONCAT('DOM', LPAD(n,6,'0')),
       ELT(FLOOR(1 + (RAND()*3)), 'Toyota','Ford','Chevrolet'),
       ELT(FLOOR(1 + (RAND()*3)), 'Base','Intermedio','Premium'),
       FLOOR(1901 + (RAND() * (YEAR(CURDATE()) - 1901))),
       CONCAT('CHASIS', n), n
FROM (
  WITH RECURSIVE nums AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM nums WHERE n < 100000
  )
  SELECT n FROM nums
) seq;
/* =============================================================
   5) VISTAS Y ACTUALIZACIONES
   ============================================================= */
CREATE VIEW parque_automotor_antiguo AS
SELECT v.brand, v.model, v.year, iv.isActive, iv.policy_number, iv.cover, iv.expire_date
FROM vehicle v
JOIN insurance_vehicle iv ON iv.insurance_vehicle_id = v.vehicle_id
HAVING year < 2015;

CREATE VIEW vehiculos_proximos_baja AS
SELECT paa.brand, paa.model, paa.year
FROM parque_automotor_antiguo paa;

CREATE VIEW active_vehicles AS
SELECT v.vehicle_id, v.domain, v.brand, v.model, i.insurance_name, i.policy_number, i.expire_date
FROM vehicle v
JOIN insurance_vehicle i ON v.insurance_vehicle_id = i.insurance_vehicle_id
WHERE v.isActive = TRUE AND i.isActive = TRUE;

SET SQL_SAFE_UPDATES = 0;

UPDATE insurance_vehicle iv
JOIN vehicle v ON iv.insurance_vehicle_id = v.vehicle_id
SET iv.isActive = 0
WHERE v.year < 2015;

SET SQL_SAFE_UPDATES = 1;

/* =============================================================
   6) SEGURIDAD Y VISTAS LIMITADAS
   ============================================================= */
CREATE USER IF NOT EXISTS 'usuario_seguro'@'localhost' IDENTIFIED BY 'root';
ALTER USER 'usuario_seguro'@'localhost' PASSWORD EXPIRE INTERVAL 360 DAY;
GRANT SELECT ON vehiculos_db.* TO 'usuario_seguro'@'localhost';

CREATE USER IF NOT EXISTS 'usuario_admin'@'localhost' IDENTIFIED BY 'root';
ALTER USER 'usuario_admin'@'localhost' PASSWORD EXPIRE INTERVAL 500 DAY;
GRANT SELECT, INSERT, UPDATE, DELETE ON vehiculos_db.* TO 'usuario_admin'@'localhost';
FLUSH PRIVILEGES;

CREATE OR REPLACE VIEW vista_insurance_vehicle_information AS
SELECT insurance_name, policy_number, cover, expire_date FROM insurance_vehicle;

CREATE OR REPLACE VIEW vista_vehicle_information AS
SELECT domain, brand, model, year, chasis_number FROM vehicle;

REVOKE ALL PRIVILEGES ON vehiculos_db.* FROM 'usuario_seguro'@'localhost';
GRANT SELECT ON vehiculos_db.vista_insurance_vehicle_information TO 'usuario_seguro'@'localhost';
GRANT SELECT ON vehiculos_db.vista_vehicle_information TO 'usuario_seguro'@'localhost';
FLUSH PRIVILEGES;

/* =============================================================
   7) PRUEBAS DE INTEGRIDAD Y VALIDACIÓN
   ============================================================= */
-- CHECK por trigger (año fuera de rango)
/*INSERT INTO vehicle (isActive, domain, brand, model, year, chasis_number)
VALUES (TRUE, 'EF789GH', 'Renault', 'Clio', 1800, 'CHASIS003');

-- UNIQUE (dominio duplicado)
INSERT INTO vehicle (isActive, domain, brand, model, year, chasis_number)
VALUES (TRUE, 'CD456EF', 'Fiat', 'Argo', 2022, 'CHASIS004');*/

/* =============================================================
   8) CONCURRENCIA Y TRANSACCIONES (EJECUCIÓN GUIADA PASO A PASO)
   ============================================================= */

-- Instrucciones generales
-- Abra DOS consolas/ventanas de MySQL: "SESION A" y "SESION B" (misma BD: vehiculos_db)
-- Copie/pegue los bloques indicados en el orden señalado. No cierre la transacción
-- hasta que el paso lo pida. Observe bloqueos, lecturas no repetibles y diferencias
-- entre READ COMMITTED y REPEATABLE READ.

USE vehiculos_db;

-- =============================================================
-- A) DEMO DE DEADLOCK / BLOQUEOS
-- =============================================================
-- Objetivo: mostrar espera/bloqueo al actualizar filas desde 2 sesiones.
-- Nota: Para forzar deadlock real, use registros que ambas sesiones toquen en orden inverso.

#DEAD LOCK

#SESION A PASO 1
START TRANSACTION;
UPDATE vehicle SET isActive = FALSE WHERE vehicle_id = 1;
-- dejar la sesion abierta

#READ COMMITTED
#paso 1 sesion A
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

SELECT * FROM vehicle WHERE vehicle_id = 262141;

#paso 2 sesion A
SELECT * FROM vehicle WHERE vehicle_id = 262141;
COMMIT;

#REPEATABLE READ
#Paso 1 sesion A
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
START TRANSACTION;

SELECT * FROM vehicle WHERE vehicle_id = 262141;

#Paso 3 sesion A
SELECT * FROM vehicle WHERE vehicle_id = 262141;
COMMIT;

USE vehiculos_db;

#DEADLOCK
#SESION A PASO 2
UPDATE vehicle SET isActive = FALSE WHERE vehicle_id = 2;
-- Deadlock debería ocurrir aquí

#READ COMMITTED

#paso 2 sesion B
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

UPDATE vehicle SET domain = 'D43356' WHERE vehicle_id = 262141;
COMMIT;

#REPEATABLE READ

#Paso 2 SESION B
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
START TRANSACTION;

UPDATE vehicle SET domain = 'Y43FS6' WHERE vehicle_id = 262141;
COMMIT;
-- ========================================================
-- D) BENCHMARK CONCURRENCIA: mysqlslap (opcional)
-- =============================================================
-- Ejecutar desde la terminal del SO (no dentro del cliente mysql).
-- Ajustar usuario/clave/host según tu entorno:

-- linux/macOS:
-- mysqlslap \
--   --user=root \
--   --password=root \
--   --host=localhost \
--   --concurrency=20 \
--   --iterations=400 \
--   --query="SELECT * FROM vehicle WHERE vehicle_id = 262141;" \
--   --verbose \
--   --create-schema=vehiculos_db

-- Windows (PowerShell):
-- & "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqlslap.exe" `
--   --user=root --password=root --host=localhost `
--   --concurrency=20 --iterations=400 `
--   --query="SELECT * FROM vehicle WHERE vehicle_id = 262141;" `
--   --verbose --create-schema=vehiculos_db

/* =============================================================
   9) MEDICIÓN DE TIEMPOS CON ÍNDICES
   ============================================================= */
SET profiling = 1;
DROP INDEX IF EXISTS idx_brand ON vehicle;
SELECT * FROM vehicle WHERE brand = 'Toyota';
SHOW PROFILES;
CREATE INDEX idx_brand ON vehicle(brand);
SELECT * FROM vehicle WHERE brand = 'Toyota';
SHOW PROFILES;

/* =============================================================
   10) CONSULTAS DE VERIFICACIÓN FINAL
   ============================================================= */
SHOW TABLES;
DESCRIBE insurance_vehicle;
DESCRIBE vehicle;
SHOW TRIGGERS FROM vehiculos_db;
SELECT * FROM insurance_vehicle;
SELECT * FROM vehicle;

-- FIN DEL SCRIPT ÚNICO