# Modelo de Datos - Tourist Cocoon

## 1. USUARIO
**Descripción:** Entidad que almacena la información de los usuarios del sistema (huéspedes, administradores).

| Atributo | Descripción | Tipo (SQL) | PK | Oblig. | Unicidad | Verificación |
|----------|-------------|-----------|-----|--------|----------|--------------|
| id | Identificador único del usuario | BIGINT | ✓ | ✓ | ✓ | AUTO_INCREMENT |
| nif | Número de Identificación Fiscal (cifrado) | VARCHAR(255) | | ✓ | ✓ | Patrón NIF válido |
| nombre | Nombre completo del usuario | VARCHAR(255) | | ✓ | | |
| email | Correo electrónico | VARCHAR(255) | | ✓ | ✓ | Validación de email |
| password | Contraseña cifrada | VARCHAR(255) | | ✓ | | Hash seguro |
| telefono | Número de teléfono (cifrado) | VARCHAR(255) | | | | Validación formato |
| rol | Rol del usuario (ADMIN, USER, STAFF) | VARCHAR(50) | | | | ENUM: ADMIN, USER, STAFF |

---

## 2. CAPSULA
**Descripción:** Entidad que representa las cápsulas disponibles en el hostal.

| Atributo | Descripción | Tipo (SQL) | PK | Oblig. | Unicidad | Verificación |
|----------|-------------|-----------|-----|--------|----------|--------------|
| id | Identificador de la cápsula (ej: C-101) | VARCHAR(50) | ✓ | ✓ | ✓ | |
| planta | Número de planta donde está ubicada | INTEGER | | ✓ | | Valores: 1-3 |
| estado | Estado actual de la cápsula | VARCHAR(50) | | ✓ | | ENUM: DISPONIBLE, OCUPADA, SUCIA, BLOQUEADA |
| categoria | Categoría de la cápsula | VARCHAR(50) | | | | Por defecto: STANDARD |
| hostalId | ID del hostal (para multi-tenancy) | BIGINT | | | | |

---

## 3. RESERVA
**Descripción:** Entidad que almacena las reservas de cápsulas realizadas por huéspedes.

| Atributo | Descripción | Tipo (SQL) | PK | Oblig. | Unicidad | Verificación |
|----------|-------------|-----------|-----|--------|----------|--------------|
| id | Identificador único de la reserva | BIGINT | ✓ | ✓ | ✓ | AUTO_INCREMENT |
| fechaInicio | Fecha de entrada | DATE | | ✓ | | Validación: >= fecha actual |
| fechaFinal | Fecha de salida | DATE | | ✓ | | Validación: > fechaInicio |
| fechaSalida | Fecha de salida real (check-out) | DATE | | | | |
| estado | Estado de la reserva | VARCHAR(50) | | ✓ | | ENUM: CONFIRMADA, CANCELADA, FINALIZADA |
| googleCalendarEventId | ID del evento en Google Calendar del hostal | VARCHAR(255) | | | | |
| googleCalendarEventIdCliente | ID del evento en Google Calendar del cliente | VARCHAR(255) | | | | |
| stripePaymentIntentId | ID de intención de pago en Stripe | VARCHAR(255) | | | | |
| checkInRealizado | Indica si se ha realizado el check-in | BOOLEAN | | ✓ | | Por defecto: false |
| fechaCheckIn | Fecha y hora del check-in | TIMESTAMP | | | | |
| documentoIdentidad | Documento de identidad del huésped | VARCHAR(255) | | | | |
| documentoValidado | Indica si el documento fue validado | BOOLEAN | | ✓ | | Por defecto: false |
| datosAutoridadEnviados | Indica si se enviaron datos a autoridades | BOOLEAN | | ✓ | | Por defecto: false |
| codigoAcceso | Código para acceso a la cápsula | VARCHAR(50) | | | | Generado dinámicamente |
| accesoValidoHasta | Fecha/hora hasta cuando es válido el código | TIMESTAMP | | | | |
| huesped_id | FK a Usuario (huésped) | BIGINT | | ✓ | | Relación ManyToOne |
| capsula_id | FK a Capsula | VARCHAR(50) | | ✓ | | Relación ManyToOne |

---

## 4. INCIDENCIA
**Descripción:** Entidad para reportar y gestionar incidencias ocurridas durante las estancias.

| Atributo | Descripción | Tipo (SQL) | PK | Oblig. | Unicidad | Verificación |
|----------|-------------|-----------|-----|--------|----------|--------------|
| id | Identificador único de la incidencia | BIGINT | ✓ | ✓ | ✓ | AUTO_INCREMENT |
| fechaCreacion | Fecha/hora de creación de la incidencia | TIMESTAMP | | ✓ | | Auto asignado |
| fechaResolucion | Fecha/hora de resolución | TIMESTAMP | | | | |
| categoria | Categoría de la incidencia | VARCHAR(50) | | ✓ | | ENUM: LIMPIEZA, MANTENIMIENTO, SEGURIDAD, ACCESO |
| prioridad | Nivel de prioridad | VARCHAR(50) | | ✓ | | ENUM: NORMAL, ALTA, URGENTE (defecto: NORMAL) |
| estado | Estado actual de la incidencia | VARCHAR(50) | | ✓ | | ENUM: ABIERTA, EN_PROCESO, RESUELTA (defecto: ABIERTA) |
| descripcion | Descripción detallada de la incidencia | VARCHAR(2000) | | ✓ | | Máx 2000 caracteres |
| fotoPath | Ruta a foto de la incidencia | VARCHAR(255) | | | | |
| notificacionEnviada | Indica si se envió notificación | BOOLEAN | | ✓ | | Por defecto: false |
| canalNotificacion | Canal por donde se envió (EMAIL, SMS, APP) | VARCHAR(50) | | | | |
| telefonoEmergenciaMostrado | Indica si se mostró teléfono emergencia | BOOLEAN | | ✓ | | Por defecto: false |
| comentarioResolucion | Comentario al resolver la incidencia | VARCHAR(2000) | | | | |
| huesped_id | FK a Usuario (quién reporta) | BIGINT | | ✓ | | Relación ManyToOne |
| reserva_id | FK a Reserva (incidencia asociada) | BIGINT | | | | Relación ManyToOne |
| capsula_id | FK a Capsula (cápsula afectada) | VARCHAR(50) | | | | Relación ManyToOne |
| resuelta_por | FK a Usuario (quién resuelve) | BIGINT | | | | Relación ManyToOne |

