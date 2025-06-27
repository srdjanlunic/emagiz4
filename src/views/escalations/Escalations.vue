<script setup>
import { ref, onMounted, computed } from 'vue';
import { useEscalationsStore } from '../../stores/escalations';
import { useAuthStore } from '../../stores/auth';
import { useAdminStore } from '../../stores/admin';

const escalationsStore = useEscalationsStore();
const authStore = useAuthStore();
const adminStore = useAdminStore();

const user = computed(() => authStore.user);
const escalations = computed(() => escalationsStore.escalations);
const loading = computed(() => escalationsStore.loading);

const showReviewModal = ref(false);
const selectedEscalation = ref(null);
const reviewNotes = ref('');
const newStatus = ref('');

const getUserById = (id) => {
  return adminStore.users.find(user => user.id === id);
};

const openReviewModal = (escalation) => {
  selectedEscalation.value = escalation;
  reviewNotes.value = '';
  newStatus.value = escalation.status;
  showReviewModal.value = true;
};

const handleReviewSubmit = async () => {
  if (!reviewNotes.value || !newStatus.value) {
    alert('Please provide a status and notes.');
    return;
  }
  
  const reviewData = {
    notes: reviewNotes.value,
    status: newStatus.value
  };

  await escalationsStore.reviewEscalation(selectedEscalation.value.id, reviewData);
  
  showReviewModal.value = false;
};

onMounted(() => {
  if (authStore.isTechnicalExpert) {
    escalationsStore.fetchEscalationsByTechExpert(user.value.id);
  } else if (authStore.isSecurityOfficer || authStore.isAdmin) {
    escalationsStore.fetchAllEscalations();
  }
  adminStore.fetchUsers();
});

const getStatusChipStyle = (status) => {
  const baseStyle = {
    padding: '4px 12px',
    borderRadius: '9999px',
    fontSize: '13px',
    fontWeight: '500',
    textTransform: 'capitalize'
  };
  switch (status) {
    case 'pending':
      return { ...baseStyle, backgroundColor: '#FEF3C7', color: '#92400E' };
    case 'in_progress':
      return { ...baseStyle, backgroundColor: '#DBEAFE', color: '#1E40AF' };
    case 'resolved':
      return { ...baseStyle, backgroundColor: '#D1FAE5', color: '#065F46' };
    case 'rejected':
      return { ...baseStyle, backgroundColor: '#FEE2E2', color: '#991B1B' };
    default:
      return { ...baseStyle, backgroundColor: '#F3F4F6', color: '#374151' };
  }
};
</script>

