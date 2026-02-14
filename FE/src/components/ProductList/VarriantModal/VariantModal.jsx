import { useState, useEffect } from 'react';
import productService from '../../../api/ProductService';
import './VariantModal.css';
import cartService from '../../../api/CartService';

// ── Mapping enum → nhãn hiển thị ──────────────────────────────────────────────
const MATERIAL_LABEL = {
  GO_SOI:    'Gỗ Sồi',
  GO_TAN_BI: 'Gỗ Tần Bì',
  GO_OC_CHO: 'Gỗ Óc Chó',
  INOX:      'Inox',
  NHOM:      'Nhôm',
  MARBLE:    'Đá Marble',
  GRANITE:   'Đá Granite',
  KINH:      'Kính',
  VAI:       'Vải',
};

const COLOR_META = {
  TRANG:       { label: 'Trắng',       hex: '#f5f5f0' },
  BE:          { label: 'Be',           hex: '#e8dcc8' },
  XAM_NHAT:    { label: 'Xám nhạt',    hex: '#c8c8c8' },
  XAM_DAM:     { label: 'Xám đậm',     hex: '#707070' },
  DEN:         { label: 'Đen',          hex: '#1a1a1a' },
  NAU_GO_NHAT: { label: 'Nâu gỗ nhạt', hex: '#c4a882' },
  NAU_GO_DAM:  { label: 'Nâu gỗ đậm',  hex: '#8b7355' },
};

// ── Helper ─────────────────────────────────────────────────────────────────────
function formatPrice(price) {
  if (!price) return '—';
  return Number(price).toLocaleString('vi-VN') + 'đ';
}

function formatDimension(length, width, height) {
  if (!length && !width && !height) return null;
  return `${length ?? '?'} × ${width ?? '?'} × ${height ?? '?'} cm`;
}

