import React, { useState} from 'react';
import './HomePage.css';
import NavBar from '../Navbar/NavBar';


export default function HomePage() {
  const [activeCategory, setActiveCategory] = useState(0);

  const categories = [
    { name: 'Phòng Khách', icon: '🛋️', count: '120+ sản phẩm' },
    { name: 'Phòng Ngủ', icon: '🛏️', count: '85+ sản phẩm' },
    { name: 'Phòng Làm Việc', icon: '💼', count: '95+ sản phẩm' },
    { name: 'Phòng Ăn', icon: '🍽️', count: '75+ sản phẩm' },
  ];

  const featuredProducts = [
    {
      id: 1,
      name: 'Sofa Mondrian',
      category: 'Phòng Khách',
      price: '28.500.000',
      image: 'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=800&h=600&fit=crop',
      tag: 'Mới nhất'
    },
    {
      id: 2,
      name: 'Bàn Làm Việc Oak',
      category: 'Phòng Làm Việc',
      price: '12.800.000',
      image: 'https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=800&h=600&fit=crop',
      tag: 'Bán chạy'
    },
    {
      id: 3,
      name: 'Giường Ngủ Luxury',
      category: 'Phòng Ngủ',
      price: '35.900.000',
      image: 'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=800&h=600&fit=crop',
      tag: 'Cao cấp'
    },
    {
      id: 4,
      name: 'Bộ Bàn Ăn Marble',
      category: 'Phòng Ăn',
      price: '45.200.000',
      image: 'https://images.unsplash.com/photo-1617806118233-18e1de247200?w=800&h=600&fit=crop',
      tag: 'Độc quyền'
    },
  ];

  const collections = [
    {
      title: 'Bộ Sưu Tập Scandinavian',
      description: 'Thiết kế tối giản Bắc Âu',
      image: 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=1200&h=800&fit=crop'
    },
    {
      title: 'Nội Thất Luxury',
      description: 'Đẳng cấp sang trọng',
      image: 'https://images.unsplash.com/photo-1600210492486-724fe5c67fb0?w=1200&h=800&fit=crop'
    }
  ];

  return (
    <div className="homepage">
      <NavBar />

      {/* Hero Section */}
      <section className="hero-section">
        <div className="hero-content">
          <div className="hero-text">
            <span className="hero-subtitle">Bộ Sưu Tập 2026</span>
            <h1 className="hero-title">
              Không Gian Sống<br/>
              <span className="highlight">Đẳng Cấp</span>
            </h1>
            <p className="hero-description">
              Khám phá những thiết kế nội thất cao cấp, được chế tác tỉ mỉ 
              từ những chất liệu tốt nhất, mang đến sự sang trọng cho ngôi nhà bạn.
            </p>
            <div className="hero-cta">
              <button className="btn-primary">
                Khám phá ngay
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M5 12h14M12 5l7 7-7 7"/>
                </svg>
              </button>
              <button className="btn-secondary">
                Xem catalog
              </button>
            </div>
            
            <div className="hero-stats">
              <div className="stat-item">
                <h3>500+</h3>
                <p>Sản phẩm</p>
              </div>
              <div className="stat-divider"></div>
              <div className="stat-item">
                <h3>10K+</h3>
                <p>Khách hàng</p>
              </div>
              <div className="stat-divider"></div>
              <div className="stat-item">
                <h3>15+</h3>
                <p>Năm kinh nghiệm</p>
              </div>
            </div>
          </div>

          <div className="hero-image">
            <div className="floating-card card-1">
              <img src="https://images.unsplash.com/photo-1556228578-0d85b1a4d571?w=600&h=800&fit=crop" alt="Living room" />
              <div className="card-overlay">
                <span className="card-tag">Phòng khách hiện đại</span>
              </div>
            </div>
            <div className="floating-card card-2">
              <img src="https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?w=500&h=600&fit=crop" alt="Bedroom" />
              <div className="card-overlay">
                <span className="card-tag">Phòng ngủ sang trọng</span>
              </div>
            </div>
            
            <div className="decorative-element circle-1"></div>
            <div className="decorative-element circle-2"></div>
          </div>
        </div>

        <div className="scroll-indicator">
          <span>Cuộn xuống</span>
          <div className="scroll-line"></div>
        </div>
      </section>

      {/* Categories Section */}
      <section className="categories-section">
        <div className="section-header">
          <span className="section-subtitle">Danh mục sản phẩm</span>
          <h2 className="section-title">Khám Phá Theo Không Gian</h2>
        </div>

        <div className="categories-grid">
          {categories.map((category, index) => (
            <div 
              key={index}
              className={`category-card ${activeCategory === index ? 'active' : ''}`}
              onMouseEnter={() => setActiveCategory(index)}
            >
              <div className="category-icon">{category.icon}</div>
              <h3 className="category-name">{category.name}</h3>
              <p className="category-count">{category.count}</p>
              <div className="category-arrow">→</div>
            </div>
          ))}
        </div>
      </section>

      {/* Featured Products */}
      <section className="products-section">
        <div className="section-header">
          <div>
            <span className="section-subtitle">Sản phẩm nổi bật</span>
            <h2 className="section-title">Được Yêu Thích Nhất</h2>
          </div>
          <button className="btn-outline">
            Xem tất cả
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M5 12h14M12 5l7 7-7 7"/>
            </svg>
          </button>
        </div>

        <div className="products-grid">
          {featuredProducts.map((product, index) => (
            <div key={product.id} className="product-card" style={{ animationDelay: `${index * 0.1}s` }}>
              <div className="product-image-wrapper">
                <img src={product.image} alt={product.name} className="product-image" />
                <span className="product-tag">{product.tag}</span>
                <div className="product-overlay">
                  <button className="quick-view-btn">Xem nhanh</button>
                  <div className="product-actions">
                    <button className="action-btn">❤️</button>
                    <button className="action-btn">👁️</button>
                  </div>
                </div>
              </div>
              <div className="product-info">
                <span className="product-category">{product.category}</span>
                <h3 className="product-name">{product.name}</h3>
                <div className="product-footer">
                  <span className="product-price">{product.price}đ</span>
                  <button className="add-to-cart-btn">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <circle cx="9" cy="21" r="1"/>
                      <circle cx="20" cy="21" r="1"/>
                      <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* Collections Banner */}
      <section className="collections-section">
        <div className="collections-grid">
          {collections.map((collection, index) => (
            <div key={index} className="collection-banner">
              <img src={collection.image} alt={collection.title} className="collection-bg" />
              <div className="collection-content">
                <h3 className="collection-title">{collection.title}</h3>
                <p className="collection-description">{collection.description}</p>
                <button className="collection-btn">Khám phá →</button>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* Features Section */}
      <section className="features-section">
        <div className="features-grid">
          <div className="feature-item">
            <div className="feature-icon">🚚</div>
            <h4>Miễn phí vận chuyển</h4>
            <p>Đơn hàng trên 10 triệu</p>
          </div>
          <div className="feature-item">
            <div className="feature-icon">💯</div>
            <h4>Bảo hành chính hãng</h4>
            <p>Đến 5 năm</p>
          </div>
          <div className="feature-item">
            <div className="feature-icon">🎨</div>
            <h4>Tư vấn thiết kế</h4>
            <p>Miễn phí 24/7</p>
          </div>
          <div className="feature-item">
            <div className="feature-icon">🔒</div>
            <h4>Thanh toán an toàn</h4>
            <p>Bảo mật tuyệt đối</p>
          </div>
        </div>
      </section>

      {/* Newsletter */}
      <section className="newsletter-section">
        <div className="newsletter-content">
          <div className="newsletter-text">
            <h2>Đăng Ký Nhận Ưu Đãi</h2>
            <p>Nhận thông tin về sản phẩm mới và ưu đãi đặc biệt</p>
          </div>
          <form className="newsletter-form">
            <input 
              type="email" 
              placeholder="Nhập email của bạn" 
              className="newsletter-input"
            />
            <button type="submit" className="newsletter-btn">
              Đăng ký
            </button>
          </form>
        </div>
      </section>

      {/* Footer */}
      <footer className="footer">
        <div className="footer-content">
          <div className="footer-section">
            <div className="footer-logo">
              <svg width="40" height="40" viewBox="0 0 40 40" fill="none">
                <rect x="8" y="12" width="24" height="18" stroke="#8b7355" strokeWidth="2"/>
                <line x1="8" y1="18" x2="32" y2="18" stroke="#8b7355" strokeWidth="2"/>
                <line x1="20" y1="18" x2="20" y2="30" stroke="#8b7355" strokeWidth="2"/>
              </svg>
              <span>LUXE INTERIOR</span>
            </div>
            <p className="footer-description">
              Mang đến những sản phẩm nội thất cao cấp, 
              thiết kế tinh tế cho không gian sống của bạn.
            </p>
            <div className="social-links">
              <a href="#" className="social-link">Facebook</a>
              <a href="#" className="social-link">Instagram</a>
              <a href="#" className="social-link">Pinterest</a>
            </div>
          </div>

          <div className="footer-section">
            <h4>Về chúng tôi</h4>
            <ul className="footer-links">
              <li><a href="#">Giới thiệu</a></li>
              <li><a href="#">Showroom</a></li>
              <li><a href="#">Tuyển dụng</a></li>
              <li><a href="#">Liên hệ</a></li>
            </ul>
          </div>

          <div className="footer-section">
            <h4>Hỗ trợ</h4>
            <ul className="footer-links">
              <li><a href="#">Chính sách bảo hành</a></li>
              <li><a href="#">Chính sách đổi trả</a></li>
              <li><a href="#">Hướng dẫn thanh toán</a></li>
              <li><a href="#">Câu hỏi thường gặp</a></li>
            </ul>
          </div>

          <div className="footer-section">
            <h4>Liên hệ</h4>
            <ul className="footer-links">
              <li>📍 123 Đường ABC, Q.1, TP.HCM</li>
              <li>📞 1900 xxxx</li>
              <li>✉️ info@luxeinterior.vn</li>
              <li>🕐 8:00 - 22:00 (Hàng ngày)</li>
            </ul>
          </div>
        </div>

        <div className="footer-bottom">
          <p>© 2024 Luxe Interior. All rights reserved.</p>
          <div className="payment-methods">
            <span>Phương thức thanh toán:</span>
            <div className="payment-icons">💳 🏦 📱</div>
          </div>
        </div>
      </footer>
    </div>
  );
}