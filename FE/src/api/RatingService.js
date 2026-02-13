import apiClient from "./apiClient";

const ratingService = {

    getRatePerProduct: async({
        page = 0,
        size = 10
    } = {}) => {
        try{
            const response = await apiClient.get('/api/v1/rates', {
                params: {
                    page,
                    size
                }
            })

            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    }
}

export default ratingService;