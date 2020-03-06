package pub.cellebi.neteasyfx.modules.explore;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import pub.cellebi.neteasyfx.modules.TitleBar;
import pub.cellebi.neteasyfx.modules.Spacer;

public final class ExplorePane extends ScrollPane {

    private final TitleBar titleBar;
    private final Carousel carousel;
    private final RecommendSongSheetPane recommendSongSheetPane;
    private final ExclusiveBroadcastPane exclusiveBroadcastPane;
    private final LatestMusicPane latestMusicPane;
    private final RecommendMVPane recommendMVPane;
    private final RecommendRadioPane recommendRadioPane;

    public ExplorePane() {
        titleBar = new TitleBar("发现音乐");
        carousel = new Carousel(800);
        recommendSongSheetPane = new RecommendSongSheetPane();
        exclusiveBroadcastPane = new ExclusiveBroadcastPane();
        latestMusicPane = new LatestMusicPane();
        recommendMVPane = new RecommendMVPane();
        recommendRadioPane = new RecommendRadioPane();

        render();
    }

    private void render() {
        var vBox = new VBox();
        vBox.getStyleClass().add("explore-content");
        vBox.setSpacing(30);
        vBox.getChildren().addAll(titleBar, carousel, recommendSongSheetPane, exclusiveBroadcastPane, latestMusicPane,
                recommendMVPane, recommendRadioPane, Spacer.VSpacer(50));
        vBox.setAlignment(Pos.TOP_CENTER);

        setContent(vBox);
        setFitToWidth(true);
        setPadding(new Insets(20, 30, 0, 30));
        setBackground(Background.EMPTY);
        getStyleClass().add("explore-pane");
    }
}
