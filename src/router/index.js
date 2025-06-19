import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

// Layout components
import AuthLayout from '../layouts/AuthLayout.vue'
import DashboardLayout from '../layouts/DashboardLayout.vue'

// General components
import Dashboard from '../views/Dashboard.vue'
import NotFound from '../views/NotFound.vue'

// Auth components
import Login from '../views/auth/Login.vue'
import Register from '../views/auth/Register.vue'

// Systems components
import Systems from '../views/systems/Systems.vue'
import AddSystem from '../views/systems/AddSystem.vue'
import EditSystem from '../views/systems/EditSystem.vue'
import SystemDetail from '../views/systems/SystemDetail.vue'

// CVE components
import CVEList from '../views/cve/CVEList.vue'
import CveDetail from '../views/cve/CVEDetails.vue'

// Admin components
import UserManagement from '../views/admin/UserManagement.vue'
import Departments from '../views/admin/Departments.vue'

// Notifications component
import Notifications from '../views/Notifications.vue'

const routes = [
  {
    path: '/',
    redirect: '/dashboard',
    component: DashboardLayout,
    meta: { requiresAuth: true },
    children: [
      { path: '/dashboard', name: 'Dashboard', component: Dashboard },
      { path: '/systems', name: 'systems', component: Systems },
      { path: '/systems/add', name: 'add-system', component: AddSystem },
      { path: '/systems/:id/edit', name: 'edit-system', component: EditSystem },
      { path: '/systems/:id', name: 'system-detail', component: SystemDetail },
      { path: '/cve', name: 'cve', component: CVEList },
      { path: '/cve/:id', name: 'cve-detail', component: CveDetail },
      { path: '/admin/users', name: 'user-management', component: UserManagement, meta: { requiresAdmin: true } },
      { path: '/admin/departments', name: 'departments', component: Departments, meta: { requiresAdmin: true } },
      { path: '/notifications', name: 'notifications', component: Notifications }
    ]
  },
  {
    path: '/auth',
    component: AuthLayout,
    children: [
      { path: 'login', name: 'Login', component: Login },
      { path: 'register', name: 'Register', component: Register }
    ]
  },
  { 
    path: '/:pathMatch(.*)*', 
    name: 'NotFound',
    component: NotFound 
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guards
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()

  // Ensure user state is resolved before proceeding
  if (authStore.loading) {
    await authStore.initialize()
  }

  const isAuthenticated = authStore.isAuthenticated

  // Guest-only pages
  if (to.meta.requiresGuest && isAuthenticated) {
    return next({ name: 'Dashboard' })
  }

  // Auth-required pages
  if (to.meta.requiresAuth && !isAuthenticated) {
    return next({ name: 'Login' })
  }

  // Role-based access
  if (to.meta.roles) {
    const userRole = authStore.userRole
    if (!to.meta.roles.includes(userRole)) {
      // Redirect to a default page if the user doesn't have access
      return next({ name: 'Dashboard' }) 
    }
  }
  
  // Admin-only pages
  if (to.meta.requiresAdmin && !authStore.isAdmin) {
    return next({ name: 'Dashboard' }) // Or a dedicated "access denied" page
  }

  next()
})

export default router 