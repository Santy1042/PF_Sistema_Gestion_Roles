package com.thegroup.pf_sgr.interfaces;

import java.util.List;
import java.util.Map;

public interface IAdminService {
    List<PerfilResponse> listarUsuarios();
    PerfilResponse cambiarRol(Long id, Map<String, String> body);
    String eliminarUsuario(Long id);
}
