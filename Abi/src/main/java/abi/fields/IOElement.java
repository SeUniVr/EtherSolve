package abi.fields;

import utils.Pair;

import java.util.ArrayList;

/**
 * Class to represent an element inside a function (both input and output)
 *
 * It stores:
 * <ul>
 *     <li>Name</li>
 *     <li>Type</li>
 *     <li>Components (currently ignored)</li>
 * </ul>
 */
public class IOElement {
    /*
    name: the name of the parameter;
    type: the canonical type of the parameter (more below).
    components: used for tuple types (more below).
     */

    private final String name;
    private final SolidityType type;
    private final ArrayList<Pair<String, SolidityType>> components;

    /**
     * Default constructor with name and type. Components is initialized as an empty list
     * @param name name of the element
     * @param type type of the element
     */
    public IOElement(String name, SolidityType type){
        this.name = name;
        this.type = type;
        this.components = new ArrayList<>();
    }

    /**
     * Adds a component
     * @param component component
     */
    public void addComponent(Pair<String, SolidityType> component){
        this.components.add(component);
    }

    /**
     * Default getter
     * @return type
     */
    public SolidityType getType() {
        return type;
    }
}
