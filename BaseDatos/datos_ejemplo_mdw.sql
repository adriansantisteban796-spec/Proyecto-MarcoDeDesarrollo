-- ==========================================================================
-- Datos de ejemplo adicionales - gestion_laboratorios
-- NO toca la tabla `usuarios`. Usa los correos que ya existen:
--   Nico1306@innovaschools.edu.pe (Docente) y coordinador@hotmail.com (Coordinador)
--
-- ANTES de correrlo: levanta el backend al menos una vez (Hibernate crea
-- prestamo, incidencia y mantenimiento y agrega id_equipo/solicitante a
-- reserva_laboratorio). Correrlo UNA sola vez (si no, duplica registros).
-- Fechas pensadas alrededor del 27/10/2026 (hay prestamos vencidos y
-- mantenimientos en curso para probar los estados).
-- ==========================================================================
SET NAMES utf8mb4;
USE gestion_laboratorios;

-- ---- Laboratorios y categorias nuevos (codigo/nombre son UNIQUE) ----
INSERT IGNORE INTO laboratorio (codigo, nombre, ubicacion) VALUES
  ('Lab C-305','Redes y Telecomunicaciones','Piso 3'),
  ('Lab D-102','IoT y Electronica','Piso 1');

INSERT IGNORE INTO categoria (nombre) VALUES
  ('Redes'),
  ('Impresion 3D'),
  ('Perifericos');

-- ---- Variables para resolver ids sin depender del AUTO_INCREMENT ----
SET @lab_101 = (SELECT id_laboratorio FROM laboratorio WHERE codigo = 'Lab A-101');
SET @lab_204 = (SELECT id_laboratorio FROM laboratorio WHERE codigo = 'Lab B-204');
SET @lab_305 = (SELECT id_laboratorio FROM laboratorio WHERE codigo = 'Lab C-305');
SET @lab_102 = (SELECT id_laboratorio FROM laboratorio WHERE codigo = 'Lab D-102');

SET @cat_co = (SELECT id_categoria FROM categoria WHERE nombre = 'Laptops / Computo');
SET @cat_ro = (SELECT id_categoria FROM categoria WHERE nombre = 'Robotica');
SET @cat_ca = (SELECT id_categoria FROM categoria WHERE nombre = 'Camaras');
SET @cat_se = (SELECT id_categoria FROM categoria WHERE nombre = 'Sensores IoT');
SET @cat_re = (SELECT id_categoria FROM categoria WHERE nombre = 'Redes');
SET @cat_im = (SELECT id_categoria FROM categoria WHERE nombre = 'Impresion 3D');
SET @cat_pe = (SELECT id_categoria FROM categoria WHERE nombre = 'Perifericos');

