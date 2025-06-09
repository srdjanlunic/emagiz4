import { defineStore } from 'pinia'

const API_BASE_URL = 'https://emagiz4.paas.hosted-by-previder.com/api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || null, 
    loading: false,
    error: null
  }),
  
  getters: {
    isAuthenticated: (state) => !!state.token && !!state.user,
    isAdmin: (state) => state.user?.role === 'ADMIN',
    isSecurityOfficer: (state) => state.user?.role === 'SECURITY_OFFICER',
    isSystemOwner: (state) => state.user?.role === 'SYSTEM_OWNER',
    isTechnicalExpert: (state) => state.user?.role === 'TECHNICAL_EXPERT',
    userRole: (state) => state.user?.role || null
  },
  
  actions: {
    async login(credentials) {
      this.loading = true;
      this.error = null;
      
      try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(credentials)
        });
        
        if (!response.ok) {
          const errorData = await response.json().catch(() => ({ message: 'Login failed' }));
          throw new Error(errorData.message || 'Invalid credentials');
        }
        
        const data = await response.json();
        
        this.token = data.token;
        this.user = data.user;
        
        // Store in localStorage
        localStorage.setItem('token', data.token);
        localStorage.setItem('user', JSON.stringify(data.user));
        
        return data;
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async register(userData) {
      this.loading = true;
      this.error = null;
      
      try {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(userData)
        });
        
        if (!response.ok) {
          const errorData = await response.json().catch(() => ({ message: 'Registration failed' }));
          throw new Error(errorData.message || 'Registration failed');
        }
        
        const data = await response.json();
        return data;
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async validateToken() {
      if (!this.token) return false;
      
      try {
        const response = await fetch(`${API_BASE_URL}/auth/validate`, {
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        });
        
        if (!response.ok) {
          this.logout();
          return false;
        }
        
        return true;
      } catch (error) {
        this.logout();
        return false;
      }
    },
    
    logout() {
      this.user = null;
      this.token = null;
      this.error = null;
      
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    },
    
    // Helper method to make authenticated API calls
    async apiCall(endpoint, options = {}) {
      if (!this.token) {
        throw new Error('No authentication token');
      }
      
      const url = endpoint.startsWith('http') ? endpoint : `${API_BASE_URL}${endpoint}`;
      
      const response = await fetch(url, {
        ...options,
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.token}`,
          ...options.headers
        }
      });
      
      if (response.status === 401) {
        this.logout();
        throw new Error('Authentication expired');
      }
      
      if (!response.ok) {
        const errorData = await response.json().catch(() => ({ message: 'Request failed' }));
        throw new Error(errorData.message || `Request failed with status ${response.status}`);
      }
      
      return response.json();
    }
  }
}) 