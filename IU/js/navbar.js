// navbar.js
(function() {
  const token = localStorage.getItem('authToken');
  const isLoggedIn = !!token;

  let authHTML = '';
  if (isLoggedIn) {
    authHTML = `
      <a href="profile.html" title="Mi Perfil">
        <img src="img/profile_placeholder.png" alt="Perfil" class="nav-profile-pic" onerror="this.src='img/APOLO_2 (1).png'">
      </a>
      <button id="navbarLogoutBtn" class="btn btn-primary">Cerrar Sesión</button>
    `;
  } else {
    authHTML = `
      <a href="login.html" class="btn btn-secondary">Iniciar Sesión</a>
      <a href="register.html" class="btn btn-primary">Registrarse</a>
    `;
  }

  const navbarHTML = `
    <header>
      <nav>
        <a href="index.html" class="logo">
          <img src="img/APOLO_2 (1).png" alt="Logo Tienda de Ropa">
          <span>TIENDA DE ROPA</span>
        </a>
        <button id="themeToggle" class="btn btn-outline theme-toggle" type="button">Modo oscuro</button>
        <a href="cart.html" class="btn btn-outline cart-btn" title="Carrito de compras">
          🛒 <span id="cartCount" class="cart-count"></span>
        </a>
        <div class="nav-auth">
          ${authHTML}
        </div>
      </nav>
    </header>
  `;

  // Find container and inject
  // If we run this script synchronously right after the container, we don't need DOMContentLoaded
  const container = document.getElementById('navbar-container');
  if (container) {
    container.innerHTML = navbarHTML;
  } else {
    // Fallback if script is loaded in head
    document.addEventListener('DOMContentLoaded', () => {
      const cont = document.getElementById('navbar-container');
      if (cont) cont.innerHTML = navbarHTML;
      setupLogout();
    });
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

  // If we injected synchronously, set up logout immediately after DOM load
  if (container) {
    document.addEventListener('DOMContentLoaded', setupLogout);
  }
})();



