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

        if (response.status === 403) {
          throw new Error('INACTIVO_403');
        }

        if (!response.ok) {
          const errData = await response.json().catch(() => ({}));
          const msg = errData.message || 'Credenciales incorrectas o error en el servidor';
          throw new Error(msg);
        }

        const data = await response.json();
        localStorage.setItem('authToken', data.token);

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

        sessionStorage.setItem('flashMessage', '¡Sesión iniciada correctamente!');
        window.location.href = 'index.html';
      } catch (error) {
        console.error('Error:', error);
        let errorMsg = error.message;
        if (errorMsg === 'INACTIVO_403' || errorMsg.toLowerCase().includes('inactivo') || errorMsg.toLowerCase().includes('forbidden')) {
          document.getElementById('inactiveModal').style.display = 'block';
        } else {
          if (errorMsg === 'Credenciales incorrectas o error en el servidor') {
            errorMsg = 'Credenciales incorrectas';
          }

          if (window.showToast) {
            showToast(errorMsg, 'error');
          } else {
            alert(errorMsg);
          }
        }
        submitBtn.disabled = false;
        submitBtn.textContent = 'Entrar';
      }
    });
  }
});
