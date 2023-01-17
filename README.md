<img src="https://github.com/SeUnivr/EtherSolve/blob/main/Logo/Logo.png" alt="EtherSolve logo" width="128px" height="128px"><br>
# EtherSolve

EtherSolve is a tool for *Control Flow Graph (CFG)* reconstruction of Solidity smart-contracts from Ethereum bytecode.

The tool is based on the ICPC 2021 paper [EtherSolve: Computing an Accurate Control-Flow Graph from Ethereum Bytecode](https://doi.org/10.1109/ICPC52881.2021.00021)

The tool also provides modules for vulnerabilities detection. It currently supports *Re-entrancy* and *Tx.origin* vulnerabilities.

## Usage

### JAR

The simplest way to try EtherSolve is through the provided `EtherSolve.jar` ([link](https://github.com/SeUnivr/EtherSolve/blob/artifact/EtherSolve.jar)).

```bash
Usage: ethersolve [-hV] [--re-entrancy] [--tx-origin] [-o=<outputFilename>] (-c | -r) (-j | -H | -s | -d) <source>
EtherSolve, build an accurate CFG from Ethereum bytecode.
      <source>                    Bytecode string or file containing it.
  -h, --help                      Show this help message and exit.
  -V, --version                   Print version information and exit.
      --re-entrancy               Execute the Re-entrancy detector and save output.
      --tx-origin                 Execute the Tx.origin detector and save output.
  -o, --output=<outputFilename>   Output file name.
  -c, --creation                  Parse bytecode as creation code.
  -r, --runtime                   Parse bytecode as runtime code.
  -j, --json                      Export a Json report.
  -H, --html                      Export a graphic HTML report. Graphviz is required!
  -s, --svg                       Export a graphic SVG image. Graphviz is required!
  -d, --dot                       Export a dot-notation file.
```

The source can be the EVM bytecode string or a path to a file containing it. Specify if the bytecode is creation code (`-c`) or runtime code (`-r`), and the desired output type. <br>
The tool has been tested on Linux x64 with Java 11.0.8. To produce a graphical output (HTML or SVG) [Graphviz](https://graphviz.org/) is required.

To run the Re-entrancy and the Tx.origin validators add the `--re-entrancy` and the `--tx-origin` options, respectively. The tool will create a CSV file containing the detected vulnerabilities and their location in the code.

#### Examples

To analize the bytecode of a smart-contract contained in a given file, you can run following:

```bash
# Generate HTML report for creation-code source file
java -jar EtherSolve.jar -c -H path/to/bytecode/file.evm
```

```bash
# Generate JSON report in 'report.json' for creation-code source file
java -jar EtherSolve.jar -c -j -o report.json path/to/bytecode/file.evm
```

```bash
# Generate HTML report in 'index.html' for runtime-code source file
java -jar EtherSolve.jar -r -H -o index.html path/to/bytecode/file.evm
```

```bash
# Generate HTML report in 'index.html' for creation-code source file with Re-entrancy detection analysis
java -jar EtherSolve.jar -c -H -o index.html --re-entrancy path/to/bytecode/file.evm
```

### Gradle

To build from source and run the project you should use gradle (minimum Gradle supported version: *5.2.1*). To build from source **Java 8** is required.

EtherSolve is composed of five modules.
- **Core**: it contains the main procedures to parse the bytecode and build the CFG.
- **Abi**: it contains the validation methods (see the paper) and a prototype for an Abi extraction.
- **UI**: it contains the procedures to graphically represent the CFG using Graphviz and to produce the HTML report. It also contains the command line interface and the main class of the tool.
- **SecurityAnalysis**: it contains the security analyser and the dataset tester used for the comparison with the [SolidiFI](https://arxiv.org/pdf/2005.11613.pdf) survey.
- **WebApp**: it contains a simple SpringBoot server to provide analysis via APIs (unmantained).

The following Gradle tasks are available.
- `bootRun`: run the project as a Spring Boot application.
- `runCoreMain`: run the main class of the Core module, that analyses a sample bytecode written in the `Main` class, producing a JSON output.
- `runIRExtractor`: run the extractor to get the intermediate representation used for the analysis.
- `runSecurityAnalyser`: run the main class of the Core module, that analyses a sample bytecode.
- `jar`: build a JAR for each module. The only JAR having an entry point is the one of the UI module. The artifact output can be found in the `UI/build/libs` folder.

For the full list run `gradle tasks`.

## Dataset

The full repication package used for the ICPC 2021 paper can be found [here](https://github.com/SeUniVr/EtherSolve_ICPC2021_ReplicationPackage).