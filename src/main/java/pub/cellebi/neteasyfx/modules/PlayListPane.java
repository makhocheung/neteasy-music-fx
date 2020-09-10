package pub.cellebi.neteasyfx.modules;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.PopupWindow;
import pub.cellebi.neteasyfx.Song;
import pub.cellebi.neteasyfx.state.Jux;

public final class PlayListPane extends PopupWindow {

    private final ListView<Song> listView;
    private final VBox root;
    private final Button close;

    public PlayListPane() {
        root = new VBox();
        listView = new ListView<>();
        close = new Button();
        render();
        dataBind();
        registerListener();
    }

    private void render() {
        var closeSvg = new SVGPath();
        closeSvg.setContent("M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z");
        closeSvg.setFill(Color.GRAY);
        close.setGraphic(closeSvg);
        close.setBackground(Background.EMPTY);
        root.setPrefSize(400, 500);
        root.getChildren().addAll(header(), listView);
        root.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        VBox.setVgrow(listView, Priority.ALWAYS);
        root.getStyleClass().add("playlist-pane");
        setAutoHide(true);
        getScene().setRoot(root);
    }

    private void dataBind() {
        listView.setItems(Jux.SONG_LIST_STATE.songs);
        listView.setCellFactory(t -> new PlayListCell());
    }

    private void registerListener() {
        listView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                var song = listView.getSelectionModel().getSelectedItem();
                Jux.PLAYER_STATE.update(song);
            }
        });
        close.setOnAction(e -> hide());
    }

    private HBox header() {
        var hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setPadding(new Insets(4));
        var label = new Label("播放列表");
        var spacer = Spacer.HSpacer(0);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        hbox.getChildren().addAll(label, spacer, close);
        return hbox;
    }

    public static class PlayListCell extends ListCell<Song> {
        @Override
        protected void updateItem(Song item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) {
                setText(item.name);
            } else {
                setText(null);
                setGraphic(null);
            }
        }
    }
}
