<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useCVEsStore } from '../../stores/cves';
import { useSystemsStore } from '../../stores/systems';
import { useAuthStore } from '../../stores/auth';

const route = useRoute();
const router = useRouter();
const cvesStore = useCVEsStore();
const systemsStore = useSystemsStore();
const authStore = useAuthStore();

const cveId = computed(() => route.params.id);
const cve = computed(() => cvesStore.getCVEById(cveId.value));
const user = computed(() => authStore.user);

const newStatus = ref('');
const statusNote = ref('');
const loading = ref(false);
const showUpdateForm = ref(false);

// Check if the current user owns at least one affected system
const canUpdateStatus = computed(() => {
  if (!cve.value || !user.value) return false;
  
  if (authStore.isAdmin || authStore.isSecurityOfficer) return true;
  
  return cve.value.affectedSystems.some(systemId => {
    const system = systemsStore.getSystemById(systemId);
    return system && system.ownerId === user.value.id;
  });
});

// Get system name by ID
const getSystemNameById = (systemId) => {
  const system = systemsStore.getSystemById(systemId);
  return system ? system.name : 'Unknown System';
};

// Format date
const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString();
};

// Get severity class
const getSeverityClass = (severity) => {
  if (severity === 'critical' || severity === 'high') return 'badge-red';
  if (severity === 'medium') return 'badge-yellow';
  return 'badge-green';
};

// Get status class
const getStatusClass = (status) => {
  if (status === 'open') return 'badge-red';
  if (status === 'in_progress') return 'badge-yellow';
  if (status === 'resolved') return 'badge-green';
  if (status === 'accepted_risk') return 'badge-yellow';
  return 'badge'; // Default
};

// Update the CVE status
const updateCVEStatus = async () => {
  if (!newStatus.value || !statusNote.value) {
    alert('Please select a status and provide a note');
    return;
  }
  
  loading.value = true;
  
  try {
    await cvesStore.updateCVEStatus(
      cveId.value,
      newStatus.value,
      statusNote.value,
      user.value.id
    );
    
    showUpdateForm.value = false;
    newStatus.value = '';
    statusNote.value = '';
  } catch (error) {
    alert('Error updating CVE status');
  } finally {
    loading.value = false;
  }
};

// Mark notification as read if applicable
onMounted(() => {
  if (cve.value && user.value) {
    const hasUnreadNotification = cve.value.notifications.some(
      n => n.userId === user.value.id && !n.read
    );
    
    if (hasUnreadNotification) {
      cvesStore.markNotificationRead(cveId.value, user.value.id);
    }
  }
});
</script>

<template>
  <div>
    <div v-if="!cve" class="card bg-white text-center py-12">
      <h3 class="text-lg font-medium text-gray-700 mb-2">CVE not found</h3>
      <p class="text-gray-500 mb-4">The requested CVE could not be found</p>
      <router-link to="/cve" class="btn btn-primary">Return to CVE List</router-link>
    </div>
    
    <div v-else>
      <!-- Header -->
      <div class="mb-6 flex justify-between items-start">
        <div>
          <div class="flex items-center space-x-3">
            <h1 class="text-2xl font-bold text-gray-900">{{ cve.id }}</h1>
            <span class="badge" :class="getSeverityClass(cve.severity)">
              {{ cve.severity.charAt(0).toUpperCase() + cve.severity.slice(1) }}
            </span>
            <span class="badge" :class="getStatusClass(cve.status)">
              {{ cve.status.replace('_', ' ').charAt(0).toUpperCase() + cve.status.replace('_', ' ').slice(1) }}
            </span>
          </div>
          <p class="text-gray-600 mt-1">Discovered: {{ formatDate(cve.dateDiscovered) }}</p>
        </div>
        
        <div v-if="canUpdateStatus && !showUpdateForm">
          <button @click="showUpdateForm = true" class="btn btn-primary">
            Update Status
          </button>
        </div>
      </div>
      
      <!-- Status Update Form -->
      <div v-if="showUpdateForm" class="card bg-white mb-6">
        <h3 class="text-lg font-medium text-gray-900 mb-4">Update CVE Status</h3>
        
        <div class="mb-4">
          <label for="status" class="form-label">New Status</label>
          <select
            id="status"
            v-model="newStatus"
            class="form-input"
          >
            <option value="">Select a status</option>
            <option value="open">Open</option>
            <option value="in_progress">In Progress</option>
            <option value="resolved">Resolved</option>
            <option value="accepted_risk">Accepted Risk</option>
          </select>
        </div>
        
        <div class="mb-4">
          <label for="note" class="form-label">Status Note</label>
          <textarea
            id="note"
            v-model="statusNote"
            rows="3"
            class="form-input"
            placeholder="Provide details about the status change"
          ></textarea>
        </div>
        
        <div class="flex justify-end space-x-3">
          <button 
            type="button" 
            class="btn btn-secondary" 
            @click="showUpdateForm = false"
          >
            Cancel
          </button>
          <button 
            type="button" 
            class="btn btn-primary"
            :disabled="loading"
            @click="updateCVEStatus"
          >
            {{ loading ? 'Updating...' : 'Update Status' }}
          </button>
        </div>
      </div>
      
      <!-- Main content -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Left column: Description and Systems -->
        <div class="lg:col-span-2">
          <div class="card bg-white mb-6">
            <h3 class="text-lg font-medium text-gray-900 mb-2">Description</h3>
            <p class="text-gray-700">{{ cve.description }}</p>
          </div>
          
          <div class="card bg-white mb-6">
            <h3 class="text-lg font-medium text-gray-900 mb-4">Affected Systems</h3>
            
            <div v-if="cve.affectedSystems.length === 0" class="text-gray-500">
              No systems affected
            </div>
            
            <div v-else class="space-y-2">
              <div v-for="systemId in cve.affectedSystems" :key="systemId" class="p-3 border border-gray-200 rounded-md">
                <div class="font-medium">{{ getSystemNameById(systemId) }}</div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Right column: External links and status history -->
        <div>
          <div class="card bg-white mb-6">
            <h3 class="text-lg font-medium text-gray-900 mb-4">External References</h3>
            
            <div v-if="cve.externalLinks.length === 0" class="text-gray-500">
              No external references available
            </div>
            
            <div v-else class="space-y-2">
              <div v-for="(link, index) in cve.externalLinks" :key="index">
                <a :href="link.url" target="_blank" rel="noopener noreferrer" class="text-blue-600 hover:underline">
                  {{ link.title }}
                </a>
              </div>
            </div>
          </div>
          
          <div class="card bg-white">
            <h3 class="text-lg font-medium text-gray-900 mb-4">Status History</h3>
            
            <div v-if="cve.statusHistory.length === 0" class="text-gray-500">
              No status changes recorded
            </div>
            
            <div v-else class="space-y-4">
              <div v-for="(entry, index) in cve.statusHistory" :key="index" class="border-b border-gray-200 pb-4 last:border-0 last:pb-0">
                <div class="flex justify-between">
                  <span class="badge" :class="getStatusClass(entry.status)">
                    {{ entry.status.replace('_', ' ').charAt(0).toUpperCase() + entry.status.replace('_', ' ').slice(1) }}
                  </span>
                  <span class="text-sm text-gray-500">{{ formatDate(entry.timestamp) }}</span>
                </div>
                <p class="text-gray-700 mt-2">{{ entry.note }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template> 