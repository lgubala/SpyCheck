package com.example.spycheck.services.tracking

import java.nio.ByteBuffer

data class UdpHeader(
    val sourcePort: Int,
    val destinationPort: Int,
    val length: Int,
    val checksum: Int
) {
    companion object {
        fun from(buffer: ByteBuffer): UdpHeader? {
            if (buffer.remaining() < 8) {
                return null
            }
            val sourcePort = buffer.short.toInt() and 0xFFFF
            val destinationPort = buffer.short.toInt() and 0xFFFF
            val length = buffer.short.toInt() and 0xFFFF
            val checksum = buffer.short.toInt() and 0xFFFF
            return UdpHeader(sourcePort, destinationPort, length, checksum)
        }
    }
}
