import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user')) || null,
    loading: false,
  }),
  getters: {
    isLoggedIn: (state) => !!state.user,
    isAdmin: (state) => state.user?.role === 'admin',
    isSecurityOfficer: (state) => state.user?.role === 'security_officer',
    isSystemOwner: (state) => state.user?.role === 'system_owner',
  },
  actions: {
    async login(credentials) {
      this.loading = true;
      try {
        // For demo, we'll just simulate a login
        const user = {
          id: 1,
          username: credentials.username,
          role: credentials.username.includes('admin') ? 'admin' : 
                credentials.username.includes('security') ? 'security_officer' : 'system_owner',
          email: `${credentials.username}@example.com`,
          department: 'IT',
        };
        
        // Store in localStorage
        localStorage.setItem('user', JSON.stringify(user));
        
        // Update store
        this.user = user;
        
        return Promise.resolve(user);
      } catch (error) {
        return Promise.reject(error);
      } finally {
        this.loading = false;
      }
    },
    async logout() {
      localStorage.removeItem('user');
      this.user = null;
    }
  }
}); 