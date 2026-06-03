const API = 'http://localhost:8080/api';

function getToken() {
  return localStorage.getItem('authToken');
}

function authHeaders() {
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${getToken()}`
  };
}

// Redirigir si no hay token o no es admin
async function checkAdmin() {
  const token = getToken();
  if (!token) { window.location.href = 'login.html'; return; }
  try {
    const res = await fetch(`${API}/profile`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!res.ok) throw new Error();
    const data = await res.json();
    if (data.role !== 'ADMIN') {
      alert('Acceso denegado. Solo administradores.');
      window.location.href = 'index.html';
    }
  } catch {
    window.location.href = 'login.html';
  }
}

// ===== UTILIDADES =====
function showToastMsg(msg, type = 'success') {
  if (window.showToast) showToast(msg, type);
  else alert(msg);
}

function renderPagination(containerId, currentPage, totalPages, onPageClick) {
  const container = document.getElementById(containerId);
  container.innerHTML = '';
  if (totalPages <= 1) return;
  for (let i = 0; i < totalPages; i++) {
    const btn = document.createElement('button');
    btn.textContent = i + 1;
    if (i === currentPage) btn.classList.add('active');
    btn.addEventListener('click', () => onPageClick(i));
    container.appendChild(btn);
  }
}

// =====================================================
// =================== PRODUCTOS =======================
// =====================================================
let productoPage = 0;

async function cargarProductos(page = 0, nombre = '') {
  productoPage = page;
  let url = nombre
    ? `${API}/products/search?name=${encodeURIComponent(nombre)}&page=${page}&size=10`
    : `${API}/products?page=${page}&size=10`;

  const res = await fetch(url);
  const data = await res.json();
  const items = data.content || [];
  const body = document.getElementById('bodyProductos');

  body.innerHTML = items.length === 0
    ? '<tr><td colspan="8" style="text-align:center">Sin resultados</td></tr>'
    : items.map(p => `
      <tr>
        <td>${p.productId}</td>
        <td>${p.name}</td>
        <td>$${parseFloat(p.price).toFixed(2)}</td>
        <td>${p.categoryName || '—'}</td>
        <td>${p.discountPercentage ?? 0}%</td>
        <td><span class="badge ${p.isActive ? 'badge-success' : 'badge-danger'}">${p.isActive ? 'Activo' : 'Inactivo'}</span></td>
        <td>${p.isFeatured ? '⭐' : '—'}</td>
        <td style="display:flex;gap:4px;flex-wrap:wrap">
          <button class="btn btn-primary btn-sm" onclick="abrirModalVariantes(${p.productId}, '${p.name.replace(/'/g, "\\'")}')">Ver Variantes</button>
          <button class="btn btn-outline btn-sm" onclick="editarProducto(${p.productId})">Editar</button>
          <button class="btn btn-sm ${p.isActive ? 'btn-danger' : 'btn-outline'}" onclick="toggleProducto(${p.productId}, ${p.isActive})">
            ${p.isActive ? 'Desactivar' : 'Activar'}
          </button>
          <button class="btn btn-danger btn-sm" onclick="eliminarProducto(${p.productId})">Eliminar</button>
        </td>
      </tr>`).join('');

  renderPagination('paginacionProductos', page, data.totalPages, (p) => cargarProductos(p, nombre));

  // Actualizar stat
  document.getElementById('statProducts').textContent = data.totalElements ?? items.length;
}

async function editarProducto(id) {
  const res = await fetch(`${API}/products/${id}`);
  const p = await res.json();
  document.getElementById('productoId').value = p.productId;
  document.getElementById('productoNombre').value = p.name;
  document.getElementById('productoPrecio').value = p.price;
  document.getElementById('productoCategoriaId').value = p.categoryId ?? '';
  document.getElementById('productoDescuento').value = p.discountPercentage ?? 0;
  document.getElementById('productoActivo').checked = p.isActive;
  document.getElementById('productoDestacado').checked = p.isFeatured;
  document.getElementById('formProductoTitle').textContent = 'Editar Producto';
  document.getElementById('formProducto').style.display = 'block';
  document.getElementById('formProducto').scrollIntoView({ behavior: 'smooth' });
}

async function toggleProducto(id, isActive) {
  const endpoint = isActive ? 'deactivate' : 'activate';
  const res = await fetch(`${API}/products/${id}/${endpoint}`, {
    method: 'PATCH',
    headers: authHeaders()
  });
  if (res.ok) {
    showToastMsg(`Producto ${isActive ? 'desactivado' : 'activado'}`);
    cargarProductos(productoPage);
  } else {
    showToastMsg('Error al cambiar estado', 'error');
  }
}

async function eliminarProducto(id) {
  if (!confirm('¿Eliminar este producto?')) return;
  const res = await fetch(`${API}/products/${id}`, {
    method: 'DELETE',
    headers: authHeaders()
  });
  if (res.ok) {
    showToastMsg('Producto eliminado');
    cargarProductos(productoPage);
  } else {
    showToastMsg('Error al eliminar', 'error');
  }
}

async function guardarProducto() {
  const id = document.getElementById('productoId').value;
  const body = {
    name: document.getElementById('productoNombre').value.trim(),
    price: parseFloat(document.getElementById('productoPrecio').value),
    categoryId: parseInt(document.getElementById('productoCategoriaId').value) || null,
    discountPercentage: parseInt(document.getElementById('productoDescuento').value) || 0,
    isActive: document.getElementById('productoActivo').checked,
    isFeatured: document.getElementById('productoDestacado').checked
  };

  if (!body.name || isNaN(body.price)) {
    showToastMsg('Nombre y precio son obligatorios', 'error'); return;
  }

  const method = id ? 'PUT' : 'POST';
  const url = id ? `${API}/products/${id}` : `${API}/products`;

  const res = await fetch(url, {
    method,
    headers: authHeaders(),
    body: JSON.stringify(body)
  });

  if (res.ok) {
    showToastMsg(id ? 'Producto actualizado' : 'Producto creado');
    cancelarFormProducto();
    cargarProductos(productoPage);
  } else {
    const err = await res.json().catch(() => ({}));
    showToastMsg(err.message || 'Error al guardar', 'error');
  }
}

function cancelarFormProducto() {
  document.getElementById('formProducto').style.display = 'none';
  document.getElementById('productoId').value = '';
  document.getElementById('productoNombre').value = '';
  document.getElementById('productoPrecio').value = '';
  document.getElementById('productoDescuento').value = 0;
  document.getElementById('productoActivo').checked = true;
  document.getElementById('productoDestacado').checked = false;
  document.getElementById('formProductoTitle').textContent = 'Crear Producto';
}

let currentProductoIdParaVariantes = null;

async function abrirModalVariantes(productId, productName) {
  currentProductoIdParaVariantes = productId;
  document.getElementById('modalVariantesTitle').textContent = `Variantes de: ${productName}`;
  document.getElementById('modalVariantes').style.display = 'flex';
  cancelarFormVariante();
  await cargarVariantesPorProducto(productId);
}

document.getElementById('btnCerrarModalVariantes').addEventListener('click', () => {
  document.getElementById('modalVariantes').style.display = 'none';
  currentProductoIdParaVariantes = null;
});

async function cargarVariantesPorProducto(productId) {
  const res = await fetch(`${API}/products/${productId}`, { headers: authHeaders() });
  const data = await res.json();
  const items = data.variants || [];
  const body = document.getElementById('bodyVariantes');

  body.innerHTML = items.length === 0
    ? '<tr><td colspan="7" style="text-align:center">Sin variantes</td></tr>'
    : items.map(v => `
      <tr>
        <td>${v.id ?? v.variantId ?? '—'}</td>
        <td><img src="${v.imageUrl || 'img/placeholder.png'}" style="width: 40px; height: 40px; object-fit: cover; border-radius: 4px;"></td>
        <td>${v.color?.name ?? '—'}</td>
        <td>${v.size?.name ?? '—'}</td>
        <td>${v.stock ?? 0}</td>
        <td><span class="badge ${v.isActive !== false ? 'badge-success' : 'badge-danger'}">${v.isActive !== false ? 'Activa' : 'Inactiva'}</span></td>
        <td style="display:flex;gap:4px;flex-wrap:wrap">
          <button class="btn btn-outline btn-sm" onclick="editarVariante(${v.id ?? v.variantId})">Editar</button>
          <button class="btn btn-danger btn-sm" onclick="eliminarVariante(${v.id ?? v.variantId})">Eliminar</button>
        </td>
      </tr>`).join('');
}

async function editarVariante(id) {
  const res = await fetch(`${API}/variants/getVariantById?variantId=${id}`);
  const v = await res.json();
  document.getElementById('varianteId').value = id;
  document.getElementById('varianteProductoId').value = v.product?.productId ?? v.productId ?? '';
  document.getElementById('varianteColorId').value = v.color?.id ?? v.colorId ?? '';
  document.getElementById('varianteTallaId').value = v.size?.id ?? v.sizeId ?? '';
  document.getElementById('varianteStock').value = v.stock ?? 0;
  document.getElementById('varianteImageUrl').value = v.imageUrl ?? '';
  document.getElementById('formVarianteTitle').textContent = 'Editar Variante';
  document.getElementById('formVariante').style.display = 'block';
}

async function guardarVariante() {
  const id = document.getElementById('varianteId').value;
  let imageUrl = document.getElementById('varianteImageUrl').value.trim();
  const fileInput = document.getElementById('varianteImageFile');
  if (fileInput.files.length > 0) {
    const formData = new FormData();
    formData.append('file', fileInput.files[0]);
    const uploadRes = await fetch(`${API}/images/upload`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${getToken()}` },
      body: formData
    });
    if (uploadRes.ok) {
      const uploadData = await uploadRes.json();
      imageUrl = uploadData.url;
    } else {
      showToastMsg('Error subiendo imagen', 'error'); return;
    }
  }

  const body = {
    product: { productId: currentProductoIdParaVariantes },
    color: document.getElementById('varianteColorId').value
      ? { id: parseInt(document.getElementById('varianteColorId').value) } : null,
    size: document.getElementById('varianteTallaId').value
      ? { id: parseInt(document.getElementById('varianteTallaId').value) } : null,
    stock: parseInt(document.getElementById('varianteStock').value) || 0,
    imageUrl
  };

  const url = id
    ? `${API}/variants/updateVariant?variantId=${id}`
    : `${API}/variants/createVariant`;
  const method = id ? 'PUT' : 'POST';

  const res = await fetch(url, {
    method,
    headers: authHeaders(),
    body: JSON.stringify(body)
  });

  if (res.ok) {
    showToastMsg(id ? 'Variante actualizada' : 'Variante creada');
    cancelarFormVariante();
    cargarVariantesPorProducto(currentProductoIdParaVariantes);
  } else {
    showToastMsg('Error al guardar variante', 'error');
  }
}

