

document.addEventListener('DOMContentLoaded', async () => {
  const profileForm = document.getElementById('profileForm');
  const token = localStorage.getItem('authToken');

  if (!token) {
    sessionStorage.setItem('flashMessage', 'Debes iniciar sesión para ver tu perfil.');
    sessionStorage.setItem('flashType', 'error');
    window.location.href = 'login.html';
    return;
  }

  try {
    const response = await fetch(`${API_BASE_URL}/profile`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.ok) {
      const profile = await response.json();
      const fullname = `${profile.firstName} ${profile.lastName}`.trim();

      document.getElementById('fullname').value = fullname;
      document.getElementById('email').value = profile.email || '';
      document.getElementById('email').disabled = true;
      document.getElementById('phone').value = profile.phoneNumber || '';
      if(document.getElementById('address')) document.getElementById('address').value = profile.address || '';

      // Populate dashboard cards
      const formatDate = (dateStr) => {
        if (!dateStr) return 'No disponible';
        const date = new Date(dateStr);
        return date.toLocaleDateString('es-ES', { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' });
      };
      
      const createdAtElem = document.getElementById('createdAtDisplay');
      if (createdAtElem) createdAtElem.textContent = formatDate(profile.createdAt);
      
      const lastAccessElem = document.getElementById('lastAccessDisplay');
      if (lastAccessElem) lastAccessElem.textContent = formatDate(profile.lastAccess);
      
      const roleElem = document.getElementById('roleDisplay');
      if (roleElem) roleElem.textContent = profile.role === 'ADMIN' ? 'Administrador' : 'Usuario';

    } else {
      console.error('Error al cargar el perfil');
      if (response.status === 401 || response.status === 403) {
        localStorage.removeItem('authToken');
        window.location.href = 'login.html';
      }
    }
  } catch (error) {
    console.error('Error de conexión:', error);
  }

  if (profileForm) {
    profileForm.addEventListener('submit', async function(e) {
      e.preventDefault();

      const submitBtn = profileForm.querySelector('button[type="submit"]');
      const fullname = document.getElementById('fullname').value.trim();
      const phone = document.getElementById('phone').value.trim();
      const address = document.getElementById('address') ? document.getElementById('address').value.trim() : '';

      const nameParts = fullname.split(' ');
      const firstName = nameParts[0];
      const lastName = nameParts.slice(1).join(' ') || '';

      try {
        submitBtn.disabled = true;
        submitBtn.textContent = 'Guardando...';

        const response = await fetch(`${API_BASE_URL}/profile`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({
            firstName: firstName,
            lastName: lastName,
            phoneNumber: phone,
            address: address
          })
        });

        if (!response.ok) {
          throw new Error('No se pudo actualizar el perfil');
        }

        const data = await response.json();
        if (window.showNotification) {
          showNotification('Tus datos se guardaron correctamente.', 'success');
        } else {
          alert('Tus datos se guardaron correctamente.');
        }
      } catch (error) {
        console.error('Error:', error);
        if (window.showNotification) {
          showNotification('Error al guardar: ' + error.message, 'error');
        } else {
          alert('Error al guardar: ' + error.message);
        }
      } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = 'Guardar cambios';
      }
    });
  }

  const deactivateBtn = document.getElementById('deactivateBtn');
  const deactivateModal = document.getElementById('deactivateModal');
  const cancelDeactivateBtn = document.getElementById('cancelDeactivateBtn');
  const confirmDeactivateBtn = document.getElementById('confirmDeactivateBtn');

  if (deactivateBtn && deactivateModal && cancelDeactivateBtn && confirmDeactivateBtn) {
    deactivateBtn.addEventListener('click', function() {
      deactivateModal.classList.add('active');
    });

    cancelDeactivateBtn.addEventListener('click', function() {
      deactivateModal.classList.remove('active');
    });

    deactivateModal.addEventListener('click', function(e) {
      if (e.target === deactivateModal) {
        deactivateModal.classList.remove('active');
      }
    });

    confirmDeactivateBtn.addEventListener('click', async function() {
      confirmDeactivateBtn.disabled = true;
      confirmDeactivateBtn.textContent = 'Procesando...';

      try {
        const response = await fetch(`${API_BASE_URL}/profile`, {
          method: 'DELETE',
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });

        if (response.ok) {
          localStorage.removeItem('authToken');
          sessionStorage.setItem('flashMessage', 'Tu cuenta ha sido dada de baja exitosamente.');
          sessionStorage.setItem('flashType', 'success');
          window.location.href = 'login.html';
        } else {
          const errorData = await response.json();
          if (window.showNotification) showNotification(errorData.message || 'Error al dar de baja la cuenta', 'error');
          else alert(errorData.message || 'Error al dar de baja la cuenta');
          deactivateModal.classList.remove('active');
        }
      } catch (error) {
        console.error('Error:', error);
        if (window.showNotification) showNotification('Error de conexión', 'error');
        deactivateModal.classList.remove('active');
      } finally {
        confirmDeactivateBtn.disabled = false;
        confirmDeactivateBtn.textContent = 'Sí, dar de baja';
      }
    });
  }

  const logoutBtn = document.getElementById('logoutBtn');
  if (logoutBtn) {
    logoutBtn.addEventListener('click', (e) => {
      e.preventDefault();
      localStorage.removeItem('authToken');
      window.location.href = 'login.html';
    });
  }
});
