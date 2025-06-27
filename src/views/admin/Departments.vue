<script setup>
import { ref, computed } from 'vue';
import { useAdminStore } from '../../stores/admin';

const adminStore = useAdminStore();
const departments = computed(() => adminStore.departments);
const users = computed(() => adminStore.users);
const loading = ref(false);
const showAddDepartmentModal = ref(false);
const newDepartmentName = ref('');
const newDepartmentDescription = ref('');

// Selected department for assigning users
const selectedDepartment = ref(null);
const selectedUserIds = ref([]);

// Open add department modal
const openAddDepartmentModal = () => {
  showAddDepartmentModal.value = true;
};

// Close add department modal
const closeAddDepartmentModal = () => {
  showAddDepartmentModal.value = false;
  newDepartmentName.value = '';
  newDepartmentDescription.value = '';
};

// Add a department (demo purposes)
const addDepartment = async () => {
  if (!newDepartmentName.value.trim()) {
    return;
  }
  loading.value = true;
  try {
    await adminStore.createDepartment({
        name: newDepartmentName.value, 
        description: newDepartmentDescription.value
    });
    closeAddDepartmentModal();
    adminStore.fetchDepartments();
  } catch (error) {
    console.error('Failed to add department:', error);
  } finally {
    loading.value = false;
  }
};

// Delete a department (demo purposes)
const deleteDepartment = async (departmentId) => {
  if (confirm('Are you sure you want to delete this department?')) {
    try {
      await adminStore.deleteDepartment(departmentId);
    } catch (error) {
      console.error(error);
    }
  }
};

// Open assign users dialog
const openAssignUsers = (dept) => {
  selectedDepartment.value = dept;
  selectedUserIds.value = [...dept.userIds];
};

// Assign users to department
const assignUsers = async () => {
  if (!selectedDepartment.value) return;
  
  try {
    await adminStore.assignUsersToDepartment(
      selectedDepartment.value.id, 
      selectedUserIds.value
    );
    selectedDepartment.value = null;
  } catch (error) {
    console.error(error);
  }
};

// Cancel assign users
const cancelAssignUsers = () => {
  selectedDepartment.value = null;
  selectedUserIds.value = [];
};

// Get user names for a department
const getDepartmentUsers = (userIds) => {
  return userIds
    .map(id => users.value.find(u => u.id === id))
    .filter(Boolean)
    .map(u => u.username)
    .join(', ');
};

// Toggle user selection
const toggleUserSelection = (userId) => {
  const index = selectedUserIds.value.indexOf(userId);
  if (index === -1) {
    selectedUserIds.value.push(userId);
  } else {
    selectedUserIds.value.splice(index, 1);
  }
};
</script>

