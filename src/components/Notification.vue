<template>
  <div v-if="notifications.length > 0" class="notification-container">
    <div v-for="notification in notifications" :key="notification.id" :class="['notification', notification.type]">
      <p>{{ notification.message }}</p>
      <button @click="removeNotification(notification.id)">&times;</button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useNotificationsStore } from '../stores/notifications';

const store = useNotificationsStore();
const notifications = computed(() => store.notifications);

const removeNotification = (id) => {
  store.removeNotification(id);
};
</script>

<style scoped>
.notification-container {
  position: fixed;
  top: 80px;
  right: 20px;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.notification {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-radius: 8px;
  color: white;
  min-width: 300px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.notification.success {
  background-color: #4caf50;
}

.notification.error {
  background-color: #f44336;
}

.notification.info {
  background-color: #2196f3;
}

.notification p {
  margin: 0;
}

.notification button {
  background: none;
  border: none;
  color: white;
  font-size: 20px;
  cursor: pointer;
  margin-left: 15px;
}
</style> 