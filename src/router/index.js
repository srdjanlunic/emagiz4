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
import SystemOwnerManagement from '../views/admin/SystemOwnerManagement.vue'

// Notifications component
import Notifications from '../views/Notifications.vue'

// Escalations component
import Escalations from '../views/escalations/Escalations.vue'

const routes = [
  {
    path: '/',
    redirect: '/dashboard',
    component: DashboardLayout,
    meta: { requiresAuth: true },
    children: [
      { path: '/dashboard', name: 'Dashboard', component: Dashboard, meta: { roles: ['admin', 'security_officer', 'system_owner', 'technical_expert'] } },
      { path: '/systems', name: 'systems', component: Systems, meta: { roles: ['admin', 'system_owner'] } },
      { path: '/systems/add', name: 'add-system', component: AddSystem, meta: { roles: ['admin', 'system_owner'] } },
      { path: '/systems/:id/edit', name: 'edit-system', component: EditSystem, meta: { roles: ['admin', 'system_owner'] } },
      { path: '/systems/:id', name: 'system-detail', component: SystemDetail, meta: { roles: ['admin', 'system_owner'] } },
      { path: '/cve', name: 'cve', component: CVEList, meta: { roles: ['admin', 'security_officer', 'system_owner', 'technical_expert'] } },
      { path: '/cve/:id', name: 'cve-detail', component: CveDetail, meta: { roles: ['admin', 'security_officer', 'system_owner', 'technical_expert'] } },
      { path: '/admin/users', name: 'user-management', component: UserManagement, meta: { roles: ['admin', 'security_officer'] } },
      { path: '/admin/departments', name: 'departments', component: Departments, meta: { roles: ['admin', 'security_officer'] } },
      { path: '/admin/system-owners', name: 'system-owner-management', component: SystemOwnerManagement, meta: { roles: ['admin', 'security_officer'] } },
      { path: '/notifications', name: 'notifications', component: Notifications, meta: { roles: ['admin', 'security_officer', 'system_owner'] } },
      { path: '/escalations', name: 'escalations', component: Escalations, meta: { roles: ['admin', 'security_officer', 'technical_expert'] } }
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

  // Role-based access check
  if (to.meta.roles && to.meta.roles.length > 0) {
    const userRole = authStore.userRole; // This is in uppercase
    
    // Handle UNKNOWN role - redirect to login
    if (!userRole || userRole === 'UNKNOWN') {
      console.warn('User has UNKNOWN role, redirecting to login');
      authStore.logout(); // Clear invalid session
      return next({ name: 'Login' });
    }
    
    if (!to.meta.roles.includes(userRole.toLowerCase())) {
      // Redirect to a default page if the user doesn't have access
      console.warn(`User role ${userRole} not allowed for route ${to.name}`);
      return next({ name: 'Dashboard' }); // Or a dedicated "access denied" page
    }
  }
  
  next()
})

export default router 