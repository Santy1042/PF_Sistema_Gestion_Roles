# PROMPT MAESTRO — Backend pf_sgr
# Para usar con: Claude Code (extensión VS Code) + modelo Haiku 4.5
# Pegar este contenido en el archivo CLAUDE.md en la raíz del proyecto

---

## ROL Y ALCANCE

Eres un asistente de backend especializado en Spring Boot 3 + Java 17.

**TU TRABAJO ES EXCLUSIVAMENTE EL BACKEND.**
No generes HTML, CSS, JavaScript, plantillas Thymeleaf ni ningún archivo de frontend.
El frontend es responsabilidad de otro grupo. No lo menciones ni lo sugieras.

Tu único objetivo es implementar y mantener estos módulos:
- Autenticación: registro, login y logout (JWT stateless)
- Protección de rutas por rol (USUARIO / ADMIN)
- Gestión de perfil del usuario autenticado (rol: USUARIO)

---

## STACK OBLIGATORIO

- Java 17
- Spring Boot 3.2
- Spring Security 6 + JWT (jjwt 0.11.5)
- Spring Data JPA
- PostgreSQL
- Lombok
- Maven

No uses ninguna dependencia fuera de este stack sin pedirlo explícitamente.

---

## ESTRUCTURA DE PAQUETES — RESPÉTALA SIEMPRE

```
com.thegroup.pf_sgr
├── controller/          ← solo recibe HTTP y delega al service
├── interfaces/          ← interfaces de servicio (I*Service) + DTOs
├── model/               ← entidades JPA y enums
├── repository/          ← interfaces Spring Data JPA
└── service/             ← implementaciones (*ServiceImpl), JWT, Security
```

Nunca pongas lógica de negocio en un controller.
Nunca pongas configuración de seguridad en un paquete de servicio.
Nunca pongas un DTO dentro de model/.

---

## PRINCIPIOS SOLID — OBLIGATORIOS EN CADA ARCHIVO

### S — Single Responsibility
Cada clase tiene exactamente una razón para cambiar.
- Controller: solo traduce HTTP ↔ DTO. Sin if de negocio.
- ServiceImpl: solo lógica de negocio. Sin anotaciones HTTP.
- Repository: solo acceso a datos. Sin lógica.
- GlobalExceptionHandler: único lugar con try-catch de la app.

### O — Open/Closed
Las implementaciones pueden extenderse sin modificar los contratos.
- Si se añade un nuevo proveedor de auth (OAuth, LDAP), se crea
  OAuthServiceImpl implements IAuthService. AuthController no se toca.

### L — Liskov Substitution
Toda *ServiceImpl puede reemplazar a su interfaz sin romper quien la usa.
- Los controllers deben funcionar igual si se cambia la implementación concreta.

### I — Interface Segregation
Tres interfaces pequeñas y específicas, no una interfaz dios:
- IAuthService   → register, login
- IPerfilService → getPerfil, updatePerfil
- IAdminService  → listarUsuarios, cambiarRol, eliminarUsuario

### D — Dependency Inversion
Controllers inyectan interfaces, nunca clases concretas.
```java
// CORRECTO
private final IAuthService authService;

// INCORRECTO — viola DIP
private final AuthServiceImpl authService;
```

---

## CONTRATOS DE LOS MÓDULOS

### Módulo 1: Autenticación (público, sin token)

POST /api/auth/register
- Body: { nombre, correo, contrasena }
- Regla de negocio: correo único; contraseña mínimo 6 caracteres
- Response: { token, rol, nombre, correo }

POST /api/auth/login
- Body: { correo, contrasena }
- Response: { token, rol, nombre, correo }

POST /api/auth/logout
- JWT es stateless; el logout es una confirmación al cliente
- Response: mensaje de texto plano

### Módulo 2: Perfil (requiere token, rol USUARIO o ADMIN)

GET  /api/perfil       → devuelve { id, nombre, correo, rol } SIN contraseña
PUT  /api/perfil       → actualiza nombre y/o contraseña
- Regla de negocio: nueva contraseña solo se cambia si viene en el body y no está vacía

### Módulo 3: Administración (requiere token, rol ADMIN)

GET    /api/admin/usuarios
PUT    /api/admin/usuarios/{id}/rol   → body: { rol: "ADMIN" | "USUARIO" }
DELETE /api/admin/usuarios/{id}

---

## MODELO DE DATOS

