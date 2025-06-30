import { defineStore } from 'pinia'

const API_BASE_URL = 'http://localhost:8088/vulnerability-management-system/api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || null, 
    loading: false,
    error: null,
    apiUrl: 'http://localhost:8088/vulnerability-management-system', // Base API URL
  }),
  
  getters: {
    isAuthenticated: (state) => !!state.token && !!state.user,
    isAdmin: (state) => state.user?.roleName?.toUpperCase() === 'ADMIN',
    isSecurityOfficer: (state) => state.user?.roleName?.toUpperCase() === 'SECURITY_OFFICER',
    isSystemOwner: (state) => state.user?.roleName?.toUpperCase() === 'SYSTEM_OWNER',
    isTechnicalExpert: (state) => state.user?.roleName?.toUpperCase() === 'TECHNICAL_EXPERT',
    userRole: (state) => state.user?.roleName?.toUpperCase() || null
  },
  
  actions: {
    async initialize() {
      // Initialize the auth store - validate current token if exists
      if (this.token) {
        const isValid = await this.validateToken();
        if (!isValid) {
          this.logout();
        }
      }
    },

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
        
        const data = await response.json();

        if (!response.ok) {
          throw new Error(data.error || 'Invalid credentials');
        }
        
        this.token = data.token;
        this.user = data.user;
        
        // If role name is still UNKNOWN, try to map it based on standard role IDs
        if (this.user && (this.user.roleName === 'UNKNOWN' || !this.user.roleName)) {
          // Map role names to ensure consistency
          const roleNameMap = {
            'admin': 'ADMIN',
            'system_owner': 'SYSTEM_OWNER', 
            'security_officer': 'SECURITY_OFFICER',
            'technical_expert': 'TECHNICAL_EXPERT',
            'ADMIN': 'ADMIN',
            'SYSTEM_OWNER': 'SYSTEM_OWNER',
            'SECURITY_OFFICER': 'SECURITY_OFFICER',
            'TECHNICAL_EXPERT': 'TECHNICAL_EXPERT'
          };
          
          // First try to use the roleName from server if it's valid
          if (this.user.roleName && roleNameMap[this.user.roleName]) {
            this.user.roleName = roleNameMap[this.user.roleName];
          } else {
            // If server didn't provide a valid role name, map based on roleId UUID
            const roleIdMap = {
              'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15': 'ADMIN',
              'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12': 'SYSTEM_OWNER', 
              'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13': 'SECURITY_OFFICER',
              'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14': 'TECHNICAL_EXPERT'
            };
            this.user.roleName = roleIdMap[this.user.roleId] || 'UNKNOWN';
          }
        }
        
        console.log("User logged in:", this.user);
        console.log("User role:", this.user?.roleName);
        
        // If role is still UNKNOWN, this indicates a data issue
        if (this.user.roleName === 'UNKNOWN') {
          console.error('Role mapping failed - user has UNKNOWN role after mapping attempts');
          console.error('User data:', this.user);
        }
        
        // Store in localStorage
        localStorage.setItem('token', data.token);
        localStorage.setItem('user', JSON.stringify(this.user));
        
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
          throw new Error(errorData.error || 'Registration failed');
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
      const url = `${API_BASE_URL}${endpoint}`;
      
      const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${this.token}`,
        ...options.headers
      }
      
      const response = await fetch(url, {
        ...options,
        headers: headers
      });
      
      if (response.status === 401) {
        this.logout();
        throw new Error('Authentication expired');
      }
      
      const responseText = await response.text();
      if (!response.ok) {
        try {
            const errorData = JSON.parse(responseText);
            throw new Error(errorData.message || `Request failed with status ${response.status}`);
        } catch (e) {
            throw new Error(responseText || `Request failed with status ${response.status}`);
        }
      }
      
      try {
        return JSON.parse(responseText);
      } catch (e) {
        return responseText;
      }
    },

    async demoLogin(roleName) {
      this.loading = true
      this.error = null
      try {
        const response = await fetch(`${API_BASE_URL}/auth/demo-login`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ roleName }),
        });

        const data = await response.json();

        if (!response.ok) {
          throw new Error(data.error || 'Demo login failed');
        }

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
        this.loading = false
      }
    },

    setAuth(token, user) {
      this.token = token;
      this.user = user;
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(user));
    }
  }
}) 