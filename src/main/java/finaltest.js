const https = require("https")
const http = require("http")
const url = require("url")

const baseUrl = "https://emagiz4.paas.hosted-by-previder.com/api"

function makeRequest(requestUrl, options = {}) {
    return new Promise((resolve, reject) => {
        const parsedUrl = url.parse(requestUrl)
        const isHttps = parsedUrl.protocol === "https:"
        const client = isHttps ? https : http

        const requestOptions = {
            hostname: parsedUrl.hostname,
            port: parsedUrl.port || (isHttps ? 443 : 80),
            path: parsedUrl.path,
            method: options.method || "GET",
            headers: options.headers || {},
        }

        const req = client.request(requestOptions, (res) => {
            let data = ""

            res.on("data", (chunk) => {
                data += chunk
            })

            res.on("end", () => {
                try {
                    const jsonData = JSON.parse(data)
                    resolve({
                        status: res.statusCode,
                        json: () => Promise.resolve(jsonData),
                        ok: res.statusCode >= 200 && res.statusCode < 300,
                    })
                } catch (e) {
                    resolve({
                        status: res.statusCode,
                        json: () => Promise.resolve({ error: "Invalid JSON", rawData: data }),
                        ok: res.statusCode >= 200 && res.statusCode < 300,
                        rawData: data,
                    })
                }
            })
        })

        req.on("error", (err) => {
            reject(err)
        })

        if (options.body) {
            req.write(options.body)
        }

        req.end()
    })
}

