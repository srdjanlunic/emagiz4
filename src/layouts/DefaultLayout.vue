<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const authStore = useAuthStore();
const router = useRouter();
const isNavOpen = ref(false);
const isMobile = ref(false);

const user = computed(() => authStore.user);
const isAdmin = computed(() => authStore.isAdmin);
const isSecurityOfficer = computed(() => authStore.isSecurityOfficer);
const isSystemOwner = computed(() => authStore.isSystemOwner);

const checkScreenSize = () => {
  if (typeof window !== 'undefined') {
    isMobile.value = window.innerWidth < 768;
  }
};

onMounted(() => {
  checkScreenSize();
  window.addEventListener('resize', checkScreenSize);
});

onUnmounted(() => {
  if (typeof window !== 'undefined') {
    window.removeEventListener('resize', checkScreenSize);
  }
});

const logout = async () => {
  await authStore.logout();
  router.push('/login');
};

const toggleNav = () => {
  isNavOpen.value = !isNavOpen.value;
};
</script>

<template>
  <div style="min-height: 100vh; background-color: #f9fafb;">
    <!-- Modern Navbar -->
    <nav style="background-color: white; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1); position: sticky; top: 0; z-index: 50;">
      <div style="max-width: 1280px; margin: 0 auto; padding: 0 16px;">
        <div style="display: flex; justify-content: space-between; height: 64px;">
          <!-- Logo Section -->
          <div style="display: flex; align-items: center;">
            <div style="display: flex; align-items: center;">
              <h1 style="font-size: 20px; font-weight: bold; background: linear-gradient(135deg, #2563eb 0%, #7c3aed 100%); -webkit-background-clip: text; background-clip: text; color: transparent;">
                CVE Management
              </h1>
            </div>
          </div>

          <!-- Desktop Navigation -->
          <div v-if="!isMobile" style="display: flex; align-items: center; gap: 4px;">
            <router-link 
              to="/" 
              style="color: #6b7280; padding: 8px 16px; border-radius: 8px; font-size: 14px; font-weight: 500; transition: all 0.2s; text-decoration: none; display: flex; align-items: center;"
              :style="$route.path === '/' ? 'background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);' : ''"
            >
              <svg style="width: 16px; height: 16px; margin-right: 8px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2H5a2 2 0 00-2-2z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 5a2 2 0 012-2h4a2 2 0 012 2v2H8V5z" />
              </svg>
              Dashboard
            </router-link>
            
            <router-link 
              to="/systems" 
              style="color: #6b7280; padding: 8px 16px; border-radius: 8px; font-size: 14px; font-weight: 500; transition: all 0.2s; text-decoration: none; display: flex; align-items: center;"
              :style="$route.path === '/systems' ? 'background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);' : ''"
            >
              <svg style="width: 16px; height: 16px; margin-right: 8px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z" />
              </svg>
              Systems
            </router-link>
            
            <router-link 
              to="/cve" 
              style="color: #6b7280; padding: 8px 16px; border-radius: 8px; font-size: 14px; font-weight: 500; transition: all 0.2s; text-decoration: none; display: flex; align-items: center;"
              :style="$route.path === '/cve' ? 'background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);' : ''"
            >
              <svg style="width: 16px; height: 16px; margin-right: 8px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.34 16.5c-.77.833.192 2.5 1.732 2.5z" />
              </svg>
              CVEs
            </router-link>
            
            <router-link 
              to="/notifications" 
              style="color: #6b7280; padding: 8px 16px; border-radius: 8px; font-size: 14px; font-weight: 500; transition: all 0.2s; text-decoration: none; display: flex; align-items: center;"
              :style="$route.path === '/notifications' ? 'background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);' : ''"
            >
              <svg style="width: 16px; height: 16px; margin-right: 8px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 5a2 2 0 114 0c0 7.667 5.333 11.333 8 12v1H2v-1c2.667-.667 8-4.333 8-12zM9 21h6" />
              </svg>
              Notifications
            </router-link>

            <!-- Admin Section -->
            <template v-if="isAdmin">
              <div style="border-left: 1px solid #e5e7eb; margin-left: 16px; padding-left: 16px;">
                <router-link 
                  to="/admin/users" 
                  style="color: #6b7280; padding: 8px 16px; border-radius: 8px; font-size: 14px; font-weight: 500; transition: all 0.2s; text-decoration: none; display: flex; align-items: center;"
                  :style="$route.path === '/admin/users' ? 'background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);' : ''"
                >
                  <svg style="width: 16px; height: 16px; margin-right: 8px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.25 2.25 0 11-4.5 0 2.25 2.25 0 014.5 0z" />
                  </svg>
                  Users
                </router-link>
                
                <router-link 
                  to="/admin/departments" 
                  style="color: #6b7280; padding: 8px 16px; border-radius: 8px; font-size: 14px; font-weight: 500; transition: all 0.2s; text-decoration: none; display: flex; align-items: center;"
                  :style="$route.path === '/admin/departments' ? 'background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);' : ''"
                >
                  <svg style="width: 16px; height: 16px; margin-right: 8px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                  </svg>
                  Departments
                </router-link>
              </div>
            </template>
          </div>

          <!-- User Profile & Actions -->
          <div v-if="!isMobile" style="display: flex; align-items: center; gap: 16px;">
            <div style="display: flex; align-items: center; gap: 12px;">
              <div style="display: flex; align-items: center; gap: 8px;">
                <div style="width: 32px; height: 32px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); border-radius: 50%; display: flex; align-items: center; justify-content: center;">
                  <span style="color: white; font-size: 14px; font-weight: 500;">
                    {{ user?.username?.[0]?.toUpperCase() }}
                  </span>
                </div>
                <div style="font-size: 14px;">
                  <div style="font-weight: 500; color: #111827;">{{ user?.username }}</div>
                  <div style="font-size: 12px; color: #6b7280;">{{ user?.role?.replace('_', ' ') }}</div>
                </div>
              </div>
              <button 
                @click="logout" 
                style="background-color: #ef4444; color: white; padding: 8px 16px; border-radius: 8px; font-size: 14px; font-weight: 500; border: none; cursor: pointer; transition: background-color 0.2s;"
              >
                Logout
              </button>
            </div>
          </div>

          <!-- Mobile Menu Button -->
          <div v-if="isMobile" style="display: flex; align-items: center;">
            <button 
              @click="toggleNav"
              style="padding: 8px; border-radius: 8px; color: #6b7280; border: none; background: none; cursor: pointer; transition: all 0.2s;"
            >
              <svg style="width: 24px; height: 24px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path v-if="!isNavOpen" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
                <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- Mobile Menu -->
      <div v-if="isNavOpen && isMobile" style="background-color: white; border-top: 1px solid #e5e7eb;">
        <div style="padding: 16px; display: flex; flex-direction: column; gap: 8px;">
          <router-link 
            to="/" 
            style="display: block; padding: 12px 16px; border-radius: 8px; color: #374151; text-decoration: none; transition: all 0.2s;"
            :style="$route.path === '/' ? 'background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; font-weight: 600;' : ''"
            @click="isNavOpen = false"
          >
            Dashboard
          </router-link>
          <router-link 
            to="/systems" 
            style="display: block; padding: 12px 16px; border-radius: 8px; color: #374151; text-decoration: none; transition: all 0.2s;"
            :style="$route.path === '/systems' ? 'background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; font-weight: 600;' : ''"
            @click="isNavOpen = false"
          >
            Systems
          </router-link>
          <router-link 
            to="/cve" 
            style="display: block; padding: 12px 16px; border-radius: 8px; color: #374151; text-decoration: none; transition: all 0.2s;"
            :style="$route.path === '/cve' ? 'background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; font-weight: 600;' : ''"
            @click="isNavOpen = false"
          >
            CVEs
          </router-link>
          <router-link 
            to="/notifications" 
            style="display: block; padding: 12px 16px; border-radius: 8px; color: #374151; text-decoration: none; transition: all 0.2s;"
            :style="$route.path === '/notifications' ? 'background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; font-weight: 600;' : ''"
            @click="isNavOpen = false"
          >
            Notifications
          </router-link>
          
          <template v-if="isAdmin">
            <div style="border-top: 1px solid #e5e7eb; padding-top: 8px; margin-top: 8px;">
              <router-link 
                to="/admin/users" 
                style="display: block; padding: 12px 16px; border-radius: 8px; color: #374151; text-decoration: none; transition: all 0.2s;"
                :style="$route.path === '/admin/users' ? 'background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; font-weight: 600;' : ''"
                @click="isNavOpen = false"
              >
                Users
              </router-link>
              <router-link 
                to="/admin/departments" 
                style="display: block; padding: 12px 16px; border-radius: 8px; color: #374151; text-decoration: none; transition: all 0.2s;"
                :style="$route.path === '/admin/departments' ? 'background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; font-weight: 600;' : ''"
                @click="isNavOpen = false"
              >
                Departments
              </router-link>
            </div>
          </template>
          
          <div style="border-top: 1px solid #e5e7eb; padding-top: 16px; margin-top: 16px;">
            <div style="display: flex; align-items: center; gap: 12px; padding: 0 16px; margin-bottom: 16px;">
              <div style="width: 40px; height: 40px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); border-radius: 50%; display: flex; align-items: center; justify-content: center;">
                <span style="color: white; font-weight: 500;">
                  {{ user?.username?.[0]?.toUpperCase() }}
                </span>
              </div>
              <div style="font-size: 14px;">
                <div style="font-weight: 500; color: #111827;">{{ user?.username }}</div>
                <div style="font-size: 12px; color: #6b7280;">{{ user?.role?.replace('_', ' ') }}</div>
              </div>
            </div>
            <button 
              @click="logout" 
              style="width: 100%; background-color: #ef4444; color: white; padding: 12px 16px; border-radius: 8px; font-size: 14px; font-weight: 500; border: none; cursor: pointer; transition: background-color 0.2s;"
            >
              Logout
            </button>
          </div>
        </div>
      </div>
    </nav>

    <!-- Main Content -->
    <main style="max-width: 1280px; margin: 0 auto; padding: 32px 16px;">
      <router-view></router-view>
    </main>
  </div>
</template>

<style scoped>
.nav-link {
  color: #6b7280;
}

.nav-link.router-link-active {
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}

.mobile-nav-link.router-link-active {
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  color: white;
  font-weight: 600;
}

/* Custom scrollbar for mobile menu */
.md\:hidden::-webkit-scrollbar {
  width: 4px;
}

.md\:hidden::-webkit-scrollbar-track {
  background: #f1f5f9;
}

.md\:hidden::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 2px;
}
</style> 