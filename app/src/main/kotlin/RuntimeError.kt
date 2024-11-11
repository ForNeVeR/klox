// SPDX-FileCopyrightText: 2024 Friedrich von Never <friedrich@fornever.me>
//
// SPDX-License-Identifier: MIT

package me.fornever.klox

class RuntimeError(internal val token: Token, message: String) : RuntimeException(message)
