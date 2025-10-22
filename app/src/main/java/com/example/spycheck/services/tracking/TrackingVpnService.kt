package com.example.spycheck.services.tracking

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.spycheck.db.tracking.TrackingEvent
import com.example.spycheck.db.tracking.TrackingDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import kotlin.concurrent.thread

class TrackingVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var vpnThread: Thread? = null

    private val db by lazy { TrackingDatabase.getDatabase(this) }
    private val scope = CoroutineScope(Dispatchers.IO)
    private val connectivityManager by lazy { getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }

    // Placeholder for tracking domain manager
    private val trackingDomainManager = object {
        fun isTrackingDomain(domain: String): Boolean = true
    }

    companion object {
        const val ACTION_STOP_VPN = "com.example.spycheck.STOP_VPN"
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TrackingVPN", "onStartCommand called with action: ${intent?.action}")

        // Check if we're being asked to stop
        if (intent?.action == ACTION_STOP_VPN) {
            Log.d("TrackingVPN", "Received STOP command")

            // CRITICAL: Stop VPN first (this stops the thread)
            stopVpn()

            // Update state
            VpnStateManager.setVpnRunning(false)

            // Stop foreground notification
            stopForeground(true)

            // Finally stop the service
            stopSelf()

            Log.d("TrackingVPN", "Stop sequence completed")
            return START_NOT_STICKY
        }

        Log.d("TrackingVPN", "VPN service starting...")
        startForeground(1, createNotification())
        startVpn()
        VpnStateManager.setVpnRunning(true)
        return START_NOT_STICKY
    }




    override fun onDestroy() {
        super.onDestroy()
        stopVpn()
        VpnStateManager.setVpnRunning(false)
        Log.d("TrackingVPN", "✅ VPN SERVICE DESTROYED")
    }

    private fun createNotification(): Notification {
        val channelId = "vpn_service_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "VPN Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("TrackingWatch Active")
            .setContentText("Monitoring network traffic")
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun startVpn() {
        try {
            val builder = Builder()
            builder.setSession("TrackingWatch")
            builder.addAddress("10.0.0.1", 32)
            builder.addRoute("8.8.8.8", 32)
            builder.addRoute("8.8.4.4", 32)
            builder.addDnsServer("8.8.8.8")
            builder.addDnsServer("8.8.4.4")

            vpnInterface = builder.establish()

            if (vpnInterface == null) {
                Log.e("TrackingVPN", "Failed to establish VPN")
                return
            }

            vpnThread = thread {
                handlePackets()
            }

            Log.d("TrackingVPN", "✓ VPN started")

        } catch (e: Exception) {
            Log.e("TrackingVPN", "Error starting VPN", e)
        }
    }

    private fun handlePackets() {
        val input = FileInputStream(vpnInterface?.fileDescriptor)
        val output = FileOutputStream(vpnInterface?.fileDescriptor)
        val buffer = ByteBuffer.allocate(32767)

        while (!Thread.interrupted()) {
            try {
                val length = input.read(buffer.array())
                if (length > 0) {
                    buffer.limit(length)

                    val packet = ByteArray(length)
                    buffer.position(0)
                    buffer.get(packet)
                    buffer.position(0)

                    handlePacketAsync(packet, output)

                    buffer.clear()
                }
            } catch (e: Exception) {
                if (!Thread.interrupted()) {
                    Log.e("TrackingVPN", "Read error: ${e.message}")
                }
                break
            }
        }
    }

    private fun handlePacketAsync(packet: ByteArray, output: FileOutputStream) {
        try {
            if (packet.size < 28) {
                output.write(packet)
                return
            }

            val protocol = packet[9].toInt() and 0xFF
            if (protocol != 17) {
                output.write(packet)
                return
            }

            val ihl = (packet[0].toInt() and 0x0F) * 4
            if (packet.size < ihl + 8) {
                output.write(packet)
                return
            }

            val destPort = ((packet[ihl + 2].toInt() and 0xFF) shl 8) or
                    (packet[ihl + 3].toInt() and 0xFF)

            if (destPort != 53) {
                output.write(packet)
                return
            }

            val srcPort = ((packet[ihl].toInt() and 0xFF) shl 8) or
                    (packet[ihl + 1].toInt() and 0xFF)

            val srcIp = packet.copyOfRange(12, 16)
            val dstIp = packet.copyOfRange(16, 20)
            val udpLen = ((packet[ihl + 4].toInt() and 0xFF) shl 8) or
                    (packet[ihl + 5].toInt() and 0xFF)

            val dnsStart = ihl + 8
            val dnsLen = udpLen - 8
            if (packet.size < dnsStart + dnsLen) {
                output.write(packet)
                return
            }

            val dns = packet.copyOfRange(dnsStart, dnsStart + dnsLen)

            // Forward DNS immediately (non-blocking)
            forwardDnsAsync(dns, srcIp, srcPort, dstIp, output)

            // Analyze in background
            analyzeInBackground(dns, srcPort)

        } catch (e: Exception) {
            Log.e("TrackingVPN", "Packet handling error", e)
            output.write(packet)
        }
    }

    private fun forwardDnsAsync(
        dns: ByteArray,
        srcIp: ByteArray,
        srcPort: Int,
        dstIp: ByteArray,
        output: FileOutputStream
    ) {
        thread(start = true, isDaemon = true) {
            var sock: DatagramSocket? = null
            try {
                sock = DatagramSocket()
                protect(sock)

                val server = InetAddress.getByName("8.8.8.8")
                val query = DatagramPacket(dns, dns.size, server, 53)
                sock.send(query)

                val respBuf = ByteArray(512)
                val resp = DatagramPacket(respBuf, respBuf.size)
                sock.soTimeout = 3000
                sock.receive(resp)

                val answer = buildResponse(srcIp, srcPort, dstIp, respBuf, resp.length)

                synchronized(output) {
                    output.write(answer)
                }

            } catch (e: Exception) {
                // Silently fail - network errors are normal
            } finally {
                sock?.close()
            }
        }
    }

    private fun analyzeInBackground(dns: ByteArray, sourcePort: Int) {
        scope.launch {
            val domain = parseDomainName(dns)
            if (domain != null && trackingDomainManager.isTrackingDomain(domain)) {
                val (pkg, appName) = getAppInfo(sourcePort)


                val category = TrackingDomainCategorizer.categorize(domain)

                val event = TrackingEvent(
                    packageName = pkg,
                    appName = appName,
                    domain = domain,
                    timestamp = System.currentTimeMillis(),
                    category = category.name
                )

                db.trackingEventDao().insert(event)
                TrackingDataHolder.updateLatestEvent(event)
            }
        }
    }

    /**
     * SIMPLIFIED & HONEST: Single reliable attempt, no guessing
     * Focus on tracking domains (who's tracking) rather than source apps
     */
    private fun getAppInfo(sourcePort: Int): Pair<String, String> {
        // Single attempt with Android Q+ API (most reliable when available)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val uid = connectivityManager.getConnectionOwnerUid(
                    17, // UDP
                    InetSocketAddress("10.0.0.1", sourcePort),
                    InetSocketAddress("8.8.8.8", 53)
                )

                if (uid > 0) {
                    val (pkg, name) = getAppFromUid(uid)
                    // Only return if we got a real app (not system/unknown)
                    if (pkg != "system" && !pkg.contains("google.android.gms")) {
                        return Pair(pkg, name)
                    }
                }
            } catch (e: Exception) {
                // Expected to fail on many devices - that's okay
            }
        }

        // If we can't reliably determine the app, be honest about it
        return Pair("system", "System Service")
    }

    /**
     * SIMPLIFIED: Clean UID to app mapping
     */
    private fun getAppFromUid(uid: Int): Pair<String, String> {
        if (uid <= 0 || uid < 10000) {
            return Pair("system", "System Service")
        }

        return try {
            val packages = packageManager.getPackagesForUid(uid)
            if (!packages.isNullOrEmpty()) {
                val pkg = packages[0]
                val appInfo = packageManager.getApplicationInfo(pkg, 0)
                val label = packageManager.getApplicationLabel(appInfo).toString()
                Pair(pkg, label)
            } else {
                Pair("system", "System Service")
            }
        } catch (e: Exception) {
            Pair("system", "System Service")
        }
    }

    private fun parseDomainName(dns: ByteArray): String? {
        try {
            if (dns.size < 13) return null

            var pos = 12
            val name = StringBuilder()

            while (pos < dns.size) {
                val len = dns[pos].toInt() and 0xFF
                if (len == 0) break
                if (len and 0xC0 == 0xC0) break

                pos++
                if (pos + len > dns.size) break

                if (name.isNotEmpty()) name.append('.')

                for (i in 0 until len) {
                    name.append(dns[pos++].toInt().toChar())
                }
            }

            return if (name.isNotEmpty()) name.toString() else null
        } catch (e: Exception) {
            return null
        }
    }

    private fun buildResponse(
        srcIp: ByteArray,
        srcPort: Int,
        dstIp: ByteArray,
        dns: ByteArray,
        dnsLen: Int
    ): ByteArray {
        val total = 28 + dnsLen
        val pkt = ByteArray(total)

        pkt[0] = 0x45.toByte()
        pkt[1] = 0
        pkt[2] = (total shr 8).toByte()
        pkt[3] = (total and 0xFF).toByte()
        pkt[4] = 0
        pkt[5] = 0
        pkt[6] = 0x40.toByte()
        pkt[7] = 0
        pkt[8] = 64
        pkt[9] = 17
        pkt[10] = 0
        pkt[11] = 0

        System.arraycopy(dstIp, 0, pkt, 12, 4)
        System.arraycopy(srcIp, 0, pkt, 16, 4)

        var sum = 0
        for (i in 0 until 20 step 2) {
            sum += ((pkt[i].toInt() and 0xFF) shl 8) or (pkt[i + 1].toInt() and 0xFF)
        }
        sum = (sum shr 16) + (sum and 0xFFFF)
        sum += sum shr 16
        val checksum = sum.inv() and 0xFFFF
        pkt[10] = (checksum shr 8).toByte()
        pkt[11] = (checksum and 0xFF).toByte()

        pkt[20] = (53 shr 8).toByte()
        pkt[21] = (53 and 0xFF).toByte()
        pkt[22] = (srcPort shr 8).toByte()
        pkt[23] = (srcPort and 0xFF).toByte()
        val udpLen = 8 + dnsLen
        pkt[24] = (udpLen shr 8).toByte()
        pkt[25] = (udpLen and 0xFF).toByte()
        pkt[26] = 0
        pkt[27] = 0

        System.arraycopy(dns, 0, pkt, 28, dnsLen)

        return pkt
    }

    private fun stopVpn() {
        Log.d("TrackingVPN", "stopVpn() called")

        // Interrupt the thread first
        vpnThread?.interrupt()
        Log.d("TrackingVPN", "VPN thread interrupted")

        // Close the VPN interface
        vpnInterface?.close()
        Log.d("TrackingVPN", "VPN interface closed")

        // Clear references
        vpnThread = null
        vpnInterface = null

        Log.d("TrackingVPN", "VPN stopped successfully")
    }
}