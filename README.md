# AURA Essentials - Sistema de Gestión de Roles y E-commerce

Bienvenido a **AURA Essentials**, una plataforma completa para la gestión de productos, ventas, carrito de compras y usuarios con distintos niveles de acceso (Administradores y Usuarios regulares).

## 🚀 Despliegue en Vivo
Puedes probar la aplicación completamente funcional y en producción aquí:
👉 **[AURA Essentials en Vercel](https://aura-essentials-gray.vercel.app/)**

---

## 🛠️ Tecnologías Utilizadas

- **Frontend:** HTML5, CSS3 Vainilla, JavaScript (ES6+), Fetch API.
- **Backend:** Java 17, Spring Boot 3.x, Spring Security (JWT), Spring Data JPA.
- **Base de Datos:** PostgreSQL (Neon Cloud para producción, PostgreSQL local para desarrollo).
- **Almacenamiento de Imágenes:** Cloudinary.

---

## ✨ Funcionalidades Principales

### 🛒 Para Usuarios (Clientes)
- **Catálogo Dinámico:** Visualización de productos con soporte para variantes (ej. distintas tallas), productos destacados y filtrado por categorías.
- **Carrito de Compras:** Sincronización inteligente (LocalStorage para visitantes, Base de Datos para logueados). Cálculo en tiempo real de subtotales y cupones de descuento.
- **Proceso de Pago (Checkout simulado):** Flujo completo de compra para simular un pago correcto
- **Gestión de Perfil:** Posibilidad de modificar datos personales (nombre, teléfono, dirección).
- **Autenticación Segura:** Sistema de login y registro protegido con JSON Web Tokens (JWT).

### ⚙️ Para Administradores (Panel de Control)
- **Dashboard Estadístico:** Monitoreo en tiempo real de ventas totales, número de pedidos y cantidad de usuarios.
- **Gestión de Productos e Inventario:** Crear, editar y desactivar productos. Integración con Cloudinary para subida automática de imágenes y configuración de tallas/variantes.
- **Control de Pedidos:** Listado de todas las transacciones con detalles de los compradores y opción para actualizar el estado del pedido (Pendiente, Pagado, Cancelado).
- **Administración de Usuarios:** Visualizar todos los registros de la plataforma y cambiar roles de seguridad (ascender a ADMIN o degradar a USER).

---

## 📁 Estructura del Proyecto

El proyecto se divide en dos componentes principales:

1. **`IU/` (Frontend):** Contiene toda la interfaz de usuario. No utiliza frameworks pesados, garantizando un rendimiento ultra rápido y una experiencia limpia.
2. **`pf_sistema_gestion_roles/` (Backend):** La API RESTful construida con Spring Boot que se encarga de la lógica de negocio, seguridad y persistencia.
3. **`database.sql`:** Script SQL completo con el esquema de la base de datos y datos iniciales para arrancar el proyecto fácilmente.

---

## ⚙️ Cómo ejecutar el proyecto en tu entorno local (Localhost)

Sigue estos pasos para desplegar el proyecto en tu propia máquina:

### 1. Base de Datos
1. Instala PostgreSQL en tu máquina (si no lo tienes).
2. Abre tu terminal y ejecuta el siguiente comando para crear la base de datos `sistema_gr` vacía:
   ```bash
   psql -U postgres -p 5432 -c "CREATE DATABASE sistema_gr;"
   ```
3. Ejecuta el script `database.sql` incluido en la raíz de este repositorio para generar todas las tablas (`users`, `products`, `sales`, etc.) y configurar la estructura necesaria. Asegúrate de estar posicionado en la carpeta raíz del proyecto y ejecuta:
   ```bash
   psql -U postgres -p 5432 -d sistema_gr -f "database.sql"
   ```
   *(Nota: Si ejecutas el comando desde otra ubicación, deberás reemplazar `"database.sql"` por la ruta absoluta hacia el archivo).*

### 2. Backend (Spring Boot)
1. Abre la carpeta `pf_sistema_gestion_roles` en tu IDE favorito (como IntelliJ IDEA, VSCode o Eclipse).
2. Asegúrate de tener **Java 17** y **Maven** instalados.
3. Abre el archivo de propiedades local: `pf_sistema_gestion_roles/src/main/resources/application-local.properties`.
4. Configura tus credenciales de base de datos locales:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/TU_BASE_DE_DATOS
   spring.datasource.username=TU_USUARIO
   spring.datasource.password=TU_CONTRASEÑA
   ```
5. Ejecuta la aplicación. Puedes hacerlo desde tu IDE ejecutando la clase `PfSgrApplication.java` o mediante la terminal con:
   ```bash
   mvn spring-boot:run
   ```
   *El backend se levantará en `http://localhost:8080/api`.*

### 3. Frontend (Interfaz de Usuario)
1. Abre la carpeta `IU` en Visual Studio Code.
2. Es sumamente recomendable usar la extensión **Live Server** de VSCode para evitar bloqueos por políticas de CORS al abrir archivos directamente desde el explorador.
3. Haz clic derecho en el archivo `index.html` y selecciona **"Open with Live Server"**.
4. ¡Listo! El frontend inteligente (`config.js`) detectará automáticamente que estás en modo local y se conectará al backend que acabas de encender en el puerto 8080.

---

## 👥 Credenciales de Prueba

Si necesitas acceder rápidamente para probar funciones, puedes registrar un usuario nuevo en la plataforma, o puedes utilizar usuarios existentes si el script SQL ya los incluye. Para acceder al **Panel de Administración**, tu usuario debe tener el rol `ADMIN` en la base de datos.
