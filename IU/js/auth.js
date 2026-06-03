(function() {
  const token = localStorage.getItem('authToken');
  if (!token) {
    sessionStorage.setItem('flashMessage', 'Debes iniciar sesión para acceder a esta página.');
    sessionStorage.setItem('flashType', 'error');
    window.location.replace('login.html');
  }
})();
