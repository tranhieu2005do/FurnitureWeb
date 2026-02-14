import { useState } from "react";
import "./Login.css";
import authService from "../../api/authservice";
export default function Login() {
  const [isLogin, setIsLogin] = useState(true);
  const [formData, setFormData] = useState({
    email: "",
    name: "",
    phone: "",
    address: "",
    birth: "",
    password: "",
    confirmPassword: "",
  });

//   console.log(isLogin);
  console.log(formData);
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
    
      if(isLogin){
        const response = await authService.login({
            phone: formData.phone,
            password: formData.password,
        });

        console.log("Login success:", response);
        localStorage.setItem('accessToken', response.data.token);
      }
      else{
        const response = await authService.register({
            name: formData.name,
            email: formData.email,
            phone: formData.phone,
            address: formData.address,
            password: formData.password,
            birth: formData.birth
        });

        console.log("Register response:", response);
      }
    } catch (error) {
      console.error("Login failed:", error);
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  return (
    <div
      style={{
        minHeight: "100vh",
        display: "flex",
        background: "linear-gradient(135deg, #f8f5f0 0%, #e8e0d5 100%)",
        fontFamily: "'Cormorant Garamond', serif",
      }}
    >
      {/* Panel bên trái - Hình ảnh & Thông điệp */}
      <div
        className="side-panel"
        style={{
          flex: "1",
          background:
            "linear-gradient(135deg, rgba(139, 115, 85, 0.95), rgba(109, 93, 75, 0.95)), url(\"data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M0 0h60v60H0V0zm30 30v30H0V30h30z' fill='%23ffffff' fill-opacity='0.03' fill-rule='evenodd'/%3E%3C/svg%3E\")",
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          alignItems: "center",
          padding: "60px",
          color: "#fff",
          position: "relative",
          overflow: "hidden",
        }}
      >
        <div
          style={{
            position: "absolute",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            width: "500px",
            height: "500px",
            background:
              "radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%)",
            pointerEvents: "none",
          }}
        ></div>

        <div style={{ position: "relative", zIndex: 1, textAlign: "center" }}>
          <h1
            style={{
              fontSize: "56px",
              fontWeight: "300",
              marginBottom: "20px",
              letterSpacing: "3px",
              textTransform: "uppercase",
            }}
          >
            Nội Thất
          </h1>
          <div
            style={{
              width: "80px",
              height: "2px",
              background: "#fff",
              margin: "0 auto 30px",
            }}
          ></div>
          <p
            style={{
              fontSize: "18px",
              fontFamily: "'Lato', sans-serif",
              fontWeight: "300",
              lineHeight: "1.8",
              maxWidth: "400px",
              opacity: "0.95",
            }}
          >
            Khám phá không gian sống sang trọng với bộ sưu tập nội thất cao cấp,
            được chế tác tỉ mỉ cho ngôi nhà của bạn
          </p>

          <div style={{ marginTop: "60px" }}>
            <svg width="60" height="60" viewBox="0 0 60 60" fill="none">
              <rect
                x="10"
                y="15"
                width="40"
                height="30"
                stroke="white"
                strokeWidth="1.5"
                opacity="0.6"
              />
              <line
                x1="10"
                y1="25"
                x2="50"
                y2="25"
                stroke="white"
                strokeWidth="1.5"
                opacity="0.6"
              />
              <line
                x1="30"
                y1="25"
                x2="30"
                y2="45"
                stroke="white"
                strokeWidth="1.5"
                opacity="0.6"
              />
            </svg>
          </div>
        </div>
      </div>

      {/* Form đăng nhập/đăng ký */}
      <div
        className="form-container"
        style={{
          flex: "1",
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          padding: "40px",
        }}
      >
        <div
          style={{
            width: "100%",
            maxWidth: "480px",
            background: "#fff",
            padding: "60px 50px",
            boxShadow: "0 20px 60px rgba(0,0,0,0.1)",
            position: "relative",
          }}
        >
          {/* Góc trang trí */}
          <div
            style={{
              position: "absolute",
              top: "0",
              left: "0",
              width: "60px",
              height: "60px",
              borderTop: "2px solid #8b7355",
              borderLeft: "2px solid #8b7355",
            }}
          ></div>
          <div
            style={{
              position: "absolute",
              bottom: "0",
              right: "0",
              width: "60px",
              height: "60px",
              borderBottom: "2px solid #8b7355",
              borderRight: "2px solid #8b7355",
            }}
          ></div>

          <div style={{ textAlign: "center", marginBottom: "50px" }}>
            <h2
              style={{
                fontSize: "42px",
                fontWeight: "400",
                color: "#3e3832",
                marginBottom: "10px",
                letterSpacing: "1px",
              }}
            >
              {isLogin ? "Đăng Nhập" : "Đăng Ký"}
            </h2>
            <p
              style={{
                fontFamily: "'Lato', sans-serif",
                fontSize: "14px",
                color: "#8b7355",
                letterSpacing: "1px",
              }}
            >
              {isLogin ? "Chào mừng bạn trở lại" : "Tạo tài khoản mới"}
            </p>
          </div>

          <form onSubmit={handleSubmit}>
            {!isLogin && (
              <div className="input-wrapper" style={{ marginBottom: "30px" }}>
                <input
                  type="text"
                  name="name"
                  placeholder="Họ và tên"
                  className="auth-input"
                  value={formData.name}
                  onChange={handleChange}
                  required={!isLogin}
                />
              </div>
            )}

            {!isLogin && (
              <div className="input-wrapper" style={{ marginBottom: "30px" }}>
                <input
                  type="email"
                  name="email"
                  placeholder="Email"
                  className="auth-input"
                  value={formData.email}
                  onChange={handleChange}
                  required={!isLogin}
                />
              </div>
            )}

            {!isLogin && (
              <div className="input-wrapper" style={{ marginBottom: "30px" }}>
                <input
                  type="text"
                  name="address"
                  placeholder="Địa chỉ"
                  className="auth-input"
                  value={formData.address}
                  onChange={handleChange}
                  required={!isLogin}
                />
              </div>
            )}

            <div className="input-wrapper" style={{ marginBottom: "30px" }}>
              <input
                type="tel"
                name="phone"
                placeholder="Số điện thoại"
                className="auth-input"
                value={formData.phone}
                onChange={handleChange}
                required
              />
            </div>

            {!isLogin && (
              <div className="input-wrapper" style={{ marginBottom: "30px" }}>
                <input
                  type="date"
                  name="birth"
                  placeholder="Ngày sinh"
                  className="auth-input"
                  value={formData.birth}
                  onChange={handleChange}
                  required={!isLogin}
                />
              </div>
            )}

            <div className="input-wrapper" style={{ marginBottom: "30px" }}>
              <input
                type="password"
                name="password"
                placeholder="Mật khẩu"
                className="auth-input"
                value={formData.password}
                onChange={handleChange}
                required
              />
            </div>

            {!isLogin && (
              <div className="input-wrapper" style={{ marginBottom: "30px" }}>
                <input
                  type="password"
                  name="confirmPassword"
                  placeholder="Xác nhận mật khẩu"
                  className="auth-input"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  required={!isLogin}
                />
              </div>
            )}

            {isLogin && (
              <div
                style={{
                  textAlign: "right",
                  marginBottom: "30px",
                }}
              >
                <a
                  href="#"
                  className="toggle-link"
                  style={{
                    fontSize: "13px",
                    fontFamily: "'Lato', sans-serif",
                  }}
                >
                  Quên mật khẩu?
                </a>
              </div>
            )}

            <button
              type="submit"
              className="submit-btn"
              style={{
                marginTop: "20px",
              }}
            >
              {isLogin ? "Đăng Nhập" : "Đăng Ký"}
            </button>
          </form>

          <div className="decorative-line"></div>

          <div
            style={{
              textAlign: "center",
              fontFamily: "'Lato', sans-serif",
              fontSize: "14px",
              color: "#7a6f5d",
            }}
          >
            {isLogin ? "Chưa có tài khoản? " : "Đã có tài khoản? "}
            <span className="toggle-link" onClick={() => setIsLogin(!isLogin)}>
              {isLogin ? "Đăng ký ngay" : "Đăng nhập"}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
}
