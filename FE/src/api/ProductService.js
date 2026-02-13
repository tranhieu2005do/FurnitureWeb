import apiClient  from "./apiClient";

const productService = {
    // public endpoint
    
    getProducts: async ({
        page = 0,
        size = 10,
        min_price,
        max_price,
        category_id,
        star,
        in_stock,
    } = {}) => {
        try {
            const response = await apiClient.get("/api/v1/product", {
                params: {
                page,
                size,
                min_price,
                max_price,
                category_id,
                star,
                in_stock,
                },
            });

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