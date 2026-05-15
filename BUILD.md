### Windows Build Steps

1. Ensure `JAVA_HOME` points to a JDK 21 installation.
2. Ensure Maven `mvn` is available on PATH
3. Run `mvn clean package`

This single command compiles, runs tests, copies dependencies, and runs `jpackage` (which internally invokes `jlink` to create a minimal JRE with the required modules). The output application image is created at `dist/FTL Autosave/`. Any previous output is automatically removed before rebuilding.
