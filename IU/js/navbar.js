// navbar.js
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

    try {
      const res = await fetch('http://localhost:8080/api/profile', {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });

      if (res.ok) {
        const user = await res.json();

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

      <div class="profile-dropdown" style="position: relative; display: inline-block;">
        <img src="img/profile_placeholder.png"
             alt="Perfil"
             class="nav-profile-pic"
             style="cursor: pointer;"
             onerror="this.src='img/APOLO_2 (1).png'"
             onclick="this.nextElementSibling.classList.toggle('show')">
        <div class="dropdown-content" style="display: none; position: absolute; right: 0; background-color: var(--surface); min-width: 160px; box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2); z-index: 1; border-radius: 8px; overflow: hidden; border: 1px solid var(--border-color);">
          <a href="profile.html" style="color: var(--text-primary); padding: 12px 16px; text-decoration: none; display: block; border-bottom: 1px solid var(--border-color);">👤 Editar Perfil</a>
          <a href="orders.html" style="color: var(--text-primary); padding: 12px 16px; text-decoration: none; display: block;">📦 Mis Pedidos</a>
        </div>
      </div>

      <button id="navbarLogoutBtn" class="btn btn-primary" style="margin-left: 10px;">
        Cerrar Sesión
      </button>
    `;
  }

  // Cierra el dropdown si se hace click fuera
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
          <img src="img/APOLO_2 (1).png" alt="Logo Tienda de Ropa">
          <span>TIENDA DE ROPA</span>
        </a>

        <a href="cart.html"
           class="btn btn-outline cart-btn"
           title="Carrito de compras">
          🛒 <span id="cartCount" class="cart-count"></span>
        </a>

        <div class="nav-auth">
          ${authHTML}
        </div>
      </nav>
    </header>
  `;

  const container = document.getElementById('navbar-container');

  if (container) {
    container.innerHTML = navbarHTML;
    setupLogout();
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



