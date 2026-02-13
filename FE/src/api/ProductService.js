import apiClient  from "./apiClient";

const productService = {
    // public endpoint
    
    getProducts: async (productData) => {
        try{
            const response = await apiClient.get('/api/v1/product', productData);
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    },

    getVariants: async (variantData) => {
        try{
            const response = await apiClient.get('/api/v1/product/variant', variantData);
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    }
   
}

export default productService;