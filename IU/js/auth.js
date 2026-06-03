(function() {
  const token = localStorage.getItem('authToken');
  if (!token) {
    alert('Debes iniciar sesión para acceder a esta página.');
    window.location.replace('login.html');
  }
})();
