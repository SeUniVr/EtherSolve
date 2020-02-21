package gui;

import com.fxgraph.edges.AbstractEdge;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
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

    private class CfgEdgeGraphic extends Pane {

        public CfgEdgeGraphic(CfgEdge cfgEdge, Graph graph) {

            Region source = graph.getGraphic(cfgEdge.getSource());
            Region target = graph.getGraphic(cfgEdge.getTarget());

            DoubleBinding startX = source.layoutXProperty().add(target.widthProperty().divide(2));
            DoubleBinding startY = source.layoutYProperty().add(source.heightProperty());
            DoubleBinding endX = target.layoutXProperty().add(target.widthProperty().divide(2));
            DoubleBinding endY = target.layoutYProperty().add(0);
            DoubleBinding centerY = startY.add(endY).divide(2);

            Random randomGenerator = new Random();
            double randomSize = 20;
            double xOffset = randomGenerator.nextDouble()*randomSize - randomSize / 2;
            startX = startX.add(xOffset);
            endX = endX.add(xOffset);

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

    /**
     * @author https://stackoverflow.com/questions/41353685/how-to-draw-arrow-javafx-pane
     */
    public class Arrow extends Group {

        private final Line line;

        public Arrow() {
            this(new Line(), new Line(), new Line());
        }

        public Arrow(Line line, Line arrow1, Line arrow2) {
            this(line, arrow1, arrow2, 10, 5);
        }

        public Arrow(Line line, Line arrow1, Line arrow2, double arrowLength, double arrowWidth) {
            super(line, arrow1, arrow2);
            this.line = line;
            InvalidationListener updater = o -> {
                double ex = getEndX();
                double ey = getEndY();
                double sx = getStartX();
                double sy = getStartY();

                arrow1.setEndX(ex);
                arrow1.setEndY(ey);
                arrow2.setEndX(ex);
                arrow2.setEndY(ey);

                if (ex == sx && ey == sy) {
                    // arrow parts of length 0
                    arrow1.setStartX(ex);
                    arrow1.setStartY(ey);
                    arrow2.setStartX(ex);
                    arrow2.setStartY(ey);
                } else {
                    double factor = arrowLength / Math.hypot(sx-ex, sy-ey);
                    double factorO = arrowWidth / Math.hypot(sx-ex, sy-ey);

                    // part in direction of main line
                    double dx = (sx - ex) * factor;
                    double dy = (sy - ey) * factor;

                    // part ortogonal to main line
                    double ox = (sx - ex) * factorO;
                    double oy = (sy - ey) * factorO;

                    arrow1.setStartX(ex + dx - oy);
                    arrow1.setStartY(ey + dy + ox);
                    arrow2.setStartX(ex + dx + oy);
                    arrow2.setStartY(ey + dy - ox);
                }
            };

            // add updater to properties
            startXProperty().addListener(updater);
            startYProperty().addListener(updater);
            endXProperty().addListener(updater);
            endYProperty().addListener(updater);
            updater.invalidated(null);
        }

        // start/end properties

        public final void setStartX(double value) {
            line.setStartX(value);
        }

        public final double getStartX() {
            return line.getStartX();
        }

        public final DoubleProperty startXProperty() {
            return line.startXProperty();
        }

        public final void setStartY(double value) {
            line.setStartY(value);
        }

        public final double getStartY() {
            return line.getStartY();
        }

        public final DoubleProperty startYProperty() {
            return line.startYProperty();
        }

        public final void setEndX(double value) {
            line.setEndX(value);
        }

        public final double getEndX() {
            return line.getEndX();
        }

        public final DoubleProperty endXProperty() {
            return line.endXProperty();
        }

        public final void setEndY(double value) {
            line.setEndY(value);
        }

        public final double getEndY() {
            return line.getEndY();
        }

        public final DoubleProperty endYProperty() {
            return line.endYProperty();
        }

    }
}
