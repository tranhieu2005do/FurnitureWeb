import { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import orderService from '../../api/orderService';
import './OrderSuccessPage.css';

/* ── Helpers ─────────────────────────────────────────────────────── */
const fmt = (n) => Number(n ?? 0).toLocaleString('vi-VN') + 'đ';

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

const PAYMENT_METHOD = {
  COD: 'Thanh toán khi nhận hàng',
  BANK_TRANSFER: 'Chuyển khoản ngân hàng',
  MOMO: 'Ví MoMo',
  VNPAY: 'VNPay',
};

const ORDER_STATUS = {
  PENDING: 'Chờ xác nhận',
  CONFIRMED: 'Đã xác nhận',
  PROCESSING: 'Đang xử lý',
  SHIPPING: 'Đang giao hàng',
  DELIVERED: 'Đã giao hàng',
  CANCELLED: 'Đã hủy',
};

/* ── Confetti Effect ─────────────────────────────────────────────── */
function Confetti() {
  const colors = ['#8b7355', '#c4a882', '#e8dcc8', '#f8f5f0', '#4CAF50', '#FFC107'];
  const pieces = Array.from({ length: 50 }, (_, i) => ({
    id: i,
    left: Math.random() * 100,
    delay: Math.random() * 0.5,
    duration: 2 + Math.random() * 2,
    color: colors[Math.floor(Math.random() * colors.length)],
  }));

  return (
    <div className="confetti-container">
      {pieces.map(p => (
        <div
          key={p.id}
          className="confetti-piece"
          style={{
            left: `${p.left}%`,
            animationDelay: `${p.delay}s`,
            animationDuration: `${p.duration}s`,
            background: p.color,
          }}
        />
      ))}
    </div>
  );
}

/* ── Main Component ──────────────────────────────────────────────── */
export default function OrderSuccessPage() {
  const navigate = useNavigate();
  const location = useLocation();
  
  const [order, setOrder]     = useState(null);
  const [loading, setLoading] = useState(true);
  const [showConfetti, setShowConfetti] = useState(true);

  /* ── Fetch order ─────────────────────────────────────────────── */
  useEffect(() => {
    const fetchOrder = async () => {
      try {
        setLoading(true);
        
        // Lấy orderId từ URL query hoặc state
        const params = new URLSearchParams(location.search);
        const orderId = params.get('orderId') || location.state?.orderId;
        
        // if (!orderId) {
        //   navigate('/');
        //   return;
        // }

        const res = await orderService.getOrderById(orderId);
        setOrder(res.data);
      } catch (err) {
        console.error('Fetch order error:', err);
        // navigate('/login');
      } finally {
        setLoading(false);
      }
    };

    fetchOrder();

    // Tắt confetti sau 4s
    setTimeout(() => setShowConfetti(false), 4000);
  }, [location, navigate]);

  /* ── Loading ─────────────────────────────────────────────────── */
  if (loading) return (
    <div className="osp-loading">
      <div className="osp-spinner"/>
      <span>Đang tải thông tin đơn hàng...</span>
    </div>
  );

  if (!order) return null;

  /* ── Calculations ────────────────────────────────────────────── */
  const subtotal = order.items.reduce(
    (sum, item) => sum + (item.price * item.quantity), 0
  );
  const shipping = order.shipping_fee ?? 0;
  const discount = order.discount ?? 0;
  const total    = order.total_amount ?? (subtotal + shipping - discount);

  return (
    <div className="osp-page">
      
      {/* Confetti */}
      {showConfetti && <Confetti />}

      {/* Container */}
      <div className="osp-container">

        {/* ═══ SUCCESS HERO ═══ */}
        <div className="osp-hero">
          {/* Animated checkmark */}
          <div className="osp-checkmark-wrap">
            <svg className="osp-checkmark" viewBox="0 0 52 52">
              <circle className="osp-checkmark-circle" cx="26" cy="26" r="25" fill="none"/>
              <path className="osp-checkmark-check" fill="none" d="M14.1 27.2l7.1 7.2 16.7-16.8"/>
            </svg>
          </div>

          {/* Text */}
          <h1 className="osp-title">Đặt hàng thành công!</h1>
          <p className="osp-subtitle">
            Cảm ơn quý khách đã tin tưởng và đặt hàng tại cửa hàng chúng tôi
          </p>

          {/* Order code */}
          <div className="osp-order-code">
            <span className="osp-code-label">Mã đơn hàng</span>
            <span className="osp-code-value">#{order.id || order.order_code}</span>
          </div>

          {/* Status badge */}
          <div className="osp-status-badge">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none"
                 stroke="currentColor" strokeWidth="2">
              <circle cx="12" cy="12" r="10"/>
              <polyline points="12 6 12 12 16 14"/>
            </svg>
            <span>{ORDER_STATUS[order.status] || 'Chờ xác nhận'}</span>
          </div>
        </div>

        {/* ═══ ORDER DETAILS ═══ */}
        <div className="osp-content">

          {/* ── Customer info ──────────────────────────────────── */}
          <section className="osp-section">
            <h2 className="osp-section-title">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                   stroke="currentColor" strokeWidth="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
              Thông tin người nhận
            </h2>
            <div className="osp-info-grid">
              <div className="osp-info-item">
                <span className="osp-info-label">Họ tên</span>
                <span className="osp-info-value">{order.customer_name || order.shipping_address?.name}</span>
              </div>
              <div className="osp-info-item">
                <span className="osp-info-label">Số điện thoại</span>
                <span className="osp-info-value">{order.customer_phone || order.shipping_address?.phone}</span>
              </div>
              <div className="osp-info-item full">
                <span className="osp-info-label">Địa chỉ giao hàng</span>
                <span className="osp-info-value">
                  {order.shipping_address
                    ? `${order.shipping_address.street}, ${order.shipping_address.ward}, ${order.shipping_address.district}, ${order.shipping_address.city}`
                    : order.address}
                </span>
              </div>
              {order.note && (
                <div className="osp-info-item full">
                  <span className="osp-info-label">Ghi chú</span>
                  <span className="osp-info-value">{order.note}</span>
                </div>
              )}
            </div>
          </section>

          {/* ── Payment method ──────────────────────────────────── */}
          <section className="osp-section">
            <h2 className="osp-section-title">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                   stroke="currentColor" strokeWidth="2">
                <rect x="1" y="4" width="22" height="16" rx="2"/>
                <line x1="1" y1="10" x2="23" y2="10"/>
              </svg>
              Phương thức thanh toán
            </h2>
            <div className="osp-payment-method">
              {PAYMENT_METHOD[order.payment_method] || order.payment_method}
            </div>
          </section>

          {/* ── Items list ──────────────────────────────────────── */}
          <section className="osp-section">
            <h2 className="osp-section-title">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                   stroke="currentColor" strokeWidth="2">
                <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
              </svg>
              Chi tiết đơn hàng ({order.items?.length || 0} sản phẩm)
            </h2>

            <div className="osp-items">
              {order.items?.map((item, index) => (
                <div key={index} className="osp-item">
                  
                  {/* Image */}
                  <div className="osp-item-img">
                    <img src={item.variant?.images?.[0]?.url || item.image || '/placeholder.jpg'}
                         alt={item.product_name || item.name} />
                  </div>

                  {/* Info */}
                  <div className="osp-item-info">
                    <h3 className="osp-item-name">
                      {item.product_name || item.name}
                    </h3>

                    {/* Variant attributes */}
                    <div className="osp-item-attrs">
                      {item.variant?.color && (
                        <span className="osp-attr">
                          <span className="osp-attr-dot"
                            style={{ background: COLOR_META[item.variant.color]?.hex ?? '#ccc' }}/>
                          {COLOR_META[item.variant.color]?.label ?? item.variant.color}
                        </span>
                      )}
                      {item.variant?.material && (
                        <span className="osp-attr osp-attr-mat">
                          {MATERIAL_LABEL[item.variant.material] ?? item.variant.material}
                        </span>
                      )}
                      {item.variant?.length && (
                        <span className="osp-attr osp-attr-size">
                          📐 {item.variant.length}×{item.variant.width}×{item.variant.height} cm
                        </span>
                      )}
                    </div>

                    {/* Quantity × Price */}
                    <div className="osp-item-qty-price">
                      <span className="osp-qty">SL: {item.quantity}</span>
                      <span className="osp-unit-price">{fmt(item.price)}/sp</span>
                    </div>
                  </div>

                  {/* Total */}
                  <div className="osp-item-total">
                    {fmt(item.price * item.quantity)}
                  </div>
                </div>
              ))}
            </div>
          </section>

          {/* ── Price breakdown ─────────────────────────────────── */}
          <section className="osp-section osp-summary">
            <div className="osp-breakdown">
              <div className="osp-breakdown-row">
                <span>Tạm tính</span>
                <span>{fmt(subtotal)}</span>
              </div>
              <div className="osp-breakdown-row">
                <span>Phí vận chuyển</span>
                <span className={shipping === 0 ? 'free' : ''}>
                  {shipping === 0 ? 'Miễn phí' : fmt(shipping)}
                </span>
              </div>
              {discount > 0 && (
                <div className="osp-breakdown-row discount">
                  <span>Giảm giá{order.voucher_code && ` (${order.voucher_code})`}</span>
                  <span>−{fmt(discount)}</span>
                </div>
              )}
            </div>

            <div className="osp-total">
              <span>Tổng cộng</span>
              <span className="osp-total-value">{fmt(total)}</span>
            </div>
          </section>

          {/* ── Expected delivery ───────────────────────────────── */}
          {order.estimated_delivery && (
            <div className="osp-delivery-info">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                   stroke="currentColor" strokeWidth="2">
                <rect x="1" y="3" width="15" height="13"/>
                <polygon points="16 8 20 8 23 11 23 16 16 16 16 8"/>
                <circle cx="5.5" cy="18.5" r="2.5"/>
                <circle cx="18.5" cy="18.5" r="2.5"/>
              </svg>
              <div>
                <span className="osp-delivery-label">Dự kiến giao hàng</span>
                <span className="osp-delivery-date">
                  {new Date(order.estimated_delivery).toLocaleDateString('vi-VN', {
                    weekday: 'long',
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric',
                  })}
                </span>
              </div>
            </div>
          )}

          {/* ── Actions ─────────────────────────────────────────── */}
          <div className="osp-actions">
            <button className="osp-btn osp-btn-secondary"
              onClick={() => navigate('/orders')}>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none"
                   stroke="currentColor" strokeWidth="2">
                <polyline points="9 11 12 14 22 4"/>
                <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/>
              </svg>
              Xem đơn hàng của tôi
            </button>

            <button className="osp-btn osp-btn-primary"
              onClick={() => navigate('/')}>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none"
                   stroke="currentColor" strokeWidth="2">
                <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
                <polyline points="9 22 9 12 15 12 15 22"/>
              </svg>
              Về trang chủ
            </button>
          </div>

          {/* ── Thank you message ───────────────────────────────── */}
          <div className="osp-thank-you">
            <p className="osp-thank-title">✨ Cảm ơn quý khách! ✨</p>
            <p className="osp-thank-text">
              Chúng tôi sẽ liên hệ với quý khách trong thời gian sớm nhất để xác nhận đơn hàng. 
              Nếu có bất kỳ thắc mắc nào, vui lòng liên hệ hotline: <strong>1900 xxxx</strong>
            </p>
          </div>

        </div>

      </div>
    </div>
  );
}