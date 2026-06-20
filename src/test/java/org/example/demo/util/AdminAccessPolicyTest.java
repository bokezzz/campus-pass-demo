package org.example.demo.util;

import org.example.demo.model.AdminRole;
import org.example.demo.model.ReservationType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminAccessPolicyTest {

    @Test
    void systemAndSchoolAdminsCanAccessEveryManagedModule() {
        for (AdminRole role : new AdminRole[]{AdminRole.SYSTEM_ADMIN, AdminRole.SCHOOL_ADMIN}) {
            assertTrue(AdminAccessPolicy.canAccess("/admin/departments", null, role, false));
            assertTrue(AdminAccessPolicy.canAccess("/admin/users", null, role, false));
            assertTrue(AdminAccessPolicy.canAccess("/admin/reservations", ReservationType.PUBLIC, role, false));
            assertTrue(AdminAccessPolicy.canAccess("/admin/reservations", ReservationType.OFFICIAL, role, false));
            assertTrue(AdminAccessPolicy.canAccess("/admin/logs", null, role, false));
        }
    }

    @Test
    void departmentAdminCanOnlyAccessDepartmentsAndOfficialReservations() {
        assertTrue(AdminAccessPolicy.canAccess("/admin/departments", null, AdminRole.DEPARTMENT_ADMIN, false));
        assertTrue(AdminAccessPolicy.canAccess("/admin/reservations", ReservationType.OFFICIAL, AdminRole.DEPARTMENT_ADMIN, false));
        assertFalse(AdminAccessPolicy.canAccess("/admin/users", null, AdminRole.DEPARTMENT_ADMIN, false));
        assertFalse(AdminAccessPolicy.canAccess("/admin/reservations", ReservationType.PUBLIC, AdminRole.DEPARTMENT_ADMIN, false));
        assertFalse(AdminAccessPolicy.canAccess("/admin/logs", null, AdminRole.DEPARTMENT_ADMIN, false));
    }

    @Test
    void auditAdminCanOnlyAccessAuditLogs() {
        assertTrue(AdminAccessPolicy.canAccess("/admin/logs", null, AdminRole.AUDIT_ADMIN, false));
        assertFalse(AdminAccessPolicy.canAccess("/admin/departments", null, AdminRole.AUDIT_ADMIN, false));
        assertFalse(AdminAccessPolicy.canAccess("/admin/reservations", ReservationType.OFFICIAL, AdminRole.AUDIT_ADMIN, false));
        assertFalse(AdminAccessPolicy.canAccess("/admin/reservations", ReservationType.PUBLIC, AdminRole.AUDIT_ADMIN, false));
    }

    @Test
    void publicReservationPermissionAddsOnlyPublicReservationAccess() {
        assertTrue(AdminAccessPolicy.canManagePublicReservations(AdminRole.DEPARTMENT_ADMIN, true));
        assertTrue(AdminAccessPolicy.canAccess("/admin/reservations", ReservationType.PUBLIC, AdminRole.DEPARTMENT_ADMIN, true));
        assertTrue(AdminAccessPolicy.canAccess("/admin/reservations", ReservationType.PUBLIC, AdminRole.AUDIT_ADMIN, true));
        assertFalse(AdminAccessPolicy.canAccess("/admin/users", null, AdminRole.AUDIT_ADMIN, true));
    }

    @Test
    void allRolesCanReachDashboardButUnknownModulesAreDenied() {
        for (AdminRole role : AdminRole.values()) {
            assertTrue(AdminAccessPolicy.canAccess("/admin/dashboard", null, role, false));
        }
        assertFalse(AdminAccessPolicy.canAccess("/admin/unknown", null, AdminRole.SYSTEM_ADMIN, false));
    }
}
