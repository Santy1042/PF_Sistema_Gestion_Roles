const API_URL = 'http://localhost:8080/api';

let allProducts = [];

async function fetchProducts() {
    try {
        const response = await fetch(`${API_URL}/products`);
        if (!response.ok) {
            throw new Error('No se pudo conectar al backend');
        }
        
        const data = await response.json();
        allProducts = data.content ? data.content : data;
        
        renderProducts(allProducts);
    } catch (error) {
        console.error('Error conectando al backend:', error);
        alert('Hubo un error al cargar los productos. Por favor, verifica que el backend esté en ejecución.');
        const grid = document.getElementById('productsGrid');
        if (grid) {
            grid.innerHTML = '<p style="text-align: center; grid-column: 1 / -1; padding: 2rem;">No se pudieron cargar los productos.</p>';
        }
    }
}


function renderProducts(products) {
    const grid = document.getElementById('productsGrid');
    
    grid.innerHTML = products.map(product => {
        return createProductCardHTML(product);
    }).join('');
}

document.addEventListener('DOMContentLoaded', fetchProducts);