-- ---- Equipos (INV-002 a INV-026). El estado ya es el que calcularia el backend ----
INSERT IGNORE INTO equipo (codigo_inventario, nombre, modelo, numero_serie, descripcion, id_categoria, id_laboratorio, id_estado, fecha_registro) VALUES
  ('INV-002','Laptop Dell','Latitude 3420','SN-DL3420-0021','Laptop para practicas de programacion',@cat_co,@lab_204,1,'2026-09-23 08:10:00'),
  ('INV-003','Laptop HP','ProBook 450 G8','SN-HP450-0334','Laptop para desarrollo y pruebas',@cat_co,@lab_204,2,'2026-09-23 08:15:00'),
  ('INV-004','Laptop Lenovo','IdeaPad 3 15','SN-LN3IP-0412','Laptop de uso general',@cat_co,@lab_204,4,'2026-09-23 08:20:00'),
  ('INV-005','Mini PC Intel','NUC 11 Pro','SN-NUC11-0078','Servidor de pruebas para practicas de redes',@cat_co,@lab_305,1,'2026-09-24 09:00:00'),
  ('INV-006','Tablet Samsung','Galaxy Tab S7','SN-SGT7-0150','Tablet para pruebas de apps moviles',@cat_co,@lab_101,1,'2026-09-24 09:10:00'),
  ('INV-007','Brazo robotico uArm','Swift Pro','SN-UARM-0007','Brazo robotico de escritorio de 4 ejes',@cat_ro,@lab_101,3,'2026-09-24 10:00:00'),
  ('INV-008','Kit Arduino','Mega 2560 Starter','SN-ARD25-0112','Kit con placa, sensores y protoboard',@cat_ro,@lab_101,2,'2026-09-24 10:30:00'),
  ('INV-009','Robot educativo LEGO','Mindstorms EV3','SN-EV3-0045','Kit de robotica educativa con motores y sensores',@cat_ro,@lab_101,1,'2026-09-25 08:00:00'),
  ('INV-010','Raspberry Pi','4 Model B 8GB','SN-RPI4-0230','Microcomputador para proyectos embebidos',@cat_ro,@lab_101,1,'2026-09-25 08:30:00'),
  ('INV-011','Drone DJI','Tello EDU','SN-TELLO-0019','Drone programable para practicas de control',@cat_ro,@lab_101,2,'2026-09-25 09:00:00'),
  ('INV-012','Camara Logitech','C920 HD Pro','SN-LC920-0560','Webcam Full HD para videoconferencia',@cat_ca,@lab_204,2,'2026-09-26 08:00:00'),
  ('INV-013','Camara Canon','EOS M50','SN-CNM50-0098','Camara mirrorless para proyectos de vision',@cat_ca,@lab_101,1,'2026-09-26 08:20:00'),
  ('INV-014','Camara IP Hikvision','DS-2CD2043G0','SN-HIK20-0341','Camara IP de vigilancia para practicas de redes',@cat_ca,@lab_305,1,'2026-09-26 09:00:00'),
  ('INV-015','Kit ESP32','DevKit V1','SN-ESP32-0870','Placa ESP32 con WiFi y Bluetooth',@cat_se,@lab_102,1,'2026-09-29 08:00:00'),
  ('INV-016','Sensor DHT22','AM2302','SN-DHT22-0615','Sensor de temperatura y humedad',@cat_se,@lab_102,1,'2026-09-29 08:10:00'),
  ('INV-017','Sensor de gas MQ-135','MQ-135','SN-MQ135-0222','Sensor de calidad de aire',@cat_se,@lab_102,1,'2026-09-29 08:20:00'),
  ('INV-018','Modulo LoRa','SX1278 433MHz','SN-LORA-0033','Modulo de comunicacion de largo alcance',@cat_se,@lab_102,4,'2026-09-29 08:30:00'),
  ('INV-019','Router Cisco','ISR 1100','SN-CSR11-0004','Router para laboratorios de enrutamiento',@cat_re,@lab_305,3,'2026-09-30 08:00:00'),
  ('INV-020','Switch Cisco','Catalyst 2960','SN-CS296-0016','Switch administrable de 24 puertos',@cat_re,@lab_305,1,'2026-09-30 08:10:00'),
  ('INV-021','Access Point Ubiquiti','UniFi U6 Lite','SN-UBU6L-0058','Punto de acceso WiFi 6',@cat_re,@lab_305,1,'2026-09-30 08:20:00'),
  ('INV-022','Tester de cables Fluke','MicroScanner2','SN-FLK-MS2-0091','Verificador de cableado estructurado',@cat_re,@lab_305,1,'2026-09-30 08:30:00'),
  ('INV-023','Impresora 3D Creality','Ender 3 V2','SN-END3-0102','Impresora 3D FDM para prototipos',@cat_im,@lab_101,3,'2026-10-01 08:00:00'),
  ('INV-024','Impresora 3D Prusa','MK4','SN-PRMK4-0011','Impresora 3D FDM de alta precision',@cat_im,@lab_101,1,'2026-10-01 08:30:00'),
  ('INV-025','Proyector Epson','PowerLite X49','SN-EPX49-0273','Proyector para clases y exposiciones',@cat_pe,@lab_204,2,'2026-10-01 09:00:00'),
  ('INV-026','Monitor LG','24MK430H','SN-LG24-0480','Monitor adicional de 24 pulgadas',@cat_pe,@lab_204,1,'2026-10-01 09:10:00');

