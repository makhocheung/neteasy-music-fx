package pub.cellebi.neteasyfx.modules;

import javafx.scene.layout.Pane;

public final class Spacer extends Pane {

    public static Pane HSpacer(int width) {
        var spacer = new Spacer();
        spacer.setPrefWidth(width);
        return spacer;
    }

    public static Pane VSpacer(int height) {
        var spacer = new Spacer();
        spacer.setPrefHeight(height);
        return spacer;
    }
}
