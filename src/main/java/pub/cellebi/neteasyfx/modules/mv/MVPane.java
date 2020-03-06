package pub.cellebi.neteasyfx.modules.mv;

import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import pub.cellebi.neteasyfx.modules.TitleBar;


public final class MVPane extends ScrollPane {

    private final TitleBar titleBar;
    private final VBox contentBox;
    private final LatestMVPane latestMVPane;
    private final NeteasyMVPane neteasyMVPane;

    public MVPane() {
        titleBar = new TitleBar("MV");
        contentBox = new VBox();
        latestMVPane = new LatestMVPane();
        neteasyMVPane = new NeteasyMVPane();

        render();
    }

    private void render() {

        contentBox.setSpacing(30);
        contentBox.setAlignment(Pos.CENTER);
        neteasyMVPane.setMaxWidth(1080);
        latestMVPane.setMaxWidth(1080);
        contentBox.getChildren().addAll(titleBar, neteasyMVPane, latestMVPane);

        setContent(contentBox);
        setBackground(Background.EMPTY);
        setFitToWidth(true);
        getStyleClass().addAll("mv-pane");
    }
}
