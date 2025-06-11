<script setup>
import { computed, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useCVEsStore } from '../../stores/cves';
import { useSystemsStore } from '../../stores/systems';
import { useAuthStore } from '../../stores/auth';

const route = useRoute();
const router = useRouter();
const cvesStore = useCVEsStore();
const systemsStore = useSystemsStore();
const authStore = useAuthStore();

const loading = ref(false);
const error = ref(null);
const searchTerm = ref('');
const selectedSeverity = ref('');
const selectedStatus = ref('');
const showImportModal = ref(false);
const importing = ref(false);

// Get query parameters
const systemFilter = computed(() => route.query.system);

// User permissions
const isSecurityOfficer = computed(() => authStore.isSecurityOfficer);
const isAdmin = computed(() => authStore.isAdmin);
const isSystemOwner = computed(() => authStore.isSystemOwner);
const userId = computed(() => authStore.user?.id);

// Get CVEs with role-based filtering
const allCVEs = computed(() => cvesStore.cves);

const filteredCVEs = computed(() => {
  let cves = allCVEs.value;

  // System owners only see CVEs for their systems
  if (isSystemOwner.value && !isAdmin.value && !isSecurityOfficer.value) {
    const userSystems = systemsStore.systems.filter(s => s.ownerId === userId.value);
    const userSystemIds = userSystems.map(s => s.id);
    cves = cves.filter(cve => 
      cve.affectedSystems?.some(systemId => userSystemIds.includes(systemId))
    );
  }

  // Filter by system if specified
  if (systemFilter.value) {
    cves = cves.filter(cve => 
      cve.affectedSystems?.includes(systemFilter.value)
    );
  }

  // Apply search filter
  if (searchTerm.value) {
    const term = searchTerm.value.toLowerCase();
    cves = cves.filter(cve =>
      cve.cveId?.toLowerCase().includes(term) ||
      cve.description?.toLowerCase().includes(term)
    );
  }

  // Apply severity filter
  if (selectedSeverity.value) {
    cves = cves.filter(cve => cve.severity === selectedSeverity.value);
  }

  // Apply status filter
  if (selectedStatus.value) {
    cves = cves.filter(cve => cve.status === selectedStatus.value);
  }

  return cves.sort((a, b) => new Date(b.dateDiscovered) - new Date(a.dateDiscovered));
});

// Statistics
const stats = computed(() => {
  const cves = isSystemOwner.value ? 
    allCVEs.value.filter(cve => {
      const userSystems = systemsStore.systems.filter(s => s.ownerId === userId.value);
      const userSystemIds = userSystems.map(s => s.id);
      return cve.affectedSystems?.some(systemId => userSystemIds.includes(systemId));
    }) : allCVEs.value;

  return {
    total: cves.length,
    open: cves.filter(cve => cve.status === 'open').length,
    inProgress: cves.filter(cve => cve.status === 'in_progress').length,
    resolved: cves.filter(cve => cve.status === 'resolved').length,
    critical: cves.filter(cve => cve.severity === 'critical').length,
    high: cves.filter(cve => cve.severity === 'high').length
  };
});

// Get system name
const getSystemName = (systemId) => {
  const system = systemsStore.getSystemById(systemId);
  return system?.name || 'Unknown System';
};

// Format date
const formatDate = (dateString) => {
  if (!dateString) return 'N/A';
  return new Date(dateString).toLocaleDateString();
};

// Get severity styling
const getSeverityClass = (severity) => {
  if (severity === 'critical') return 'background-color: #fee2e2; color: #991b1b;';
  if (severity === 'high') return 'background-color: #fee2e2; color: #991b1b;';
  if (severity === 'medium') return 'background-color: #fef3c7; color: #92400e;';
  return 'background-color: #d1fae5; color: #065f46;';
};

