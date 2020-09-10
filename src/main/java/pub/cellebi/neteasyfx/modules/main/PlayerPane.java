package pub.cellebi.neteasyfx.modules.main;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.When;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import pub.cellebi.neteasyfx.modules.PlayListPane;
import pub.cellebi.neteasyfx.modules.VolumePane;
import pub.cellebi.neteasyfx.state.Jux;
import pub.cellebi.neteasyfx.utils.ImageUtil;
import pub.cellebi.neteasyfx.modules.Spacer;

public final class PlayerPane extends HBox {

    private final StackPane playHead;
    private final ImageView songImgView;
    private final Button pre;
    private final Button next;
    private final Button action;
    private final Slider playProcess;
    private final Button volume;
    private final Button playList;
    private final SVGPath pauseSVG;
    private final SVGPath playSVG;
    private final Label song;
    private final Label artists;
    private VolumePane volumePane;
    private PlayListPane playListPane;

    public PlayerPane() {
        song = new Label();
        artists = new Label();
        pauseSVG = new SVGPath();
        playSVG = new SVGPath();
        playHead = new StackPane();
        songImgView = new ImageView();
        pre = new Button();
        next = new Button();
        action = new Button();
        playProcess = new Slider();
        volume = new Button();
        playList = new Button();

        render();
        dataBind();
        registerListener();
    }

    public void render() {
        pauseSVG.setFill(Color.WHITE);
        pauseSVG.setContent("M6 19h4V5H6v14zm8-14v14h4V5h-4z");
        var vBox = new VBox();
        vBox.getChildren().addAll(createPlayName(), playProcess);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(4);
        HBox.setHgrow(vBox, Priority.ALWAYS);
        playHead.getChildren().add(songImgView);
        playHead.getStyleClass().add("player-head");
        playHead.setPrefWidth(60);
        var preSvg = new SVGPath();
        preSvg.setContent("M6 6h2v12H6zm3.5 6l8.5 6V6z");
        preSvg.setFill(Color.WHITE);
        pre.setGraphic(preSvg);
        pre.getStyleClass().add("player-button");
        pre.setPrefSize(29, 29);
        var nextSvg = new SVGPath();
        nextSvg.setContent("M6 18l8.5-6L6 6v12zM16 6v12h2V6h-2z");
        nextSvg.setFill(Color.WHITE);
        next.setGraphic(nextSvg);
        next.getStyleClass().add("player-button");
        next.setPrefSize(29, 29);
        playSVG.setContent("M8 5v14l11-7z");
        playSVG.setFill(Color.WHITE);
        action.setGraphic(playSVG);
        action.getStyleClass().add("player-button");
        action.setPrefSize(33, 33);
        var volSvg = new SVGPath();
        volSvg.setContent("M12.79 9c0-1.3-.72-2.42-1.79-3v6c1.06-.58 1.79-1.7 1.79-3zM2 7v4h3l4 4V3L5 " +
                "7H2zm9-5v1.5c2.32.74 4 2.93 4 5.5s-1.68 4.76-4 5.5V16c3.15-.78 5.5-3.6 5.5-7S14.15 2.78 11 2z");
        volSvg.setFill(Color.GRAY);
        volume.setGraphic(volSvg);
        volume.getStyleClass().add("volume-button");
        volume.setPrefSize(15, 15);

        var playListSvg = new SVGPath();
        playListSvg.setContent("M3 13h2v-2H3v2zm0 4h2v-2H3v2zm0-8h2V7H3v2zm4 4h14v-2H7v2zm0 4h14v-2H7v2zM7 7v2h14V7H7z");
        playListSvg.setFill(Color.GRAY);
        playList.setGraphic(playListSvg);
        playList.getStyleClass().add("playlist-button");
        playList.setPrefSize(15, 15);

        getChildren().addAll(playHead, pre, action, next, vBox, playList, volume, Spacer.HSpacer(10));
        getStyleClass().add("player-pane");
        setPrefHeight(60);
        setSpacing(25);
        setAlignment(Pos.CENTER_LEFT);
    }

