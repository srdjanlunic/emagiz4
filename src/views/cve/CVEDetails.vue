<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useCVEsStore } from '../../stores/cves';
import { useSystemsStore } from '../../stores/systems';
import { useAuthStore } from '../../stores/auth';
import { useAdminStore } from '../../stores/admin';

const route = useRoute();
const router = useRouter();
const cvesStore = useCVEsStore();
const systemsStore = useSystemsStore();
const authStore = useAuthStore();
const adminStore = useAdminStore();

const cveId = computed(() => route.params.id);
const cve = computed(() => cvesStore.getCVEById(cveId.value));
const user = computed(() => authStore.user);

const newStatus = ref('');
const statusNote = ref('');
const loading = ref(false);
const showUpdateForm = ref(false);
const showEscalateModal = ref(false);
const escalationReason = ref('');

const showAssignSystemsModal = ref(false);
const selectedSystemIds = ref([]);
const assigningLoading = ref(false);

// Technical experts pool - escalations are available to all experts

// Check if the current user owns at least one affected system
const canUpdateStatus = computed(() => {
  if (!cve.value || !user.value) return false;
  
  if (authStore.isAdmin || authStore.isSecurityOfficer) return true;
  
  return cve.value.affectedSystems.some(systemId => {
    const system = systemsStore.getSystemById(systemId);
    return system && system.ownerId === user.value.id;
  });
});

// Check if user can manage affected systems (Admins and Security Officers only)
const canManageSystems = computed(() => {
  return authStore.isAdmin || authStore.isSecurityOfficer;
});

