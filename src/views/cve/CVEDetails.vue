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
  if (severity === 'critical' || severity === 'high') return 'background-color: #fee2e2; color: #991b1b;';
  if (severity === 'medium') return 'background-color: #fef3c7; color: #92400e;';
  return 'background-color: #d1fae5; color: #065f46;';
};

// Get status class
const getStatusClass = (status) => {
  if (status === 'open') return 'background-color: #fee2e2; color: #991b1b;';
  if (status === 'in_progress') return 'background-color: #fef3c7; color: #92400e;';
  if (status === 'resolved') return 'background-color: #d1fae5; color: #065f46;';
  if (status === 'accepted_risk') return 'background-color: #fef3c7; color: #92400e;';
  return 'background-color: #f3f4f6; color: #374151;'; // Default
};

// Update the CVE status
const updateCVEStatus = async () => {
  if (!newStatus.value || !statusNote.value) {
    alert('Please select a status and provide a note');
    return;
  }
  
  if (!cve.value.affectedSystems || cve.value.affectedSystems.length === 0) {
    alert('This CVE is not associated with any system.');
    return;
  }

  // For simplicity, we'll update the status for the first affected system.
  // A more advanced implementation would let the user choose.
  const systemIdToUpdate = cve.value.affectedSystems[0];

  loading.value = true;
  
  try {
    await cvesStore.updateCVEStatus(
      cveId.value,
      systemIdToUpdate,
      newStatus.value,
      statusNote.value
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
  <div style="padding: 24px;">
    <div v-if="!cve" style="background: white; border-radius: 12px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); overflow: hidden; text-align: center; padding: 48px 24px;">
      <h3 style="font-size: 18px; font-weight: 500; color: #374151; margin-bottom: 8px;">CVE not found</h3>
      <p style="color: #6b7280; margin-bottom: 24px;">The requested CVE could not be found</p>
      <router-link to="/cve" style="display: inline-flex; align-items: center; padding: 10px 20px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; border: none; border-radius: 8px; font-size: 14px; font-weight: 500; text-decoration: none;">Return to CVE List</router-link>
    </div>
    
    <div v-else>
      <!-- Header -->
      <div style="display: flex; justify-content: space-between; align-items: start; margin-bottom: 32px;">
        <div>
          <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 8px;">
            <h1 style="font-size: 30px; font-weight: bold; color: #111827;">{{ cve.id }}</h1>
            <span style="display: inline-block; padding: 4px 12px; border-radius: 9999px; font-size: 13px; font-weight: 500;" :style="getSeverityClass(cve.severity)">
              {{ cve.severity.charAt(0).toUpperCase() + cve.severity.slice(1) }}
            </span>
            <span style="display: inline-block; padding: 4px 12px; border-radius: 9999px; font-size: 13px; font-weight: 500;" :style="getStatusClass(cve.status)">
              {{ cve.status.replace('_', ' ').charAt(0).toUpperCase() + cve.status.replace('_', ' ').slice(1) }}
            </span>
          </div>
          <p style="color: #6b7280; font-size: 14px;">Discovered: {{ formatDate(cve.dateDiscovered) }}</p>
        </div>
        
        <div v-if="canUpdateStatus && !showUpdateForm">
          <button @click="showUpdateForm = true" style="display: inline-flex; align-items: center; padding: 10px 20px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; border: none; border-radius: 8px; font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.2s; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);">
            Update Status
          </button>
        </div>
      </div>
      
      <!-- Status Update Form -->
      <div v-if="showUpdateForm" style="background: white; border-radius: 12px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); padding: 24px; margin-bottom: 24px;">
        <h3 style="font-size: 18px; font-weight: 600; color: #111827; margin-bottom: 16px;">Update CVE Status</h3>
        
        <div style="display: grid; gap: 16px; margin-bottom: 24px;">
          <div>
            <label for="status" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 6px;">New Status</label>
            <div style="position: relative;">
              <select id="status" v-model="newStatus" style="width: 100%; border-radius: 6px; border: 1px solid #D1D5DB; padding: 10px 40px 10px 12px; font-size: 14px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s; box-sizing: border-box; appearance: none;">
                <option value="">Select a status</option>
                <option value="open">Open</option>
                <option value="in_progress">In Progress</option>
                <option value="resolved">Resolved</option>
                <option value="accepted_risk">Accepted Risk</option>
              </select>
              <div style="pointer-events: none; position: absolute; top: 0; right: 0; bottom: 0; display: flex; align-items: center; padding: 0 12px; color: #6b7280;">
                <svg style="width: 16px; height: 16px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 9l4-4 4 4m0 6l-4 4-4-4" />
                </svg>
                             </div>
             </div>
          </div>
          
          <div>
            <label for="note" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 6px;">Status Note</label>
            <textarea id="note" v-model="statusNote" rows="3" style="width: 100%; border-radius: 6px; border: 1px solid #D1D5DB; padding: 10px 12px; font-size: 14px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s; box-sizing: border-box; resize: vertical; min-height: 80px;" placeholder="Provide details about the status change"></textarea>
          </div>
        </div>
        
        <div style="display: flex; justify-content: flex-end; gap: 12px;">
          <button @click="showUpdateForm = false" style="padding: 10px 20px; background-color: #F3F4F6; color: #374151; border-radius: 6px; font-weight: 500; border: none; cursor: pointer; transition: background-color 0.2s;">Cancel</button>
          <button @click="updateCVEStatus" :disabled="loading" style="padding: 10px 20px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; border-radius: 6px; font-weight: 500; border: none; cursor: pointer; transition: all 0.2s;" :style="loading ? 'opacity: 0.7; cursor: not-allowed;' : ''">
            {{ loading ? 'Updating...' : 'Update Status' }}
          </button>
        </div>
      </div>
      
      <!-- Main content -->
      <div style="display: grid; grid-template-columns: repeat(1, minmax(0, 1fr)); gap: 24px;" :style="'@media (min-width: 1024px) { grid-template-columns: repeat(3, minmax(0, 1fr)); }'">
        <!-- Left column: Description and Systems -->
        <div style="grid-column: span 1 / span 1;" :style="'@media (min-width: 1024px) { grid-column: span 2 / span 2; }'">
          <div style="background: white; border-radius: 12px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); padding: 24px; margin-bottom: 24px;">
            <h3 style="font-size: 18px; font-weight: 600; color: #111827; margin-bottom: 16px;">Description</h3>
            <p style="color: #374151; line-height: 1.6;">{{ cve.description }}</p>
          </div>
          
          <div style="background: white; border-radius: 12px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); padding: 24px;">
            <h3 style="font-size: 18px; font-weight: 600; color: #111827; margin-bottom: 16px;">Affected Systems</h3>
            
            <div v-if="cve.affectedSystems.length === 0" style="color: #6b7280; font-style: italic;">
              No systems affected
            </div>
            
            <div v-else style="display: grid; gap: 12px;">
              <div v-for="systemId in cve.affectedSystems" :key="systemId" style="padding: 12px; border: 1px solid #E5E7EB; border-radius: 8px; background: #F9FAFB;">
                <div style="font-weight: 500; color: #111827;">{{ getSystemNameById(systemId) }}</div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Right column: External links and status history -->
        <div>
          <div style="background: white; border-radius: 12px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); padding: 24px; margin-bottom: 24px;">
            <h3 style="font-size: 18px; font-weight: 600; color: #111827; margin-bottom: 16px;">External References</h3>
            
            <div v-if="cve.externalLinks.length === 0" style="color: #6b7280; font-style: italic;">
              No external references available
            </div>
            
            <div v-else style="display: grid; gap: 8px;">
              <div v-for="(link, index) in cve.externalLinks" :key="index">
                <a :href="link.url" target="_blank" rel="noopener noreferrer" style="color: #2563eb; font-weight: 500; text-decoration: none; transition: color 0.2s; display: flex; align-items: center; gap: 4px;">
                  {{ link.title }}
                  <svg style="width: 12px; height: 12px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
                  </svg>
                </a>
              </div>
            </div>
          </div>
          
          <div style="background: white; border-radius: 12px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); padding: 24px;">
            <h3 style="font-size: 18px; font-weight: 600; color: #111827; margin-bottom: 16px;">Status History</h3>
            
            <div v-if="cve.statusHistory.length === 0" style="color: #6b7280; font-style: italic;">
              No status changes recorded
            </div>
            
            <div v-else style="display: grid; gap: 16px;">
              <div v-for="(entry, index) in cve.statusHistory" :key="index" style="border-bottom: 1px solid #E5E7EB; padding-bottom: 16px;" :style="index === cve.statusHistory.length - 1 ? 'border-bottom: none; padding-bottom: 0;' : ''">
                <div style="display: flex; justify-content: space-between; align-items: start; margin-bottom: 8px;">
                  <span style="display: inline-block; padding: 4px 12px; border-radius: 9999px; font-size: 13px; font-weight: 500;" :style="getStatusClass(entry.status)">
                    {{ entry.status.replace('_', ' ').charAt(0).toUpperCase() + entry.status.replace('_', ' ').slice(1) }}
                  </span>
                  <span style="font-size: 12px; color: #6b7280;">{{ formatDate(entry.timestamp) }}</span>
                </div>
                <p style="color: #374151; font-size: 14px; line-height: 1.5;">{{ entry.note }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template> 