// API Configuration
var API_BASE_URL = 'http://localhost:8080/api';
var CART_API = `${API_BASE_URL}/cart`;
var STORAGE_KEY = 'localCart';

// Cart state
let currentCart = null;
let currentEditingItemId = null;
let pendingRemovalItemId = null;
let authToken = localStorage.getItem('authToken');
let isAuthenticated = !!authToken;

// Initialize cart on page load
document.addEventListener('DOMContentLoaded', () => {
    loadCart();
    initializeSidebar();
});

async function loadCart() {
    try {
        if (isAuthenticated) {
            await loadCartFromAPI();
        } else {
            loadCartFromLocalStorage();
        }
    } catch (error) {
        console.error('Error cargando carrito:', error);
        showNotification('Error al cargar el carrito', 'error');
    }
}

function mapBackendCart(data) {
    if (!data) return { cartItems: [] };
    console.log("Raw cart data from backend:", data);
    return {
        cartItems: (data.items || []).map(backendItem => {
            const variant = backendItem.productVariant;
            const product = variant?.product;
            const imageUrl = variant?.imageUrl || product?.imageUrl || product?.image || 'productos/default.jpg';
            
            return {
                idCartItem: backendItem.itemCartId,
                productVariantId: variant?.variantId,
                productName: product?.name || 'Producto',
                quantity: backendItem.quantity,
                price: product?.price || 0,
                productImage: imageUrl,
                color: variant?.color?.name,
                size: variant?.size?.name
            };
        })
    };
}

async function loadCartFromAPI() {
    try {
        const response = await fetch(`${CART_API}/getCart`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            if (response.status === 401) {
                localStorage.removeItem('authToken');
                isAuthenticated = false;
                authToken = null;
                loadCartFromLocalStorage();
                return;
            }
            throw new Error('Error al cargar el carrito');
        }

        const data = await response.json();
        currentCart = mapBackendCart(data);
        renderCart();
    } catch (error) {
        console.error('Error cargando carrito desde API:', error);
        throw error;
    }
}

function loadCartFromLocalStorage() {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved) {
        try {
            currentCart = JSON.parse(saved);
        } catch (e) {
            console.error('Error parsing saved cart:', e);
            currentCart = { cartItems: [] };
        }
    } else {
        currentCart = { cartItems: [] };
    }
    renderCart();
}

// Save cart to localStorage
function saveCartToLocalStorage() {
    if (currentCart) {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(currentCart));
    }
}

// Render cart items
function renderCart() {
    const container = document.getElementById('cartItemsContainer');
    const emptyMessage = document.getElementById('emptyCartMessage');

    if (!currentCart || !currentCart.cartItems || currentCart.cartItems.length === 0) {
        container.style.display = 'none';
        emptyMessage.style.display = 'block';
        updateCartSummary(0, 0);
        return;
    }

    container.style.display = 'grid';
    emptyMessage.style.display = 'none';

    container.innerHTML = currentCart.cartItems.map(item => createCartItemHTML(item)).join('');
    updateCartSummary();
}

// Create HTML for a single cart item
function createCartItemHTML(item) {
    const itemTotal = item.quantity * item.price;
    const imageUrl = item.productImage || 'img/placeholder.png';

    return `
        <div class="cart-item" data-item-id="${item.idCartItem}">
            <div class="item-image">
                <img src="${imageUrl}" alt="${item.productName}" 
                     onerror="this.src='img/placeholder.png'">
            </div>
            
            <div class="item-details">
                <h3>${item.productName}</h3>
                <div class="item-specs">
                    ${item.color ? `<span class="spec">🎨 ${item.color}</span>` : ''}
                    ${item.size ? `<span class="spec">📏 ${item.size}</span>` : ''}
                </div>
            </div>

            <div class="item-quantity">
                <div class="quantity-control">
                    <button class="qty-btn" onclick="decreaseItemQty(${item.idCartItem})">−</button>
                    <span class="qty-display">${item.quantity}</span>
                    <button class="qty-btn" onclick="increaseItemQty(${item.idCartItem})">+</button>
                </div>
                <button class="btn-edit" onclick="openEditQuantityModal(${item.idCartItem}, ${item.quantity})" 
                        title="Editar cantidad">✎</button>
            </div>

            <div class="item-price">
                <div class="price-breakdown">
                    <span class="unit-price">$${item.price.toFixed(2)}</span>
                    <span class="item-total">Total: $${itemTotal.toFixed(2)}</span>
                </div>
            </div>

            <div class="item-actions">
                <button class="btn-remove" onclick="openRemoveConfirm(${item.idCartItem}, '${item.productName}')" 
                        title="Eliminar del carrito">🗑️</button>
            </div>
        </div>
    `;
}

