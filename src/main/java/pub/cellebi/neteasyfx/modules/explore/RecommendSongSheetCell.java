package pub.cellebi.neteasyfx.modules.explore;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import pub.cellebi.neteasyfx.service.NetService;
import pub.cellebi.neteasyfx.utils.ImageUtil;

import java.util.Map;

import static pub.cellebi.neteasyfx.modules.main.NavigationPane.NAVIGATION_PANE;
import static pub.cellebi.neteasyfx.utils.Util.MAPPER;

public final class RecommendSongSheetCell extends VBox {

    private final ImageView songSheetImage;
    private final Label nameLabel;

    private final Map<String, Object> data;

    public RecommendSongSheetCell(Map<String, Object> data) {
        songSheetImage = new ImageView();
        nameLabel = new Label();

        this.data = data;

        render();
        initState();
        registerListener();
    }

    private void render() {
        songSheetImage.setFitHeight(140);
        songSheetImage.setFitWidth(140);
        getChildren().addAll(songSheetImage, nameLabel);
        setSpacing(5);
        getStyleClass().add("meta-cell");
    }

    @SuppressWarnings("unchecked")
    private void registerListener() {
        setOnMouseClicked(e -> NetService.get("http://cellebi.pub:3000/playlist/detail?id=" + data.get("id"), json -> {
            var playList = (Map<String, Object>) MAPPER.readValue(json, Map.class).get("playlist");
            Platform.runLater(() -> {
                var songSheetPane = new SongSheetMainPane(playList);
                NAVIGATION_PANE.navigate(songSheetPane);
            });
        }));
    }

    private void initState() {
        var image = ImageUtil.loadImage((String) data.get("picUrl"), 280);
        songSheetImage.setImage(image);
        var name = (String) data.get("name");
        nameLabel.setText(name.length() > 9 ? name.substring(0, 10) + "..." : name);
    }
}
