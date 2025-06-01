<script setup>
import { ref, computed } from 'vue';
import { useAdminStore } from '../../stores/admin';

const adminStore = useAdminStore();
const users = computed(() => adminStore.users);
const loading = ref(false);
const showAddUserModal = ref(false);

// Placeholder for creating a new user
const newUser = ref({
  username: '',
  email: '',
  role: 'system_owner',
  department: ''
});

// Open add user modal
const openAddUserModal = () => {
  showAddUserModal.value = true;
};

// Close add user modal
const closeAddUserModal = () => {
  showAddUserModal.value = false;
  newUser.value = {
    username: '',
    email: '',
    role: 'system_owner',
    department: ''
  };
};

// Add a user (demo purposes)
const addUser = async () => {
  if (!newUser.value.username || !newUser.value.email) {
    alert('Please fill in all required fields');
    return;
  }
  
  loading.value = true;
  try {
    await adminStore.createUser(newUser.value);
    closeAddUserModal();
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
  <div class="user-management-page" style="padding: 24px;">
    <div class="page-header" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 32px;">
      <div class="header-content">
        <h1 style="font-size: 24px; font-weight: bold; color: #111827; margin-bottom: 4px;">User Management</h1>
        <p style="color: #6b7280; font-size: 14px;">Manage system users and their roles</p>
      </div>
      <button @click="openAddUserModal" style="display: inline-flex; align-items: center; padding: 10px 20px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; border: none; border-radius: 8px; font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.2s; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);">
        <svg style="width: 16px; height: 16px; margin-right: 8px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
        </svg>
        Add User
      </button>
    </div>

    <div class="systems-container" style="background: white; border-radius: 12px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); overflow: hidden;">
      <div v-if="users.length === 0" style="text-align: center; padding: 48px 24px;">
        <h3 style="font-size: 18px; font-weight: 500; color: #374151; margin-bottom: 8px;">No users registered yet</h3>
        <p style="color: #6b7280; margin-bottom: 24px;">Add a user to get started</p>
        <button @click="openAddUserModal" style="display: inline-flex; align-items: center; padding: 10px 20px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; border: none; border-radius: 8px; font-size: 14px; font-weight: 500; cursor: pointer;">Add User</button>
      </div>

      <div v-else style="overflow-x: auto;">
        <table style="width: 100%; border-collapse: separate; border-spacing: 0;">
          <thead>
            <tr>
              <th style="padding: 12px 24px; text-align: left; font-size: 12px; font-weight: 500; text-transform: uppercase; color: #6b7280; background: #f9fafb; border-bottom: 1px solid #e5e7eb;">Username</th>
              <th style="padding: 12px 24px; text-align: left; font-size: 12px; font-weight: 500; text-transform: uppercase; color: #6b7280; background: #f9fafb; border-bottom: 1px solid #e5e7eb;">Email</th>
              <th style="padding: 12px 24px; text-align: left; font-size: 12px; font-weight: 500; text-transform: uppercase; color: #6b7280; background: #f9fafb; border-bottom: 1px solid #e5e7eb;">Role</th>
              <th style="padding: 12px 24px; text-align: left; font-size: 12px; font-weight: 500; text-transform: uppercase; color: #6b7280; background: #f9fafb; border-bottom: 1px solid #e5e7eb;">Department</th>
              <th style="padding: 12px 24px; text-align: right; font-size: 12px; font-weight: 500; text-transform: uppercase; color: #6b7280; background: #f9fafb; border-bottom: 1px solid #e5e7eb;"><span style="position: absolute; width: 1px; height: 1px; padding: 0; margin: -1px; overflow: hidden; clip: rect(0, 0, 0, 0); white-space: nowrap; border-width: 0;">Actions</span></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id" style="border-bottom: 1px solid #e5e7eb;">
              <td style="padding: 16px 24px;">
                <div style="font-weight: 500; color: #111827;">{{ user.username }}</div>
              </td>
              <td style="padding: 16px 24px; color: #6b7280;">{{ user.email }}</td>
              <td style="padding: 16px 24px;">
                <span style="display: inline-block; padding: 4px 12px; border-radius: 9999px; font-size: 13px; font-weight: 500; background-color: #f3f4f6; color: #374151;">
                  {{ user.role.replace('_', ' ') }}
                </span>
              </td>
              <td style="padding: 16px 24px; color: #6b7280;">{{ user.department }}</td>
              <td style="padding: 16px 24px; text-align: right;">
                <button v-if="user.id !== 1" @click="deleteUser(user.id)" style="color: #2563eb; font-weight: 500; text-decoration: none; transition: color 0.2s; :hover { color: #1d4ed8; }">Delete</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Add User Modal -->
    <div v-if="showAddUserModal" style="position: fixed; inset: 0; background-color: rgba(107, 114, 128, 0.75); display: flex; align-items: center; justify-content: center; padding: 16px; z-index: 50;">
      <div style="background-color: white; border-radius: 8px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -1px rgba(0,0,0,0.06); padding: 24px; max-width: 500px; width: 100%;">
        <h3 style="font-size: 20px; font-weight: 600; color: #111827; margin-bottom: 24px;">Add New User</h3>
        
        <div style="display: grid; gap: 16px; margin-bottom: 24px;">
          <div>
            <label for="modal-username" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 6px;">Username <span style="color: #EF4444;">*</span></label>
            <input id="modal-username" v-model="newUser.username" type="text" style="width: 100%; border-radius: 6px; border: 1px solid #D1D5DB; padding: 10px 12px; font-size: 14px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s; box-sizing: border-box;" placeholder="Enter username"/>
          </div>
          
          <div>
            <label for="modal-email" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 6px;">Email <span style="color: #EF4444;">*</span></label>
            <input id="modal-email" v-model="newUser.email" type="email" style="width: 100%; border-radius: 6px; border: 1px solid #D1D5DB; padding: 10px 12px; font-size: 14px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s; box-sizing: border-box;" placeholder="Enter email"/>
          </div>
          
          <div>
            <label for="modal-role" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 6px;">Role</label>
            <select id="modal-role" v-model="newUser.role" style="width: 100%; border-radius: 6px; border: 1px solid #D1D5DB; padding: 10px 12px; font-size: 14px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s; box-sizing: border-box; appearance: none; background-image: url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAiIGhlaWdodD0iMjAiIHZpZXdCb3g9IjAgMCAyMCAyMCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTYgOEwxMCAxMkwxNCA4IiBzdHJva2U9IiM2QjcyODAiIHN0cm9rZS13aWR0aD0iMS41IiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiLz4KPC9zdmc+'); background-position: right 12px center; background-repeat: no-repeat; background-size: 16px 16px; padding-right: 40px;">
              <option value="admin">Admin</option>
              <option value="security_officer">Security Officer</option>
              <option value="system_owner">System Owner</option>
            </select>
          </div>
          
          <div>
            <label for="modal-department" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 6px;">Department</label>
            <input id="modal-department" v-model="newUser.department" type="text" style="width: 100%; border-radius: 6px; border: 1px solid #D1D5DB; padding: 10px 12px; font-size: 14px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s; box-sizing: border-box;" placeholder="Enter department"/>
          </div>
        </div>
        
        <div style="display: flex; justify-content: flex-end; gap: 12px;">
          <button @click="closeAddUserModal" style="padding: 10px 20px; background-color: #F3F4F6; color: #374151; border-radius: 6px; font-weight: 500; border: none; cursor: pointer; transition: background-color 0.2s;">Cancel</button>
          <button @click="addUser" :disabled="loading" style="padding: 10px 20px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; border-radius: 6px; font-weight: 500; border: none; cursor: pointer; transition: all 0.2s;" :style="loading ? 'opacity: 0.7; cursor: not-allowed;' : ''">
            {{ loading ? 'Adding...' : 'Add User' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Using inline styles as requested */
</style> 