package gvgui;

import parseTree.BasicBlock;
import parseTree.Cfg;

import java.io.*;
import java.time.LocalDateTime;

public class CFGPrinter {
    private static final String DEFAULT_SAVE_PATH = "./renderedCFG/";
    public static final String PNG_FORMAT = "png";
    public static final String SVG_FORMAT = "svg";

    private static final String CURRENT_FORMAT = SVG_FORMAT;

    /**
     * Create the dot notation from the cfg
     *
     * @param cfg input cfg
     * @return dot notation string
     */
    private static String getDotNotation(Cfg cfg) {
        GVGraph graph = new GVGraph();

        GVBlock from, to;
        for (BasicBlock bb : cfg){
            from = new GVBlock(bb);
            if (! graph.getBlocks().contains(from))
                graph.addBlock(from);
            for (BasicBlock child : bb.getChildren()){
                to = new GVBlock(child);
                if (! graph.getBlocks().contains(to))
                    graph.addBlock(to);
                graph.addEdge(from, to);
            }
        }
        return graph.toString(); // dot notation
    }

    /**
     * Save the rendered graph
     *
     * @param cfg graph to save
     * @return file path of saved graph
     */
    public static String save(Cfg cfg){
        String dot = getDotNotation(cfg);
        return renderGraph(dot, CURRENT_FORMAT);
    }

    /**
     * Save the rendered graph and show it graphically
     *
     * @param cfg graph to save and show
     */
    public static void show(Cfg cfg) {
        new Thread(() -> {
            String filepath = save(cfg);
            showGraph(filepath);
        }).start();
    }

    /**
     * This method:
     *  - creates the Dot notation file
     *  - compiles Dot notation file into the relative image
     *
     * @param s Dot Notation String
     * @return file path of saved graph
     */
    private static String renderGraph(String s, String format){
        try {
            new File(DEFAULT_SAVE_PATH).mkdirs();
            String fileName = LocalDateTime.now().toString();
            writeFile(DEFAULT_SAVE_PATH + fileName + ".dot", s);

            String command = "dot " + DEFAULT_SAVE_PATH + fileName + ".dot" +
                            " -T" + format + " -o " +
                            DEFAULT_SAVE_PATH + fileName + "." + format;
            System.out.println(command);
            executeCommand(command);

            return DEFAULT_SAVE_PATH + fileName + "." + format;
        }
        catch(Exception e){
            System.out.println("renderGraph: Error");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method shows the graph image on screen
     */
    private static void showGraph(String filepath){
        try {
            String osName = System.getProperty("os.name").toLowerCase();
            if(osName.contains("mac"))
                executeCommand("open " + filepath);
            else { //getOS().toLowerCase().contains("linux")
                executeCommand("eog " + filepath); //xdg-open --> problem with wait
            }
        }
        catch(Exception e){
            System.out.println("showGraph: Error");
            e.printStackTrace();
        }
    }

    /**
     * Executes a command on the linux terminal
     *
     * @param command command to be executed
     * @param waitResponse if true the method returns the value only after the command has been executed
     * @return message result of command
     */
    private static String executeCommand(String command, boolean waitResponse) throws Exception{
        StringBuilder output = new StringBuilder();
        Process p = Runtime.getRuntime().exec(command);

        if(!waitResponse)
            return "The command has been executed";

        p.waitFor();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = "";
        while ((line = reader.readLine())!= null) {
            output.append(line).append("\n");
        }

        return output.toString();
    }

    /**
     * Executes a command on the linux terminal, Default behavior: wait the command result
     *
     * @param command command to be executed
     * @return result of the command
     */
    private static String executeCommand(String command) throws Exception {
        return executeCommand(command, true);
    }

    /**
     * Writes a file
     *
     * @param path destination path of file
     * @param text text to write on file
     */
    private static void writeFile(String path, String text) throws IOException
    {
        File file = new File(path);
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(text);
        out.close();
    }
}
