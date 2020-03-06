package pub.cellebi.neteasyfx.modules.explore;

import javafx.application.Platform;
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

public final class ExclusiveBroadcastCell extends VBox {

    private final ImageView imageView;
    private final Label nameLabel;

    private final Map<String, Object> data;

    public ExclusiveBroadcastCell(Map<String, Object> data) {
        imageView = new ImageView();
        nameLabel = new Label();
        this.data = data;

        render();
        registerListener();
        initState();
    }

    public void render() {
        imageView.fitWidthProperty().bind(widthProperty().multiply(0.9));
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        setSpacing(5);
        getChildren().addAll(imageView, nameLabel);
        getStyleClass().add("meta-cell");
    }

    @SuppressWarnings("unchecked")
    public void registerListener() {
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

    public void initState() {
        var image = ImageUtil.loadImage((String) data.get("picUrl"), 720);
        imageView.setImage(image);
        var name = (String) data.get("name");
        nameLabel.setText(name.length() > 9 ? name.substring(0, 10) + "..." : name);
    }
}
