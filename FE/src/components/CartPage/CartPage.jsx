import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import cartService from "../../api/CartService";
import promotionService from "../../api/PromotionService";
import NavBar from "../Navbar/NavBar";
import "./CartPage.css";

/* ── Helpers ─────────────────────────────────────────────────────── */
const fmt = (n) => Number(n ?? 0).toLocaleString("vi-VN") + "đ";

const MATERIAL_LABEL = {
  GO_SOI: "Gỗ Sồi",
  GO_TAN_BI: "Gỗ Tần Bì",
  GO_OC_CHO: "Gỗ Óc Chó",
  INOX: "Inox",
  NHOM: "Nhôm",
  MARBLE: "Đá Marble",
  GRANITE: "Đá Granite",
  KINH: "Kính",
  VAI: "Vải",
};
const COLOR_META = {
  TRANG: { label: "Trắng", hex: "#f5f5f0" },
  BE: { label: "Be", hex: "#e8dcc8" },
  XAM_NHAT: { label: "Xám nhạt", hex: "#c8c8c8" },
  XAM_DAM: { label: "Xám đậm", hex: "#707070" },
  DEN: { label: "Đen", hex: "#1a1a1a" },
  NAU_GO_NHAT: { label: "Nâu gỗ nhạt", hex: "#c4a882" },
  NAU_GO_DAM: { label: "Nâu gỗ đậm", hex: "#8b7355" },
};

