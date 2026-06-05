function getStockInfo(stock) {
    if (stock > 10) return `En stock (${stock} disponibles)`;
    if (stock > 0) return `Stock limitado (${stock} disponibles)`;
    return `Agotado (0 disponibles)`;
}

function handleAddToCartClick(productId, productName, productPrice, fallbackImage, originalPrice, discountPercentage) {
    let variantId = productId;
    let color = null;
    let size = null;
    let image = fallbackImage;

    const hiddenInput = document.getElementById(`variant-select-${productId}`);
    if (hiddenInput) {
        variantId = parseInt(hiddenInput.value, 10) || hiddenInput.value;
        const text = hiddenInput.getAttribute('data-text');
        if (text) {
            const parts = text.split('-');
            if (parts.length >= 2) {
                color = parts[0].trim();
                size = parts[1].replace('Talla', '').trim();
            }
        }
    } else {
        if (window.showToast) showToast('Este producto no tiene variantes configuradas', 'error');
        else alert('Este producto no tiene variantes configuradas');
        return;
    }

    const imgElement = document.getElementById(`product-img-${productId}`);
    if (imgElement && imgElement.src && !imgElement.src.includes('data:image')) {
        image = imgElement.src;
    }

    const productData = {
        price: productPrice,
        originalPrice: originalPrice || productPrice,
        discountPercentage: discountPercentage || 0,
        image: image,
        color: color,
        size: size,
        description: hiddenInput ? hiddenInput.getAttribute('data-text') : ''
    };

    if (typeof addToCart === 'function') {
        addToCart(variantId, productName, 1, productData);
    }
}

function selectColor(productId, btnElement) {
    const allBtns = document.querySelectorAll(`.color-btn-${productId}`);
    allBtns.forEach(btn => {
        btn.style.backgroundColor = 'transparent';
        btn.style.color = 'var(--text-primary)';
        btn.style.borderColor = 'var(--border-color)';
    });

    btnElement.style.backgroundColor = 'var(--primary)';
    btnElement.style.color = 'white';
    btnElement.style.borderColor = 'var(--primary)';

    const color = btnElement.getAttribute('data-color');
    const activeVariants = window.activeProductVariants[productId] || [];
    const colorVariants = activeVariants.filter(v => v.color === color);

    const sizesContainer = document.getElementById(`sizes-container-${productId}`);
    if (sizesContainer && colorVariants.length > 0) {
        sizesContainer.innerHTML = colorVariants.map((v, index) => {
            const isSelected = index === 0;
            const activeStyle = isSelected
                ? 'background-color: var(--primary); color: white; border-color: var(--primary);'
                : 'background-color: transparent; color: var(--text-primary); border-color: var(--border-color);';
            return `<button type="button" 
                       class="size-btn-${productId}" 
                       data-id="${v.id}" 
                       data-img="${v.imageUrl || ''}" 
                       data-stock="${v.stock}" 
                       data-text="${v.color} - Talla ${v.size}"
                       onclick="selectSize(${productId}, this)"
                       style="padding: 4px 10px; margin: 0 6px 6px 0; border: 1px solid; border-radius: 8px; cursor: pointer; font-size: 0.85em; font-weight: 500; transition: all 0.2s ease; ${activeStyle}">
                       ${v.size}
                   </button>`;
        }).join('');

        // Automatically select the first size of the new color
        const firstSizeBtn = sizesContainer.querySelector(`.size-btn-${productId}`);
        if (firstSizeBtn) {
            selectSize(productId, firstSizeBtn, true);
        }
    }
}

