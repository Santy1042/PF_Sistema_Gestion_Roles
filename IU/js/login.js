var API_BASE_URL = 'http://localhost:8080/api';

document.addEventListener('DOMContentLoaded', () => {
  const loginForm = document.getElementById('loginForm');
  if (loginForm) {
    loginForm.addEventListener('submit', async function(e) {
      e.preventDefault();
      
      const email = document.getElementById('email').value;
      const password = document.getElementById('password').value;
      const submitBtn = document.querySelector('.form-submit');
      
      if (!email || !password) {
        if (window.showToast) showToast('Por favor rellena todos los campos', 'error');
        else alert('Por favor rellena todos los campos');
        return;
      }
      
      try {
        submitBtn.disabled = true;
        submitBtn.textContent = 'Iniciando sesión...';

        const response = await fetch(`${API_BASE_URL}/auth/login`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ email, password })
        });

        if (!response.ok) {
          throw new Error('Credenciales incorrectas o error en el servidor');
        }

        const data = await response.json();
        localStorage.setItem('authToken', data.token);

        // Sincronizar carrito local si existe
        const localCartStr = localStorage.getItem('localCart');
        if (localCartStr) {
          try {
            const localCart = JSON.parse(localCartStr);
            if (localCart && localCart.cartItems && localCart.cartItems.length > 0) {
              const syncItems = localCart.cartItems.map(item => ({
                productVariantId: item.productVariantId || item.idCartItem, // idCartItem was used in addToCartLocalStorage
                quantity: item.quantity
              }));
              await fetch(`${API_BASE_URL}/cart/syncCart`, {
                method: 'POST',
                headers: {
                  'Content-Type': 'application/json',
                  'Authorization': `Bearer ${data.token}`
                },
                body: JSON.stringify(syncItems)
              });
              localStorage.removeItem('localCart');
            }
          } catch (err) {
            console.error('Error syncing cart:', err);
          }
        }
        
        // Show success message and redirect
        sessionStorage.setItem('flashMessage', '¡Sesión iniciada correctamente!');
        window.location.href = 'index.html';
      } catch (error) {
        console.error('Error:', error);
        if (window.showToast) {
          showToast('Credenciales incorrectas', 'error');
        } else {
          alert('Credenciales incorrectas');
        }
        submitBtn.disabled = false;
        submitBtn.textContent = 'Entrar';
      }
    });
  }
});


