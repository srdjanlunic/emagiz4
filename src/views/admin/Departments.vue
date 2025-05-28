<script setup>
import { ref, computed } from 'vue';
import { useAdminStore } from '../../stores/admin';

const adminStore = useAdminStore();
const departments = computed(() => adminStore.departments);
const users = computed(() => adminStore.users);
const loading = ref(false);

// Placeholder for creating a new department
const newDepartment = ref({
  name: '',
  userIds: []
});

// Selected department for assigning users
const selectedDepartment = ref(null);
const selectedUserIds = ref([]);

// Add a department (demo purposes)
const addDepartment = async () => {
  if (!newDepartment.value.name) {
    alert('Please enter a department name');
    return;
  }
  
  loading.value = true;
  try {
    await adminStore.createDepartment({
      name: newDepartment.value.name,
      userIds: []
    });
    newDepartment.value.name = '';
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

// Delete a department (demo purposes)
const deleteDepartment = async (deptId) => {
  if (confirm('Are you sure you want to delete this department?')) {
    try {
      await adminStore.deleteDepartment(deptId);
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
  <div>
    <div class="mb-6 flex justify-between items-center">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Departments</h1>
        <p class="text-gray-600">Manage departments and assign users</p>
      </div>
    </div>
    
    <!-- Add Department Form -->
    <div class="card bg-white mb-6">
      <h2 class="text-lg font-medium text-gray-900 mb-4">Add New Department</h2>
      
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label for="name" class="form-label">Department Name</label>
          <input
            id="name"
            v-model="newDepartment.name"
            type="text"
            class="form-input"
            placeholder="Enter department name"
          />
        </div>
      </div>
      
      <div class="mt-4">
        <button @click="addDepartment" class="btn btn-primary" :disabled="loading">
          {{ loading ? 'Adding...' : 'Add Department' }}
        </button>
      </div>
    </div>
    
    <!-- Departments List -->
    <div class="card bg-white">
      <h2 class="text-lg font-medium text-gray-900 mb-4">Departments</h2>
      
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Department Name
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Users
              </th>
              <th scope="col" class="relative px-6 py-3">
                <span class="sr-only">Actions</span>
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="dept in departments" :key="dept.id">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="font-medium text-gray-900">{{ dept.name }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ getDepartmentUsers(dept.userIds) || 'No users assigned' }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <button @click="openAssignUsers(dept)" class="text-blue-600 hover:text-blue-900 mr-4">
                  Assign Users
                </button>
                <button v-if="dept.id !== 1" @click="deleteDepartment(dept.id)" class="text-red-600 hover:text-red-900">
                  Delete
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    
    <!-- Assign Users Dialog -->
    <div v-if="selectedDepartment" class="fixed inset-0 bg-gray-500 bg-opacity-75 flex items-center justify-center">
      <div class="bg-white rounded-lg p-6 max-w-md w-full">
        <h3 class="text-lg font-medium text-gray-900 mb-4">
          Assign Users to {{ selectedDepartment.name }}
        </h3>
        
        <div class="max-h-60 overflow-y-auto mb-4">
          <div v-for="user in users" :key="user.id" class="py-2">
            <label class="flex items-center">
              <input
                type="checkbox"
                :checked="selectedUserIds.includes(user.id)"
                @change="toggleUserSelection(user.id)"
                class="h-4 w-4 text-blue-600 border-gray-300 rounded"
              />
              <span class="ml-2">{{ user.username }} ({{ user.email }})</span>
            </label>
          </div>
        </div>
        
        <div class="flex justify-end space-x-3">
          <button @click="cancelAssignUsers" class="btn btn-secondary">
            Cancel
          </button>
          <button @click="assignUsers" class="btn btn-primary">
            Save
          </button>
        </div>
      </div>
    </div>
  </div>
</template> 