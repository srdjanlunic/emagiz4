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
        // Fetch initial data in parallel
        await Promise.allSettled([
          systemsStore.fetchSystems(),
          cvesStore.fetchCVEs(),
          adminStore.fetchUsers(),
          adminStore.fetchDepartments()
        ])
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