function cancelarFormVariante() {
  document.getElementById('formVariante').style.display = 'none';
  document.getElementById('varianteId').value = '';
  document.getElementById('varianteStock').value = '';
  document.getElementById('varianteImageUrl').value = '';
  document.getElementById('varianteImageFile').value = '';
  document.getElementById('formVarianteTitle').textContent = 'Crear Variante';
}

async function eliminarVariante(id) {
  if (!confirm('¿Eliminar esta variante?')) return;
  const res = await fetch(`${API}/variants/deleteVariant?variantId=${id}`, {
    method: 'DELETE',
    headers: authHeaders()
  });
  if (res.ok) {
    showToastMsg('Variante eliminada');
    cargarVariantesPorProducto(currentProductoIdParaVariantes);
  } else {
    showToastMsg('Error al eliminar', 'error');
  }
}


// =====================================================
// =================== USUARIOS ========================
// =====================================================
async function cargarUsuarios(busqueda = '', tipo = '') {
  let url = `${API}/admin/usuarios`;
  if (busqueda) {
    if (busqueda.includes('@'))
      url = `${API}/admin/usuarios/buscar/email?email=${encodeURIComponent(busqueda)}`;
    else
      url = `${API}/admin/usuarios/buscar/nombre?nombre=${encodeURIComponent(busqueda)}`;
  }

  const res = await fetch(url, { headers: authHeaders() });
  if (!res.ok) { showToastMsg('Error cargando usuarios', 'error'); return; }
  const data = await res.json();
  const body = document.getElementById('bodyUsuarios');

  document.getElementById('statUsers').textContent = data.length;

  body.innerHTML = data.length === 0
    ? '<tr><td colspan="6" style="text-align:center">Sin usuarios</td></tr>'
    : data.map((u, idx) => `
      <tr>
        <td>${u.firstName}</td>
        <td>${u.lastName}</td>
        <td>${u.email}</td>
        <td>${u.phoneNumber || '—'}</td>
        <td><span class="badge ${u.role === 'ADMIN' ? 'badge-warning' : 'badge-info'}">${u.role}</span></td>
        <td style="display:flex;gap:4px;flex-wrap:wrap">
          <button class="btn btn-outline btn-sm" onclick="abrirModalRol('${u.email}', '${u.firstName} ${u.lastName}', '${u.role}')">Cambiar Rol</button>
        </td>
      </tr>`).join('');
}

