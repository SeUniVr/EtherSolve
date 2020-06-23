package processing;

import graphviz.CFGPrinter;

public class GraphvizProcessor {
    public static String process(String dotNotation) throws GraphvizException{
        return CFGPrinter.renderDotToSvgString(dotNotation);
    }
}