// Get status styling
const getStatusClass = (status) => {
  if (status === 'open') return 'background-color: #fee2e2; color: #991b1b;';
  if (status === 'in_progress') return 'background-color: #fef3c7; color: #92400e;';
  if (status === 'resolved') return 'background-color: #d1fae5; color: #065f46;';
  if (status === 'accepted_risk') return 'background-color: #fef3c7; color: #92400e;';
  return 'background-color: #f3f4f6; color: #374151;';
};

const handleStatusUpdate = async (cve, newStatus) => {
  if (!isSecurityOfficer.value && !isAdmin.value) return;

  // Determine the system context for the update
  let systemIdForUpdate = null;
  if (systemFilter.value) {
    systemIdForUpdate = systemFilter.value;
  } else if (cve.affectedSystems && cve.affectedSystems.length > 0) {
    // If not filtering by a system, default to the first affected system.
    // A more complex UI might ask the user to specify which system context.
    systemIdForUpdate = cve.affectedSystems[0];
  }

  if (!systemIdForUpdate) {
    error.value = 'Could not determine the system for this CVE status update.';
    return;
  }

  try {
    await cvesStore.updateCVEStatus(cve.cveId, newStatus, systemIdForUpdate);
    // Optionally, you can show a success notification here
  } catch (err) {
    error.value = `Failed to update status: ${err.message}`;
  }
};

// Import CVEs (Security Officer only)
const importCVEs = async () => {
  if (!isSecurityOfficer.value && !isAdmin.value) return;
  
  importing.value = true;
  try {
    await cvesStore.importCVEs();
    showImportModal.value = false;
    // Refresh data
    await loadData();
  } catch (err) {
    error.value = err.message;
  } finally {
    importing.value = false;
  }
};

// Clear all filters
const clearFilters = () => {
  searchTerm.value = '';
  selectedSeverity.value = '';
  selectedStatus.value = '';
  router.replace({ query: {} });
};

// Load data
const loadData = async () => {
  loading.value = true;
  error.value = null;
  
  try {
    // Load systems first, then CVEs
    await systemsStore.fetchSystems();
    
    if (systemFilter.value) {
      // Load CVEs for specific system
      await cvesStore.fetchCVEsBySystem(systemFilter.value);
    } else {
      // Load all CVEs
      await cvesStore.fetchCVEs();
    }
  } catch (err) {
    error.value = err.message;
    console.error('Error loading CVE data:', err);
  } finally {
    loading.value = false;
  }
};

// Watch for system filter changes
watch(() => route.query.system, () => {
  loadData();
});

// Load data on mount
onMounted(() => {
  loadData();
});
</script>

