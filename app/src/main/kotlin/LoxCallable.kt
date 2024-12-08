// SPDX-FileCopyrightText: 2024 Friedrich von Never <friedrich@fornever.me>
//
// SPDX-License-Identifier: MIT

package me.fornever.klox

interface LoxCallable {
    fun call(interpreter: Interpreter, arguments: List<Any?>): Any?
}
