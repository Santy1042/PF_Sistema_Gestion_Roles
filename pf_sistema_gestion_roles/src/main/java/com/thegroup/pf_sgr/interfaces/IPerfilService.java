package com.thegroup.pf_sgr.interfaces;

public interface IPerfilService {
    PerfilResponse getPerfil(String correo);
    PerfilResponse updatePerfil(String correo, PerfilRequest request);
}
