import { defineStore } from 'pinia';
import { useAuthStore } from './auth';

export const useNotificationsStore = defineStore('notifications', {
  state: () => ({
    notifications: [],
    loading: false,
    error: null,
    pollingIntervalId: null,
  }),
  actions: {
    async fetchNotifications() {
      const authStore = useAuthStore();
      if (!authStore.user) return;
      this.loading = true;
      this.error = null;
      try {
        const data = await authStore.apiCall(`/notifications/user/${authStore.user.id}`);
        this.notifications = data;
      } catch (error) {
        this.error = error.message;
        console.error('Error fetching notifications:', error);
      } finally {
        this.loading = false;
      }
    },
    async markNotificationAsRead(notificationId) {
      const authStore = useAuthStore();
      try {
        await authStore.apiCall(`/notifications/${notificationId}/read`, { method: 'PUT' });
        const notification = this.notifications.find(n => n.id === notificationId);
        if (notification) {
          notification.isRead = true;
        }
      } catch (error) {
        console.error('Error marking notification as read:', error);
      }
    },
    addNotification(message, type = 'info') {
      this.notifications.unshift({
        id: new Date().getTime(), // temporary unique ID
        message,
        type,
        isRead: false,
        createdAt: new Date().toISOString(),
      });
    },
    removeNotification(id) {
      this.notifications = this.notifications.filter(n => n.id !== id);
    },
    startPolling() {
      if (this.pollingIntervalId) return; // Already polling
      this.fetchNotifications(); // Fetch immediately
      this.pollingIntervalId = setInterval(() => {
        this.fetchNotifications();
      }, 15000); // Poll every 15 seconds
    },
    stopPolling() {
      if (this.pollingIntervalId) {
        clearInterval(this.pollingIntervalId);
        this.pollingIntervalId = null;
      }
    }
  },
}); 