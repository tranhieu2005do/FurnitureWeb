import apiClient from "./apiClient";

const cartService = {
    getUserCart: async () => {
        try {
            const response = await apiClient.get('/api/v1/carts');
            return response.data;
        } catch (error){
            throw error.response?.data || error.message;
        }
    },

    addItemToCart: async (cartItemData) => {
        try {
            const response = await apiClient.post('/api/v1/carts/add_item', cartItemData);
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    },

    updateCartItem: async (cartItemData) => {
        try {
            const response = await apiClient.patch('/api/v1/carts/cartItem', cartItemData);
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    },

    removeItem: async (cartItemId) => {
        try {
            const response = await apiClient.patch(`/api/v1/carts/cartItem/${cartItemId}`);
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    }
}

export default cartService;