// ⚠️ AVISO AL BACKEND: changeRole y deleteUser necesitan el ID del usuario.
// ProfileResponse no lo incluye. Hay que agregar `private Long id;` a ProfileResponse.java
// Por ahora, cambiar rol usa email para identificar al usuario si el backend lo soporta,
// pero actualmente solo funciona con ID en el endpoint PUT /api/admin/usuarios/{id}/rol

function abrirModalRol(email, nombre, rolActual) {
  document.getElementById('modalRolNombre').textContent = `Usuario: ${nombre} (${email})`;
  document.getElementById('modalRolUserId').value = email; // Temporal hasta que el backend devuelva ID
  document.getElementById('modalRolSelect').value = rolActual;
  document.getElementById('modalRol').style.display = 'flex';
}

// =====================================================
// =================== CATEGORÍAS ======================
// =====================================================
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
    ? '<tr><td colspan="3" style="text-align:center">Sin categorías</td></tr>'
    : items.map(c => `
        <tr>
            <td>${c.idCategory}</td>
            <td>${c.name}</td>
            <td>
            <button
                type="button"
                class="btn btn-danger btn-sm"
                onclick="eliminarCategoria(${Number(c.idCategory)})">
                Eliminar
            </button>
            </td>
        </tr>
        `).join('');

  renderPagination('paginacionCategorias', page, data.totalPages, cargarCategorias);
}

