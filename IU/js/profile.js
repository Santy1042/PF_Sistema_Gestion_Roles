var API_BASE_URL = 'http://localhost:8080/api';

document.addEventListener('DOMContentLoaded', async () => {
  const profileForm = document.getElementById('profileForm');
  const token = localStorage.getItem('authToken');

  if (!token) {
    alert('Debes iniciar sesión para ver tu perfil.');
    window.location.href = 'login.html';
    return;
  }

  // Cargar datos del perfil
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
      document.getElementById('email').disabled = true; // El email usualmente no se cambia directamente sin validación
      document.getElementById('phone').value = profile.phoneNumber || '';
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
            phoneNumber: phone
          })
        });

        if (!response.ok) {
          throw new Error('No se pudo actualizar el perfil');
        }

        const data = await response.json();
        alert('Tus datos se guardaron correctamente.');
      } catch (error) {
        console.error('Error:', error);
        alert('Error al guardar: ' + error.message);
      } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = 'Guardar cambios';
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
