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

// Enhanced Chart data with gradients
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

  // Create gradients for Chart.js
  const createGradient = (ctx, colorStops) => {
    const gradient = ctx.createLinearGradient(0, 0, 0, 400);
    colorStops.forEach((stop, index) => {
      gradient.addColorStop(index / (colorStops.length - 1), stop);
    });
    return gradient;
  };

  return {
    labels,
    datasets: [
      {
        label: 'Open CVEs',
        backgroundColor: (context) => {
          const chart = context.chart;
          const {ctx} = chart;
          return createGradient(ctx, ['#ff6b6b', '#ee5a52', '#e74c3c']);
        },
        borderColor: '#e74c3c',
        borderWidth: 2,
        borderRadius: 8,
        borderSkipped: false,
        data: openData,
        hoverBackgroundColor: '#ff5252',
        hoverBorderColor: '#d32f2f',
        hoverBorderWidth: 3,
        tension: 0.4
      },
      {
        label: 'Resolved CVEs',
        backgroundColor: (context) => {
          const chart = context.chart;
          const {ctx} = chart;
          return createGradient(ctx, ['#4ecdc4', '#26d0ce', '#00bcd4']);
        },
        borderColor: '#00bcd4',
        borderWidth: 2,
        borderRadius: 8,
        borderSkipped: false,
        data: resolvedData,
        hoverBackgroundColor: '#26c6da',
        hoverBorderColor: '#0097a7',
        hoverBorderWidth: 3,
        tension: 0.4
      }
    ]
  };
});

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  interaction: {
    intersect: false,
    mode: 'index'
  },
  plugins: {
    legend: {
      display: false
    },
    tooltip: {
      backgroundColor: 'rgba(0, 0, 0, 0.9)',
      titleColor: '#ffffff',
      bodyColor: '#ffffff',
      borderColor: 'rgba(255, 255, 255, 0.1)',
      borderWidth: 1,
      cornerRadius: 12,
      padding: 16,
      titleFont: {
        size: 14,
        weight: '600'
      },
      bodyFont: {
        size: 13,
        weight: '400'
      },
      displayColors: true,
      usePointStyle: true,
      callbacks: {
        title: function(context) {
          return `System: ${context[0].label}`;
        },
        label: function(context) {
          return `${context.dataset.label}: ${context.parsed.y} CVEs`;
        }
      }
    }
  },
  scales: {
    x: {
      beginAtZero: true,
      grid: {
        display: false
      },
      ticks: {
        font: {
          size: 12,
          family: "'Inter', 'system-ui', '-apple-system', 'sans-serif'",
          weight: '500'
        },
        color: '#6b7280',
        maxRotation: 45,
        minRotation: 0
      },
      border: {
        display: false
      }
    },
    y: {
      beginAtZero: true,
      grid: {
        color: 'rgba(156, 163, 175, 0.1)',
        lineWidth: 1
      },
      ticks: {
        precision: 0,
        font: {
          size: 12,
          family: "'Inter', 'system-ui', '-apple-system', 'sans-serif'",
          weight: '400'
        },
        color: '#6b7280',
        padding: 12
      },
      border: {
        display: false
      }
    }
  },
  animation: {
    duration: 1500,
    easing: 'easeOutQuart'
  },
  elements: {
    bar: {
      borderRadius: 8
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
    <div class="chart-container" style="background: linear-gradient(145deg, #ffffff 0%, #f8fafc 100%); border-radius: 20px; padding: 32px; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04); border: 1px solid rgba(255, 255, 255, 0.8); position: relative; overflow: hidden;">
      <!-- Header with enhanced styling -->
      <div class="chart-header" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 32px; position: relative; z-index: 2;">
        <div>
          <h2 style="font-size: 24px; font-weight: 700; color: #111827; margin-bottom: 8px; font-family: 'Inter', system-ui, -apple-system, sans-serif;">CVE Status Overview</h2>
          <p style="color: #6b7280; font-size: 14px; font-weight: 400;">Vulnerability distribution across your systems</p>
        </div>
        <div class="chart-legend-custom" style="display: flex; gap: 24px; align-items: center;">
          <div class="legend-item" style="display: flex; align-items: center; gap: 8px;">
            <div style="width: 16px; height: 16px; background: linear-gradient(135deg, #ff6b6b, #e74c3c); border-radius: 4px; box-shadow: 0 2px 4px rgba(231, 76, 60, 0.3);"></div>
            <span style="font-size: 14px; font-weight: 500; color: #374151;">Open CVEs</span>
          </div>
          <div class="legend-item" style="display: flex; align-items: center; gap: 8px;">
            <div style="width: 16px; height: 16px; background: linear-gradient(135deg, #4ecdc4, #00bcd4); border-radius: 4px; box-shadow: 0 2px 4px rgba(0, 188, 212, 0.3);"></div>
            <span style="font-size: 14px; font-weight: 500; color: #374151;">Resolved CVEs</span>
          </div>
        </div>
      </div>
      
      <!-- Background decoration -->
      <div style="position: absolute; top: -50px; right: -50px; width: 200px; height: 200px; background: radial-gradient(circle, rgba(59, 130, 246, 0.05) 0%, transparent 70%); border-radius: 50%; z-index: 1;"></div>
      <div style="position: absolute; bottom: -30px; left: -30px; width: 150px; height: 150px; background: radial-gradient(circle, rgba(16, 185, 129, 0.05) 0%, transparent 70%); border-radius: 50%; z-index: 1;"></div>
      
      <div v-if="loading" style="text-align: center; padding: 64px; position: relative; z-index: 2;">
        <div style="display: inline-flex; align-items: center; gap: 12px; color: #6b7280; font-size: 16px; font-weight: 500;">
          <svg class="loading-icon" style="width: 24px; height: 24px; animation: spin 1s linear infinite;" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          Loading chart data...
        </div>
      </div>
      <div v-else-if="!systems.length" style="text-align: center; padding: 64px; position: relative; z-index: 2;">
        <div style="background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%); border-radius: 16px; padding: 32px; border: 2px dashed #d1d5db;">
          <svg style="width: 48px; height: 48px; color: #9ca3af; margin: 0 auto 16px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
          </svg>
          <h3 style="font-size: 18px; font-weight: 600; color: #374151; margin-bottom: 8px;">No Systems Found</h3>
          <p style="color: #6b7280; font-size: 14px;">Add a system to view vulnerability analytics and charts.</p>
        </div>
      </div>
      <div v-else style="height: 450px; position: relative; z-index: 2; padding: 16px; background: rgba(255, 255, 255, 0.6); border-radius: 16px; backdrop-filter: blur(10px);">
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

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
}

.dashboard-page {
  animation: fadeInUp 0.6s ease-out;
}

.action-button {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.action-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.action-button:hover::before {
  left: 100%;
}

.action-button:hover {
  background-color: #f3f4f6;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px -5px rgba(0, 0, 0, 0.15);
}

.action-button.import:hover {
  opacity: 0.9;
  transform: translateY(-2px);
  box-shadow: 0 12px 35px -5px rgba(59, 130, 246, 0.4);
}

.status-card {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  animation: fadeInUp 0.6s ease-out;
}

.status-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 35px -5px rgba(0, 0, 0, 0.15), 0 8px 20px -8px rgba(0, 0, 0, 0.1);
}

.chart-container {
  animation: fadeInUp 0.8s ease-out;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.chart-container:hover {
  transform: translateY(-2px);
  box-shadow: 0 25px 50px -5px rgba(0, 0, 0, 0.12), 0 15px 25px -8px rgba(0, 0, 0, 0.08);
}

.legend-item {
  transition: all 0.2s ease;
}

.legend-item:hover {
  transform: scale(1.05);
}

.loading-icon {
  animation: spin 1s linear infinite;
}

/* Status card animations */
.status-card:nth-child(1) { animation-delay: 0.1s; }
.status-card:nth-child(2) { animation-delay: 0.2s; }
.status-card:nth-child(3) { animation-delay: 0.3s; }
.status-card:nth-child(4) { animation-delay: 0.4s; }

/* Card icon hover effects */
.card-icon {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.status-card:hover .card-icon {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 8px 25px -5px rgba(0, 0, 0, 0.15);
}

/* Metric number animation */
.metric {
  transition: all 0.3s ease;
}

.status-card:hover .metric {
  transform: scale(1.05);
  font-weight: 800;
}

/* Chart header animations */
.chart-header {
  animation: fadeInUp 0.6s ease-out 0.5s both;
}

/* Responsive enhancements */
@media (max-width: 768px) {
  .chart-legend-custom {
    flex-direction: column;
    gap: 12px !important;
    align-items: flex-start !important;
  }
  
  .chart-header {
    flex-direction: column;
    align-items: flex-start !important;
    gap: 16px;
  }
}
</style> 