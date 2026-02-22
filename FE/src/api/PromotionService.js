import apiClient from "./apiClient";


const promotionService = {
    getActivePromotionForUser: async () => {
        try {
            const response = await apiClient.get('/api/v1/promotions');
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    }
}

export default promotionService;