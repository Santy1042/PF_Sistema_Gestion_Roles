import re

filepath = 'IU/admin-dashboard.html'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Remove the "Variantes" tab button
content = re.sub(r'<button class="admin-tab" data-tab="variantes">Variantes</button>\s*', '', content)

# 2. Extract the "tab-variantes" section
# Let's find the boundaries of the section
match = re.search(r'<!-- ===================== TAB: VARIANTES ===================== -->\s*<section class="tab-content" id="tab-variantes">.*?</section>\s*', content, re.DOTALL)
if match:
    # 3. Replace the section with the new Modal HTML structure for variants
    modal_html = """
    <!-- Modal Gestion de Variantes -->
    <div class="admin-modal" id="modalVariantes" style="display:none">
      <div class="modal-content" style="max-width: 800px; width: 90%; max-height: 90vh; overflow-y: auto;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
          <h2 id="modalVariantesTitle">Variantes de Producto</h2>
          <button class="btn btn-outline" id="btnCerrarModalVariantes">Cerrar</button>
        </div>
        
        <div style="margin-bottom: 1rem;">
          <button class="btn btn-primary" id="btnNuevaVariante">+ Añadir Variante</button>
        </div>

        <div class="admin-form-panel" id="formVariante" style="display:none; margin-bottom: 2rem;">
          <h3 id="formVarianteTitle">Crear Variante</h3>
          <div class="form-grid">
            <input type="hidden" id="varianteId">
            <input type="hidden" id="varianteProductoId">
            <input type="hidden" id="varianteImageUrl">
            <div class="form-group">
              <label>Color</label>
              <select id="varianteColorId"><option value="">Sin color</option></select>
            </div>
            <div class="form-group">
              <label>Talla</label>
              <select id="varianteTallaId"><option value="">Sin talla</option></select>
            </div>
            <div class="form-group">
              <label>Stock *</label>
              <input type="number" id="varianteStock" placeholder="0" min="0">
            </div>
            <div class="form-group">
              <label>Subir imagen (opcional)</label>
              <input type="file" id="varianteImageFile" accept="image/*">
            </div>
          </div>
          <div class="form-actions">
            <button class="btn btn-primary" id="btnGuardarVariante">Guardar</button>
            <button class="btn btn-outline" id="btnCancelarVariante">Cancelar</button>
          </div>
        </div>

        <div class="table-panel">
          <table id="tablaVariantes">
            <thead>
              <tr>
                <th>ID</th><th>Color</th><th>Talla</th>
                <th>Stock</th><th>Estado</th><th>Acciones</th>
              </tr>
            </thead>
            <tbody id="bodyVariantes"><tr><td colspan="6">Cargando...</td></tr></tbody>
          </table>
        </div>
      </div>
    </div>
    """
    content = content[:match.start()] + modal_html + content[match.end():]

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
