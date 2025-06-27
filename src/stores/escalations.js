import { defineStore } from 'pinia'
import { useAuthStore } from './auth'
import { useNotificationsStore } from './notifications'

export const useEscalationsStore = defineStore('escalations', {
  state: () => ({
    escalations: [],
    loading: false,
    error: null,
  }),
  
  getters: {
    getEscalationsByExpert: (state) => (expertId) => {
      return state.escalations.filter(e => e.techExpertId === expertId);
    },
    getEscalationsByOfficer: (state) => (officerId) => {
        return state.escalations.filter(e => e.securityOfficerId === officerId);
    }
  },
  
  actions: {
    async fetchAllEscalations() {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      try {
        const data = await authStore.apiCall('/escalations');
        this.escalations = data;
      } catch (error) {
        this.error = error.message;
        console.error('Error fetching escalations:', error);
      } finally {
        this.loading = false;
      }
    },

    async fetchEscalationsByTechExpert(expertId) {
        const authStore = useAuthStore();
        this.loading = true;
        this.error = null;
        try {
          const data = await authStore.apiCall(`/escalations/expert/${expertId}`);
          this.escalations = data;
        } catch (error) {
          this.error = error.message;
          console.error('Error fetching escalations for expert:', error);
        } finally {
          this.loading = false;
        }
      },

    async reviewEscalation(escalationId, reviewData) {
        const authStore = useAuthStore();
        const notificationsStore = useNotificationsStore();
        this.loading = true;
        this.error = null;
        try {
            const updatedEscalation = await authStore.apiCall(`/escalations/review/${escalationId}`, {
                method: 'PUT',
                body: JSON.stringify(reviewData)
            });
            const index = this.escalations.findIndex(e => e.id === escalationId);
            if (index !== -1) {
                this.escalations[index] = updatedEscalation;
            }
            notificationsStore.addNotification('Escalation reviewed successfully.', 'success');
        } catch (error) {
            this.error = error.message;
            notificationsStore.addNotification(error.message || 'Failed to review escalation.', 'error');
            throw error;
        } finally {
            this.loading = false;
        }
    }
  }
}) 