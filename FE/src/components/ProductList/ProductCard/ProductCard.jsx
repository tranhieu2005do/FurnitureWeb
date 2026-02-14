import { useState } from 'react';
import VariantModal from '../VarriantModal/VariantModal';

export default function ProductCard({ product, viewMode = 'grid', animationDelay = 0 }) {
  const [isWishlisted, setIsWishlisted]   = useState(false);
  const [showModal, setShowModal]         = useState(false);
  const [addedFeedback, setAddedFeedback] = useState(false);

  // Callback khi thêm giỏ hàng thành công từ modal
  const handleAddSuccess = ({ variant, quantity }) => {
    console.log(`✅ Đã thêm: variant #${variant.id}, số lượng ${quantity}`);
    setAddedFeedback(true);
    setTimeout(() => setAddedFeedback(false), 2000);
  };

  return (
    <>
      <div
        className={`product-card-item ${viewMode}`}
        style={{ animationDelay: `${animationDelay}s` }}
      >
        {/* ── Image Section ── */}
        <div className="product-image-section">
          <img src={product.image} alt={product.name} className="product-img" />

          {product.discount && (
            <span className="discount-badge">-{product.discount}%</span>
          )}
          {product.isNew && <span className="new-badge">Mới</span>}

          <div className="quick-actions">
            <button
              className={`wishlist-btn ${isWishlisted ? 'active' : ''}`}
              onClick={() => setIsWishlisted(!isWishlisted)}
              title="Yêu thích"
            >
              {isWishlisted ? '❤️' : '🤍'}
            </button>
            <button className="quick-view-action" title="Xem nhanh">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                   stroke="currentColor" strokeWidth="2">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                <circle cx="12" cy="12" r="3"/>
              </svg>
            </button>
          </div>
        </div>

        {/* ── Details Section ── */}
        <div className="product-details-section">
          <div className="product-meta">
            <span className="product-cat">{product.category}</span>
            <div className="product-rating">
              <span className="stars">⭐ {product.rating}</span>
              <span className="reviews">({product.reviews})</span>
            </div>
          </div>

          <h3 className="product-title">
            <a href={`/product/${product.id}`}>{product.name}</a>
          </h3>

          {viewMode === 'list' && (
            <p className="product-desc">{product.description}</p>
          )}

          <div className="product-footer-section">
            <div className="price-section">
              {product.originalPrice && (
                <span className="original-price">
                  {product.originalPrice.toLocaleString()}đ
                </span>
              )}
              <span className="current-price">
                {product.price.toLocaleString()}đ
              </span>
            </div>

            {/* ── Nút Add → mở VariantModal ── */}
            <button
              className={`add-cart-action ${addedFeedback ? 'added' : ''}`}
              onClick={() => setShowModal(true)}
              disabled={product.stock === 0}
              title={product.stock === 0
                ? 'Hết hàng'
                : 'Chọn phiên bản & thêm vào giỏ'}
            >
              {addedFeedback ? (
                <>
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                       stroke="currentColor" strokeWidth="2.5">
                    <polyline points="20 6 9 17 4 12"/>
                  </svg>
                  {viewMode === 'list' && <span>Đã thêm!</span>}
                </>
              ) : (
                <>
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                       stroke="currentColor" strokeWidth="2">
                    <circle cx="9"  cy="21" r="1"/>
                    <circle cx="20" cy="21" r="1"/>
                    <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
                  </svg>
                  {viewMode === 'list' && <span>Thêm vào giỏ</span>}
                </>
              )}
            </button>
          </div>

          {product.stock <= 5 && product.stock > 0 && (
            <span className="stock-warning">Chỉ còn {product.stock} sản phẩm</span>
          )}
          {product.stock === 0 && (
            <span className="out-of-stock">Hết hàng</span>
          )}
        </div>
      </div>

      {/* Modal render bên ngoài card để tránh overflow:hidden */}
      {showModal && (
        <VariantModal
          product={product}
          onClose={() => setShowModal(false)}
          onAddSuccess={handleAddSuccess}
        />
      )}
    </>
  );
}