import "./Login.css"

function Login(){
    return(
        <div className="container">
            <div id="loginForm" class="form-container active">
                <h2>Đăng Nhập</h2>
                <form onsubmit="handleLogin(event)">
                    <div class="input-group">
                        <label for="loginPhone">Số điện thoại</label>
                        <input type="tel" id="loginPhone" name="phone" placeholder="Nhập số điện thoại" required />
                    </div>
                    <div class="input-group">
                        <label for="loginPassword">Mật khẩu</label>
                        <input type="password" id="loginPassword" name="password" placeholder="Nhập mật khẩu" required />
                    </div>
                    <button type="submit" class="btn">Đăng Nhập</button>
                    <div class="toggle-form">
                        Chưa có tài khoản? <a onclick="toggleForm()">Đăng ký ngay</a>
                    </div>
                </form>
            </div>
        </div>
    );
}
export default Login;