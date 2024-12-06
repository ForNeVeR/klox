// SPDX-FileCopyrightText: 2024 Friedrich von Never <friedrich@fornever.me>
//
// SPDX-License-Identifier: MIT

package me.fornever.klox.testFramework

import me.fornever.klox.Lox
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets

data class SideEffects(
    val stdOut: String,
    val stdErr: String,
    val hadError: Boolean,
    val hadRuntimeError: Boolean
)

fun doTestWithStdIo(action: () -> Unit): SideEffects {
    var hadError = false
    var hadRuntimeError = false
    lateinit var stdOut: String
    val stdErr = withStreamOverride(System.err, System::setErr) {
        stdOut = withStreamOverride(System.out, System::setOut) {
            try {
                action()
                hadError = Lox.hadError
                hadRuntimeError = Lox.hadRuntimeError
            } finally {
                Lox.hadError = false
                Lox.hadRuntimeError = false
            }
        }
    }

    return SideEffects(stdOut, stdErr, hadError, hadRuntimeError)
}

private fun withStreamOverride(
    previousStream: PrintStream,
    setter: (PrintStream) -> Unit,
    action: () -> Unit
): String {
    val output = ByteArrayOutputStream()
    PrintStream(output, true, StandardCharsets.UTF_8).use { ps ->
        setter(ps)
        try {
            action()
        } finally {
            setter(previousStream)
        }
    }

    return output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n")
}
