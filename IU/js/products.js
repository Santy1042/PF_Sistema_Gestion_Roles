let allProducts = [];

async function fetchProducts() {
    try {
        const response = await fetch(`${API_BASE_URL}/products?page=0&size=100`);
        if (!response.ok) throw new Error('No se pudo conectar al backend');
        const data = await response.json();
        allProducts = (data.content ? data.content : data).filter(p => p.isActive !== false);
        await loadCategories();
        renderProducts(allProducts);
        updateCount(allProducts.length);
    } catch (error) {
        console.error('Error conectando al backend:', error);
        const grid = document.getElementById('productsGrid');
        if (grid) grid.innerHTML = '<p style="text-align:center;grid-column:1/-1;padding:2rem;">No se pudieron cargar los productos.</p>';
    }
}

async function loadCategories() {
    try {
        const res = await fetch(`${API_URL}/categories?page=0&size=100`);
        if (!res.ok) return;
        const data = await res.json();
        const items = data.content || [];
        const sel = document.getElementById('catalogCategoryFilter');
        if (!sel) return;
        sel.innerHTML = '<option value="">Todas las categorías</option>' +
            items.map(c => `<option value="${c.idCategory}">${c.name}</option>`).join('');
    } catch (e) { console.error('Error cargando categorias', e); }
}

function renderProducts(products) {
    const grid = document.getElementById('productsGrid');
    if (!grid) return;
    if (products.length === 0) {
        grid.innerHTML = '<p style="text-align:center;grid-column:1/-1;padding:3rem;opacity:0.6;">No se encontraron productos.</p>';
        return;
    }
    grid.innerHTML = products.map(product => createProductCardHTML(product)).join('');
}

function updateCount(count) {
    const el = document.getElementById('catalogResultCount');
    if (el) el.textContent = `Mostrando ${count} producto${count !== 1 ? 's' : ''}`;
}

function applyFilters() {
    const query = (document.getElementById('catalogSearchInput')?.value || '').trim().toLowerCase();
    const catId = document.getElementById('catalogCategoryFilter')?.value || '';
    let filtered = allProducts;
    if (query) {
        filtered = filtered.filter(p => p.name.toLowerCase().includes(query));
    }
    if (catId) {
        filtered = filtered.filter(p => String(p.categoryId) === catId || String(p.category?.idCategory) === catId);
    }
    renderProducts(filtered);
    updateCount(filtered.length);
}

document.addEventListener('DOMContentLoaded', () => {
    fetchProducts();

    document.getElementById('catalogSearchBtn')?.addEventListener('click', applyFilters);
    document.getElementById('catalogClearBtn')?.addEventListener('click', () => {
        const input = document.getElementById('catalogSearchInput');
        const cat = document.getElementById('catalogCategoryFilter');
        if (input) input.value = '';
        if (cat) cat.value = '';
        renderProducts(allProducts);
        updateCount(allProducts.length);
    });
    document.getElementById('catalogSearchInput')?.addEventListener('keydown', e => {
        if (e.key === 'Enter') applyFilters();
    });
    document.getElementById('catalogCategoryFilter')?.addEventListener('change', applyFilters);
});
