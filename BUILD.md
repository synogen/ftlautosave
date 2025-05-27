### Windows Build Steps

1. `mvn clean package`

2. `mvn dependency:copy-dependencies -DoutputDirectory=target/libs`

3. `jlink --add-modules java.base,java.logging,java.desktop,javafx.controls,javafx.graphics,javafx.base,javafx.fxml,java.xml --module-path "target/libs;../java/jdk-21.0.1.12-hotspot/jmods" --output target/jre`

4. `jpackage --input target --name "FTL Autosave" --main-jar "ftlautosave-1.0.0.jar" --main-class org.synogen.ftlautosave.App --runtime-image target/jre --app-version "1.0.0" --vendor "synogen" --icon src/main/resources/save-icon.ico --type app-image`