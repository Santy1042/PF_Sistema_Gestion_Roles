// Add to Cart functionality - Works for authenticated and unauthenticated users
var API_BASE_URL = 'http://localhost:8080/api';
var CART_API = `${API_BASE_URL}/cart`;
var STORAGE_KEY = 'localCart';

// Add product to cart
async function addToCart(productVariantId, productName, quantity = 1, productData = null) {
    try {
        const authToken = localStorage.getItem('authToken');

        if (authToken) {
            // Authenticated user - use API
            await addToCartAPI(productVariantId, quantity, authToken);
        } else {
            // Unauthenticated user - use localStorage
            addToCartLocalStorage(productVariantId, quantity, productName, productData);
        }

        showNotification(`✓ "${productName}" agregado al carrito`, 'success');
        updateAllCartCounters();
    } catch (error) {
        console.error('Error adding to cart:', error);
        showNotification('Error al agregar producto al carrito', 'error');
    }
}

// Add to cart via API (authenticated)
async function addToCartAPI(productVariantId, quantity, authToken) {
    const response = await fetch(
        `${CART_API}/addItemToCart?productVariantId=${productVariantId}&quantity=${quantity}`,
        {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            }
        }
    );

    if (!response.ok) {
        throw new Error('Error adding to cart via API');
    }
}

// Add to cart to localStorage (unauthenticated)
function addToCartLocalStorage(productVariantId, quantity, productName, productData = null) {
    let cart = getLocalCart();

    // Check if item already exists
    const existingItem = cart.cartItems.find(item => item.idCartItem === productVariantId || item.productVariantId === productVariantId);

    if (existingItem) {
        // Increase quantity
        existingItem.quantity += quantity;
    } else {
        // Add new item (use mock data if productData not provided)
        const newItem = {
            idCartItem: productVariantId,
            productVariantId: productVariantId,
            productName: productName,
            quantity: quantity,
            price: productData?.price || 29.99,
            productImage: productData?.image || 'img/placeholder.png',
            color: productData?.color || null,
            size: productData?.size || null,
            productVariantDescription: productData?.description || ''
        };

        cart.cartItems.push(newItem);
    }

    localStorage.setItem(STORAGE_KEY, JSON.stringify(cart));
}

// Get cart from localStorage
function getLocalCart() {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved) {
        try {
            return JSON.parse(saved);
        } catch (e) {
            console.error('Error parsing saved cart:', e);
            return { cartItems: [] };
        }
    }
    return { cartItems: [] };
}

// Update all cart counters on the page
function updateAllCartCounters() {
    // Trigger storage event to update other tabs
    window.dispatchEvent(new Event('storage'));
    
    // Update counter on current page if it exists
    const cartCountElement = document.getElementById('cartCount');
    if (cartCountElement) {
        const authToken = localStorage.getItem('authToken');
        if (!authToken) {
            const cart = getLocalCart();
            const count = cart.cartItems ? cart.cartItems.length : 0;
            if (count > 0) {
                cartCountElement.textContent = count;
                cartCountElement.style.display = 'flex';
            }
        }
    }
}

// Show notification
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
