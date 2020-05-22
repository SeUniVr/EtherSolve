package graphviz;

import parseTree.cfg.BasicBlock;
import parseTree.cfg.Cfg;
import parseTree.cfg.CfgBuildReport;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CFGPrinter {
    private static final String DEFAULT_OUTPUT_PATH = "./outputs/reports/";
    private static final String DEFAULT_TEMPLATE = "report-template/template.html";
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
            for (BasicBlock child : bb.getSuccessors()){
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
     * @param cfg graph to save and show
     * @return the path of the cfg generated
     */
    public static String saveAndShow(Cfg cfg) {
        String filepath = save(cfg);

        new Thread(() -> { // New Thread
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
        }).start();

        return filepath;
    }

    /**
     * This method:
     *  - creates the Dot notation file
     *  - compiles Dot notation file into the relative image
     *
     * @param s Dot Notation String
     * @return file path of saved graph
     */
    private static String renderGraph(String s, String format) {
        try {
            new File(DEFAULT_OUTPUT_PATH).mkdirs();
            String fileName = LocalDateTime.now().toString();
            writeFile(DEFAULT_OUTPUT_PATH + fileName + ".dot", s);

            String command = "dot " + DEFAULT_OUTPUT_PATH + fileName + ".dot" +
                    " -T" + format + " -o " +
                    DEFAULT_OUTPUT_PATH + fileName + "." + format;
            System.out.println(command);
            executeCommand(command);

            return DEFAULT_OUTPUT_PATH + fileName + "." + format;
        } catch (Exception e) {
            System.out.println("renderGraph: Error");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convert Hex to Ascii
     *
     * @param hexStr hex string to convert
     * @return ascii string
     */
    private static String hexToAsciiString(String hexStr) {
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    /**
     * Create a HTML Report with generated cfg
     *
     * @param svg_filename path of generated cfg
     * @param solidity_version solidity version of the current contract
     * @param elapsed_time elapsed time
     * @param remainingData hexadecimal data
     * @param buildReport
     * @return path of generated report
     */
    public static String createReport(String svg_filename, String solidity_version, long elapsed_time, String remainingData, CfgBuildReport buildReport){
        String current_datetime = LocalDateTime.now().toString();

        String svg_xml = loadFile(svg_filename);
        String template = loadFile(Objects.requireNonNull(CFGPrinter.class.getClassLoader().getResource(DEFAULT_TEMPLATE)).getPath());

        Map<String, String> model = new HashMap<>();
        model.put("svg_xml", svg_xml);
        model.put("datetime", current_datetime);
        model.put("solidity_version", solidity_version);
        model.put("elapsed_time", elapsed_time + " ms");
        model.put("remaining_data", remainingData);
        model.put("decoded_remaining_data", hexToAsciiString(remainingData));
        model.put("error_log", buildReport.toString().replace("\n", "<br>")
                + "<br><br>" + buildReport.getLog().replace("\n", "<br>"));

        for (String key : model.keySet()){
            template = template.replace("%{" + key + "}", model.get(key));
        }

        String output_fileName = DEFAULT_OUTPUT_PATH + current_datetime + ".html";
        writeFile(output_fileName, template);
        return output_fileName;
    }

    /**
     * Open a html report in the default browser
     * @param report_path the path of the report
     */
    public static void openHtmlReport(String report_path) {
        try {
            Desktop desktop = java.awt.Desktop.getDesktop();
            String url = "file:///" + new File(report_path).getAbsolutePath();
            desktop.browse(new URI(url));
        } catch (Exception e) {
            System.err.format("Error while opening the html report %s: %s%n", report_path, e);
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
    private static String executeCommand(String command, boolean waitResponse) {
        StringBuilder output = new StringBuilder();

        try {
            Process p = Runtime.getRuntime().exec(command);
            if (!waitResponse)
                return "The command has been executed";

            p.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (Exception e){
            System.err.format("Error while executing the command %s: %s%n", command, e);
            e.printStackTrace();
        }

        return output.toString();
    }

    /**
     * Executes a command on the linux terminal, Default behavior: wait the command result
     *
     * @param command command to be executed
     * @return result of the command
     */
    private static String executeCommand(String command) {
        return executeCommand(command, true);
    }

    /**
     * Load a file and return the content of this one
     *
     * @param path path of file
     * @return the content of the file
     */
    private static String loadFile(String path) {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.format("Error reading file %s: %s%n", path, e);
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Writes a file with specified text
     *
     * @param path destination path of file
     * @param text text to write on file
     */
    private static void writeFile(String path, String text) {
        File file = new File(path);
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
            out.write(text);
        } catch (IOException e) {
            System.err.format("Error writing file %s: %s%n", path, e);
            e.printStackTrace();
        }
    }
}
