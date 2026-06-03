window.escapeHTML = function(str) {
    if (str === null || str === undefined) return '';
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
};
const API = API_BASE_URL;

function getToken() {
  return localStorage.getItem('authToken');
}

function authHeaders() {
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${getToken()}`
  };
}

async function checkAdmin() {
  const token = getToken();
  if (!token) { window.location.href = 'login.html'; return; }
  try {
    const res = await fetch(`${API}/profile`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!res.ok) throw new Error();
    const data = await res.json();
    if (data.role !== 'ADMIN') {
      alert('Acceso denegado. Solo administradores.');
      window.location.replace('index.html');
    } else {
      document.body.style.display = 'block'; // Show body if admin
    }
  } catch {
    window.location.replace('login.html');
  }
}

function showToastMsg(msg, type = 'success') {
  if (window.showToast) showToast(msg, type);
  else alert(msg);
}

function renderPagination(containerId, currentPage, totalPages, onPageClick) {
  const container = document.getElementById(containerId);
  container.innerHTML = '';
  if (totalPages <= 1) return;
  for (let i = 0; i < totalPages; i++) {
    const btn = document.createElement('button');
    btn.textContent = i + 1;
    if (i === currentPage) btn.classList.add('active');
    btn.addEventListener('click', () => onPageClick(i));
    container.appendChild(btn);
  }
}
