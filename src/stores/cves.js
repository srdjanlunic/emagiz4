import { defineStore } from 'pinia'
import { useSystemsStore } from './systems'

export const useCVEsStore = defineStore('cves', {
  state: () => ({
    cves: JSON.parse(localStorage.getItem('cves')) || [],
    loading: false,
    lastImportDate: localStorage.getItem('lastImportDate') || null,
  }),
  getters: {
    getCVEById: (state) => (id) => state.cves.find(cve => cve.id === id),
    getOpenCVEsBySystem: (state) => (systemId) => {
      return state.cves.filter(cve => 
        cve.affectedSystems.includes(systemId) && 
        cve.status !== 'resolved' && 
        cve.status !== 'accepted_risk'
      );
    },
    getResolvedCVEsBySystem: (state) => (systemId) => {
      return state.cves.filter(cve => 
        cve.affectedSystems.includes(systemId) && 
        (cve.status === 'resolved' || cve.status === 'accepted_risk')
      );
    },
    getUnreadNotifications: (state) => (userId) => {
      return state.cves
        .filter(cve => 
          cve.notifications.some(n => n.userId === userId && !n.read)
        )
        .map(cve => ({
          ...cve,
          notification: cve.notifications.find(n => n.userId === userId)
        }));
    }
  },
  actions: {
    async importCVEs() {
      this.loading = true;
      try {
        // For demo, generate some random CVEs
        const systems = useSystemsStore().systems;
        const newCVEs = [];
        
        for (let i = 0; i < 5; i++) {
          const randSystem = systems[Math.floor(Math.random() * systems.length)];
          if (!randSystem) continue;
          
          const cveYear = 2023 + Math.floor(Math.random() * 2);
          const cveNum = Math.floor(Math.random() * 10000).toString().padStart(4, '0');
          
          const newCVE = {
            id: `CVE-${cveYear}-${cveNum}`,
            description: `Security vulnerability affecting ${randSystem.name} that could allow unauthorized access.`,
            severity: ['low', 'medium', 'high', 'critical'][Math.floor(Math.random() * 4)],
            status: 'open',
            dateDiscovered: new Date().toISOString(),
            affectedSystems: [randSystem.id],
            externalLinks: [
              { title: 'MITRE', url: `https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-${cveYear}-${cveNum}` },
              { title: 'NVD', url: `https://nvd.nist.gov/vuln/detail/CVE-${cveYear}-${cveNum}` }
            ],
            statusHistory: [],
            notifications: [
              { 
                userId: randSystem.ownerId,
                read: false,
                createdAt: new Date().toISOString(),
                message: `New vulnerability CVE-${cveYear}-${cveNum} affects your system ${randSystem.name}`
              }
            ]
          };
          
          newCVEs.push(newCVE);
        }
        
        this.cves = [...this.cves, ...newCVEs];
        this.lastImportDate = new Date().toISOString();
        
        localStorage.setItem('cves', JSON.stringify(this.cves));
        localStorage.setItem('lastImportDate', this.lastImportDate);
        
        return Promise.resolve(newCVEs);
      } catch (error) {
        return Promise.reject(error);
      } finally {
        this.loading = false;
      }
    },
    async updateCVEStatus(cveId, newStatus, note, userId) {
      const cveIndex = this.cves.findIndex(cve => cve.id === cveId);
      if (cveIndex === -1) return Promise.reject('CVE not found');
      
      // Update the CVE
      const updatedCVE = {
        ...this.cves[cveIndex],
        status: newStatus,
        statusHistory: [
          ...this.cves[cveIndex].statusHistory,
          {
            status: newStatus,
            note,
            userId,
            timestamp: new Date().toISOString()
          }
        ]
      };
      
      this.cves[cveIndex] = updatedCVE;
      localStorage.setItem('cves', JSON.stringify(this.cves));
      
      return Promise.resolve(updatedCVE);
    },
    async markNotificationRead(cveId, userId) {
      const cveIndex = this.cves.findIndex(cve => cve.id === cveId);
      if (cveIndex === -1) return Promise.reject('CVE not found');
      
      const notificationIndex = this.cves[cveIndex].notifications.findIndex(n => n.userId === userId);
      if (notificationIndex === -1) return Promise.reject('Notification not found');
      
      // Mark as read
      this.cves[cveIndex].notifications[notificationIndex].read = true;
      localStorage.setItem('cves', JSON.stringify(this.cves));
      
      return Promise.resolve(this.cves[cveIndex]);
    }
  }
}); 