---

## 5. ORDEN_LIMPIEZA
**Descripción:** Entidad para gestionar órdenes de limpieza de cápsulas.

| Atributo | Descripción | Tipo (SQL) | PK | Oblig. | Unicidad | Verificación |
|----------|-------------|-----------|-----|--------|----------|--------------|
| id | Identificador único de la orden | BIGINT | ✓ | ✓ | ✓ | AUTO_INCREMENT |
| fechaCreacion | Fecha/hora de creación de la orden | TIMESTAMP | | ✓ | | Auto asignado |
| estado | Estado de la orden | VARCHAR(50) | | ✓ | | ENUM: PENDIENTE, COMPLETADA |
| mensaje | Descripción/instrucciones de limpieza | VARCHAR(255) | | ✓ | | |
| capsula_id | FK a Capsula (cápsula a limpiar) | VARCHAR(50) | | ✓ | | Relación ManyToOne |
| reserva_id | FK a Reserva (asociada a) | BIGINT | | ✓ | | Relación ManyToOne |

---

## 6. REGISTRO_ACCESO
**Descripción:** Entidad para auditar y registrar todos los intentos de acceso al hostal y cápsulas.

| Atributo | Descripción | Tipo (SQL) | PK | Oblig. | Unicidad | Verificación |
|----------|-------------|-----------|-----|--------|----------|--------------|
| id | Identificador único del registro | BIGINT | ✓ | ✓ | ✓ | AUTO_INCREMENT |
| fechaHora | Fecha y hora del intento de acceso | TIMESTAMP | | ✓ | | |
| puerta | Tipo de puerta (EDIFICIO, CAPSULA) | VARCHAR(50) | | ✓ | | ENUM: EDIFICIO, CAPSULA |
| resultado | Resultado del acceso (EXITO, DENEGADO) | VARCHAR(50) | | ✓ | | ENUM: EXITO, DENEGADO |
| credencial | Tipo de credencial usada | VARCHAR(50) | | ✓ | | ENUM: APP, QR, PIN |
| objetivo | Puerta principal o ID de cápsula | VARCHAR(50) | | | | |
| motivo | Motivo o descripción adicional | VARCHAR(255) | | | | |
| huesped_id | FK a Usuario (quién accede) | BIGINT | | ✓ | | Relación ManyToOne |
| reserva_id | FK a Reserva (asociada a) | BIGINT | | | | Relación ManyToOne |

---

## 7. GOOGLE_OAUTH_TOKEN
**Descripción:** Entidad para almacenar tokens OAuth de Google Calendar integrados por usuarios.

| Atributo | Descripción | Tipo (SQL) | PK | Oblig. | Unicidad | Verificación |
|----------|-------------|-----------|-----|--------|----------|--------------|
| id | Identificador único del token | BIGINT | ✓ | ✓ | ✓ | AUTO_INCREMENT |
| user_id | FK a Usuario | BIGINT | | ✓ | ✓ | Relación OneToOne |
| refreshToken | Token de refresco de Google (4096 chars) | VARCHAR(4096) | | ✓ | | Información sensible |
| calendarId | ID del calendario en Google | VARCHAR(255) | | ✓ | | Por defecto: "primary" |
| connectedAt | Fecha/hora de conexión | TIMESTAMP | | ✓ | | Auto asignado |
| revokedAt | Fecha/hora de revocación | TIMESTAMP | | | | |

---

## Relaciones Principales

- **Usuario → Reserva**: 1:N (Un usuario puede tener múltiples reservas)
- **Usuario → Incidencia**: 1:N (Un usuario reporta múltiples incidencias)
- **Usuario → RegistroAcceso**: 1:N (Un usuario tiene múltiples registros de acceso)
- **Usuario ↔ GoogleOAuthToken**: 1:1 (Relación uno a uno)
- **Reserva → Capsula**: N:1 (Múltiples reservas para cápsulas)
- **Reserva → Incidencia**: 1:N (Una reserva puede tener múltiples incidencias)
- **Reserva → OrdenLimpieza**: 1:N (Una reserva genera órdenes de limpieza)
- **Reserva → RegistroAcceso**: 1:N (Una reserva genera múltiples accesos)
- **Capsula → OrdenLimpieza**: 1:N (Una cápsula recibe múltiples órdenes de limpieza)
- **Capsula → Incidencia**: 1:N (Una cápsula puede tener múltiples incidencias)
- **Incidencia → Usuario (resuelta_por)**: N:1 (Múltiples incidencias resueltas por un staff)

---

## Notas Importantes

- **Cifrado**: Los campos `nif` y `telefono` en Usuario se cifran con AES-128 usando EncryptionConverter
- **Estados Enum**: Algunas entidades usan enums para estados con valores predefinidos
- **Auditoría**: RegistroAcceso registra todos los intentos de acceso para auditoría y seguridad
- **Base de datos**: PostgreSQL (Neon serverless)
- **ORM**: Hibernate/JPA con Spring Data