function selectSize(productId, btnElement, isAutoSelect = false) {
    if (!isAutoSelect) {
        const allBtns = document.querySelectorAll(`.size-btn-${productId}`);
        allBtns.forEach(btn => {
            btn.style.backgroundColor = 'transparent';
            btn.style.color = 'var(--text-primary)';
            btn.style.borderColor = 'var(--border-color)';
        });

        btnElement.style.backgroundColor = 'var(--primary)';
        btnElement.style.color = 'white';
        btnElement.style.borderColor = 'var(--primary)';
    }

    const hiddenInput = document.getElementById(`variant-select-${productId}`);
    if (hiddenInput) {
        hiddenInput.value = btnElement.getAttribute('data-id');
        hiddenInput.setAttribute('data-text', btnElement.getAttribute('data-text'));
    }

    const newImg = btnElement.getAttribute('data-img');
    const newStock = parseInt(btnElement.getAttribute('data-stock'), 10);

    if (newImg && newImg !== 'null' && newImg !== '') {
        const imgElement = document.getElementById(`product-img-${productId}`);
        if (imgElement) {
            imgElement.src = newImg;
        }
    }

    const stockElement = document.getElementById(`product-stock-${productId}`);
    if (stockElement) {
        stockElement.innerHTML = `<span>${getStockInfo(newStock)}</span>`;
    }

    const addToCartBtn = document.getElementById(`add-to-cart-btn-${productId}`);
    if (addToCartBtn) {
        if (newStock <= 0) {
            addToCartBtn.disabled = true;
            addToCartBtn.style.opacity = '0.5';
            addToCartBtn.style.cursor = 'not-allowed';
            addToCartBtn.textContent = 'Agotado';
        } else {
            addToCartBtn.disabled = false;
            addToCartBtn.style.opacity = '1';
            addToCartBtn.style.cursor = 'pointer';
            addToCartBtn.textContent = 'Agregar al carrito';
        }
    }
}