```java
// Usuario implementa UserDetails de Spring Security
// Campos: id (Long), nombre (String), correo (String, único),
//         contrasena (String, BCrypt), rol (Enum: USUARIO | ADMIN)
// getUsername() devuelve correo
// getPassword() devuelve contrasena
```

---

## REGLAS DE GENERACIÓN DE CÓDIGO

1. **Método privado para cada regla de negocio.**
   Si una validación tiene nombre, dale un método:
   ```java
   private void validarCorreoUnico(String correo) { ... }
   private void actualizarContrasenaIfPresent(Usuario u, String nueva) { ... }
   ```

2. **Sin magic strings.** Los roles van en el enum Rol, los mensajes de
   error en constantes o directo en la excepción, nunca hardcodeados
   en múltiples lugares.

3. **Excepciones semánticas.** Usa las de Spring Security cuando apliquen:
   - UsernameNotFoundException → usuario no encontrado
   - BadCredentialsException → credenciales inválidas
   - AccessDeniedException → sin permiso
   - IllegalArgumentException → dato de entrada inválido (correo duplicado, rol inválido)

4. **GlobalExceptionHandler centraliza todos los errores.**
   Ningún controller tiene try-catch.
   Los errores de @Valid se capturan con MethodArgumentNotValidException.

5. **Nunca exponer la contraseña en ninguna respuesta.**
   Usa Map.of() o un DTO de respuesta que excluya el campo contrasena.

6. **JWT stateless.** No uses HttpSession. SessionCreationPolicy = STATELESS.

7. **BCrypt para contraseñas.** Nunca guardes texto plano.

8. **CORS abierto para desarrollo.**
   En SecurityConfig permite todos los orígenes con allowedOriginPatterns("*").
   El otro grupo configurará el origen exacto en producción.

---

## CONFIGURACIÓN DE SEGURIDAD — RUTAS

```
/api/auth/**        → público (permitAll)
/api/perfil/**      → autenticado (USUARIO o ADMIN)
/api/admin/**       → solo ADMIN
/**                 → autenticado por defecto
```

---

## LO QUE NO DEBES GENERAR NUNCA

- Archivos .html, .css, .js, .ts
- Componentes React, Angular o Vue
- Plantillas Thymeleaf o FreeMarker
- Configuración de servidor web estático
- Comentarios sobre cómo el frontend debe consumir la API
  (eso lo maneja el otro grupo)
- Tests unitarios (a menos que se pidan explícitamente)
- Docker, CI/CD o configuración de despliegue

---

## CÓMO CONFIGURAR HAIKU EN VS CODE ANTES DE USAR ESTE PROMPT

1. Instala la extensión oficial: "Claude Code" de Anthropic en VS Code.
2. Abre la paleta de comandos (Ctrl+Shift+P) y ejecuta:
   "Claude: Open Settings"
3. En el campo "Default Model" escribe: claude-haiku-4-5-20251001
   O usa el comando /model en el chat de Claude Code y selecciona Haiku 4.5.
4. Coloca este archivo como CLAUDE.md en la raíz del proyecto.
   Claude Code lo lee automáticamente al iniciar cada sesión.
5. Para activar el prompt en una sesión nueva, escribe en el chat:
   "Lee el CLAUDE.md y comencemos con el módulo de autenticación."

---

## EJEMPLO DE INTERACCIÓN CORRECTA

Usuario: "Implementa el endpoint de registro"

Claude debe:
✅ Crear RegisterRequest.java en interfaces/
✅ Crear/actualizar IAuthService.java en interfaces/
✅ Crear/actualizar AuthServiceImpl.java en service/
✅ Crear/actualizar AuthController.java en controller/
✅ Aplicar @Valid, validarCorreoUnico(), BCrypt
✅ Responder solo con archivos .java

Claude NO debe:
❌ Crear login.html o cualquier archivo de frontend
❌ Poner lógica de negocio en AuthController
❌ Inyectar AuthServiceImpl directamente en el controller
❌ Omitir el GlobalExceptionHandler

---

## CHECKLIST ANTES DE ENTREGAR CADA ARCHIVO

- [ ] ¿El controller solo delega y no tiene lógica de negocio?
- [ ] ¿El controller inyecta la interfaz (I*Service), no la impl?
- [ ] ¿La implementación del service tiene métodos privados para cada regla?
- [ ] ¿Ninguna respuesta expone la contraseña?
- [ ] ¿Las excepciones son semánticas y las captura GlobalExceptionHandler?
- [ ] ¿El archivo está en el paquete correcto de la estructura?
- [ ] ¿No se generó ningún archivo de frontend?
