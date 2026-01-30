<!--
SPDX-FileCopyrightText: 2024 Friedrich von Never <friedrich@fornever.me>

SPDX-License-Identifier: MIT
-->

klox [![Status Zero][status-zero]][andivionian-status-classifier]
====
Klox is an interpreter for Lox, a programming language described in [Crafting Interpreters][books.crafting-interpreters].

Running
-------
To run the Read-Eval-Print Loop (REPL):
```console
$ ./gradlew run --console=plain
```
Terminate the REPL with `Ctrl+C`.

To run a script from a file:
```console
$ ./gradlew run --args "file.lox"
```

Documentation
-------------
- [Contributor Guide][docs.contributing]

License
-------
This project is distributed under the terms of the [MIT License][docs.license], unless a particular file states otherwise.

The license information in the project's sources complies with the [REUSE Specification v3.3][reuse.spec].

[andivionian-status-classifier]: https://andivionian.fornever.me/v1/#status-zero-
[books.crafting-interpreters]: https://craftinginterpreters.com/
[docs.contributing]: CONTRIBUTING.md
[docs.license]: LICENSES/MIT.txt
[reuse.spec]: https://reuse.software/spec-3.3/
[status-zero]: https://img.shields.io/badge/status-zero-lightgrey.svg
