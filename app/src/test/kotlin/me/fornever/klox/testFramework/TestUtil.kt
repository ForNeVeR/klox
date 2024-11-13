// SPDX-FileCopyrightText: 2024 Friedrich von Never <friedrich@fornever.me>
//
// SPDX-License-Identifier: MIT

package me.fornever.klox.testFramework

import me.fornever.klox.Lox
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets

data class SideEffects(val stdErr: String, val hadError: Boolean, val hadRuntimeError: Boolean)

fun doTestWithStdErr(action: () -> Unit): SideEffects {
    val output = ByteArrayOutputStream()
    PrintStream(output, true, StandardCharsets.UTF_8).use { ps ->
        val previousOutput = System.err
        System.setErr(ps)
        try {
            action()
            val error = output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n")
            return SideEffects(error, Lox.hadError, Lox.hadRuntimeError)
        } finally {
            Lox.hadError = false
            System.setErr(previousOutput)
        }
    }
}