SET @e1 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-001');
SET @e2 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-002');
SET @e3 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-003');
SET @e4 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-004');
SET @e5 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-005');
SET @e6 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-006');
SET @e7 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-007');
SET @e8 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-008');
SET @e9 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-009');
SET @e10 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-010');
SET @e11 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-011');
SET @e12 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-012');
SET @e13 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-013');
SET @e14 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-014');
SET @e15 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-015');
SET @e16 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-016');
SET @e17 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-017');
SET @e18 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-018');
SET @e19 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-019');
SET @e20 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-020');
SET @e21 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-021');
SET @e22 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-022');
SET @e23 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-023');
SET @e24 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-024');
SET @e25 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-025');
SET @e26 = (SELECT id_equipo FROM equipo WHERE codigo_inventario = 'INV-026');

-- ---- Prestamos (ACTIVO / DEVUELTO / CANCELADO; INV-008 e INV-011 estan atrasados) ----
INSERT INTO prestamo (id_equipo, responsable, fecha_salida, fecha_limite, fecha_devolucion, estado) VALUES
  (@e3,'Nico1306@innovaschools.edu.pe','2026-10-24 09:00:00','2026-10-30 18:00:00',NULL,'ACTIVO'),
  (@e8,'Nico1306@innovaschools.edu.pe','2026-10-22 14:00:00','2026-10-25 18:00:00',NULL,'ACTIVO'),
  (@e11,'coordinador@hotmail.com','2026-10-15 10:00:00','2026-10-20 18:00:00',NULL,'ACTIVO'),
  (@e12,'coordinador@hotmail.com','2026-10-26 08:00:00','2026-11-02 17:00:00',NULL,'ACTIVO'),
  (@e25,'Nico1306@innovaschools.edu.pe','2026-10-27 07:30:00','2026-10-29 12:00:00',NULL,'ACTIVO'),
  (@e1,'Nico1306@innovaschools.edu.pe','2026-10-01 09:00:00','2026-10-03 18:00:00','2026-10-03 16:40:00','DEVUELTO'),
  (@e1,'coordinador@hotmail.com','2026-10-10 08:30:00','2026-10-12 18:00:00','2026-10-12 17:15:00','DEVUELTO'),
  (@e2,'Nico1306@innovaschools.edu.pe','2026-10-05 08:00:00','2026-10-08 18:00:00','2026-10-08 12:30:00','DEVUELTO'),
  (@e2,'coordinador@hotmail.com','2026-10-14 10:00:00','2026-10-16 18:00:00','2026-10-17 09:10:00','DEVUELTO'),
  (@e6,'Nico1306@innovaschools.edu.pe','2026-09-28 09:00:00','2026-09-30 18:00:00','2026-09-30 17:50:00','DEVUELTO'),
  (@e9,'coordinador@hotmail.com','2026-10-08 11:00:00','2026-10-09 18:00:00','2026-10-09 15:20:00','DEVUELTO'),
  (@e15,'Nico1306@innovaschools.edu.pe','2026-10-12 08:00:00','2026-10-14 18:00:00','2026-10-14 10:05:00','DEVUELTO'),
  (@e16,'Nico1306@innovaschools.edu.pe','2026-10-19 09:30:00','2026-10-21 18:00:00','2026-10-21 17:30:00','DEVUELTO'),
  (@e22,'coordinador@hotmail.com','2026-10-06 08:00:00','2026-10-06 18:00:00','2026-10-06 16:00:00','DEVUELTO'),
  (@e13,'Nico1306@innovaschools.edu.pe','2026-10-18 09:00:00','2026-10-20 18:00:00',NULL,'CANCELADO'),
  (@e4,'coordinador@hotmail.com','2026-10-21 08:00:00','2026-10-23 18:00:00',NULL,'CANCELADO');

