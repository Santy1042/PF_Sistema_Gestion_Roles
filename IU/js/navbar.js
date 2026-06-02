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

      <a href="profile.html" title="Mi Perfil">
        <img src="img/profile_placeholder.png"
            alt="Perfil"
            class="nav-profile-pic"
            onerror="this.src='img/APOLO_2 (1).png'">
      </a>

      <button id="navbarLogoutBtn" class="btn btn-primary">
        Cerrar Sesión
      </button>
    `;
  }

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