export default function VariantModal({ product, onClose, onAddSuccess }) {
  const [variants, setVariants]         = useState([]);
  const [loading, setLoading]           = useState(true);
  const [selectedVariant, setSelected]  = useState(null);
  const [quantity, setQuantity]         = useState(1);
  const [adding, setAdding]             = useState(false);
  const [error, setError]               = useState('');

  // ── Filter state để thu hẹp lựa chọn ────────────────────────────────────────
  const [filterColor, setFilterColor]       = useState(null);
  const [filterMaterial, setFilterMaterial] = useState(null);

  // ── Fetch variants của product ────────────────────────────────────────────────
  useEffect(() => {
    const fetchVariants = async () => {
      try {
        setLoading(true);
        const res = await productService.getProductVariants(product.id);
        console.log("Variant response:", res);
        const content = res.data;
        setVariants(content.filter(v => v.is_active));
      } catch (err) {
        console.error('Lỗi tải variant:', err);
        setError('Không thể tải thông tin biến thể sản phẩm.');
      } finally {
        setLoading(false);
      }
    };
    fetchVariants();
  }, [product.id]);

  // ── Danh sách màu / chất liệu có sẵn (từ variants thực tế) ──────────────────
  const availableColors    = [...new Set(variants.map(v => v.color).filter(Boolean))];
  const availableMaterials = [...new Set(variants.map(v => v.material).filter(Boolean))];

  // ── Variants sau khi lọc theo color / material ────────────────────────────────
  const filteredVariants = variants.filter(v => {
    if (filterColor    && v.color    !== filterColor)    return false;
    if (filterMaterial && v.material !== filterMaterial) return false;
    return true;
  });

  // ── Khi filter thay đổi, bỏ chọn variant nếu không còn trong danh sách ───────
  useEffect(() => {
    if (selectedVariant && !filteredVariants.find(v => v.id === selectedVariant.id)) {
      setSelected(null);
    }
  }, [filterColor, filterMaterial]);

  // ── Add to cart ───────────────────────────────────────────────────────────────
  const handleAddToCart = async () => {
    if (!selectedVariant) return;
    try {
      setAdding(true);
      setError('');
      const itemRequest = {
        cart_id: localStorage.getItem('cart_id'),
        variant_id: selectedVariant.id,
        quantity: quantity
      }
      cartService.addItemToCart(itemRequest);
      onAddSuccess?.({ variant: selectedVariant, quantity });
      onClose();
    } catch (err) {
      console.error('Lỗi thêm vào giỏ:', err);
      setError(err.response?.data?.message || 'Thêm vào giỏ hàng thất bại.');
    } finally {
      setAdding(false);
    }
  };

  const canAdd = selectedVariant && selectedVariant.inStock > 0;

  return (
    <>
      {/* Backdrop */}
      <div className="modal-backdrop" onClick={onClose} />

      {/* Modal */}
      <div className="variant-modal" role="dialog" aria-modal="true">
        {/* Header */}
        <div className="vm-header">
          <div className="vm-header-info">
            <h2 className="vm-title">Chọn Phiên Bản</h2>
            <p className="vm-product-name">{product.name}</p>
          </div>
          <button className="vm-close" onClick={onClose} aria-label="Đóng">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6"  y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>

        <div className="vm-body">
          {loading ? (
            <div className="vm-loading">
              <div className="vm-spinner"/>
              <span>Đang tải phiên bản...</span>
            </div>
          ) : variants.length === 0 ? (
            <div className="vm-empty">
              <span>😕</span>
              <p>Sản phẩm này chưa có biến thể nào.</p>
            </div>
          ) : (
            <>
              {/* ── Quick filter: Color ── */}
              {availableColors.length > 1 && (
                <div className="vm-filter-row">
                  <span className="vm-filter-label">Màu sắc:</span>
                  <div className="vm-color-chips">
                    {availableColors.map(c => {
                      const meta = COLOR_META[c] ?? { label: c, hex: '#ccc' };
                      return (
                        <button
                          key={c}
                          title={meta.label}
                          className={`vm-color-chip ${filterColor === c ? 'active' : ''}`}
                          onClick={() => setFilterColor(prev => prev === c ? null : c)}
                        >
                          <span className="vm-color-dot" style={{ background: meta.hex }}/>
                          <span>{meta.label}</span>
                        </button>
                      );
                    })}
                  </div>
                </div>
              )}

              {/* ── Quick filter: Material ── */}
              {availableMaterials.length > 1 && (
                <div className="vm-filter-row">
                  <span className="vm-filter-label">Chất liệu:</span>
                  <div className="vm-material-chips">
                    {availableMaterials.map(m => (
                      <button
                        key={m}
                        className={`vm-mat-chip ${filterMaterial === m ? 'active' : ''}`}
                        onClick={() => setFilterMaterial(prev => prev === m ? null : m)}
                      >
                        {MATERIAL_LABEL[m] ?? m}
                      </button>
                    ))}
                  </div>
                </div>
              )}

              {/* ── Variant list ── */}
              <p className="vm-count">
                {filteredVariants.length} phiên bản
                {(filterColor || filterMaterial) && ' phù hợp'}
              </p>

              <div className="vm-variant-list">
                {filteredVariants.map(v => {
                  const colorMeta = COLOR_META[v.color];
                  const isSelected = selectedVariant?.id === v.id;
                  const outOfStock = !v.inStock || v.inStock <= 0;

                  return (
                    <button
                      key={v.id}
                      className={`vm-variant-card
                        ${isSelected   ? 'selected'    : ''}
                        ${outOfStock   ? 'out-of-stock' : ''}
                      `}
                      onClick={() => !outOfStock && setSelected(v)}
                      disabled={outOfStock}
                    >
                      {/* Tick icon */}
                      {isSelected && (
                        <span className="vm-tick">
                          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3">
                            <polyline points="20 6 9 17 4 12"/>
                          </svg>
                        </span>
                      )}

                      <div className="vm-variant-row">
                        {/* Color swatch */}
                        {v.color && colorMeta && (
                          <span
                            className="vm-swatch"
                            style={{ background: colorMeta.hex }}
                            title={colorMeta.label}
                          />
                        )}

                        {/* Info */}
                        <div className="vm-variant-info">
                          <div className="vm-variant-attrs">
                            {v.color    && <span className="vm-attr">{colorMeta?.label ?? v.color}</span>}
                            {v.material && <span className="vm-attr vm-attr-mat">{MATERIAL_LABEL[v.material] ?? v.material}</span>}
                          </div>
                          {formatDimension(v.length, v.width, v.height) && (
                            <span className="vm-dimension">
                              📐 {formatDimension(v.length, v.width, v.height)}
                            </span>
                          )}
                        </div>

                        {/* Price + stock */}
                        <div className="vm-variant-right">
                          <span className="vm-price">{formatPrice(v.price)}</span>
                          <span className={`vm-stock ${outOfStock ? 'empty' : v.inStock <= 5 ? 'low' : 'ok'}`}>
                            {outOfStock
                              ? 'Hết hàng'
                              : v.inStock <= 5
                                ? `Còn ${v.inStock}`
                                : 'Còn hàng'}
                          </span>
                        </div>
                      </div>
                    </button>
                  );
                })}
              </div>
            </>
          )}
        </div>

        {/* Footer */}
        {!loading && variants.length > 0 && (
          <div className="vm-footer">
            {/* Selected summary */}
            {selectedVariant ? (
              <div className="vm-selected-summary">
                <div className="vm-summary-info">
                  <span className="vm-summary-label">Đã chọn:</span>
                  <div className="vm-summary-attrs">
                    {selectedVariant.color    && (
                      <span className="vm-summary-tag">
                        <span className="vm-dot" style={{ background: COLOR_META[selectedVariant.color]?.hex }}/>
                        {COLOR_META[selectedVariant.color]?.label}
                      </span>
                    )}
                    {selectedVariant.material && (
                      <span className="vm-summary-tag">
                        {MATERIAL_LABEL[selectedVariant.material]}
                      </span>
                    )}
                    {formatDimension(selectedVariant.length, selectedVariant.width, selectedVariant.height) && (
                      <span className="vm-summary-tag">
                        {formatDimension(selectedVariant.length, selectedVariant.width, selectedVariant.height)}
                      </span>
                    )}
                  </div>
                  <span className="vm-summary-price">{formatPrice(selectedVariant.price)}</span>
                </div>

                {/* Quantity */}
                <div className="vm-quantity">
                  <button
                    className="vm-qty-btn"
                    onClick={() => setQuantity(q => Math.max(1, q - 1))}
                    disabled={quantity <= 1}
                  >−</button>
                  <span className="vm-qty-value">{quantity}</span>
                  <button
                    className="vm-qty-btn"
                    onClick={() => setQuantity(q => Math.min(selectedVariant.inStock, q + 1))}
                    disabled={quantity >= selectedVariant.inStock}
                  >+</button>
                </div>
              </div>
            ) : (
              <p className="vm-hint">👆 Chọn một phiên bản để tiếp tục</p>
            )}

            {/* Error */}
            {error && <p className="vm-error">{error}</p>}

            {/* Action buttons */}
            <div className="vm-actions">
              <button className="vm-btn-cancel" onClick={onClose}>
                Hủy
              </button>
              <button
                className="vm-btn-add"
                onClick={handleAddToCart}
                disabled={!canAdd || adding}
              >
                {adding ? (
                  <>
                    <div className="vm-btn-spinner"/>
                    Đang thêm...
                  </>
                ) : (
                  <>
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <circle cx="9"  cy="21" r="1"/>
                      <circle cx="20" cy="21" r="1"/>
                      <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
                    </svg>
                    Thêm vào giỏ hàng
                  </>
                )}
              </button>
            </div>
          </div>
        )}
      </div>
    </>
  );
}