package abi.fields;

import javafx.util.Pair;

import java.util.ArrayList;

public class IOElement {
    /*
    name: the name of the parameter;
    type: the canonical type of the parameter (more below).
    components: used for tuple types (more below).
     */

    private String name;
    private SolidityType type;
    private ArrayList<Pair<String, SolidityType>> components;

    private IOElement(){}

    public IOElement(String name, SolidityType type){
        this.name = name;
        this.type = type;
        this.components = new ArrayList<>();
    }

    public void addComponent(Pair<String, SolidityType> component){
        this.components.add(component);
    }
}
