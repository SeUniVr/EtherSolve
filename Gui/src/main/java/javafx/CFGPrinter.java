package javafx;

import com.fxgraph.graph.Graph;
import com.fxgraph.graph.Model;
import com.fxgraph.layout.AbegoTreeLayout;
import javafx.scene.layout.Pane;
import org.abego.treelayout.Configuration;
import parseTree.BasicBlock;
import parseTree.Cfg;

public class CFGPrinter {
    public static Pane print(Cfg cfg) {

        Graph mGraph = new Graph();
        final Model mModel = mGraph.getModel();

        mGraph.beginUpdate();

        BasicBlockCell from, to;
        for (BasicBlock bb : cfg){
            from = new BasicBlockCell(bb);
            if (! mModel.getAddedCells().contains(from))
                mModel.addCell(from);
            for (BasicBlock child : bb.getSuccessors()){
                to = new BasicBlockCell(child);
                if (! mModel.getAddedCells().contains(to))
                    mModel.addCell(to);
                if (bb.getOffset() < child.getOffset())
                    mModel.addEdge(new CfgEdge(from, to));
                else
                    mModel.addEdge(new CfgBackwardEdge(from, to));
            }
        }

        mGraph.endUpdate();
        mGraph.layout(new AbegoTreeLayout(200, 200, Configuration.Location.Bottom));

        return mGraph.getCanvas();

    }
}
