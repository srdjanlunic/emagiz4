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

const searchQuery = ref('');
const selectedSystemId = ref(route.query.system || '');
const selectedStatus = ref('');

// Get the list of systems for filtering
const systems = computed(() => {
  if (authStore.isAdmin || authStore.isSecurityOfficer) {
    return systemsStore.systems;
  }
  return systemsStore.systems.filter(s => s.ownerId === authStore.user?.id);
});

// Get all CVEs and apply filters
const filteredCVEs = computed(() => {
  let result = cvesStore.cves;
  
  // Filter by system if selected
  if (selectedSystemId.value) {
    result = result.filter(cve => 
      cve.affectedSystems.includes(parseInt(selectedSystemId.value))
    );
  }
  
  // Filter by status if selected
  if (selectedStatus.value) {
    result = result.filter(cve => cve.status === selectedStatus.value);
  }
  
  // Filter by search query if provided
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(cve => 
      cve.id.toLowerCase().includes(query) || 
      cve.description.toLowerCase().includes(query)
    );
  }
  
  return result;
});

// Get system name by ID
const getSystemNameById = (systemId) => {
  const system = systemsStore.getSystemById(systemId);
  return system ? system.name : 'Unknown System';
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
  return 'badge'; // Default
};

// Format date
const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString();
};

// Navigation
const viewCVEDetails = (cveId) => {
  router.push(`/cve/${cveId}`);
};

// Update URL when filters change
const updateUrlParams = () => {
  const query = {};
  if (selectedSystemId.value) query.system = selectedSystemId.value;
  if (searchQuery.value) query.search = searchQuery.value;
  if (selectedStatus.value) query.status = selectedStatus.value;
  
  router.replace({ query });
};

// Watch for changes in route query params
onMounted(() => {
  // Set initial values from URL if present
  if (route.query.search) searchQuery.value = route.query.search;
  if (route.query.status) selectedStatus.value = route.query.status;
});
</script>

<template>
  <div class="cve-page">
    <div class="page-header">
      <h1>CVE List</h1>
      <p>View and manage Common Vulnerabilities and Exposures</p>
    </div>
    
    <!-- Filters -->
    <div class="filters-container">
      <div class="filters-grid">
        <div class="filter-group">
          <label for="search" class="filter-label">
            <svg class="filter-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
            Search CVEs
          </label>
          <div class="input-wrapper">
            <input
              id="search"
              v-model="searchQuery"
              type="text"
              placeholder="Search by CVE ID or description"
              @input="updateUrlParams"
            />
          </div>
        </div>
        
        <div class="filter-group">
          <label for="system" class="filter-label">
            <svg class="filter-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z" />
            </svg>
            Filter by System
          </label>
          <div class="input-wrapper">
            <select
              id="system"
              v-model="selectedSystemId"
              @change="updateUrlParams"
            >
              <option value="">All Systems</option>
              <option v-for="system in systems" :key="system.id" :value="system.id">
                {{ system.name }}
              </option>
            </select>
          </div>
        </div>
        
        <div class="filter-group">
          <label for="status" class="filter-label">
            <svg class="filter-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            Filter by Status
          </label>
          <div class="input-wrapper">
            <select
              id="status"
              v-model="selectedStatus"
              @change="updateUrlParams"
            >
              <option value="">All Statuses</option>
              <option value="open">Open</option>
              <option value="in_progress">In Progress</option>
              <option value="resolved">Resolved</option>
              <option value="accepted_risk">Accepted Risk</option>
            </select>
          </div>
        </div>
      </div>
    </div>
    
    <!-- CVE List -->
    <div class="cve-container">
      <div v-if="filteredCVEs.length === 0" class="empty-state">
        <h3>No CVEs found</h3>
        <p>Try changing your search criteria or import new CVEs</p>
      </div>
      
      <div v-else class="table-container">
        <table>
          <thead>
            <tr>
              <th>CVE ID</th>
              <th>Description</th>
              <th>Severity</th>
              <th>Status</th>
              <th>System</th>
              <th>Discovered</th>
              <th><span class="sr-only">View</span></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="cve in filteredCVEs" :key="cve.id" @click="viewCVEDetails(cve.id)" class="cve-row">
              <td class="cve-id">{{ cve.id }}</td>
              <td class="description">
                <div>{{ cve.description }}</div>
              </td>
              <td>
                <span class="severity-badge" :class="getSeverityClass(cve.severity)">
                  {{ cve.severity.charAt(0).toUpperCase() + cve.severity.slice(1) }}
                </span>
              </td>
              <td>
                <span class="status-badge" :class="getStatusClass(cve.status)">
                  {{ cve.status.replace('_', ' ').charAt(0).toUpperCase() + cve.status.slice(1).replace('_', ' ') }}
                </span>
              </td>
              <td class="system">
                {{ getSystemNameById(cve.affectedSystems[0]) }}
              </td>
              <td class="date">
                {{ formatDate(cve.discoveredAt) }}
              </td>
              <td class="actions">
                <button class="view-button">View Details</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<style scoped>
