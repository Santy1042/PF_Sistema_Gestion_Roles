async function cargarStats() {
    try {
      const res = await fetch(`${API}/admin/stats`, { headers: authHeaders() });
      if (res.ok) {
        const data = await res.json();
        document.getElementById('statSalesAmount').textContent = `$${parseFloat(data.totalSalesAmount || 0).toFixed(2)}`;
        document.getElementById('statSalesCount').textContent = data.totalOrders || 0;
      }
    } catch (e) {
      console.error('Error al cargar stats', e);
    }
  }

  async function cargarVentas() {
    try {
      const [salesRes, usersRes] = await Promise.all([
        fetch(`${API}/admin/sales`, { headers: authHeaders() }),
        fetch(`${API}/admin/usuarios`, { headers: authHeaders() })
      ]);
      if (!salesRes.ok) return;
      const data = await salesRes.json();
      const usersData = usersRes.ok ? await usersRes.json() : [];
      const userMap = {};
      usersData.forEach(u => { userMap[u.id] = `${u.firstName} ${u.lastName}`; });

      window.saleDetailsMap = window.saleDetailsMap || {};
      data.forEach(v => { window.saleDetailsMap[v.idSale] = v.details || []; });

      const body = document.getElementById('bodyVentas');
      body.innerHTML = data.length === 0
        ? '<tr><td colspan="7" style="text-align:center">Sin ventas</td></tr>'
        : data.map(v => {
          const userName = userMap[v.idUser] || `#${v.idUser}`;
          const hasPago = v.status === 'PAID' || v.status === 'REFUNDED';
          return `
          <tr>
            <td>#${v.idSale}</td>
            <td>${window.escapeHTML(userName)}</td>
            <td>${new Date(v.saleDate).toLocaleDateString()}</td>
            <td>$${v.total.toFixed(2)}</td>
            <td><span class="badge ${v.status === 'PAID' ? 'badge-success' : v.status === 'PENDING' ? 'badge-warning' : 'badge-danger'}">${v.status}</span></td>
            <td style="display:flex;gap:4px;flex-wrap:wrap">
              <button class="btn btn-outline btn-sm" onclick="editarVenta(${v.idSale}, '${v.status}', '${(window.escapeHTML(v.shippingAddress || '')).replace(/'/g, "\\'")}', '${(window.escapeHTML(v.statusReport || '')).replace(/'/g, "\\'")}')">Administrar</button>
              <button class="btn btn-outline btn-sm" onclick="verDetallesVenta(${v.idSale})">Ver Productos</button>
              ${hasPago && v.payment ? `<button class="btn btn-sm badge-info" style="border:1px solid var(--primary);cursor:pointer" onclick="verPago(${JSON.stringify(v.payment).replace(/"/g,'&quot;')}, ${v.idSale})">Ver Pago</button>` : ''}
            </td>
          </tr>`;
        }).join('');
    } catch (e) {
      console.error('Error al cargar ventas', e);
    }
  }

  window.editarVenta = function(id, status, address, report) {
    document.getElementById('modalVentaIdDisplay').textContent = id;
    document.getElementById('modalVentaId').value = id;
    document.getElementById('modalVentaStatus').value = status;
    document.getElementById('modalVentaAddress').value = address;
    document.getElementById('modalVentaReport').value = report;
    document.getElementById('modalVentaConfirm').value = '';
    document.getElementById('btnGuardarVenta').disabled = true;
    document.getElementById('modalVenta').style.display = 'flex';
  };

  window.verPago = function(payment, saleId) {
    document.getElementById('modalPagoSaleId').textContent = saleId;
    document.getElementById('modalPagoMetodo').textContent = payment.paymentMethod || '—';
    document.getElementById('modalPagoMonto').textContent = `$${parseFloat(payment.amount).toFixed(2)}`;
    document.getElementById('modalPagoEstado').textContent = payment.status || '—';
    document.getElementById('modalPagoFecha').textContent = payment.paymentDate ? new Date(payment.paymentDate).toLocaleString() : '—';
    document.getElementById('modalPagoId').textContent = payment.idPayment || '—';
    document.getElementById('modalPago').style.display = 'flex';
  };

  document.getElementById('btnCerrarModalPago').addEventListener('click', () => {
    document.getElementById('modalPago').style.display = 'none';
  });

  window.verDetallesVenta = function(saleId) {
    const details = window.saleDetailsMap[saleId] || [];
    const tbody = document.getElementById('bodyDetallesVenta');
    if (details.length === 0) {
      tbody.innerHTML = '<tr><td colspan="4" style="text-align:center">Sin productos</td></tr>';
    } else {
      tbody.innerHTML = details.map(d => `
        <tr>
          <td>${d.idVariant || '—'}</td>
          <td>
            <div style="font-weight: 500;">${d.productName || 'Producto'}</div>
            <div style="margin-top: 4px; display: flex; gap: 4px;">
              <span class="badge" style="font-size: 0.75rem; padding: 2px 6px; border: 1px solid var(--border-color); background: var(--surface-muted);">${d.color || 'Sin Color'}</span>
              <span class="badge" style="font-size: 0.75rem; padding: 2px 6px; border: 1px solid var(--border-color); background: var(--surface-muted);">${d.size || 'Sin Talla'}</span>
            </div>
          </td>
          <td>${d.quantity}</td>
          <td>$${parseFloat(d.totalPrice).toFixed(2)}</td>
        </tr>
      `).join('');
    }
    document.getElementById('modalDetallesVentaSaleId').textContent = saleId;
    document.getElementById('modalDetallesVenta').style.display = 'flex';
  };

  document.getElementById('btnCerrarModalDetallesVenta').addEventListener('click', () => {
    document.getElementById('modalDetallesVenta').style.display = 'none';
  });

  document.getElementById('btnCancelarVenta').addEventListener('click', () => {
    document.getElementById('modalVenta').style.display = 'none';
  });

  const confirmInput = document.getElementById('modalVentaConfirm');
  const btnGuardarVenta = document.getElementById('btnGuardarVenta');

  confirmInput.addEventListener('input', (e) => {
    if (e.target.value.trim().toLowerCase() === 'confirmar') {
      btnGuardarVenta.disabled = false;
    } else {
      btnGuardarVenta.disabled = true;
    }
  });

  btnGuardarVenta.addEventListener('click', async () => {
    const id = document.getElementById('modalVentaId').value;
    const status = document.getElementById('modalVentaStatus').value;
    const shippingAddress = document.getElementById('modalVentaAddress').value.trim();
    const statusReport = document.getElementById('modalVentaReport').value.trim();

    try {
      const res = await fetch(`${API}/admin/sales/${id}`, {
        method: 'PUT',
        headers: authHeaders(),
        body: JSON.stringify({ status, shippingAddress, statusReport })
      });

      if (res.ok) {
        showToastMsg('Venta actualizada correctamente');
        document.getElementById('modalVenta').style.display = 'none';
        cargarVentas();
        cargarStats(); // Refrescar stats por si cambió el status
      } else {
        showToastMsg('Error al actualizar venta', 'error');
      }
    } catch (e) {
      showToastMsg('Error de red', 'error');
    }
  });

  document.getElementById('btnNuevaCategoria').addEventListener('click', () => {
    document.getElementById('formCategoria').style.display = 'block';
  });
  document.getElementById('btnGuardarCategoria').addEventListener('click', guardarCategoria);
  document.getElementById('btnCancelarCategoria').addEventListener('click', () => {
    document.getElementById('formCategoria').style.display = 'none';
    document.getElementById('categoriaId').value = '';
    document.getElementById('categoriaNombre').value = '';
    document.getElementById('formCategoriaTitle').textContent = 'Crear Categoría';
  });

  document.getElementById('btnNuevoTag').addEventListener('click', () => {
    document.getElementById('formTag').style.display = 'block';
  });
  document.getElementById('btnGuardarTag').addEventListener('click', guardarTag);
  document.getElementById('btnCancelarTag').addEventListener('click', () => {
    document.getElementById('formTag').style.display = 'none';
    document.getElementById('tagId').value = '';
    document.getElementById('tagNombre').value = '';
    document.getElementById('formTagTitle').textContent = 'Crear Tag';
  });

  document.getElementById('btnNuevoColor').addEventListener('click', () => {
    document.getElementById('formColor').style.display = 'block';
  });
  document.getElementById('btnGuardarColor').addEventListener('click', guardarColor);
  document.getElementById('btnCancelarColor').addEventListener('click', () => {
    document.getElementById('formColor').style.display = 'none';
    document.getElementById('colorId').value = '';
    document.getElementById('colorNombre').value = '';
    document.getElementById('formColorTitle').textContent = 'Crear Color';
  });

  document.getElementById('btnNuevaTalla').addEventListener('click', () => {
    document.getElementById('formTalla').style.display = 'block';
  });
  document.getElementById('btnGuardarTalla').addEventListener('click', guardarTalla);
  document.getElementById('btnCancelarTalla').addEventListener('click', () => {
    document.getElementById('formTalla').style.display = 'none';
    document.getElementById('tallaId').value = '';
    document.getElementById('tallaNombre').value = '';
  });
