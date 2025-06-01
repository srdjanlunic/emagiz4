<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useSystemsStore } from '../../stores/systems';
import { useCVEsStore } from '../../stores/cves';
import { useAuthStore } from '../../stores/auth';

const router = useRouter();
const systemsStore = useSystemsStore();
const cvesStore = useCVEsStore();
const authStore = useAuthStore();

const loading = ref(false);
const error = ref(null);

const systems = computed(() => systemsStore.systems);
const isAdmin = computed(() => authStore.isAdmin);
const isSecurityOfficer = computed(() => authStore.isSecurityOfficer);
const isTechnicalExpert = computed(() => authStore.isTechnicalExpert);
const isSystemOwner = computed(() => authStore.isSystemOwner);
const userId = computed(() => authStore.user?.id);

// Filter systems based on user role
const filteredSystems = computed(() => {
  if (isAdmin.value || isSecurityOfficer.value || isTechnicalExpert.value) {
    return systems.value;
  }
  // System owners only see their own systems
  if (isSystemOwner.value) {
    return systems.value.filter(s => s.ownerId === userId.value);
  }
  return [];
});

// Count CVEs for each system
const getOpenCVEsCount = (systemId) => {
  return cvesStore.getOpenCVEsBySystem(systemId).length;
};

const getResolvedCVEsCount = (systemId) => {
  return cvesStore.getResolvedCVEsBySystem(systemId).length;
};

// Get criticality class
const getCriticalityClass = (criticality) => {
  const level = criticality?.toUpperCase();
  if (level === 'CRITICAL' || level === 'HIGH') return 'badge-red';
  if (level === 'MEDIUM') return 'badge-yellow';
  return 'badge-green';
};

// Calculate risk score based on criticality
const calculateRiskScore = (system) => {
  if (!system) return 0;
  
  const baseScore = {
    'CRITICAL': 90,
    'HIGH': 70,
    'MEDIUM': 40,
    'LOW': 20
  }[system.criticalityLevel?.toUpperCase()] || 20;
  
  // Add factors for internet facing and data classification
  let score = baseScore;
  if (system.internetFacing) score += 10;
  if (system.dataClassification === 'SENSITIVE') score += 10;
  
  // Add random variance for demo
  return Math.min(100, score + Math.floor(Math.random() * 10));
};

// Format date
const formatDate = (dateString) => {
  if (!dateString) return 'N/A';
  return new Date(dateString).toLocaleDateString();
};

// Navigate to add system (only for authorized users)
const addSystem = () => {
  if (isAdmin.value || isSystemOwner.value) {
    router.push('/systems/add');
  }
};

// Check if user can add systems
const canAddSystem = computed(() => {
  return isAdmin.value || isSystemOwner.value;
});

