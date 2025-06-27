<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useSystemsStore } from '../../stores/systems';
import { useAuthStore } from '../../stores/auth';
import { useNotificationsStore } from '../../stores/notifications';

const route = useRoute();
const router = useRouter();
const systemsStore = useSystemsStore();
const authStore = useAuthStore();
const notificationsStore = useNotificationsStore();

const loading = ref(false);
const error = ref(null);
const systemId = computed(() => route.params.id);

const formData = ref({
  name: '',
  description: '',
  vendor: '',
  ownerId: null,
  // Implementation fields
  version: '',
  environment: '',
  internetFacing: false,
  dataClassification: 'PUBLIC',
  criticalityLevel: 'LOW',
  sensitiveCustomerData: false,
  riskScore: 0,
  implementations: []
});

const dataClassificationOptions = [
  { value: 'PUBLIC', label: 'Public' },
  { value: 'INTERNAL', label: 'Internal' },
  { value: 'CONFIDENTIAL', label: 'Confidential' },
  { value: 'RESTRICTED', label: 'Restricted' },
  { value: 'SENSITIVE', label: 'Sensitive' }
];

const criticalityOptions = [
  { value: 'LOW', label: 'Low' },
  { value: 'MEDIUM', label: 'Medium' },
  { value: 'HIGH', label: 'High' },
  { value: 'CRITICAL', label: 'Critical' }
];

const validateForm = () => {
  if (!formData.value.name.trim()) return 'System name is required';
  if (!formData.value.description.trim()) return 'Description is required';
  if (!formData.value.version.trim()) return 'Version is required';
  if (!formData.value.vendor.trim()) return 'Vendor is required';
  return null;
};

const handleSubmit = async () => {
  error.value = null;
  const validationError = validateForm();
  if (validationError) {
    error.value = validationError;
    return;
  }
  
  loading.value = true;
  
  try {
    // 1. Prepare system data
    const systemData = {
      name: formData.value.name,
      description: formData.value.description,
      vendor: formData.value.vendor,
      ownerId: formData.value.ownerId
    };
    
    // 2. Prepare implementation data (assuming one implementation for now)
    const implementation = formData.value.implementations[0];
    if (implementation) {
      const implementationData = {
        id: implementation.id,
        systemId: systemId.value,
        departmentId: implementation.departmentId, // preserve existing department
        version: formData.value.version,
        environment: formData.value.environment,
        dataClassification: formData.value.dataClassification,
        criticalityLevel: formData.value.criticalityLevel,
        internetFacing: formData.value.internetFacing,
        sensitiveCustomerData: formData.value.sensitiveCustomerData
      };

      // 3. Call update actions
      await systemsStore.updateSystem(systemId.value, systemData);
      await systemsStore.updateImplementation(implementation.id, implementationData);
    } else {
        // if no implementation, just update system
        await systemsStore.updateSystem(systemId.value, systemData);
    }


    notificationsStore.addNotification('System updated successfully', 'success');
    router.push('/systems');
  } catch (err) {
    error.value = err.message;
    console.error('Error updating system:', err);
  } finally {
    loading.value = false;
  }
};

const handleCancel = () => {
  router.push('/systems');
};

