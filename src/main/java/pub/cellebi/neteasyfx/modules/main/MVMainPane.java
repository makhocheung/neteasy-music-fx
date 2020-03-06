package pub.cellebi.neteasyfx.modules.main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import pub.cellebi.neteasyfx.state.Jux;
import pub.cellebi.neteasyfx.modules.TitleBar;
import pub.cellebi.neteasyfx.modules.Line;
import pub.cellebi.neteasyfx.modules.Spacer;

import java.util.Map;

public class MVMainPane extends VBox {

    private final TitleBar titleBar;
    private final MediaView mediaView;
    private final Label descLabel;
    private final Label publishTimeLabel;
    private final Label artistLabel;
    private final Label playCountLabel;

    private final Map<String, Object> data;


    public MVMainPane(Map<String, Object> data) {
        titleBar = new TitleBar((String) data.get("name"));
        mediaView = new MediaView();
        descLabel = new Label();
        publishTimeLabel = new Label();
        artistLabel = new Label();
        playCountLabel = new Label();
        this.data = data;

        render();
        dataBind();
        registerListener();
        initState();
    }

    public void render() {
        var mediaContentPane = new StackPane();
        mediaContentPane.setMaxWidth(1080);
        mediaContentPane.setStyle("-fx-background-color: black");
        mediaContentPane.getChildren().addAll(mediaView);
        mediaView.setFitWidth(720);
        publishTimeLabel.setTextFill(Color.rgb(172, 172, 172));
        descLabel.setTextFill(Color.rgb(172, 172, 172));
        descLabel.setWrapText(true);
        var spacer = Spacer.HSpacer(0);
        artistLabel.setTextFill(Color.rgb(26, 90, 153));
        playCountLabel.setTextFill(Color.rgb(153, 153, 153));
        titleBar.getChildren().addAll(artistLabel, spacer, playCountLabel);
        titleBar.setMaxWidth(1080);
        titleBar.setSpacing(5);
        titleBar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(titleBar, mediaContentPane, createContentPane());
        setAlignment(Pos.TOP_CENTER);
        setSpacing(10);
        setPadding(new Insets(20, 30, 0, 30));
        setStyle("-fx-background-color: rgb(250, 250, 252)");
    }

    public void dataBind() {
        mediaView.mediaPlayerProperty().bind(Jux.PLAYER_STATE.player);
    }

    public void registerListener() {
        titleBar.action = () -> {
            mediaView.mediaPlayerProperty().unbind();
            mediaView.setMediaPlayer(null);
            var mediaPlayer = Jux.PLAYER_STATE.player.get();
            mediaPlayer.stop();
            mediaPlayer.dispose();
            Jux.PLAYER_STATE.player.set(null);
            Jux.PLAYER_STATE.isPlaying.set(false);
            Jux.PLAYER_STATE.songImageUrl.set(null);
        };
    }

    @SuppressWarnings("unchecked")
    public void initState() {
        artistLabel.setText((String) data.get("artistName"));
        playCountLabel.setText("播放: " + data.get("playCount"));
        publishTimeLabel.setText("发布时间: " + data.get("publishTime"));
        descLabel.setText((String) data.get("desc"));
        var brs = (Map<String, String>) data.get("brs");
        var mediaPlayer = new MediaPlayer(new Media(brs.get("720")));
        mediaPlayer.play();
        Jux.PLAYER_STATE.updatePlayer(mediaPlayer);
        Jux.PLAYER_STATE.songImageUrl.set((String) data.get("cover"));
        Jux.PLAYER_STATE.isPlaying.set(true);
        Jux.PLAYER_STATE.songName.set((String) data.get("name"));
        Jux.PLAYER_STATE.artists.set((String) data.get("artistName"));
    }

    private VBox createContentPane() {
        var vBox = new VBox();
        var title = new Label("MV介绍");
        vBox.getChildren().addAll(title, Line.HLine(), publishTimeLabel, descLabel);
        vBox.setAlignment(Pos.CENTER_LEFT);
        vBox.setSpacing(10);
        vBox.setMaxWidth(1080);
        return vBox;
    }
}
