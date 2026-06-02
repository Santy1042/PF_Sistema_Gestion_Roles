const themeToggleBtn = document.getElementById('themeToggle');

function applyTheme(theme) {
  document.body.classList.toggle('theme-dark', theme === 'dark');
  document.body.classList.toggle('theme-light', theme === 'light');
  if (themeToggleBtn) {
    themeToggleBtn.textContent = theme === 'dark' ? 'Modo claro' : 'Modo oscuro';
  }
  localStorage.setItem('theme', theme);
}

function toggleTheme() {
  const current = document.body.classList.contains('theme-dark') ? 'dark' : 'light';
  applyTheme(current === 'dark' ? 'light' : 'dark');
}

function initializeTheme() {
  const saved = localStorage.getItem('theme') || 'dark';
  applyTheme(saved);
}

document.addEventListener('DOMContentLoaded', () => {
  initializeTheme();
  if (themeToggleBtn) {
    themeToggleBtn.addEventListener('click', toggleTheme);
  }
});