onMounted(async () => {
  loading.value = true;
  try {
    const system = await systemsStore.fetchSystemById(systemId.value);
    if (system) {
      // Populate system fields
      formData.value.name = system.name;
      formData.value.description = system.description;
      formData.value.vendor = system.vendor;
      formData.value.ownerId = system.ownerId;
      
      // Fetch and populate implementation fields
      const implementations = await systemsStore.fetchImplementationsForSystem(systemId.value);
      formData.value.implementations = implementations;
      
      if (implementations && implementations.length > 0) {
        const impl = implementations[0]; // Assuming we edit the first one
        formData.value.version = impl.version;
        formData.value.environment = impl.environment;
        formData.value.internetFacing = impl.internetFacing;
        formData.value.dataClassification = impl.dataClassification;
        formData.value.criticalityLevel = impl.criticalityLevel;
        formData.value.sensitiveCustomerData = impl.sensitiveCustomerData;
        formData.value.riskScore = impl.riskScore;
      }
    } else {
      error.value = 'System not found.';
    }
  } catch (err) {
    error.value = 'Failed to load system data.';
    console.error(err);
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div style="padding: 24px;">
    <!-- Header -->
    <div style="margin-bottom: 32px;">
      <h1 style="font-size: 24px; font-weight: bold; color: #111827; margin-bottom: 4px;">Edit System</h1>
      <p style="color: #6b7280; font-size: 14px;">Update the details of an existing information system</p>
    </div>

    <!-- Error message -->
    <div v-if="error" style="background: #fee2e2; border: 1px solid #fca5a5; border-radius: 12px; padding: 16px; margin-bottom: 24px;">
      <div style="display: flex; align-items: center; gap: 8px;">
        <svg style="width: 20px; height: 20px; color: #dc2626;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
        </svg>
        <span style="color: #dc2626; font-weight: 500;">{{ error }}</span>
      </div>
    </div>

    <!-- Form -->
    <div style="background: white; border-radius: 12px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); padding: 32px;">
      <form @submit.prevent="handleSubmit">
        <div style="display: grid; gap: 24px;">
          <!-- Basic Information -->
          <div>
            <h3 style="font-size: 18px; font-weight: 600; color: #111827; margin-bottom: 16px;">Basic Information</h3>
            
            <div style="display: grid; gap: 16px;">
              <div>
                <label for="name" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 6px;">System Name *</label>
                <input id="name" v-model="formData.name" type="text" required style="width: 100%; border-radius: 6px; border: 1px solid #D1D5DB; padding: 10px 12px; font-size: 14px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s; box-sizing: border-box;" placeholder="Enter system name">
              </div>
              
              <div>
                <label for="description" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 6px;">Description *</label>
                <textarea id="description" v-model="formData.description" rows="3" required style="width: 100%; border-radius: 6px; border: 1px solid #D1D5DB; padding: 10px 12px; font-size: 14px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s; box-sizing: border-box; resize: vertical; min-height: 80px;" placeholder="Describe the system's purpose and functionality"></textarea>
              </div>
              
              <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
                <div>
                  <label for="version" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 6px;">Version *</label>
                  <input id="version" v-model="formData.version" type="text" required style="width: 100%; border-radius: 6px; border: 1px solid #D1D5DB; padding: 10px 12px; font-size: 14px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s; box-sizing: border-box;" placeholder="e.g., 1.0.0">
                </div>
                
                <div>
                  <label for="vendor" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 6px;">Vendor *</label>
                  <input id="vendor" v-model="formData.vendor" type="text" required style="width: 100%; border-radius: 6px; border: 1px solid #D1D5DB; padding: 10px 12px; font-size: 14px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s; box-sizing: border-box;" placeholder="System vendor/manufacturer">
                </div>
              </div>
              <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
                <div>
                  <label for="environment" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 6px;">Environment</label>
                  <input id="environment" v-model="formData.environment" type="text" style="width: 100%; border-radius: 6px; border: 1px solid #D1D5DB; padding: 10px 12px; font-size: 14px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s; box-sizing: border-box;" placeholder="e.g., Production">
                </div>
                 <div>
                  <label for="riskScore" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 6px;">Risk Score</label>
                  <input id="riskScore" :value="formData.riskScore" type="text" readonly style="width: 100%; border-radius: 6px; border: 1px solid #D1D5DB; padding: 10px 12px; font-size: 14px; line-height: 1.5; color: #111827; background-color: #F3F4F6; cursor: not-allowed;" >
                </div>
              </div>
            </div>
          </div>

          <!-- Security Classification -->
          <div>
            <h3 style="font-size: 18px; font-weight: 600; color: #111827; margin-bottom: 16px;">Security Classification</h3>
            
            <div style="display: grid; gap: 16px;">
              <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
                <div>
                  <label for="dataClassification" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 6px;">Data Classification</label>
                  <select id="dataClassification" v-model="formData.dataClassification" style="width: 100%; border-radius: 6px; border: 1px solid #D1D5DB; padding: 10px 12px; font-size: 14px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s; box-sizing: border-box; appearance: none; background-image: url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAiIGhlaWdodD0iMjAiIHZpZXdCb3g9IjAgMCAyMCAyMCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTYgOEwxMCAxMkwxNCA4IiBzdHJva2U9IiM2QjcyODAiIHN0cm9rZS13aWR0aD0iMS41IiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiLz4KPC9zdmc+'); background-position: right 12px center; background-repeat: no-repeat; background-size: 16px 16px; padding-right: 40px;">
                    <option v-for="option in dataClassificationOptions" :key="option.value" :value="option.value">{{ option.label }}</option>
                  </select>
                </div>
                
                <div>
                  <label for="criticalityLevel" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 6px;">Criticality Level</label>
                  <select id="criticalityLevel" v-model="formData.criticalityLevel" style="width: 100%; border-radius: 6px; border: 1px solid #D1D5DB; padding: 10px 12px; font-size: 14px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s; box-sizing: border-box; appearance: none; background-image: url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAiIGhlaWdodD0iMjAiIHZpZXdCb3g9IjAgMCAyMCAyMCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTYgOEwxMCAxMkwxNCA4IiBzdHJva2U9IiM2QjcyODAiIHN0cm9rZS13aWR0aD0iMS41IiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiLz4KPC9zdmc+'); background-position: right 12px center; background-repeat: no-repeat; background-size: 16px 16px; padding-right: 40px;">
                    <option v-for="option in criticalityOptions" :key="option.value" :value="option.value">{{ option.label }}</option>
                  </select>
                </div>
              </div>
              
              <div>
                <label style="display: flex; align-items: center; gap: 8px; cursor: pointer;">
                  <input type="checkbox" v-model="formData.internetFacing" style="width: 16px; height: 16px; accent-color: #3b82f6;">
                  <span style="font-size: 14px; font-weight: 500; color: #374151;">Internet Facing</span>
                </label>
                <p style="font-size: 13px; color: #6b7280; margin-top: 4px; margin-left: 24px;">Check if this system is accessible from the internet</p>
              </div>
              <div>
                <label style="display: flex; align-items: center; gap: 8px; cursor: pointer;">
                  <input type="checkbox" v-model="formData.sensitiveCustomerData" style="width: 16px; height: 16px; accent-color: #3b82f6;">
                  <span style="font-size: 14px; font-weight: 500; color: #374151;">Handles Sensitive Customer Data</span>
                </label>
                <p style="font-size: 13px; color: #6b7280; margin-top: 4px; margin-left: 24px;">Check if this system processes or stores sensitive customer data</p>
              </div>
            </div>
          </div>

          <!-- Owner (Admin only) -->
          <div v-if="authStore.isAdmin">
            <h3 style="font-size: 18px; font-weight: 600; color: #111827; margin-bottom: 16px;">System Owner</h3>
            
            <div>
              <label for="ownerId" style="display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 6px;">Owner ID</label>
              <input id="ownerId" v-model="formData.ownerId" type="number" style="width: 100%; border-radius: 6px; border: 1px solid #D1D5DB; padding: 10px 12px; font-size: 14px; line-height: 1.5; color: #111827; background-color: white; transition: border-color 0.2s; box-sizing: border-box;" placeholder="User ID of the system owner">
            </div>
          </div>

          <!-- Current Owner Display -->
          <div v-if="!authStore.isAdmin">
            <h3 style="font-size: 18px; font-weight: 600; color: #111827; margin-bottom: 16px;">System Owner</h3>
            
            <div style="padding: 16px; background-color: #f9fafb; border-radius: 8px; border: 1px solid #e5e7eb;">
              <p style="font-size: 14px; color: #6b7280;">This system is currently assigned to:</p>
              <p style="font-size: 16px; font-weight: 500; color: #111827; margin-top: 4px;">
                {{ formData.ownerId ? 'System Owner (ID: ' + formData.ownerId + ')' : 'No owner assigned' }}
              </p>
              <p style="font-size: 13px; color: #6b7280; margin-top: 8px;">
                To change system ownership, contact your security officer.
              </p>
            </div>
          </div>

          <!-- Implementation Summary (for reference) -->
          <div v-if="formData.implementations && formData.implementations.length > 0">
            <h3 style="font-size: 18px; font-weight: 600; color: #111827; margin-bottom: 16px;">Implementation Details</h3>
            
            <div style="padding: 16px; background-color: #f9fafb; border-radius: 8px; border: 1px solid #e5e7eb;">
              <div v-for="(implementation, index) in formData.implementations" :key="index" style="margin-bottom: 16px;">
                <h4 style="font-size: 16px; font-weight: 500; color: #111827; margin-bottom: 8px;">
                  {{ implementation.environment || 'Production' }} Environment (v{{ implementation.version || formData.version }})
                </h4>
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 12px; font-size: 14px; color: #6b7280;">
                  <div><strong>Version:</strong> {{ implementation.version || formData.version }}</div>
                  <div><strong>Environment:</strong> {{ implementation.environment || 'Production' }}</div>
                  <div><strong>Risk Score:</strong> {{ implementation.riskScore || formData.riskScore }}</div>
                  <div><strong>Location:</strong> {{ implementation.location || 'Not specified' }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- Actions -->
          <div style="display: flex; justify-content: flex-end; gap: 12px; padding-top: 24px; border-top: 1px solid #e5e7eb;">
            <button type="button" @click="handleCancel" :disabled="loading" style="padding: 10px 20px; background-color: #F3F4F6; color: #374151; border-radius: 6px; font-weight: 500; border: none; cursor: pointer; transition: background-color 0.2s;" :style="loading ? 'opacity: 0.7; cursor: not-allowed;' : ''">Cancel</button>
            <button type="submit" :disabled="loading" style="padding: 10px 20px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; border-radius: 6px; font-weight: 500; border: none; cursor: pointer; transition: all 0.2s; display: flex; align-items: center; gap: 8px;" :style="loading ? 'opacity: 0.7; cursor: not-allowed;' : ''">
              <svg v-if="loading" style="width: 16px; height: 16px; animation: spin 1s linear infinite;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
              {{ loading ? 'Updating System...' : 'Update System' }}
            </button>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

input:focus,
textarea:focus,
select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

input:hover,
textarea:hover,
select:hover {
  border-color: #9ca3af;
}
</style> 