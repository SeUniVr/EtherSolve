package graphviz;

import SolidityInfo.SolidityVersionUnknownException;
import parseTree.Contract;
import parseTree.cfg.BasicBlock;
import parseTree.cfg.Cfg;
import parseTree.cfg.CfgBuildReport;
import utils.Message;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CFGPrinter {
    private static final String DEFAULT_OUTPUT_PATH = "./outputs/reports/";
    private static final String DEFAULT_TEMP_OUTPUT_PATH = "./";
    private static final String DEFAULT_TEMPLATE = "report-template/template.html";
    private static final String NEW_TEMPLATE = "report-template/template-vue.html";

    public static final String PNG_FORMAT = "png";
    public static final String SVG_FORMAT = "svg";
    private static final String DEFAULT_FORMAT = SVG_FORMAT;

    /**
     * Create the dot notation from the cfg
     *
     * @param cfg input cfg
     * @return dot notation string
     */
    public static String getDotNotation(Cfg cfg) {
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
     * This method:
     *  - creates the Dot notation file
     *  - renders Dot notation file into the relative image
     *
     * @param dotString dot notation string
     * @param format Graphviz rendering output
     * @param temp if true instead of saving files return render file as string
     * @return file path of saved graph image
     */
    private static String renderGraph(String dotString, String format, boolean temp) {
        try {
            String outputPath = !temp ? DEFAULT_OUTPUT_PATH : DEFAULT_TEMP_OUTPUT_PATH;

            // Creation of the dot file
            LocalDateTime datetime = LocalDateTime.now();
            DateTimeFormatter datetime_format = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String fileName = datetime_format.format(datetime);
            String dotFilePath = outputPath + datetime_format.format(datetime) + ".dot";
            writeFile(dotFilePath, dotString);

            // Rendering of the dot file
            String imageFilePath = outputPath + fileName + "." + format;
            String command = "dot " + dotFilePath + " -T" + format + " -o " + imageFilePath;
            Message.printDebug(command);
            executeCommand(command);

            if (!temp)
                return imageFilePath;
            else {
                String svgString = loadFile(imageFilePath);
                new File(dotFilePath).delete();
                new File(imageFilePath).delete();
                return svgString;
            }
        } catch (Exception e) {
            System.out.println("renderGraph: Error");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Render cfg into an svg image and return it as string
     *
     * @param dotNotation dotNotation of cfg
     * @return svg string of rendered cfg
     */
    public static String renderDotToSvgString(String dotNotation){
        return renderGraph(dotNotation, SVG_FORMAT, true);
    }

    /**
     * Render cfg into an image with a specific format and save it
     *
     * @param cfg cfg to render
     * @param format render image format
     * @return path of rendered image
     */
    public static String renderAndSave(Cfg cfg, String format){
        String dot = getDotNotation(cfg);
        return renderGraph(dot, format, false);
    }

    /**
     * Render cfg into an image with default format and save it
     *
     * @param cfg cfg to render
     * @return path of rendered image
     */
    public static String renderAndSave(Cfg cfg){
        return renderAndSave(cfg, DEFAULT_FORMAT);
    }

    /**
     * Save the rendered graph and show it graphically with EOG
     * @param cfg graph to save and show
     * @return the path of the cfg generated
     */
    public static String renderSaveAndShow(Cfg cfg) {
        String filepath = renderAndSave(cfg);

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
        LocalDateTime datetime = LocalDateTime.now();

        String svg_xml = loadFile(svg_filename);
        String template = loadFile(Objects.requireNonNull(CFGPrinter.class.getClassLoader().getResource(DEFAULT_TEMPLATE)).getPath());

        Map<String, String> model = new HashMap<>();
        model.put("svg_xml", svg_xml);
        model.put("datetime", datetime.toString());
        model.put("solidity_version", solidity_version);
        model.put("elapsed_time", elapsed_time + " ms");
        model.put("remaining_data", remainingData);
        model.put("decoded_remaining_data", hexToAsciiString(remainingData));
        model.put("error_log", buildReport.toString().replace("\n", "<br>")
                + "<br><br>" + buildReport.getLog().replace("\n", "<br>"));

        for (String key : model.keySet()){
            template = template.replace("%{" + key + "}", model.get(key));
        }

        DateTimeFormatter datetime_format = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String fileName = DEFAULT_OUTPUT_PATH + datetime_format.format(datetime) + ".html";
        writeFile(fileName, template);
        return fileName;
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
        file.getParentFile().mkdirs(); // create the directory if it does not exist
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
            out.write(text);
        } catch (IOException e) {
            System.err.format("Error writing file %s: %s%n", path, e);
            e.printStackTrace();
        }
    }

    public static String getHtmlReport(Contract contract) {
        String template = loadResource(NEW_TEMPLATE);

        Cfg cfg = contract.getRuntimeCfg();
        CfgBuildReport report = cfg.getBuildReport();
        String solidityVersion;
        try {
            solidityVersion = contract.getExactSolidityVersion();
        } catch (SolidityVersionUnknownException e) {
            solidityVersion = "Unknown or < 0.5.9";
        }
        String errorsLog = report.getLog();
        if (errorsLog.equals(""))
            errorsLog = "Nothing to show";

        Map<String, String> model = new HashMap<>();
        model.put("contractName", contract.getName());
        model.put("solidityVersion", solidityVersion);
        model.put("buildTimeMillis", String.valueOf(report.getBuildTimeMillis()));
        model.put("contractHash", contract.getContractHash());
        model.put("isOnlyRuntime", String.valueOf(contract.isOnlyRuntime()));
        model.put("outputLog", errorsLog);
        model.put("sourceCode", contract.getBinarySource());
        model.put("remainingData", cfg.getRemainingData());
        model.put("errorsCount", String.valueOf(report.getTotalErrors()));
        model.put("criticalErrors", String.valueOf(report.getCriticalErrors()));
        model.put("blockLimitErrors", String.valueOf(report.getBlockLimitErrors()));
        model.put("orphanJumpTargetUnknownErrors", String.valueOf(report.getOrphanJumpTargetUnknownErrors()));
        model.put("orphanJumpTargetNullErrors", String.valueOf(report.getOrphanJumpTargetNullErrors()));
        model.put("directJumpTargetErrors", String.valueOf(report.getDirectJumpTargetErrors()));
        model.put("loopDepthExceededErrors", String.valueOf(report.getLoopDepthExceededErrors()));
        model.put("multipleRootNodesErrors", String.valueOf(report.getMultipleRootNodesErrors()));
        model.put("stackExceededErrors", String.valueOf(report.getStackExceededErrors()));
        model.put("svgXml", renderDotToSvgString(getDotNotation(cfg)));


        for (String key : model.keySet()){
            template = template.replace("%{" + key + "}%", model.get(key));
        }

        return template;
    }

    private static String loadResource(String resourceName){
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(CFGPrinter.class.getClassLoader().getResourceAsStream(resourceName))))){
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error reading resource " + resourceName);
        }

        return sb.toString();
    }
}
