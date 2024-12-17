# Sistema de Becas

Este proyecto es una aplicación web de **Gestión de Solicitudes de Becas**, diseñada con un **frontend** en React y un **backend** en Java utilizando Spring Boot. Implementa una arquitectura cliente-servidor con API RESTful y utiliza MongoDB como base de datos.

---

## 📁 Estructura del Proyecto

### Backend - **Java Spring Boot**

Ubicado en el directorio principal del proyecto:

- **src/main/java/com.prograWeb.sistemabecas**
  - `config` - Configuraciones de CORS y otras configuraciones.
    - `CorsConfig`
  - `controller` - Controladores que gestionan las solicitudes HTTP.
    - `AuthController`
    - `SolicitudController`
  - `dto` - Objetos de Transferencia de Datos.
    - `SolicitudEnriquecida`
  - `model` - Modelos de datos para la aplicación.
    - `LoginRequest`, `Solicitud`, `Usuario`, `UsuarioSolicitud`
  - `repository` - Interfaces para el acceso a MongoDB.
    - `SolicitudRepository`, `UsuarioRepository`, `UsuarioSolicitudRepository`
  - `security` - Implementación de seguridad JWT.
    - `CustomUserDetailsService`, `JwtAuthenticationFilter`, `JWTUtil`, `SecurityConfig`
  - `service`
    - `ProyectsistemabecasApplication` - Clase principal de Spring Boot.
- **resources/application.properties** - Configuración de propiedades del servidor.
- **test/java** - Pruebas del backend.

---

### Frontend - **React con Vite**

Ubicado en la carpeta `frontend/`:

- **src**
  - `App.jsx` - Componente principal de la aplicación.
  - `main.jsx` - Punto de entrada del frontend.
  - `solicitudes.jsx` - Componente para gestionar solicitudes de becas.
  - `assets/` - Recursos estáticos.
  - **Estilos**:
    - `App.css`
    - `solicitudes.css`
  - **Configuración**:
    - `vite.config.js` - Configuración de Vite.
- **public/** - Archivos estáticos para la aplicación React.

---

## 🚀 Tecnologías Utilizadas

### Backend:

- **Java** y **Spring Boot** - Framework principal para construir el servidor REST.
- **Maven** - Gestión de dependencias.
- **MongoDB** - Base de datos NoSQL para almacenar solicitudes de becas y usuarios.
- **JWT** - Seguridad y autenticación con JSON Web Tokens.

### Frontend:

- **React** - Framework de JavaScript para el desarrollo de la interfaz.
- **Vite** - Herramienta de construcción rápida para React.
- **CSS** - Estilización de componentes.

---

## 🔧 Instalación

### Requisitos previos:

- **Node.js** (para el frontend)
- **Java 17+** y **Maven** (para el backend)
- **MongoDB** instalado y configurado.

### Clonar el repositorio

```bash
git clone https://github.com/tuusuario/sistemabecas.git
cd sistemabecas
```
