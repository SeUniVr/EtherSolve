package parseTree;

import java.util.HashSet;
import java.util.Set;

public class Contract {
    private final String name;
    private Bytecode constructor;
    private Bytecode runtime;
    private String constructorCode;
    private String runtimeCode;
    private String metadata;

    private Cfg constructorCfg = null;
    private Cfg bodyCfg = null;

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

    private String removeCompilationInfo(String binary) {
        // From version solc-0.4.17
        // 0xa1 0x65 'b' 'z' 'z' 'r' '0' 0x58 0x20 <32 bytes swarm hash> 0x00 0x29

        if (binary.matches("^[0-9a-f]*a165627a7a72305820[0-9a-f]{64}0029$")){
            metadata = binary.substring(binary.length() - 86);
            return binary.substring(0, binary.length() - 86);
        }

        // From version solc-0.5.19
        // 0xa2
        // 0x65 'b' 'z' 'z' 'r' '0' 0x58 0x20 <32 bytes swarm hash>
        // 0x64 's' 'o' 'l' 'c' 0x43 <3 byte version encoding>
        // 0x00 0x32
        else if (binary.matches("^[0-9a-f]*a264697066735822[0-9a-f]{68}64736f6c6343[0-9a-f]{6}0033$")){
            metadata = binary.substring(binary.length() - 110);
            return binary.substring(0, binary.length() - 110);
        }

        return binary;
    }

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

    public Cfg getConstructorCfg() {
        if (constructorCfg == null)
            constructorCfg = new Cfg(constructor);
        return constructorCfg;
    }

    public Cfg getBodyCfg() {
        if (bodyCfg == null)
            bodyCfg = new Cfg(runtime);
        return bodyCfg;
    }
}
