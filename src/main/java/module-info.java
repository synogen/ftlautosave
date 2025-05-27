module org.synogen.ftlautosave {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires java.logging;
    requires static lombok;

    opens org.synogen.ftlautosave to javafx.graphics;
    opens org.synogen.ftlautosave.ui to javafx.fxml;
    exports org.synogen.ftlautosave;
}
