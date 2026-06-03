(function() {
  function renderFooter() {
    const container = document.getElementById('footer-container');
    if (!container) return;

    container.innerHTML = `
      <footer>
        <div class="footer-content">
          <div class="footer-section">
            <h3>AURA Essentials</h3>
            <ul>
              <li><a href="index.html">Inicio</a></li>
              <li><a href="products.html">Catálogo</a></li>
              <li><a href="cart.html">Carrito</a></li>
            </ul>
          </div>
          <div class="footer-section">
            <h3>Mi Cuenta</h3>
            <ul>
              <li><a href="profile.html">Mi Perfil</a></li>
              <li><a href="orders.html">Mis Pedidos</a></li>
              <li><a href="login.html">Iniciar Sesión</a></li>
              <li><a href="register.html">Registrarse</a></li>
            </ul>
          </div>
          <div class="footer-section">
            <h3>Compras</h3>
            <ul>
              <li><a href="products.html">Ver Productos</a></li>
            </ul>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; ${new Date().getFullYear()} AURA Essentials. Todos los derechos reservados.</p>
        </div>
      </footer>
    `;
  }

  document.addEventListener('DOMContentLoaded', renderFooter);
})();