// Update cart summary
function updateCartSummary() {
    if (!currentCart || !currentCart.cartItems) {
        document.getElementById('subtotal').textContent = '$0.00';
        document.getElementById('total').textContent = '$0.00';
        return;
    }

    let subtotal = 0;
    currentCart.cartItems.forEach(item => {
        subtotal += item.quantity * item.price;
    });

    const total = subtotal;

    document.getElementById('subtotal').textContent = `$${subtotal.toFixed(2)}`;
    document.getElementById('total').textContent = `$${total.toFixed(2)}`;

    // Update cart counter in other pages
    updateCartCounterInOtherPages();
}

// Increase item quantity
async function increaseItemQty(itemId) {
    const item = currentCart.cartItems.find(i => i.idCartItem === itemId);
    if (item && item.quantity < 99) {
        await updateItemQuantity(itemId, item.quantity + 1);
    }
}

// Decrease item quantity
async function decreaseItemQty(itemId) {
    const item = currentCart.cartItems.find(i => i.idCartItem === itemId);
    if (item && item.quantity > 1) {
        await updateItemQuantity(itemId, item.quantity - 1);
    } else if (item && item.quantity === 1) {
        openRemoveConfirm(itemId, item.productName);
    }
}

// Open quantity edit modal
function openEditQuantityModal(itemId, currentQty) {
    currentEditingItemId = itemId;
    document.getElementById('modalQuantity').value = currentQty;
    document.getElementById('quantityModal').style.display = 'block';
    document.getElementById('modalQuantity').focus();
}

// Quantity modal controls
function increaseQty() {
    const input = document.getElementById('modalQuantity');
    if (parseInt(input.value) < 99) {
        input.value = parseInt(input.value) + 1;
    }
}

function decreaseQty() {
    const input = document.getElementById('modalQuantity');
    if (parseInt(input.value) > 1) {
        input.value = parseInt(input.value) - 1;
    }
}

// Update quantity from modal
async function updateQuantity() {
    const newQty = parseInt(document.getElementById('modalQuantity').value);
    if (newQty > 0 && newQty < 100) {
        await updateItemQuantity(currentEditingItemId, newQty);
        closeModal('quantityModal');
    }
}

// Update item quantity via API or localStorage
async function updateItemQuantity(itemId, quantity) {
    try {
        if (isAuthenticated) {
            // Authenticated user - use API
            const response = await fetch(`${CART_API}/updateItemQuantity?itemCartId=${itemId}&quantity=${quantity}`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${authToken}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) throw new Error('Error actualizando cantidad');

            const data = await response.json();
            currentCart = mapBackendCart(data);
        } else {
            // Unauthenticated user - use localStorage
            const item = currentCart.cartItems.find(i => i.idCartItem === itemId);
            if (item) {
                item.quantity = quantity;
                saveCartToLocalStorage();
            }
        }
        
        renderCart();
        showNotification('✓ Cantidad actualizada', 'success');
    } catch (error) {
        console.error('Error:', error);
        showNotification('Error al actualizar cantidad', 'error');
    }
}

// Open remove confirmation modal
function openRemoveConfirm(itemId, productName) {
    pendingRemovalItemId = itemId;
    document.getElementById('confirmMessage').textContent = 
        `¿Estás seguro de que deseas eliminar "${productName}" de tu carrito?`;
    document.getElementById('confirmModal').style.display = 'block';
}

// Confirm and remove item
async function confirmRemove() {
    if (pendingRemovalItemId) {
        await removeFromCart(pendingRemovalItemId);
        closeModal('confirmModal');
    }
}

