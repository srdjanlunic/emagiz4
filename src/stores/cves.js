import { defineStore } from 'pinia'
import { useAuthStore } from './auth'

export const useCVEsStore = defineStore('cves', {
  state: () => ({
    cves: [],
    loading: false,
    error: null,
    lastImportDate: null,
    lastFetchTime: null
  }),
  
  getters: {
    getCVEById: (state) => (id) => state.cves.find(cve => cve.cveId === id),
    
    getOpenCVEsBySystem: (state) => (systemId) => {
      return state.cves.filter(cve => 
        cve.affectedSystems?.includes(systemId) && 
        (cve.status === 'open' || cve.status === 'in_progress')
      );
    },
    
    getResolvedCVEsBySystem: (state) => (systemId) => {
      return state.cves.filter(cve => 
        cve.affectedSystems?.includes(systemId) && 
        cve.status === 'resolved'
      );
    },
    
    getCVEsBySystem: (state) => (systemId) => {
      return state.cves.filter(cve => cve.affectedSystems?.includes(systemId));
    },
    
    getUnreadNotifications: (state) => (userId) => {
      if (!state.cves.length || !userId) return [];
      return state.cves.filter(cve => 
        cve.notifications?.some(n => n.userId === userId && !n.isRead)
      );
    },
    
    getHighSeverityCVEs: (state) => state.cves.filter(cve => cve.severity === 'high'),
  },
  
  actions: {
    async fetchCVEs() {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      
      try {
        const data = await authStore.apiCall('/vulnerabilities');
        this.cves = data.map(this.transformCVEData);
        this.lastFetchTime = new Date().toISOString();
      } catch (error) {
        this.error = error.message;
        console.error('Error fetching CVEs:', error);
      } finally {
        this.loading = false;
      }
    },
    
    async fetchCVEById(cveId) {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      
      try {
        const data = await authStore.apiCall(`/vulnerabilities/${cveId}`);
        const transformedCVE = this.transformCVEData(data);
        
        // Update the CVE in the array or add it if not present
        const existingIndex = this.cves.findIndex(c => c.cveId === transformedCVE.cveId);
        if (existingIndex >= 0) {
          this.cves[existingIndex] = transformedCVE;
        } else {
          this.cves.push(transformedCVE);
        }
        
        return transformedCVE;
      } catch (error) {
        this.error = error.message;
        console.error('Error fetching CVE:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async fetchCVEsBySystem(systemId) {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      
      try {
        const data = await authStore.apiCall(`/vulnerabilities/system/${systemId}`);
        const transformedCVEs = data.map(this.transformCVEData);
        
        // Update CVEs list with fetched CVEs
        transformedCVEs.forEach(fetchedCVE => {
          const existingIndex = this.cves.findIndex(c => c.cveId === fetchedCVE.cveId);
          if (existingIndex >= 0) {
            this.cves[existingIndex] = fetchedCVE;
          } else {
            this.cves.push(fetchedCVE);
          }
        });
        
        return transformedCVEs;
      } catch (error) {
        this.error = error.message;
        console.error('Error fetching CVEs by system:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async searchCVEs(searchTerm) {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      
      try {
        const data = await authStore.apiCall(`/vulnerabilities/search?q=${encodeURIComponent(searchTerm)}`);
        return data.map(this.transformCVEData);
      } catch (error) {
        this.error = error.message;
        console.error('Error searching CVEs:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async importCVEs() {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      
      try {
        await authStore.apiCall('/vulnerabilities/import', {
          method: 'POST'
        });
        
        this.lastImportDate = new Date().toISOString();
        
        // Refresh CVEs after import
        await this.fetchCVEs();
        
        return { message: 'CVE import completed successfully' };
      } catch (error) {
        this.error = error.message;
        console.error('Error importing CVEs:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async updateCVEStatus(cveId, status, systemId) {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      try {
        const updatedCVE = await authStore.apiCall(`/vulnerabilities/${cveId}/status`, {
          method: 'PUT',
          body: JSON.stringify({ status, systemId }),
        });
        const index = this.cves.findIndex(c => c.cveId === cveId);
        if (index !== -1) {
          this.cves[index] = { ...this.cves[index], ...this.transformCVEData(updatedCVE) };
        }
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async markNotificationRead(cveId, userId) {
      // Note: This should use the notifications API endpoint
      const authStore = useAuthStore();
      
      try {
        // Find the notification for this CVE and user
        const cve = this.getCVEById(cveId);
        if (cve && cve.notifications) {
          const notification = cve.notifications.find(n => n.userId === userId);
          if (notification) {
            // This would call PUT /notifications/{id}/read in the real implementation
            notification.read = true;
          }
        }
      } catch (error) {
        console.error('Error marking notification as read:', error);
      }
    },
    
    // Transform backend CVE data to match frontend expectations
    transformCVEData(backendCVE) {
      return {
        id: backendCVE.cveId,
        cveId: backendCVE.cveId,
        description: backendCVE.description,
        severity: this.mapSeverity(backendCVE.severity),
        status: backendCVE.status || 'open',
        dateDiscovered: backendCVE.publishedDate || backendCVE.importedAt,
        discoveredAt: backendCVE.publishedDate || backendCVE.importedAt,
        lastModified: backendCVE.lastModified,
        cvssScore: backendCVE.cvssScore,
        affectedProducts: backendCVE.affectedProducts,
        vendor: backendCVE.vendor,
        affectedSystems: backendCVE.affectedSystemIds || [],
        externalLinks: [
          { title: 'MITRE', url: `https://cve.mitre.org/cgi-bin/cvename.cgi?name=${backendCVE.cveId}` },
          { title: 'NVD', url: `https://nvd.nist.gov/vuln/detail/${backendCVE.cveId}` }
        ],
        statusHistory: backendCVE.statusHistory || [
          { 
            status: 'open', 
            note: 'Imported from external source.', 
            userId: 1, 
            timestamp: backendCVE.importedAt || new Date().toISOString() 
          }
        ],
        notifications: backendCVE.notifications || []
      };
    },
    
    // Map backend severity to frontend severity
    mapSeverity(backendSeverity) {
      if (!backendSeverity) return 'low';
      
      const severity = backendSeverity.toLowerCase();
      if (severity.includes('critical')) return 'critical';
      if (severity.includes('high')) return 'high';
      if (severity.includes('medium')) return 'medium';
      return 'low';
    }
  }
}) 