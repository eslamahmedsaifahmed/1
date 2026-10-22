package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.network.MikroTikParser
import com.example.data.util.NetworkUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("el.doctor", appName)
    }

    @Test
    fun `parse MikroTik status sample html`() {
        val sampleHtml = """
            <html>
            <head><title>mikrotik hotspot > status</title></head>
            <body>
            <table border="1">
                <tr><td>IP address:</td><td>10.0.0.55</td></tr>
                <tr><td>bytes up/down:</td><td>450.0 MiB / 3.2 GiB</td></tr>
                <tr><td>connected / uptime:</td><td>2h15m</td></tr>
                <tr><td>status / refresh:</td><td>10m</td></tr>
            </table>
            <form action="http://10.0.0.1/logout" name="logout">
                <input type="submit" value="log off">
            </form>
            </body>
            </html>
        """.trimIndent()

        val status = MikroTikParser.parseStatusHtml(sampleHtml)
        assertEquals("10.0.0.55", status.ipAddress)
        assertEquals("2h15m", status.uptime)
        assertEquals("3.2 GiB", status.bytesIn)
        assertEquals("450.0 MiB", status.bytesOut)
        assertTrue(status.isLoggedIn)
    }

    @Test
    fun `parse byte strings and formatting`() {
        val bytes = NetworkUtils.parseByteStringToLong("3.5 GiB")
        assertTrue(bytes > 3_000_000_000L)
        val arabicFormatted = NetworkUtils.formatBytesToArabic(bytes)
        assertTrue(arabicFormatted.contains("جيجابايت"))
    }
}
