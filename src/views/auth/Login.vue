<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../stores/auth';

const authStore = useAuthStore();
const router = useRouter();

const username = ref('');
const password = ref('');
const error = ref('');
const loading = ref(false);

const handleSubmit = async () => {
  if (!username.value || !password.value) {
    error.value = 'Please enter both username and password';
    return;
  }

  error.value = '';
  loading.value = true;
  
  try {
    await authStore.login({
      username: username.value,
      password: password.value
    });
    router.push('/');
  } catch (err) {
    error.value = 'Invalid credentials';
  } finally {
    loading.value = false;
  }
};

const handleDemoLogin = async (role) => {
  error.value = '';
  try {
    await authStore.demoLogin(role);
    router.push('/');
  } catch (err) {
    error.value = `Demo login failed: ${err.message}`;
  }
};

// Demo helper function to pre-fill credentials
const fillDemo = (role) => {
  if (role === 'admin') {
    username.value = 'admin';
    password.value = 'admin123';
  } else if (role === 'security') {
    username.value = 'security';
    password.value = 'security123';
  } else {
    username.value = 'system_owner';
    password.value = 'owner123';
  }
};
</script>

<template>
  <div style="min-height: 100vh; background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%); display: flex; align-items: center; justify-content: center; padding: 16px;">
    <div style="background: white; border-radius: 16px; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04); padding: 48px; width: 100%; max-width: 400px;">
      <!-- Header with Logo -->
      <div style="text-align: center; margin-bottom: 32px;">
        <div style="width: 64px; height: 64px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); border-radius: 16px; display: flex; align-items: center; justify-content: center; margin: 0 auto 16px;">
          <svg style="width: 32px; height: 32px; color: white;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
          </svg>
        </div>
        <h1 style="font-size: 28px; font-weight: 700; color: #111827; margin-bottom: 8px;">CVE Management</h1>
        <p style="color: #6b7280; font-size: 16px;">Sign in to your account</p>
      </div>
      
      <!-- Error Message -->
      <div v-if="error" style="background: #fee2e2; border: 1px solid #fca5a5; border-radius: 12px; padding: 16px; margin-bottom: 24px;">
        <div style="display: flex; align-items: center; gap: 8px;">
          <svg style="width: 20px; height: 20px; color: #dc2626;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
          <span style="color: #dc2626; font-weight: 500;">{{ error }}</span>
        </div>
      </div>
      
      <!-- Login Form -->
      <form @submit.prevent="handleSubmit">
        <div style="margin-bottom: 20px;">
          <label for="username" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 8px;">Username</label>
          <input id="username" v-model="username" type="text" required style="width: 100%; border-radius: 8px; border: 1px solid #D1D5DB; padding: 12px 16px; font-size: 16px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s, box-shadow 0.2s; box-sizing: border-box;" placeholder="Enter your username">
        </div>
        
        <div style="margin-bottom: 24px;">
          <label for="password" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 8px;">Password</label>
          <input id="password" v-model="password" type="password" required style="width: 100%; border-radius: 8px; border: 1px solid #D1D5DB; padding: 12px 16px; font-size: 16px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s, box-shadow 0.2s; box-sizing: border-box;" placeholder="Enter your password">
        </div>
        
        <button type="submit" :disabled="loading" style="width: 100%; padding: 12px 24px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; border: none; border-radius: 8px; font-size: 16px; font-weight: 600; cursor: pointer; transition: all 0.2s; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); display: flex; align-items: center; justify-content: center; gap: 8px;" :style="loading ? 'opacity: 0.7; cursor: not-allowed;' : ''">
          <svg v-if="loading" style="width: 20px; height: 20px; animation: spin 1s linear infinite;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
          </svg>
          {{ loading ? 'Signing in...' : 'Sign in' }}
        </button>
      </form>

      <!-- Demo Accounts -->
      <div style="margin-top: 32px; padding-top: 24px; border-top: 1px solid #e5e7eb;">
        <p style="font-size: 14px; color: #6b7280; margin-bottom: 12px; text-align: center;">Demo Accounts</p>
        <div style="display: grid; gap: 8px;">
          <button @click="handleDemoLogin('ADMIN')" style="width: 100%; padding: 8px 16px; background-color: #f1f5f9; color: #334155; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.2s;">
            Admin (admin / admin123)
          </button>
          <button @click="handleDemoLogin('SECURITY_OFFICER')" style="width: 100%; padding: 8px 16px; background-color: #f8fafc; color: #475569; border: 1px solid #e2e8f0; border-radius: 6px; font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.2s;">
            Security Officer (security / security123)
          </button>
          <button @click="handleDemoLogin('SYSTEM_OWNER')" style="width: 100%; padding: 8px 16px; background-color: #f9fafb; color: #374151; border: 1px solid #d1d5db; border-radius: 6px; font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.2s;">
            System Owner (system_owner / owner123)
          </button>
          <button @click="handleDemoLogin('TECHNICAL_EXPERT')" style="width: 100%; padding: 8px 16px; background-color: #f9fafb; color: #374151; border: 1px solid #d1d5db; border-radius: 6px; font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.2s;">
            Technical Expert (technical_expert / expert123)
          </button>
        </div>
      </div>

      <!-- Register Link -->
      <div style="margin-top: 24px; text-align: center;">
        <p style="font-size: 14px; color: #6b7280;">
          Don't have an account? 
          <router-link to="/auth/register" style="color: #475569; font-weight: 500; text-decoration: none; margin-left: 4px;">Sign up</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

input:focus {
  outline: none;
  border-color: #475569;
  box-shadow: 0 0 0 3px rgba(71, 85, 105, 0.1);
}

input:hover {
  border-color: #9ca3af;
}

button:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 6px 8px rgba(0, 0, 0, 0.15);
}

button[style*="background-color: #f1f5f9"]:hover {
  background-color: #e2e8f0 !important;
}

button[style*="background-color: #f8fafc"]:hover {
  background-color: #f1f5f9 !important;
}

button[style*="background-color: #f9fafb"]:hover {
  background-color: #f3f4f6 !important;
}

.demo-login-container {
  margin-top: 32px;
  text-align: center;
  width: 100%;
}

.demo-title {
  font-size: 16px;
  font-weight: 500;
  color: #4b5563;
  margin-bottom: 16px;
}

.demo-buttons {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.demo-button {
  padding: 10px;
  border-radius: 6px;
  border: 1px solid #d1d5db;
  background-color: #f9fafb;
  color: #374151;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.demo-button:hover {
  background-color: #f3f4f6;
}
</style> 