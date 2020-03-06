package pub.cellebi.neteasyfx.state;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

public class RouteState {
    public final SimpleObjectProperty<Node> currentRouteNode;

    public RouteState() {
        currentRouteNode = new SimpleObjectProperty<>();
    }
}
