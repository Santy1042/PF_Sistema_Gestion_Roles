// API Configuration
var API_BASE_URL = 'http://localhost:8080/api';
var SALES_API = `${API_BASE_URL}/sale`;

// State
let checkoutCart = null;
let authToken = localStorage.getItem('authToken');

// Initialize checkout on page load
document.addEventListener('DOMContentLoaded', () => {
    initializeCheckout();
});

// Initialize checkout form
function initializeCheckout() {
    if (!authToken) {
        window.location.href = 'login.html';
        return;
    }

    // Load cart from localStorage
    const savedCart = localStorage.getItem('checkoutCart');
    if (!savedCart) {
        window.location.href = 'cart.html';
        return;
    }

    checkoutCart = JSON.parse(savedCart);
    loadUserData();
    renderOrderSummary();
    setupFormHandlers();
}

// Load user data from localStorage or profile
function loadUserData() {
    const userInfo = localStorage.getItem('userInfo');
    if (userInfo) {
        const user = JSON.parse(userInfo);
        document.getElementById('email').value = user.email || '';
        document.getElementById('firstName').value = user.firstName || '';
        document.getElementById('lastName').value = user.lastName || '';
        document.getElementById('phone').value = user.phone || '';
    }
}

// Render order summary
function renderOrderSummary() {
    if (!checkoutCart || !checkoutCart.cartItems) return;

    const itemsHTML = checkoutCart.cartItems.map(item => `
        <div class="order-item">
            <div class="item-info">
                <span class="item-name">${item.productName}</span>
                <span class="item-qty">x${item.quantity}</span>
            </div>
            <span class="item-price">$${(item.quantity * item.price).toFixed(2)}</span>
        </div>
    `).join('');

    document.getElementById('orderItems').innerHTML = itemsHTML;
    document.getElementById('mobileSummary').innerHTML = itemsHTML;

    updateTotals();
}

// Update order totals
function updateTotals() {
    let subtotal = 0;
    checkoutCart.cartItems.forEach(item => {
        subtotal += item.quantity * item.price;
    });

    const shippingMethod = document.querySelector('input[name="shipping"]:checked').value;
    let shippingCost = 0;
    let shippingLabel = 'Gratis';

    if (shippingMethod === 'express') {
        shippingCost = 10;
        shippingLabel = '$10.00';
    } else if (shippingMethod === 'overnight') {
        shippingCost = 25;
        shippingLabel = '$25.00';
    }

    const total = subtotal + shippingCost;

    document.getElementById('summarySubtotal').textContent = `$${subtotal.toFixed(2)}`;
    document.getElementById('summaryShipping').textContent = shippingLabel;
    document.getElementById('summaryTotal').textContent = `$${total.toFixed(2)}`;
}

// Setup form handlers
function setupFormHandlers() {
    // Shipping method change
    document.querySelectorAll('input[name="shipping"]').forEach(radio => {
        radio.addEventListener('change', updateTotals);
    });

    // Payment method change
    document.querySelectorAll('input[name="payment"]').forEach(radio => {
        radio.addEventListener('change', (e) => {
            document.getElementById('cardPaymentForm').style.display = 
                e.target.value === 'card' ? 'block' : 'none';
        });
    });

    // Card number formatting
    const cardNumberInput = document.getElementById('cardNumber');
    if (cardNumberInput) {
        cardNumberInput.addEventListener('input', (e) => {
            let value = e.target.value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
            let formattedValue = value.replace(/(\d{4})(?=\d)/g, '$1 ');
            e.target.value = formattedValue;
        });
    }

    // Expiry formatting
    const expiryInput = document.getElementById('expiry');
    if (expiryInput) {
        expiryInput.addEventListener('input', (e) => {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length >= 2) {
                value = value.slice(0, 2) + '/' + value.slice(2, 4);
            }
            e.target.value = value;
        });
    }

    // CVV formatting
    const cvvInput = document.getElementById('cvv');
    if (cvvInput) {
        cvvInput.addEventListener('input', (e) => {
            e.target.value = e.target.value.replace(/\D/g, '').slice(0, 4);
        });
    }
}