// Fetch data on mount
onMounted(async () => {
  loading.value = true;
  error.value = null;
  
  try {
    if (isSystemOwner.value && userId.value) {
      // Fetch only user's systems
      await systemsStore.fetchSystemsByOwner(userId.value);
    } else {
      // Fetch all systems
      await systemsStore.fetchSystems();
    }
    
    // Fetch CVEs to get counts
    await cvesStore.fetchCVEs();
  } catch (err) {
    error.value = err.message;
    console.error('Error loading systems:', err);
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="systems-page">
    <!-- Loading state -->
    <div v-if="loading && systems.length === 0" style="padding: 24px; text-align: center;">
      <div style="background: white; border-radius: 12px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); padding: 48px 24px;">
        <div style="width: 32px; height: 32px; border: 3px solid #f3f4f6; border-radius: 50%; border-top-color: #3b82f6; animation: spin 1s ease-in-out infinite; margin: 0 auto 16px;"></div>
        <p style="color: #6b7280;">Loading systems...</p>
      </div>
    </div>

    <!-- Error state -->
    <div v-else-if="error" style="padding: 24px;">
      <div style="background: #fee2e2; border: 1px solid #fca5a5; border-radius: 12px; padding: 16px; margin-bottom: 24px;">
        <div style="display: flex; align-items: center; gap: 8px;">
          <svg style="width: 20px; height: 20px; color: #dc2626;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
          <span style="color: #dc2626; font-weight: 500;">Error loading systems: {{ error }}</span>
        </div>
      </div>
    </div>

    <!-- Main content -->
    <div v-else>
      <div class="page-header">
        <div class="header-content">
          <h1>{{ isSystemOwner ? 'My Systems' : 'Systems' }}</h1>
          <p>{{ isSystemOwner ? 'Manage your registered information systems' : 'View registered information systems' }}</p>
        </div>
        <button v-if="canAddSystem" @click="addSystem" class="add-button">
          <svg class="plus-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
          Add System
        </button>
      </div>
      
      <!-- Systems list -->
      <div class="systems-container">
        <div v-if="filteredSystems.length === 0" class="empty-state">
          <h3>{{ isSystemOwner ? 'No systems registered yet' : 'No systems found' }}</h3>
          <p>{{ isSystemOwner ? 'Add a system to start tracking CVEs' : 'No systems are currently registered in the system' }}</p>
          <button v-if="canAddSystem" @click="addSystem" class="add-button">Add System</button>
        </div>
        
        <div v-else class="table-container">
          <table>
            <thead>
              <tr>
                <th>System Name</th>
                <th>Risk Score</th>
                <th>Criticality</th>
                <th>CVEs</th>
                <th>Date Added</th>
                <th><span class="sr-only">Actions</span></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="system in filteredSystems" :key="system.id">
                <td class="system-name">
                  <div class="name">{{ system.name }}</div>
                  <div class="os">{{ system.version || 'N/A' }}</div>
                </td>
                <td>
                  <div class="risk-score" :class="{
                    'high': calculateRiskScore(system) > 60,
                    'medium': calculateRiskScore(system) > 30 && calculateRiskScore(system) <= 60,
                    'low': calculateRiskScore(system) <= 30
                  }">
                    {{ calculateRiskScore(system) }}
                  </div>
                </td>
                <td>
                  <span class="criticality-badge" :class="getCriticalityClass(system.criticalityLevel)">
                    {{ system.criticalityLevel?.charAt(0).toUpperCase() + system.criticalityLevel?.slice(1).toLowerCase() || 'Unknown' }}
                  </span>
                </td>
                <td>
                  <div class="cve-count">
                    <span class="open">{{ getOpenCVEsCount(system.id) }}</span> open, 
                    <span class="resolved">{{ getResolvedCVEsCount(system.id) }}</span> resolved
                  </div>
                </td>
                <td class="date">
                  {{ formatDate(system.createdAt) }}
                </td>
                <td class="actions">
                  <router-link :to="`/cve?system=${system.id}`" class="view-link">
                    View CVEs
                  </router-link>
                </td>
              </tr>
            </tbody>
          </table>
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

.systems-page {
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.header-content h1 {
  font-size: 24px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 4px;
}

.header-content p {
  color: #6b7280;
  font-size: 14px;
}

.add-button {
  display: inline-flex;
  align-items: center;
  padding: 10px 20px;
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.add-button:hover {
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.15);
}

.plus-icon {
  width: 16px;
  height: 16px;
  margin-right: 8px;
}

.systems-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.empty-state {
  text-align: center;
  padding: 48px 24px;
}

.empty-state h3 {
  font-size: 18px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 8px;
}

.empty-state p {
  color: #6b7280;
  margin-bottom: 24px;
}

.table-container {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
}

th {
  padding: 12px 24px;
  text-align: left;
  font-size: 12px;
  font-weight: 500;
  text-transform: uppercase;
  color: #6b7280;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
}

td {
  padding: 16px 24px;
  border-bottom: 1px solid #e5e7eb;
}

tr:last-child td {
  border-bottom: none;
}

.system-name .name {
  font-weight: 500;
  color: #111827;
  margin-bottom: 4px;
}

.system-name .os {
  font-size: 13px;
  color: #6b7280;
}

.risk-score {
  font-weight: 500;
}

.risk-score.high { color: #dc2626; }
.risk-score.medium { color: #d97706; }
.risk-score.low { color: #059669; }

.criticality-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 9999px;
  font-size: 13px;
  font-weight: 500;
}

.badge-red {
  background-color: #fee2e2;
  color: #991b1b;
}

.badge-yellow {
  background-color: #fef3c7;
  color: #92400e;
}

.badge-green {
  background-color: #d1fae5;
  color: #065f46;
}

.cve-count {
  font-size: 14px;
  color: #374151;
}

.cve-count .open {
  color: #dc2626;
  font-weight: 500;
}

.cve-count .resolved {
  color: #059669;
  font-weight: 500;
}

.date {
  color: #6b7280;
  font-size: 14px;
}

.view-link {
  color: #2563eb;
  font-weight: 500;
  text-decoration: none;
  transition: color 0.2s;
}

.view-link:hover {
  color: #1d4ed8;
}

.actions {
  text-align: right;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .add-button {
    width: 100%;
    justify-content: center;
  }
}
</style> 