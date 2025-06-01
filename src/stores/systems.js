import { defineStore } from 'pinia'
import { useAuthStore } from './auth'

export const useSystemsStore = defineStore('systems', {
  state: () => ({
    systems: [],
    loading: false,
    error: null,
    lastFetchTime: null
  }),
  
  getters: {
    getSystemById: (state) => (id) => state.systems.find(system => system.id === parseInt(id)),
    
    // Get systems owned by current user
    userSystems: (state) => {
      const authStore = useAuthStore();
      if (!authStore.user) return [];
      return state.systems.filter(system => system.ownerId === authStore.user.id);
    },
    
    // Get high criticality systems
    highCriticalitySystems: (state) => {
      return state.systems.filter(system => system.criticalityLevel === 'HIGH');
    }
  },
  
  actions: {
    async fetchSystems() {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      
      try {
        const data = await authStore.apiCall('/systems');
        this.systems = data;
        this.lastFetchTime = new Date().toISOString();
      } catch (error) {
        this.error = error.message;
        console.error('Error fetching systems:', error);
      } finally {
        this.loading = false;
      }
    },
    
    async fetchSystemById(id) {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      
      try {
        const data = await authStore.apiCall(`/systems/${id}`);
        
        // Update the system in the array or add it if not present
        const existingIndex = this.systems.findIndex(s => s.id === data.id);
        if (existingIndex >= 0) {
          this.systems[existingIndex] = data;
        } else {
          this.systems.push(data);
        }
        
        return data;
      } catch (error) {
        this.error = error.message;
        console.error('Error fetching system:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async addSystem(systemData) {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      
      try {
        // Add current user as owner if not specified
        if (!systemData.ownerId) {
          systemData.ownerId = authStore.user.id;
        }
        
        const data = await authStore.apiCall('/systems', {
          method: 'POST',
          body: JSON.stringify(systemData)
        });
        
        this.systems.push(data);
        return data;
      } catch (error) {
        this.error = error.message;
        console.error('Error adding system:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async updateSystem(id, systemData) {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      
      try {
        const data = await authStore.apiCall(`/systems/${id}`, {
          method: 'PUT',
          body: JSON.stringify(systemData)
        });
        
        const index = this.systems.findIndex(s => s.id === parseInt(id));
        if (index >= 0) {
          this.systems[index] = data;
        }
        
        return data;
      } catch (error) {
        this.error = error.message;
        console.error('Error updating system:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async deleteSystem(id) {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      
      try {
        await authStore.apiCall(`/systems/${id}`, {
          method: 'DELETE'
        });
        
        this.systems = this.systems.filter(s => s.id !== parseInt(id));
      } catch (error) {
        this.error = error.message;
        console.error('Error deleting system:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async fetchSystemsByOwner(ownerId) {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      
      try {
        const data = await authStore.apiCall(`/systems/owner/${ownerId}`);
        
        // Update systems list with fetched systems
        data.forEach(fetchedSystem => {
          const existingIndex = this.systems.findIndex(s => s.id === fetchedSystem.id);
          if (existingIndex >= 0) {
            this.systems[existingIndex] = fetchedSystem;
          } else {
            this.systems.push(fetchedSystem);
          }
        });
        
        return data;
      } catch (error) {
        this.error = error.message;
        console.error('Error fetching systems by owner:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    // Calculate risk score based on vulnerabilities and system criticality
    calculateRiskScore(systemId) {
      // This would typically involve CVE data
      // For now, return a placeholder based on criticality
      const system = this.getSystemById(systemId);
      if (!system) return 0;
      
      const baseScore = {
        'LOW': 10,
        'MEDIUM': 30,
        'HIGH': 50,
        'CRITICAL': 70
      }[system.criticalityLevel] || 0;
      
      // Add random variance for demo
      return baseScore + Math.floor(Math.random() * 20);
    }
  }
}) 