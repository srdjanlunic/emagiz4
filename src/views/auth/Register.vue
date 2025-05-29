<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const formData = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  role: 'system_owner'
});

const errors = ref({});
const loading = ref(false);

const handleSubmit = () => {
  // For demo purposes, we'll just redirect to login
  // In a real app, this would send data to an API
  router.push('/login');
};
</script>

<template>
  <div>
    <h2 class="text-2xl font-bold text-gray-900 mb-6">Create an account</h2>
    
    <form @submit.prevent="handleSubmit">
      <div class="mb-4">
        <label for="username" class="form-label">Username</label>
        <input
          id="username"
          v-model="formData.username"
          type="text"
          class="form-input"
          placeholder="Enter your username"
          required
        />
      </div>
      
      <div class="mb-4">
        <label for="email" class="form-label">Email</label>
        <input
          id="email"
          v-model="formData.email"
          type="email"
          class="form-input"
          placeholder="Enter your email"
          required
        />
      </div>
      
      <div class="mb-4">
        <label for="password" class="form-label">Password</label>
        <input
          id="password"
          v-model="formData.password"
          type="password"
          class="form-input"
          placeholder="Enter your password"
          required
        />
      </div>
      
      <div class="mb-4">
        <label for="confirm-password" class="form-label">Confirm Password</label>
        <input
          id="confirm-password"
          v-model="formData.confirmPassword"
          type="password"
          class="form-input"
          placeholder="Confirm your password"
          required
        />
      </div>
      
      <div class="mb-6">
        <label for="role" class="form-label">Role</label>
        <select
          id="role"
          v-model="formData.role"
          class="form-input"
          required
        >
          <option value="system_owner">System Owner</option>
          <option value="security_officer">Security Officer</option>
        </select>
      </div>
      
      <button
        type="submit"
        class="btn btn-primary w-full"
        :disabled="loading"
      >
        {{ loading ? 'Creating account...' : 'Register' }}
      </button>
    </form>
    
    <div class="mt-4 text-center">
      <p class="text-sm text-gray-600">
        Already have an account?
        <router-link to="/login" class="text-blue-600 hover:underline">Sign in</router-link>
      </p>
    </div>
  </div>
</template> 