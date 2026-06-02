function applyTheme(theme) {
  document.body.classList.toggle('theme-dark', theme === 'dark');
  document.body.classList.toggle('theme-light', theme === 'light');
  localStorage.setItem('theme', theme);

  const chk = document.getElementById('theme-switch-chk');
  if (chk) chk.checked = (theme === 'light');

  const oldBtn = document.getElementById('themeToggle');
  if (oldBtn) oldBtn.style.display = 'none';
}

function toggleTheme() {
  const current = document.body.classList.contains('theme-light') ? 'light' : 'dark';
  applyTheme(current === 'dark' ? 'light' : 'dark');
}

function injectSwitch() {
  if (document.getElementById('theme-switch-widget')) return;

  const widget = document.createElement('div');
  widget.id = 'theme-switch-widget';
  widget.innerHTML = `
    <span class="tsw-icon">🌙</span>
    <label class="tsw-track" title="Cambiar tema">
      <input type="checkbox" id="theme-switch-chk">
      <span class="tsw-thumb"></span>
    </label>
    <span class="tsw-icon">☀️</span>
  `;
  document.body.appendChild(widget);

  document.getElementById('theme-switch-chk').addEventListener('change', toggleTheme);
}

document.addEventListener('DOMContentLoaded', () => {
  injectSwitch();
  const saved = localStorage.getItem('theme') || 'dark';
  applyTheme(saved);

  const oldBtn = document.getElementById('themeToggle');
  if (oldBtn) oldBtn.style.display = 'none';
});