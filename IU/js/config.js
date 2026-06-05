const isLocalhost = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1' || window.location.hostname === '';

const API_BASE_URL = isLocalhost 
    ? 'http://localhost:8080/api' 
    : 'https://pf-backend-api-7cwj.onrender.com/api';

const STORAGE_KEY   = 'localCart';

const originalFetch = window.fetch;
window.fetch = async function(...args) {
    let retries = 3;
    let delay = 1500;
    
    while (retries > 0) {
        try {
            const response = await originalFetch(...args);
            
            if (response.status >= 500) {
                throw new Error(`Server error: ${response.status}`);
            }
            return response;
            
        } catch (error) {
            retries--;
            if (retries === 0) throw error;
            
            if (retries === 2 && typeof showToastMsg === 'function') {
                showToastMsg('El servidor en la nube está despertando. Esto puede tardar hasta 1 minuto...', 'info');
            }
            
            await new Promise(res => setTimeout(res, delay));
            delay *= 2; 
        }
    }
};
