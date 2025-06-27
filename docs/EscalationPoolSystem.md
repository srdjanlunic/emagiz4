# Escalation Pool System

## Overview
The escalation system now uses a **pool-based approach** where escalations are available to all technical experts rather than being assigned to specific individuals.

## How It Works

### Creating Escalations
- **Who can escalate**: Admins, Security Officers, and System Owners
- **When to escalate**: Critical CVEs or unclear status situations
- **Assignment**: CVEs are escalated to the **expert pool** (not specific individuals)

### Technical Expert Access
- All technical experts can see **all unassigned escalations**
- Escalations with `tech_expert_id = NULL` are available to any expert
- Any technical expert can pick up and work on any available escalation

### Database Structure
- `escalations` table now supports NULL values for `tech_expert_id`
- Query: `SELECT * FROM escalations WHERE tech_expert_id = ? OR tech_expert_id IS NULL`
- This returns both assigned escalations and pool escalations

### API Endpoints
- **GET** `/vulnerabilities/escalated/{techExpertId}` - Returns escalated CVEs for the expert
  - Includes escalations assigned to the specific expert
  - Includes all unassigned escalations (pool)

### Frontend Changes
- Removed technical expert selection from escalation modal
- Escalation button no longer requires expert availability
- UI shows "Escalate to Experts" (plural) indicating pool approach
- Success message confirms escalation to "technical experts"

### Benefits
1. **Load Balancing**: Any available expert can pick up work
2. **Flexibility**: No bottlenecks from unavailable specific experts
3. **Efficiency**: Faster response times as first available expert can respond
4. **Simplified Workflow**: No manual assignment required

### Workflow Example
1. Security Officer finds critical CVE
2. Clicks "Escalate" and provides reason
3. CVE is added to escalation pool (tech_expert_id = NULL)
4. All technical experts see this escalation in their dashboard
5. First available expert can start working on it
6. When expert responds, escalation can be assigned to them 