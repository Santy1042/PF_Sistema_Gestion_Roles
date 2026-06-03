async function cargarUsuarios(busqueda = '', tipo = '') {
  let url = `${API}/admin/usuarios`;
  if (busqueda) {
    if (tipo === 'email')
      url = `${API}/admin/usuarios/buscar/email?email=${encodeURIComponent(busqueda)}`;
    else
      url = `${API}/admin/usuarios/buscar/nombre?nombre=${encodeURIComponent(busqueda)}`;
  }

  const res = await fetch(url, { headers: authHeaders() });
  if (!res.ok) { showToastMsg('Error cargando usuarios', 'error'); return; }
  const data = await res.json();
  const body = document.getElementById('bodyUsuarios');

  if (!busqueda) {
    document.getElementById('statUsers').textContent = data.length;
  }

  body.innerHTML = data.length === 0
    ? '<tr><td colspan="6" style="text-align:center">Sin usuarios</td></tr>'
    : data.map((u, idx) => `
      <tr>
        <td>${window.escapeHTML(u.firstName)} ${window.escapeHTML(u.lastName)}</td>
        <td>${window.escapeHTML(u.email)}</td>
        <td>${u.phoneNumber || '—'}</td>
        <td>${u.address || '—'}</td>
        <td><span class="badge ${u.active ? 'badge-success' : 'badge-danger'}">${u.active ? 'Activo' : 'Inactivo'}</span></td>
        <td>${u.role}</td>
        <td>
          <button class="btn btn-outline btn-sm" onclick="abrirModalUsuario(${u.id}, '${u.role}', '${window.escapeHTML(u.firstName)}', '${window.escapeHTML(u.lastName)}', '${window.escapeHTML(u.email)}', '${u.phoneNumber || ''}', '${u.address || ''}', ${u.active})">Editar Usuario</button>
        </td>
      </tr>`).join('');
}

let currentUserIdParaRol = null;

function abrirModalUsuario(id, currentRole, firstName, lastName, email, phone, address, isActive) {
  currentUserIdParaRol = id;
  document.getElementById('modalRolRole').value = currentRole;
  document.getElementById('modalUsuarioFirstName').value = firstName;
  document.getElementById('modalUsuarioLastName').value = lastName;
  document.getElementById('modalUsuarioEmail').value = email;
  document.getElementById('modalUsuarioPhone').value = phone;
  document.getElementById('modalUsuarioAddress').value = address;
  document.getElementById('modalUsuarioActive').value = isActive ? "true" : "false";
  document.getElementById('modalRol').style.display = 'flex';
}

let categoriaPage = 0;

async function cargarCategorias(page = 0) {
  categoriaPage = page;
  const res = await fetch(
    `${API}/categories?page=${page}&size=10`,
    {
        headers: authHeaders()
    }
);
  const data = await res.json();
  const items = data.content || [];
  const body = document.getElementById('bodyCategorias');

  document.getElementById('statCategories').textContent = data.totalElements ?? items.length;

  body.innerHTML = items.length === 0
    ? '<tr><td colspan="4" style="text-align:center">Sin categorías</td></tr>'
    : items.map(c => `
      <tr>
        <td>${c.idCategory}</td>
        <td>${c.name}</td>
        <td><span class="badge ${c.isActive !== false ? 'badge-success' : 'badge-danger'}">${c.isActive !== false ? 'Activa' : 'Inactiva'}</span></td>
        <td>
          <button class="btn btn-outline btn-sm" onclick="editarCategoria(${c.idCategory}, '${c.name}', ${c.isActive})">Editar</button>
          <button class="btn btn-danger btn-sm" onclick="eliminarCategoria(${c.idCategory})">Eliminar</button>
        </td>
      </tr>`).join('');

  renderPagination('paginacionCategorias', page, data.totalPages, cargarCategorias);
}

function editarCategoria(id, name, isActive) {
  document.getElementById('categoriaId').value = id;
  document.getElementById('categoriaNombre').value = name;
  document.getElementById('formCategoriaTitle').textContent = 'Editar Categoría';
  document.getElementById('formCategoria').style.display = 'block';
}

