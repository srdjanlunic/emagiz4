import { createApp } from 'vue'
import { createPinia } from 'pinia'
import './style.css'
import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/auth'
import { useSystemsStore } from './stores/systems'
import { useCVEsStore } from './stores/cves'
import { useAdminStore } from './stores/admin'
import '@mdi/font/css/materialdesignicons.css'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

// Initialize authentication and data
const initializeApp = async () => {
  const authStore = useAuthStore()
  const systemsStore = useSystemsStore()
  const cvesStore = useCVEsStore()
  const adminStore = useAdminStore()

  // Check if user is logged in and validate token
  if (authStore.isAuthenticated) {
    try {
      const isValid = await authStore.validateToken()
      if (isValid) {
        // Fetch initial data based on user role
        const promises = []
        
        // Systems data - accessible by admin, security_officer, and system_owner
        if (authStore.isAdmin || authStore.isSecurityOfficer || authStore.isSystemOwner) {
          promises.push(systemsStore.fetchSystems())
        }
        
        // CVE data - accessible by different roles with different endpoints
        if (authStore.isAdmin || authStore.isSecurityOfficer) {
          // Admin and security officers see all CVEs
          promises.push(cvesStore.fetchCVEs())
        } else if (authStore.isTechnicalExpert) {
          // Technical experts see only escalated CVEs
          promises.push(cvesStore.fetchEscalatedCVEs(authStore.user.id))
        } else if (authStore.isSystemOwner) {
          // System owners will load CVEs when they view their systems
          // No need to load all CVEs during initialization
        }
        
        // Admin data - only accessible by admin and security_officer
        if (authStore.isAdmin || authStore.isSecurityOfficer) {
          promises.push(adminStore.fetchUsers())
          promises.push(adminStore.fetchDepartments())
        }
        
        // Execute all authorized requests in parallel
        if (promises.length > 0) {
          await Promise.allSettled(promises)
        }
      }
    } catch (error) {
      console.error('Error initializing app:', error)
    }
  }
}

// Initialize app and then mount
initializeApp().then(() => {
  app.mount('#app')
})
