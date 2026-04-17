# Tourist Cocoon

Aplicación web para la gestión de un **hostal de cápsulas**, desarrollada como proyecto de la asignatura **ISST** en la **UPM**.

Tourist Cocoon permite gestionar el ciclo completo de una estancia:
- registro e inicio de sesión de huéspedes,
- consulta y edición del perfil,
- visualización de cápsulas disponibles,
- creación y cancelación de reservas,
- check-in y check-out,
- solicitud de acceso al edificio y a la cápsula,
- panel de administración para reservas, usuarios, cápsulas, limpieza y registros de acceso.

---

## Índice

- [Descripción del proyecto](#descripción-del-proyecto)
- [Arquitectura](#arquitectura)
- [Tecnologías utilizadas](#tecnologías-utilizadas)
- [Funcionalidades principales](#funcionalidades-principales)
- [Estructura del repositorio](#estructura-del-repositorio)
- [Requisitos previos](#requisitos-previos)
- [Configuración del proyecto](#configuración-del-proyecto)
- [Ejecución en local](#ejecución-en-local)
- [Endpoints principales](#endpoints-principales)
- [Panel de administración](#panel-de-administración)
- [Estado actual del proyecto](#estado-actual-del-proyecto)
- [Líneas de mejora](#líneas-de-mejora)
- [Autores](#autores)

---

## Descripción del proyecto

**Tourist Cocoon** es una solución orientada a la digitalización de la operativa de un hostal de cápsulas.  
El objetivo del sistema es centralizar en una sola aplicación tanto la experiencia del huésped como la gestión operativa del establecimiento.

El sistema permite:
- reservar una cápsula en un rango de fechas,
- realizar el check-in validando identidad,
- habilitar el acceso a las instalaciones mediante credenciales temporales,
- registrar intentos de acceso para trazabilidad,
- gestionar el estado de las cápsulas,
- generar y completar órdenes de limpieza,
- ofrecer una vista administrativa de la operativa del hostal.

---

## Arquitectura

El proyecto está dividido en dos capas principales:

### Backend
API REST desarrollada con **Spring Boot**, responsable de:
- lógica de negocio,
- persistencia de datos,
- validaciones,
- gestión de reservas,
- check-in / check-out,
- control de accesos,
- administración,
- integración con Google Calendar.

### Frontend
Aplicación cliente desarrollada con **React + Vite**, responsable de:
- autenticación de usuarios,
- formularios de reserva,
- vistas de huésped,
- acceso a funcionalidades de check-in y acceso,
- panel de administración.

---

## Tecnologías utilizadas

### Backend
- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- Spring Validation
- PostgreSQL
- Google Calendar API
- Maven

### Frontend
- React
- React Router
- Vite
- ESLint
- JavaScript
- CSS

---

## Funcionalidades principales

### 1. Autenticación y perfil
- Registro de usuario
- Inicio de sesión
- Consulta de perfil
- Actualización de perfil

### 2. Gestión de cápsulas
- Listado de cápsulas
- Consulta de disponibilidad por fechas
- Actualización del estado de cápsulas

### 3. Reservas
- Creación de reservas
- Consulta de reservas por huésped
- Consulta de reserva activa
- Cancelación de reservas
- Check-out de reservas

### 4. Check-in
- Validación de identidad mediante DNI/NIE
- Asociación del check-in a la reserva activa
- Generación de código de acceso
- Activación de acceso hasta la fecha límite de salida

### 5. Control de accesos
- Solicitud de acceso al edificio
- Solicitud de acceso a la cápsula asignada
- Validación de estancia activa
- Validación de check-in realizado
- Validación de vigencia de credenciales
- Registro de trazabilidad de accesos

### 6. Administración
- Visualización de todas las reservas
- Visualización de usuarios
- Visualización de cápsulas
- Visualización de órdenes de limpieza
- Consulta filtrada de registros de acceso
- Exportación de registros de acceso a CSV
- Marcado de órdenes de limpieza como completadas

### 7. Integración con Google Calendar
- Creación de eventos asociados a reservas
- Integración prevista con el calendario operativo del hostal

---

## Estructura del repositorio

```text
ISST_Tourist_Cocoon_G13/
│
├── ISST_Tourist_Cocoon_G13/
│   ├── BACKEND/
│   │   ├── pom.xml
│   │   └── src/main/java/...
│   │
│   └── FRONTEND/
│       └── frontend_tourist_cocoon/
│           ├── package.json
│           └── src/
│
└── .vscode/