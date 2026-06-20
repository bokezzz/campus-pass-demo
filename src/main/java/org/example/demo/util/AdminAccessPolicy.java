package org.example.demo.util;

import org.example.demo.model.AdminRole;
import org.example.demo.model.ReservationType;

public final class AdminAccessPolicy {
    private AdminAccessPolicy() {
    }

    public static boolean canManagePublicReservations(AdminRole role, boolean publicReservationPermission) {
        return role == AdminRole.SYSTEM_ADMIN
                || role == AdminRole.SCHOOL_ADMIN
                || publicReservationPermission;
    }

    public static boolean canAccess(String path, ReservationType reservationType, AdminRole role,
                                    boolean publicReservationPermission) {
        if (role == null) {
            return false;
        }

        return switch (path) {
            case "/admin/dashboard", "/admin/logout", "/admin/login" -> true;
            case "/admin/departments" -> role == AdminRole.SYSTEM_ADMIN
                    || role == AdminRole.SCHOOL_ADMIN
                    || role == AdminRole.DEPARTMENT_ADMIN;
            case "/admin/users" -> role == AdminRole.SYSTEM_ADMIN || role == AdminRole.SCHOOL_ADMIN;
            case "/admin/logs" -> role == AdminRole.SYSTEM_ADMIN
                    || role == AdminRole.SCHOOL_ADMIN
                    || role == AdminRole.AUDIT_ADMIN;
            case "/admin/reservations" -> reservationType == ReservationType.OFFICIAL
                    ? role == AdminRole.SYSTEM_ADMIN
                    || role == AdminRole.SCHOOL_ADMIN
                    || role == AdminRole.DEPARTMENT_ADMIN
                    : canManagePublicReservations(role, publicReservationPermission);
            case "/admin/review" -> role == AdminRole.SYSTEM_ADMIN
                    || role == AdminRole.SCHOOL_ADMIN
                    || role == AdminRole.DEPARTMENT_ADMIN;
            default -> false;
        };
    }
}
