package parseTree;

import SolidityInfo.SolidityVersion;
import SolidityInfo.SolidityVersionUnknownException;
import parseTree.cfg.Cfg;
import parseTree.cfg.CfgBuilder;
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

    /**
     * Creates a contract with the given inputs.
     *
     * The bytecode is divided between metadata, constructor, runtime and remaining data. Then the cfg is built.
     * @param name contract name
     * @param binary hexadecimal string with the bytecode
     * @param isOnlyRuntime if it is only runtime code or creation code
     * @param address address of the contract if it's deployed in the blockchain
     * @throws NotSolidityContractException If the bytecode has not been generated by solc
     */
    public Contract(String name, String binary, boolean isOnlyRuntime, String address) throws NotSolidityContractException {
        // The first part is for the libraries (73<address>3014)
        if (! binary.matches("^(73[0-9a-fA-F]{40}3014)?60(60|80)604052[0-9a-fA-F]*$"))
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
        // 0xa1
        // 0x65 'b' 'z' 'z' 'r' '0' 0x58 0x20 <32 bytes swarm hash>
        // 0x00 0x29
        if (binary.matches("^[0-9a-f]*a165627a7a72305820[0-9a-f]{64}0029[0-9a-f]*$")){
            solidityVersion = SolidityVersion.FROM_0_4_17_TO_0_5_8;
            String[] splitString = binary.split("(?<=a165627a7a72305820[0-9a-f]{64}0029)", 2);
            metadata = binary.substring(splitString[0].length() - 86);
            return new Pair<>(splitString[0].substring(0, splitString[0].length() - 86), splitString[1]);
        }
        // Experimental option in Solidity due to ABIEncoderV2
        // example: a265627a7a723058201e1bfc77d507025cf70760b0848f01673dd1fb26af9d47b555da548df16224066c6578706572696d656e74616cf50037
        // 0xa2
        // 0x65 'b' 'z' 'z' 'r' '0' 0x58 0x20 <32 bytes swarm hash>
        // 0x6c 'e' 'x' 'p' 'e' 'r' 'i' 'm' 'e' 'n' 't' 'a' 'l' 0xf5
        // 0x00 0x37
        else if (binary.matches("^[0-9a-f]*a265627a7a72305820[0-9a-f]{64}6c6578706572696d656e74616cf50037[0-9a-f]*$")){
            solidityVersion = SolidityVersion.FROM_0_4_17_TO_0_5_8_EXPERIMENTAL;
            String[] splitString = binary.split("(?<=a265627a7a72305820[0-9a-f]{64}6c6578706572696d656e74616cf50037)", 2);
            metadata = binary.substring(splitString[0].length() - 114);
            return new Pair<>(splitString[0].substring(0, splitString[0].length() - 114), splitString[1]);
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
        // Experimental option in Solidity due to ABIEncoderV2
        // example: a365627a7a7230582022316da6de015a68fad6ca8a732898f553832e95b48e9f39b85fe694b2264db26c6578706572696d656e74616cf564736f6c634300050a0040
        // 0xa3
        // 0x65 'b' 'z' 'z' 'r' '0' 0x58 0x20 <32 bytes swarm hash>
        // 0x6c 'e' 'x' 'p' 'e' 'r' 'i' 'm' 'e' 'n' 't' 'a' 'l' 0xf5
        // 0x64 's' 'o' 'l' 'c' 0x43 <3 byte version encoding>
        // 0x00 0x40
        else if (binary.matches("^[0-9a-f]*a365627a7a72305820[0-9a-f]{64}6c6578706572696d656e74616cf564736f6c6343[0-9a-f]{6}0040[0-9a-f]*$")){
            solidityVersion = SolidityVersion.FROM_0_5_9_TO_0_5_11_EXPERIMENTAL;
            String[] splitString = binary.split("(?<=a365627a7a72305820[0-9a-f]{64}6c6578706572696d656e74616cf564736f6c6343[0-9a-f]{6}0040)", 2);
            metadata = splitString[0].substring(splitString[0].length() - 132);
            return new Pair<>(splitString[0].substring(0, splitString[0].length() - 132), splitString[1]);
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
        // Experimental option in Solidity due to ABIEncoderV2
        // example: a365627a7a7231582076f04f08ed9ab2d9078ead8a728e5e444700aed42abb0cd3bd94a1ae5612d38f6c6578706572696d656e74616cf564736f6c63430005110040
        // 0xa3
        // 0x65 'b' 'z' 'z' 'r' '1' 0x58 0x20 <32 bytes swarm hash>
        // 0x6c 'e' 'x' 'p' 'e' 'r' 'i' 'm' 'e' 'n' 't' 'a' 'l' 0xf5
        // 0x64 's' 'o' 'l' 'c' 0x43 <3 byte version encoding>
        // 0x00 0x40
        else if (binary.matches("^[0-9a-f]*a365627a7a72315820[0-9a-f]{64}6c6578706572696d656e74616cf564736f6c6343[0-9a-f]{6}0040[0-9a-f]*$")){
            solidityVersion = SolidityVersion.FROM_0_5_12_TO_0_5_15_EXPERIMENTAL;
            String[] splitString = binary.split("(?<=a365627a7a72315820[0-9a-f]{64}6c6578706572696d656e74616cf564736f6c6343[0-9a-f]{6}0040)", 2);
            metadata = splitString[0].substring(splitString[0].length() - 132);
            return new Pair<>(splitString[0].substring(0, splitString[0].length() - 132), splitString[1]);
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

    /**
     * Default printer with name and code
     * @return a string with name and code
     */
    @Override
    public String toString() {
        return name + "\nCode: " + binarySource;
    }

    /**
     * Default getter
     * @return binary source code
     */
    public String getBytes(){
        return binarySource;
    }

    /**
     * Default getter
     * @return contract name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Default getter
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Default getter
     * @return binary source code
     */
    public String getBinarySource() {
        return binarySource;
    }

    /**
     * Default getter. The contract hash is the hashcode of the binary source code
     * @return contract hash
     */
    public String getContractHash() {
        return contractHash;
    }

    /**
     * Default getter
     * @return if the contract has only runtime code or creation code
     */
    public boolean isOnlyRuntime() {
        return isOnlyRuntime;
    }

    /**
     * Default getter
     * @return metadata
     */
    public String getMetadata() {
        return metadata;
    }

    /**
     * Default getter
     * @return constructor cfg
     */
    public Cfg getConstructorCfg() {
        return constructorCfg;
    }

    /**
     * Default getter
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
     * @throws SolidityVersionUnknownException if the version is under 0.5.8 then it's unresolvable
     */
    public String getExactSolidityVersion() throws SolidityVersionUnknownException {
        if (solidityVersion == SolidityVersion.UNKNOWN ||
                solidityVersion == SolidityVersion.FROM_0_4_17_TO_0_5_8 ||
                solidityVersion == SolidityVersion.FROM_0_4_17_TO_0_5_8_EXPERIMENTAL)
            throw new SolidityVersionUnknownException("Version unknown or before 0.5.9");
        String version = metadata.substring(metadata.length() - 10, metadata.length() - 4);
        int v1, v2, v3;
        v1 = Integer.parseInt(version.substring(0,2), 16);
        v2 = Integer.parseInt(version.substring(2,4), 16);
        v3 = Integer.parseInt(version.substring(4), 16);
        String experimental = "";
        if (solidityVersion == SolidityVersion.FROM_0_4_17_TO_0_5_8_EXPERIMENTAL ||
                solidityVersion == SolidityVersion.FROM_0_5_9_TO_0_5_11_EXPERIMENTAL ||
                solidityVersion == SolidityVersion.FROM_0_5_12_TO_0_5_15_EXPERIMENTAL)
            experimental = " - experimental";
        return String.valueOf(v1) + '.' + v2 + '.' + v3 + experimental;
    }

    /**
     * Default getter
     * @return bytecode of the constructor
     */
    public Bytecode getConstructor() {
        return constructorCfg.getBytecode();
    }

    /**
     * Default getter
     * @return bytecode of the runtime
     */
    public Bytecode getRuntime() {
        return runtimeCfg.getBytecode();
    }

    /**
     * Default getter
     * @return constructor remaining data
     */
    public String getConstructorRemainingData() {
        return constructorRemainingData;
    }
}
