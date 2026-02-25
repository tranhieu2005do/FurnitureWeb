import apiClient from "./apiClient";

const chatService = {
    sendMessage: async (formData) => {
        try {
            const response = await apiClient.post('/api/v1/conservation', formData, {
                headers: {
                    "Content-Type" : "multipart/form-data"
                }
            });
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    },

    getMessageConservationByUserId: async (formData) => {
        try {
            const response = await apiClient.get('/api/v1/conservation/messages', formData);
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    },

    getConversation: async () => {
        try {
            const response = await apiClient.get('/api/v1/conservation');
            return response.data;
        } catch (error) {
            throw error.response?.data || error.message;
        }
    }
}

export default chatService;