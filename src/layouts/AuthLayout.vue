<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100">
    <div class="max-w-md w-full">
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold text-blue-600">CVE Management System</h1>
        <p class="text-gray-600 mt-2">Your security management platform</p>
      </div>
      <div class="bg-white p-8 rounded-lg shadow-md">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template> 