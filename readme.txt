This project uses gradle with a multi-module setup.
The module application/core contains the actual logic,
The module application/terminal is a (very thin) wrapper to use that logic in a terminal application.

This project contains a gradle wrapper, so you do not need to install gradle yourself to run this application.
Do note that the gradle configuration builds for java 15.
If you do not have java 15, you can adjust the java version in the build.gradle file in the root directory (adjust sourceCompatibility/targetCompatibility to the desired versions).
Note that for versions up to java 10, you must use VERSION_1_10 instead of VERSION_10.

---

To run all the tests, execute the following command in the root of this project
gradlew clean test

---

To build the application, execute the following command in the root of this project
gradlew clean shadowJar

This will result in a jar in application/terminal/build/libs you can use.

---

Use the jar as follows to read a log file named server.log in the current directory and write the output to an output file named output.xml in the current directory:
java -jar terminal-1.0-SNAPSHOT-all.jar ./server.log ./output.xml