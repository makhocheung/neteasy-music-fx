package pub.cellebi.neteasyfx.modules.explore;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pub.cellebi.neteasyfx.utils.ImageUtil;

import java.util.Map;

public final class LatestMusicCell extends ListCell<Map<String, Object>> {

    private final ImageView imageView;
    private final Label name;
    private final Label artist;
    private final HBox contentPane;

    private boolean isInit;

    public LatestMusicCell() {
        imageView = new ImageView();
        imageView.setFitHeight(60);
        imageView.setFitWidth(60);
        name = new Label();
        artist = new Label();
        contentPane = new HBox();

        setPrefHeight(80);
    }

    @Override
    protected void updateItem(Map<String, Object> item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) {
            render();
            if (!isInit) {
                isInit = true;
                var image = ImageUtil.loadImage((String) item.get("picUrl"), 180);
                imageView.setImage(image);
                name.setText((String) item.get("name"));
                artist.setText((String) item.get("artists"));
                setGraphic(contentPane);
            }
        } else {
            setGraphic(null);
        }
    }

    private void render() {
        if (contentPane.getChildren().isEmpty()) {
            var vBox = new VBox();
            vBox.getChildren().addAll(name, artist);
            vBox.setAlignment(Pos.CENTER_LEFT);
            vBox.setSpacing(5);
            contentPane.getChildren().addAll(imageView, vBox);
            contentPane.setAlignment(Pos.CENTER_LEFT);
            contentPane.setSpacing(10);
        }
    }
}
