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
        sortBy,
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
                sortBy
                },
            });

            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    },

    getVariants: async ({
        page = 0,
        size = 10,
        color,
        length,
        height,
        width,
        material,
        inStock
    } = {}) => {
        try{
            const response = await apiClient.get('/api/v1/product/variant', {
                params: {
                    page,
                    size,
                    color,
                    length,
                    height,
                    width,
                    material,
                    inStock
                }
            });
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    },

    getProductVariants: async (productId) => {
        try {
            const response = await apiClient.get(`/api/v1/product/${productId}/variant`);
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    },

    getProductByProductId: async (productId) => {
        try {
            const response = await apiClient.get(`/api/v1/product/${productId}`);
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    },

    getVariantImages: async (variantId) => {
        try {
            const response = await apiClient.get(`/api/v1/product/variant/${variantId}`);
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    },

    // private endpoint
    
    createProduct: async (productData) => {
        try {
            const response = await apiClient.post('/api/v1/product', productData);
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    },
    
    insertVariant: async (productId, variantData) => {
        try {
            const response = await apiClient.post(`/api/v1/product/${productId}`, variantData);
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    },

    deleteVariant: async (productId) => {
        try {
            const response = await apiClient.delete(`/api/v1/product/${productId}`);
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    },

    uploadVariantImage: async (variantId, file) => {
        try {
            const formData = new FormData();
            formData.append("file", file);
            const response = await apiClient.post(
                `/api/v1/product/variant/${variantId}`, 
                formData,
                {
                    headers: {
                        "Content-Type": "multipart/form-data"
                    }
                }
            );
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    },

    softDeleteProduct: async (productId) => {
        try {
            const response = await apiClient.patch(`/api/v1/product/${productId}/soft_delete`);
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    },

    deleteProduct: async (productId) => {
        try {
            const response = await apiClient.delete(`/api/v1/product/${productId}`);
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    }
}

export default productService;