-- ---- Mantenimientos (en curso, programados a futuro y finalizados) ----
INSERT INTO mantenimiento (id_equipo, motivo, fecha_inicio, fecha_fin, estado, tipo) VALUES
  (@e7,'Calibracion de servomotores del eje base','2026-10-25 08:00:00','2026-10-30 18:00:00','PROGRAMADO','CORRECTIVO'),
  (@e19,'Actualizacion de firmware y respaldo de configuracion','2026-10-27 08:00:00','2026-10-28 18:00:00','PROGRAMADO','PREVENTIVO'),
  (@e23,'Reemplazo de boquilla y nivelacion de cama','2026-10-26 09:00:00',NULL,'PROGRAMADO','CORRECTIVO'),
  (@e20,'Limpieza y revision de puertos','2026-11-05 08:00:00','2026-11-05 12:00:00','PROGRAMADO','PREVENTIVO'),
  (@e24,'Lubricacion de ejes y revision del extrusor','2026-11-10 08:00:00','2026-11-11 18:00:00','PROGRAMADO','PREVENTIVO'),
  (@e5,'Limpieza interna y actualizacion de BIOS','2026-11-15 09:00:00','2026-11-15 13:00:00','PROGRAMADO','PREVENTIVO'),
  (@e2,'Limpieza interna y cambio de bateria','2026-09-15 09:00:00','2026-09-16 13:00:00','FINALIZADO','PREVENTIVO'),
  (@e14,'Cambio de fuente de alimentacion','2026-09-20 09:00:00','2026-09-23 17:00:00','FINALIZADO','CORRECTIVO'),
  (@e1,'Limpieza interna y cambio de pasta termica','2026-09-25 10:00:00','2026-09-25 15:00:00','FINALIZADO','PREVENTIVO'),
  (@e21,'Actualizacion de firmware del controlador','2026-10-02 08:00:00','2026-10-02 10:00:00','FINALIZADO','PREVENTIVO'),
  (@e10,'Reemplazo de tarjeta microSD corrupta','2026-10-06 09:00:00','2026-10-07 12:00:00','FINALIZADO','CORRECTIVO'),
  (@e23,'Lubricacion de ejes y ajuste de correas','2026-09-10 08:00:00','2026-09-10 12:00:00','FINALIZADO','PREVENTIVO');

-- ---- Incidencias (2 criticas abiertas = equipos Bloqueados) ----
INSERT INTO incidencia (id_equipo, descripcion, reportado_por, fecha_reporte, estado, severidad) VALUES
  (@e18,'Modulo no enciende, posible cortocircuito en la linea de alimentacion','Nico1306@innovaschools.edu.pe','2026-10-26 11:20:00','ABIERTA','CRITICA'),
  (@e4,'Pantalla rota y bateria hinchada, riesgo de incendio','coordinador@hotmail.com','2026-10-27 09:15:00','ABIERTA','CRITICA'),
  (@e7,'El servo del eje base vibra y pierde precision','Nico1306@innovaschools.edu.pe','2026-10-24 15:40:00','ABIERTA','ALTA'),
  (@e20,'Puerto Gi0/12 con falla intermitente','coordinador@hotmail.com','2026-10-25 10:05:00','ABIERTA','ALTA'),
  (@e3,'Teclado con teclas que no responden (A, S, D)','Nico1306@innovaschools.edu.pe','2026-10-26 16:30:00','ABIERTA','MEDIA'),
  (@e23,'La primera capa no se adhiere a la cama','Nico1306@innovaschools.edu.pe','2026-10-25 12:00:00','ABIERTA','MEDIA'),
  (@e12,'Lente con pequenas manchas de polvo','coordinador@hotmail.com','2026-10-27 08:45:00','ABIERTA','BAJA'),
  (@e9,'Motor del puerto B no responde','Nico1306@innovaschools.edu.pe','2026-10-09 15:30:00','RESUELTO','ALTA'),
  (@e17,'Lecturas fuera de rango en ambiente cerrado','Nico1306@innovaschools.edu.pe','2026-10-13 11:00:00','RESUELTO','MEDIA'),
  (@e10,'La Raspberry no arranca, tarjeta microSD corrupta','coordinador@hotmail.com','2026-10-05 17:10:00','RESUELTO','CRITICA'),
  (@e14,'Camara sin energia y sin imagen','coordinador@hotmail.com','2026-09-19 14:20:00','CERRADA','CRITICA'),
  (@e2,'Cargador con cable danado','Nico1306@innovaschools.edu.pe','2026-10-08 12:40:00','CERRADA','BAJA');

