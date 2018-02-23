import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The Parser class is used to parse each line of an .asm program
 * into its indivual components that can then be translated into binary.
 *
 * author: Thomas Hillenbrand
 * last update: 2/22/2018
 *
 */

public class Parser implements AutoCloseable{

  private static final String A_COMMAND = "a_command";
  private static final String C_COMMAND = "c_command";
  private static final String L_COMMAND = "l_command";

  private static final char A_PREFIX = '@';
  private static final char L_PREFIX = '(';

  private BufferedReader reader;
  private String currentLine;
  private List<String> assemblyProgramLines;
  private SymbolTable symbolTable;

  /**
   * Constructor for Parser class. Loads the input .asm file and the
   * initialized SymboleTable by default.
   *
   * @param input
   * @throws FileNotFoundException
   */

  public Parser(File input, SymbolTable symbolTable) throws FileNotFoundException {
    this.reader = new BufferedReader(new FileReader(input));
    this.assemblyProgramLines = new ArrayList<String>();
    this.currentLine = new String();
    this.symbolTable = symbolTable;

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

  public String commandType(String currentLine){
    switch (currentLine.charAt(0)) {
      case A_PREFIX:
        return A_COMMAND;
      case L_PREFIX:
        return L_COMMAND;
      default:
        return C_COMMAND;
    }

  }


  /**
   * Method to load a new .asm file into the parser.
   *
   * @param filePath
   * @throws IOException
   */

  public void loadFile(String filePath) throws Exception {
    File file = new File(filePath);
    this.close();
    this.reader = new BufferedReader(new FileReader(file));

  }

  /**
   * Method to close the parser
   *
   * @throws Exception
   */

  @Override
  public void close() throws Exception {
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


  public void test() throws Exception{

    while(this.hasMoreCommands()){
      this.advance();
      System.out.println("current line: "+this.currentLine);
      System.out.println("Current line command type: "+this.commandType(this.currentLine));
      System.out.println("++++++++++++++++++++++++++++++++++");

    }


  }

}