async function guardarCategoria() {
  const nombre = document.getElementById('categoriaNombre').value.trim();
  if (!nombre) { showToastMsg('El nombre es obligatorio', 'error'); return; }

  const res = await fetch(`${API}/categories/createCategory`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify({ name: nombre })
  });

  if (res.ok) {
    showToastMsg('Categoría creada');
    document.getElementById('categoriaNombre').value = '';
    document.getElementById('formCategoria').style.display = 'none';
    cargarCategorias(categoriaPage);
    cargarSelectCategorias(); // recargar el select de productos
  } else {
    showToastMsg('Error al crear categoría', 'error');
  }
}

async function eliminarCategoria(id) {
  console.log("ID categoria:", id);

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

// =====================================================
// ====================== TAGS =========================
// =====================================================
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

  document.getElementById('statTags').textContent = data.totalElements ?? items.length;

  body.innerHTML = items.length === 0
    ? '<tr><td colspan="3" style="text-align:center">Sin tags</td></tr>'
    : items.map(t => `
      <tr>
        <td>${t.id ?? t.tagId}</td>
        <td>${t.name}</td>
        <td>
          <button class="btn btn-danger btn-sm" onclick="eliminarTag(${t.id ?? t.tagId})">Eliminar</button>
        </td>
      </tr>`).join('');

  renderPagination('paginacionTags', page, data.totalPages, cargarTags);
}

