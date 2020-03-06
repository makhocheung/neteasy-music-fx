package pub.cellebi.neteasyfx.modules;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.PopupWindow;

public final class VolumePane extends PopupWindow {
    public final Slider volumeControl;

    public VolumePane(double width, double height) {
        volumeControl = new Slider();
        render(width, height);
        registerListener();
    }

    private void render(double width, double height) {
        volumeControl.getStyleClass().add("player-head");
        var label = new Label();
        var svgPath = new SVGPath();
        svgPath.setContent("M12.79 9c0-1.3-.72-2.42-1.79-3v6c1.06-.58 1.79-1.7 1.79-3zM2 7v4h3l4 4V3L5 7H2zm9-5v1.5c2.32.74 4 2.93 4 5.5s-1.68 4.76-4 5.5V16c3.15-.78 5.5-3.6 5.5-7S14.15 2.78 11 2z");
        svgPath.setFill(Color.rgb(179, 179, 179));
        label.setGraphic(svgPath);
        var content = new HBox();
        content.setPrefSize(width, height);
        content.getStyleClass().add("volume-pane");
        content.getChildren().addAll(label, volumeControl);
        HBox.setHgrow(volumeControl, Priority.ALWAYS);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(0, 10, 0, 10));
        content.setSpacing(7);
        setAutoHide(true);
        getScene().setRoot(content);
    }

    private void registerListener() {
        volumeControl.valueProperty().addListener((o, ov, nv) -> {
            String style = String.format("-fx-background-color: linear-gradient(to right, #ec0202 %d%%, #e5e5e5 %d%%);",
                    nv.intValue(), nv.intValue());
            volumeControl.lookup(".track").setStyle(style);
        });
    }
}