function createProductCardHTML(product) {
    const pId = product.productId || product.id;
    let activeVariants = [];
    if (product.variants && product.variants.length > 0) {
        activeVariants = product.variants.filter(v => v.isActive !== false);
    }
    const hasVariants = activeVariants.length > 0;
    const defaultVariant = hasVariants ? activeVariants[0] : null;

    let imageUrl = defaultVariant && defaultVariant.imageUrl ? defaultVariant.imageUrl : (product.imageUrl || product.image || 'img/default_product.png');
    let initialStock = defaultVariant ? defaultVariant.stock : (product.stock || 0);
    let stockInfo = getStockInfo(initialStock);

    let variantsHtml = '';
    if (hasVariants) {
        window.activeProductVariants = window.activeProductVariants || {};
        window.activeProductVariants[pId] = activeVariants;
        
        const colors = [...new Set(activeVariants.map(v => v.color))];
        const defaultColor = colors[0];
        const defaultVariantsForColor = activeVariants.filter(v => v.color === defaultColor);

        const colorBtns = colors.map((c, index) => {
            const isSelected = index === 0;
            const activeStyle = isSelected
                ? 'background-color: var(--primary); color: white; border-color: var(--primary);'
                : 'background-color: transparent; color: var(--text-primary); border-color: var(--border-color);';
            return `<button type="button" 
                       class="color-btn-${pId}" 
                       data-color="${c}"
                       onclick="selectColor(${pId}, this)"
                       style="padding: 6px 14px; margin: 0 8px 8px 0; border: 1px solid; border-radius: 999px; cursor: pointer; font-size: 0.9em; font-weight: 500; transition: all 0.2s ease; ${activeStyle}">
                       ${c}
                   </button>`;
        }).join('');

        const sizeBtns = defaultVariantsForColor.map((v, index) => {
            const isSelected = index === 0;
            const activeStyle = isSelected
                ? 'background-color: var(--primary); color: white; border-color: var(--primary);'
                : 'background-color: transparent; color: var(--text-primary); border-color: var(--border-color);';
            return `<button type="button" 
                       class="size-btn-${pId}" 
                       data-id="${v.id}" 
                       data-img="${v.imageUrl || ''}" 
                       data-stock="${v.stock}" 
                       data-text="${v.color} - Talla ${v.size}"
                       onclick="selectSize(${pId}, this)"
                       style="padding: 4px 10px; margin: 0 6px 6px 0; border: 1px solid; border-radius: 8px; cursor: pointer; font-size: 0.85em; font-weight: 500; transition: all 0.2s ease; ${activeStyle}">
                       ${v.size}
                   </button>`;
        }).join('');

        variantsHtml = `
            <div style="margin-bottom: 0.4rem; font-size: 0.8rem; color: var(--text-secondary); font-weight: 600;">Color:</div>
            <div class="product-color-selector" style="margin-bottom: 0.8rem; display: flex; flex-wrap: wrap;">
                ${colorBtns}
            </div>
            <div style="margin-bottom: 0.4rem; font-size: 0.8rem; color: var(--text-secondary); font-weight: 600;">Talla:</div>
            <div id="sizes-container-${pId}" class="product-size-selector" style="margin-bottom: 1rem; display: flex; flex-wrap: wrap;">
                ${sizeBtns}
            </div>
            <input type="hidden" id="variant-select-${pId}" value="${defaultVariant.id}" data-text="${defaultVariant.color} - Talla ${defaultVariant.size}">
        `;
    }

    let tagsHtml = '';
    if (product.tags && product.tags.length > 0) {
        tagsHtml = `<div class="product-tags" style="margin-bottom: 0.75rem; display: flex; flex-wrap: wrap; gap: 4px;">
            ${product.tags.map(tag => `<span style="background: var(--surface-muted); color: var(--text-secondary); padding: 3px 8px; border-radius: 999px; font-size: 0.75em; border: 1px solid var(--border-color);">#${tag}</span>`).join('')}
        </div>`;
    }

    let badgesHtml = '';
    if (product.discountPercentage && product.discountPercentage > 0) {
        badgesHtml += `<span style="position: absolute; top: 10px; left: 10px; background: var(--error); color: white; padding: 4px 8px; border-radius: 4px; font-weight: bold; font-size: 0.8em; z-index: 10; box-shadow: 0 2px 4px rgba(0,0,0,0.2);">-${product.discountPercentage}%</span>`;
    }
    if (product.isFeatured) {
        badgesHtml += `<span style="position: absolute; top: 10px; right: 10px; background: var(--primary); color: white; padding: 4px 8px; border-radius: 4px; font-weight: bold; font-size: 0.8em; z-index: 10; box-shadow: 0 2px 4px rgba(0,0,0,0.2);">⭐ Destacado</span>`;
    }

    let priceHtml = '';
    const originalPrice = Number(product.price);
    let currentPrice = originalPrice;

    if (product.discountPercentage && product.discountPercentage > 0) {
        currentPrice = originalPrice - (originalPrice * product.discountPercentage / 100);
        priceHtml = `
            <div class="product-price-container" style="display: flex; align-items: center; gap: 0.75rem; margin-bottom: 1rem;">
                <span style="color: var(--error); font-weight: 800; font-size: 1.4rem;">$${currentPrice.toFixed(2)}</span>
                <span style="text-decoration: line-through; color: var(--text-secondary); font-size: 1rem;">$${originalPrice.toFixed(2)}</span>
            </div>
        `;
    } else {
        priceHtml = `<p class="product-price">$${originalPrice.toFixed(2)}</p>`;
    }

    return `
    <article class="product-card" id="product-${pId}">
      <div class="product-image" style="position: relative;">
        ${badgesHtml}
        <img id="product-img-${pId}" src="${imageUrl}" alt="${product.name}" onerror="this.onerror=null; this.src='img/default_product.png'" />
      </div>
      <div class="product-info">
        <span class="product-category">${product.category || product.categoryName || 'General'}</span>
        <h3 class="product-name">${product.name}</h3>
        ${priceHtml}
        ${tagsHtml}
        ${variantsHtml}
        <div class="product-stock" id="product-stock-${pId}">
          <span>${stockInfo}</span>
        </div>
        <button id="add-to-cart-btn-${pId}" class="btn btn-primary" onclick="handleAddToCartClick(${pId}, '${product.name.replace(/'/g, "\\'")}', ${currentPrice}, '${imageUrl}', ${originalPrice}, ${product.discountPercentage || 0})" ${initialStock <= 0 ? 'disabled style="opacity: 0.5; cursor: not-allowed;"' : ''}>
          ${initialStock <= 0 ? 'Agotado' : 'Agregar al carrito'}
        </button>
      </div>
    </article>
    `;
}