// Remove item from cart via API or localStorage
async function removeFromCart(itemId) {
    try {
        if (isAuthenticated) {
            // Authenticated user - use API
            const response = await fetch(`${CART_API}/removeItemFromCart?itemCartId=${itemId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${authToken}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) throw new Error('Error eliminando artículo');

            const data = await response.json();
            currentCart = mapBackendCart(data);
        } else {
            // Unauthenticated user - use localStorage
            currentCart.cartItems = currentCart.cartItems.filter(item => item.idCartItem !== itemId);
            saveCartToLocalStorage();
        }

        renderCart();
        showNotification('✓ Artículo eliminado', 'success');
    } catch (error) {
        console.error('Error:', error);
        showNotification('Error al eliminar artículo', 'error');
    }
}

// Apply promo code
function applyPromoCode() {
    const code = document.getElementById('promoCode').value.trim().toUpperCase();
    const messageDiv = document.getElementById('promoMessage');

    if (!code) {
        showNotification('Por favor ingresa un código', 'warning');
        return;
    }

    // Simple promo codes for demo
    const promoCodes = {
        'DESCUENTO10': 0.10,
        'DESCUENTO20': 0.20,
        'ENVIOGRATIS': 0
    };

    if (promoCodes.hasOwnProperty(code)) {
        messageDiv.style.display = 'block';
        messageDiv.className = 'promo-success';
        messageDiv.textContent = `✓ Código "${code}" aplicado correctamente`;
        showNotification('✓ Código de descuento aplicado', 'success');
    } else {
        messageDiv.style.display = 'block';
        messageDiv.className = 'promo-error';
        messageDiv.textContent = `✗ Código "${code}" no válido`;
        showNotification('✗ Código no válido', 'error');
    }
}

// Proceed to checkout
async function proceedToCheckout() {
    if (!isAuthenticated) {
        // Save current cart state and redirect
        saveCartToLocalStorage();
        window.location.href = 'login.html?redirect=cart';
        return;
    }

    if (!currentCart || !currentCart.cartItems || currentCart.cartItems.length === 0) {
        showNotification('Tu carrito está vacío', 'error');
        return;
    }

    const checkoutBtn = document.getElementById('checkoutBtn');
    if (checkoutBtn) {
        checkoutBtn.disabled = true;
        checkoutBtn.textContent = 'Procesando...';
    }

    try {
        const response = await fetch(`${API_BASE_URL}/sales/checkout`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            const errData = await response.json().catch(() => ({}));
            console.error("Backend error response:", errData);
            throw new Error(errData.message || 'Error al generar la orden');
        }

        const saleData = await response.json();
        
        // Redirigir a checkout.html pasando el ID de la orden en localStorage
        localStorage.setItem('pendingSaleId', saleData.idSale || saleData.id || saleData.saleId || saleData.saleNumber || '');
        saveCartToLocalStorage(); 
        
        window.location.href = 'checkout.html';
    } catch (error) {
        console.error('Error procesando checkout:', error);
        showNotification('Error al iniciar el pago', 'error');
        if (checkoutBtn) {
            checkoutBtn.disabled = false;
            checkoutBtn.textContent = 'Proceder al pago';
        }
    }
}

// Modal management
function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

window.onclick = function(event) {
    const modals = document.querySelectorAll('.modal');
    modals.forEach(modal => {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });
}

// Notification system
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    document.body.appendChild(notification);

    setTimeout(() => {
        notification.classList.add('show');
    }, 10);

    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// Initialize sidebar
function initializeSidebar() {
    const sidebar = document.getElementById('sidebar');
    if (sidebar) {
        sidebar.innerHTML = `
            <div id="sidebarContent"></div>
        `;
        // Sidebar manager will handle the rest
        if (typeof loadSidebar === 'function') {
            loadSidebar();
        }
    }
}

// Update cart counter in other pages (for consistency)
function updateCartCounterInOtherPages() {
    if (currentCart && currentCart.cartItems) {
        localStorage.setItem('cartItemCount', currentCart.cartItems.length);
    }
}
