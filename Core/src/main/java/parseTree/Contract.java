package parseTree;

import SolidityInfo.SolidityVersion;
import SolidityInfo.SolidityVersionUnknownException;
import utils.Pair;

public class Contract {
    private final String name;
    private final String address;
    private final String binarySource;
    private final String contractHash;
    private final boolean isOnlyRuntime;
    private String metadata;
    private SolidityVersion solidityVersion;
    private final String constructorRemainingData;

    private final Cfg constructorCfg;
    private final Cfg runtimeCfg;

    public Contract(String name, String binary, boolean isOnlyRuntime, String address) throws NotSolidityContractException {
        if (! binary.matches("^60(60|80)604052[0-9a-fA-F]*$"))
            throw new NotSolidityContractException();
        this.name = name;
        this.address = address;
        this.binarySource = binary;
        this.contractHash = String.format("%x", binary.hashCode());
        this.isOnlyRuntime = isOnlyRuntime;
        Pair<String, String> strippedCode = removeCompilationInfo(binary);
        constructorRemainingData = strippedCode.getValue();
        String remainingCode = strippedCode.getKey();
        String constructor, runtime;
        if (! isOnlyRuntime){
            String[] splitCode = remainingCode.split("(?=60(60|80)604052)", 2);
            constructor = splitCode[0];
            runtime = splitCode[1];
        } else {
            constructor = "";
            runtime = remainingCode;
        }
        constructorCfg = CfgBuilder.buildCfg(constructor);
        runtimeCfg = CfgBuilder.buildCfg(runtime);
    }

    public Contract(String name, String binary) throws NotSolidityContractException {
        this(name, binary, false);
    }

    public Contract(String name, String binary, boolean isOnlyRuntime) throws NotSolidityContractException {
        this(name, binary, isOnlyRuntime, "");
    }

    /**
     * Removes the final part of the string with contains Solidity metadata and constructor remaining data
     *
     * It cycles through a list of regexp which represents the changes from version 0.4.17 on
     * @param binary the bytecode string
     * @return the stripped string and the constructor remaining data
     */
    private Pair<String, String> removeCompilationInfo(String binary) {
        // From version solc-0.4.17
        // 0xa1 0x65 'b' 'z' 'z' 'r' '0' 0x58 0x20 <32 bytes swarm hash> 0x00 0x29
        if (binary.matches("^[0-9a-f]*a165627a7a72305820[0-9a-f]{64}0029[0-9a-f]*$")){
            solidityVersion = SolidityVersion.FROM_0_4_17_TO_0_5_8;
            String[] splitString = binary.split("(?<=a165627a7a72305820[0-9a-f]{64}0029)", 2);
            metadata = binary.substring(splitString[0].length() - 86);
            return new Pair<>(splitString[0].substring(0, splitString[0].length() - 86), splitString[1]);
        }

        // From version solc-0.5.9
        // 0xa2
        // 0x65 'b' 'z' 'z' 'r' '0' 0x58 0x20 <32 bytes swarm hash>
        // 0x64 's' 'o' 'l' 'c' 0x43 <3 byte version encoding>
        // 0x00 0x32
        else if (binary.matches("^[0-9a-f]*a265627a7a72305820[0-9a-f]{64}64736f6c6343[0-9a-f]{6}0032[0-9a-f]*$")){
            solidityVersion = SolidityVersion.FROM_0_5_9_TO_0_5_11;
            String[] splitString = binary.split("(?<=a265627a7a72305820[0-9a-f]{64}64736f6c6343[0-9a-f]{6}0032)", 2);
            metadata = splitString[0].substring(splitString[0].length() - 104);
            return new Pair<>(splitString[0].substring(0, splitString[0].length() - 104), splitString[1]);
        }

        // From version solc-0.5.12
        // 0xa2
        // 0x65 'b' 'z' 'z' 'r' '1' 0x58 0x20 <32 bytes swarm hash>
        // 0x64 's' 'o' 'l' 'c' 0x43 <3 byte version encoding>
        // 0x00 0x32
        else if (binary.matches("^[0-9a-f]*a265627a7a72315820[0-9a-f]{64}64736f6c6343[0-9a-f]{6}0032[0-9a-f]*$")){
            solidityVersion = SolidityVersion.FROM_0_5_12_TO_0_5_15;
            String[] splitString = binary.split("(?<=a265627a7a72315820[0-9a-f]{64}64736f6c6343[0-9a-f]{6}0032)", 2);
            metadata = splitString[0].substring(splitString[0].length() - 104);
            return new Pair<>(splitString[0].substring(0, splitString[0].length() - 104), splitString[1]);
        }

        // From version solc-0.6.0
        // 0xa2
        // 0x64 'i' 'p' 'f' 's' 0x58 0x22 <34 bytes IPFS hash>
        // 0x64 's' 'o' 'l' 'c' 0x43 <3 byte version encoding>
        // 0x00 0x32
        else if (binary.matches("^[0-9a-f]*a264697066735822[0-9a-f]{68}64736f6c6343[0-9a-f]{6}0032[0-9a-f]*$")){
            solidityVersion = SolidityVersion.FROM_0_6_0_TO_0_6_1;
            String[] splitString = binary.split("(?<=a264697066735822[0-9a-f]{68}64736f6c6343[0-9a-f]{6}0032)", 2);
            metadata = splitString[0].substring(splitString[0].length() - 106);
            return new Pair<>(splitString[0].substring(0, splitString[0].length() - 106), splitString[1]);
        }

        // From version solc-0.6.2
        // 0xa2
        // 0x64 'i' 'p' 'f' 's' 0x58 0x22 <34 bytes IPFS hash>
        // 0x64 's' 'o' 'l' 'c' 0x43 <3 byte version encoding>
        // 0x00 0x33
        else if (binary.matches("^[0-9a-f]*a264697066735822[0-9a-f]{68}64736f6c6343[0-9a-f]{6}0033[0-9a-f]*$")){
            solidityVersion = SolidityVersion.FROM_0_6_2_TO_LATEST;
            String[] splitString = binary.split("(?<=a264697066735822[0-9a-f]{68}64736f6c6343[0-9a-f]{6}0033)", 2);
            metadata = splitString[0].substring(splitString[0].length() - 106);
            return new Pair<>(splitString[0].substring(0, splitString[0].length() - 106), splitString[1]);
        }

        solidityVersion = SolidityVersion.UNKNOWN;
        return new Pair<>(binary, "");
    }

