package parseTree;

import SolidityInfo.SolidityVersion;
import SolidityInfo.SolidityVersionUnknownException;

import java.util.HashSet;
import java.util.Set;

public class Contract {
    private final String name;
    private Bytecode constructor;
    private Bytecode runtime;
    private String constructorCode;
    private String runtimeCode;
    private String metadata;
    private SolidityVersion solidityVersion;

    private Cfg constructorCfg = null;
    private Cfg runtimeCfg = null;

    private final Set<BasicBlock> basicBlocks;

    private Contract(String name){
        this(name, new Bytecode(), new Bytecode());
    }

    private Contract(String name, Bytecode constructor, Bytecode body){
        this.name = name;
        this.constructor = constructor;
        this.runtime = body;
        this.basicBlocks = new HashSet<>();
        this.constructorCode = "";
        this.runtimeCode = "";
        this.metadata = "";
    }

    public Contract(String name, String binary){
        this(name);
        String remainingCode = removeCompilationInfo(binary);
        splitBytecode(remainingCode);
        constructor = BytecodeParser.getInstance().parse(constructorCode);
        runtime = BytecodeParser.getInstance().parse(runtimeCode);
    }

    /**
     * Removes the final part of the string with contains Solidity metadata
     *
     * It cycles through a list of regexp which represents the changes from version 0.4.17 on
     * @param binary the bytecode string
     * @return the stripped string
     */
    private String removeCompilationInfo(String binary) {
        // From version solc-0.4.17
        // 0xa1 0x65 'b' 'z' 'z' 'r' '0' 0x58 0x20 <32 bytes swarm hash> 0x00 0x29
        if (binary.matches("^[0-9a-f]*a165627a7a72305820[0-9a-f]{64}0029$")){
            solidityVersion = SolidityVersion.FROM_0_4_17_TO_0_5_8;
            metadata = binary.substring(binary.length() - 86);
            return binary.substring(0, binary.length() - 86);
        }

        // From version solc-0.5.9
        // 0xa2
        // 0x65 'b' 'z' 'z' 'r' '0' 0x58 0x20 <32 bytes swarm hash>
        // 0x64 's' 'o' 'l' 'c' 0x43 <3 byte version encoding>
        // 0x00 0x32
        else if (binary.matches("^[0-9a-f]*a265627a7a72305820[0-9a-f]{64}64736f6c6343[0-9a-f]{6}0032$")){
            solidityVersion = SolidityVersion.FROM_0_5_9_TO_0_5_11;
            metadata = binary.substring(binary.length() - 104);
            return binary.substring(0, binary.length() - 104);
        }

        // From version solc-0.5.12
        // 0xa2
        // 0x65 'b' 'z' 'z' 'r' '1' 0x58 0x20 <32 bytes swarm hash>
        // 0x64 's' 'o' 'l' 'c' 0x43 <3 byte version encoding>
        // 0x00 0x32
        else if (binary.matches("^[0-9a-f]*a265627a7a72315820[0-9a-f]{64}64736f6c6343[0-9a-f]{6}0032$")){
            solidityVersion = SolidityVersion.FROM_0_5_12_TO_0_5_15;
            metadata = binary.substring(binary.length() - 104);
            return binary.substring(0, binary.length() - 104);
        }

        // From version solc-0.6.0
        // 0xa2
        // 0x64 'i' 'p' 'f' 's' 0x58 0x22 <34 bytes IPFS hash>
        // 0x64 's' 'o' 'l' 'c' 0x43 <3 byte version encoding>
        // 0x00 0x32
        else if (binary.matches("^[0-9a-f]*a264697066735822[0-9a-f]{68}64736f6c6343[0-9a-f]{6}0032$")){
            solidityVersion = SolidityVersion.FROM_0_6_0_TO_0_6_1;
            metadata = binary.substring(binary.length() - 106);
            return binary.substring(0, binary.length() - 106);
        }

        // From version solc-0.6.2
        // 0xa2
        // 0x64 'i' 'p' 'f' 's' 0x58 0x22 <34 bytes IPFS hash>
        // 0x64 's' 'o' 'l' 'c' 0x43 <3 byte version encoding>
        // 0x00 0x33
        else if (binary.matches("^[0-9a-f]*a264697066735822[0-9a-f]{68}64736f6c6343[0-9a-f]{6}0033$")){
            solidityVersion = SolidityVersion.FROM_0_6_2_TO_LATEST;
            metadata = binary.substring(binary.length() - 106);
            return binary.substring(0, binary.length() - 106);
        }

        solidityVersion = SolidityVersion.UNKNOWN;
        return binary;
    }

    /**
     * Splits the code between constructor and runtime
     *
     * It splits the string using the patter 6060604052 or 6080604052
     * @param binary complete string
     */
    private void splitBytecode(String binary) {
        this.constructor = new Bytecode();
        // 6080604052 or 6060604052
        // ?= means "keep the regexp on te found match"
        String[] temp = binary.split("(?=60(60|80)604052)", 2);
        constructorCode = temp[0];
        runtimeCode = temp[1];
    }

    @Override
    public String toString() {
        return "Constructor:\n" + constructor + "Body:\n" + runtime;
    }

    public String getBytes(){
        // TODO big as a house: calculate contract medatata
        return constructor.getBytes() + runtime.getBytes();
    }

    public Set<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    public String getName() {
        return this.name;
    }

    /**
     * Builds the cfg and updates the bytecode if it founds any other extra data
     * @return constructor cfg
     */
    public Cfg getConstructorCfg() {
        if (constructorCfg == null)
            constructorCfg = new Cfg(constructor);
        constructor = constructorCfg.getBytecode();
        return constructorCfg;
    }

    /**
     * Builds the cfg and updates the bytecode if it founds any other extra data
     * @return runtime cfg
     */
    public Cfg getRuntimeCfg() {
        if (runtimeCfg == null)
            runtimeCfg = new Cfg(runtime);
        runtime = runtimeCfg.getBytecode();
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
        v1 = Integer.parseInt(version.substring(0,2));
        v2 = Integer.parseInt(version.substring(2,4));
        v3 = Integer.parseInt(version.substring(4));
        return String.valueOf(v1) + '.' + v2 + '.' + v3;
    }

    public Bytecode getConstructor() {
        return constructor;
    }

    public Bytecode getRuntime() {
        return runtime;
    }
}
