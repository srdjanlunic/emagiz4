<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const formData = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  role: 'SYSTEM_OWNER'
});

const errors = ref({});
const loading = ref(false);

const handleSubmit = () => {
  // For demo purposes, we'll just redirect to login
  // In a real app, this would send data to an API
  router.push('/auth/login');
};

const roleOptions = [
  { value: 'SYSTEM_OWNER', label: 'System Owner' },
  { value: 'TECHNICAL_EXPERT', label: 'Technical Expert' }
];
</script>

<template>
  <div style="min-height: 100vh; background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%); display: flex; align-items: center; justify-content: center; padding: 16px;">
    <div style="background: white; border-radius: 16px; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04); padding: 48px; width: 100%; max-width: 400px;">
      <!-- Header with Logo -->
      <div style="text-align: center; margin-bottom: 32px;">
        <div style="width: 64px; height: 64px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); border-radius: 16px; display: flex; align-items: center; justify-content: center; margin: 0 auto 16px;">
          <svg style="width: 32px; height: 32px; color: white;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z" />
          </svg>
        </div>
        <h1 style="font-size: 28px; font-weight: 700; color: #111827; margin-bottom: 8px;">CVE Management</h1>
        <p style="color: #6b7280; font-size: 16px;">Create your account</p>
      </div>
      
      <!-- Register Form -->
      <form @submit.prevent="handleSubmit">
        <div style="margin-bottom: 20px;">
          <label for="username" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 8px;">Username</label>
          <input id="username" v-model="formData.username" type="text" required style="width: 100%; border-radius: 8px; border: 1px solid #D1D5DB; padding: 12px 16px; font-size: 16px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s, box-shadow 0.2s; box-sizing: border-box;" placeholder="Enter your username">
        </div>
        
        <div style="margin-bottom: 20px;">
          <label for="email" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 8px;">Email</label>
          <input id="email" v-model="formData.email" type="email" required style="width: 100%; border-radius: 8px; border: 1px solid #D1D5DB; padding: 12px 16px; font-size: 16px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s, box-shadow 0.2s; box-sizing: border-box;" placeholder="Enter your email">
        </div>
        
        <div style="margin-bottom: 20px;">
          <label for="password" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 8px;">Password</label>
          <input id="password" v-model="formData.password" type="password" required style="width: 100%; border-radius: 8px; border: 1px solid #D1D5DB; padding: 12px 16px; font-size: 16px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s, box-shadow 0.2s; box-sizing: border-box;" placeholder="Enter your password">
        </div>
        
        <div style="margin-bottom: 20px;">
          <label for="confirm-password" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 8px;">Confirm Password</label>
          <input id="confirm-password" v-model="formData.confirmPassword" type="password" required style="width: 100%; border-radius: 8px; border: 1px solid #D1D5DB; padding: 12px 16px; font-size: 16px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s, box-shadow 0.2s; box-sizing: border-box;" placeholder="Confirm your password">
        </div>
        
        <div style="margin-bottom: 24px;">
          <label for="role" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 8px;">Role</label>
          <select id="role" v-model="formData.role" required style="width: 100%; border-radius: 8px; border: 1px solid #D1D5DB; padding: 12px 16px; font-size: 16px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s, box-shadow 0.2s; box-sizing: border-box; appearance: none; background-image: url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAiIGhlaWdodD0iMjAiIHZpZXdCb3g9IjAgMCAyMCAyMCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTYgOEwxMCAxMkwxNCA4IiBzdHJva2U9IiM2QjcyODAiIHN0cm9rZS13aWR0aD0iMS41IiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiLz4KPC9zdmc+'); background-position: right 12px center; background-repeat: no-repeat; background-size: 16px 16px; padding-right: 40px;">
            <option v-for="option in roleOptions" :key="option.value" :value="option.value">{{ option.label }}</option>
          </select>
        </div>
        
        <button type="submit" :disabled="loading" style="width: 100%; padding: 12px 24px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; border: none; border-radius: 8px; font-size: 16px; font-weight: 600; cursor: pointer; transition: all 0.2s; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); display: flex; align-items: center; justify-content: center; gap: 8px;" :style="loading ? 'opacity: 0.7; cursor: not-allowed;' : ''">
          <svg v-if="loading" style="width: 20px; height: 20px; animation: spin 1s linear infinite;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
          </svg>
          {{ loading ? 'Creating account...' : 'Create Account' }}
        </button>
      </form>

      <!-- Login Link -->
      <div style="margin-top: 24px; text-align: center;">
        <p style="font-size: 14px; color: #6b7280;">
          Already have an account? 
          <router-link to="/auth/login" style="color: #475569; font-weight: 500; text-decoration: none; margin-left: 4px;">Sign in</router-link>
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

input:focus,
select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

input:hover,
select:hover {
  border-color: #9ca3af;
}

button:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 6px 8px rgba(0, 0, 0, 0.15);
}
</style> 