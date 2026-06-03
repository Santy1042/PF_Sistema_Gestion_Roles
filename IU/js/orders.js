// orders.js
const API_BASE_URL = 'http://localhost:8080/api';
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

    // Ordenar de más reciente a más antiguo
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
                </div>
            `;
        } else if (order.status === 'PAID') {
            statusText = 'PAGADO';
            actionButtons = `
                <div class="order-actions">
                    <button class="btn btn-secondary" onclick="openInvoiceModal(${order.idSale})">Ver Factura</button>
                </div>
            `;
        } else if (order.status === 'CANCELLED') {
            statusText = 'CANCELADO';
        } else if (order.status === 'REFUNDED') {
            statusText = 'REEMBOLSADO';
            actionButtons = `
                <div class="order-actions">
                    <button class="btn btn-secondary" onclick="openInvoiceModal(${order.idSale})">Ver Factura</button>
                </div>
            `;
        }

        const reportHtml = order.statusReport ? `
            <div class="order-report">
                <strong>Reporte de Estado:</strong><br>
                ${order.statusReport}
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

        // Action successful, reload orders
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

            invoiceHtml += `
                <tr style="border-bottom: 1px solid var(--border-color); border-bottom-style: dashed;">
                    <td style="padding: 0.5rem;">
                        ${detail.productName || 'Producto Eliminado'}
                        ${variantStr}
                    </td>
                    <td style="padding: 0.5rem;">${detail.quantity}</td>
                    <td style="padding: 0.5rem;">$${detail.unitPrice.toLocaleString()}</td>
                    <td style="padding: 0.5rem;">$${(detail.unitPrice * detail.quantity).toLocaleString()}</td>
                </tr>
            `;
        });
        invoiceHtml += `
                    </tbody>
                </table>
                <div style="text-align: right; margin-top: 1rem; font-size: 1.1rem;">
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
