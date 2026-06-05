window.escapeHTML = function(str) {
    if (str === null || str === undefined) return '';
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
};


const authToken = localStorage.getItem('authToken');

let currentOrders = [];
let pendingAction = null; // { saleId, action: 'pay' | 'cancel' }

document.addEventListener('DOMContentLoaded', () => {
    if (!authToken) {
        window.location.href = 'login.html';
        return;
    }
    loadOrders();
});

async function loadOrders() {
    try {
        const response = await fetch(`${API_BASE_URL}/sales/my-orders`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (!response.ok) {
            throw new Error('No se pudieron cargar los pedidos');
        }

        currentOrders = await response.json();
        renderOrders();
    } catch (error) {
        console.error(error);
        document.getElementById('ordersList').innerHTML = `
            <div class="no-orders">
                <p>Ocurrió un error al cargar tus pedidos. Por favor, intenta de nuevo.</p>
            </div>
        `;
    }
}

function renderOrders() {
    const container = document.getElementById('ordersList');

    if (currentOrders.length === 0) {
        container.innerHTML = `
            <div class="no-orders">
                <p>Aún no tienes ningún pedido.</p>
                <a href="products.html" class="btn btn-primary" style="margin-top: 1rem; display: inline-block;">Ir a la tienda</a>
            </div>
        `;
        return;
    }

    currentOrders.sort((a, b) => new Date(b.saleDate) - new Date(a.saleDate));

    container.innerHTML = currentOrders.map(order => {
        const date = new Date(order.saleDate).toLocaleDateString('es-ES', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });

        let statusText = order.status;
        let actionButtons = '';

        if (order.status === 'PENDING') {
            statusText = 'PENDIENTE';
            actionButtons = `
                <div class="order-actions">
                    <button class="btn btn-primary" onclick="goToCheckout(${order.idSale})">Pagar</button>
                    <button class="btn btn-outline" onclick="openReportModal(${order.idSale}, 'cancel')" style="color: var(--error); border-color: var(--error);">Cancelar</button>
                    <button class="btn btn-outline" onclick="verDetallesVenta(${order.idSale})">Ver Productos</button>
                </div>
            `;
        } else if (order.status === 'PAID') {
            statusText = 'PAGADO';
            actionButtons = `
                <div class="order-actions">
                    <button class="btn btn-secondary" onclick="openInvoiceModal(${order.idSale})">Ver Factura</button>
                    <button class="btn btn-outline" onclick="verDetallesVenta(${order.idSale})">Ver Productos</button>
                </div>
            `;
        } else if (order.status === 'CANCELLED') {
            statusText = 'CANCELADO';
            actionButtons = `
                <div class="order-actions">
                    <button class="btn btn-outline" onclick="verDetallesVenta(${order.idSale})">Ver Productos</button>
                </div>
            `;
        } else if (order.status === 'REFUNDED') {
            statusText = 'REEMBOLSADO';
            actionButtons = `
                <div class="order-actions">
                    <button class="btn btn-secondary" onclick="openInvoiceModal(${order.idSale})">Ver Factura</button>
                    <button class="btn btn-outline" onclick="verDetallesVenta(${order.idSale})">Ver Productos</button>
                </div>
            `;
        }

        const reportHtml = order.statusReport ? `
            <div class="order-report">
                <strong>Reporte de Estado:</strong><br>
                ${window.escapeHTML(order.statusReport)}
            </div>
        ` : '';

        return `
            <div class="order-card">
                <div class="order-header">
                    <div>
                        <div class="order-id">Pedido #${order.idSale}</div>
                        <div class="order-date">${date}</div>
                    </div>
                    <div class="order-status status-${order.status}">
                        ${statusText}
                    </div>
                </div>

                <div class="order-details">
                    <div>
                        <p style="color: var(--text-secondary); margin-bottom: 0.5rem;">Total de Productos: ${order.details ? order.details.length : 0}</p>
                    </div>
                    <div class="order-total">
                        $${order.total.toLocaleString()}
                    </div>
                </div>

                ${reportHtml}
                ${actionButtons}
            </div>
        `;
    }).join('');
}

function goToCheckout(saleId) {
    localStorage.setItem('pendingSaleId', saleId);
    window.location.href = 'checkout.html';
}

function openReportModal(saleId, action) {
    pendingAction = { saleId, action };

    document.getElementById('reportModalTitle').textContent = 'Cancelar Pedido';
    document.getElementById('reportModalDesc').textContent = 'Por favor, ingresa el motivo de la cancelación (opcional): ';

    document.getElementById('statusReportText').value = '';

    const confirmBtn = document.getElementById('confirmActionBtn');
    confirmBtn.onclick = executePendingAction;

    document.getElementById('reportModal').style.display = 'block';
}

