package pub.cellebi.neteasyfx.state;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Map;
//todo
public class RecommendSongSheetState {
    public final ObservableList<Map<String,String>> items;
    public RecommendSongSheetState(){
        items = FXCollections.observableArrayList();
    }
}