async function guardarCategoria() {
  const id = document.getElementById('categoriaId').value;
  const name = document.getElementById('categoriaNombre').value.trim();
  if (!name) { showToastMsg('Nombre requerido', 'error'); return; }

  const method = id ? 'PUT' : 'POST';
  const url = id ? `${API}/categories/updateCategory?categoryId=${id}` : `${API}/categories/createCategory`;

  const res = await fetch(url, {
    method, headers: authHeaders(),
    body: JSON.stringify({ name })
  });
  if (res.ok) {
    showToastMsg(id ? 'Categoría actualizada' : 'Categoría creada');
    document.getElementById('formCategoria').style.display = 'none';
    document.getElementById('categoriaId').value = '';
    document.getElementById('categoriaNombre').value = '';
    document.getElementById('formCategoriaTitle').textContent = 'Crear Categoría';
    cargarCategorias();
    cargarSelectCategorias(); // Actualizar selectores
  } else {
    showToastMsg('Error al guardar categoría', 'error');
  }
}

async function eliminarCategoria(id) {
  if (!confirm('¿Eliminar esta categoría?')) return;
  const res = await fetch(`${API}/categories/deleteCategory?categoryId=${id}`, {
    method: 'DELETE',
    headers: authHeaders()
  });
  if (res.ok) {
    showToastMsg('Categoría eliminada');
    cargarCategorias(categoriaPage);
  } else {
    showToastMsg('Error al eliminar', 'error');
  }
}

let tagPage = 0;

async function cargarTags(page = 0) {
  tagPage = page;
  const res = await fetch(
    `${API}/tags?page=${page}&size=10`,
    {
        headers: authHeaders()
    }
);
  const data = await res.json();
  const items = data.content || [];
  const body = document.getElementById('bodyTags');

  const statTagsEl = document.getElementById('statTags');
  if (statTagsEl) statTagsEl.textContent = data.totalElements ?? items.length;

  body.innerHTML = items.length === 0
    ? '<tr><td colspan="3" style="text-align:center">Sin tags</td></tr>'
    : items.map(t => `
      <tr>
        <td>${t.tagId}</td>
        <td>${t.name}</td>
        <td>
          <button class="btn btn-outline btn-sm" onclick="editarTag(${t.tagId}, '${t.name}')">Editar</button>
          <button class="btn btn-danger btn-sm" onclick="eliminarTag(${t.tagId})">Eliminar</button>
        </td>
      </tr>`).join('');

  renderPagination('paginacionTags', page, data.totalPages, cargarTags);

  const tagsSelect = document.getElementById('productoTags');
  if (page === 0) {

    fetch(`${API}/tags?page=0&size=100`, { headers: authHeaders() })
      .then(r => r.json())
      .then(d => {
        const allItems = d.content || [];
        tagsSelect.innerHTML = allItems.map(t => `<option value="${t.tagId}">${t.name}</option>`).join('');
      })
      .catch(e => console.error("Error loading tags for select", e));
  }
}

function editarTag(id, name) {
  document.getElementById('tagId').value = id;
  document.getElementById('tagNombre').value = name;
  document.getElementById('formTagTitle').textContent = 'Editar Tag';
  document.getElementById('formTag').style.display = 'block';
}

async function guardarTag() {
  const id = document.getElementById('tagId').value;
  const name = document.getElementById('tagNombre').value.trim();
  if (!name) { showToastMsg('Nombre requerido', 'error'); return; }

  const method = id ? 'PUT' : 'POST';
  const url = id ? `${API}/tags/updateTag?tagId=${id}` : `${API}/tags/createTag`;

  const res = await fetch(url, {
    method, headers: authHeaders(),
    body: JSON.stringify({ name })
  });
  if (res.ok) {
    showToastMsg(id ? 'Tag actualizado' : 'Tag creado');
    document.getElementById('formTag').style.display = 'none';
    document.getElementById('tagId').value = '';
    document.getElementById('tagNombre').value = '';
    document.getElementById('formTagTitle').textContent = 'Crear Tag';
    cargarTags();
  } else {
    showToastMsg('Error al guardar tag', 'error');
  }
}

