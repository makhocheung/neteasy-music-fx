package pub.cellebi.neteasyfx.modules;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public final class Line extends Pane {

    public static Line HLine() {
        var line = new Line();
        line.setPrefHeight(.25);
        line.setBackground(new Background(new BackgroundFill(Color.rgb(215, 215, 215), null, null)));
        return line;
    }

    public static Line VLine() {
        var line = new Line();
        line.setPrefWidth(.25);
        line.setBackground(new Background(new BackgroundFill(Color.web("#dddde1"), null, null)));
        return line;
    }
}
