import apiClient from "./apiClient";

const categoryService = {
   
    // public endpoint
    getRootCategories: async () => {
        try{
            const response = await apiClient.get('/api/v1/category/root');
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    },

    getAllCategories: async () => {
        try {
            const response = await apiClient.get('api/v1/category');
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    },

    getSubCategory: async (categoryId) => {
        try {
            const response = await apiClient.get(`/api/v1/category/${categoryId}`);
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    },

    // admin,staff endpoint
    createCategory: async (categoryData) => {
        try {
            const response = await apiClient.post('/api/v1/category', categoryData);
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    },

    updateCategory: async (categoryId, newCategoryData) => {
        try {
            const response = await apiClient.put(`/api/v1/category/${categoryId}`, newCategoryData);
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    },

    deleteCategory: async (categoryId) => {
        try {
            const response = await apiClient.delete(`/api/v1/category/${categoryId}`);
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    }
}

export default categoryService;