module evenMoreFun{
    requires javafx.controls;
    requires javafx.fxml;
    requires jasypt;
	requires javafx.graphics;
	requires jakarta.mail;
    requires jakarta.activation;
	requires jdk.httpserver;
    
    opens application to javafx.graphics, javafx.fxml;
    opens view to javafx.fxml;

    exports application;
    exports view;
}
