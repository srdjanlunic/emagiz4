<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useSystemsStore } from '../../stores/systems';

const route = useRoute();
const router = useRouter();
const systemsStore = useSystemsStore();

const systemId = route.params.id;
const system = ref(null);
const loading = ref(false);
const error = ref(null);

const fetchSystem = async () => {
  loading.value = true;
  error.value = null;
  try {
    system.value = await systemsStore.fetchSystemById(systemId);
  } catch (err) {
    error.value = 'Failed to load system data.';
  } finally {
    loading.value = false;
  }
};

const handleUpdate = async () => {
  loading.value = true;
  error.value = null;
  try {
    await systemsStore.updateSystem(systemId, system.value);
    router.push('/systems');
  } catch (err) {
    error.value = 'Failed to update system.';
  } finally {
    loading.value = false;
  }
};

const handleDelete = async () => {
  if (confirm('Are you sure you want to delete this system?')) {
    loading.value = true;
    error.value = null;
    try {
      await systemsStore.deleteSystem(systemId);
      router.push('/systems');
    } catch (err) {
      error.value = 'Failed to delete system.';
    } finally {
      loading.value = false;
    }
  }
};

onMounted(fetchSystem);
</script>

<template>
  <div class="system-edit-page">
    <div v-if="loading" class="loading-state">Loading system details...</div>
    <div v-if="error" class="error-state">{{ error }}</div>
    
    <form v-if="system" @submit.prevent="handleUpdate">
      <h1>Edit System: {{ system.name }}</h1>
      
      <div>
        <label for="name">System Name</label>
        <input id="name" v-model="system.name" type="text" required>
      </div>
      
      <div>
        <label for="version">Version</label>
        <input id="version" v-model="system.version" type="text">
      </div>

      <div>
        <label for="vendor">Vendor</label>
        <input id="vendor" v-model="system.vendor" type="text">
      </div>
      
      <div>
        <label for="criticality">Criticality Level</label>
        <select id="criticality" v-model="system.criticalityLevel">
          <option>LOW</option>
          <option>MEDIUM</option>
          <option>HIGH</option>
          <option>CRITICAL</option>
        </select>
      </div>

      <button type="submit" :disabled="loading">
        {{ loading ? 'Updating...' : 'Update System' }}
      </button>

      <button type="button" @click="handleDelete" :disabled="loading" class="delete-button">
        Delete System
      </button>
    </form>
  </div>
</template>

<style scoped>
/* Basic styling for the form */
.system-edit-page {
  padding: 24px;
}
form {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-width: 600px;
  margin: auto;
}
.delete-button {
  background-color: #ef4444;
  color: white;
}
</style> 