var CART_API = `${API_BASE_URL}/cart`;

async function addToCart(productVariantId, productName, quantity = 1, productData = null) {
    try {
        const authToken = localStorage.getItem('authToken');

        if (authToken) {

            await addToCartAPI(productVariantId, quantity, authToken);
        } else {

            addToCartLocalStorage(productVariantId, quantity, productName, productData);
        }

        showNotification(`✓ "${productName}" agregado al carrito`, 'success');
        updateAllCartCounters();
    } catch (error) {
        console.error('Error adding to cart:', error);
        showNotification('Error al agregar producto al carrito', 'error');
    }
}

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

function addToCartLocalStorage(productVariantId, quantity, productName, productData = null) {
    let cart = getLocalCart();

    const existingItem = cart.cartItems.find(item => item.idCartItem === productVariantId || item.productVariantId === productVariantId);

    if (existingItem) {

        existingItem.quantity += quantity;
    } else {

        const newItem = {
            idCartItem: productVariantId,
            productVariantId: productVariantId,
            productName: productName,
            quantity: quantity,
            price: productData?.price || 29.99,
            originalPrice: productData?.originalPrice || productData?.price || 29.99,
            discountPercentage: productData?.discountPercentage || 0,
            productImage: productData?.image || 'img/default_product.png',
            color: productData?.color || null,
            size: productData?.size || null,
            productVariantDescription: productData?.description || ''
        };

        cart.cartItems.push(newItem);
    }

    localStorage.setItem(STORAGE_KEY, JSON.stringify(cart));
}

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

function updateAllCartCounters() {

    window.dispatchEvent(new Event('storage'));

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
