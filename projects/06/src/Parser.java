import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The Parser class is used to parse each line of an .asm program
 * into its indivual components that can then be translated into binary.
 *
 * author: Thomas Hillenbrand
 * last update: 2/22/2018
 *
 */

public class Parser implements AutoCloseable{

    private static final String CMNT_PREFIX   = "//";
    private static final String A_PREFIX      = "@";
    private static final String L_PREFIX      = "(";
    private static final String A_COMMAND     = "a_command";
    private static final String C_COMMAND     = "c_command";
    private static final String L_COMMAND     = "l_command";
    private static final String COMMENT       = "comment";
    private static final String EMPTY         = "empty";

    private BufferedReader reader;
    private String currentLine;
    private List<String> assemblyProgramLines;

    /**
     * Constructor for Parser class. Loads the input .asm file and the
     * initialized SymboleTable by default.
     *
     * @param input
     * @throws FileNotFoundException
     */

    public Parser(File input) throws FileNotFoundException {
        this.reader = new BufferedReader(new FileReader(input));
        this.assemblyProgramLines = new ArrayList<String>();
        this.currentLine = new String();

    }

    /**
     * Method to return a boolean indicating whether or not the
     * pareser has more lines ot read fom the .asm file.
     *
     * @return
     * @throws IOException
     */
    public boolean hasMoreCommands() throws IOException {
        return this.reader.ready();
    }

    /**
     * Method to advance the parser by one line in the .asm file
     * @throws IOException
     */

    public void advance() throws IOException {
        this.currentLine = reader.readLine();
    }

    /**
     *  Method to return the type of instruction line the currentLine is
     *
     * @param currentLine
     * @return
     */

    public String getCommandType(String currentLine){

        if(currentLine.isEmpty()) return EMPTY;
        else if(currentLine.startsWith(CMNT_PREFIX)) return COMMENT;
        else if(currentLine.startsWith(A_PREFIX)) return A_COMMAND;
        else if(currentLine.startsWith(L_PREFIX)) return L_COMMAND;
        else return C_COMMAND;

    }

    /**
     * Method to trim an instruction line of whitespaces and comments
     *
     * @param line
     * @return
     */

    public String trimInstruction(String line){
        line = line.trim();
        String[] splitLine = line.split(" ");
        return splitLine[0];

    }

    /**
     * Method to trim L_Command lines into the relevant symbole value to be
     * added to the symbole table.
     * @param line
     * @return
     */

    public String trimJumpLabel(String line){
        line=line.trim();
        line=line.substring(1, line.length()-1);

        return line;

    }

    /**
     * This method should make the parser read thru the .asm file for the first
     * time. On the first pass the parser should:
     *  1. Strip out all comment lines and blank lines
     *  2. Add each (XXX) L_Command to the symbol table (symbole and address)
     *  3. Trim each instruction line of white space and comments, add line to
     *     instruction list.
     *
     * End result is the assemblyProgramLines list that will be translated.
     * @param symboleTable
     * @throws IOException
     */
    public void firstPass(SymbolTable symboleTable) throws IOException {
        Objects.requireNonNull(symboleTable);
        int nextAddress;
        String line;
        String commandType;

        while(this.hasMoreCommands()){
            nextAddress = this.assemblyProgramLines.size();
            this.advance();
            line = this.getCurrentLine();

            commandType = this.getCommandType(line);
            if(commandType.equals(C_COMMAND) || commandType.equals(A_COMMAND)){
                line = this.trimInstruction(line);
                this.assemblyProgramLines.add(line);

            }
            else if(commandType.equals(L_COMMAND)){
                line = this.trimJumpLabel(line);
                if(!symboleTable.contains(line)) symboleTable.addEntry(line, nextAddress);

            }

        }

    }


    public void secondPass(SymbolTable symbolTable){


    }

    /**
     * Method to close the parser
     *
     * @throws IOException
     */

    @Override
    public void close() throws IOException {
        this.reader.close();
    }


    /**
     * Getters and Setters
     */

    public String getCurrentLine(){
        return this.currentLine;
    }

    public void setCurrentLine(String line){
        this.currentLine = line;
    }

    public List<String> getAssemblyProgramLines() {
        return this.assemblyProgramLines;
    }

    public void setAssemblyProgramLines(List<String> assemblyProgramLines) {
        this.assemblyProgramLines = assemblyProgramLines;
    }
}
