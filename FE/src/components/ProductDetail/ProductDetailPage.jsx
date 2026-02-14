import { useState, useEffect, useRef } from 'react';
import apiClient from '../../api/apiClient';
import productService from '../../api/ProductService';
import './ProductDetailPage.css';

// ── Enums mapping ──────────────────────────────────────────────────────────────
const MATERIAL_LABEL = {
  GO_SOI:'Gỗ Sồi', GO_TAN_BI:'Gỗ Tần Bì', GO_OC_CHO:'Gỗ Óc Chó',
  INOX:'Inox', NHOM:'Nhôm', MARBLE:'Đá Marble',
  GRANITE:'Đá Granite', KINH:'Kính', VAI:'Vải',
};
const COLOR_META = {
  TRANG:       { label:'Trắng',       hex:'#f5f5f0' },
  BE:          { label:'Be',           hex:'#e8dcc8' },
  XAM_NHAT:    { label:'Xám nhạt',    hex:'#c8c8c8' },
  XAM_DAM:     { label:'Xám đậm',     hex:'#707070' },
  DEN:         { label:'Đen',          hex:'#1a1a1a' },
  NAU_GO_NHAT: { label:'Nâu gỗ nhạt', hex:'#c4a882' },
  NAU_GO_DAM:  { label:'Nâu gỗ đậm',  hex:'#8b7355' },
};

function fmtPrice(p) {
  return p ? Number(p).toLocaleString('vi-VN') + 'đ' : '—';
}

// ── StarRating display ─────────────────────────────────────────────────────────
function StarDisplay({ value = 0, size = 18 }) {
  return (
    <span className="star-display">
      {[1,2,3,4,5].map(i => {
        const fill = Math.min(1, Math.max(0, value - (i - 1)));
        return (
          <span key={i} className="star-wrap" style={{ width: size, height: size }}>
            <svg width={size} height={size} viewBox="0 0 24 24">
              <defs>
                <linearGradient id={`g${i}`}>
                  <stop offset={`${fill*100}%`} stopColor="#c4a353"/>
                  <stop offset={`${fill*100}%`} stopColor="#e0d5c5"/>
                </linearGradient>
              </defs>
              <polygon
                points="12,2 15.09,8.26 22,9.27 17,14.14 18.18,21.02 12,17.77 5.82,21.02 7,14.14 2,9.27 8.91,8.26"
                fill={`url(#g${i})`} stroke="#c4a353" strokeWidth="0.5"
              />
            </svg>
          </span>
        );
      })}
    </span>
  );
}

// ── Interactive star picker ────────────────────────────────────────────────────
function StarPicker({ value, onChange }) {
  const [hovered, setHovered] = useState(0);
  return (
    <span className="star-picker">
      {[1,2,3,4,5].map(i => (
        <button key={i} type="button"
          className={`star-pick-btn ${i <= (hovered || value) ? 'lit' : ''}`}
          onMouseEnter={() => setHovered(i)}
          onMouseLeave={() => setHovered(0)}
          onClick={() => onChange(i)}
        >★</button>
      ))}
    </span>
  );
}