async function guardarTag() {
  const nombre = document.getElementById('tagNombre').value.trim();
  if (!nombre) { showToastMsg('El nombre es obligatorio', 'error'); return; }

  const res = await fetch(`${API}/tags/createTag`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify({ name: nombre })
  });

  if (res.ok) {
    showToastMsg('Tag creado');
    document.getElementById('tagNombre').value = '';
    document.getElementById('formTag').style.display = 'none';
    cargarTags(tagPage);
  } else {
    showToastMsg('Error al crear tag', 'error');
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

// =====================================================
// =================== COLORES =========================
// =====================================================
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
        <td>${c.id ?? c.colorId}</td>
        <td>${c.name}</td>
        <td><span class="color-swatch" style="background:${c.hexCode || c.hex || '#888'}"></span> ${c.hexCode || c.hex || '—'}</td>
        <td>
          <button class="btn btn-danger btn-sm" onclick="eliminarColor(${c.id ?? c.colorId})">Eliminar</button>
        </td>
      </tr>`).join('');

  renderPagination('paginacionColores', page, data.totalPages, cargarColores);

  // Actualizar select de variantes
  const sel = document.getElementById('varianteColorId');
  const current = sel.value;
  sel.innerHTML = '<option value="">Sin color</option>' +
    items.map(c => `<option value="${c.id ?? c.colorId}">${c.name}</option>`).join('');
  sel.value = current;
}

async function guardarColor() {
  const nombre = document.getElementById('colorNombre').value.trim();
  const hex = document.getElementById('colorHex').value;
  if (!nombre) { showToastMsg('El nombre es obligatorio', 'error'); return; }

  const res = await fetch(`${API}/colors/createColor`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify({ name: nombre, hexCode: hex })
  });

  if (res.ok) {
    showToastMsg('Color creado');
    document.getElementById('colorNombre').value = '';
    document.getElementById('formColor').style.display = 'none';
    cargarColores(colorPage);
  } else {
    showToastMsg('Error al crear color', 'error');
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

// =====================================================
// =================== TALLAS ==========================
// =====================================================
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
        <td>${s.id ?? s.sizeId}</td>
        <td>${s.name}</td>
        <td>
          <button class="btn btn-danger btn-sm" onclick="eliminarTalla(${s.id ?? s.sizeId})">Eliminar</button>
        </td>
      </tr>`).join('');

  renderPagination('paginacionTallas', page, data.totalPages, cargarTallas);

  // Actualizar select de variantes
  const sel = document.getElementById('varianteTallaId');
  const current = sel.value;
  sel.innerHTML = '<option value="">Sin talla</option>' +
    items.map(s => `<option value="${s.id ?? s.sizeId}">${s.name}</option>`).join('');
  sel.value = current;
}

