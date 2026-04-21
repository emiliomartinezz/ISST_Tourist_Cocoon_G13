# 🏨 Tourist Cocoon

Aplicación web para la gestión de un hostal de cápsulas, desarrollada como proyecto de la asignatura **ISST** en la UPM.

Tourist Cocoon permite gestionar el ciclo completo de una estancia:

- Registro e inicio de sesión de huéspedes  
- Consulta y edición del perfil  
- Visualización de cápsulas disponibles  
- Creación y cancelación de reservas  
- Check-in y check-out  
- Solicitud de acceso al edificio y a la cápsula  
- Panel de administración para reservas, usuarios, cápsulas, limpieza y registros de acceso  

---

## 📑 Índice

- [Descripción del proyecto](#-descripción-del-proyecto)
- [Arquitectura](#-arquitectura)
- [Tecnologías utilizadas](#-tecnologías-utilizadas)
- [Funcionalidades principales](#-funcionalidades-principales)
- [Estructura del repositorio](#-estructura-del-repositorio)
- [Requisitos previos](#-requisitos-previos)
- [Configuración del proyecto](#-configuración-del-proyecto)
- [Ejecución en local](#-ejecución-en-local)
- [Endpoints principales](#-endpoints-principales)
- [Panel de administración](#-panel-de-administración)
- [Estado actual del proyecto](#-estado-actual-del-proyecto)
- [Líneas de mejora](#-líneas-de-mejora)
- [Autores](#-autores)

---

## 📌 Descripción del proyecto

**Tourist Cocoon** es una solución orientada a la digitalización de la operativa de un hostal de cápsulas.

El objetivo del sistema es centralizar en una sola aplicación tanto la experiencia del huésped como la gestión operativa del establecimiento.

El sistema permite:

- Reservar una cápsula en un rango de fechas  
- Realizar el check-in validando identidad  
- Habilitar el acceso mediante credenciales temporales  
- Registrar intentos de acceso para trazabilidad  
- Gestionar el estado de las cápsulas  
- Generar y completar órdenes de limpieza  
- Ofrecer una vista administrativa completa  

---

## 🏗️ Arquitectura

El proyecto está dividido en dos capas principales:

### 🔙 Backend

API REST desarrollada con **Spring Boot**, responsable de:

- Lógica de negocio  
- Persistencia de datos  
- Validaciones  
- Gestión de reservas  
- Check-in / check-out  
- Control de accesos  
- Administración  
- Integración con Google Calendar  

### 🎨 Frontend

Aplicación cliente desarrollada con **React + Vite**, responsable de:

- Autenticación de usuarios  
- Formularios de reserva  
- Vistas de huésped  
- Check-in y acceso  
- Panel de administración  

---

## ⚙️ Tecnologías utilizadas

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

## 🚀 Funcionalidades principales

### 1. Autenticación y perfil

- Registro de usuario  
- Inicio de sesión  
- Consulta de perfil  
- Actualización de perfil  

### 2. Gestión de cápsulas

- Listado de cápsulas  
- Consulta de disponibilidad por fechas  
- Actualización del estado  

### 3. Reservas

- Creación de reservas  
- Consulta de reservas por huésped  
- Consulta de reserva activa  
- Cancelación de reservas  
- Check-out  

### 4. Check-in

- Validación de identidad (DNI/NIE)  
- Asociación a reserva activa  
- Generación de código de acceso  
- Activación hasta fecha de salida  

### 5. Control de accesos

- Acceso al edificio  
- Acceso a cápsula asignada  
- Validación de estancia activa  
- Validación de check-in  
- Validación de credenciales  
- Registro de accesos (trazabilidad)  

### 6. Administración

- Visualización de reservas  
- Gestión de usuarios  
- Gestión de cápsulas  
- Órdenes de limpieza  
- Registros de acceso filtrables  
- Exportación a CSV  
- Marcado de limpieza completada  

### 7. Integración con Google Calendar

- Creación de eventos asociados a reservas  
- Integración con calendario operativo (en desarrollo)  

---

## 📁 Estructura del repositorio

ISST_Tourist_Cocoon_G13/
│
├── ISST_Tourist_Cocoon_G13/
│ ├── BACKEND/
│ │ ├── pom.xml
│ │ └── src/main/java/...
│ │
│ └── FRONTEND/
│ └── frontend_tourist_cocoon/
│ ├── package.json
│ └── src/
│
└── .vscode/