<template>
  <div style="padding: 24px;">
    <!-- Loading state -->
    <div v-if="loading && allCVEs.length === 0" style="text-align: center;">
      <div style="background: white; border-radius: 12px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); padding: 48px 24px;">
        <div style="width: 32px; height: 32px; border: 3px solid #f3f4f6; border-radius: 50%; border-top-color: #3b82f6; animation: spin 1s ease-in-out infinite; margin: 0 auto 16px;"></div>
        <p style="color: #6b7280;">Loading CVEs...</p>
      </div>
    </div>

    <!-- Error state -->
    <div v-else-if="error" style="margin-bottom: 24px;">
      <div style="background: #fee2e2; border: 1px solid #fca5a5; border-radius: 12px; padding: 16px;">
        <div style="display: flex; align-items: center; gap: 8px;">
          <svg style="width: 20px; height: 20px; color: #dc2626;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
          <span style="color: #dc2626; font-weight: 500;">Error loading CVEs: {{ error }}</span>
        </div>
      </div>
    </div>

    <!-- Main content -->
    <div v-else>
      <!-- Header -->
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 32px;">
        <div>
          <h1 style="font-size: 24px; font-weight: bold; color: #111827; margin-bottom: 4px;">
            {{ isSystemOwner ? 'My CVEs' : 'CVE Management' }}
          </h1>
          <p style="color: #6b7280; font-size: 14px;">
            {{ systemFilter ? `CVEs for ${getSystemName(systemFilter)}` : 'Track and manage vulnerability reports' }}
          </p>
        </div>
        
        <div style="display: flex; gap: 12px;">
          <button v-if="(isSecurityOfficer || isAdmin) && !systemFilter" @click="showImportModal = true" style="display: inline-flex; align-items: center; padding: 10px 20px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; border: none; border-radius: 8px; font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.2s; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);">
            <svg style="width: 16px; height: 16px; margin-right: 8px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M9 19l3 3m0 0l3-3m-3 3V10" />
            </svg>
            Import CVEs
          </button>
        </div>
      </div>

      <!-- Statistics -->
      <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; margin-bottom: 24px;">
        <div style="background: white; padding: 20px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);">
          <div style="display: flex; align-items: center; justify-content: space-between;">
            <div>
              <p style="font-size: 14px; color: #6b7280; margin-bottom: 4px;">Total CVEs</p>
              <p style="font-size: 24px; font-weight: bold; color: #111827;">{{ stats.total }}</p>
            </div>
            <div style="width: 48px; height: 48px; background: #eff6ff; border-radius: 8px; display: flex; align-items: center; justify-content: center;">
              <svg style="width: 24px; height: 24px; color: #3b82f6; margin-right: 8px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </div>
          </div>
        </div>

        <div style="background: white; padding: 20px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);">
          <div style="display: flex; align-items: center; justify-content: space-between;">
            <div>
              <p style="font-size: 14px; color: #6b7280; margin-bottom: 4px;">Open</p>
              <p style="font-size: 24px; font-weight: bold; color: #dc2626;">{{ stats.open }}</p>
            </div>
            <div style="width: 48px; height: 48px; background: #fef2f2; border-radius: 8px; display: flex; align-items: center; justify-content: center;">
              <svg style="width: 24px; height: 24px; color: #dc2626; margin-right: 8px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
            </div>
          </div>
        </div>

        <div style="background: white; padding: 20px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);">
          <div style="display: flex; align-items: center; justify-content: space-between;">
            <div>
              <p style="font-size: 14px; color: #6b7280; margin-bottom: 4px;">Critical/High</p>
              <p style="font-size: 24px; font-weight: bold; color: #ef4444;">{{ stats.critical + stats.high }}</p>
            </div>
            <div style="width: 48px; height: 48px; background: #fee2e2; border-radius: 8px; display: flex; align-items: center; justify-content: center;">
              <svg style="width: 24px; height: 24px; color: #ef4444; margin-right: 8px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
              </svg>
            </div>
          </div>
        </div>

        <div style="background: white; padding: 20px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);">
          <div style="display: flex; align-items: center; justify-content: space-between;">
            <div>
              <p style="font-size: 14px; color: #6b7280; margin-bottom: 4px;">Resolved</p>
              <p style="font-size: 24px; font-weight: bold; color: #10b981;">{{ stats.resolved }}</p>
            </div>
            <div style="width: 48px; height: 48px; background: #d1fae5; border-radius: 8px; display: flex; align-items: center; justify-content: center;">
              <svg style="width: 24px; height: 24px; color: #059669; margin-right: 8px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
          </div>
        </div>
      </div>

      <!-- Filters -->
      <div style="background: white; border-radius: 12px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); padding: 24px; margin-bottom: 24px;">
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; align-items: end;">
          <div>
            <label style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 6px;">Search CVEs</label>
            <input v-model="searchTerm" type="text" placeholder="Search by CVE ID or description..." style="width: 100%; border-radius: 6px; border: 1px solid #D1D5DB; padding: 10px 12px; font-size: 14px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s; box-sizing: border-box;">
          </div>
          
          <div>
            <label style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 6px;">Severity</label>
            <select v-model="selectedSeverity" style="width: 100%; border-radius: 6px; border: 1px solid #D1D5DB; padding: 10px 12px; font-size: 14px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s; box-sizing: border-box; appearance: none; padding-right: 40px;">
              <option value="">All Severities</option>
              <option value="critical">Critical</option>
              <option value="high">High</option>
              <option value="medium">Medium</option>
              <option value="low">Low</option>
            </select>
          </div>
          
          <div>
            <label style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 6px;">Status</label>
            <select v-model="selectedStatus" style="width: 100%; border-radius: 6px; border: 1px solid #D1D5DB; padding: 10px 12px; font-size: 14px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s; box-sizing: border-box; appearance: none; padding-right: 40px;">
              <option value="">All Statuses</option>
              <option value="open">Open</option>
              <option value="in_progress">In Progress</option>
              <option value="resolved">Resolved</option>
              <option value="accepted_risk">Accepted Risk</option>
            </select>
          </div>
          
          <div>
            <button @click="clearFilters" style="padding: 10px 20px; background-color: #F3F4F6; color: #374151; border-radius: 6px; font-weight: 500; border: none; cursor: pointer; transition: background-color 0.2s; width: 100%;">Clear Filters</button>
          </div>
        </div>
      </div>

      <!-- CVE List -->
      <div style="background: white; border-radius: 12px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); overflow: hidden;">
        <div v-if="filteredCVEs.length === 0" style="text-align: center; padding: 48px 24px;">
          <h3 style="font-size: 18px; font-weight: 500; color: #374151; margin-bottom: 8px;">No CVEs found</h3>
          <p style="color: #6b7280; margin-bottom: 24px;">{{ searchTerm || selectedSeverity || selectedStatus ? 'Try adjusting your filters' : 'No CVEs have been imported yet' }}</p>
          <button v-if="(isSecurityOfficer || isAdmin) && !searchTerm && !selectedSeverity && !selectedStatus" @click="showImportModal = true" style="display: inline-flex; align-items: center; padding: 10px 20px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; border: none; border-radius: 8px; font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.2s;">Import CVEs</button>
        </div>
        
        <div v-else style="overflow-x: auto;">
          <table style="width: 100%; border-collapse: separate; border-spacing: 0;">
            <thead>
              <tr style="background: #f9fafb;">
                <th style="padding: 12px 24px; text-align: left; font-size: 12px; font-weight: 500; text-transform: uppercase; color: #6b7280; border-bottom: 1px solid #e5e7eb;">CVE ID</th>
                <th style="padding: 12px 24px; text-align: left; font-size: 12px; font-weight: 500; text-transform: uppercase; color: #6b7280; border-bottom: 1px solid #e5e7eb;">Severity</th>
                <th style="padding: 12px 24px; text-align: left; font-size: 12px; font-weight: 500; text-transform: uppercase; color: #6b7280; border-bottom: 1px solid #e5e7eb;">Status</th>
                <th style="padding: 12px 24px; text-align: left; font-size: 12px; font-weight: 500; text-transform: uppercase; color: #6b7280; border-bottom: 1px solid #e5e7eb;">Affected Systems</th>
                <th style="padding: 12px 24px; text-align: left; font-size: 12px; font-weight: 500; text-transform: uppercase; color: #6b7280; border-bottom: 1px solid #e5e7eb;">Date Discovered</th>
                <th style="padding: 12px 24px; text-align: left; font-size: 12px; font-weight: 500; text-transform: uppercase; color: #6b7280; border-bottom: 1px solid #e5e7eb;">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="cve in filteredCVEs" :key="cve.cveId" style="border-bottom: 1px solid #e5e7eb;">
                <td style="padding: 16px 24px;">
                  <div style="font-weight: 500; color: #111827; margin-bottom: 4px;">{{ cve.cveId }}</div>
                  <div style="font-size: 13px; color: #6b7280; line-height: 1.4; max-width: 300px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">{{ cve.description }}</div>
                </td>
                <td style="padding: 16px 24px;">
                  <span style="display: inline-block; padding: 4px 12px; border-radius: 9999px; font-size: 13px; font-weight: 500;" :style="getSeverityClass(cve.severity)">
                    {{ cve.severity?.charAt(0).toUpperCase() + cve.severity?.slice(1) }}
                  </span>
                </td>
                <td style="padding: 16px 24px; color: #6b7280; min-width: 150px;">
                  <div style="position: relative;">
                    <select
                      :value="cve.status"
                      @change="handleStatusUpdate(cve, $event.target.value)"
                      :disabled="!isSecurityOfficer && !isAdmin"
                      style="width: 100%; padding: 6px 32px 6px 12px; font-size: 12px; font-weight: 500; border-radius: 6px; border: 1px solid #d1d5db; box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05); outline: none; appearance: none; background-color: white;"
                      :style="(!isSecurityOfficer && !isAdmin) ? 'cursor: not-allowed; background-color: #f3f4f6;' : ''"
                    >
                      <option value="open">Open</option>
                      <option value="in_progress">In Progress</option>
                      <option value="resolved">Resolved</option>
                      <option value="accepted_risk">Accepted Risk</option>
                    </select>
                    <div style="pointer-events: none; position: absolute; top: 0; right: 0; bottom: 0; display: flex; align-items: center; padding: 0 8px; color: #6b7280;">
                      <svg style="width: 16px; height: 16px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 9l4-4 4 4m0 6l-4 4-4-4" />
                      </svg>
                    </div>
                  </div>
                </td>
                <td style="padding: 16px 24px; color: #6b7280;">
                  <div v-if="cve.affectedSystems && cve.affectedSystems.length > 0">
                    <router-link v-for="systemId in cve.affectedSystems.slice(0, 2)" :key="systemId" 
                                 :to="`/systems/${systemId}`" 
                                 style="display: inline-block; margin-right: 4px; margin-bottom: 4px; padding: 2px 8px; background-color: #e5e7eb; border-radius: 6px; font-size: 12px; color: #374151; text-decoration: none;">
                      {{ getSystemName(systemId) }}
                    </router-link>
                    <span v-if="cve.affectedSystems.length > 2" style="font-size: 12px; color: #6b7280;">
                      +{{ cve.affectedSystems.length - 2 }} more
                    </span>
                  </div>
                  <div v-else style="font-size: 12px; color: #9ca3af;">No systems affected</div>
                </td>
                <td style="padding: 16px 24px; text-align: right; color: #6b7280; min-width: 150px;">{{ formatDate(cve.dateDiscovered) }}</td>
                <td style="padding: 16px 24px;">
                  <router-link :to="`/cve/${cve.cveId}`" style="color: #2563eb; font-weight: 500; text-decoration: none; transition: color 0.2s;">
                    View Details
                  </router-link>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Import Modal -->
    <div v-if="showImportModal" style="position: fixed; top: 0; left: 0; right: 0; bottom: 0; background-color: rgba(0, 0, 0, 0.5); display: flex; align-items: center; justify-content: center; z-index: 50;">
      <div style="background: white; border-radius: 12px; padding: 24px; max-width: 400px; width: 90%; margin: 16px;">
        <h3 style="font-size: 18px; font-weight: 600; color: #111827; margin-bottom: 16px;">Import CVEs</h3>
        <p style="color: #6b7280; margin-bottom: 24px;">This will import the latest CVE data from external sources. This process may take a few minutes.</p>
        
        <div style="display: flex; justify-content: flex-end; gap: 12px;">
          <button @click="showImportModal = false" :disabled="importing" style="padding: 10px 20px; background-color: #F3F4F6; color: #374151; border-radius: 6px; font-weight: 500; border: none; cursor: pointer; transition: background-color 0.2s;" :style="importing ? 'opacity: 0.7; cursor: not-allowed;' : ''">Cancel</button>
          <button @click="importCVEs" :disabled="importing" style="padding: 10px 20px; background: linear-gradient(135deg, #059669 0%, #047857 100%); color: white; border-radius: 6px; font-weight: 500; border: none; cursor: pointer; transition: all 0.2s;" :style="importing ? 'opacity: 0.7; cursor: not-allowed;' : ''">
            {{ importing ? 'Importing...' : 'Import CVEs' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style> 