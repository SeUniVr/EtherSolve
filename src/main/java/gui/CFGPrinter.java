package gui;

import com.fxgraph.edges.DoubleCorneredEdge;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.Model;
import com.fxgraph.layout.AbegoTreeLayout;
import javafx.geometry.Orientation;
import javafx.scene.layout.Pane;
import org.abego.treelayout.Configuration;
import parseTree.BasicBlock;
import parseTree.Contract;

public class CFGPrinter {
    public static Pane print(Contract contract) {

        Graph mGraph = new Graph();
        final Model mModel = mGraph.getModel();

        mGraph.beginUpdate();

        BasicBlockCell from, to;
        for (BasicBlock bb : contract.getBasicBlocks()){
            from = new BasicBlockCell(bb);
            if (! mModel.getAddedCells().contains(from))
                mModel.addCell(from);
            for (BasicBlock child : bb.getChildren()){
                to = new BasicBlockCell(child);
                if (! mModel.getAddedCells().contains(to))
                    mModel.addCell(to);
                mModel.addEdge(new DoubleCorneredEdge(from, to, Orientation.VERTICAL));
            }
        }

        mGraph.endUpdate();
        mGraph.layout(new AbegoTreeLayout(200, 200, Configuration.Location.Top));

        return mGraph.getCanvas();

    }
}
