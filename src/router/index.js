import { createRouter, createWebHistory } from 'vue-router'

// Layout components
import DefaultLayout from '../layouts/DefaultLayout.vue'
import AuthLayout from '../layouts/AuthLayout.vue'

// Auth pages
import Login from '../views/auth/Login.vue'
import Register from '../views/auth/Register.vue'

// Main pages
import Dashboard from '../views/Dashboard.vue'
import Systems from '../views/systems/Systems.vue'
import AddSystem from '../views/systems/AddSystem.vue'
import CVEList from '../views/cve/CVEList.vue'
import CVEDetails from '../views/cve/CVEDetails.vue'
import Notifications from '../views/Notifications.vue'
import UserManagement from '../views/admin/UserManagement.vue'
import Departments from '../views/admin/Departments.vue'

const routes = [
  {
    path: '/',
    component: DefaultLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '/',
        name: 'Dashboard',
        component: Dashboard,
      },
      {
        path: '/systems',
        name: 'Systems',
        component: Systems,
      },
      {
        path: '/systems/add',
        name: 'AddSystem',
        component: AddSystem,
      },
      {
        path: '/cve',
        name: 'CVEList',
        component: CVEList,
      },
      {
        path: '/cve/:id',
        name: 'CVEDetails',
        component: CVEDetails,
        props: true,
      },
      {
        path: '/notifications',
        name: 'Notifications',
        component: Notifications,
      },
      {
        path: '/admin/users',
        name: 'UserManagement',
        component: UserManagement,
        meta: { requiresAdmin: true }
      },
      {
        path: '/admin/departments',
        name: 'Departments',
        component: Departments,
        meta: { requiresAdmin: true }
      },
    ]
  },
  {
    path: '/',
    component: AuthLayout,
    meta: { isGuest: true },
    children: [
      {
        path: '/login',
        name: 'Login',
        component: Login
      },
      {
        path: '/register',
        name: 'Register',
        component: Register
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard for auth
router.beforeEach((to, from, next) => {
  // This is a demo, so we'll just check if there's a user in localStorage
  const user = localStorage.getItem('user')
  
  // If route requires auth and user is not logged in
  if (to.meta.requiresAuth && !user) {
    next({ name: 'Login' })
  }
  // If route is for guests only and user is logged in
  else if (to.meta.isGuest && user) {
    next({ name: 'Dashboard' })
  }
  // If route requires admin and user is not admin
  else if (to.meta.requiresAdmin && user) {
    const userData = JSON.parse(user)
    if (userData.role !== 'admin') {
      next({ name: 'Dashboard' })
    } else {
      next()
    }
  }
  else {
    next()
  }
})

export default router 