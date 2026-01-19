import "./Navbar.css";

export const Navbar = () => {
  const handleSearch = () => {
    return;
  }
  return (
    <>
      {/* TOP BAR */}
      <div className="top-bar">
        <div className="top-left">
          <div className="top-item">
            <span className="icon">📞</span>
            <div>
              <small>HOTLINE</small>
              <strong>0974 933 486</strong>
            </div>
          </div>

          <div className="top-item">
            <span className="icon">🚚</span>
            <div>
              <small>GIAO HÀNG</small>
              <strong>TOÀN QUỐC</strong>
            </div>
          </div>

          <div className="top-item">
            <span className="icon">💰</span>
            <div>
              <small>THANH TOÁN</small>
              <strong>TẠI NHÀ</strong>
            </div>
          </div>
        </div>

        <div className="top-middle">
          <img
            src="/logo.png"
            alt="logo"
            className="logo"
          />
        </div>
        <div className="top-right">
          <input type="text" placeholder="Bạn tìm kiếm sản phẩm nào"></input>
          <button onClick={handleSearch}><label className="search-icon" htmlFor="search-input">🔍</label></button>
        </div>
      </div>

      {/* MAIN NAV */}
      <div className="main-nav">
        <button className="category-btn">
          ☰ DANH MỤC SẢN PHẨM
        </button>

        <ul className="menu">
          <li><a href="#">🔥 KHUYẾN MẠI</a></li>
          <li><a href="#">GIỚI THIỆU</a></li>
          <li><a href="#">TIN TỨC</a></li>
          <li><a href="#">LIÊN HỆ</a></li>
        </ul>

        <div className="cart">
          🛒 <span className="badge">0</span>
        </div>
      </div>
    </>
  );
};

// export default Navbar;