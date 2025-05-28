<script setup>
import { ref, computed } from 'vue';
import { useAdminStore } from '../../stores/admin';

const adminStore = useAdminStore();
const users = computed(() => adminStore.users);
const loading = ref(false);

// Placeholder for creating a new user
const newUser = ref({
  username: '',
  email: '',
  role: 'system_owner',
  department: ''
});

// Add a user (demo purposes)
const addUser = async () => {
  if (!newUser.value.username || !newUser.value.email) {
    alert('Please fill in all required fields');
    return;
  }
  
  loading.value = true;
  try {
    await adminStore.createUser(newUser.value);
    newUser.value = {
      username: '',
      email: '',
      role: 'system_owner',
      department: ''
    };
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

// Delete a user (demo purposes)
const deleteUser = async (userId) => {
  if (confirm('Are you sure you want to delete this user?')) {
    try {
      await adminStore.deleteUser(userId);
    } catch (error) {
      console.error(error);
    }
  }
};
</script>

<template>
  <div>
    <div class="mb-6 flex justify-between items-center">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">User Management</h1>
        <p class="text-gray-600">Manage system users and roles</p>
      </div>
    </div>
    
    <!-- Add User Form -->
    <div class="card bg-white mb-6">
      <h2 class="text-lg font-medium text-gray-900 mb-4">Add New User</h2>
      
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label for="username" class="form-label">Username</label>
          <input
            id="username"
            v-model="newUser.username"
            type="text"
            class="form-input"
            placeholder="Enter username"
          />
        </div>
        
        <div>
          <label for="email" class="form-label">Email</label>
          <input
            id="email"
            v-model="newUser.email"
            type="email"
            class="form-input"
            placeholder="Enter email"
          />
        </div>
        
        <div>
          <label for="role" class="form-label">Role</label>
          <select
            id="role"
            v-model="newUser.role"
            class="form-input"
          >
            <option value="admin">Admin</option>
            <option value="security_officer">Security Officer</option>
            <option value="system_owner">System Owner</option>
          </select>
        </div>
        
        <div>
          <label for="department" class="form-label">Department</label>
          <input
            id="department"
            v-model="newUser.department"
            type="text"
            class="form-input"
            placeholder="Enter department"
          />
        </div>
      </div>
      
      <div class="mt-4">
        <button @click="addUser" class="btn btn-primary" :disabled="loading">
          {{ loading ? 'Adding...' : 'Add User' }}
        </button>
      </div>
    </div>
    
    <!-- Users List -->
    <div class="card bg-white">
      <h2 class="text-lg font-medium text-gray-900 mb-4">Users</h2>
      
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Username
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Email
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Role
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Department
              </th>
              <th scope="col" class="relative px-6 py-3">
                <span class="sr-only">Actions</span>
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="user in users" :key="user.id">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="font-medium text-gray-900">{{ user.username }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ user.email }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ user.role.replace('_', ' ') }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ user.department }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <button v-if="user.id !== 1" @click="deleteUser(user.id)" class="text-red-600 hover:text-red-900">
                  Delete
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template> 