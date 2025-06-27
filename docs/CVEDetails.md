# CVEDetails Component

## Purpose
The CVEDetails component displays detailed information about a specific CVE (Common Vulnerabilities and Exposures) and provides functionality for managing the CVE lifecycle.

## Key Features

### Basic Information Display
- CVE ID and description
- Severity level with color-coded badges
- Current status with color-coded badges
- Discovery date
- CVSS score
- External reference links
- Status history timeline

### System Assignment (New Feature)
- **Purpose**: Allows authorized users to assign/unassign systems that are affected by a CVE
- **Access**: Only available to Admins and Security Officers
- **Location**: "Assign Systems" button in the header actions
- **Functionality**:
  - Modal interface showing all available systems
  - Checkbox selection for affected systems
  - Real-time count of selected systems
  - Bulk select/clear all options
  - System names and descriptions displayed

### Status Management
- Update CVE status (Open, In Progress, Resolved, Accepted Risk)
- Add notes explaining status changes
- Available to System Owners (for their systems), Security Officers, and Admins

### Escalation
- Escalate CVEs to Technical Experts
- Provide escalation reason
- Select specific Technical Expert for assignment

## User Permissions
- **System Owners**: Can update status and escalate for systems they own
- **Security Officers**: Full access to all features including system assignment
- **Admins**: Full access to all features including system assignment
- **Technical Experts**: View access (escalation target)

## API Integration
- `GET /vulnerabilities/{cveId}` - Fetch CVE details
- `PUT /vulnerabilities/{cveId}/systems` - Update affected systems
- `PUT /vulnerabilities/system/{systemId}/vulnerability/{cveId}` - Update CVE status
- `POST /escalations` - Create escalation

## Usage Example
1. Navigate to CVE list and click "View Details" on any CVE
2. Use "Assign Systems" button to manage which systems are affected
3. Use "Update Status" to change CVE status with explanatory notes
4. Use "Escalate" to assign to Technical Experts when needed

## Side Effects
- Notifications are sent when status changes occur
- System assignment changes are immediately reflected in the CVE list
- Status changes are logged in the CVE history
- Escalations create new escalation records and notifications

## Recent Updates
- **Fixed System Assignment**: Added backend endpoint `PUT /vulnerabilities/{cveId}/systems` to properly handle system assignments
- **Backend Integration**: The system assignment now properly creates/deletes `system_vulnerabilities` relationships
- **Permission Control**: Only Admins and Security Officers can assign systems to CVEs
- **Database Consistency**: System assignments are automatically managed with proper cleanup

## Known Caveats
- System assignment requires systems to be loaded first (automatic on component mount)
- Status updates require at least one affected system
- Escalation uses the first affected system for system-specific escalations
- Modal interfaces prevent interaction with background content while open
- System assignments create new relationships with default "open" status 