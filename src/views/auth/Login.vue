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
  <div>
    <h2 class="text-2xl font-bold text-gray-900 mb-6">Sign in to your account</h2>
    
    <div v-if="error" class="bg-red-50 border border-red-300 text-red-700 px-4 py-3 rounded mb-4">
      {{ error }}
    </div>
    
    <form @submit.prevent="handleSubmit">
      <div class="mb-4">
        <label for="username" class="form-label">Username</label>
        <input
          id="username"
          v-model="username"
          type="text"
          class="form-input"
          placeholder="Enter your username"
          required
        />
      </div>
      
      <div class="mb-6">
        <label for="password" class="form-label">Password</label>
        <input
          id="password"
          v-model="password"
          type="password"
          class="form-input"
          placeholder="Enter your password"
          required
        />
      </div>
      
      <button
        type="submit"
        class="btn btn-primary w-full"
        :disabled="loading"
      >
        {{ loading ? 'Signing in...' : 'Sign in' }}
      </button>
    </form>

    <div class="mt-8">
      <p class="text-sm text-gray-500 mb-2">Demo Accounts:</p>
      <div class="flex space-x-2">
        <button @click="fillDemo('admin')" class="text-xs bg-blue-100 text-blue-700 px-2 py-1 rounded">Admin</button>
        <button @click="fillDemo('security')" class="text-xs bg-green-100 text-green-700 px-2 py-1 rounded">Security Officer</button>
        <button @click="fillDemo('owner')" class="text-xs bg-purple-100 text-purple-700 px-2 py-1 rounded">System Owner</button>
      </div>
    </div>
  </div>
</template> 