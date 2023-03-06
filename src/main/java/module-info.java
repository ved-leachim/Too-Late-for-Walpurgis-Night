module ch.ffhs.swea.town.town {
    requires javafx.controls;
    requires javafx.fxml;


    opens ch.ffhs.swea.town.client to javafx.fxml;
    exports ch.ffhs.swea.town.client;
}