<script setup>
import { ref, computed, onMounted } from 'vue';
import { Bar } from 'vue-chartjs';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from 'chart.js';
import { useSystemsStore } from '../stores/systems';
import { useCVEsStore } from '../stores/cves';
import { useAuthStore } from '../stores/auth';
import { useNotificationsStore } from '../stores/notifications';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const systemsStore = useSystemsStore();
const cvesStore = useCVEsStore();
const authStore = useAuthStore();
const notificationStore = useNotificationsStore();

const systems = computed(() => systemsStore.systems);
const isAdmin = computed(() => authStore.isAdmin);
const isSecurityOfficer = computed(() => authStore.isSecurityOfficer);
const userId = computed(() => authStore.user?.id);

const loading = ref(true);

// Chart data
const chartData = computed(() => {
  const labels = systems.value.map(s => s.name);
  const openData = systems.value.map(s => {
    return cvesStore.cves.filter(cve => 
      cve.affectedSystems?.includes(s.id) && 
      (cve.status === 'open' || cve.status === 'in_progress')
    ).length;
  });
  const resolvedData = systems.value.map(s => {
    return cvesStore.cves.filter(cve => 
      cve.affectedSystems?.includes(s.id) && 
      (cve.status === 'resolved' || cve.status === 'accepted_risk')
    ).length;
  });

  return {
    labels,
    datasets: [
      {
        label: 'Open CVEs',
        backgroundColor: '#ef4444',
        data: openData
      },
      {
        label: 'Resolved CVEs',
        backgroundColor: '#10b981',
        data: resolvedData
      }
    ]
  };
});

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        precision: 0
      }
    }
  }
};

// Import new CVEs if user is admin or security officer
const importNewCVEs = async () => {
  if (systems.value.length === 0) {
    alert('Please add systems first to import CVEs');
    return;
  }
  
  try {
    loading.value = true;
    const result = await cvesStore.importCVEs();
    notificationStore.addNotification({
      message: result.message,
      type: 'success',
      timeout: 5000
    });
  } catch (error) {
    notificationStore.addNotification({
      message: 'Error importing CVEs',
      type: 'error',
      timeout: 5000
    });
  } finally {
    loading.value = false;
  }
};

// Stats
const totalSystems = computed(() => systems.value.length);
const totalCVEs = computed(() => cvesStore.cves.length);
const openCVEs = computed(() => 
  cvesStore.cves.filter(c => c.status !== 'resolved' && c.status !== 'accepted_risk').length
);
const resolvedCVEs = computed(() => 
  cvesStore.cves.filter(c => c.status === 'resolved' || c.status === 'accepted_risk').length
);

const lastImportDate = computed(() => {
  if (!cvesStore.lastImportDate) return 'Never';
  return new Date(cvesStore.lastImportDate).toLocaleString();
});

const unreadNotifications = computed(() => {
  if (!userId.value) return 0;
  return cvesStore.getUnreadNotifications(userId.value).length;
});