// Check if user can escalate CVEs
const canEscalate = computed(() => {
  if (!cve.value) return false;
  
  // Admins can always escalate
  if (authStore.isAdmin) return true;
  
  // Security officers and system owners can escalate
  if (!authStore.isSecurityOfficer && !authStore.isSystemOwner) return false;
  
  // System owners can only escalate CVEs affecting their systems
  if (authStore.isSystemOwner && !authStore.isSecurityOfficer) {
    const userOwnsAffectedSystem = cve.value.affectedSystems?.some(systemId => {
      const system = systemsStore.getSystemById(systemId);
      return system && system.ownerId === user.value.id;
    });
    if (!userOwnsAffectedSystem) return false;
  }
  
  // Security officers: Can escalate if CVE is critical OR if status is unclear/unknown
  if (authStore.isSecurityOfficer) {
    const isCritical = cve.value.severity?.toLowerCase() === 'critical';
    const isUnclearStatus = !cve.value.status || cve.value.status === 'open' || cve.value.status === 'unclear';
    return isCritical || isUnclearStatus;
  }
  
  // System owners: Can always escalate CVEs affecting their systems
  return true;
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
  if (status === 'false_positive') return 'background-color: #e5e7eb; color: #374151;';
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

const openEscalateModal = () => {
    showEscalateModal.value = true;
};

const escalateCVE = async () => {
    if (!escalationReason.value) {
        alert('Please provide a reason for escalation.');
        return;
    }

    const systemIdToEscalate = cve.value.affectedSystems?.[0];
    
    if (!systemIdToEscalate) {
        alert('This CVE is not associated with any systems. Please assign it to a system first.');
        return;
    }
    
    try {
        // Escalate without assigning to specific technical expert - available to all experts
        await cvesStore.escalateCVE(cveId.value, systemIdToEscalate, escalationReason.value, null);
        showEscalateModal.value = false;
        escalationReason.value = '';
    } catch (error) {
        console.error('Escalation error:', error);
        alert('Failed to escalate CVE.');
    }
};

const openAssignSystemsModal = () => {
  // Initialize with current affected systems
  selectedSystemIds.value = [...(cve.value.affectedSystems || [])];
  showAssignSystemsModal.value = true;
};

const updateAffectedSystems = async () => {
  assigningLoading.value = true;
  
  try {
    await cvesStore.updateAffectedSystems(cveId.value, selectedSystemIds.value);
    showAssignSystemsModal.value = false;
  } catch (error) {
    // Error handled by store
  } finally {
    assigningLoading.value = false;
  }
};

// Technical Expert Actions
const markFalsePositive = async () => {
  if (!confirm('Are you sure you want to mark this CVE as a false positive?')) {
    return;
  }
  
  try {
    // Update status to false positive with technical expert analysis
    const note = 'Marked as false positive by technical expert after analysis.';
    
    if (cve.value.affectedSystems && cve.value.affectedSystems.length > 0) {
      await cvesStore.updateCVEStatus(
        cveId.value,
        cve.value.affectedSystems[0],
        'resolved',
        note
      );
    }
  } catch (error) {
    alert('Error marking CVE as false positive');
  }
};

const recommendUpdate = async () => {
  const recommendation = prompt('Please provide update recommendation details:');
  if (!recommendation) return;
  
  try {
    const note = `Technical expert recommends update: ${recommendation}`;
    
    if (cve.value.affectedSystems && cve.value.affectedSystems.length > 0) {
      await cvesStore.updateCVEStatus(
        cveId.value,
        cve.value.affectedSystems[0],
        'in_progress',
        note
      );
    }
  } catch (error) {
    alert('Error adding update recommendation');
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
  
  // Load systems for the assign systems functionality
  systemsStore.fetchSystems();
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
          <button v-if="canEscalate" @click="openEscalateModal" style="display: inline-flex; align-items: center; padding: 10px 20px; background-color: #f59e0b; color: white; border: none; border-radius: 8px; font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.2s; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); margin-left: 12px;">
            Escalate
          </button>
          <button v-if="canManageSystems" @click="openAssignSystemsModal" style="display: inline-flex; align-items: center; padding: 10px 20px; background: linear-gradient(135deg, #059669 0%, #047857 100%); color: white; border: none; border-radius: 8px; font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.2s; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); margin-left: 12px;">
            <svg style="width: 16px; height: 16px; margin-right: 8px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
            </svg>
            Assign Systems
          </button>
          <button @click="showUpdateForm = true" style="display: inline-flex; align-items: center; padding: 10px 20px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; border: none; border-radius: 8px; font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.2s; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); margin-left: 12px;">
            Update Status
          </button>
        </div>
        
        <!-- Technical Expert Actions -->
        <div v-if="authStore.isTechnicalExpert && !showUpdateForm">
          <button @click="markFalsePositive" style="display: inline-flex; align-items: center; padding: 10px 20px; background-color: #6b7280; color: white; border: none; border-radius: 8px; font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.2s; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); margin-left: 12px;">
            Mark False Positive
          </button>
          <button @click="recommendUpdate" style="display: inline-flex; align-items: center; padding: 10px 20px; background-color: #059669; color: white; border: none; border-radius: 8px; font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.2s; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); margin-left: 12px;">
            Recommend Update
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
                <option v-if="authStore.isTechnicalExpert" value="false_positive">False Positive</option>
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
      
      <!-- Escalate Modal -->
      <div v-if="showEscalateModal" style="position: fixed; inset: 0; background-color: rgba(107, 114, 128, 0.75); display: flex; align-items: center; justify-content: center; padding: 16px; z-index: 50;">
        <div style="background-color: white; border-radius: 8px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -1px rgba(0,0,0,0.06); padding: 24px; max-width: 500px; width: 100%;">
            <h3 style="font-size: 20px; font-weight: 600; color: #111827; margin-bottom: 16px;">Escalate CVE to Technical Experts</h3>
            <p style="color: #6b7280; font-size: 14px; margin-bottom: 24px;">
              This CVE will be escalated and made available to all technical experts for analysis and recommendation.
            </p>
            <div style="display: grid; gap: 16px; margin-bottom: 24px;">
                <div>
                    <label for="escalation-reason" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 6px;">Escalation Reason</label>
                    <textarea id="escalation-reason" v-model="escalationReason" rows="4" style="width: 100%; border-radius: 6px; border: 1px solid #D1D5DB; padding: 10px 12px; font-size: 14px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s; box-sizing: border-box;" placeholder="Describe why this CVE requires technical expert analysis (e.g., unclear impact, complex vulnerability, etc.)"></textarea>
                </div>
                <div style="background-color: #f0f9ff; border: 1px solid #0ea5e9; border-radius: 6px; padding: 12px;">
                    <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 4px;">
                        <svg style="width: 16px; height: 16px; color: #0ea5e9;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                        </svg>
                        <span style="font-size: 14px; font-weight: 500; color: #0ea5e9;">Escalation to Expert Pool</span>
                    </div>
                    <p style="font-size: 13px; color: #075985;">CVE will be available to all technical experts. Any expert can pick it up for analysis.</p>
                </div>
            </div>
            <div style="display: flex; justify-content: flex-end; gap: 12px;">
                <button @click="showEscalateModal = false" style="padding: 10px 20px; background-color: #F3F4F6; color: #374151; border-radius: 6px; font-weight: 500; border: none; cursor: pointer;">Cancel</button>
                <button @click="escalateCVE" style="padding: 10px 20px; background-color: #f59e0b; color: white; border-radius: 6px; font-weight: 500; border: none; cursor: pointer;">
                  Escalate to Experts
                </button>
            </div>
        </div>
      </div>

      <!-- Assign Systems Modal -->
      <div v-if="showAssignSystemsModal" style="position: fixed; inset: 0; background-color: rgba(107, 114, 128, 0.75); display: flex; align-items: center; justify-content: center; padding: 16px; z-index: 50;">
        <div style="background-color: white; border-radius: 8px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -1px rgba(0,0,0,0.06); padding: 24px; max-width: 600px; width: 100%; max-height: 80vh; overflow-y: auto;">
            <h3 style="font-size: 20px; font-weight: 600; color: #111827; margin-bottom: 24px;">Assign Systems to CVE</h3>
            <p style="color: #6b7280; margin-bottom: 24px;">Select which systems are affected by this CVE:</p>
            
            <div style="max-height: 400px; overflow-y: auto; border: 1px solid #E5E7EB; border-radius: 8px; padding: 16px; margin-bottom: 24px;">
              <div v-if="systemsStore.systems.length === 0" style="text-align: center; color: #6b7280; padding: 20px;">
                No systems available
              </div>
              
              <div v-else style="display: grid; gap: 12px;">
                <label v-for="system in systemsStore.systems" :key="system.id" style="display: flex; items-center; gap: 12px; padding: 12px; border: 1px solid #E5E7EB; border-radius: 6px; background: #F9FAFB; cursor: pointer; transition: background-color 0.2s;" :style="selectedSystemIds.includes(system.id) ? 'background: #EBF8FF; border-color: #3B82F6;' : ''">
                  <input type="checkbox" :value="system.id" v-model="selectedSystemIds" style="margin: 0; transform: scale(1.2);">
                  <div style="flex: 1;">
                    <div style="font-weight: 500; color: #111827;">{{ system.name }}</div>
                    <div style="font-size: 14px; color: #6b7280;">{{ system.description || 'No description' }}</div>
                  </div>
                </label>
              </div>
            </div>
            
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
              <span style="font-size: 14px; color: #6b7280;">{{ selectedSystemIds.length }} system(s) selected</span>
              <div style="display: flex; gap: 8px;">
                <button @click="selectedSystemIds = []" style="padding: 6px 12px; background-color: #F3F4F6; color: #374151; border-radius: 4px; font-size: 12px; border: none; cursor: pointer;">Clear All</button>
                <button @click="selectedSystemIds = systemsStore.systems.map(s => s.id)" style="padding: 6px 12px; background-color: #3B82F6; color: white; border-radius: 4px; font-size: 12px; border: none; cursor: pointer;">Select All</button>
              </div>
            </div>
            
            <div style="display: flex; justify-content: flex-end; gap: 12px;">
                <button @click="showAssignSystemsModal = false" :disabled="assigningLoading" style="padding: 10px 20px; background-color: #F3F4F6; color: #374151; border-radius: 6px; font-weight: 500; border: none; cursor: pointer;" :style="assigningLoading ? 'opacity: 0.7; cursor: not-allowed;' : ''">Cancel</button>
                <button @click="updateAffectedSystems" :disabled="assigningLoading" style="padding: 10px 20px; background: linear-gradient(135deg, #059669 0%, #047857 100%); color: white; border-radius: 6px; font-weight: 500; border: none; cursor: pointer;" :style="assigningLoading ? 'opacity: 0.7; cursor: not-allowed;' : ''">
                  {{ assigningLoading ? 'Updating...' : 'Update Assignments' }}
                </button>
            </div>
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