function closeReportModal() {
    document.getElementById('reportModal').style.display = 'none';
    pendingAction = null;
}

async function executePendingAction() {
    if (!pendingAction) return;

    const { saleId, action } = pendingAction;
    const reportText = document.getElementById('statusReportText').value;

    const endpoint = action === 'pay' ? `/sales/${saleId}/pay` : `/sales/${saleId}/cancel`;
    const btn = document.getElementById('confirmActionBtn');
    const originalText = btn.textContent;

    try {
        btn.disabled = true;
        btn.textContent = 'Procesando...';

        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ statusReport: reportText })
        });

        if (!response.ok) {
            const err = await response.json().catch(()=>({}));
            throw new Error(err.message || 'Error al procesar la acción');
        }

        closeReportModal();
        await loadOrders();

    } catch (error) {
        console.error(error);
        alert('Error: ' + error.message);
    } finally {
        btn.disabled = false;
        btn.textContent = originalText;
    }
}

function openInvoiceModal(saleId) {
    const order = currentOrders.find(o => o.idSale === saleId);
    if (!order) return;

    let invoiceHtml = `
        <div style="border-bottom: 1px solid var(--border-color); padding-bottom: 1rem; margin-bottom: 1rem;">
            <strong>No. Factura / Pedido:</strong> #${order.idSale} <br>
            <strong>Fecha de Venta:</strong> ${new Date(order.saleDate).toLocaleString()} <br>
            <strong>Estado de Venta:</strong> ${order.status} <br>
            <strong>Dirección de Envío:</strong> ${order.shippingAddress || 'No especificada'}
        </div>
    `;

    if (order.payment) {
        invoiceHtml += `
            <div style="border-bottom: 1px solid var(--border-color); padding-bottom: 1rem; margin-bottom: 1rem;">
                <h4 style="margin-bottom: 0.5rem; color: var(--primary);">Detalles de Pago</h4>
                <strong>ID de Pago:</strong> #${order.payment.idPayment} <br>
                <strong>Método:</strong> ${order.payment.paymentMethod} <br>
                <strong>Fecha de Pago:</strong> ${new Date(order.payment.paymentDate).toLocaleString()} <br>
                <strong>Monto Pagado:</strong> $${order.payment.amount.toLocaleString()} <br>
                <strong>Estado de Pago:</strong> ${order.payment.status}
            </div>
        `;
    } else {
        invoiceHtml += `
            <div style="border-bottom: 1px solid var(--border-color); padding-bottom: 1rem; margin-bottom: 1rem;">
                <h4 style="margin-bottom: 0.5rem; color: var(--warning);">Detalles de Pago</h4>
                <p>No se encontró registro de pago en el sistema.</p>
            </div>
        `;
    }

    if (order.details && order.details.length > 0) {
        invoiceHtml += `
            <div>
                <h4 style="margin-bottom: 0.5rem;">Productos Comprados</h4>
                <table style="width: 100%; text-align: left; border-collapse: collapse;">
                    <thead>
                        <tr style="border-bottom: 1px solid var(--border-color);">
                            <th style="padding: 0.5rem;">Producto</th>
                            <th style="padding: 0.5rem;">Cant.</th>
                            <th style="padding: 0.5rem;">Precio Unit.</th>
                            <th style="padding: 0.5rem;">Subtotal</th>
                        </tr>
                    </thead>
                    <tbody>
        `;
        order.details.forEach(detail => {
            const variantDetails = [];
            if (detail.color && detail.color !== 'N/A') variantDetails.push(`Color: ${detail.color}`);
            if (detail.size && detail.size !== 'N/A') variantDetails.push(`Talla: ${detail.size}`);
            const variantStr = variantDetails.length > 0 ? ` <br><small style="color: var(--text-secondary);">(${variantDetails.join(', ')})</small>` : '';

            let priceHtml = '';
            if (detail.discountPercentage && detail.discountPercentage > 0) {
                priceHtml = `
                    <span style="text-decoration: line-through; color: var(--text-secondary); font-size: 0.85em; display: block;">$${(detail.originalPrice || 0).toLocaleString()}</span>
                    <span style="color: var(--primary); font-weight: bold;">$${(detail.unitPrice || 0).toLocaleString()} (-${detail.discountPercentage}%)</span>
                `;
            } else {
                priceHtml = `$${(detail.unitPrice || 0).toLocaleString()}`;
            }

            invoiceHtml += `
                <tr style="border-bottom: 1px solid var(--border-color); border-bottom-style: dashed;">
                    <td style="padding: 0.5rem;">
                        ${detail.productName || 'Producto Eliminado'}
                        ${variantStr}
                    </td>
                    <td style="padding: 0.5rem;">${detail.quantity}</td>
                    <td style="padding: 0.5rem;">${priceHtml}</td>
                    <td style="padding: 0.5rem;">$${((detail.unitPrice || 0) * detail.quantity).toLocaleString()}</td>
                </tr>
            `;
        });
        invoiceHtml += `
                    </tbody>
                </table>
                <div style="text-align: right; margin-top: 1rem; font-size: 1.1rem;">
                    <strong>Subtotal: $${order.subtotal.toLocaleString()}</strong><br>
                    ${order.subtotal > order.total ? `<strong style="color: var(--primary);">Descuento Aplicado: -$${(order.subtotal - order.total).toLocaleString()}</strong><br>` : ''}
                    <strong>Total a Pagar: $${order.total.toLocaleString()}</strong>
                </div>
            </div>
        `;
    }

    document.getElementById('invoiceContent').innerHTML = invoiceHtml;
    document.getElementById('invoiceModal').style.display = 'block';
}

