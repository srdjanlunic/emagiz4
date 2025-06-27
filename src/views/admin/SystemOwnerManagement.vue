<script setup>
import { ref, computed, onMounted } from 'vue';
import { useAdminStore } from '../../stores/admin';
import { useSystemsStore } from '../../stores/systems';
import { useAuthStore } from '../../stores/auth';

const adminStore = useAdminStore();
const systemsStore = useSystemsStore();
const authStore = useAuthStore();

const loading = ref(false);
const error = ref(null);
const searchQuery = ref('');
const showAssignModal = ref(false);
const loadingAssignment = ref(false);

// Get data
const users = computed(() => adminStore.users);
const systems = computed(() => systemsStore.systems);

// Filter system owners only
const systemOwners = computed(() => {
  return users.value.filter(user => user.roleName === 'system_owner');
});

// Filter systems and owners based on search
const filteredData = computed(() => {
  if (!searchQuery.value) {
    return systems.value;
  }
  return systems.value.filter(system =>
    system.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    system.vendor.toLowerCase().includes(searchQuery.value.toLowerCase())
  );
});

// Assignment form
const assignmentForm = ref({
  userId: '',
  systemId: '',
  implementationId: ''
});

// Modal state
const selectedSystem = ref(null);
const systemImplementations = ref([]);

// Get owner name by ID
const getOwnerName = (ownerId) => {
  if (!ownerId) return 'Unassigned';
  const owner = users.value.find(u => u.id === ownerId);
  return owner ? owner.username : 'Unknown User';
};

// Open assignment modal for a system
const openAssignModal = async (system) => {
  selectedSystem.value = system;
  assignmentForm.value.systemId = system.id;
  assignmentForm.value.userId = '';
  assignmentForm.value.implementationId = '';
  
  try {
    // Fetch implementations for this system
    systemImplementations.value = await systemsStore.fetchImplementationsForSystem(system.id);
  } catch (err) {
    console.error('Error fetching implementations:', err);
    systemImplementations.value = [];
  }
  
  showAssignModal.value = true;
};

// Close assignment modal
const closeAssignModal = () => {
  showAssignModal.value = false;
  selectedSystem.value = null;
  systemImplementations.value = [];
  assignmentForm.value = { userId: '', systemId: '', implementationId: '' };
};

// Assign owner to system implementation
const assignOwner = async () => {
  if (!assignmentForm.value.userId || !assignmentForm.value.implementationId) {
    alert('Please select both a user and an implementation');
    return;
  }
  
  loadingAssignment.value = true;
  error.value = null;
  
  try {
    const response = await authStore.apiCall('/owners', {
      method: 'POST',
      body: JSON.stringify({
        userId: assignmentForm.value.userId,
        implementationId: assignmentForm.value.implementationId
      })
    });
    
    // Refresh systems data to show updated owner
    await systemsStore.fetchSystems(1, 100);
    closeAssignModal();
  } catch (err) {
    error.value = err.message;
  } finally {
    loadingAssignment.value = false;
  }
};

// Remove owner assignment
const removeOwner = async (system) => {
  if (!system.ownerId) {
    alert('No owner assigned to remove');
    return;
  }
  
  if (!confirm(`Are you sure you want to remove ${getOwnerName(system.ownerId)} as owner of ${system.name}?`)) {
    return;
  }
  
  loading.value = true;
  error.value = null;
  
  try {
    // First, need to get the implementation ID for this system
    const implementations = await systemsStore.fetchImplementationsForSystem(system.id);
    if (implementations.length > 0) {
      const implId = implementations[0].id; // Use first implementation
      
      await authStore.apiCall(`/owners/${system.ownerId}/${implId}`, {
        method: 'DELETE'
      });
      
      // Refresh systems data
      await systemsStore.fetchSystems(1, 100);
    }
  } catch (err) {
    error.value = err.message;
  } finally {
    loading.value = false;
  }
};

