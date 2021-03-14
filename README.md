<img src="https://github.com/VersusF/EtherSolve/blob/master/Logo/Logo.png" alt="EtherSolve logo" width="128px" height="128px"><br>
# EtherSolve

EtherSolve is a tool for the control-flow graph extraction from Ethereum bytecode. It analyses smart contracts generated by Solidity.

## Usage

To run the tool please use the provided JAR

`java -jar EtherSolve.jar --help`

```bash
Usage: ethersolve [-hV] [-o=<outputFilename>] (-c | -r) (-j | -H | -s | -d)
                  <source>
EtherSolve, build an accurate CFG from Ethereum bytecode
      <source>     Bytecode string or file containing it
  -c, --creation   Parse bytecode as creation code
  -d, --dot        Export a dot-notation file
  -h, --help       Show this help message and exit.
  -H, --html       Export a graphic HTML report. Graphviz is required!
  -j, --json       Export a Json report
  -o, --output=<outputFilename>
                   Output file
  -r, --runtime    Parse bytecode as runtime code
  -s, --svg        Export a graphic SVG image. Graphviz is required!
  -V, --version    Print version information and exit.
```

The source can be both the bytecode or a path to a file containing it.
Specify if the bytecode is creation code or runtime code, and the desired output type.

The tool has been tested on Linux x64 with Java 11.0.8. To produce a graphical output (Html or Svg) [Graphviz](https://graphviz.org/) is required.

#### Example

The following command is used to analyse the bytecode in the `example007.evm` file as a creation code and to produce the html report in the desired file.

`java -jar EtherSolve.jar -c -H -o index.html example_1.evm`

## Dataset

The file `dataset.csv` contains the smart contracts used in the experimental validation.