// ── Main component ─────────────────────────────────────────────────────────────
export default function ProductDetailPage({ productId = 1 }) {
  const [product,  setProduct]  = useState(null);
  const [variants, setVariants] = useState([]);
  const [reviews,  setReviews]  = useState([]);
  const [loading,  setLoading]  = useState(true);

  // Selection state
  const [selectedColor,    setColor]    = useState(null);
  const [selectedMaterial, setMaterial] = useState(null);
  const [selectedVariant,  setVariant]  = useState(null);
  const [quantity, setQty]              = useState(1);

  // Gallery state
  const [activeImg,  setActiveImg]  = useState(0);
  const [zoomStyle,  setZoomStyle]  = useState({});
  const [zoomed,     setZoomed]     = useState(false);
  const imgRef = useRef(null);

  // Cart feedback
  const [cartStatus, setCartStatus] = useState('idle'); // idle | adding | added | error

  // Review form
  const [reviewForm,    setReviewForm]    = useState({ star: 0, comment: '' });
  const [submitStatus,  setSubmitStatus]  = useState('idle');
  const [reviewPage,    setReviewPage]    = useState(0);
  const [reviewTotal,   setReviewTotal]   = useState(0);

  // ── Fetch product + variants + reviews ──────────────────────────────────────
  useEffect(() => {
    const id = productId || window.location.pathname.split('/').pop();
    Promise.all([
      productService.getProductByProductId(id),
      productService.getProductVariants(id),
      apiClient.get(`/products/${id}/ratings`, { params: { page: 0, size: 5 } }),
    ]).then(([pRes, vRes, rRes]) => {
      setProduct(pRes.data.data);
      const vList = vRes.data.data?.content ?? [];
      setVariants(vList.filter(v => v.isActive));
      const rData = rRes.data.data;
      setReviews(rData.content ?? []);
      setReviewTotal(rData.totalElements ?? 0);
    }).catch(console.error)
      .finally(() => setLoading(false));
  }, [productId]);

  // ── Derived: available colors / materials based on current filter ────────────
  const availColors    = [...new Set(variants.map(v => v.color).filter(Boolean))];
  const availMaterials = [...new Set(
    variants.filter(v => !selectedColor || v.color === selectedColor)
            .map(v => v.material).filter(Boolean)
  )];

  // ── Auto-select variant when color+material are set ──────────────────────────
  useEffect(() => {
    if (!selectedColor && !selectedMaterial) { setVariant(null); return; }
    const match = variants.find(v =>
      (!selectedColor    || v.color    === selectedColor) &&
      (!selectedMaterial || v.material === selectedMaterial)
    );
    setVariant(match || null);
    if (match) setQty(1);
  }, [selectedColor, selectedMaterial, variants]);

  // ── All images from all variants ─────────────────────────────────────────────
  const allImages = variants.flatMap(v =>
    (v.images ?? []).map(img => ({ url: img.url, variantId: v.id }))
  );
  // fallback nếu không có ảnh
  if (allImages.length === 0 && product?.thumbnail) {
    allImages.push({ url: product.thumbnail, variantId: null });
  }

  // ── Image zoom on hover ───────────────────────────────────────────────────────
  const handleMouseMove = (e) => {
    const rect = imgRef.current?.getBoundingClientRect();
    if (!rect) return;
    const x = ((e.clientX - rect.left) / rect.width) * 100;
    const y = ((e.clientY - rect.top)  / rect.height) * 100;
    setZoomStyle({ transformOrigin: `${x}% ${y}%`, transform: 'scale(2)' });
  };

  // ── Add to cart ───────────────────────────────────────────────────────────────
  const handleAddToCart = async () => {
    if (!selectedVariant || cartStatus === 'adding') return;
    try {
      setCartStatus('adding');
      await apiClient.post('/cart/items', {
        productVariantId: selectedVariant.id,
        quantity,
      });
      setCartStatus('added');
      setTimeout(() => setCartStatus('idle'), 2500);
    } catch {
      setCartStatus('error');
      setTimeout(() => setCartStatus('idle'), 3000);
    }
  };

  // ── Buy now ───────────────────────────────────────────────────────────────────
  const handleBuyNow = async () => {
    await handleAddToCart();
    window.location.href = '/cart';
  };

  // ── Submit review ─────────────────────────────────────────────────────────────
  const handleSubmitReview = async (e) => {
    e.preventDefault();
    if (!reviewForm.star || !reviewForm.comment.trim()) return;
    try {
      setSubmitStatus('loading');
      const id = productId || window.location.pathname.split('/').pop();
      await apiClient.post(`/products/${id}/ratings`, reviewForm);
      setSubmitStatus('success');
      setReviewForm({ star: 0, comment: '' });
      // reload reviews
      const rRes = await apiClient.get(`/products/${id}/ratings`, { params: { page: 0, size: 5 } });
      const rData = rRes.data.data;
      setReviews(rData.content ?? []);
      setReviewTotal(rData.totalElements ?? 0);
      setReviewPage(0);
    } catch {
      setSubmitStatus('error');
    } finally {
      setTimeout(() => setSubmitStatus('idle'), 3000);
    }
  };

  // ── Load more reviews ──────────────────────────────────────────────────────────
  const loadMoreReviews = async () => {
    const id = productId || window.location.pathname.split('/').pop();
    const nextPage = reviewPage + 1;
    const rRes = await apiClient.get(`/products/${id}/ratings`, {
      params: { page: nextPage, size: 5 }
    });
    setReviews(prev => [...prev, ...(rRes.data.data.content ?? [])]);
    setReviewPage(nextPage);
  };

  // ── Render ────────────────────────────────────────────────────────────────────
  if (loading) return (
    <div className="pdp-loading">
      <div className="pdp-spinner"/>
      <span>Đang tải sản phẩm...</span>
    </div>
  );
  if (!product) return (
    <div className="pdp-loading">
      <span>Không tìm thấy sản phẩm</span>
    </div>
  );

  const activeVariantImages = selectedVariant
    ? (selectedVariant.images ?? []).map(i => i.url)
    : allImages.map(i => i.url);

  const displayImages = activeVariantImages.length ? activeVariantImages : ['/placeholder.jpg'];
  const safeActive    = Math.min(activeImg, displayImages.length - 1);

  return (
    <div className="pdp-page">
      {/* Breadcrumb */}
      <nav className="pdp-breadcrumb">
        <a href="/">Trang chủ</a><span>/</span>
        <a href="/products">Sản phẩm</a><span>/</span>
        <span>{product.name}</span>
      </nav>

      {/* ── MAIN SECTION ──────────────────────────────────────────── */}
      <section className="pdp-main">

        {/* Gallery */}
        <div className="pdp-gallery">
          {/* Thumbnails */}
          <div className="pdp-thumbs">
            {displayImages.map((url, i) => (
              <button key={i}
                className={`pdp-thumb ${safeActive === i ? 'active' : ''}`}
                onClick={() => setActiveImg(i)}
              >
                <img src={url} alt={`${product.name} ${i+1}`}/>
              </button>
            ))}
          </div>

          {/* Main image */}
          <div className="pdp-main-img-wrap"
            onMouseMove={handleMouseMove}
            onMouseEnter={() => setZoomed(true)}
            onMouseLeave={() => { setZoomed(false); setZoomStyle({}); }}
          >
            <img
              ref={imgRef}
              src={displayImages[safeActive]}
              alt={product.name}
              className="pdp-main-img"
              style={zoomed ? zoomStyle : {}}
            />
            <span className="pdp-zoom-hint">🔍 Di chuột để phóng to</span>

            {/* Nav arrows */}
            {displayImages.length > 1 && (
              <>
                <button className="pdp-img-arrow left"
                  onClick={() => setActiveImg(i => Math.max(0, i - 1))}
                  disabled={safeActive === 0}
                >‹</button>
                <button className="pdp-img-arrow right"
                  onClick={() => setActiveImg(i => Math.min(displayImages.length - 1, i + 1))}
                  disabled={safeActive === displayImages.length - 1}
                >›</button>
              </>
            )}

            {/* Dot indicators */}
            <div className="pdp-img-dots">
              {displayImages.map((_, i) => (
                <button key={i}
                  className={`pdp-dot ${safeActive === i ? 'active' : ''}`}
                  onClick={() => setActiveImg(i)}
                />
              ))}
            </div>
          </div>
        </div>

        {/* Info panel */}
        <div className="pdp-info">
          {/* Category + badges */}
          <div className="pdp-top-meta">
            <span className="pdp-category">{product.category?.name}</span>
            {product.purchaseCount > 0 && (
              <span className="pdp-badge-sold">🛒 {product.purchaseCount.toLocaleString()} lượt mua</span>
            )}
          </div>

          <h1 className="pdp-product-name">{product.name}</h1>

          {/* Rating row */}
          <div className="pdp-rating-row">
            <StarDisplay value={product.regardStar ?? 0}/>
            <span className="pdp-rating-value">{(product.regardStar ?? 0).toFixed(1)}</span>
            <span className="pdp-rating-count">({reviewTotal} đánh giá)</span>
            <span className="pdp-divider">|</span>
            <span className="pdp-instock-label">
              {product.inStockCount > 0
                ? <span className="instock">✓ Còn {product.inStockCount} sản phẩm</span>
                : <span className="outstock">✗ Hết hàng</span>}
            </span>
          </div>

          {/* Price */}
          <div className="pdp-price-block">
            {selectedVariant ? (
              <span className="pdp-price-main">{fmtPrice(selectedVariant.price)}</span>
            ) : (
              <>
                <span className="pdp-price-main">{fmtPrice(product.minPrice)}</span>
                {product.maxPrice && product.maxPrice !== product.minPrice && (
                  <span className="pdp-price-range">– {fmtPrice(product.maxPrice)}</span>
                )}
              </>
            )}
          </div>

          <div className="pdp-divider-line"/>

          {/* ── Color selector ── */}
          {availColors.length > 0 && (
            <div className="pdp-option-group">
              <div className="pdp-option-label">
                <span>Màu sắc</span>
                {selectedColor && (
                  <span className="pdp-selected-val">
                    {COLOR_META[selectedColor]?.label}
                  </span>
                )}
              </div>
              <div className="pdp-color-row">
                {availColors.map(c => {
                  const meta = COLOR_META[c] ?? { label: c, hex: '#ccc' };
                  const isActive = selectedColor === c;
                  return (
                    <button key={c}
                      title={meta.label}
                      className={`pdp-color-btn ${isActive ? 'active' : ''}`}
                      onClick={() => {
                        setColor(prev => prev === c ? null : c);
                        setMaterial(null);
                      }}
                    >
                      <span className="pdp-color-swatch"
                        style={{ background: meta.hex }}/>
                      <span className="pdp-color-name">{meta.label}</span>
                    </button>
                  );
                })}
              </div>
            </div>
          )}

          {/* ── Material selector ── */}
          {availMaterials.length > 0 && (
            <div className="pdp-option-group">
              <div className="pdp-option-label">
                <span>Chất liệu</span>
                {selectedMaterial && (
                  <span className="pdp-selected-val">
                    {MATERIAL_LABEL[selectedMaterial]}
                  </span>
                )}
              </div>
              <div className="pdp-material-row">
                {availMaterials.map(m => (
                  <button key={m}
                    className={`pdp-material-chip ${selectedMaterial === m ? 'active' : ''}`}
                    onClick={() => setMaterial(prev => prev === m ? null : m)}
                  >
                    {MATERIAL_LABEL[m] ?? m}
                  </button>
                ))}
              </div>
            </div>
          )}

          {/* ── Variant detail box ── */}
          {selectedVariant && (
            <div className="pdp-variant-box">
              <div className="pdp-variant-box-title">📦 Phiên bản đã chọn</div>
              <div className="pdp-variant-specs">
                {selectedVariant.length && (
                  <div className="pdp-spec-item">
                    <span className="spec-label">Kích thước</span>
                    <span className="spec-val">
                      {selectedVariant.length} × {selectedVariant.width} × {selectedVariant.height} cm
                    </span>
                  </div>
                )}
                <div className="pdp-spec-item">
                  <span className="spec-label">Tồn kho</span>
                  <span className={`spec-val ${selectedVariant.inStock > 5 ? 'ok' : selectedVariant.inStock > 0 ? 'low' : 'out'}`}>
                    {selectedVariant.inStock > 0 ? `${selectedVariant.inStock} sản phẩm` : 'Hết hàng'}
                  </span>
                </div>
                <div className="pdp-spec-item">
                  <span className="spec-label">Đơn giá</span>
                  <span className="spec-val price">{fmtPrice(selectedVariant.price)}</span>
                </div>
              </div>
            </div>
          )}

          {/* ── Quantity + CTA ── */}
          <div className="pdp-action-row">
            <div className="pdp-qty-control">
              <button className="pdp-qty-btn"
                disabled={quantity <= 1}
                onClick={() => setQty(q => q - 1)}>−</button>
              <span className="pdp-qty-val">{quantity}</span>
              <button className="pdp-qty-btn"
                disabled={!selectedVariant || quantity >= selectedVariant.inStock}
                onClick={() => setQty(q => q + 1)}>+</button>
            </div>

            <div className="pdp-cta-btns">
              <button
                className={`pdp-btn-cart ${cartStatus}`}
                onClick={handleAddToCart}
                disabled={!selectedVariant || !selectedVariant.inStock || cartStatus === 'adding'}
              >
                {cartStatus === 'adding' && <span className="btn-spinner"/>}
                {cartStatus === 'added'  && '✓ '}
                {cartStatus === 'error'  && '✗ '}
                {cartStatus === 'adding' ? 'Đang thêm...'
                  : cartStatus === 'added' ? 'Đã thêm vào giỏ!'
                  : cartStatus === 'error' ? 'Lỗi, thử lại'
                  : (
                    <>
                      <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                           stroke="currentColor" strokeWidth="2">
                        <circle cx="9" cy="21" r="1"/>
                        <circle cx="20" cy="21" r="1"/>
                        <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
                      </svg>
                      Thêm vào giỏ
                    </>
                  )}
              </button>

              <button
                className="pdp-btn-buy"
                onClick={handleBuyNow}
                disabled={!selectedVariant || !selectedVariant.inStock}
              >
                Mua ngay
              </button>
            </div>
          </div>

          {!selectedVariant && (
            <p className="pdp-select-hint">
              ← Vui lòng chọn màu sắc {availMaterials.length > 1 && 'và chất liệu'} để xem giá & thêm vào giỏ
            </p>
          )}

          <div className="pdp-divider-line"/>

          {/* Description */}
          <div className="pdp-description">
            <h3>Mô tả sản phẩm</h3>
            <p>{product.description}</p>
          </div>

          {/* Perks */}
          <div className="pdp-perks">
            {[
              { icon:'🚚', text:'Miễn phí vận chuyển đơn trên 10 triệu' },
              { icon:'💯', text:'Bảo hành chính hãng đến 5 năm'         },
              { icon:'🔄', text:'Đổi trả trong 30 ngày'                  },
              { icon:'🔒', text:'Thanh toán bảo mật 100%'                },
            ].map((p,i) => (
              <div key={i} className="pdp-perk">
                <span>{p.icon}</span><span>{p.text}</span>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── REVIEWS SECTION ───────────────────────────────────────── */}
      <section className="pdp-reviews">
        <div className="pdp-reviews-inner">

          {/* Summary */}
          <div className="pdp-review-summary">
            <div className="pdp-review-big-score">
              <span className="big-number">{(product.regardStar ?? 0).toFixed(1)}</span>
              <StarDisplay value={product.regardStar ?? 0} size={28}/>
              <span className="review-count-label">{reviewTotal} đánh giá</span>
              <span className="purchase-count-label">
                🛒 {product.purchaseCount?.toLocaleString() ?? 0} lượt mua
              </span>
            </div>

            {/* Rating bars */}
            <div className="pdp-rating-bars">
              {[5,4,3,2,1].map(star => {
                const count = reviews.filter(r => Math.round(r.star) === star).length;
                const pct = reviewTotal > 0
                  ? Math.round((count / reviewTotal) * 100)
                  : 0;
                return (
                  <div key={star} className="rating-bar-row">
                    <span className="bar-star">{star} ★</span>
                    <div className="bar-track">
                      <div className="bar-fill" style={{ width: `${pct}%` }}/>
                    </div>
                    <span className="bar-pct">{pct}%</span>
                  </div>
                );
              })}
            </div>
          </div>

          {/* Write a review */}
          <div className="pdp-write-review">
            <h3>Viết đánh giá của bạn</h3>
            <form onSubmit={handleSubmitReview} className="pdp-review-form">
              <div className="pdp-form-row">
                <label>Đánh giá</label>
                <StarPicker
                  value={reviewForm.star}
                  onChange={v => setReviewForm(f => ({ ...f, star: v }))}
                />
              </div>
              <div className="pdp-form-row">
                <label>Nhận xét</label>
                <textarea
                  placeholder="Chia sẻ trải nghiệm của bạn về sản phẩm này..."
                  value={reviewForm.comment}
                  onChange={e => setReviewForm(f => ({ ...f, comment: e.target.value }))}
                  rows={4}
                  required
                />
              </div>
              <button
                type="submit"
                className={`pdp-submit-review ${submitStatus}`}
                disabled={!reviewForm.star || submitStatus === 'loading'}
              >
                {submitStatus === 'loading' ? 'Đang gửi...'
                  : submitStatus === 'success' ? '✓ Đã gửi đánh giá!'
                  : submitStatus === 'error'   ? '✗ Lỗi, thử lại'
                  : 'Gửi đánh giá'}
              </button>
            </form>
          </div>

          {/* Review list */}
          <div className="pdp-review-list">
            <h3>Đánh giá từ khách hàng ({reviewTotal})</h3>

            {reviews.length === 0 ? (
              <div className="pdp-no-review">
                <span>💬</span>
                <p>Chưa có đánh giá nào. Hãy là người đầu tiên!</p>
              </div>
            ) : (
              <>
                {reviews.map((r, i) => (
                  <div key={r.id ?? i} className="pdp-review-item">
                    <div className="review-avatar">
                      {(r.user?.name ?? r.userName ?? 'K')[0].toUpperCase()}
                    </div>
                    <div className="review-content">
                      <div className="review-header">
                        <span className="review-author">
                          {r.user?.name ?? r.userName ?? 'Khách hàng'}
                        </span>
                        <StarDisplay value={r.star} size={15}/>
                        <span className="review-date">
                          {r.createdAt
                            ? new Date(r.createdAt).toLocaleDateString('vi-VN')
                            : ''}
                        </span>
                      </div>
                      <p className="review-text">{r.comment}</p>
                    </div>
                  </div>
                ))}

                {reviews.length < reviewTotal && (
                  <button className="pdp-load-more" onClick={loadMoreReviews}>
                    Xem thêm đánh giá ({reviewTotal - reviews.length} còn lại)
                  </button>
                )}
              </>
            )}
          </div>
        </div>
      </section>
    </div>
  );
}