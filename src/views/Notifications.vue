<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { useCVEsStore } from '../stores/cves';
import { useAuthStore } from '../stores/auth';

const router = useRouter();
const cvesStore = useCVEsStore();
const authStore = useAuthStore();

const userId = computed(() => authStore.user?.id);

// Get all notifications for current user
const notifications = computed(() => {
  if (!userId.value) return [];
  
  return cvesStore.cves
    .filter(cve => cve.notifications.some(n => n.userId === userId.value))
    .map(cve => ({
      ...cve,
      notification: cve.notifications.find(n => n.userId === userId.value)
    }))
    .sort((a, b) => {
      // Sort by read status (unread first) then by date (newest first)
      if (!a.notification.read && b.notification.read) return -1;
      if (a.notification.read && !b.notification.read) return 1;
      return new Date(b.notification.createdAt) - new Date(a.notification.createdAt);
    });
});

// Format date
const formatDate = (dateString) => {
  const date = new Date(dateString);
  const now = new Date();
  const diffMs = now - date;
  const diffMinutes = Math.floor(diffMs / (1000 * 60));
  const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
  
  if (diffMinutes < 60) {
    return `${diffMinutes} minute${diffMinutes !== 1 ? 's' : ''} ago`;
  } else if (diffHours < 24) {
    return `${diffHours} hour${diffHours !== 1 ? 's' : ''} ago`;
  } else if (diffDays < 7) {
    return `${diffDays} day${diffDays !== 1 ? 's' : ''} ago`;
  } else {
    return date.toLocaleDateString();
  }
};

// Navigate to CVE details
const viewCVE = (cveId) => {
  router.push(`/cve/${cveId}`);
};

// Mark a notification as read
const markAsRead = async (cveId) => {
  try {
    await cvesStore.markNotificationRead(cveId, userId.value);
  } catch (error) {
    console.error('Error marking notification as read:', error);
  }
};
</script>

<template>
  <div class="notifications-page">
    <div class="page-header">
      <h1>Notifications</h1>
      <p>System and CVE notifications</p>
    </div>
    
    <div class="notifications-container">
      <div v-if="notifications.length === 0" class="empty-state">
        <svg class="empty-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
        </svg>
        <h3>No notifications</h3>
        <p>You don't have any notifications at the moment</p>
      </div>
      
      <div v-else class="notifications-list">
        <div 
          v-for="item in notifications" 
          :key="item.id"
          class="notification-item"
          :class="{ 'unread': !item.notification.read }"
          @click="viewCVE(item.id)"
        >
          <div class="notification-content">
            <div class="notification-header">
              <div class="notification-title">
                <div v-if="!item.notification.read" class="unread-dot"></div>
                <h3>{{ item.id }}</h3>
              </div>
              <div v-if="!item.notification.read" class="mark-read">
                <button 
                  @click.stop="markAsRead(item.id)"
                  class="mark-read-button"
                >
                  Mark as read
                </button>
              </div>
            </div>
            <p class="notification-message">{{ item.notification.message }}</p>
            <p class="notification-time">{{ formatDate(item.notification.createdAt) }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.notifications-page {
  padding: 24px;
}

.page-header {
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 4px;
}

.page-header p {
  color: #6b7280;
  font-size: 14px;
}

.notifications-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.empty-state {
  text-align: center;
  padding: 48px 24px;
}

.empty-icon {
  width: 48px;
  height: 48px;
  color: #9ca3af;
  margin: 0 auto 16px;
}

.empty-state h3 {
  font-size: 18px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 8px;
}

.empty-state p {
  color: #6b7280;
}

.notifications-list {
  border-top: 1px solid #e5e7eb;
}

.notification-item {
  border-bottom: 1px solid #e5e7eb;
  cursor: pointer;
  transition: background-color 0.2s;
}

.notification-item:hover {
  background-color: #f9fafb;
}

.notification-item.unread {
  background-color: #eff6ff;
}

.notification-item.unread:hover {
  background-color: #dbeafe;
}

.notification-content {
  padding: 16px 24px;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.notification-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.unread-dot {
  width: 8px;
  height: 8px;
  background-color: #2563eb;
  border-radius: 50%;
}

.notification-title h3 {
  font-weight: 500;
  color: #111827;
}

.mark-read-button {
  font-size: 14px;
  color: #2563eb;
  background: none;
  border: none;
  padding: 4px 8px;
  cursor: pointer;
  transition: color 0.2s;
}

.mark-read-button:hover {
  color: #1d4ed8;
}

.notification-message {
  color: #374151;
  margin-bottom: 8px;
  line-height: 1.5;
}

.notification-time {
  font-size: 13px;
  color: #6b7280;
}

@media (max-width: 640px) {
  .notification-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .mark-read {
    width: 100%;
  }
  
  .mark-read-button {
    width: 100%;
    padding: 8px;
    background-color: #eff6ff;
    border-radius: 6px;
  }
}
</style> 