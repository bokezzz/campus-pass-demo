package org.example.demo.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilTest {
    @Test
    void passwordComplexityRequiresMixedCharactersAndLength() {
        assertFalse(SecurityUtil.isStrongPassword("abc123"));
        assertFalse(SecurityUtil.isStrongPassword("abcdefgh"));
        assertFalse(SecurityUtil.isStrongPassword("ABCDEFGH1"));
        assertTrue(SecurityUtil.isStrongPassword("Campus@1234"));
    }

    @Test
    void sm3HashIsStableAndHexEncoded() {
        String first = SecurityUtil.sm3("Campus@1234");
        String second = SecurityUtil.sm3("Campus@1234");

        assertEquals(first, second);
        assertEquals(64, first.length());
        assertTrue(first.matches("[0-9a-f]+"));
        assertNotEquals(first, SecurityUtil.sm3("Campus@12345"));
    }

    @Test
    void masksSensitiveVisitorInformation() {
        assertEquals("张*", SecurityUtil.maskName("张三"));
        assertEquals("王**", SecurityUtil.maskName("王小明"));
        assertEquals("330************011", SecurityUtil.maskIdentityNo("330102199901010011"));
        assertEquals("138****5678", SecurityUtil.maskPhone("13812345678"));
    }
}
