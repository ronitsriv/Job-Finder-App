module com.example.demojobfinder {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;


    opens com.example.demojobfinder to javafx.fxml;
    exports com.example.demojobfinder;
}