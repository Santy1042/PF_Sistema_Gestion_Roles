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
        <td>${window.escapeHTML(p.name)}</td>
        <td>$${parseFloat(p.price).toFixed(2)}</td>
        <td>${window.escapeHTML(p.categoryName || '—')}</td>
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

  if (!nombre) {
    document.getElementById('statProducts').textContent = data.totalElements ?? items.length;
  }
}

async function editarProducto(id) {
  const res = await fetch(`${API}/products/${id}`);
  const p = await res.json();
  document.getElementById('productoId').value = p.productId;
  document.getElementById('productoNombre').value = p.name;
  document.getElementById('productoPrecio').value = p.price;

  setTimeout(() => {
    document.getElementById('productoCategoriaId').value = p.categoryId ?? p.category?.idCategory ?? '';
  }, 50);

  document.getElementById('productoDescuento').value = p.discountPercentage ?? 0;
  document.getElementById('productoActivo').checked = p.isActive;
  document.getElementById('productoDestacado').checked = p.isFeatured;

  const tagIds = p.tags?.map(t => t.tagId) || [];
  Array.from(document.getElementById('productoTags').options).forEach(o => {
    o.selected = tagIds.includes(parseInt(o.value));
  });

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
    categoryId: document.getElementById('productoCategoriaId').value ? parseInt(document.getElementById('productoCategoriaId').value) : null,
    discountPercentage: parseFloat(document.getElementById('productoDescuento').value) || 0,
    isActive: document.getElementById('productoActivo').checked,
    isFeatured: document.getElementById('productoDestacado').checked,
    tags: Array.from(document.getElementById('productoTags').selectedOptions).map(o => ({ tagId: parseInt(o.value) }))
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
    const savedProduct = await res.json();
    showToastMsg(id ? 'Producto actualizado' : 'Producto creado. Agrega al menos una variante.');
    cancelarFormProducto();
    cargarProductos(productoPage);

    if (!id && savedProduct.productId) {

      abrirModalVariantes(savedProduct.productId, savedProduct.name);
    }
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
  Array.from(document.getElementById('productoTags').options).forEach(o => o.selected = false);
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
  window.currentProductVariantsData = items;
  renderImageGallery();
  
  const body = document.getElementById('bodyVariantes');

  body.innerHTML = items.length === 0
    ? '<tr><td colspan="7" style="text-align:center">Sin variantes</td></tr>'
    : items.map(v => `
      <tr>
        <td>${v.id ?? v.variantId ?? '—'}</td>
        <td><img src="${v.imageUrl || 'img/default_product.png'}" style="width: 40px; height: 40px; object-fit: cover; border-radius: 4px;"></td>
        <td>${v.color?.name ?? v.color ?? '—'}</td>
        <td>${v.size?.name ?? v.size ?? '—'}</td>
        <td>${v.stock ?? 0}</td>
        <td><span class="badge ${v.isActive !== false ? 'badge-success' : 'badge-danger'}">${v.isActive !== false ? 'Activa' : 'Inactiva'}</span></td>
        <td style="display:flex;gap:4px;flex-wrap:wrap">
          <button class="btn btn-outline btn-sm" onclick="editarVariante(${v.id ?? v.variantId})">Editar</button>
          <button class="btn btn-sm ${v.isActive !== false ? 'btn-danger' : 'btn-outline'}" onclick="toggleVariante(${v.id ?? v.variantId}, ${v.isActive !== false})">
            ${v.isActive !== false ? 'Desactivar' : 'Activar'}
          </button>
          <button class="btn btn-danger btn-sm" onclick="eliminarVariante(${v.id ?? v.variantId})">Eliminar</button>
        </td>
      </tr>`).join('');
}

async function toggleVariante(id, isActive) {
  const endpoint = isActive ? 'deactivateVariant' : 'activateVariant';
  const method = isActive ? 'DELETE' : 'PATCH';
  const res = await fetch(`${API}/variants/${endpoint}?variantId=${id}`, {
    method, headers: authHeaders()
  });
  if (res.ok) {
    showToastMsg(`Variante ${isActive ? 'desactivada' : 'activada'}`);
    cargarVariantesPorProducto(currentProductoIdParaVariantes);
  } else {
    showToastMsg('Error al cambiar estado de la variante', 'error');
  }
}

async function editarVariante(id) {
  const res = await fetch(`${API}/variants/getVariantById?variantId=${id}`, { headers: authHeaders() });
  const v = await res.json();
  document.getElementById('varianteId').value = id;
  document.getElementById('varianteProductoId').value = v.product?.productId ?? v.productId ?? '';
  document.getElementById('varianteColorId').value = v.color?.colorId ?? v.colorId ?? '';
  document.getElementById('varianteTallaId').value = v.size?.sizeId ?? v.sizeId ?? '';
  document.getElementById('varianteStock').value = v.stock ?? 0;
  document.getElementById('varianteImageUrl').value = v.imageUrl ?? '';
  document.getElementById('formVarianteTitle').textContent = 'Editar Variante';
  document.getElementById('formVariante').style.display = 'block';
}

async function guardarVariante() {
  const id = document.getElementById('varianteId').value;
  let imageUrl = document.getElementById('varianteImageUrl').value.trim();
  const fileInput = document.getElementById('varianteImageFile');
  
  if (!imageUrl && fileInput.files.length === 0) {
    showToastMsg('La imagen de la variante es obligatoria', 'error');
    return;
  }

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
      ? { colorId: parseInt(document.getElementById('varianteColorId').value) } : null,
    size: document.getElementById('varianteTallaId').value
      ? { sizeId: parseInt(document.getElementById('varianteTallaId').value) } : null,
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
  document.querySelectorAll('.gallery-img-selector').forEach(img => img.style.borderColor = 'transparent');
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

function renderImageGallery() {
  const gallery = document.getElementById('varianteImageGallery');
  if (!gallery) return;
  const items = window.currentProductVariantsData || [];
  const uniqueUrls = [...new Set(items.map(v => v.imageUrl).filter(url => url && !url.includes('default_product.png')))];
  
  if (uniqueUrls.length === 0) {
    gallery.innerHTML = '<span style="font-size: 0.8em; color: var(--text-secondary);">No hay fotos previas para este producto.</span>';
    return;
  }

  gallery.innerHTML = uniqueUrls.map(url => `
    <img src="${url}" 
         class="gallery-img-selector"
         style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px; cursor: pointer; border: 2px solid transparent; transition: all 0.2s ease;"
         onclick="selectGalleryImage('${url}', this)"
         title="Usar esta imagen">
  `).join('');
}

window.selectGalleryImage = function(url, imgElement) {
  document.querySelectorAll('.gallery-img-selector').forEach(img => img.style.borderColor = 'transparent');
  imgElement.style.borderColor = 'var(--primary)';
  document.getElementById('varianteImageUrl').value = url;
  document.getElementById('varianteImageFile').value = '';
};
