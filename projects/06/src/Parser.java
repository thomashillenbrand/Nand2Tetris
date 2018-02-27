import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

    private static final String DEST_SYMBOL   = "=";
    private static final String JMP_SYMBOL   = ";";
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
    private boolean hasMoreCommands() throws IOException {
        return this.reader.ready();
    }

    /**
     * Method to advance the parser by one line in the .asm file
     * @throws IOException
     */

    private void advance() throws IOException {
        this.currentLine = reader.readLine();
    }

    /**
     *  Method to return the type of instruction line the currentLine is
     *
     * @param currentLine
     * @return
     */

    private String getCommandType(String currentLine){

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

    private String trimInstruction(String line){
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

    private String trimJumpLabel(String line){
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
            switch(commandType){
                case C_COMMAND: case A_COMMAND:
                    line = this.trimInstruction(line);
                    this.assemblyProgramLines.add(line);
                    break;
                case L_COMMAND:
                    line = this.trimJumpLabel(line);
                    if(!symboleTable.contains(line)) symboleTable.addEntry(line, nextAddress);
                    break;
            }

        }

    }

    /**
     * This method causes the parser to do its second pass over the
     * assembly program. This method will:
     *   1. Read each line from the assemblyProgramLines list and
     *      classify it as an A or C command.
     *   2. If its an A command, indicate as such and pass it to the Translator
     *   3. If its a C command, parse the line into its components and pass it
     *      to the Translator
     *
     * @param symbolTable
     */

    public void secondPass(SymbolTable symbolTable) throws IOException {
        String commandType;
        String outPutFilePath = HackAssembler.FILE_PATH.replaceAll(".asm", ".hack");

        Translator t = new Translator(outPutFilePath);
        System.out.println(this.assemblyProgramLines.toString());

        for(String line : this.assemblyProgramLines){
            commandType = this.getCommandType(line);
            System.out.println("Pre-parse Line: "+line);
            System.out.println("Type: "+commandType);

            switch(commandType){
                case A_COMMAND:
                    line = this.parseA(line, symbolTable);
                    t.translateA(line);
                    break;
                case C_COMMAND:
                    HashMap<String, String> cMnemonics = this.parseC(line);
                    t.translateC(cMnemonics);
                    System.out.println(cMnemonics.toString());
                    break;
            }
            System.out.println("Post-parse line: "+line);
            System.out.println("=======================================");

        }

    }

    /**
     * Method to parse an a-instruction. First the leading "@" is trimmed off. Next, we determine if the a-instruction
     * provides a numerical address or a symbol. If its a numerical address, we return the line as-is. If its a symbol,
     * we check for its existence in the symbolTable, give it the next available address in memory if it doesn't exist,
     * and return the corresponding numerical address.
     *
     * @param line
     * @param symbolTable
     * @return String representation of the numerical address specified by the instruction.
     */
    private String parseA(String line, SymbolTable symbolTable) {
        line = line.substring(1, line.length()); //remove the @
        int currentMemLoc = symbolTable.getCurrentMemLoc();

        try{
            Integer.parseInt(line);

        } catch(NumberFormatException e){
            if(symbolTable.contains(line)) line = Integer.toString(symbolTable.getAddress(line));
            else{
                symbolTable.addEntry(line, currentMemLoc);
                line = Integer.toString(currentMemLoc);
                symbolTable.advCurrentMemLoc();

            }

            return line;
        }

        return line;
    }

    /**
     * Method to parse a c-instruction into its indivual components, i.e. DEST, COMP, and JMP.
     *
     * @param line
     * @return Map with the key/value pairs being hte mnemonics and their values.
     */
    private HashMap parseC(String line) {
        HashMap<String, String> cMnemonics = new HashMap<String, String>();
        boolean hasDest = line.contains(DEST_SYMBOL);
        boolean hasJmp = line.contains(JMP_SYMBOL);


        if(hasDest){
            int index = line.indexOf("=");
            cMnemonics.put("dest", line.substring(0, index));
            cMnemonics.put("comp", line.substring(index+1, line.length()));
        }
        if(hasJmp){
            int index = line.indexOf(";");
            cMnemonics.put("jmp", line.substring(index+1, line.length()));
            cMnemonics.put("comp", line.substring(0, index));
        }

        return cMnemonics;
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
