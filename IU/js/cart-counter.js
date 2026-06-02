// Cart Counter - Manages cart item count display across all pages
var API_BASE_URL = 'http://localhost:8080/api';
var CART_API = `${API_BASE_URL}/cart`;
var STORAGE_KEY = 'localCart';

// Update cart counter on page load
document.addEventListener('DOMContentLoaded', () => {
    updateCartCounter();
});

// Function to update cart counter
async function updateCartCounter() {
    try {
        const authToken = localStorage.getItem('authToken');
        const cartCountElement = document.getElementById('cartCount');
        
        if (!cartCountElement) return; // Element not on this page
        
        if (!authToken) {
            // User not logged in - cart is in localStorage
            const cart = localStorage.getItem(STORAGE_KEY);
            if (cart) {
                const cartData = JSON.parse(cart);
                const count = cartData.cartItems ? cartData.cartItems.length : 0;
                displayCartCount(count);
            } else {
                displayCartCount(0);
            }
            return;
        }

        // User logged in - get cart from API
        const response = await fetch(`${CART_API}/getCart`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const cart = await response.json();
            const count = cart.cartItems ? cart.cartItems.length : 0;
            displayCartCount(count);
            // Save to localStorage for quick access
            localStorage.setItem('cartItemCount', count);
        } else {
            displayCartCount(0);
        }
    } catch (error) {
        console.error('Error updating cart counter:', error);
        displayCartCount(0);
    }
}

// Display the count on the cart button
function displayCartCount(count) {
    const cartCountElement = document.getElementById('cartCount');
    if (!cartCountElement) return;
    
    if (count > 0) {
        cartCountElement.textContent = count;
        cartCountElement.style.display = 'flex';
    } else {
        cartCountElement.style.display = 'none';
    }
}

// Listen for storage changes (cart updates in other tabs)
window.addEventListener('storage', () => {
    updateCartCounter();
});
