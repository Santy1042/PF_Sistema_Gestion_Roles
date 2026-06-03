async function fetchFeaturedProducts() {
    try {
        const response = await fetch('http://localhost:8080/api/products');
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();

        let allProducts = data.content ? data.content : data;
        allProducts = allProducts.filter(p => p.isActive !== false);
        const featuredProducts = allProducts.filter(p => p.isFeatured);

        renderFeaturedCarousel(featuredProducts);
    } catch (error) {
        console.error('Error fetching featured products:', error);
        const container = document.getElementById('productsContainer');
        if (container) {
            container.innerHTML = '<p style="text-align: center; width: 100%; color: var(--error);">No se pudieron cargar los productos destacados.</p>';
        }
    }
}

function renderFeaturedCarousel(products) {
    const container = document.getElementById('productsContainer');
    if (!container) return;

    if (products.length === 0) {
        container.innerHTML = '<p style="text-align: center; width: 100%; color: var(--text-secondary);">No hay productos destacados en este momento.</p>';
        return;
    }

    container.style.display = 'flex';
    container.style.gap = '2rem';
    container.style.overflowX = 'auto';
    container.style.scrollSnapType = 'x mandatory';
    container.style.paddingBottom = '1rem';
    container.style.scrollbarWidth = 'thin';
    container.style.scrollbarColor = 'var(--primary) var(--surface-light)';

    container.innerHTML = products.map(product => {
        return `<div style="min-width: 300px; max-width: 300px; scroll-snap-align: start; flex-shrink: 0;">
            ${createProductCardHTML(product)}
        </div>`;
    }).join('');
}

document.addEventListener('DOMContentLoaded', fetchFeaturedProducts);
