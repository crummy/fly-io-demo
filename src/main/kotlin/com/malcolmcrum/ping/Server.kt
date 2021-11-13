package com.malcolmcrum.ping

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import org.slf4j.event.Level
import java.net.Inet6Address
import java.net.InetAddress
import java.time.Duration
import java.time.Instant


fun main() {
    embeddedServer(Netty, port = 8080) {
        install(XForwardedHeaderSupport)
        install(CallLogging) {
            level = Level.INFO
        }
        routing {
            get("/") {
                val sourceIp = call.request.headers["x-forwarded-for"]?.split(", ")?.firstOrNull()
                if (sourceIp != null) {
                    val ms = ping(sourceIp)
                    call.respondText("I can ping you in $ms ms ($sourceIp)")
                } else {
                    call.respondText("I don't know what your IP is. Headers: ${call.request.headers.toMap()}")
                }
            }

        }
    }.start(wait = true)
}

fun ping(sourceIp: String): Long {
    println("pinging $sourceIp")
    val inetAddress = if (sourceIp.contains(":")) Inet6Address.getByName(sourceIp) else InetAddress.getByName(sourceIp)
    val start = Instant.now()
    inetAddress.isReachable(5000)
    val duration = Duration.between(start, Instant.now())
    return duration.toMillis()
}

