import { defineStore } from 'pinia'
import { useAuthStore } from './auth'
import { useNotificationsStore } from './notifications'

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
    async fetchLastImportDate() {
      const authStore = useAuthStore();
      try {
        const data = await authStore.apiCall('/settings/last_cve_import_time');
        this.lastImportDate = data.value;
      } catch (error) {
        // Not a critical error, so just log it
        console.error('Could not fetch last CVE import date.', error);
      }
    },

    async fetchCVEs() {
      const now = new Date();
      const fiveMinutes = 5 * 60 * 1000;

      if (this.lastFetchTime && (now - new Date(this.lastFetchTime)) < fiveMinutes) {
        return;
      }

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
      const existingCVE = this.cves.find(c => c.cveId === cveId);
      if (existingCVE) {
        return existingCVE;
      }

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

    async fetchEscalatedCVEs(techExpertId) {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      
      try {
        // Fetch escalated CVEs for the technical expert
        const data = await authStore.apiCall(`/vulnerabilities/escalated/${techExpertId}`);
        const transformedCVEs = data.map(this.transformCVEData);
        
        // Replace the CVEs list with only escalated CVEs for technical experts
        this.cves = transformedCVEs;
        
        return transformedCVEs;
      } catch (error) {
        this.error = error.message;
        console.error('Error fetching escalated CVEs:', error);
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
      const authStore = useAuthStore()
      const notificationsStore = useNotificationsStore()
      this.loading = true
      this.error = null
      
      try {
        const result = await authStore.apiCall('/vulnerabilities/import', {
          method: 'POST'
        });
        
        await this.fetchCVEs();
        
        notificationsStore.addNotification(result.message || 'CVEs imported successfully.', 'success');
        await this.fetchLastImportDate();
        return { message: 'CVE import completed successfully' };
      } catch (error) {
        this.error = error.message;
        notificationsStore.addNotification(error.message || 'Error importing CVEs.', 'error');
        console.error('Error importing CVEs:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async updateCVEStatus(cveId, systemId, status, explanation) {
      const authStore = useAuthStore();
      const notificationsStore = useNotificationsStore();
      this.loading = true;
      this.error = null;
      try {
        if (!systemId) {
            notificationsStore.addNotification('Could not determine the system for this CVE status update.', 'error');
            throw new Error('Could not determine the system for this CVE status update.');
        }
        
        await authStore.apiCall(`/vulnerabilities/system/${systemId}/vulnerability/${cveId}`, {
          method: 'PUT',
          body: JSON.stringify({ status, notes: explanation }),
        });
        
        // After updating, refetch the data for that system to get the new status
        await this.fetchCVEBySystemAndCveId(systemId, cveId);

        notificationsStore.addNotification('CVE status updated successfully!', 'success');
      } catch (error) {
        this.error = error.message;
        notificationsStore.addNotification(error.message || `Failed to update CVE ${cveId} status.`, 'error');
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async fetchCVEBySystemAndCveId(systemId, cveId) {
        const authStore = useAuthStore();
        this.loading = true;
        this.error = null;
        try {
            const data = await authStore.apiCall(`/vulnerabilities/system/${systemId}/vulnerability/${cveId}`);
            const transformedCVE = this.transformCVEData(data);
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
    
    async markNotificationRead(cveId, userId) {
      const authStore = useAuthStore()
      const cve = this.cves.find(c => c.cveId === cveId)
      if (cve && cve.notifications) {
        const notification = cve.notifications.find(n => n.userId === userId);
        if (notification) {
          notification.read = true;
        }
      }
    },
    
    // Transform backend CVE data to match frontend expectations
    transformCVEData(backendCVE) {
      return {
        id: backendCVE.id,
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
        externalLinks: backendCVE.externalLinks || [],
        statusHistory: backendCVE.statusHistory || [],
        notifications: backendCVE.notifications || [],
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
    },

    async getCVEHistory(cveId) {
      // Implementation of getCVEHistory method
    },

    async escalateCVE(cveId, systemId, reason, techExpertId) {
        const authStore = useAuthStore();
        const notificationsStore = useNotificationsStore();
        this.loading = true;
        this.error = null;
        try {
            await authStore.apiCall('/escalations', {
                method: 'POST',
                body: JSON.stringify({
                    cveId,
                    systemId,
                    reason,
                    techExpertId,
                    securityOfficerId: authStore.user.id
                }),
            });
            notificationsStore.addNotification(`CVE ${cveId} escalated successfully.`, 'success');
        } catch (error) {
            this.error = error.message;
            notificationsStore.addNotification(error.message || `Failed to escalate CVE ${cveId}.`, 'error');
            throw error;
        } finally {
            this.loading = false;
        }
    },

    async updateAffectedSystems(cveId, systemIds) {
      const authStore = useAuthStore();
      const notificationsStore = useNotificationsStore();
      this.loading = true;
      this.error = null;
      
      try {
        await authStore.apiCall(`/vulnerabilities/${cveId}/systems`, {
          method: 'PUT',
          body: JSON.stringify({ systemIds }),
        });
        
        // Update the local CVE data
        const cveIndex = this.cves.findIndex(c => c.cveId === cveId);
        if (cveIndex >= 0) {
          this.cves[cveIndex].affectedSystems = systemIds;
        }
        
        notificationsStore.addNotification('Affected systems updated successfully!', 'success');
      } catch (error) {
        this.error = error.message;
        notificationsStore.addNotification(error.message || `Failed to update affected systems for CVE ${cveId}.`, 'error');
        throw error;
      } finally {
        this.loading = false;
      }
    }
  }
}) 