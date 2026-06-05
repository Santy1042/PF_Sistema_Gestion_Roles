(function() {
  const token = localStorage.getItem('authToken');
  const isLoggedIn = !!token;

  function renderNavbarBase() {
    const navbarHTML = `
      <header>
        <nav>
          <a href="index.html" class="logo">
            <img src="img/dark_logo.png" alt="Logo AURA Essentials" id="navLogo" style="height: 60px; width: auto; max-width: none;">
            <span>AURA ESSENTIALS</span>
          </a>

          <div class="nav-auth">
            <a href="cart.html" class="btn btn-outline cart-btn" title="Carrito de compras">🛒</a>
            <div id="auth-container-wrapper" style="display: flex; gap: 10px; align-items: center;">
              <div class="skeleton" style="width: 100px; height: 40px; border-radius: 8px;"></div>
              <div class="skeleton" style="width: 100px; height: 40px; border-radius: 8px;"></div>
            </div>
          </div>
        </nav>
      </header>
    `;

    const container = document.getElementById('navbar-container');
    if (container) {
      container.innerHTML = navbarHTML;
      if (window.applyTheme) window.applyTheme(localStorage.getItem('theme') || 'dark');
    }
  }

  async function loadAuthSection() {
    const authWrapper = document.getElementById('auth-container-wrapper');
    if (!authWrapper) return;

    if (!isLoggedIn) {
      authWrapper.innerHTML = `
        <a href="login.html" class="btn btn-secondary">Iniciar Sesión</a>
        <a href="register.html" class="btn btn-primary">Registrarse</a>
      `;
      return;
    }

    let adminButton = '';
    let userNameHTML = '';

    try {
      const res = await fetch(`${API_BASE_URL}/profile`, {
        headers: { Authorization: `Bearer ${token}` }
      });

      if (res.ok) {
        const user = await res.json();
        userNameHTML = `<span style="margin-right: 10px; font-weight: 500; transition: color 0.3s;">${user.firstName || 'Usuario'}</span>`;
        if (user.role === 'ADMIN') {
          adminButton = `<a href="admin-dashboard.html" class="btn btn-outline">Panel Admin</a>`;
        }
      }
    } catch (error) {
      console.error(error);
    } finally {
      authWrapper.innerHTML = `
        ${adminButton}
        <div class="profile-dropdown" style="position: relative; display: inline-flex; align-items: center;">
          ${userNameHTML}
          <img src="img/default_user.jpeg" alt="Perfil" class="nav-profile-pic" style="cursor: pointer; border-radius: 50%; border: 2px solid transparent; transition: all 0.3s ease;" onerror="this.src='img/default_user.jpeg'" onclick="this.nextElementSibling.classList.toggle('show')">
          <div class="dropdown-content" style="display: none; position: absolute; right: 0; top: 100%; background-color: var(--surface); min-width: 160px; box-shadow: 0px 8px 16px var(--shadow); z-index: 10; border-radius: 8px; overflow: hidden; border: 1px solid var(--border-color); opacity: 0; transition: opacity 0.3s ease;">
            <a href="profile.html" style="color: var(--text-primary); padding: 12px 16px; text-decoration: none; display: block; border-bottom: 1px solid var(--border-color);">👤 Editar Perfil</a>
            <a href="orders.html" style="color: var(--text-primary); padding: 12px 16px; text-decoration: none; display: block;">📦 Mis Pedidos</a>
          </div>
        </div>
        <button id="navbarLogoutBtn" class="btn btn-primary" style="margin-left: 10px;">Cerrar Sesión</button>
      `;
      setupLogout();
    }
  }

  window.addEventListener('click', function(e) {
    if (!e.target.matches('.nav-profile-pic')) {
      const dropdowns = document.getElementsByClassName("dropdown-content");
      for (const d of dropdowns) {
        if (d.classList.contains('show')) {
          d.classList.remove('show');
          d.style.opacity = '0';
          setTimeout(() => d.style.display = 'none', 300);
        }
      }
    } else {
      const dropdown = e.target.nextElementSibling;
      if (dropdown.classList.contains('show')) {
        dropdown.classList.remove('show');
        dropdown.style.opacity = '0';
        setTimeout(() => dropdown.style.display = 'none', 300);
      } else {
        dropdown.style.display = 'block';
        setTimeout(() => { dropdown.classList.add('show'); dropdown.style.opacity = '1'; }, 10);
      }
    }
  });

  function setupLogout() {
    const logoutBtn = document.getElementById('navbarLogoutBtn');
    if (logoutBtn) {
      logoutBtn.addEventListener('click', () => {
        logoutBtn.innerHTML = '<span class="spinner-loader"></span> Saliendo...';
        logoutBtn.disabled = true;
        setTimeout(() => {
          localStorage.removeItem('authToken');
          sessionStorage.setItem('flashMessage', 'Has cerrado sesion correctamente');
          window.location.href = 'index.html';
        }, 500);
      });
    }
  }

  document.addEventListener('DOMContentLoaded', () => {
    renderNavbarBase();
    loadAuthSection();
  });
})();
