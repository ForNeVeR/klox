<!--
SPDX-FileCopyrightText: 2024 klox contributors <https://github.com/ForNeVeR/klox>

SPDX-License-Identifier: MIT
-->

Contributor Guide
=================
<!-- REUSE-IgnoreStart -->

Build
-----

### Prerequisites
- OpenJDK-compatible JDK version 21 or later

### Build
To build the application, execute this shell command:
```console
$ ./gradlew build
```

### Run
To build the application, execute this shell command:
```console
$ ./gradlew run
```

License Automation
------------------
If the CI asks you to update the file licenses, follow one of these:
1. Update the headers manually (look at the existing files), something like this:
   ```csharp
   // SPDX-FileCopyrightText: %year% %your name% <%your contact info, e.g. email%>
   //
   // SPDX-License-Identifier: MIT
   ```
   (accommodate to the file's comment style if required).
2. Alternately, use the [REUSE][reuse] tool:
   ```console
   $ reuse annotate --license MIT --copyright '%your name% <%your contact info, e.g. email%>' %file names to annotate%
   ```

(Feel free to attribute the changes to "klox contributors <https://github.com/ForNeVeR/klox>" instead of your name in a multi-author file, or if you don't want your name to be mentioned in the project's source: this doesn't mean you'll lose the copyright.)

[reuse]: https://reuse.software/

<!-- REUSE-IgnoreEnd -->