async function guardarTalla() {
  const nombre = document.getElementById('tallaNombre').value.trim();
  if (!nombre) { showToastMsg('El nombre es obligatorio', 'error'); return; }

  const res = await fetch(`${API}/sizes/createSize`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify({ name: nombre })
  });

  if (res.ok) {
    showToastMsg('Talla creada');
    document.getElementById('tallaNombre').value = '';
    document.getElementById('formTalla').style.display = 'none';
    cargarTallas(tallaPage);
  } else {
    showToastMsg('Error al crear talla', 'error');
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

// =====================================================
// ======= CARGAR SELECTS AUXILIARES ===================
// =====================================================
async function cargarSelectCategorias() {
  try {
    const res = await fetch(`${API}/categories?page=0&size=100`);
    const data = await res.json();
    const items = data.content || [];
    const sel = document.getElementById('productoCategoriaId');
    const current = sel.value;
    sel.innerHTML = '<option value="">Sin categoría</option>' +
      items.map(c => `<option value="${c.idCategory}">${c.name}</option>`).join('');
    sel.value = current;
  } catch (e) { console.error('Error cargando categorías select', e); }
}

// =====================================================
// ============= TABS Y EVENTOS ========================
// =====================================================
document.addEventListener('DOMContentLoaded', async () => {
  await checkAdmin();

  // Cargar datos iniciales
  cargarProductos();
  cargarSelectCategorias();
  cargarUsuarios();
  cargarCategorias();
  cargarTags();
  cargarColores();
  cargarTallas();

  // Tabs
  document.querySelectorAll('.admin-tab').forEach(tab => {
    tab.addEventListener('click', () => {
      document.querySelectorAll('.admin-tab').forEach(t => t.classList.remove('active'));
      document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));
      tab.classList.add('active');
      document.getElementById('tab-' + tab.dataset.tab).classList.add('active');

      if (tab.dataset.tab === 'productos') cargarProductos(0);
    });
  });

  // ---- PRODUCTOS ----
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

  // ---- VARIANTES ----
  document.getElementById('btnNuevaVariante').addEventListener('click', () => {
    cancelarFormVariante();
    document.getElementById('formVariante').style.display = 'block';
    cargarColores(); cargarTallas();
  });
  document.getElementById('btnGuardarVariante').addEventListener('click', guardarVariante);
  document.getElementById('btnCancelarVariante').addEventListener('click', cancelarFormVariante);

  // ---- USUARIOS ----
  document.getElementById('btnBuscarUsuario').addEventListener('click', () => {
    cargarUsuarios(document.getElementById('searchUsuario').value);
  });
  document.getElementById('btnTodosUsuarios').addEventListener('click', () => {
    document.getElementById('searchUsuario').value = '';
    cargarUsuarios();
  });
  document.getElementById('searchUsuario').addEventListener('keydown', e => {
    if (e.key === 'Enter') cargarUsuarios(e.target.value);
  });

  // Modal rol
  document.getElementById('btnCancelarRol').addEventListener('click', () => {
    document.getElementById('modalRol').style.display = 'none';
  });
  document.getElementById('btnConfirmarRol').addEventListener('click', async () => {
    // ⚠️ Este endpoint necesita el ID numérico del usuario.
    // Cuando el backend agregue `id` al ProfileResponse, reemplazar esta lógica.
    showToastMsg('⚠️ Pendiente: el backend debe devolver el ID del usuario en ProfileResponse', 'error');
    document.getElementById('modalRol').style.display = 'none';
  });

  // ---- CATEGORÍAS ----
  document.getElementById('btnNuevaCategoria').addEventListener('click', () => {
    document.getElementById('formCategoria').style.display = 'block';
  });
  document.getElementById('btnGuardarCategoria').addEventListener('click', guardarCategoria);
  document.getElementById('btnCancelarCategoria').addEventListener('click', () => {
    document.getElementById('formCategoria').style.display = 'none';
    document.getElementById('categoriaNombre').value = '';
  });

  // ---- TAGS ----
  document.getElementById('btnNuevoTag').addEventListener('click', () => {
    document.getElementById('formTag').style.display = 'block';
  });
  document.getElementById('btnGuardarTag').addEventListener('click', guardarTag);
  document.getElementById('btnCancelarTag').addEventListener('click', () => {
    document.getElementById('formTag').style.display = 'none';
    document.getElementById('tagNombre').value = '';
  });

  // ---- COLORES ----
  document.getElementById('btnNuevoColor').addEventListener('click', () => {
    document.getElementById('formColor').style.display = 'block';
  });
  document.getElementById('btnGuardarColor').addEventListener('click', guardarColor);
  document.getElementById('btnCancelarColor').addEventListener('click', () => {
    document.getElementById('formColor').style.display = 'none';
    document.getElementById('colorNombre').value = '';
  });

  // ---- TALLAS ----
  document.getElementById('btnNuevaTalla').addEventListener('click', () => {
    document.getElementById('formTalla').style.display = 'block';
  });
  document.getElementById('btnGuardarTalla').addEventListener('click', guardarTalla);
  document.getElementById('btnCancelarTalla').addEventListener('click', () => {
    document.getElementById('formTalla').style.display = 'none';
    document.getElementById('tallaNombre').value = '';
  });
});