function closeInvoiceModal() {
    document.getElementById('invoiceModal').style.display = 'none';
}

function printInvoice() {
    const originalTitle = document.title;
    document.title = "Factura_" + new Date().getTime();
    window.print();
    document.title = originalTitle;
}

window.verDetallesVenta = function(saleId) {
    const order = currentOrders.find(o => o.idSale === saleId);
    if (!order) return;
    const details = order.details || [];
    const tbody = document.getElementById('bodyDetallesVenta');
    if (details.length === 0) {
        tbody.innerHTML = '<tr><td colspan="3" style="text-align:center">Sin productos</td></tr>';
    } else {
        tbody.innerHTML = details.map(d => {
            let priceHtml = '';
            if (d.discountPercentage && d.discountPercentage > 0) {
                priceHtml = `
                    <span style="text-decoration: line-through; color: var(--text-secondary); font-size: 0.85em; display: block;">$${parseFloat(d.originalPrice || 0).toFixed(2)}</span>
                    <span style="color: var(--primary); font-weight: bold;">$${parseFloat(d.unitPrice || 0).toFixed(2)}</span>
                `;
            } else {
                priceHtml = `$${parseFloat(d.unitPrice || 0).toFixed(2)}`;
            }

            return `
            <tr style="border-bottom: 1px solid var(--border-color);">
                <td style="padding: 0.5rem;">
                    <div style="font-weight: 500;">${d.productName || 'Producto'}</div>
                    <div style="margin-top: 4px; display: flex; gap: 4px;">
                        <span class="badge" style="font-size: 0.75rem; padding: 2px 6px; border: 1px solid var(--border-color); background: var(--surface-muted);">${d.color || 'Sin Color'}</span>
                        <span class="badge" style="font-size: 0.75rem; padding: 2px 6px; border: 1px solid var(--border-color); background: var(--surface-muted);">${d.size || 'Sin Talla'}</span>
                    </div>
                </td>
                <td style="padding: 0.5rem;">${d.quantity}</td>
                <td style="padding: 0.5rem;">${priceHtml}</td>
                <td style="padding: 0.5rem;">$${parseFloat(d.totalPrice).toFixed(2)}</td>
            </tr>
            `;
        }).join('');

        // Add summary row
        tbody.innerHTML += `
            <tr style="background: var(--surface-muted);">
                <td colspan="3" style="padding: 0.5rem; text-align: right; font-weight: bold;">Subtotal:</td>
                <td style="padding: 0.5rem; font-weight: bold;">$${parseFloat(order.subtotal).toFixed(2)}</td>
            </tr>
            ${order.subtotal > order.total ? `
            <tr style="background: var(--surface-muted); color: var(--primary);">
                <td colspan="3" style="padding: 0.5rem; text-align: right; font-weight: bold;">Descuento:</td>
                <td style="padding: 0.5rem; font-weight: bold;">-$${parseFloat(order.subtotal - order.total).toFixed(2)}</td>
            </tr>` : ''}
            <tr style="background: var(--surface); border-top: 2px solid var(--border-color);">
                <td colspan="3" style="padding: 0.5rem; text-align: right; font-weight: bold; font-size: 1.1em;">Total Final:</td>
                <td style="padding: 0.5rem; font-weight: bold; font-size: 1.1em;">$${parseFloat(order.total).toFixed(2)}</td>
            </tr>
        `;
    }
    document.getElementById('modalDetallesVentaSaleId').textContent = saleId;
    document.getElementById('modalDetallesVenta').style.display = 'block';
};
