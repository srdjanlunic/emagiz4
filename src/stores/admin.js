import { defineStore } from 'pinia'

export const useAdminStore = defineStore('admin', {
  state: () => ({
    users: JSON.parse(localStorage.getItem('users')) || [
      {
        id: 1,
        username: 'admin',
        email: 'admin@example.com',
        role: 'admin',
        department: 'IT Security',
        createdAt: new Date().toISOString()
      }
    ],
    departments: JSON.parse(localStorage.getItem('departments')) || [
      {
        id: 1,
        name: 'IT Security',
        createdAt: new Date().toISOString(),
        userIds: [1]
      }
    ],
    loading: false
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
    async createUser(user) {
      this.loading = true;
      try {
        const newUser = {
          ...user,
          id: Date.now(),
          createdAt: new Date().toISOString()
        };
        
        this.users.push(newUser);
        localStorage.setItem('users', JSON.stringify(this.users));
        
        return Promise.resolve(newUser);
      } catch (error) {
        return Promise.reject(error);
      } finally {
        this.loading = false;
      }
    },
    async updateUser(id, updates) {
      const userIndex = this.users.findIndex(u => u.id === id);
      if (userIndex === -1) return Promise.reject('User not found');
      
      const updatedUser = { ...this.users[userIndex], ...updates };
      this.users[userIndex] = updatedUser;
      localStorage.setItem('users', JSON.stringify(this.users));
      
      return Promise.resolve(updatedUser);
    },
    async deleteUser(id) {
      const userIndex = this.users.findIndex(u => u.id === id);
      if (userIndex === -1) return Promise.reject('User not found');
      
      this.users.splice(userIndex, 1);
      
      // Also remove user from departments
      this.departments.forEach(dept => {
        const userIdIndex = dept.userIds.indexOf(id);
        if (userIdIndex !== -1) {
          dept.userIds.splice(userIdIndex, 1);
        }
      });
      
      localStorage.setItem('users', JSON.stringify(this.users));
      localStorage.setItem('departments', JSON.stringify(this.departments));
      
      return Promise.resolve();
    },
    async createDepartment(department) {
      this.loading = true;
      try {
        const newDepartment = {
          ...department,
          id: Date.now(),
          createdAt: new Date().toISOString(),
          userIds: department.userIds || []
        };
        
        this.departments.push(newDepartment);
        localStorage.setItem('departments', JSON.stringify(this.departments));
        
        return Promise.resolve(newDepartment);
      } catch (error) {
        return Promise.reject(error);
      } finally {
        this.loading = false;
      }
    },
    async updateDepartment(id, updates) {
      const deptIndex = this.departments.findIndex(d => d.id === id);
      if (deptIndex === -1) return Promise.reject('Department not found');
      
      const updatedDept = { ...this.departments[deptIndex], ...updates };
      this.departments[deptIndex] = updatedDept;
      localStorage.setItem('departments', JSON.stringify(this.departments));
      
      return Promise.resolve(updatedDept);
    },
    async deleteDepartment(id) {
      const deptIndex = this.departments.findIndex(d => d.id === id);
      if (deptIndex === -1) return Promise.reject('Department not found');
      
      this.departments.splice(deptIndex, 1);
      localStorage.setItem('departments', JSON.stringify(this.departments));
      
      return Promise.resolve();
    },
    async assignUsersToDepartment(departmentId, userIds) {
      const department = this.departments.find(d => d.id === departmentId);
      if (!department) return Promise.reject('Department not found');
      
      // Update the department's user list
      department.userIds = [...new Set([...department.userIds, ...userIds])];
      localStorage.setItem('departments', JSON.stringify(this.departments));
      
      return Promise.resolve(department);
    },
    async removeUsersFromDepartment(departmentId, userIds) {
      const department = this.departments.find(d => d.id === departmentId);
      if (!department) return Promise.reject('Department not found');
      
      // Remove users from department
      department.userIds = department.userIds.filter(id => !userIds.includes(id));
      localStorage.setItem('departments', JSON.stringify(this.departments));
      
      return Promise.resolve(department);
    }
  }
}); 