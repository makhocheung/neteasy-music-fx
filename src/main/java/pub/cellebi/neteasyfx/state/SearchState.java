package pub.cellebi.neteasyfx.state;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

import java.util.LinkedHashSet;

public class SearchState {
    public SimpleStringProperty searchWord = new SimpleStringProperty();
    public ObservableList<String> hotSearch = FXCollections.observableArrayList();
    public ObservableSet<String> historySearch = FXCollections.observableSet(new LinkedHashSet<>());
}
