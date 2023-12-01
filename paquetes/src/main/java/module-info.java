module com.paquetes {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    opens com.paquetes to javafx.fxml;
    exports com.paquetes;
}
