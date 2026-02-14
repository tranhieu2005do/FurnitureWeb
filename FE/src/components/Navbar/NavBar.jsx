import "./NavBar.css";
import { useState, useEffect } from "react";
import cartService from "../../api/CartService";

export default function NavBar(){

    const [cartItems, setCartItems] = useState([]);
    const [scrollY, setScrollY] = useState(0);

    useEffect(() => {
      const fetchCart = async () => {
        try {
          const response = await cartService.getUserCart();
          console.log("Cart response:", response.data);
          localStorage.setItem('cart_id', response.data.cart_id);
          setCartItems(response.data.cart_items);
        } catch (error) {
          console.log(error);
        }
      };
      fetchCart();
    }, []);

    useEffect(() => {
        const handleScroll = () => setScrollY(window.scrollY);
        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);
    return(
        <nav className="navbar" style={{
        background: scrollY > 50 ? 'rgba(255, 255, 255, 0.95)' : 'transparent',
        backdropFilter: scrollY > 50 ? 'blur(10px)' : 'none'
      }}>
        <div className="nav-container">
          <div className="nav-logo">
            <svg width="40" height="40" viewBox="0 0 40 40" fill="none">
              <rect x="8" y="12" width="24" height="18" stroke="#8b7355" strokeWidth="2"/>
              <line x1="8" y1="18" x2="32" y2="18" stroke="#8b7355" strokeWidth="2"/>
              <line x1="20" y1="18" x2="20" y2="30" stroke="#8b7355" strokeWidth="2"/>
            </svg>
            <span className="logo-text">LUXE INTERIOR</span>
          </div>
          
          <ul className="nav-menu">
            <li><a href="#home">Trang chủ</a></li>
            <li><a href="#products">Sản phẩm</a></li>
            <li><a href="#collections">Bộ sưu tập</a></li>
            <li><a href="#about">Về chúng tôi</a></li>
            <li><a href="#contact">Liên hệ</a></li>
          </ul>

          <div className="nav-actions">
            <button className="icon-btn">
              <svg  viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <circle cx="11" cy="11" r="8"/>
                <path d="m21 21-4.35-4.35"/>
              </svg>
            </button>
            <button className="icon-btn">
              <svg  viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
            </button>
            <button className="icon-btn cart-btn">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <circle cx="9" cy="21" r="1" />
                    <circle cx="20" cy="21" r="1" />
                    <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6" />
                </svg>

                <span className="cart-badge">{cartItems.length}</span>
            </button>

          </div>
        </div>
      </nav>
    );
}