package javafx;

import com.fxgraph.edges.AbstractEdge;
import com.fxgraph.graph.Arrow;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;

import java.util.Random;

public class CfgBackwardEdge extends AbstractEdge {

    private float offset;

    public CfgBackwardEdge(ICell source, ICell target){
        this(source, target, 25);
    }

    public CfgBackwardEdge(ICell source, ICell target, float offset) {
        super(source, target);
        this.offset = offset;
    }

    @Override
    public Region getGraphic(Graph graph) {
        return new CfgBackwardEdgeGraphic(this, graph);
    }

    private static class CfgBackwardEdgeGraphic extends Pane {
        public CfgBackwardEdgeGraphic(CfgBackwardEdge cfgBackwardEdge, Graph graph) {
            Region source = graph.getGraphic(cfgBackwardEdge.getSource());
            Region target = graph.getGraphic(cfgBackwardEdge.getTarget());

            DoubleBinding startX = source.layoutXProperty().add(source.widthProperty().divide(2));
            DoubleBinding startY = source.layoutYProperty().add(source.heightProperty());
            DoubleBinding endX = target.layoutXProperty().add(target.widthProperty().divide(2));
            DoubleBinding endY = target.layoutYProperty().add(0);

            DoubleBinding centerX = startX.add(endX).divide(2);

            Random randomGenerator = new Random();
            double randomOffset = randomGenerator.nextDouble() / 2 - 0.25;
            startX = startX.add(source.widthProperty().multiply(randomOffset));
            endX = endX.add(target.widthProperty().multiply(randomOffset));

            Line lineA = new Line();
            Line lineB = new Line();
            Line lineC = new Line();
            Line lineD = new Line();
            Arrow lineE = new Arrow();

            lineA.startXProperty().bind(startX);
            lineA.startYProperty().bind(startY);
            lineA.endXProperty().bind(startX);
            lineA.endYProperty().bind(startY.add(cfgBackwardEdge.offset));

            lineB.startXProperty().bind(startX);
            lineB.startYProperty().bind(startY.add(cfgBackwardEdge.offset));
            lineB.endXProperty().bind(centerX);
            lineB.endYProperty().bind(startY.add(cfgBackwardEdge.offset));

            lineC.startXProperty().bind(centerX);
            lineC.startYProperty().bind(startY.add(cfgBackwardEdge.offset));
            lineC.endXProperty().bind(centerX);
            lineC.endYProperty().bind(endY.subtract(cfgBackwardEdge.offset));

            lineD.startXProperty().bind(centerX);
            lineD.startYProperty().bind(endY.subtract(cfgBackwardEdge.offset));
            lineD.endXProperty().bind(endX);
            lineD.endYProperty().bind(endY.subtract(cfgBackwardEdge.offset));

            lineE.startXProperty().bind(endX);
            lineE.startYProperty().bind(endY.subtract(cfgBackwardEdge.offset));
            lineE.endXProperty().bind(endX);
            lineE.endYProperty().bind(endY);

            getChildren().addAll(lineA, lineB, lineC, lineD, lineE);
        }
    }
}
