<template>
  <div class="system-detail-page" style="padding: 24px;">
    <div v-if="loading" style="text-align: center; padding: 48px;">
      <p>Loading system details...</p>
    </div>
    <div v-else-if="error" style="color: red; text-align: center; padding: 48px;">
      <p>{{ error }}</p>
    </div>
    <div v-else-if="system">
      <h1 class="text-2xl font-bold mb-4">{{ system.name }}</h1>
      <p class="mb-2"><strong>Vendor:</strong> {{ system.vendor }}</p>
      <p class="mb-2"><strong>Description:</strong> {{ system.description }}</p>
      <p class="mb-2"><strong>Risk Score:</strong> {{ system.riskScore }}</p>
      
      <div class="mt-8">
        <h2 class="text-xl font-bold mb-4">Implementations</h2>
        <div v-if="system.implementations && system.implementations.length > 0">
          <div v-for="impl in system.implementations" :key="impl.id" class="p-4 border rounded-md mb-4">
            <p><strong>Version:</strong> {{ impl.version }}</p>
            <p><strong>Environment:</strong> {{ impl.environment }}</p>
            <p><strong>Risk Score:</strong> {{ impl.riskScore }}</p>
            <p><strong>Location:</strong> {{ impl.location }}</p>
            <p><strong>Department:</strong> {{ impl.departmentName }}</p>
          </div>
        </div>
        <p v-else>No implementations found for this system.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useSystemsStore } from '../../stores/systems'

const route = useRoute()
const systemsStore = useSystemsStore()

const system = ref(null)
const loading = ref(true)
const error = ref(null)

onMounted(async () => {
  const systemId = route.params.id
  try {
    const fetchedSystem = await systemsStore.fetchSystemById(systemId)
    if (fetchedSystem) {
      system.value = fetchedSystem;
      // Also fetch implementations
      system.value.implementations = await systemsStore.fetchImplementationsForSystem(systemId);
    } else {
      error.value = 'System not found.'
    }
  } catch (err) {
    error.value = 'Failed to load system details.'
    console.error(err)
  } finally {
    loading.value = false
  }
})
</script> 