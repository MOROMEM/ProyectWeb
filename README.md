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

from graphviz import Digraph

# Crear un diagrama de flujo

diagram = Digraph(comment='Sistema de Becas', format='png')

# Estilo general

diagram.attr(rankdir='TB', size='10')

# Nodo inicial

diagram.node('start', 'Inicio', shape='ellipse')

# Proceso de Login / Registro

diagram.node('login', 'Login / Registro', shape='box')
diagram.node('user', 'USER\n(Rol: Usuario o Admin)', shape='diamond')

# Acciones del Usuario

diagram.node('user_view_requests', 'Ver todas las solicitudes\n y solicitar becas', shape='box')
diagram.node('user_view_my_requests', 'Ver solicitudes asociadas', shape='box')

# Acciones del Admin

diagram.node('admin_create_request', 'Crear nuevas solicitudes\n(Agregar descripción)', shape='box')
diagram.node('admin_update_status', 'Actualizar estado de becas', shape='box')
diagram.node('admin_delete_request', 'Eliminar solicitudes', shape='box')

# Fin

diagram.node('end', 'Fin', shape='ellipse')

# Conexiones

diagram.edge('start', 'login')
diagram.edge('login', 'user')

# Usuario conectado

diagram.edge('user', 'user_view_requests', label='Usuario')
diagram.edge('user_view_requests', 'user_view_my_requests')
diagram.edge('user', 'admin_create_request', label='Admin')
diagram.edge('admin_create_request', 'admin_update_status')
diagram.edge('admin_update_status', 'admin_delete_request')
diagram.edge('user_view_my_requests', 'end')
diagram.edge('admin_delete_request', 'end')

# Renderizar el diagrama

diagram_path = '/mnt/data/sistema_becas_diagrama'
diagram.render(diagram_path, cleanup=True)

diagram_path + '.png'

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
git chttps://github.com/MOROMEM/ProyectWeb.git
cd sistemabecas
```
