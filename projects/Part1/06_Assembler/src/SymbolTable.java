import java.util.HashMap;

/**
 * The SymbolTable class is a data structure to keep track of the symbols used
 * within an .asm program. The table is initialized on runtime, and
 * values can be added or retrieved.
 *
 * Key   = symbol String
 * Value = address integer value
 *
 *
 * author: Thomas Hillenbrand
 *
 */

public class SymbolTable {

    private int currentMemLoc;
    private HashMap<String, Integer> symbolMap;

    /**
     * SymbolTable constructor that will the table with all default
     * values pre-loaded.
     *
     * Initializes first available memory location value ot be 16.
     *
     */

    public SymbolTable(){
        this.symbolMap = new HashMap<String, Integer>();
        for(int i=0; i<16; i++) {
            addEntry(("R"+ i), i);
        }
        addEntry("SP", 0);
        addEntry("LCL", 1);
        addEntry("ARG", 2);
        addEntry("THIS", 3);
        addEntry("THAT", 4);
        addEntry("SCREEN", 16384);
        addEntry("KBD", 24576);

        this.currentMemLoc = 16;

    }

    /**
     * Method to add an entry to the symbol table
     *
     * @param symbol
     * @param address
     */

    public void addEntry(String symbol, int address){

        this.symbolMap.put(symbol, address);

    }

    /**
     * Method to check if the symbol table already contains a symbol value.
     *
     * @param symbol
     * @return boolean indicating whether the symboleTable contains an address
     *         for the input symbol.
     */

    public boolean contains(String symbol){

        return this.symbolMap.containsKey(symbol);

    }

    /**
     * Method to return the numerical address (>=0) associated
     * with a symbol string. If there is no address associated
     * with the input symbol, then -1 is returned.
     *
     * @param symbol
     * @return integer value of the address of the input symbol.
     */

    public int getAddress(String symbol){

        return (this.contains(symbol)) ? this.symbolMap.get(symbol) : -1;

    }


    /**
     * to String Method for the Symbole Table
     *
     * @return String representation of the SymbolTable contents.
     */

    public String toString(){
        StringBuffer toString = new StringBuffer();
        for(String key : symbolMap.keySet()){
            toString = toString.append(key);
            toString = toString.append(" : ");
            toString = toString.append(symbolMap.get(key));
            toString = toString.append("\n");

        }


        return toString.toString();
    }

    /**
     * Method to get the address of the next available memory location.
     *
     * @return integer value of the memory location
     */
    public int getCurrentMemLoc(){
        return this.currentMemLoc;
    }

    /**
     * Method to increment the next available memory location by one.
     */
    public void advCurrentMemLoc(){
        this.currentMemLoc++;
    }


}
