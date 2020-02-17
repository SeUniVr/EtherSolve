package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.javafx.FxGraphRenderer;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

public class MainGUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setScene(new Scene(new StackPane((javafx.scene.Node) prova()), 800, 600));
        primaryStage.show();
    }

    private View prova() {
        Graph mGraph = new SingleGraph("1");
        System.setProperty("org.graphstream.ui", "javafx");
        mGraph.setAttribute("ui.antialias");
        mGraph.setAttribute("ui.quality");
        mGraph.addNode("1");
        Node a = mGraph.getNode("1");
        a.setAttribute("ui.label", "Ciao\nCome\nVa\nTesto Molto lungo\nAltro testo\nPUSH 0x100\nJUMP");
        a.setAttribute("ui.stroke-mode", "plain");
        a.setAttribute("ui.shape", "box");
        a.setAttribute("ui.size", "40px, 40px");
        mGraph.addNode("2");
        a = mGraph.getNode("2");
        a.setAttribute("ui.label", "Ciao\nCome\nVa\nTesto Molto lungo\nAltro testo\nPUSH 0x100\nJUMP");
        a.setAttribute("ui.stroke-mode", "plain");
        a.setAttribute("ui.shape", "box");
        a.setAttribute("ui.size", "40px, 40px");
        mGraph.addEdge("1", "1", "2");
        return new FxViewer(mGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD).addDefaultView(false, new FxGraphRenderer());
    }
}
