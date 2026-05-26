package com.thegroup.pf_sgr.interfaces;

import com.thegroup.pf_sgr.model.Usuario;

public interface IUsuarioRepository {
    Usuario findByCorreo(String correo);
    boolean existsByCorreo(String correo);
}

