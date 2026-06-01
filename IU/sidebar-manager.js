// Los listeners se asignarán después de que se inyecte el HTML en DOMContentLoaded

document.addEventListener('DOMContentLoaded', () => {
  const sidebarHTML = `<div class="sidebar-overlay" id="sidebarOverlay"></div>
<aside class="sidebar" id="sidebar">
  <div class="sidebar-header">
    <h3>Menú</h3>
    <button class="sidebar-close" id="sidebarClose">✕</button>
  </div>
  
  <div class="sidebar-content">
    <div class="sidebar-section">
      <div class="sidebar-section-title">Perfil</div>
      <ul class="sidebar-menu">
        <li><a href="profile.html">👤 Mi Perfil</a></li>
        <li><a href="products.html">📦 Productos</a></li>
      </ul>
    </div>

    <div class="sidebar-section">
      <div class="sidebar-section-title">Administración</div>
      <ul class="sidebar-menu">
        <li><a href="admin-dashboard.html">🔒 Panel Admin (Temporal)</a></li>
      </ul>
    </div>

    <div class="sidebar-section">
      <div class="sidebar-section-title">Configuración</div>
      <ul class="sidebar-menu">
        <li><button id="sidebarThemeToggle">🌙 Cambiar Tema</button></li>
        <li><a href="#">⚙️ Preferencias</a></li>
        <li><a href="#">❓ Ayuda y Soporte</a></li>
      </ul>
    </div>
  </div>

  <div class="sidebar-footer">
    <a href="index.html" class="btn btn-secondary">Inicio</a>
    <a href="login.html" class="btn btn-outline">Cerrar Sesión</a>
  </div>
</aside>

<button class="sidebar-toggle" id="sidebarToggle">☰</button>`;

  document.body.insertAdjacentHTML('afterbegin', sidebarHTML);
  
  const newSidebarToggle = document.getElementById('sidebarToggle');
  const newSidebar = document.getElementById('sidebar');
  const newSidebarClose = document.getElementById('sidebarClose');
  const newSidebarOverlay = document.getElementById('sidebarOverlay');
  const newSidebarThemeToggle = document.getElementById('sidebarThemeToggle');

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
});