async function eliminarTag(id) {
  if (!confirm('¿Eliminar este tag?')) return;
  const res = await fetch(`${API}/tags/deleteTag?tagId=${id}`, {
    method: 'DELETE',
    headers: authHeaders()
  });
  if (res.ok) {
    showToastMsg('Tag eliminado');
    cargarTags(tagPage);
  } else {
    showToastMsg('Error al eliminar', 'error');
  }
}

let colorPage = 0;

async function cargarColores(page = 0) {
  colorPage = page;
  const res = await fetch(
    `${API}/colors?page=${page}&size=10`,
    {
        headers: authHeaders()
    }
);
  const data = await res.json();
  const items = data.content || [];
  const body = document.getElementById('bodyColores');

  body.innerHTML = items.length === 0
    ? '<tr><td colspan="4" style="text-align:center">Sin colores</td></tr>'
    : items.map(c => `
      <tr>
        <td>${c.colorId}</td>
        <td>${c.name}</td>
        <td>
          <button class="btn btn-outline btn-sm" onclick="editarColor(${c.colorId}, '${c.name}')">Editar</button>
          <button class="btn btn-danger btn-sm" onclick="eliminarColor(${c.colorId})">Eliminar</button>
        </td>
      </tr>`).join('');

  renderPagination('paginacionColores', page, data.totalPages, cargarColores);

  const sel = document.getElementById('varianteColorId');
  const current = sel.value;
  sel.innerHTML = '<option value="">Sin color</option>' +
    items.map(c => `<option value="${c.colorId}">${c.name}</option>`).join('');
  sel.value = current;
}

function editarColor(id, name) {
  document.getElementById('colorId').value = id;
  document.getElementById('colorNombre').value = name;
  document.getElementById('formColorTitle').textContent = 'Editar Color';
  document.getElementById('formColor').style.display = 'block';
}

async function guardarColor() {
  const id = document.getElementById('colorId').value;
  const name = document.getElementById('colorNombre').value.trim();
  if (!name) { showToastMsg('Nombre requerido', 'error'); return; }

  const method = id ? 'PUT' : 'POST';
  const url = id ? `${API}/colors/updateColor?colorId=${id}` : `${API}/colors/createColor`;

  const res = await fetch(url, {
    method, headers: authHeaders(),
    body: JSON.stringify({ name })
  });
  if (res.ok) {
    showToastMsg(id ? 'Color actualizado' : 'Color creado');
    document.getElementById('formColor').style.display = 'none';
    document.getElementById('colorId').value = '';
    document.getElementById('colorNombre').value = '';
    document.getElementById('formColorTitle').textContent = 'Crear Color';
    cargarColores();
  } else {
    showToastMsg('Error al guardar color', 'error');
  }
}

async function eliminarColor(id) {
  if (!confirm('¿Eliminar este color?')) return;
  const res = await fetch(`${API}/colors/deleteColor?colorId=${id}`, {
    method: 'DELETE',
    headers: authHeaders()
  });
  if (res.ok) {
    showToastMsg('Color eliminado');
    cargarColores(colorPage);
  } else {
    showToastMsg('Error al eliminar', 'error');
  }
}

let tallaPage = 0;

async function cargarTallas(page = 0) {
  tallaPage = page;
  const res = await fetch(
    `${API}/sizes?page=${page}&size=10`,
    {
        headers: authHeaders()
    }
);
  const data = await res.json();
  const items = data.content || [];
  const body = document.getElementById('bodyTallas');

  body.innerHTML = items.length === 0
    ? '<tr><td colspan="3" style="text-align:center">Sin tallas</td></tr>'
    : items.map(s => `
      <tr>
        <td>${s.sizeId}</td>
        <td>${s.name}</td>
        <td>
          <button class="btn btn-outline btn-sm" onclick="editarTalla(${s.sizeId}, '${s.name}')">Editar</button>
          <button class="btn btn-danger btn-sm" onclick="eliminarTalla(${s.sizeId})">Eliminar</button>
        </td>
      </tr>`).join('');

  renderPagination('paginacionTallas', page, data.totalPages, cargarTallas);

  const sel = document.getElementById('varianteTallaId');
  const current = sel.value;
  sel.innerHTML = '<option value="">Sin talla</option>' +
    items.map(s => `<option value="${s.sizeId}">${s.name}</option>`).join('');
  sel.value = current;
}

