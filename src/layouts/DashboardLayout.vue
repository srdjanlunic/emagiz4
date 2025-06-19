<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import Notification from '../components/Notification.vue';

const router = useRouter();
const authStore = useAuthStore();

const user = computed(() => authStore.user);

// Navigation items based on user role
const navigationItems = computed(() => {
  const role = authStore.userRole;
  const baseItems = [
    { name: 'Dashboard', path: '/dashboard', icon: 'M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z' }
  ];

  switch (role) {
    case 'ADMIN':
      return [
        ...baseItems,
        { name: 'Systems', path: '/systems', icon: 'M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z' },
        { name: 'CVEs', path: '/cve', icon: 'M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z' },
        { name: 'Users', path: '/admin/users', icon: 'M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z' },
        { name: 'Departments', path: '/admin/departments', icon: 'M8 14v3m4-3v3m4-3v3M3 21h18M3 10h18M3 7l9-4 9 4M4 10h16v11H4V10z' },
        { name: 'Notifications', path: '/notifications', icon: 'M15 17h5l-5 5v-5zM4 4a2 2 0 012-2h4a2 2 0 012 2v6a2 2 0 01-2 2H6a2 2 0 01-2-2V4z' }
      ];
      
    case 'SECURITY_OFFICER':
      return [
        ...baseItems,
        { name: 'Systems', path: '/systems', icon: 'M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z' },
        { name: 'CVEs', path: '/cve', icon: 'M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z' },
        { name: 'Notifications', path: '/notifications', icon: 'M15 17h5l-5 5v-5zM4 4a2 2 0 012-2h4a2 2 0 012 2v6a2 2 0 01-2 2H6a2 2 0 01-2-2V4z' },
        { name: 'Departments', path: '/admin/departments', icon: 'M8 14v3m4-3v3m4-3v3M3 21h18M3 10h18M3 7l9-4 9 4M4 10h16v11H4V10z' }
      ];
      
    case 'SYSTEM_OWNER':
      return [
        ...baseItems,
        { name: 'My Systems', path: '/systems', icon: 'M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z' },
        { name: 'My CVEs', path: '/cve', icon: 'M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z' },
        { name: 'My Notifications', path: '/notifications', icon: 'M15 17h5l-5 5v-5zM4 4a2 2 0 012-2h4a2 2 0 012 2v6a2 2 0 01-2 2H6a2 2 0 01-2-2V4z' }
      ];
      
    case 'TECHNICAL_EXPERT':
      return [
        ...baseItems,
        { name: 'Systems', path: '/systems', icon: 'M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z' },
        { name: 'CVEs', path: '/cve', icon: 'M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z' },
        { name: 'Notifications', path: '/notifications', icon: 'M15 17h5l-5 5v-5zM4 4a2 2 0 012-2h4a2 2 0 012 2v6a2 2 0 01-2 2H6a2 2 0 01-2-2V4z' }
      ];
      
    default:
      return baseItems;
  }
});

// Show different action button based on role
const primaryAction = computed(() => {
  const role = authStore.userRole;
  
  switch (role) {
    case 'SECURITY_OFFICER':
      return { name: 'Import CVEs', path: '/cve', action: 'import' };
    case 'SYSTEM_OWNER':
      return { name: 'Add System', path: '/systems/add' };
    default:
      return null;
  }
});

const logout = () => {
  authStore.logout();
  router.push('/auth/login');
};

const handlePrimaryAction = () => {
  if (primaryAction.value?.action === 'import') {
    // Handle CVE import - this would need to be implemented
    router.push('/cve');
  } else if (primaryAction.value?.path) {
    router.push(primaryAction.value.path);
  }
};
</script>

<template>
  <div style="min-height: 100vh; background-color: #f5f5f7;">
    <Notification />
    <!-- Navigation Sidebar -->
    <div style="position: fixed; top: 0; left: 0; bottom: 0; width: 256px; background-color: #1f2937; color: white; overflow-y: auto; z-index: 40;">
      <!-- Header -->
      <div style="padding: 24px; border-bottom: 1px solid #374151;">
        <div style="display: flex; align-items: center; gap: 12px;">
          <div style="width: 32px; height: 32px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); border-radius: 8px; display: flex; align-items: center; justify-content: center;">
            <svg style="width: 20px; height: 20px; color: white;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
            </svg>
          </div>
          <div>
            <h1 style="font-size: 18px; font-weight: 700; margin: 0;">CVE Management</h1>
            <p style="font-size: 12px; color: #9ca3af; margin: 0;">System</p>
          </div>
        </div>
      </div>

      <!-- Navigation Items -->
      <nav style="padding: 16px 0;">
        <router-link
          v-for="item in navigationItems"
          :key="item.name"
          :to="item.path"
          style="display: flex; align-items: center; gap: 12px; padding: 12px 24px; color: #d1d5db; text-decoration: none; transition: all 0.2s; margin: 2px 16px; border-radius: 6px;"
          active-class="active-nav-item"
        >
          <svg style="width: 20px; height: 20px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" :d="item.icon" />
          </svg>
          <span style="font-size: 14px; font-weight: 500;">{{ item.name }}</span>
        </router-link>
      </nav>

      <!-- Primary Action Button -->
      <div v-if="primaryAction" style="padding: 16px 24px;">
        <button @click="handlePrimaryAction" style="width: 100%; padding: 12px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; border: none; border-radius: 8px; font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.2s; display: flex; align-items: center; justify-content: center; gap: 8px;">
          <svg style="width: 16px; height: 16px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
          {{ primaryAction.name }}
        </button>
      </div>

      <!-- User Profile -->
      <div style="position: absolute; bottom: 0; left: 0; right: 0; padding: 16px 24px; border-top: 1px solid #374151;">
        <div style="display: flex; align-items: center; justify-content: space-between;">
          <div style="display: flex; align-items: center; gap: 12px;">
            <div style="width: 32px; height: 32px; background-color: #3b82f6; border-radius: 50%; display: flex; align-items: center; justify-content: center;">
              <span style="font-size: 14px; font-weight: 600; color: white;">{{ user?.username?.charAt(0).toUpperCase() }}</span>
            </div>
            <div>
              <p style="font-size: 14px; font-weight: 500; color: white; margin: 0;">{{ user?.username }}</p>
              <p style="font-size: 12px; color: #9ca3af; margin: 0; text-transform: capitalize;">{{ user?.roleName?.replace('_', ' ').toLowerCase() }}</p>
            </div>
          </div>
          <button @click="logout" style="padding: 8px; background: none; border: none; color: #9ca3af; cursor: pointer; border-radius: 4px; transition: color 0.2s;">
            <svg style="width: 16px; height: 16px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <div style="margin-left: 256px; min-height: 100vh;">
      <router-view />
    </div>
  </div>
</template>

<style scoped>
.active-nav-item {
  background-color: rgba(59, 130, 246, 0.1) !important;
  color: #60a5fa !important;
}

nav a:hover {
  background-color: rgba(59, 130, 246, 0.05);
  color: #60a5fa;
}
</style> 