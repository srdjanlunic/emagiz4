<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useSystemsStore } from '../../stores/systems';
import { useAuthStore } from '../../stores/auth';

const router = useRouter();
const systemsStore = useSystemsStore();
const authStore = useAuthStore();

const user = computed(() => authStore.user);

const system = ref({
  name: '',
  description: '',
  criticality: 'medium',
  dataClassification: 'internal',
  internetFacing: false,
  ownerId: user.value?.id,
  operatingSystem: '',
  hostName: '',
  ipAddress: '',
  location: '',
  contacts: '',
  notes: ''
});

const errors = ref({});
const loading = ref(false);

const validateForm = () => {
  const newErrors = {};
  
  if (!system.value.name) {
    newErrors.name = 'System name is required';
  }
  
  if (!system.value.description) {
    newErrors.description = 'System description is required';
  }
  
  if (!system.value.criticality) {
    newErrors.criticality = 'Criticality is required';
  }
  
  if (!system.value.dataClassification) {
    newErrors.dataClassification = 'Data classification is required';
  }
  
  if (!system.value.operatingSystem) {
    newErrors.operatingSystem = 'Operating system is required';
  }
  
  errors.value = newErrors;
  return Object.keys(newErrors).length === 0;
};

const handleSubmit = async () => {
  if (!validateForm()) {
    return;
  }
  
  loading.value = true;
  
  try {
    await systemsStore.addSystem(system.value);
    router.push('/systems');
  } catch (error) {
    alert('Error adding system');
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="add-system-page">
    <div class="page-header">
      <h1>Add New System</h1>
      <p>Register a new information system and set its initial risk profile</p>
    </div>
    
    <div class="form-container">
      <form @submit.prevent="handleSubmit">
        <div class="form-grid">
          <!-- Basic Information -->
          <div class="form-section">
            <div class="section-header">
              <svg class="section-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              <h2>Basic Information</h2>
            </div>
            
            <div class="form-group">
              <label for="name">
                System Name
                <span class="required">*</span>
              </label>
              <div class="input-wrapper">
                <input
                  id="name"
                  v-model="system.name"
                  type="text"
                  :class="{ 'error': errors.name }"
                  placeholder="Enter system name"
                />
                <p v-if="errors.name" class="error-message">{{ errors.name }}</p>
              </div>
            </div>
            
            <div class="form-group">
              <label for="description">
                Description
                <span class="required">*</span>
              </label>
              <div class="input-wrapper">
                <textarea
                  id="description"
                  v-model="system.description"
                  rows="3"
                  :class="{ 'error': errors.description }"
                  placeholder="Enter system description"
                ></textarea>
                <p v-if="errors.description" class="error-message">{{ errors.description }}</p>
              </div>
            </div>
            
            <div class="form-group">
              <label for="os">
                Operating System
                <span class="required">*</span>
              </label>
              <div class="input-wrapper">
                <input
                  id="os"
                  v-model="system.operatingSystem"
                  type="text"
                  :class="{ 'error': errors.operatingSystem }"
                  placeholder="e.g., Windows Server 2019, Ubuntu 20.04"
                />
                <p v-if="errors.operatingSystem" class="error-message">{{ errors.operatingSystem }}</p>
              </div>
            </div>
            
            <div class="form-group">
              <label for="hostname">Host Name</label>
              <input
                id="hostname"
                v-model="system.hostName"
                type="text"
                placeholder="Enter host name"
              />
            </div>
            
            <div class="form-group">
              <label for="ip">IP Address</label>
              <input
                id="ip"
                v-model="system.ipAddress"
                type="text"
                placeholder="e.g., 192.168.1.1"
              />
            </div>
          </div>
          
          <!-- Risk and Additional Information -->
          <div class="form-section">
            <div class="section-header">
              <svg class="section-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
              <h2>Risk Profile</h2>
            </div>
            
            <div class="form-group">
              <label for="criticality">
                Criticality
                <span class="required">*</span>
              </label>
              <div class="input-wrapper">
                <select
                  id="criticality"
                  v-model="system.criticality"
                  :class="{ 'error': errors.criticality }"
                >
                  <option value="low">Low</option>
                  <option value="medium">Medium</option>
                  <option value="high">High</option>
                </select>
                <p v-if="errors.criticality" class="error-message">{{ errors.criticality }}</p>
              </div>
            </div>
            
            <div class="form-group">
              <label for="dataClass">
                Data Classification
                <span class="required">*</span>
              </label>
              <div class="input-wrapper">
                <select
                  id="dataClass"
                  v-model="system.dataClassification"
                  :class="{ 'error': errors.dataClassification }"
                >
                  <option value="public">Public</option>
                  <option value="internal">Internal</option>
                  <option value="sensitive">Sensitive/Confidential</option>
                </select>
                <p v-if="errors.dataClassification" class="error-message">{{ errors.dataClassification }}</p>
              </div>
            </div>
            
            <div class="form-group checkbox-group">
              <div class="checkbox-wrapper">
                <input
                  id="internet"
                  v-model="system.internetFacing"
                  type="checkbox"
                />
                <label for="internet">Internet Facing System</label>
              </div>
              <p class="help-text">Check if this system is directly accessible from the internet</p>
            </div>
            
            <div class="form-group">
              <label for="location">Location</label>
              <input
                id="location"
                v-model="system.location"
                type="text"
                placeholder="e.g., Main Datacenter, Cloud"
              />
            </div>
            
            <div class="form-group">
              <label for="contacts">Additional Contacts</label>
              <textarea
                id="contacts"
                v-model="system.contacts"
                rows="2"
                placeholder="Enter additional contacts"
              ></textarea>
            </div>
            
            <div class="form-group">
              <label for="notes">Notes</label>
              <textarea
                id="notes"
                v-model="system.notes"
                rows="2"
                placeholder="Any additional notes"
              ></textarea>
            </div>
          </div>
        </div>
        
        <!-- Submit buttons -->
        <div class="form-actions">
          <button
            type="button"
            class="btn-secondary"
            @click="router.push('/systems')"
          >
            Cancel
          </button>
          <button
            type="submit"
            class="btn-primary"
            :class="{ 'loading': loading }"
            :disabled="loading"
          >
            <span v-if="loading" class="loader"></span>
            <span>{{ loading ? 'Saving...' : 'Save System' }}</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.add-system-page {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
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

.form-container {
  background: white;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 32px;
}

@media (min-width: 768px) {
  .form-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 48px;
  }
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.section-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
}

.section-icon {
  width: 24px;
  height: 24px;
  color: #6b7280;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  display: flex;
  align-items: center;
  gap: 4px;
}

.required {
  color: #dc2626;
}

.input-wrapper {
  position: relative;
  width: 100%;
}

input,
select,
textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  line-height: 1.5;
  color: #111827;
  background-color: white;
  transition: all 0.2s;
}

select {
  appearance: none;
  background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3e%3cpath stroke='%236B7280' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.5' d='M6 8l4 4 4-4'/%3e%3c/svg%3e");
  background-position: right 12px center;
  background-repeat: no-repeat;
  background-size: 16px 16px;
  padding-right: 40px;
}

textarea {
  resize: vertical;
  min-height: 80px;
}

input:hover,
select:hover,
textarea:hover {
  border-color: #9ca3af;
}

input:focus,
select:focus,
textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.error {
  border-color: #dc2626 !important;
}

.error-message {
  color: #dc2626;
  font-size: 13px;
  margin-top: 4px;
}

.checkbox-group {
  margin-top: 4px;
}

.checkbox-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.checkbox-wrapper input[type="checkbox"] {
  width: 16px;
  height: 16px;
  border-radius: 4px;
  border: 1px solid #d1d5db;
  cursor: pointer;
}

.checkbox-wrapper label {
  font-size: 14px;
  color: #374151;
  cursor: pointer;
}

.help-text {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
}

.btn-primary,
.btn-secondary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-primary {
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  color: white;
  border: none;
}

.btn-primary:hover:not(:disabled) {
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.btn-primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-secondary {
  background: white;
  color: #374151;
  border: 1px solid #d1d5db;
}

.btn-secondary:hover {
  background: #f9fafb;
  border-color: #9ca3af;
}

.loader {
  width: 16px;
  height: 16px;
  border: 2px solid #ffffff;
  border-bottom-color: transparent;
  border-radius: 50%;
  display: inline-block;
  animation: rotation 1s linear infinite;
}

@keyframes rotation {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* Dark mode support */
@media (prefers-color-scheme: dark) {
  .form-container {
    background-color: #1f2937;
  }

  .section-header h2 {
    color: #f3f4f6;
  }

  .form-group label {
    color: #e5e7eb;
  }

  input,
  select,
  textarea {
    background-color: #374151;
    border-color: #4b5563;
    color: #f3f4f6;
  }

  input::placeholder,
  textarea::placeholder {
    color: #9ca3af;
  }

  input:hover,
  select:hover,
  textarea:hover {
    border-color: #6b7280;
  }

  .btn-secondary {
    background-color: #374151;
    border-color: #4b5563;
    color: #e5e7eb;
  }

  .btn-secondary:hover {
    background-color: #4b5563;
    border-color: #6b7280;
  }

  .form-actions {
    border-color: #374151;
  }
}
</style> 