import { computed } from 'vue';
import { useAuthStore } from './stores/auth';

export const navigation = computed(() => {
  const authStore = useAuthStore();
  const userRole = authStore.user?.roleName?.toLowerCase();
  
  return [
  {
    name: 'Main',
    links: [
      { name: 'Dashboard', to: '/', icon: 'M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2H5a2 2 0 00-2-2z M8 5a2 2 0 012-2h4a2 2 0 012 2v2H8V5z' },
      { name: 'Systems', to: '/systems', icon: 'M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z', roles: ['admin', 'system_owner'] },
      { name: 'Notifications', to: '/notifications', icon: 'M10 5a2 2 0 114 0c0 7.667 5.333 11.333 8 12v1H2v-1c2.667-.667 8-4.333 8-12zM9 21h6', roles: ['admin', 'security_officer', 'system_owner'] },
    ],
  },
  {
    name: 'Vulnerabilities',
    links: [
      { name: 'CVEs', to: '/cve', icon: 'M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.34 16.5c-.77.833.192 2.5 1.732 2.5z', roles: ['security_officer', 'admin', 'technical_expert', 'system_owner'] },
      { name: 'Escalations', to: '/escalations', icon: 'M13 10V3L4 14h7v7l9-11h-7z', roles: ['security_officer', 'admin', 'technical_expert'] },
    ],
    roles: ['security_officer', 'admin', 'technical_expert', 'system_owner'],
  },
  {
    name: 'Admin',
    links: [
      { name: 'Users', to: '/admin/users', icon: 'M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.25 2.25 0 11-4.5 0 2.25 2.25 0 014.5 0z' },
      { name: 'Departments', to: '/admin/departments', icon: 'M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4' },
      { name: 'System Owners', to: '/admin/system-owners', icon: 'M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z M17 9h2v2h-2V9z M17 13h2v2h-2v-2z' },
    ],
    roles: ['admin', 'security_officer'],
  },
].map(section => {
  // Filter links based on user role if they have role restrictions
  const filteredLinks = section.links.filter(link => {
    if (!link.roles) return true;
    return link.roles.includes(userRole);
  });
  
  return { ...section, links: filteredLinks };
}).filter(section => {
  // Only show sections that have visible links or no role restrictions
  if (!section.roles) return section.links.length > 0;
  return section.roles.includes(userRole) && section.links.length > 0;
});
}); 