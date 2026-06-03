const isLocalhost = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';

const API_BASE_URL = isLocalhost 
    ? 'http://localhost:8080/api' 
    : 'https://pf-backend-api-7cwj.onrender.com';

const STORAGE_KEY   = 'localCart';
