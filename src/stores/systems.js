import { defineStore } from 'pinia'

export const useSystemsStore = defineStore('systems', {
  state: () => ({
    systems: JSON.parse(localStorage.getItem('systems')) || [],
    loading: false,
  }),
  getters: {
    getSystemById: (state) => (id) => state.systems.find(s => s.id === id),
    getSystemsByOwner: (state) => (ownerId) => state.systems.filter(s => s.ownerId === ownerId),
  },
  actions: {
    async addSystem(system) {
      this.loading = true;
      try {
        // For demo, we'll just add it to the array
        const newSystem = {
          ...system,
          id: Date.now(),
          createdAt: new Date().toISOString(),
          riskScore: this.calculateRiskScore(system),
        };
        
        this.systems.push(newSystem);
        localStorage.setItem('systems', JSON.stringify(this.systems));
        
        return Promise.resolve(newSystem);
      } catch (error) {
        return Promise.reject(error);
      } finally {
        this.loading = false;
      }
    },
    calculateRiskScore(system) {
      // Simple algorithm for risk score calculation
      let score = 0;
      
      // Base risk based on criticality
      if (system.criticality === 'high') score += 30;
      else if (system.criticality === 'medium') score += 20;
      else score += 10;
      
      // Risk based on data classification
      if (system.dataClassification === 'sensitive') score += 30;
      else if (system.dataClassification === 'internal') score += 15;
      else score += 5;
      
      // Risk based on connectivity
      if (system.internetFacing) score += 20;
      
      return score;
    }
  }
}); 