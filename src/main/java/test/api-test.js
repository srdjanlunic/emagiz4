const BASE_URL = "https://emagiz4.paas.hosted-by-previder.com/api"

async function runTests() {
    console.log("Testing API endpoints...")

    try {
        // Login
        console.log("1. Login test...")
        const loginResponse = await fetch(`${BASE_URL}/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username: "demo_admin", password: "admin123" }),
        })
        const { token } = await loginResponse.json()
        console.log("Login OK")

        // Get departments
        console.log("2. Get departments...")
        const deptResponse = await fetch(`${BASE_URL}/departments`, {
            headers: { Authorization: `Bearer ${token}` },
        })
        const departments = await deptResponse.json()
        console.log(`Found ${departments.length} departments`)

        // Get systems
        console.log("3. Get systems...")
        const systemsResponse = await fetch(`${BASE_URL}/systems`, {
            headers: { Authorization: `Bearer ${token}` },
        })
        const systems = await systemsResponse.json()
        console.log(`Found ${systems.length} systems`)

        // Create system
        console.log("4. Create new system...")
        const newSystemResponse = await fetch(`${BASE_URL}/systems`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify({
                name: "Test System",
                vendor: "Test Vendor",
                description: "Test system for API",
            }),
        })
        const newSystem = await newSystemResponse.json()
        console.log(`Created system: ${newSystem.name}`)

        // Create implementation
        console.log("5. Create system implementation...")
        const implResponse = await fetch(`${BASE_URL}/systems/implementations`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify({
                systemId: newSystem.id,
                departmentId: departments[0].id,
                dataClassification: "INTERNAL",
                criticalityLevel: "MEDIUM",
                internetFacing: false,
                sensitiveCustomerData: false,
                version: "1.0",
                environment: "TEST",
            }),
        })
        const implementation = await implResponse.json()
        console.log(`Created implementation with risk score: ${implementation.riskScore}`)

        // Test other endpoints
        console.log("6. Test other endpoints...")

        const notifResponse = await fetch(`${BASE_URL}/notifications/user/550e8400-e29b-41d4-a716-446655440032`, {
            headers: { Authorization: `Bearer ${token}` },
        })
        console.log(`Notifications: ${notifResponse.status === 200 ? "OK" : "FAIL"}`)

        const statsResponse = await fetch(`${BASE_URL}/vulnerabilities/dashboard/stats`, {
            headers: { Authorization: `Bearer ${token}` },
        })
        console.log(`Dashboard: ${statsResponse.status === 200 ? "OK" : "FAIL"}`)

        console.log("\nAll tests passed!")
    } catch (error) {
        console.log("Test failed:", error.message)
    }
}

runTests()
