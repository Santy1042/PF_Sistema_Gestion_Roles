var API_BASE_URL = 'http://localhost:8080/api';

document.addEventListener('DOMContentLoaded', () => {
  const registerForm = document.getElementById('registerForm');
  if (registerForm) {
    registerForm.addEventListener('submit', async function(e) {
      e.preventDefault();
      
      const fullname = document.getElementById('fullname').value.trim();
      const email = document.getElementById('email').value.trim();
      const phone = document.getElementById('phone') ? document.getElementById('phone').value.trim() : '';
      const password = document.getElementById('password').value;
      const confirmPassword = document.getElementById('confirmPassword').value;
      const terms = registerForm.elements['terms'].checked;
      const submitBtn = document.querySelector('.form-submit');

      if (!fullname || !email || !password || !confirmPassword) {
        if (window.showToast) showToast('Por favor rellena todos los campos', 'error');
        else alert('Por favor rellena todos los campos');
        return;
      }

      if (password !== confirmPassword) {
        if (window.showToast) showToast('Las contraseÃ±as no coinciden', 'error');
        else alert('Las contraseÃ±as no coinciden');
        return;
      }

      if (password.length < 6) {
        if (window.showToast) showToast('La contraseÃ±a debe tener al menos 6 caracteres', 'error');
        else alert('La contraseÃ±a debe tener al menos 6 caracteres');
        return;
      }

      if (!terms) {
        if (window.showToast) showToast('Debes aceptar los tÃ©rminos de servicio', 'error');
        else alert('Debes aceptar los tÃ©rminos de servicio');
        return;
      }

      const nameParts = fullname.split(' ');
      const firstName = nameParts[0];
      const lastName = nameParts.slice(1).join(' ') || nameParts[0];

      try {
        submitBtn.disabled = true;
        submitBtn.textContent = 'Creando cuenta...';

        const response = await fetch(`${API_BASE_URL}/auth/register`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            firstName: firstName,
            lastName: lastName,
            email: email,
            phoneNumber: phone,
            password: password
          })
        });

        if (!response.ok) {
          const errorData = await response.json().catch(() => ({}));
          let errMsg = errorData.message;
          if (!errMsg && Object.keys(errorData).length > 0) {
              errMsg = Object.values(errorData).join(', ');
          }
          throw new Error(errMsg || 'Error en el registro');
        }

        const data = await response.json();
        if (data.token) {
          localStorage.setItem('authToken', data.token);

          // Sincronizar carrito local si existe
          const localCartStr = localStorage.getItem('localCart');
          if (localCartStr) {
            try {
              const localCart = JSON.parse(localCartStr);
              if (localCart && localCart.cartItems && localCart.cartItems.length > 0) {
                const syncItems = localCart.cartItems.map(item => ({
                  productVariantId: item.productVariantId || item.idCartItem,
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
        }
        
        sessionStorage.setItem('flashMessage', 'ÂCuenta creada exitosamente');
        window.location.href = 'index.html';
      } catch (error) {
        console.error('Error:', error);
        if (window.showToast) {
          showToast('Error en el registro: ' + error.message, 'error');
        } else {
          alert('Error en el registro: ' + error.message);
        }
        submitBtn.disabled = false;
        submitBtn.textContent = 'Crear Cuenta';
      }
    });

    const confirmPasswordInput = document.getElementById('confirmPassword');
    if (confirmPasswordInput) {
      confirmPasswordInput.addEventListener('blur', function() {
        if (this.value && this.value !== document.getElementById('password').value) {
          this.style.borderColor = 'var(--error)';
        } else {
          this.style.borderColor = '';
        }
      });
    }

    document.querySelectorAll('.btn.btn-secondary').forEach(btn => {
      if (btn.textContent.trim() === 'Google') {
        btn.addEventListener('click', function(e) {
          e.preventDefault();
          alert('Redirigiendo a Google para registro...');
        });
      }
    });
  }
});


