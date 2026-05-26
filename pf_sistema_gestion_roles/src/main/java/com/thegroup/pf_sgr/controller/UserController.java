package com.thegroup.pf_sgr.controller;

import com.thegroup.pf_sgr.repository.UsuarioRepository;
import com.thegroup.pf_sgr.model.Usuario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador para gestionar el perfil personal del usuario autenticado.
 * Responsabilidades:
 * - Editar perfil (nombre, correo, contraseña)
 * - Dar de baja la propia cuenta
 *
 * Principios SOLID aplicados:
 * - S: Single Responsibility - solo maneja solicitudes HTTP de perfil de usuario
 * - D: Dependency Inversion - inyecta interfaces y servicios, no implementaciones concretas
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UsuarioRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Actualiza el perfil del usuario autenticado (nombre, correo y/o contraseña).
     *
     * @param request DTO con los campos a actualizar
     * @return ResponseEntity con los datos del usuario actualizado
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        String correoActual = obtenerCorreoActual();

        Usuario usuario = userRepository.findByCorreo(correoActual)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Aplicar validaciones y actualizaciones usando métodos privados (SRP)
        actualizarCorreoIfPresent(usuario, request.getCorreo());
        actualizarNombreIfPresent(usuario, request.getNombre());
        actualizarContrasenaIfPresent(usuario, request.getContrasena());

        usuario = userRepository.save(usuario);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Perfil actualizado correctamente",
                "id", usuario.getId(),
                "nombre", usuario.getNombre(),
                "correo", usuario.getCorreo(),
                "rol", usuario.getRol()
        ));
    }

    /**
     * Elimina la cuenta del usuario autenticado después de confirmación.
     *
     * @param request DTO con el campo de confirmación
     * @return ResponseEntity con mensaje de éxito
     */
    @DeleteMapping("/account")
    public ResponseEntity<?> deleteAccount(@Valid @RequestBody DeleteAccountRequest request) {
        validarConfirmacionEliminar(request.getConfirmar());

        String correoActual = obtenerCorreoActual();
        Usuario usuario = userRepository.findByCorreo(correoActual)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        userRepository.deleteById(usuario.getId());

        return ResponseEntity.ok(Map.of(
                "mensaje", "Cuenta eliminada correctamente"
        ));
    }

    /**
     * Obtiene el correo del usuario autenticado desde el contexto de seguridad.
     * El nombre de usuario en Spring Security es el correo.
     *
     * @return correo del usuario autenticado
     * @throws IllegalArgumentException si no hay usuario autenticado
     */
    private String obtenerCorreoActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalArgumentException("Usuario no autenticado");
        }
        return auth.getName();
    }

    /**
     * Valida unicidad y actualiza el correo si está presente y es diferente.
     * Privado para respetar Single Responsibility Principle.
     *
     * @param usuario entidad Usuario
     * @param nuevoCorreo nuevo correo desde la solicitud
     * @throws IllegalArgumentException si el correo ya está registrado
     */
    private void actualizarCorreoIfPresent(Usuario usuario, String nuevoCorreo) {
        if (nuevoCorreo != null && !nuevoCorreo.isEmpty() && !nuevoCorreo.equals(usuario.getCorreo())) {
            validarCorreoUnico(nuevoCorreo);
            usuario.setCorreo(nuevoCorreo);
        }
    }

    /**
     * Actualiza el nombre si está presente y no está vacío.
     *
     * @param usuario entidad Usuario
     * @param nuevoNombre nuevo nombre desde la solicitud
     */
    private void actualizarNombreIfPresent(Usuario usuario, String nuevoNombre) {
        if (nuevoNombre != null && !nuevoNombre.isEmpty()) {
            usuario.setNombre(nuevoNombre);
        }
    }

    /**
     * Actualiza la contraseña con BCrypt si está presente y no está vacía.
     *
     * @param usuario entidad Usuario
     * @param nuevaContrasena nueva contraseña desde la solicitud
     */
    private void actualizarContrasenaIfPresent(Usuario usuario, String nuevaContrasena) {
        if (nuevaContrasena != null && !nuevaContrasena.isEmpty()) {
            usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        }
    }

    /**
     * Valida que el correo sea único en la base de datos.
     * Principio DIP: delega al repositorio, no toca la BD directamente.
     *
     * @param correo correo a validar
     * @throws IllegalArgumentException si el correo ya existe
     */
    private void validarCorreoUnico(String correo) {
        if (userRepository.existsByCorreo(correo)) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }
    }

    /**
     * Valida que el usuario haya confirmado la eliminación de su cuenta.
     *
     * @param confirmar valor booleano de confirmación
     * @throws IllegalArgumentException si no se confirma
     */
    private void validarConfirmacionEliminar(Boolean confirmar) {
        if (confirmar == null || !confirmar) {
            throw new IllegalArgumentException("Debe confirmar la eliminación de su cuenta");
        }
    }

    /**
     * DTO para actualizar el perfil del usuario.
     * Campos opcionales: nombre, correo y contraseña.
     * Validaciones aplicadas con Jakarta Validation.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProfileRequest {

        private String nombre;

        @Email(message = "El correo debe ser una dirección válida")
        private String correo;

        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        private String contrasena;
    }

    /**
     * DTO para confirmar la eliminación de cuenta del usuario.
     * Requiere confirmación explícita (confirmar = true).
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteAccountRequest {

        @NotNull(message = "Debe confirmar la eliminación de su cuenta")
        private Boolean confirmar;
    }
}