onMounted(async () => {
  try {
    await systemsStore.fetchSystems();
    await cvesStore.fetchCVEs();
  } catch (error) {
    console.error("Failed to load dashboard data:", error);
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h1>Dashboard</h1>
      <div v-if="isAdmin || isSecurityOfficer">
        <button 
          @click="importNewCVEs" 
          class="import-button"
          :class="{ 'disabled': loading }"
          :disabled="loading"
        >
          <span class="button-content">
            <svg v-if="loading" class="loading-icon" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            {{ loading ? 'Importing...' : 'Import New CVEs' }}
          </span>
        </button>
      </div>
    </div>

    <!-- Status Cards -->
    <div class="status-cards">
      <div class="status-card">
        <div class="card-content">
          <div class="card-info">
            <p class="card-label">Total Systems</p>
            <p class="metric blue">{{ totalSystems }}</p>
          </div>
          <div class="card-icon blue-bg">
            <svg style="width: 24px; height: 24px; color: #3b82f6;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
            </svg>
          </div>
        </div>
      </div>
      
      <div class="status-card">
        <div class="card-content">
          <div class="card-info">
            <p class="card-label">Total CVEs</p>
            <p class="metric blue">{{ totalCVEs }}</p>
          </div>
          <div class="card-icon blue-bg">
            <svg style="width: 24px; height: 24px; color: #3b82f6;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
          </div>
        </div>
      </div>
      
      <div class="status-card">
        <div class="card-content">
          <div class="card-info">
            <p class="card-label">Open CVEs</p>
            <p class="metric red">{{ openCVEs }}</p>
          </div>
          <div class="card-icon red-bg">
            <svg style="width: 24px; height: 24px; color: #ef4444;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
          </div>
        </div>
      </div>
      
      <div class="status-card">
        <div class="card-content">
          <div class="card-info">
            <p class="card-label">Resolved CVEs</p>
            <p class="metric green">{{ resolvedCVEs }}</p>
          </div>
          <div class="card-icon green-bg">
            <svg style="width: 24px; height: 24px; color: #10b981;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- Import Status -->
    <div class="import-status">
      <div class="import-status-content">
        <div>
          <h3>CVE Import Status</h3>
          <p class="last-import">Last import: {{ lastImportDate }}</p>
        </div>
        <div 
          v-if="unreadNotifications > 0" 
          class="notification-badge"
        >
          {{ unreadNotifications }} new notification{{ unreadNotifications > 1 ? 's' : '' }}
        </div>
      </div>
    </div>

    <!-- Chart -->
    <div 
      v-if="systems.length > 0" 
      class="chart-container"
    >
      <h3>CVEs by System</h3>
      <div class="chart">
        <Bar :data="chartData" :options="chartOptions" />
      </div>
    </div>

    <!-- Empty state -->
    <div 
      v-else 
      class="empty-state"
    >
      <div class="empty-state-content">
        <svg class="empty-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
        </svg>
        <h3>No systems added yet</h3>
        <p>Add your first system to start tracking CVEs and keep your infrastructure secure.</p>
        <router-link 
          to="/systems/add" 
          class="add-system-button"
        >
          <svg class="plus-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
          Add System
        </router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  padding: 24px;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.dashboard-header h1 {
  font-size: 30px;
  font-weight: bold;
  color: #111827;
}

.import-button {
  padding: 10px 24px;
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  color: white;
  border-radius: 8px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  border: none;
  cursor: pointer;
  transition: all 0.2s;
}

.import-button:hover {
  box-shadow: 0 8px 12px -1px rgba(0, 0, 0, 0.15);
}

.import-button.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.button-content {
  display: flex;
  align-items: center;
}

.loading-icon {
  width: 20px;
  height: 20px;
  margin-right: 12px;
  margin-left: -4px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.status-cards {
  display: grid;
  grid-template-columns: repeat(1, 1fr);
  gap: 24px;
  margin-bottom: 32px;
}

@media (min-width: 768px) {
  .status-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 1024px) {
  .status-cards {
    grid-template-columns: repeat(4, 1fr);
  }
}

.status-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.2s;
}

.status-card:hover {
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.card-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-info {
  flex: 1;
}

.card-label {
  font-size: 14px;
  font-weight: 500;
  color: #6b7280;
  margin: 0 0 4px 0;
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.blue-bg { background-color: #eff6ff; }
.red-bg { background-color: #fef2f2; }
.green-bg { background-color: #f0fdf4; }

.metric {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
}

.metric.blue { color: #3b82f6; }
.metric.red { color: #ef4444; }
.metric.green { color: #10b981; }

.import-status {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 32px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.import-status-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.import-status h3 {
  font-size: 18px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 8px;
}

.last-import {
  font-size: 14px;
  color: #6b7280;
}

.notification-badge {
  background-color: #fee2e2;
  color: #991b1b;
  padding: 8px 16px;
  border-radius: 9999px;
  font-size: 14px;
  font-weight: 500;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.chart-container {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 32px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.chart-container h3 {
  font-size: 18px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 24px;
}

.chart {
  height: 384px;
}

.empty-state {
  background: white;
  border-radius: 12px;
  padding: 48px;
  text-align: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.empty-state-content {
  max-width: 400px;
  margin: 0 auto;
}

.empty-icon {
  width: 64px;
  height: 64px;
  color: #9ca3af;
  margin: 0 auto 16px;
}

.empty-state h3 {
  font-size: 20px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 12px;
}

.empty-state p {
  color: #6b7280;
  margin-bottom: 24px;
}

.add-system-button {
  display: inline-flex;
  align-items: center;
  padding: 12px 24px;
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  color: white;
  border-radius: 8px;
  text-decoration: none;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  transition: all 0.2s;
}

.add-system-button:hover {
  box-shadow: 0 8px 12px -1px rgba(0, 0, 0, 0.15);
}

.plus-icon {
  width: 20px;
  height: 20px;
  margin-right: 8px;
}
</style> 