/* ── Main Component ──────────────────────────────────────────────── */
export default function CartPage() {
  //   const navigate = useNavigate();

  const [cart, setCart] = useState(null);
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState({}); // { itemId: true }

  const [voucherCode, setVoucherCode] = useState("");
  const [appliedVoucher, setApplied] = useState(null);
  const [voucherStatus, setVStatus] = useState("idle"); // idle | checking | valid | invalid

  const [selectedItems, setSelected] = useState(new Set());

  const [availableVouchers, setAvailableVouchers] = useState([]);
  const [voucherLoading, setVoucherLoading] = useState(false);

  /* ── Fetch cart ──────────────────────────────────────────────── */
  const fetchCart = async () => {
    try {
      setLoading(true);
      const cartId = localStorage.getItem("cart_id");
      if (!cartId) {
        setItems([]);
        setLoading(false);
        return;
      }

      const res = await cartService.getUserCart();
      console.log("Cart:", res);
      setCart(res.data);
      setItems(res.data.cart_items ?? []);

      // Auto-select all items có inStock
      const selectableIds = (res.data.items ?? [])
        .filter((item) => item.variant?.inStock > 0)
        .map((item) => item.id);
      setSelected(new Set(selectableIds));
    } catch (err) {
      console.error("Fetch cart error:", err);
      setItems([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCart();
  }, []);

  /* ── Update quantity ─────────────────────────────────────────── */
  const updateQty = async (itemId, newQty) => {
    console.log("Update");
    if (newQty < 1) return;
    const item = items.find((i) => i.variant.id === itemId);
    if (!item) return;
    if (newQty > item.variant.inStock) {
      alert(`Chỉ còn ${item.variant.inStock} sản phẩm trong kho`);
      return;
    }

    try {
      setUpdating((prev) => ({ ...prev, [itemId]: true }));
      const cartItemData = {
        cart_id: localStorage.getItem("cart_id"),
        variant_id: itemId,
        quantity: newQty,
      };
      console.log(cartItemData);
      const response = await cartService.updateCartItem(cartItemData);
      console.log("Update cart response:", response);
      await fetchCart();
    } catch (err) {
      console.error("Update qty error:", err);
    } finally {
      setUpdating((prev) => ({ ...prev, [itemId]: false }));
    }
  };

  // Load voucher user có
  useEffect(() => {
    const fetchVouchers = async () => {
      setVoucherLoading(true);
      const res = await promotionService.getActivePromotionForUser();
      console.log("Promotion response:", res);
      setAvailableVouchers(res.data);
      setVoucherLoading(false);
    };

    fetchVouchers();
  }, []);

  /* ── Remove item ─────────────────────────────────────────────── */
  const removeItem = async (itemId) => {
    if (!confirm("Xóa sản phẩm khỏi giỏ hàng?")) return;
    try {
      setUpdating((prev) => ({ ...prev, [itemId]: true }));
      await cartService.removeCartItem(itemId);
      await fetchCart();
    } catch (err) {
      console.error("Remove item error:", err);
      setUpdating((prev) => ({ ...prev, [itemId]: false }));
    }
  };

  /* ── Toggle select ───────────────────────────────────────────── */
  const toggleSelect = (itemId) => {
    setSelected((prev) => {
      const next = new Set(prev);
      if (next.has(itemId)) next.delete(itemId);
      else next.add(itemId);
      return next;
    });
  };

  const toggleSelectAll = () => {
    console.log("Select All");
    const selectableIds = items
      .filter((item) => item.variant?.inStock > 0)
      .map((item) => item.id);

    if (selectedItems.size === selectableIds.length) {
      setSelected(new Set());
    } else {
      setSelected(new Set(selectableIds));
    }
  };

  /* ── Apply voucher ───────────────────────────────────────────── */
  const applyVoucher = async () => {
    if (!voucherCode.trim()) return;
    try {
      setVStatus("checking");
        const res = await voucherService.checkVoucher(voucherCode);
        if (res.data.valid) {
          setApplied(res.data.voucher);
          setVStatus('valid');
        } else {
          setVStatus('invalid');
          setTimeout(() => setVStatus('idle'), 3000);
        }
    } catch {
      setVStatus("invalid");
      setTimeout(() => setVStatus("idle"), 3000);
    }
  };

  const removeVoucher = () => {
    setApplied(null);
    setVoucherCode("");
    setVStatus("idle");
  };

  /* ── Calculations ────────────────────────────────────────────── */
  const selectedItemsList = items.filter((item) => selectedItems.has(item.id));

  const subtotal = selectedItemsList.reduce(
    (sum, item) => sum + item.variant.price * item.quantity,
    0
  );

  const shipping = subtotal > 10_000_000 ? 0 : 50_000;

  let discount = 0;
  if (appliedVoucher) {
    if (appliedVoucher.discount_type === "PERCENTAGE") {
      discount = (subtotal * appliedVoucher.discount_value) / 100;
      if (appliedVoucher.max_discount) {
        discount = Math.min(discount, appliedVoucher.max_discount);
      }
    } else {
      discount = appliedVoucher.discount_value;
    }
  }

  const total = subtotal + shipping - discount;

  /* ── Checkout ────────────────────────────────────────────────── */
  const handleCheckout = () => {
    if (selectedItems.size === 0) {
      alert("Vui lòng chọn ít nhất 1 sản phẩm để thanh toán");
      return;
    }

    // Chuyển sang trang checkout với selectedItems + voucher
    const checkoutData = {
      items: selectedItemsList.map((item) => ({
        variant_id: item.variant.id,
        quantity: item.quantity,
      })),
      voucher_code: appliedVoucher?.code,
    };

    localStorage.setItem("checkout_data", JSON.stringify(checkoutData));
    // navigate('/checkout');
  };

  /* ── Render ──────────────────────────────────────────────────── */
  if (loading)
    return (
      <div className="cart-loading">
        <div className="cart-spinner" />
        <span>Đang tải giỏ hàng...</span>
      </div>
    );

  return (
    <div className="cart-page">
      <NavBar />

      <div className="cart-container">
        {/* Header */}
        <div className="cart-header">
          <h1 className="cart-title">Giỏ hàng của bạn</h1>
          <span className="cart-count">{items.length} sản phẩm</span>
        </div>

        {items.length === 0 ? (
          /* Empty state */
          <div className="cart-empty">
            <div className="cart-empty-icon">🛒</div>
            <h2>Giỏ hàng trống</h2>
            <p>Hãy khám phá các sản phẩm tuyệt vời của chúng tôi!</p>
            {/* <button className="cart-empty-btn" onClick={() => navigate('/products')}>
              Khám phá ngay
            </button> */}
          </div>
        ) : (
          <div className="cart-content">
            {/* Left: Items list */}
            <div className="cart-items">
              {/* Select all header */}
              <div className="cart-select-all">
                <label className="cart-checkbox-wrap">
                  <input
                    type="checkbox"
                    checked={
                      selectedItems.size > 0 &&
                      selectedItems.size ===
                        items.filter((i) => i.variant?.inStock > 0).length
                    }
                    onChange={toggleSelectAll}
                  />
                  <span className="cart-checkbox-custom" />
                  <span className="cart-select-label">
                    Chọn tất cả (
                    {items.filter((i) => i.variant?.inStock > 0).length})
                  </span>
                </label>

                {selectedItems.size > 0 && (
                  <button
                    className="cart-delete-selected"
                    onClick={() => {
                      if (
                        confirm(`Xóa ${selectedItems.size} sản phẩm đã chọn?`)
                      ) {
                        Promise.all(
                          Array.from(selectedItems).map((id) =>
                            cartService.removeCartItem(id)
                          )
                        ).then(() => fetchCart());
                      }
                    }}
                  >
                    <svg
                      width="16"
                      height="16"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      strokeWidth="2"
                    >
                      <polyline points="3 6 5 6 21 6" />
                      <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
                      <line x1="10" y1="11" x2="10" y2="17" />
                      <line x1="14" y1="11" x2="14" y2="17" />
                    </svg>
                    Xóa đã chọn
                  </button>
                )}
              </div>

              {/* Item cards */}
              {items.map((item) => {
                const variant = item.variant;
                const isSelected = selectedItems.has(item.id);
                const isUpdating = updating[item.id];
                const outOfStock = !variant.inStock || variant.inStock === 0;

                return (
                  <div
                    key={item.variant.id}
                    className={`cart-item ${isSelected ? "selected" : ""} ${
                      outOfStock ? "out-of-stock" : ""
                    }`}
                  >
                    {/* Checkbox */}
                    <label className="cart-checkbox-wrap">
                      <input
                        type="checkbox"
                        checked={isSelected}
                        onChange={() => toggleSelect(item.id)}
                        disabled={outOfStock}
                      />
                      <span className="cart-checkbox-custom" />
                    </label>

                    {/* Image */}
                    <div className="cart-item-img">
                      <img
                        src={variant.images?.[0]?.url ?? "/placeholder.jpg"}
                        alt={variant.product_name}
                      />
                      {outOfStock && (
                        <span className="cart-out-badge">Hết hàng</span>
                      )}
                    </div>

                    {/* Info */}
                    <div className="cart-item-info">
                      <h3 className="cart-item-name">{item.product_name}</h3>

                      <div className="cart-item-attrs">
                        {variant.color && (
                          <span className="cart-attr">
                            <span
                              className="cart-attr-dot"
                              style={{
                                background:
                                  COLOR_META[variant.color]?.hex ?? "#ccc",
                              }}
                            />
                            {COLOR_META[variant.color]?.label ?? variant.color}
                          </span>
                        )}
                        {variant.material && (
                          <span className="cart-attr cart-attr-mat">
                            {MATERIAL_LABEL[variant.material] ??
                              variant.material}
                          </span>
                        )}
                        {variant.length && (
                          <span className="cart-attr cart-attr-size">
                            {variant.length}×{variant.width}×{variant.height} cm
                          </span>
                        )}
                      </div>

                      <div className="cart-item-stock">
                        {outOfStock ? (
                          <span className="stock-empty">✗ Hết hàng</span>
                        ) : variant.inStock <= 5 ? (
                          <span className="stock-low">
                            Chỉ còn {variant.inStock}
                          </span>
                        ) : (
                          <span className="stock-ok">Còn hàng</span>
                        )}
                      </div>
                    </div>

                    {/* Price */}
                    <div className="cart-item-price">
                      <span className="cart-price-unit">
                        {fmt(variant.price)}
                      </span>
                      <span className="cart-price-total">
                        = {fmt(variant.price * item.quantity)}
                      </span>
                    </div>

                    {/* Quantity control */}
                    <div className="cart-item-qty">
                      <button
                        className="cart-qty-btn"
                        type="button"
                        disabled={
                          item.quantity <= 1 || isUpdating || outOfStock
                        }
                        onClick={() => updateQty(variant.id, item.quantity - 1)}
                      >
                        −
                      </button>
                      <span className="cart-qty-val">
                        {isUpdating ? "..." : item.quantity}
                      </span>
                      <button
                        className="cart-qty-btn"
                        type="button"
                        disabled={
                          item.quantity >= variant.inStock ||
                          isUpdating ||
                          outOfStock
                        }
                        onClick={() => updateQty(variant.id, item.quantity + 1)}
                      >
                        +
                      </button>
                    </div>

                    {/* Remove */}
                    <button
                      className="cart-item-remove"
                      onClick={() => removeItem(item.id)}
                      disabled={isUpdating}
                      title="Xóa"
                    >
                      <svg
                        width="18"
                        height="18"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                      >
                        <polyline points="3 6 5 6 21 6" />
                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
                      </svg>
                    </button>
                  </div>
                );
              })}
            </div>

            {/* Right: Summary */}
            <div className="cart-summary">
              <div className="cart-summary-inner">
                <h3 className="cart-summary-title">Tóm tắt đơn hàng</h3>

                {/* Voucher */}
                {console.log(availableVouchers)}
                <div className="cart-voucher">
                  {availableVouchers.length > 0 && (
                    <label className="cart-voucher-label">
                      CHỌN MÃ GIẢM GIÁ
                    </label>
                  )}

                  {voucherLoading ? (
                    <div className="cart-voucher-loading">
                      <span>Đang tải voucher...</span>
                    </div>
                  ) : availableVouchers.length === 0 ? (
                    <div className="cart-no-voucher">
                      <span>Bạn không có voucher khả dụng</span>
                    </div>
                  ) : (
                    <div className="cart-voucher-grid">
                      {availableVouchers.map((voucher) => {
                        const isApplied =
                          appliedVoucher?.id === voucher.promotionId;

                        return (
                          <div
                            key={voucher.promotionId}
                            className={`cart-voucher-item ${
                              isApplied ? "applied" : ""
                            }`}
                            onClick={() =>
                              !isApplied && applyVoucher(voucher.promotionId)
                            }
                          >
                            <div className="voucher-content">
                              <div className="voucher-header">
                                <span className="voucher-tag">MÃ GIẢM GIÁ {voucher.productName ?? ''}</span>
                                {isApplied && (
                                  <span className="voucher-badge">
                                    ĐÃ ÁP DỤNG
                                  </span>
                                )}
                              </div>

                              <div className="voucher-code">{voucher.code}</div>

                              <div className="voucher-info">
                                <div className="voucher-discount">
                                  Giảm {voucher.discountValue}% {voucher.type=="ORDER" ? "giá trị đơn hàng" : ""}
                                </div>
                                <div className="voucher-expiry">
                                  HS: 31/12/2024
                                </div>
                              </div>

                              <button
                                className={`voucher-action ${
                                  isApplied ? "applied" : ""
                                }`}
                                disabled={isApplied}
                              >
                                {isApplied ? "ĐÃ ÁP DỤNG" : "ÁP DỤNG"}
                              </button>
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  )}
                </div>

                {/* Breakdown */}
                <div className="cart-breakdown">
                  <div className="cart-breakdown-row">
                    <span>Tạm tính ({selectedItems.size} sản phẩm)</span>
                    <span>{fmt(subtotal)}</span>
                  </div>
                  <div className="cart-breakdown-row">
                    <span>Phí vận chuyển</span>
                    <span className={shipping === 0 ? "free" : ""}>
                      {shipping === 0 ? "Miễn phí" : fmt(shipping)}
                    </span>
                  </div>
                  {discount > 0 && (
                    <div className="cart-breakdown-row discount">
                      <span>Giảm giá</span>
                      <span>−{fmt(discount)}</span>
                    </div>
                  )}
                </div>

                {/* Total */}
                <div className="cart-total">
                  <span>Tổng cộng</span>
                  <span className="cart-total-price">{fmt(total)}</span>
                </div>

                {/* Checkout button */}
                <button
                  className="cart-checkout-btn"
                  onClick={handleCheckout}
                  disabled={selectedItems.size === 0}
                >
                  {selectedItems.size === 0
                    ? "Chọn sản phẩm"
                    : `Đặt hàng (${selectedItems.size})`}
                </button>

                {/* Perks */}
                <div className="cart-perks">
                  <div className="cart-perk">
                    <svg
                      width="20"
                      height="20"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      strokeWidth="1.8"
                    >
                      <circle cx="12" cy="12" r="10" />
                      <polyline points="12 6 12 12 16 14" />
                    </svg>
                    <span>Giao hàng trong 10-15 ngày</span>
                  </div>
                  <div className="cart-perk">
                    <svg
                      width="20"
                      height="20"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      strokeWidth="1.8"
                    >
                      <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
                    </svg>
                    <span>Thanh toán an toàn 100%</span>
                  </div>
                  <div className="cart-perk">
                    <svg
                      width="20"
                      height="20"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      strokeWidth="1.8"
                    >
                      <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
                      <polyline points="9 22 9 12 15 12 15 22" />
                    </svg>
                    <span>Đổi trả trong 30 ngày</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