<template>
  <div class="escalations-page" style="padding: 24px; background-color: #f9fafb; min-height: 100vh;">
    <div class="page-header" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 32px;">
      <div class="header-content">
        <h1 style="font-size: 28px; font-weight: 700; color: #111827;">Escalations Dashboard</h1>
        <p style="color: #6b7280; font-size: 16px; margin-top: 4px;">Track and manage all security vulnerability escalations.</p>
      </div>
    </div>

    <div v-if="loading" style="display: flex; justify-content: center; align-items: center; padding: 48px;">
      <p style="font-size: 16px; color: #6b7280;">Loading escalations...</p>
    </div>
    
    <div v-else-if="escalations.length === 0" style="text-align: center; background: white; border-radius: 12px; padding: 64px 32px; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
      <h3 style="font-size: 20px; font-weight: 600; color: #111827; margin-bottom: 8px;">No Escalations Found</h3>
      <p style="color: #6b7280;">There are currently no active escalations assigned to you or to display.</p>
    </div>

    <div v-else class="escalations-grid" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(350px, 1fr)); gap: 24px;">
      <div v-for="escalation in escalations" :key="escalation.id" class="escalation-card" style="background: white; border-radius: 12px; padding: 24px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -2px rgba(0,0,0,0.1); transition: all 0.2s ease-in-out; position: relative;">
        <div class="card-header" style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px;">
          <h2 style="font-size: 20px; font-weight: 700; color: #1f2937; margin-right: 16px;">{{ escalation.cveId }}</h2>
          <span :style="getStatusChipStyle(escalation.status)" style="padding: 4px 12px; border-radius: 9999px; font-size: 13px; font-weight: 500; text-transform: capitalize;">{{ escalation.status.replace('_', ' ') }}</span>
        </div>
        
        <div class="details-grid" style="display: grid; grid-template-columns: 1fr; gap: 12px; margin-bottom: 20px;">
          <div class="detail-item">
            <p style="font-size: 14px; color: #6b7280; margin-bottom: 2px;">System</p>
            <p style="font-size: 15px; font-weight: 500; color: #374151;">{{ escalation.systemName || escalation.systemId }}</p>
          </div>
          <div class="detail-item">
            <p style="font-size: 14px; color: #6b7280; margin-bottom: 2px;">Reason for Escalation</p>
            <p style="font-size: 15px; font-weight: 500; color: #374151;">{{ escalation.reason }}</p>
          </div>
          <div class="detail-item">
            <p style="font-size: 14px; color: #6b7280; margin-bottom: 2px;">Assigned To</p>
            <p style="font-size: 15px; font-weight: 500; color: #374151;">{{ getUserById(escalation.techExpertId)?.username || 'N/A' }}</p>
          </div>
          <div class="detail-item">
            <p style="font-size: 14px; color: #6b7280; margin-bottom: 2px;">Reported By</p>
            <p style="font-size: 15px; font-weight: 500; color: #374151;">{{ getUserById(escalation.securityOfficerId)?.username || 'N/A' }}</p>
          </div>
        </div>
        
        <div class="card-footer" style="display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #e5e7eb; padding-top: 16px;">
          <router-link :to="`/cve/${escalation.cveId}`" style="color: #3b82f6; font-weight: 500; text-decoration: none; transition: color 0.2s;">View CVE Details</router-link>
          
          <button v-if="authStore.isTechnicalExpert && escalation.techExpertId === user.id && escalation.status !== 'RESOLVED' && escalation.status !== 'REJECTED'" @click="openReviewModal(escalation)" style="padding: 8px 16px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; border: none; border-radius: 8px; font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.2s; box-shadow: 0 1px 2px rgba(0,0,0,0.05);">
            Review Escalation
          </button>
        </div>
      </div>
    </div>

    <!-- Review Modal -->
    <div v-if="showReviewModal" style="position: fixed; inset: 0; background-color: rgba(107, 114, 128, 0.75); display: flex; align-items: center; justify-content: center; padding: 16px; z-index: 50;">
      <div style="background-color: white; border-radius: 12px; box-shadow: 0 10px 15px -3px rgba(0,0,0,0.1), 0 4px 6px -2px rgba(0,0,0,0.05); padding: 24px; max-width: 550px; width: 100%;">
        <h3 style="font-size: 22px; font-weight: 600; color: #111827; margin-bottom: 24px;">Review Escalation: {{ selectedEscalation.cveId }}</h3>
        
        <div style="display: grid; gap: 20px; margin-bottom: 24px;">
          <div>
            <label for="modal-status" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 8px;">Update Status</label>
            <select id="modal-status" v-model="newStatus" style="width: 100%; border-radius: 6px; border: 1px solid #D1D5DB; padding: 10px 12px; font-size: 14px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s; box-sizing: border-box; appearance: none; background-image: url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAiIGhlaWdodD0iMjAiIHZpZXdCb3g9IjAgMCAyMCAyMCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTYgOEwxMCAxMkwxNCA4IiBzdHJva2U9IiM2QjcyODAiIHN0cm9rZS13aWR0aD0iMS41IiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiLz4KPC9zdmc+'); background-position: right 12px center; background-repeat: no-repeat; background-size: 16px 16px; padding-right: 40px;">
              <option value="in_progress">In Progress</option>
              <option value="resolved">Resolved</option>
              <option value="rejected">Rejected</option>
            </select>
          </div>
          
          <div>
            <label for="modal-notes" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 8px;">Review Notes</label>
            <textarea id="modal-notes" v-model="reviewNotes" rows="5" style="width: 100%; border-radius: 6px; border: 1px solid #D1D5DB; padding: 10px 12px; font-size: 14px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s; box-sizing: border-box;" placeholder="Provide detailed notes on your review..."></textarea>
          </div>
        </div>
        
        <div style="display: flex; justify-content: flex-end; gap: 12px; margin-top: 12px;">
          <button @click="showReviewModal = false" style="padding: 10px 20px; background-color: #F3F4F6; color: #374151; border-radius: 8px; font-weight: 500; border: none; cursor: pointer; transition: background-color 0.2s;">Cancel</button>
          <button @click="handleReviewSubmit" style="padding: 10px 20px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; border-radius: 8px; font-weight: 500; border: none; cursor: pointer; transition: all 0.2s;">Submit Review</button>
        </div>
      </div>
    </div>
  </div>
</template> 