.cve-page {
  padding: 24px;
}

.page-header {
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 4px;
}

.page-header p {
  color: #6b7280;
  font-size: 14px;
}

.filters-container {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.filters-grid {
  display: grid;
  grid-template-columns: repeat(1, 1fr);
  gap: 16px;
}

@media (min-width: 768px) {
  .filters-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
    align-items: end;
  }
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
}

.filter-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  line-height: 1;
}

.filter-icon {
  flex-shrink: 0;
  width: 16px;
  height: 16px;
  color: #6b7280;
}

.input-wrapper {
  position: relative;
  width: 100%;
}

.input-wrapper input,
.input-wrapper select {
  box-sizing: border-box;
  width: 100%;
  height: 40px;
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  line-height: 1.5;
  color: #111827;
  background-color: white;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.input-wrapper select {
  padding-right: 36px;
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
  background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3e%3cpath stroke='%236B7280' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.5' d='M6 8l4 4 4-4'/%3e%3c/svg%3e");
  background-position: right 12px center;
  background-repeat: no-repeat;
  background-size: 16px 16px;
}

.input-wrapper input:hover,
.input-wrapper select:hover {
  border-color: #9ca3af;
}

.input-wrapper input:focus,
.input-wrapper select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.input-wrapper input::placeholder {
  color: #9ca3af;
}

/* Dark mode support */
@media (prefers-color-scheme: dark) {
  .filters-container {
    background-color: #1f2937;
  }

  .filter-label {
    color: #d1d5db;
  }

  .filter-icon {
    color: #9ca3af;
  }

  .input-wrapper input,
  .input-wrapper select {
    background-color: #374151;
    border-color: #4b5563;
    color: #f3f4f6;
  }
  
  .input-wrapper input::placeholder {
    color: #6b7280;
  }
  
  .input-wrapper input:hover,
  .input-wrapper select:hover {
    border-color: #4b5563;
  }

  .input-wrapper select {
    background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3e%3cpath stroke='%239CA3AF' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.5' d='M6 8l4 4 4-4'/%3e%3c/svg%3e");
  }
}

.cve-container {
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

.cve-row {
  cursor: pointer;
  transition: background-color 0.2s;
}

.cve-row:hover {
  background-color: #f9fafb;
}

.cve-row:last-child td {
  border-bottom: none;
}

.cve-id {
  font-weight: 500;
  color: #2563eb;
}

.description {
  max-width: 400px;
}

.description div {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  font-size: 14px;
  color: #374151;
  line-height: 1.5;
}

.severity-badge,
.status-badge {
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

.system {
  font-size: 14px;
  color: #374151;
}

.date {
  font-size: 14px;
  color: #6b7280;
}

.actions {
  text-align: right;
}

.view-button {
  padding: 6px 12px;
  background: transparent;
  color: #2563eb;
  border: 1px solid #2563eb;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.view-button:hover {
  background: #2563eb;
  color: white;
}
</style> 