async function authenticate() {
    console.log("Getting authentication token...")

    const response = await makeRequest(`${baseUrl}/auth/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            username: "demo_admin",
            password: "admin123",
        }),
    })

    const data = await response.json()

    if (data.token) {
        console.log("✅ Authentication successful")
        return data.token
    } else {
        throw new Error("Authentication failed: " + JSON.stringify(data))
    }
}

async function getCurrentVulnerabilities(token) {
    console.log("\n=== CHECKING CURRENT VULNERABILITIES ===")

    const response = await makeRequest(`${baseUrl}/vulnerabilities`, {
        method: "GET",
        headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
        },
    })

    const vulnerabilities = await response.json()
    console.log(`Current vulnerability count: ${vulnerabilities.length}`)

    console.log("\nExisting CVEs:")
    vulnerabilities.forEach((vuln, index) => {
        console.log(`  ${index + 1}. ${vuln.cveId} - ${vuln.severity} (${vuln.cvssScore})`)
    })

    return vulnerabilities
}

async function importCVEs(token) {
    console.log("\n=== IMPORTING NEW CVES ===")

    const response = await makeRequest(`${baseUrl}/vulnerabilities/import`, {
        method: "POST",
        headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
        },
        body: JSON.stringify({}),
    })

    const result = await response.json()
    console.log("Import response status:", response.status)
    console.log("Import result:", JSON.stringify(result, null, 2))

    return result
}

async function createSpecificCVEs(token) {
    console.log("\n=== CREATING SPECIFIC TEST CVES ===")

    const testCVEs = [
        {
            cveId: "CVE-2024-12345",
            description: "Critical remote code execution vulnerability in Apache HTTP Server",
            severity: "CRITICAL",
            cvssScore: 9.8,
            affectedProducts: "cpe:2.3:a:apache:http_server:*:*:*:*:*:*:*:*",
            vendor: "Apache Software Foundation",
            publishedDate: new Date("2024-01-15").toISOString(),
            lastModified: new Date("2024-01-16").toISOString(),
            importedAt: new Date().toISOString(),
        },
        {
            cveId: "CVE-2024-54321",
            description: "SQL injection vulnerability in MySQL Server",
            severity: "HIGH",
            cvssScore: 8.1,
            affectedProducts: "cpe:2.3:a:oracle:mysql:*:*:*:*:*:*:*:*",
            vendor: "Oracle Corporation",
            publishedDate: new Date("2024-02-10").toISOString(),
            lastModified: new Date("2024-02-11").toISOString(),
            importedAt: new Date().toISOString(),
        },
        {
            cveId: "CVE-2024-98765",
            description: "Cross-site scripting vulnerability in WordPress",
            severity: "MEDIUM",
            cvssScore: 6.1,
            affectedProducts: "cpe:2.3:a:wordpress:wordpress:*:*:*:*:*:*:*:*",
            vendor: "WordPress Foundation",
            publishedDate: new Date("2024-03-05").toISOString(),
            lastModified: new Date("2024-03-06").toISOString(),
            importedAt: new Date().toISOString(),
        },
        {
            cveId: "CVE-2024-11111",
            description: "Privilege escalation vulnerability in Linux Kernel",
            severity: "HIGH",
            cvssScore: 7.8,
            affectedProducts: "cpe:2.3:o:linux:linux_kernel:*:*:*:*:*:*:*:*",
            vendor: "Linux Foundation",
            publishedDate: new Date("2024-04-20").toISOString(),
            lastModified: new Date("2024-04-21").toISOString(),
            importedAt: new Date().toISOString(),
        },
        {
            cveId: "CVE-2024-22222",
            description: "Buffer overflow vulnerability in OpenSSL",
            severity: "CRITICAL",
            cvssScore: 9.1,
            affectedProducts: "cpe:2.3:a:openssl:openssl:*:*:*:*:*:*:*:*",
            vendor: "OpenSSL Project",
            publishedDate: new Date("2024-05-15").toISOString(),
            lastModified: new Date("2024-05-16").toISOString(),
            importedAt: new Date().toISOString(),
        },
    ]

    let createdCount = 0

    for (const cve of testCVEs) {
        try {
            console.log(`\nCreating CVE: ${cve.cveId}`)

            const response = await makeRequest(`${baseUrl}/vulnerabilities/test-create`, {
                method: "POST",
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(cve),
            })

            const result = await response.json()

            if (response.status === 201 && result.success) {
                console.log(`✅ Successfully created ${cve.cveId} with ID: ${result.id}`)
                createdCount++
            } else {
                console.log(`❌ Failed to create ${cve.cveId}: ${result.message || "Unknown error"}`)
            }
        } catch (error) {
            console.error(`❌ Error creating ${cve.cveId}:`, error.message)
        }
    }

    console.log(`\n📊 Successfully created ${createdCount} out of ${testCVEs.length} CVEs`)
    return createdCount
}

async function searchVulnerabilities(token, searchTerm) {
    console.log(`\n=== SEARCHING FOR: "${searchTerm}" ===`)

    const response = await makeRequest(`${baseUrl}/vulnerabilities/search?q=${encodeURIComponent(searchTerm)}`, {
        method: "GET",
        headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
        },
    })

    const results = await response.json()
    console.log(`Found ${results.length} vulnerabilities matching "${searchTerm}":`)

    results.forEach((vuln, index) => {
        console.log(`  ${index + 1}. ${vuln.cveId} - ${vuln.severity} - ${vuln.vendor}`)
    })

    return results
}

async function main() {
    try {
        console.log("=== REAL CVE IMPORT TEST ===")
        console.log(`Base URL: ${baseUrl}`)

        // Authenticate
        const token = await authenticate()

        // Get current vulnerabilities (before import)
        const beforeVulns = await getCurrentVulnerabilities(token)
        const beforeCount = beforeVulns.length

        // Try the built-in import function first
        await importCVEs(token)

        // Create specific test CVEs
        const createdCount = await createSpecificCVEs(token)

        // Get vulnerabilities after import
        const afterVulns = await getCurrentVulnerabilities(token)
        const afterCount = afterVulns.length

        // Show the results
        console.log("\n" + "=".repeat(50))
        console.log("📊 IMPORT RESULTS SUMMARY")
        console.log("=".repeat(50))
        console.log(`Before import: ${beforeCount} vulnerabilities`)
        console.log(`After import:  ${afterCount} vulnerabilities`)
        console.log(`Net increase:  ${afterCount - beforeCount} vulnerabilities`)
        console.log(`Manually created: ${createdCount} vulnerabilities`)

        // Test search functionality
        await searchVulnerabilities(token, "Apache")
        await searchVulnerabilities(token, "MySQL")
        await searchVulnerabilities(token, "CRITICAL")

        // Show all vulnerabilities by severity
        console.log("\n=== VULNERABILITIES BY SEVERITY ===")
        const severityCounts = {}
        afterVulns.forEach((vuln) => {
            severityCounts[vuln.severity] = (severityCounts[vuln.severity] || 0) + 1
        })

        Object.entries(severityCounts).forEach(([severity, count]) => {
            console.log(`${severity}: ${count} vulnerabilities`)
        })

        console.log("\n✅ CVE IMPORT TEST COMPLETED SUCCESSFULLY!")
        console.log(`Your system now has ${afterCount} vulnerabilities in the database.`)
    } catch (error) {
        console.error("❌ Test failed:", error)
        process.exit(1)
    }
}

main()
