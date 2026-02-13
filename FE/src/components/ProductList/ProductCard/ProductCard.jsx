import { useState } from "react";

export default function ProductCard({ product, viewMode, animationDelay }){
    const [isWishlisted, setIsWishlisted] = useState(false);
    
      return (
        <div 
          className={`product-card-item ${viewMode}`}
          style={{ animationDelay: `${animationDelay}s` }}
        >
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
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                  <circle cx="12" cy="12" r="3"/>
                </svg>
              </button>
            </div>
          </div>
    
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
                  <span className="original-price">{product.originalPrice.toLocaleString()}đ</span>
                )}
                <span className="current-price">{product.price.toLocaleString()}đ</span>
              </div>
    
              <button className="add-cart-action">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <circle cx="9" cy="21" r="1"/>
                  <circle cx="20" cy="21" r="1"/>
                  <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
                </svg>
                {viewMode === 'list' && <span>Thêm vào giỏ</span>}
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
      );
}