// Load data on mount
onMounted(async () => {
  loading.value = true;
  try {
    await Promise.all([
      adminStore.fetchUsers(),
      systemsStore.fetchSystems(1, 100)
    ]);
  } catch (err) {
    error.value = err.message;
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div style="padding: 24px;">
    <!-- Header -->
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 32px;">
      <div>
        <h1 style="font-size: 24px; font-weight: bold; color: #111827; margin-bottom: 4px;">System Owner Management</h1>
        <p style="color: #6b7280; font-size: 14px;">Assign and manage system ownership</p>
      </div>
    </div>

    <!-- Error message -->
    <div v-if="error" style="background: #fee2e2; border: 1px solid #fca5a5; border-radius: 12px; padding: 16px; margin-bottom: 24px;">
      <div style="display: flex; align-items: center; gap: 8px;">
        <svg style="width: 20px; height: 20px; color: #dc2626;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
        </svg>
        <span style="color: #dc2626; font-weight: 500;">{{ error }}</span>
      </div>
    </div>

    <!-- Search -->
    <div style="margin-bottom: 24px;">
      <div style="position: relative; max-width: 500px;">
        <svg style="position: absolute; left: 12px; top: 50%; transform: translateY(-50%); width: 16px; height: 16px; color: #9ca3af;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
        <input v-model="searchQuery" type="text" placeholder="Search systems..." style="width: 100%; border-radius: 8px; border: 1px solid #D1D5DB; padding: 12px 16px 12px 40px; font-size: 14px; color: #111827; background-color: white; transition: all 0.2s; box-sizing: border-box;">
      </div>
    </div>

    <!-- Loading state -->
    <div v-if="loading && systems.length === 0" style="text-align: center; padding: 48px;">
      <div style="background: white; border-radius: 12px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); padding: 48px 24px;">
        <div style="width: 32px; height: 32px; border: 3px solid #f3f4f6; border-radius: 50%; border-top-color: #3b82f6; animation: spin 1s ease-in-out infinite; margin: 0 auto 16px;"></div>
        <p style="color: #6b7280;">Loading systems...</p>
      </div>
    </div>

    <!-- Systems table -->
    <div v-else style="background: white; border-radius: 12px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); overflow: hidden;">
      <div v-if="filteredData.length === 0" style="text-align: center; padding: 48px 24px;">
        <h3 style="font-size: 18px; font-weight: 500; color: #374151; margin-bottom: 8px;">No systems found</h3>
        <p style="color: #6b7280;">No systems match your search criteria</p>
      </div>

      <div v-else style="overflow-x: auto;">
        <table style="width: 100%; border-collapse: separate; border-spacing: 0;">
          <thead>
            <tr>
              <th style="padding: 12px 24px; text-align: left; font-size: 12px; font-weight: 500; text-transform: uppercase; color: #6b7280; background: #f9fafb; border-bottom: 1px solid #e5e7eb;">System</th>
              <th style="padding: 12px 24px; text-align: left; font-size: 12px; font-weight: 500; text-transform: uppercase; color: #6b7280; background: #f9fafb; border-bottom: 1px solid #e5e7eb;">Vendor</th>
              <th style="padding: 12px 24px; text-align: left; font-size: 12px; font-weight: 500; text-transform: uppercase; color: #6b7280; background: #f9fafb; border-bottom: 1px solid #e5e7eb;">Current Owner</th>
              <th style="padding: 12px 24px; text-align: left; font-size: 12px; font-weight: 500; text-transform: uppercase; color: #6b7280; background: #f9fafb; border-bottom: 1px solid #e5e7eb;">Risk Score</th>
              <th style="padding: 12px 24px; text-align: right; font-size: 12px; font-weight: 500; text-transform: uppercase; color: #6b7280; background: #f9fafb; border-bottom: 1px solid #e5e7eb;">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="system in filteredData" :key="system.id" style="border-bottom: 1px solid #e5e7eb;">
              <td style="padding: 16px 24px;">
                <div style="font-weight: 500; color: #111827; margin-bottom: 4px;">{{ system.name }}</div>
                <div v-if="system.version" style="font-size: 13px; color: #6b7280;">v{{ system.version }}</div>
              </td>
              <td style="padding: 16px 24px; color: #6b7280;">{{ system.vendor }}</td>
              <td style="padding: 16px 24px;">
                <span v-if="system.ownerId" style="display: inline-block; padding: 4px 12px; border-radius: 9999px; font-size: 13px; font-weight: 500; background-color: #dbeafe; color: #1d4ed8;">
                  {{ getOwnerName(system.ownerId) }}
                </span>
                <span v-else style="display: inline-block; padding: 4px 12px; border-radius: 9999px; font-size: 13px; font-weight: 500; background-color: #fee2e2; color: #991b1b;">
                  Unassigned
                </span>
              </td>
              <td style="padding: 16px 24px;">
                <div style="font-weight: 500;" :style="system.riskScore > 60 ? 'color: #dc2626;' : system.riskScore > 30 ? 'color: #d97706;' : 'color: #059669;'">
                  {{ system.riskScore || 'N/A' }}
                </div>
              </td>
              <td style="padding: 16px 24px; text-align: right;">
                <button @click="openAssignModal(system)" style="color: #2563eb; font-weight: 500; text-decoration: none; transition: color 0.2s; background: none; border: none; cursor: pointer; margin-right: 16px;">
                  {{ system.ownerId ? 'Reassign' : 'Assign' }}
                </button>
                <button v-if="system.ownerId" @click="removeOwner(system)" style="color: #dc2626; font-weight: 500; text-decoration: none; transition: color 0.2s; background: none; border: none; cursor: pointer;">
                  Remove
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Assignment Modal -->
    <div v-if="showAssignModal" style="position: fixed; inset: 0; background-color: rgba(107, 114, 128, 0.75); display: flex; align-items: center; justify-content: center; padding: 16px; z-index: 50;">
      <div style="background-color: white; border-radius: 8px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -1px rgba(0,0,0,0.06); padding: 24px; max-width: 500px; width: 100%;">
        <h3 style="font-size: 20px; font-weight: 600; color: #111827; margin-bottom: 24px;">
          Assign Owner to {{ selectedSystem?.name }}
        </h3>
        
        <div style="display: grid; gap: 16px; margin-bottom: 24px;">
          <div>
            <label style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 6px;">System Owner</label>
            <select v-model="assignmentForm.userId" style="width: 100%; border-radius: 6px; border: 1px solid #D1D5DB; padding: 10px 12px; font-size: 14px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s; box-sizing: border-box; appearance: none; background-image: url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAiIGhlaWdodD0iMjAiIHZpZXdCb3g9IjAgMCAyMCAyMCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTYgOEwxMCAxMkwxNCA4IiBzdHJva2U9IiM2QjcyODAiIHN0cm9rZS13aWR0aD0iMS41IiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiLz4KPC9zdmc+'); background-position: right 12px center; background-repeat: no-repeat; background-size: 16px 16px; padding-right: 40px;">
              <option value="">Select a system owner...</option>
              <option v-for="owner in systemOwners" :key="owner.id" :value="owner.id">
                {{ owner.username }} ({{ owner.email }})
              </option>
            </select>
          </div>
          
          <div>
            <label style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 6px;">System Implementation</label>
            <select v-model="assignmentForm.implementationId" style="width: 100%; border-radius: 6px; border: 1px solid #D1D5DB; padding: 10px 12px; font-size: 14px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s; box-sizing: border-box; appearance: none; background-image: url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAiIGhlaWdodD0iMjAiIHZpZXdCb3g9IjAgMCAyMCAyMCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTYgOEwxMCAxMkwxNCA4IiBzdHJva2U9IiM2QjcyODAiIHN0cm9rZS13aWR0aD0iMS41IiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiLz4KPC9zdmc+'); background-position: right 12px center; background-repeat: no-repeat; background-size: 16px 16px; padding-right: 40px;">
              <option value="">Select an implementation...</option>
              <option v-for="impl in systemImplementations" :key="impl.id" :value="impl.id">
                {{ impl.version ? `v${impl.version}` : 'No version' }} - {{ impl.environment || 'No environment' }}
              </option>
            </select>
            <p style="font-size: 13px; color: #6b7280; margin-top: 4px;">Select which implementation this owner will be responsible for</p>
          </div>
        </div>
        
        <div style="display: flex; justify-content: flex-end; gap: 12px;">
          <button @click="closeAssignModal" :disabled="loadingAssignment" style="padding: 10px 20px; background-color: #F3F4F6; color: #374151; border-radius: 6px; font-weight: 500; border: none; cursor: pointer; transition: background-color 0.2s;">
            Cancel
          </button>
          <button @click="assignOwner" :disabled="loadingAssignment" style="padding: 10px 20px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; border-radius: 6px; font-weight: 500; border: none; cursor: pointer; transition: all 0.2s; display: flex; align-items: center; gap: 8px;">
            <svg v-if="loadingAssignment" style="width: 16px; height: 16px; animation: spin 1s linear infinite;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
            {{ loadingAssignment ? 'Assigning...' : 'Assign Owner' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@keyframes spin {
  to { transform: rotate(360deg); }
}

input:focus, select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}
</style> 