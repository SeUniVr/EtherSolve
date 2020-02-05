package parseTree;

import java.util.HashSet;
import java.util.Set;

public class Contract {
    private String name;
    private Set<Field> fields;
    private Set<Function> functions;

    public Contract(String name){
        this.name = name;
        this.fields = new HashSet<>();
        this.functions = new HashSet<>();
    }
}
