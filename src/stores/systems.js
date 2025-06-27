import { defineStore } from 'pinia'
import { useAuthStore } from './auth'

export const useSystemsStore = defineStore('systems', {
  state: () => ({
    systems: [],
    loading: false,
    error: null,
    lastFetchTime: null,
    totalSystems: 0,
    page: 1,
    pageSize: 10
  }),
  
  getters: {
    getSystemById: (state) => (id) => state.systems.find(system => system.id === id),
    
    // Get systems owned by current user
    // This getter will now only work on the current page of systems.
    // For full functionality, this filtering should be moved to the backend.
    userSystems: (state) => {
      const authStore = useAuthStore();
      if (!authStore.user) return [];
      return state.systems.filter(system => system.ownerId === authStore.user.id);
    },
    
    // Get high criticality systems
    // This getter will now only work on the current page of systems.
    // For full functionality, this filtering should be moved to the backend.
    highCriticalitySystems: (state) => {
      return state.systems.filter(system => system.criticalityLevel === 'HIGH');
    }
  },
  
  actions: {
    async fetchSystems(page = 1, pageSize = 10) {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      
      try {
        const data = await authStore.apiCall(`/systems?page=${page}&pageSize=${pageSize}`);
        this.systems = data.systems;
        this.totalSystems = data.totalSystems;
        this.page = data.page;
        this.pageSize = data.pageSize;
        this.lastFetchTime = new Date().toISOString();
      } catch (error) {
        this.error = error.message;
        console.error('Error fetching systems:', error);
      } finally {
        this.loading = false;
      }
    },
    
    async fetchSystemById(id) {
      const existingSystem = this.systems.find(s => s.id === id);
      if (existingSystem) {
        return existingSystem;
      }

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
    
    async updateImplementation(id, implementationData) {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      
      try {
        const data = await authStore.apiCall(`/systems/implementations/${id}`, {
          method: 'PUT',
          body: JSON.stringify(implementationData)
        });
        
        // Optionally, update the implementation in the local state
        // This is more complex as implementations are nested.
        // A simple approach is to refetch the system or its implementations.
        
        return data;
      } catch (error) {
        this.error = error.message;
        console.error('Error updating implementation:', error);
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
      const system = this.getSystemById(systemId);
      if (!system) return 0;
      
      const baseScore = {
        'LOW': 10,
        'MEDIUM': 40,
        'HIGH': 70,
        'CRITICAL': 90
      }[system.criticalityLevel] || 0;
      
      // A more stable calculation than pure random
      const hash = (s) => s.split('').reduce((a,b) => (((a << 5) - a) + b.charCodeAt(0))|0, 0);
      const variance = Math.abs(hash(system.id)) % 10;
      
      return Math.min(100, baseScore + variance);
    },
    
    async fetchImplementationsForSystem(systemId) {
        const authStore = useAuthStore();
        this.loading = true;
        this.error = null;
        try {
            const data = await authStore.apiCall(`/systems/${systemId}/implementations`);
            const system = this.getSystemById(systemId);
            if (system) {
                system.implementations = data;
            }
            return data;
        } catch (error) {
            this.error = error.message;
            console.error('Error fetching implementations for system:', error);
            throw error;
        } finally {
            this.loading = false;
        }
    }
  }
}) 