import parseTree.Bytecode;

public class Main {
    public static void main(String[] args) {
        String input = "6080604052348015600f57600080fd5b5060aa60008190555060538060256000396000f3fe608060405260008054600e6014565b01905050005b6000608890509056fea2646970667358221220e73604337ca6440964bb7aaae76c90999a4ba3d1746f922f9b1534366a8051c864736f6c63430006020033";
        Bytecode bytecode = BytecodeParser.getInstance().parse(input);
        System.out.println(bytecode);
        System.out.println(input.equals(bytecode.getBytes()));
    }
}