<template>
  <div class="departments-page" style="padding: 24px;">
    <div class="page-header" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 32px;">
      <div class="header-content">
        <h1 style="font-size: 24px; font-weight: bold; color: #111827; margin-bottom: 4px;">Departments</h1>
        <p style="color: #6b7280; font-size: 14px;">Manage departments in your organization</p>
      </div>
      <button @click="openAddDepartmentModal" style="display: inline-flex; align-items: center; padding: 10px 20px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; border: none; border-radius: 8px; font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.2s; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);">
        <svg style="width: 16px; height: 16px; margin-right: 8px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
        </svg>
        Add Department
      </button>
    </div>

    <div class="systems-container" style="background: white; border-radius: 12px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); overflow: hidden;">
      <div v-if="departments.length === 0" style="text-align: center; padding: 48px 24px;">
        <h3 style="font-size: 18px; font-weight: 500; color: #374151; margin-bottom: 8px;">No departments registered yet</h3>
        <p style="color: #6b7280; margin-bottom: 24px;">Add a department to get started</p>
        <button @click="openAddDepartmentModal" style="display: inline-flex; align-items: center; padding: 10px 20px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; border: none; border-radius: 8px; font-size: 14px; font-weight: 500; cursor: pointer;">Add Department</button>
      </div>

      <div v-else style="overflow-x: auto;">
        <table style="width: 100%; border-collapse: separate; border-spacing: 0;">
          <thead>
            <tr>
              <th style="padding: 12px 24px; text-align: left; font-size: 12px; font-weight: 500; text-transform: uppercase; color: #6b7280; background: #f9fafb; border-bottom: 1px solid #e5e7eb;">Department Name</th>
              <th style="padding: 12px 24px; text-align: left; font-size: 12px; font-weight: 500; text-transform: uppercase; color: #6b7280; background: #f9fafb; border-bottom: 1px solid #e5e7eb;">Description</th>
              <th style="padding: 12px 24px; text-align: right; font-size: 12px; font-weight: 500; text-transform: uppercase; color: #6b7280; background: #f9fafb; border-bottom: 1px solid #e5e7eb;"><span style="position: absolute; width: 1px; height: 1px; padding: 0; margin: -1px; overflow: hidden; clip: rect(0, 0, 0, 0); white-space: nowrap; border-width: 0;">Actions</span></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="dept in departments" :key="dept.id" style="border-bottom: 1px solid #e5e7eb;">
              <td style="padding: 16px 24px;">
                <div style="font-weight: 500; color: #111827;">{{ dept.name }}</div>
              </td>
              <td style="padding: 16px 24px; color: #6b7280;">{{ dept.description }}</td>
              <td style="padding: 16px 24px; text-align: right;">
                <button @click="deleteDepartment(dept.id)" style="color: #2563eb; font-weight: 500; text-decoration: none; transition: color 0.2s; :hover { color: #1d4ed8; }">Delete</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Add Department Modal -->
    <div v-if="showAddDepartmentModal" style="position: fixed; inset: 0; background-color: rgba(107, 114, 128, 0.75); display: flex; align-items: center; justify-content: center; padding: 16px; z-index: 50;">
      <div style="background-color: white; border-radius: 8px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -1px rgba(0,0,0,0.06); padding: 24px; max-width: 500px; width: 100%;">
        <h3 style="font-size: 20px; font-weight: 600; color: #111827; margin-bottom: 24px;">Add New Department</h3>
        <div style="margin-bottom: 24px;">
          <div style="margin-bottom: 16px;">
            <label for="dept-name" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 6px;">Department Name</label>
            <input id="dept-name" v-model="newDepartmentName" type="text" style="width: 100%; border-radius: 6px; border: 1px solid #D1D5DB; padding: 10px 12px; font-size: 14px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s; box-sizing: border-box;" placeholder="Enter department name" />
          </div>
          <div>
            <label for="dept-desc" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 6px;">Description</label>
            <textarea id="dept-desc" v-model="newDepartmentDescription" rows="3" style="width: 100%; border-radius: 6px; border: 1px solid #D1D5DB; padding: 10px 12px; font-size: 14px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s; box-sizing: border-box;" placeholder="Enter department description"></textarea>
          </div>
        </div>
        <div style="display: flex; justify-content: flex-end; gap: 12px;">
          <button @click="closeAddDepartmentModal" style="padding: 10px 20px; background-color: #F3F4F6; color: #374151; border-radius: 6px; font-weight: 500; border: none; cursor: pointer; transition: background-color 0.2s;">Cancel</button>
          <button @click="addDepartment" :disabled="loading" style="padding: 10px 20px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; border-radius: 6px; font-weight: 500; border: none; cursor: pointer; transition: all 0.2s;" :style="loading ? 'opacity: 0.7; cursor: not-allowed;' : ''">
            {{ loading ? 'Adding...' : 'Add Department' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Assign Users Dialog -->
    <div v-if="selectedDepartment" style="position: fixed; inset: 0; background-color: rgba(107, 114, 128, 0.75); display: flex; align-items: center; justify-content: center; padding: 16px;">
      <div style="background-color: white; border-radius: 8px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -1px rgba(0,0,0,0.06); padding: 24px; max-width: 500px; width: 100%;">
        <h3 style="font-size: 20px; font-weight: 600; color: #111827; margin-bottom: 24px;">
          Assign Users to {{ selectedDepartment.name }}
        </h3>
        
        <div style="max-height: 300px; overflow-y: auto; margin-bottom: 24px; border: 1px solid #E5E7EB; border-radius: 6px;">
          <div v-for="user in users" :key="user.id" style="padding: 12px 16px; border-bottom: 1px solid #E5E7EB; :last-child { border-bottom: none; }">
            <label style="display: flex; align-items: center; font-size: 14px; color: #374151;">
              <input
                type="checkbox"
                :checked="selectedUserIds.includes(user.id)"
                @change="toggleUserSelection(user.id)"
                style="height: 16px; width: 16px; color: #3B82F6; border: 1px solid #D1D5DB; border-radius: 4px; margin-right: 12px;"
              />
              <span>{{ user.username }} <span style="color: #6B7280;">({{ user.email }})</span></span>
            </label>
          </div>
           <div v-if="users.length === 0" style="padding: 16px; text-align: center; color: #6B7280;">No users available to assign.</div>
        </div>
        
        <div style="display: flex; justify-content: flex-end; gap: 12px;">
          <button @click="cancelAssignUsers" class="btn btn-secondary" style="padding: 10px 20px; background-color: #E5E7EB; color: #374151; border-radius: 6px; font-weight: 500; border: none; cursor: pointer;">
            Cancel
          </button>
          <button @click="assignUsers" class="btn btn-primary" style="padding: 10px 20px; background-color: #3B82F6; color: white; border-radius: 6px; font-weight: 500; border: none; cursor: pointer;">
            Save Assignments
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Using inline styles as requested */
</style> 