package pub.cellebi.neteasyfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import pub.cellebi.neteasyfx.components.Banner;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        var root = new AnchorPane();
        var banner = new Banner();
        root.getChildren().add(banner);
        var scene = new Scene(root, 640, 480);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
