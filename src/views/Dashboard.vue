<script setup>
import { ref, computed, onMounted } from 'vue';
import { Bar } from 'vue-chartjs';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from 'chart.js';
import { useSystemsStore } from '../stores/systems';
import { useCVEsStore } from '../stores/cves';
import { useAuthStore } from '../stores/auth';
import { useNotificationsStore } from '../stores/notifications';
import { useAdminStore } from '../stores/admin';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const systemsStore = useSystemsStore();
const cvesStore = useCVEsStore();
const authStore = useAuthStore();
const notificationStore = useNotificationsStore();
const adminStore = useAdminStore();

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

const downloadReport = async () => {
    try {
        await adminStore.downloadRiskAssessmentReport();
    } catch (error) {
        notificationStore.addNotification({
            message: 'Error downloading report',
            type: 'error',
            timeout: 5000
        });
    }
};

const downloadVulnerabilityReport = async () => {
    try {
        await adminStore.downloadVulnerabilityReport();
    } catch (error) {
        notificationStore.addNotification({
            message: 'Error downloading report',
            type: 'error',
            timeout: 5000
        });
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
  <div class="dashboard-page" style="padding: 24px; background-color: #f9fafb; min-height: 100vh;">
    <!-- Header -->
    <div class="dashboard-header" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 32px;">
      <div class="header-content">
        <h1 style="font-size: 28px; font-weight: 700; color: #111827;">Dashboard</h1>
        <p style="color: #6b7280; font-size: 16px; margin-top: 4px;">Welcome back, {{ authStore.user?.username }}!</p>
      </div>
      <div v-if="isAdmin || isSecurityOfficer" style="display: flex; gap: 12px;">
        <button 
          @click="downloadVulnerabilityReport" 
          class="action-button"
          style="padding: 10px 20px; background-color: white; color: #374151; border: 1px solid #D1D5DB; border-radius: 8px; font-weight: 500; cursor: pointer; transition: all 0.2s;"
        >
          Download CVE Report
        </button>
        <button 
          @click="downloadReport" 
          class="action-button"
          style="padding: 10px 20px; background-color: white; color: #374151; border: 1px solid #D1D5DB; border-radius: 8px; font-weight: 500; cursor: pointer; transition: all 0.2s;"
        >
          Download Risk Assessment Report
        </button>
        <button 
          @click="importNewCVEs" 
          class="action-button import"
          :class="{ 'disabled': loading }"
          :disabled="loading"
          style="padding: 10px 20px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; border: none; border-radius: 8px; font-weight: 500; cursor: pointer; transition: all 0.2s; display: inline-flex; align-items: center;"
        >
          <svg v-if="loading" class="loading-icon" style="width: 16px; height: 16px; margin-right: 8px; animation: spin 1s linear infinite;" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          {{ loading ? 'Importing...' : 'Import New CVEs' }}
        </button>
      </div>
    </div>

    <!-- Status Cards -->
    <div class="status-cards" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 24px; margin-bottom: 32px;">
      <div class="status-card" style="background-color: white; border-radius: 12px; padding: 24px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -2px rgba(0,0,0,0.1);">
        <div class="card-content" style="display: flex; justify-content: space-between; align-items: center;">
          <div class="card-info">
            <p class="card-label" style="font-size: 14px; color: #6b7280; margin-bottom: 8px;">Total Systems</p>
            <p class="metric" style="font-size: 32px; font-weight: 700; color: #111827;">{{ totalSystems }}</p>
          </div>
          <div class="card-icon" style="background-color: #DBEAFE; padding: 12px; border-radius: 50%;">
            <svg style="width: 24px; height: 24px; color: #3b82f6;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
            </svg>
          </div>
        </div>
      </div>
      
      <div class="status-card" style="background-color: white; border-radius: 12px; padding: 24px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -2px rgba(0,0,0,0.1);">
        <div class="card-content" style="display: flex; justify-content: space-between; align-items: center;">
          <div class="card-info">
            <p class="card-label" style="font-size: 14px; color: #6b7280; margin-bottom: 8px;">Total CVEs</p>
            <p class="metric" style="font-size: 32px; font-weight: 700; color: #111827;">{{ totalCVEs }}</p>
          </div>
          <div class="card-icon" style="background-color: #DBEAFE; padding: 12px; border-radius: 50%;">
            <svg style="width: 24px; height: 24px; color: #3b82f6;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
          </div>
        </div>
      </div>
      
      <div class="status-card" style="background-color: white; border-radius: 12px; padding: 24px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -2px rgba(0,0,0,0.1);">
        <div class="card-content" style="display: flex; justify-content: space-between; align-items: center;">
          <div class="card-info">
            <p class="card-label" style="font-size: 14px; color: #6b7280; margin-bottom: 8px;">Open CVEs</p>
            <p class="metric" style="font-size: 32px; font-weight: 700; color: #ef4444;">{{ openCVEs }}</p>
          </div>
          <div class="card-icon" style="background-color: #FEE2E2; padding: 12px; border-radius: 50%;">
            <svg style="width: 24px; height: 24px; color: #ef4444;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
          </div>
        </div>
      </div>
      
      <div class="status-card" style="background-color: white; border-radius: 12px; padding: 24px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -2px rgba(0,0,0,0.1);">
        <div class="card-content" style="display: flex; justify-content: space-between; align-items: center;">
          <div class="card-info">
            <p class="card-label" style="font-size: 14px; color: #6b7280; margin-bottom: 8px;">Resolved CVEs</p>
            <p class="metric" style="font-size: 32px; font-weight: 700; color: #10b981;">{{ resolvedCVEs }}</p>
          </div>
          <div class="card-icon" style="background-color: #D1FAE5; padding: 12px; border-radius: 50%;">
            <svg style="width: 24px; height: 24px; color: #10b981;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- Chart Container -->
    <div class="chart-container" style="background-color: white; border-radius: 12px; padding: 24px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -2px rgba(0,0,0,0.1);">
      <h2 style="font-size: 20px; font-weight: 600; color: #111827; margin-bottom: 24px;">CVE Status by System</h2>
      <div v-if="loading" style="text-align: center; padding: 48px;">Loading chart data...</div>
      <div v-else-if="!systems.length" style="text-align: center; padding: 48px;">
        <p>No systems found. Please add a system to view chart data.</p>
      </div>
      <div v-else style="height: 400px;">
        <Bar :data="chartData" :options="chartOptions" />
      </div>
    </div>
  </div>
</template>

<style scoped>
@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.action-button:hover {
  background-color: #f3f4f6;
}

.action-button.import:hover {
  opacity: 0.9;
}
</style> 