// Validate shipping form
function validateShippingForm() {
    const requiredFields = [
        'firstName', 'lastName', 'email', 'phone', 
        'address', 'city', 'state', 'zipCode', 'country'
    ];

    for (const field of requiredFields) {
        const value = document.getElementById(field).value.trim();
        if (!value) {
            showNotification(`Por favor completa el campo: ${field}`, 'error');
            return false;
        }
    }

    // Validate email
    const email = document.getElementById('email').value;
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        showNotification('Email inválido', 'error');
        return false;
    }

    // Validate phone
    const phone = document.getElementById('phone').value;
    if (!/^\+?[\d\s\-()]{10,}$/.test(phone)) {
        showNotification('Teléfono inválido', 'error');
        return false;
    }

    return true;
}

// Validate card form
function validateCardForm() {
    const paymentMethod = document.querySelector('input[name="payment"]:checked').value;

    if (paymentMethod !== 'card') {
        return true; // Other methods don't need validation here
    }

    const cardName = document.getElementById('cardName').value.trim();
    const cardNumber = document.getElementById('cardNumber').value.replace(/\s/g, '');
    const expiry = document.getElementById('expiry').value;
    const cvv = document.getElementById('cvv').value;

    if (!cardName) {
        showNotification('Nombre en la tarjeta es requerido', 'error');
        return false;
    }

    if (!/^\d{16}$/.test(cardNumber)) {
        showNotification('Número de tarjeta inválido (16 dígitos)', 'error');
        return false;
    }

    if (!/^\d{2}\/\d{2}$/.test(expiry)) {
        showNotification('Fecha de vencimiento inválida (MM/AA)', 'error');
        return false;
    }

    if (!/^\d{3,4}$/.test(cvv)) {
        showNotification('CVV inválido', 'error');
        return false;
    }

    return true;
}

// Complete order
async function completeOrder() {
    // Validate forms
    if (!validateShippingForm() || !validateCardForm()) {
        return;
    }

    try {
        // Show loading state
        const btn = document.getElementById('completeOrderBtn');
        const originalText = btn.textContent;
        btn.disabled = true;
        btn.textContent = 'Procesando...';

        // Create sale/checkout request
        const checkoutData = {
            items: checkoutCart.cartItems.map(item => ({
                productVariantId: item.productVariantId,
                quantity: item.quantity,
                price: item.price
            })),
            shippingAddress: {
                firstName: document.getElementById('firstName').value,
                lastName: document.getElementById('lastName').value,
                email: document.getElementById('email').value,
                phone: document.getElementById('phone').value,
                address: document.getElementById('address').value,
                city: document.getElementById('city').value,
                state: document.getElementById('state').value,
                zipCode: document.getElementById('zipCode').value,
                country: document.getElementById('country').value
            },
            shippingMethod: document.querySelector('input[name="shipping"]:checked').value,
            paymentMethod: document.querySelector('input[name="payment"]:checked').value
        };

        // Call API to create sale
        const response = await fetch(`${SALES_API}/createSale`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(checkoutData)
        });

        if (!response.ok) {
            throw new Error('Error al procesar la compra');
        }

        const sale = await response.json();

        // Show success modal
        document.getElementById('orderNumber').textContent = `Número de Pedido: ${sale.saleNumber || sale.id}`;
        document.getElementById('successModal').style.display = 'block';

        // Clear cart from localStorage
        localStorage.removeItem('checkoutCart');
        localStorage.removeItem('cart');

    } catch (error) {
        console.error('Error:', error);
        showNotification('Error al procesar la compra: ' + error.message, 'error');
        document.getElementById('completeOrderBtn').disabled = false;
        document.getElementById('completeOrderBtn').textContent = originalText || 'Completar Compra';
    }
}

// Modal functions
function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

function goToOrders() {
    window.location.href = 'profile.html';
}

function continueShopping() {
    window.location.href = 'products.html';
}

// Close modal on outside click
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
    }, 4000);
}