-- ---- Reservas (laboratorio = id del lab como texto, igual que guarda el front) ----
INSERT INTO reserva_laboratorio (laboratorio, fecha, hora_inicio, hora_fin, estado, id_equipo, solicitante) VALUES
  (CAST(@lab_101 AS CHAR),'2026-10-28','08:00','10:00','PENDIENTE',@e9,'Nico1306@innovaschools.edu.pe'),
  (CAST(@lab_101 AS CHAR),'2026-10-28','10:00','12:00','APROBADA',@e13,'coordinador@hotmail.com'),
  (CAST(@lab_204 AS CHAR),'2026-10-28','14:00','16:00','APROBADA',@e2,'coordinador@hotmail.com'),
  (CAST(@lab_204 AS CHAR),'2026-10-29','08:00','10:00','PENDIENTE',@e1,'Nico1306@innovaschools.edu.pe'),
  (CAST(@lab_305 AS CHAR),'2026-10-29','10:00','12:00','APROBADA',@e20,'coordinador@hotmail.com'),
  (CAST(@lab_305 AS CHAR),'2026-10-30','14:00','16:00','PENDIENTE',@e21,'Nico1306@innovaschools.edu.pe'),
  (CAST(@lab_102 AS CHAR),'2026-10-30','08:00','10:00','APROBADA',@e15,'Nico1306@innovaschools.edu.pe'),
  (CAST(@lab_102 AS CHAR),'2026-10-31','16:00','18:00','PENDIENTE',@e16,'Nico1306@innovaschools.edu.pe'),
  (CAST(@lab_101 AS CHAR),'2026-11-03','14:00','16:00','CANCELADA',@e24,'Nico1306@innovaschools.edu.pe'),
  (CAST(@lab_101 AS CHAR),'2026-11-03','14:00','16:00','APROBADA',NULL,'coordinador@hotmail.com'),
  (CAST(@lab_204 AS CHAR),'2026-11-04','10:00','12:00','PENDIENTE',NULL,'Nico1306@innovaschools.edu.pe'),
  (CAST(@lab_204 AS CHAR),'2026-11-05','16:00','18:00','APROBADA',@e26,'coordinador@hotmail.com'),
  (CAST(@lab_204 AS CHAR),'2026-10-20','08:00','10:00','APROBADA',@e2,'Nico1306@innovaschools.edu.pe'),
  (CAST(@lab_101 AS CHAR),'2026-10-15','14:00','16:00','APROBADA',@e9,'coordinador@hotmail.com'),
  (CAST(@lab_102 AS CHAR),'2026-10-12','10:00','12:00','CANCELADA',NULL,'Nico1306@innovaschools.edu.pe'),
  (CAST(@lab_305 AS CHAR),'2026-10-22','16:00','18:00','APROBADA',@e22,'coordinador@hotmail.com'),
  (CAST(@lab_204 AS CHAR),'2026-10-23','10:00','12:00','APROBADA',NULL,'Nico1306@innovaschools.edu.pe');
