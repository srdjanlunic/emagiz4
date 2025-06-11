import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

// Layout components
import DashboardLayout from '../layouts/DashboardLayout.vue'

// Auth components
import LoginPage from '../views/auth/Login.vue'
import RegisterPage from '../views/auth/Register.vue'

// Dashboard components
import DashboardView from '../views/Dashboard.vue'

// Systems components
import Systems from '../views/systems/Systems.vue'
import AddSystem from '../views/systems/AddSystem.vue'

// CVE components
import CVEList from '../views/cve/CVEList.vue'
import CVEDetails from '../views/cve/CVEDetails.vue'

// Admin components
import UserManagement from '../views/admin/UserManagement.vue'
import Departments from '../views/admin/Departments.vue'

// Notifications component
import NotificationsView from '../views/Notifications.vue'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/auth/login',
    name: 'Login',
    component: LoginPage,
    meta: { requiresGuest: true }
  },
  {
    path: '/auth/register',
    name: 'Register',
    component: RegisterPage,
    meta: { requiresGuest: true }
  },
  {
    path: '/',
    component: DashboardLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: DashboardView
      },
      {
        path: 'systems',
        name: 'Systems',
        component: Systems,
        meta: { 
          roles: ['ADMIN', 'SECURITY_OFFICER', 'SYSTEM_OWNER', 'TECHNICAL_EXPERT'] 
        }
      },
      {
        path: 'systems/add',
        name: 'AddSystem',
        component: AddSystem,
        meta: { 
          roles: ['ADMIN', 'SYSTEM_OWNER', 'SECURITY_OFFICER'] 
        }
      },
      {
        path: 'cve',
        name: 'CVEList',
        component: CVEList,
        meta: { 
          roles: ['ADMIN', 'SECURITY_OFFICER', 'SYSTEM_OWNER', 'TECHNICAL_EXPERT'] 
        }
      },
      {
        path: 'cve/:id',
        name: 'CVEDetails',
        component: CVEDetails,
        meta: { 
          roles: ['ADMIN', 'SECURITY_OFFICER', 'SYSTEM_OWNER', 'TECHNICAL_EXPERT'] 
        }
      },
      {
        path: 'notifications',
        name: 'Notifications',
        component: NotificationsView,
        meta: { 
          roles: ['ADMIN', 'SECURITY_OFFICER', 'SYSTEM_OWNER', 'TECHNICAL_EXPERT'] 
        }
      },
      {
        path: 'admin/users',
        name: 'UserManagement',
        component: UserManagement,
        meta: { 
          roles: ['ADMIN'] 
        }
      },
      {
        path: 'admin/departments',
        name: 'Departments',
        component: Departments,
        meta: { 
          roles: ['ADMIN', 'SECURITY_OFFICER'] 
        }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// Navigation guards
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()

  // Check if route requires authentication
  if (to.meta.requiresAuth) {
    if (!authStore.isAuthenticated) {
      return next({ name: 'Login' })
    }

    // Validate token if needed (can be intensive)
    const isValid = await authStore.validateToken()
    if (!isValid) {
      return next({ name: 'Login' })
    }

    // Check role-based access
    const userRole = authStore.userRole
    if (to.meta.roles && !to.meta.roles.includes(userRole)) {
      return next({ name: 'Dashboard' }) // Redirect to a safe page
    }
  }

  // Redirect authenticated users away from guest-only pages
  if (to.meta.requiresGuest && authStore.isAuthenticated) {
    return next({ name: 'Dashboard' })
  }

  next()
})

export default router 