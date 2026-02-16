import apiClient from "./apiClient";

const commentService = {
    sendComment: async (formData) => {
        try {
            const response = await apiClient.post('/api/v1/comments', formData, {
                headers: {
                    "Content-Type" : "multipart/form-data"
                }
            });
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    },

    getCommentOfProduct: async (productId) => {
        try {
            const response = await apiClient.get(`/api/v1/comments/product/${productId}`, {
                params: {
                    page: 0,
                    size: 20
                }
            });
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    }
}

export default commentService;