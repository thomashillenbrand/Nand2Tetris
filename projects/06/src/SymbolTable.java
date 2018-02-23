import sun.awt.Symbol;

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
 * last update: 2/22/2018
 *
 */

public class SymbolTable {

    private HashMap<String, Integer> symbolMap;

    /**
     * SymbolTable constructor that will the table with all default
     * values pre-loaded.
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
     * @return
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
     * @return
     */

    public int getAddress(String symbol){

        return (this.contains(symbol)) ? this.symbolMap.get(symbol) : -1;

    }


}
