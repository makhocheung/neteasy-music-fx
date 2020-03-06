package pub.cellebi.neteasyfx.modules.explore;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import pub.cellebi.neteasyfx.service.NetService;
import pub.cellebi.neteasyfx.modules.main.MVMainPane;
import pub.cellebi.neteasyfx.utils.ImageUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static pub.cellebi.neteasyfx.modules.main.MainPane.MAIN;
import static pub.cellebi.neteasyfx.modules.main.NavigationPane.NAVIGATION_PANE;
import static pub.cellebi.neteasyfx.utils.Util.MAPPER;

public final class RecommendMVCell extends VBox {

    private final ImageView imageView;
    private final Label nameLabel;
    private final Label artistsLabel;

    private final Map<String, Object> data;

    public RecommendMVCell(Map<String, Object> data) {
        imageView = new ImageView();
        nameLabel = new Label();
        artistsLabel = new Label();
        this.data = data;
        render();
        registerListener();
        initState();
    }

    public void render() {
        imageView.fitWidthProperty().bind(widthProperty().multiply(0.9));
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        getChildren().addAll(imageView, nameLabel, artistsLabel);
        setSpacing(5);
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

    @SuppressWarnings("unchecked")
    public void initState() {
        var image = ImageUtil.loadImage((String) data.get("picUrl"), 720);
        imageView.setImage(image);
        var name = (String) data.get("name");
        nameLabel.setText(name.length() > 9 ? name.substring(0, 10) + "..." : name);
        var artists = (List<Map<String, Object>>) data.get("artists");
        var artistsName = artists.stream().map(m -> (String) m.get("name")).collect(Collectors.joining("/"));
        artistsLabel.setText(artistsName);
    }
}
