package pub.cellebi.neteasyfx.modules.mv;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import pub.cellebi.neteasyfx.service.NetService;
import pub.cellebi.neteasyfx.modules.main.MVMainPane;
import pub.cellebi.neteasyfx.utils.ImageUtil;

import java.util.Map;

import static pub.cellebi.neteasyfx.modules.main.MainPane.MAIN;
import static pub.cellebi.neteasyfx.modules.main.NavigationPane.NAVIGATION_PANE;
import static pub.cellebi.neteasyfx.utils.Util.MAPPER;

public final class MVCell extends VBox {

    private final ImageView imageView;
    private final Label nameLabel;
    private final Label artistsLabel;

    private final Map<String, Object> data;

    public MVCell(Map<String, Object> data) {
        imageView = new ImageView();
        nameLabel = new Label();
        artistsLabel = new Label();

        this.data = data;

        render();
        registerListener();
        initState();
    }

    private void render() {
        imageView.fitWidthProperty().bind(widthProperty().multiply(0.9));
        imageView.setSmooth(true);
        imageView.setPreserveRatio(true);

        getChildren().addAll(imageView, nameLabel, artistsLabel);
        setSpacing(5);
        setAlignment(Pos.CENTER);
        setMaxWidth(360);
        getStyleClass().add("meta-cell");
    }

    private void initState() {
        var image = ImageUtil.loadImage((String) data.get("cover"), 720);
        imageView.setImage(image);
        var name = (String) data.get("name");
        nameLabel.setText(name.length() > 20 ? name.substring(0, 10) + "..." : name);
        artistsLabel.setText((String) data.get("artistName"));
    }

    @SuppressWarnings("unchecked")
    private void registerListener() {
        setOnMouseClicked(e -> {
            NetService.get("http://cellebi.pub:3000/mv/detail?mvid=" + data.get("id"), json -> {
                var map = (Map<String, Object>) MAPPER.readValue(json, Map.class).get("data");
                Platform.runLater(() -> {
                    var mvMainPane = new MVMainPane(map);
                    NAVIGATION_PANE.navigate(mvMainPane);
                    MAIN.getLeft().setVisible(false);
                    MAIN.getLeft().setManaged(false);
                });
            });
        });
    }
}
