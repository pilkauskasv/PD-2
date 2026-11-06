package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * JVM Unit testai temų konstantoms ir tipografijai padengti.
 */
class ThemeTest {

    @Test
    fun typography_bodyLarge_isDefined() {
        // Arrange & Act
        val typography = Typography

        // Assert
        assertNotNull(typography.bodyLarge)
        assertNotNull(typography.bodyLarge.fontFamily)
    }

    @Test
    fun colors_lightAndDarkTokens_areDefinedCorrectly() {
        // Assert - Light theme colors
        assertEquals(Color(0xFF1E40AF), BluePrimaryLight)
        assertEquals(Color(0xFFFFFFFF), BlueOnPrimaryLight)
        assertEquals(Color(0xFFDBEAFE), BluePrimaryContainerLight)
        assertEquals(Color(0xFF1E3A8A), BlueOnPrimaryContainerLight)
        assertEquals(Color(0xFF0284C7), BlueSecondaryLight)
        assertEquals(Color(0xFF4338CA), BlueTertiaryLight)
        assertEquals(Color(0xFFF8FAFC), NeutralBackgroundLight)
        assertEquals(Color(0xFFFFFFFF), NeutralSurfaceLight)
        assertEquals(Color(0xFFF1F5F9), NeutralSurfaceVariantLight)

        // Assert - Dark theme colors
        assertEquals(Color(0xFF93C5FD), BluePrimaryDark)
        assertEquals(Color(0xFF172554), BlueOnPrimaryDark)
        assertEquals(Color(0xFF1E3A8A), BluePrimaryContainerDark)
        assertEquals(Color(0xFFDBEAFE), BlueOnPrimaryContainerDark)
        assertEquals(Color(0xFF38BDF8), BlueSecondaryDark)
        assertEquals(Color(0xFFA5B4FC), BlueTertiaryDark)
        assertEquals(Color(0xFF0F172A), NeutralBackgroundDark)
        assertEquals(Color(0xFF1E293B), NeutralSurfaceDark)
        assertEquals(Color(0xFF334155), NeutralSurfaceVariantDark)
    }
}