    @Override
    public String toString() {
        return name + "\nCode: " + binarySource;
    }

    public String getBytes(){
        return binarySource;
    }

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return address;
    }

    public String getBinarySource() {
        return binarySource;
    }

    public String getContractHash() {
        return contractHash;
    }

    public boolean isOnlyRuntime() {
        return isOnlyRuntime;
    }

    public String getMetadata() {
        return metadata;
    }

    /**
     * Builds the cfg and updates the bytecode if it founds any other extra data
     * @return constructor cfg
     */
    public Cfg getConstructorCfg() {
        return constructorCfg;
    }

    /**
     * Builds the cfg and updates the bytecode if it founds any other extra data
     * @return runtime cfg
     */
    public Cfg getRuntimeCfg() {
        return runtimeCfg;
    }

    public SolidityVersion getSolidityVersion() {
        return solidityVersion;
    }

    /**
     * Parse the contract metadata trying to reconstruct the solidity compiler version
     * @return A string with the version
     * @throws SolidityVersionUnknownException if the version is under 0.4.17 then it's unresolvable
     */
    public String getExactSolidityVersion() throws SolidityVersionUnknownException {
        if (solidityVersion == SolidityVersion.UNKNOWN || solidityVersion == SolidityVersion.FROM_0_4_17_TO_0_5_8)
            throw new SolidityVersionUnknownException("Version unknown or before 0.5.9");
        String version = metadata.substring(metadata.length() - 10, metadata.length() - 4);
        int v1, v2, v3;
        v1 = Integer.parseInt(version.substring(0,2), 16);
        v2 = Integer.parseInt(version.substring(2,4), 16);
        v3 = Integer.parseInt(version.substring(4), 16);
        return String.valueOf(v1) + '.' + v2 + '.' + v3;
    }

    public Bytecode getConstructor() {
        return constructorCfg.getBytecode();
    }

    public Bytecode getRuntime() {
        return runtimeCfg.getBytecode();
    }

    public String getConstructorRemainingData() {
        return constructorRemainingData;
    }
}
