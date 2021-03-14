package json_utils;

public class PostRequestBody {
    private final String address;
    private final String bytecode;
    private final boolean isOnlyRuntime;
    private final String name;

    public PostRequestBody(String address, String bytecode, boolean isOnlyRuntime, String name) {
        this.address = address;
        this.bytecode = bytecode;
        this.isOnlyRuntime = isOnlyRuntime;
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public String getBytecode() {
        return bytecode;
    }

    public boolean isOnlyRuntime() {
        return isOnlyRuntime;
    }

    public String getName() {
        return name;
    }
}
