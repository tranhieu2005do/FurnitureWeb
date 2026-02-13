import apiClient from "./apiClient";

const categoryService = {

    /**
   * Đăng nhập
   * @param {Object} credentials - Thông tin đăng nhập
   * @param {string} credentials.email - Email
   * @param {string} credentials.password - Mật khẩu
   * @returns {Promise} Response chứa token và thông tin user
   */
    getCategories: async () => {
        try{
            const response = await apiClient.get('/api/v1/category/root');
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    }
}

export default categoryService;