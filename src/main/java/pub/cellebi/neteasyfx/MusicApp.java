package pub.cellebi.neteasyfx;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import pub.cellebi.neteasyfx.modules.main.MainPane;
import pub.cellebi.neteasyfx.state.Jux;
import pub.cellebi.neteasyfx.utils.ImageUtil;
import pub.cellebi.neteasyfx.utils.LogUtil;
import pub.cellebi.neteasyfx.utils.Util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class MusicApp extends Application {

    public static final Path HOME = Path.of(System.getProperty("user.home")).resolve(".neteasy-music-fx");

    @Override
    public void init() throws Exception {
        if (Files.notExists(HOME)) {
            Files.createDirectory(HOME);
        }
        LogUtil.getLogger().info("Start up application");
        var historyFile = HOME.resolve("history.json").toFile();
        if (historyFile.exists() && historyFile.length() > 0) {
            var list = Util.MAPPER.readValue(historyFile, new TypeReference<List<String>>() {
            });
            Jux.SEARCH_STATE.historySearch.addAll(list);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Cellebi Music");
        primaryStage.getIcons().add(new Image(MusicApp.class.getResource("logo.png").toExternalForm()));
        primaryStage.setMinWidth(1100);
        var scene = new Scene(new MainPane(), 1100, 700);
        scene.getStylesheets().add(this.getClass().getResource("music-app.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> ImageUtil.EXECUTOR_SERVICE.shutdown());
        var circle = new Circle(2, Color.web("#ec0202"));
        var thumb = (StackPane) scene.lookup(".player-pane .thumb");
        thumb.setEffect(null);
        thumb.getChildren().add(circle);
        var track = (StackPane) scene.lookup(".player-pane .track");
    }

    @Override
    public void stop() throws Exception {
        var array = Jux.SEARCH_STATE.historySearch.toArray(new String[0]);
        if (array.length > 0) {
            var historyFile = HOME.resolve("history.json").toFile();
            Util.MAPPER.writeValue(historyFile, array);
        }
        LogUtil.getLogger().info("Shut down application");
    }
}
