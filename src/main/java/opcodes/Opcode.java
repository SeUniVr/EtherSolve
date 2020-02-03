package opcodes;

public abstract class Opcode {
    //TODO: the next 2 fields should have the "final" modifier, once set can not be changed
    //TODO: "name" should not be a string, but a constant... we should talk about this, it is not easy to describe in one line
    protected String name;
    //TODO: this should not be of type "byte" but "enum"
    protected byte opcode;
    protected long offset;

    /**
     * Get the bytecode length of the opcode
     * @return length in byte of the opcode
     */
    public int getLength(){
        return 1;
    }


    //TODO: "input" is a misleading terminology, let's say "consumed" stack elements
    /**
     * Get the number of inputs that the opcode needs. They have to be already in the stack.
     * @return input number
     */
    public abstract int getStackInput();

    //TODO: "output" is a misleading terminology, let's say "generated" stack elements
    //TODO: are you shure that all the opcodes generate 1 value in the stack?
    /**
     * Get the number of outputs that the opcode leaves on the stack.
     * @return output number
     */
    public int getStackOutput(){
        return 1;
    }

    //TODO: add a methof "getKind" that returns a value from an enum
    public String getName() {
        return name;
    }

    //TODO: this should be "getBytes"
    public byte getOpcode() {
        return opcode;
    }

    //why don't you compute this on the fligth?
    public long getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return name;
    }
}
