package parseTree;

import opcodes.Opcode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Contract {
    private String name;
    private Bytecode constructor;
    private Bytecode body;

    private Cfg constructorCfg = null;
    private Cfg bodyCfg = null;

    private Set<BasicBlock> basicBlocks;

    private Contract(String name){
        this(name, new Bytecode(), new Bytecode());
    }

    public Contract(String name, String binary){
        this(name);
        Bytecode rawBytecode = BytecodeParser.getInstance().parse(binary);
        splitBytecode(rawBytecode);
    }

    private void splitBytecode(Bytecode rawBytecode) {
        this.constructor = new Bytecode();

        ArrayList<Opcode> opcodes = rawBytecode.getOpcodes();

        for (int i = 0; i<opcodes.size()-2; i++){
            if ((opcodes.get(i).getBytes().equals("6080") || opcodes.get(i).getBytes().equals("6080")) && i != 0){
                if (opcodes.get(i+1).getBytes().equals("6040")){
                    if (opcodes.get(i+2).getBytes().equals("52")){
                        this.body = new Bytecode();
                        long bodyOffset = opcodes.get(i).getOffset();
                        body.addAll(opcodes.subList(i, opcodes.size()));
                        body.forEach((o) -> o.addOffset(-bodyOffset));
                        return;
                    }
                }
            } else {
                constructor.addOpcode(opcodes.get(i));
            }
        }

    }

    public Contract(String name, Bytecode constructor, Bytecode body){
        this.name = name;
        this.constructor = constructor;
        this.body = body;
        this.basicBlocks = new HashSet<>();
    }

    @Override
    public String toString() {
        return "Constructor:\n" + constructor + "Body:\n" + body;
    }

    public String getBytes(){
        return constructor.getBytes() + body.getBytes();
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
            bodyCfg = new Cfg(body);
        return bodyCfg;
    }
}