function editarTalla(id, name) {
  document.getElementById('tallaId').value = id;
  document.getElementById('tallaNombre').value = name;
  document.getElementById('formTallaTitle').textContent = 'Editar Talla';
  document.getElementById('formTalla').style.display = 'block';
}

async function guardarTalla() {
  const id = document.getElementById('tallaId').value;
  const name = document.getElementById('tallaNombre').value.trim();
  if (!name) { showToastMsg('Nombre requerido', 'error'); return; }

  const method = id ? 'PUT' : 'POST';
  const url = id ? `${API}/sizes/updateSize?sizeId=${id}` : `${API}/sizes/createSize`;

  const res = await fetch(url, {
    method, headers: authHeaders(),
    body: JSON.stringify({ name })
  });
  if (res.ok) {
    showToastMsg(id ? 'Talla actualizada' : 'Talla creada');
    document.getElementById('formTalla').style.display = 'none';
    document.getElementById('tallaId').value = '';
    document.getElementById('tallaNombre').value = '';
    document.getElementById('formTallaTitle').textContent = 'Crear Talla';
    cargarTallas();
  } else {
    showToastMsg('Error al guardar talla', 'error');
  }
}

async function eliminarTalla(id) {
  if (!confirm('¿Eliminar esta talla?')) return;
  const res = await fetch(`${API}/sizes/deleteSize?sizeId=${id}`, {
    method: 'DELETE',
    headers: authHeaders()
  });
  if (res.ok) {
    showToastMsg('Talla eliminada');
    cargarTallas(tallaPage);
  } else {
    showToastMsg('Error al eliminar', 'error');
  }
}

async function cargarSelectCategorias() {
  try {
    const res = await fetch(`${API}/categories?page=0&size=100`, { headers: authHeaders() });
    const data = await res.json();
    const items = data.content || [];
    const sel = document.getElementById('productoCategoriaId');
    const current = sel.value;
    sel.innerHTML = '<option value="">Sin categoría</option>' +
      items.map(c => `<option value="${c.idCategory}">${c.name}</option>`).join('');
    sel.value = current;
  } catch (e) { console.error('Error cargando categorías select', e); }
}

