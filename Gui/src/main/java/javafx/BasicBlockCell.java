package javafx;

import com.fxgraph.cells.AbstractCell;
import com.fxgraph.graph.Graph;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import parseTree.BasicBlock;
import parseTree.BasicBlockType;

public class BasicBlockCell extends AbstractCell {

    private final BasicBlock mBasicBlock;

    private BasicBlockCell(){
        this(new BasicBlock());
    }

    public BasicBlockCell(BasicBlock mBasicBlock) {
        this.mBasicBlock = mBasicBlock;
    }

    @Override
    public Region getGraphic(Graph graph) {
        Label result = new Label(mBasicBlock.toString());
        result.setStyle("-fx-border-color: red;");
        result.setStyle("-fx-background-color: #dddddd");
        if (mBasicBlock.getType() == BasicBlockType.DISPATCHER)
            result.setStyle("-fx-background-color: #fdffaf");
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicBlockCell that = (BasicBlockCell) o;
        return mBasicBlock.equals(that.mBasicBlock);
    }

    @Override
    public int hashCode() {
        return mBasicBlock.hashCode();
    }

}
