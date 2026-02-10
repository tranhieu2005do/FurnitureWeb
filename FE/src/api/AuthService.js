import apiClient from './apiClient';

/**
 * Service xử lý tất cả API liên quan đến authentication
 */
const authService = {
  /**
   * Đăng ký tài khoản mới
   * @param {Object} userData - Thông tin người dùng
   * @param {string} userData.name - Họ têAn
   * @param {string} userData.email - Email
   * @param {string} userData.password - Mật khẩu
   * @returns {Promise} Response từ server
   */
  register: async (userData) => {
    try {
      const response = await apiClient.post('/api/v1/auth/register', userData);
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  /**
   * Đăng nhập
   * @param {Object} credentials - Thông tin đăng nhập
   * @param {string} credentials.email - Email
   * @param {string} credentials.password - Mật khẩu
   * @returns {Promise} Response chứa token và thông tin user
   */
  login: async (credentials) => {
    try {
      const response = await apiClient.post('/api/v1/auth/log-in', credentials);
      
      // Lưu token vào localStorage
      if (response.data.accessToken) {
        localStorage.setItem('accessToken', response.data.accessToken);
      }
      if (response.data.refreshToken) {
        localStorage.setItem('refreshToken', response.data.refreshToken);
      }
      
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  /**
   * Đăng xuất
   * @returns {Promise} Response từ server
   */
  logout: async () => {
    try {
      const response = await apiClient.post('/auth/logout');
      
      // Xóa token khỏi localStorage
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      
      return response.data;
    } catch (error) {
      // Vẫn xóa token dù API lỗi
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      throw error.response?.data || error.message;
    }
  },

  /**
   * Lấy thông tin user hiện tại
   * @returns {Promise} Thông tin user
   */
  getCurrentUser: async () => {
    try {
      const response = await apiClient.get('/auth/me');
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  /**
   * Quên mật khẩu - Gửi email reset
   * @param {string} email - Email người dùng
   * @returns {Promise} Response từ server
   */
  forgotPassword: async (email) => {
    try {
      const response = await apiClient.post('/auth/forgot-password', { email });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  /**
   * Reset mật khẩu
   * @param {Object} resetData - Dữ liệu reset password
   * @param {string} resetData.token - Token từ email
   * @param {string} resetData.password - Mật khẩu mới
   * @returns {Promise} Response từ server
   */
  resetPassword: async (resetData) => {
    try {
      const response = await apiClient.post('/auth/reset-password', resetData);
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  /**
   * Thay đổi mật khẩu
   * @param {Object} passwordData - Dữ liệu đổi mật khẩu
   * @param {string} passwordData.currentPassword - Mật khẩu hiện tại
   * @param {string} passwordData.newPassword - Mật khẩu mới
   * @returns {Promise} Response từ server
   */
  changePassword: async (passwordData) => {
    try {
      const response = await apiClient.put('/auth/change-password', passwordData);
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  /**
   * Refresh access token
   * @param {string} refreshToken - Refresh token
   * @returns {Promise} Response chứa access token mới
   */
  refreshToken: async (refreshToken) => {
    try {
      const response = await apiClient.post('/auth/refresh', { refreshToken });
      
      if (response.data.accessToken) {
        localStorage.setItem('accessToken', response.data.accessToken);
      }
      
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },
};

export default authService;