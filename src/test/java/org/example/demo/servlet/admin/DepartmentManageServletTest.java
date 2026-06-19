package org.example.demo.servlet.admin;

import org.junit.jupiter.api.Test;

import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DepartmentManageServletTest {
    @Test
    void identifiesDuplicateDepartmentCodeConstraint() {
        assertTrue(DepartmentManageServlet.isDuplicateDepartmentCode(
                new SQLIntegrityConstraintViolationException("Duplicate entry 'D004' for key 'departments.code'", "23000", 1062)));
        assertFalse(DepartmentManageServlet.isDuplicateDepartmentCode(new SQLException("Connection failed")));
    }
}
