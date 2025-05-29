<script setup>
import { ref, computed, onMounted } from 'vue';
import { Bar } from 'vue-chartjs';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from 'chart.js';
import { useSystemsStore } from '../stores/systems';
import { useCVEsStore } from '../stores/cves';
import { useAuthStore } from '../stores/auth';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const systemsStore = useSystemsStore();
const cvesStore = useCVEsStore();
const authStore = useAuthStore();

const systems = computed(() => systemsStore.systems);
const isAdmin = computed(() => authStore.isAdmin);
const isSecurityOfficer = computed(() => authStore.isSecurityOfficer);
const userId = computed(() => authStore.user?.id);

const loading = ref(true);

// Chart data
const chartData = computed(() => {
  const labels = systems.value.map(s => s.name);
  const openData = systems.value.map(s => 
    cvesStore.getOpenCVEsBySystem(s.id).length
  );
  const resolvedData = systems.value.map(s => 
    cvesStore.getResolvedCVEsBySystem(s.id).length
  );

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
    const newCVEs = await cvesStore.importCVEs();
    alert(`Successfully imported ${newCVEs.length} new CVEs`);
  } catch (error) {
    alert('Error importing CVEs');
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

onMounted(() => {
  loading.value = false;
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
        <h3>Total Systems</h3>
        <p class="metric blue">{{ totalSystems }}</p>
      </div>
      
      <div class="status-card">
        <h3>Total CVEs</h3>
        <p class="metric blue">{{ totalCVEs }}</p>
      </div>
      
      <div class="status-card">
        <h3>Open CVEs</h3>
        <p class="metric red">{{ openCVEs }}</p>
      </div>
      
      <div class="status-card">
        <h3>Resolved CVEs</h3>
        <p class="metric green">{{ resolvedCVEs }}</p>
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
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.2s;
}

.status-card:hover {
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.status-card h3 {
  font-size: 18px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 8px;
}

.metric {
  font-size: 36px;
  font-weight: bold;
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