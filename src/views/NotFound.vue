<template>
  <div style="min-height: 100vh; display: flex; align-items: center; justify-content: center; background-color: #f5f5f7; text-align: center;">
    <div>
      <h1 style="font-size: 48px; font-weight: bold; color: #1f2937;">404</h1>
      <p style="font-size: 18px; color: #6b7280; margin-top: 8px;">Page Not Found</p>
      <p style="color: #6b7280; margin-top: 16px;">The page you are looking for does not exist.</p>
      <router-link to="/dashboard" style="display: inline-block; margin-top: 24px; padding: 12px 24px; background-color: #3b82f6; color: white; border-radius: 8px; text-decoration: none; font-weight: 500;">
        Go to Dashboard
      </router-link>
    </div>
  </div>
</template>

<script setup>
// No script needed for this simple component
</script> 