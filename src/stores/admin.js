import { defineStore } from 'pinia'
import { useAuthStore } from './auth'

export const useAdminStore = defineStore('admin', {
  state: () => ({
    users: [],
    departments: [],
    loading: false,
    error: null,
  }),
  getters: {
    getUserById: (state) => (id) => state.users.find(u => u.id === id),
    getUsersByDepartment: (state) => (departmentId) => 
      state.users.filter(u => {
        const dept = state.departments.find(d => d.id === departmentId);
        return dept && dept.userIds.includes(u.id);
      }),
    getDepartmentById: (state) => (id) => state.departments.find(d => d.id === id),
    getUsersInDepartment: (state) => (departmentId) => {
      const department = state.departments.find(d => d.id === departmentId);
      if (!department) return [];
      return state.users.filter(u => department.userIds.includes(u.id));
    }
  },
  actions: {
    // User Management
    async fetchUsers() {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      try {
        const data = await authStore.apiCall('/users');
        this.users = data;
      } catch (error) {
        this.error = error.message;
        console.error('Error fetching users:', error);
      } finally {
        this.loading = false;
      }
    },

    async createUser(user) {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      try {
        const newUser = await authStore.apiCall('/users', {
          method: 'POST',
          body: JSON.stringify(user),
        });
        this.users.push(newUser);
        return newUser;
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async updateUser(id, updates) {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      try {
        const updatedUser = await authStore.apiCall(`/users/${id}`, {
          method: 'PUT',
          body: JSON.stringify(updates),
        });
        const index = this.users.findIndex(u => u.id === id);
        if (index !== -1) {
          this.users[index] = updatedUser;
        }
        return updatedUser;
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async deleteUser(id) {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      try {
        await authStore.apiCall(`/users/${id}`, {
          method: 'DELETE',
        });
        this.users = this.users.filter(u => u.id !== id);
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },

    // Department Management
    async fetchDepartments() {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      try {
        const data = await authStore.apiCall('/departments');
        this.departments = data;
      } catch (error) {
        this.error = error.message;
        console.error('Error fetching departments:', error);
      } finally {
        this.loading = false;
      }
    },

    async createDepartment(department) {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      try {
        const newDepartment = await authStore.apiCall('/departments', {
          method: 'POST',
          body: JSON.stringify(department),
        });
        this.departments.push(newDepartment);
        return newDepartment;
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async updateDepartment(id, updates) {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      try {
        const updatedDept = await authStore.apiCall(`/departments/${id}`, {
          method: 'PUT',
          body: JSON.stringify(updates),
        });
        const index = this.departments.findIndex(d => d.id === id);
        if (index !== -1) {
          this.departments[index] = updatedDept;
        }
        return updatedDept;
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async deleteDepartment(id) {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      try {
        await authStore.apiCall(`/departments/${id}`, {
          method: 'DELETE',
        });
        this.departments = this.departments.filter(d => d.id !== id);
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },

    // Department-User Management
    async assignUsersToDepartment(departmentId, userIds) {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      try {
        const updatedDepartment = await authStore.apiCall(
          `/departments/${departmentId}/users`,
          {
            method: 'POST',
            body: JSON.stringify(userIds),
          }
        );
        const index = this.departments.findIndex(d => d.id === departmentId);
        if (index !== -1) {
          this.departments[index] = updatedDepartment;
        }
        return updatedDepartment;
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async removeUsersFromDepartment(departmentId, userIds) {
      const authStore = useAuthStore();
      this.loading = true;
      this.error = null;
      try {
        const updatedDepartment = await authStore.apiCall(
          `/departments/${departmentId}/users`,
          {
            method: 'DELETE',
            body: JSON.stringify(userIds),
          }
        );
        const index = this.departments.findIndex(d => d.id === departmentId);
        if (index !== -1) {
          this.departments[index] = updatedDepartment;
        }
        return updatedDepartment;
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
  },
}); 