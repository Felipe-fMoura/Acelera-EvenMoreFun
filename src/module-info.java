module evenMoreFun{
    requires javafx.controls;
    requires javafx.fxml;
    requires jasypt;

    opens application to javafx.graphics, javafx.fxml;
    opens view to javafx.fxml;

    exports application;
    exports view;
}
