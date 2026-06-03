(function() {
  const token = localStorage.getItem('authToken');
  const isLoggedIn = !!token;

  let authHTML = '';

  async function buildAuthHTML() {

    if (!isLoggedIn) {
      return `
        <a href="login.html" class="btn btn-secondary">Iniciar Sesión</a>
        <a href="register.html" class="btn btn-primary">Registrarse</a>
      `;
    }

    let adminButton = '';
    let userNameHTML = '';

    try {
      const res = await fetch(`${API_BASE_URL}/profile`, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });

      if (res.ok) {
        const user = await res.json();

        userNameHTML = `<span style="margin-right: 10px; font-weight: 500;">${user.firstName || 'Usuario'}</span>`;

        if (user.role === 'ADMIN') {
          adminButton = `
            <a href="admin-dashboard.html" class="btn btn-outline">
              Panel Admin
            </a>
          `;
        }
      }
    } catch (error) {
      console.error(error);
    }
    return `
      ${adminButton}

      <div class="profile-dropdown" style="position: relative; display: inline-flex; align-items: center;">
        ${userNameHTML}
        <img src="img/default_user.jpeg"
             alt="Perfil"
             class="nav-profile-pic"
             style="cursor: pointer;"
             onerror="this.src='img/default_user.jpeg'"
             onclick="this.nextElementSibling.classList.toggle('show')">
        <div class="dropdown-content" style="display: none; position: absolute; right: 0; top: 100%; background-color: var(--surface); min-width: 160px; box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2); z-index: 10; border-radius: 8px; overflow: hidden; border: 1px solid var(--border-color);">
          <a href="profile.html" style="color: var(--text-primary); padding: 12px 16px; text-decoration: none; display: block; border-bottom: 1px solid var(--border-color);">👤 Editar Perfil</a>
          <a href="orders.html" style="color: var(--text-primary); padding: 12px 16px; text-decoration: none; display: block;">📦 Mis Pedidos</a>
        </div>
      </div>

      <button id="navbarLogoutBtn" class="btn btn-primary" style="margin-left: 10px;">
        Cerrar Sesión
      </button>
    `;
  }

  window.addEventListener('click', function(e) {
    if (!e.target.matches('.nav-profile-pic')) {
      var dropdowns = document.getElementsByClassName("dropdown-content");
      for (var d of dropdowns) {
        if (d.classList.contains('show')) {
          d.classList.remove('show');
          d.style.display = 'none';
        }
      }
    } else {
      var dropdown = e.target.nextElementSibling;
      if (dropdown.classList.contains('show')) {
        dropdown.style.display = 'block';
      } else {
        dropdown.style.display = 'none';
      }
    }
  });

  async function renderNavbar() {

  authHTML = await buildAuthHTML();

  const navbarHTML = `
    <header>
      <nav>
        <a href="index.html" class="logo">
          <img src="img/dark_logo.png" alt="Logo AURA Essentials" id="navLogo" style="height: 60px; width: auto; max-width: none;">
          <span>AURA ESSENTIALS</span>
        </a>

        <div class="nav-auth">
          <a href="cart.html"
             class="btn btn-outline cart-btn"
             title="Carrito de compras">
            🛒
          </a>
          ${authHTML}
        </div>
      </nav>
    </header>
  `;

  const container = document.getElementById('navbar-container');

  if (container) {
    container.innerHTML = navbarHTML;
    setupLogout();
    if (window.applyTheme) {
      window.applyTheme(localStorage.getItem('theme') || 'dark');
    }
  }
  }

  function setupLogout() {
    const logoutBtn = document.getElementById('navbarLogoutBtn');
    if (logoutBtn) {
      logoutBtn.addEventListener('click', () => {
        localStorage.removeItem('authToken');
        sessionStorage.setItem('flashMessage', 'Has cerrado sesion correctamente');
        window.location.href = 'index.html';
      });
    }
  }

  document.addEventListener('DOMContentLoaded', renderNavbar);
})();
