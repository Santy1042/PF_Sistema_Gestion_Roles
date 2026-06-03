// Los listeners se asignarán después de que se inyecte el HTML en DOMContentLoaded

document.addEventListener('DOMContentLoaded', () => {
  const token = localStorage.getItem('authToken');
  if (!token) {
    // Si no está logueado, no mostramos el menú lateral ni su botón
    return;
  }

  const sidebarHTML = `<div class="sidebar-overlay" id="sidebarOverlay"></div>
<aside class="sidebar" id="sidebar">
  <div class="sidebar-header">
    <h3>Menú</h3>
    <button class="sidebar-close" id="sidebarClose">✕</button>
  </div>
  
  <div class="sidebar-content">
    <div class="sidebar-section">
      <div class="sidebar-section-title">Mi Cuenta</div>
      <ul class="sidebar-menu">
        <li><a href="profile.html">👤 Mi Perfil</a></li>
        <li><a href="orders.html">📦 Mis Pedidos</a></li>
        <li><a href="products.html">🛍️ Productos</a></li>
        <li><a href="cart.html">🛒 Carrito</a></li>
      </ul>
    </div>

    <div class="sidebar-section">
      <div class="sidebar-section-title">Configuración</div>
      <ul class="sidebar-menu">
        <li><button id="sidebarThemeToggle">🌙 Cambiar Tema</button></li>
      </ul>
    </div>
  </div>

  <div class="sidebar-footer">
    <a href="index.html" class="btn btn-secondary">Inicio</a>
    <button id="sidebarLogoutBtn" class="btn btn-outline">Cerrar Sesión</button>
  </div>
</aside>

<button class="sidebar-toggle" id="sidebarToggle">☰</button>`;

  document.body.insertAdjacentHTML('afterbegin', sidebarHTML);
  
  const newSidebarToggle = document.getElementById('sidebarToggle');
  const newSidebar = document.getElementById('sidebar');
  const newSidebarClose = document.getElementById('sidebarClose');
  const newSidebarOverlay = document.getElementById('sidebarOverlay');
  const newSidebarThemeToggle = document.getElementById('sidebarThemeToggle');
  const sidebarLogoutBtn = document.getElementById('sidebarLogoutBtn');

  if (newSidebarToggle) {
    newSidebarToggle.addEventListener('click', () => {
      newSidebar.classList.add('active');
      newSidebarOverlay.classList.add('active');
    });
  }

  if (newSidebarClose) {
    newSidebarClose.addEventListener('click', () => {
      newSidebar.classList.remove('active');
      newSidebarOverlay.classList.remove('active');
    });
  }

  if (newSidebarOverlay) {
    newSidebarOverlay.addEventListener('click', () => {
      newSidebar.classList.remove('active');
      newSidebarOverlay.classList.remove('active');
    });
  }

  if (newSidebarThemeToggle) {
    newSidebarThemeToggle.addEventListener('click', () => {
      const current = document.body.classList.contains('theme-dark') ? 'dark' : 'light';
      const newTheme = current === 'dark' ? 'light' : 'dark';
      
      document.body.classList.toggle('theme-dark', newTheme === 'dark');
      document.body.classList.toggle('theme-light', newTheme === 'light');
      localStorage.setItem('theme', newTheme);
      
      const toggle = document.getElementById('themeToggle');
      if (toggle) {
        toggle.textContent = newTheme === 'dark' ? 'Modo claro' : 'Modo oscuro';
      }
    });
  }

  if (sidebarLogoutBtn) {
    sidebarLogoutBtn.addEventListener('click', () => {
      localStorage.removeItem('authToken');
      sessionStorage.setItem('flashMessage', 'Has cerrado sesion correctamente');
      window.location.href = 'index.html';
    });
  }
});



