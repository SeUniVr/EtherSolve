package gui;

import com.fxgraph.edges.AbstractEdge;
import com.fxgraph.graph.Arrow;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;

import java.util.Random;

public class CfgEdge extends AbstractEdge {

    public CfgEdge(ICell source, ICell target) {
        super(source, target);
    }

    @Override
    public Region getGraphic(Graph graph) {
        return new CfgEdgeGraphic(this, graph);
    }

    private static class CfgEdgeGraphic extends Pane {

        public CfgEdgeGraphic(CfgEdge cfgEdge, Graph graph) {

            Region source = graph.getGraphic(cfgEdge.getSource());
            Region target = graph.getGraphic(cfgEdge.getTarget());

            DoubleBinding startX = source.layoutXProperty().add(source.widthProperty().divide(2));
            DoubleBinding startY = source.layoutYProperty().add(source.heightProperty());
            DoubleBinding endX = target.layoutXProperty().add(target.widthProperty().divide(2));
            DoubleBinding endY = target.layoutYProperty().add(0);
            DoubleBinding centerY = startY.add(endY).divide(2);

            Random randomGenerator = new Random();
            double randomOffset = randomGenerator.nextDouble() / 2 - 0.25;
            startX = startX.add(source.widthProperty().multiply(randomOffset));
            endX = endX.add(target.widthProperty().multiply(randomOffset));

            Line lineA = new Line();
            Line lineB = new Line();
            Arrow lineC = new Arrow();

            lineA.startXProperty().bind(startX);
            lineA.startYProperty().bind(startY);
            lineA.endXProperty().bind(startX);
            lineA.endYProperty().bind(centerY);

            lineB.startXProperty().bind(startX);
            lineB.startYProperty().bind(centerY);
            lineB.endXProperty().bind(endX);
            lineB.endYProperty().bind(centerY);

            lineC.startXProperty().bind(endX);
            lineC.startYProperty().bind(centerY);
            lineC.endXProperty().bind(endX);
            lineC.endYProperty().bind(endY);

            getChildren().addAll(lineA, lineB, lineC);
        }
    }
}