document.addEventListener('DOMContentLoaded', async () => {
  await checkAdmin();

  cargarStats();
  cargarProductos();
  cargarSelectCategorias();
  cargarUsuarios();
  cargarVentas();
  cargarCategorias();
  cargarTags();
  cargarColores();
  cargarTallas();

  document.querySelectorAll('.admin-tab').forEach(tab => {
    tab.addEventListener('click', () => {
      document.querySelectorAll('.admin-tab').forEach(t => t.classList.remove('active'));
      document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));
      tab.classList.add('active');
      document.getElementById('tab-' + tab.dataset.tab).classList.add('active');

      if (tab.dataset.tab === 'productos') cargarProductos(0);
      if (tab.dataset.tab === 'ventas') cargarVentas();
    });
  });

  document.getElementById('btnNuevoProducto').addEventListener('click', () => {
    cancelarFormProducto();
    document.getElementById('formProducto').style.display = 'block';
  });
  document.getElementById('btnGuardarProducto').addEventListener('click', guardarProducto);
  document.getElementById('btnCancelarProducto').addEventListener('click', cancelarFormProducto);
  document.getElementById('btnBuscarProducto').addEventListener('click', () => {
    cargarProductos(0, document.getElementById('searchProducto').value);
  });
  document.getElementById('btnTodosProductos').addEventListener('click', () => {
    document.getElementById('searchProducto').value = '';
    cargarProductos(0);
  });
  document.getElementById('searchProducto').addEventListener('keydown', e => {
    if (e.key === 'Enter') cargarProductos(0, e.target.value);
  });

  document.getElementById('btnNuevaVariante').addEventListener('click', () => {
    cancelarFormVariante();
    document.getElementById('formVariante').style.display = 'block';
    cargarColores(); cargarTallas();
  });
  document.getElementById('btnGuardarVariante').addEventListener('click', guardarVariante);
  document.getElementById('btnCancelarVariante').addEventListener('click', cancelarFormVariante);

  document.getElementById('btnNuevoUsuario').addEventListener('click', () => {
    currentUserIdParaRol = null;
    document.getElementById('modalUsuarioTitle').textContent = 'Crear Usuario';
    document.getElementById('modalUsuarioFirstName').value = '';
    document.getElementById('modalUsuarioLastName').value = '';
    document.getElementById('modalUsuarioEmail').value = '';
    document.getElementById('modalUsuarioPhone').value = '';
    document.getElementById('modalUsuarioAddress').value = '';
    document.getElementById('modalRolRole').value = 'USER';
    document.getElementById('modalUsuarioActive').value = 'true';
    document.getElementById('modalUsuarioPassword').value = '';
    document.getElementById('modalUsuarioConfirmPassword').value = '';
    document.getElementById('modalRol').style.display = 'flex';
  });

  document.getElementById('btnBuscarUsuario').addEventListener('click', () => {
    const busqueda = document.getElementById('searchUsuario').value;
    const tipo = document.getElementById('searchUsuarioTipo').value;
    cargarUsuarios(busqueda, tipo);
  });
  document.getElementById('btnTodosUsuarios').addEventListener('click', () => {
    document.getElementById('searchUsuario').value = '';
    cargarUsuarios();
  });
  document.getElementById('searchUsuario').addEventListener('keydown', e => {
    if (e.key === 'Enter') {
      const tipo = document.getElementById('searchUsuarioTipo').value;
      cargarUsuarios(e.target.value, tipo);
    }
  });

  document.getElementById('btnCancelarRol').addEventListener('click', () => {
    document.getElementById('modalRol').style.display = 'none';
  });
  document.getElementById('btnConfirmarRol').addEventListener('click', async () => {
    const role = document.getElementById('modalRolRole').value;
    const firstName = document.getElementById('modalUsuarioFirstName').value.trim();
    const lastName = document.getElementById('modalUsuarioLastName').value.trim();
    const email = document.getElementById('modalUsuarioEmail').value.trim();
    const phone = document.getElementById('modalUsuarioPhone').value.trim();
    const address = document.getElementById('modalUsuarioAddress').value.trim();
    const isActive = document.getElementById('modalUsuarioActive').value === "true";
    const password = document.getElementById('modalUsuarioPassword').value.trim();
    const confirmPassword = document.getElementById('modalUsuarioConfirmPassword').value.trim();

    if (password && password !== confirmPassword) {
      showToastMsg('Las contraseñas no coinciden', 'error');
      return;
    }

    if (!currentUserIdParaRol) {
      if (!firstName || !email || !password) {
        showToastMsg('Nombre, email y contraseña son obligatorios', 'error');
        return;
      }
      if (password.length < 6) {
        showToastMsg('La contraseña debe tener al menos 6 caracteres', 'error');
        return;
      }
      try {
        const res = await fetch(`${API}/auth/register`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            firstName, lastName, email, phoneNumber: phone, password
          })
        });
        if (res.ok) {
          showToastMsg('Usuario creado');
          document.getElementById('modalRol').style.display = 'none';
          cargarUsuarios();
        } else {
          const errorData = await res.json().catch(() => ({}));
          showToastMsg(errorData.message || 'Error al crear usuario', 'error');
        }
      } catch (e) {
        showToastMsg('Error de red al crear', 'error');
      }
      return;
    }

    const body = {
      firstName, lastName, email, phoneNumber: phone, address, role, isActive
    };
    if (password) {
      if (password.length < 6) {
        showToastMsg('La nueva contraseña debe tener al menos 6 caracteres', 'error');
        return;
      }
      body.password = password;
    }

    try {
      const res = await fetch(`${API}/admin/usuarios/${currentUserIdParaRol}`, {
        method: 'PUT',
        headers: authHeaders(),
        body: JSON.stringify(body)
      });
      if (res.ok) {
        showToastMsg('Usuario actualizado');
        document.getElementById('modalRol').style.display = 'none';
        document.getElementById('modalUsuarioPassword').value = '';
        document.getElementById('modalUsuarioConfirmPassword').value = '';
        cargarUsuarios();
      } else {
        const errorData = await res.json().catch(() => ({}));
        showToastMsg(errorData.message || 'Error al actualizar usuario', 'error');
      }
    } catch (e) {
      showToastMsg('Error de red al actualizar', 'error');
    }
  });
});