    public void registerListener() {
        action.setOnAction(e -> {
            if (!Jux.PLAYER_STATE.isDisable()) {
                if (Jux.PLAYER_STATE.isPlaying.get()) {
                    Jux.PLAYER_STATE.isPlaying.set(false);
                    Jux.PLAYER_STATE.player.get().pause();
                } else {
                    Jux.PLAYER_STATE.isPlaying.set(true);
                    Jux.PLAYER_STATE.player.get().play();
                }
            }
        });
        pre.setOnAction(e -> {
            if (Jux.SONG_LIST_STATE.canPre()) {
                Jux.SONG_LIST_STATE.currentIndex = Jux.SONG_LIST_STATE.currentIndex - 1;
                var song = Jux.SONG_LIST_STATE.songs.get(Jux.SONG_LIST_STATE.currentIndex);
                Jux.PLAYER_STATE.update(song);
            }
        });
        next.setOnAction(e -> {
            if (Jux.SONG_LIST_STATE.canNext()) {
                Jux.SONG_LIST_STATE.currentIndex = Jux.SONG_LIST_STATE.currentIndex + 1;
                var song = Jux.SONG_LIST_STATE.songs.get(Jux.SONG_LIST_STATE.currentIndex);
                Jux.PLAYER_STATE.update(song);
            }
        });
        volume.setOnAction(e -> {
            var player = Jux.PLAYER_STATE.player.get();
            if (player == null) {
                return;
            }
            var fixWidth = getWidth() - volume.getLayoutX() - volume.getWidth();
            var bounds = volume.getBoundsInLocal();
            var screenPoint = volume.localToScreen(bounds);
            var width = 300 + fixWidth;
            var height = 60;
            var x = screenPoint.getCenterX() - 300;
            var y = screenPoint.getMinY() - volume.getHeight() / 2 - height;
            if (volumePane == null) {
                volumePane = new VolumePane(width, height);
                volumePane.show(volume, x, y);
                var volumeValue = player.getVolume();
                volumePane.volumeControl.setValue(volumeValue * 100);
                player.volumeProperty().bind(volumePane.volumeControl.valueProperty().divide(100));
                var circle = new Circle(2, Color.web("#ec0202"));
                var thumb = (StackPane) volumePane.volumeControl.lookup(".thumb");
                thumb.setEffect(null);
                thumb.getChildren().add(circle);
            } else {
                volumePane.show(volume, x, y);
            }
        });
        playList.setOnAction(e -> {
            //var fixWidth = getWidth() - playList.getLayoutX() - playList.getWidth();
            var bounds = playList.getBoundsInLocal();
            var screenPoint = playList.localToScreen(bounds);
            //var width = 300 + fixWidth;
            var x = screenPoint.getCenterX() - 300;
            var y = screenPoint.getMinY() - 500;
            if (playListPane == null) {
                playListPane = new PlayListPane();
            }
            playListPane.show(playList, x, y);

        });
        playProcess.valueProperty().addListener((o, ov, nv) -> {
            String style = String.format("-fx-background-color: linear-gradient(to right, #ec0202 %d%%, #e5e5e5 %d%%);",
                    nv.intValue(), nv.intValue());
            playProcess.lookup(".track").setStyle(style);
        });
        Jux.PLAYER_STATE.player.addListener((v, o, n) -> {
            if (o != null) {
                o.volumeProperty().unbind();
            }
            if (n != null && volumePane != null) {
                n.volumeProperty().bind(volumePane.volumeControl.valueProperty().divide(100));
            }
            var handler = playProcess.lookup(".track").getOnMousePressed();
            if (n != null) {
                action.graphicProperty().bind(new When(Jux.PLAYER_STATE.isPlaying).then(pauseSVG).otherwise(playSVG));

                var mediaPlayer = Jux.PLAYER_STATE.player.get();
                mediaPlayer.currentTimeProperty().addListener((v1, o1, n1) -> {
                    var p = mediaPlayer.getCurrentTime().toSeconds() / mediaPlayer.getTotalDuration().toSeconds();
                    playProcess.setValue(p * 100);
                });

                playProcess.lookup(".thumb").setOnDragDetected(e -> {
                    Jux.PLAYER_STATE.player.get().pause();
                });

                playProcess.lookup(".thumb").setOnMouseReleased(e -> {
                    var seekTime = Jux.PLAYER_STATE.player.get().getTotalDuration().multiply(playProcess.getValue() / 100);
                    Jux.PLAYER_STATE.player.get().seek(seekTime);
                    Jux.PLAYER_STATE.player.get().play();
                });

                playProcess.lookup(".track").setOnMousePressed(e -> {
                    Jux.PLAYER_STATE.player.get().pause();
                    handler.handle(e);
                });
                playProcess.lookup(".track").setOnMouseReleased(e -> {
                    var seekTime = Jux.PLAYER_STATE.player.get().getTotalDuration().multiply(playProcess.getValue() / 100);
                    Jux.PLAYER_STATE.player.get().seek(seekTime);
                    Jux.PLAYER_STATE.player.get().play();
                });
            } else {
                action.graphicProperty().unbind();
                action.setGraphic(playSVG);
                playProcess.valueProperty().unbind();
                playProcess.setValue(0);
            }
        });
    }

    public void dataBind() {
        songImgView.fitHeightProperty().bind(playHead.heightProperty());
        songImgView.fitWidthProperty().bind(playHead.widthProperty());
        songImgView.imageProperty().bind(Bindings.createObjectBinding(() -> {
            if (Jux.PLAYER_STATE.songImageUrl.get() == null) {
                return null;
            } else {
                return ImageUtil.loadImage(Jux.PLAYER_STATE.songImageUrl.get(), 180);
            }
        }, Jux.PLAYER_STATE.songImageUrl));
        song.textProperty().bind(Jux.PLAYER_STATE.songName);
        artists.textProperty().bind(Jux.PLAYER_STATE.artists);
    }

    private HBox createPlayName() {
        var hBox = new HBox();
        var dash = new Label("-");
        hBox.getChildren().addAll(song, dash, artists);
        dash.setTextFill(Color.rgb(151, 151, 151));
        artists.setTextFill(Color.rgb(151, 151, 151));
        hBox.setSpacing(3);
        return hBox;
    }
}
