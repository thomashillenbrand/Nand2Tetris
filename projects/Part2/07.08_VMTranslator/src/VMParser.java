import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


@SuppressWarnings("JavaDoc")
public class VMParser {

  public static final String C_ARITHMETIC = "arithmetic";
  public static final String C_PUSH       = "push";
  public static final String C_POP        = "pop";
  public static final String C_LABEL      = "label";
  public static final String C_GOTO       = "goto";
  public static final String C_IF         = "if";
  public static final String C_FUNCTION   = "function";
  public static final String C_RETURN     = "return";
  public static final String C_CALL       = "call";

  private static final String COMMENT      = "comment";
  private static final String EMPTY        = "empty";

  private static final List<String> ARITHMETIC_COMMANDS =
      Arrays.asList("add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not");

  private BufferedReader reader;
  private String currentLine;


  /**
   * Constructor when provided an input file.
   *
   * @param inputFile
   */
  public VMParser(File inputFile) throws IOException{
    this.reader = new BufferedReader(new FileReader(inputFile));

  }

  /**
   * Default constructor
   */

  public VMParser(){}

  /**
   * Method to advance the parser by one line in the .vm input file.
   *
   * @throws IOException
   */
  private void advance() throws IOException{
    this.currentLine = this.reader.readLine();

  }

  /**
   * Method to determine and return a String representation of the
   * currentLine input's command type.
   *
   * NOTE: currently only looks for C_PUSH or C_ARITHMEMTIC commands
   *
   * @param currentLine
   * @return commandType of currentLine
   */
  private String commandType(String currentLine){

    if(this.isArithmetic(currentLine))          return C_ARITHMETIC;
    else if(currentLine.startsWith(C_IF))       return C_IF;
    else if(currentLine.startsWith(C_POP))      return C_POP;
    else if(currentLine.startsWith(C_PUSH))     return C_PUSH;
    else if(currentLine.startsWith(C_CALL))     return C_CALL;
    else if(currentLine.startsWith(C_GOTO))     return C_GOTO;
    else if(currentLine.startsWith(C_LABEL))    return C_LABEL;
    else if(currentLine.startsWith(C_RETURN))   return C_RETURN;
    else if(currentLine.startsWith(C_FUNCTION)) return C_FUNCTION;
    else if(currentLine.startsWith("//"))       return COMMENT;
    else                                        return EMPTY;

  }

  /**
   * Method to return the different components of the VMcode line.
   * @param currentLine
   * @param commandType
   * @return map containing the command components
   */
  private HashMap<String, String> getArgs(String currentLine, String commandType){
    HashMap<String, String> args = new HashMap<>();
    args.put("line", currentLine);
    String[] splitLine = currentLine.split(" ", 3);

    switch(commandType){
      case C_ARITHMETIC:
        args.put("arg1", splitLine[0]);
        break;
      case C_LABEL:case C_GOTO:case C_IF:
        args.put("arg1", splitLine[1]);
        break;
      case C_PUSH: case C_POP: case C_FUNCTION: case C_CALL:
        args.put("arg1", splitLine[1]);
        args.put("arg2", splitLine[2]);
        break;
      default:
        break;

    }

    return args;
  }

  /**
   * Method to return a boolean indicating whether or not the parser
   * has more lines to read from the .vm input file.
   *
   * @return boolean indicating whether there are more commands to be translated
   * @throws IOException
   */
  public boolean hasMoreCommands() throws IOException{
    return this.reader.ready();
  }

  /**
   * Method to return a boolean indicating whether a given line is an arithmetic expression.
   * Trimming the line prior to checking the arithmeticCommands list shoudl eliminate
   * trailing comments.
   * @param currentLine
   * @return
   */
  private boolean isArithmetic(String currentLine){
    String trimmedLine = this.trimLine(currentLine);
    return ARITHMETIC_COMMANDS.contains(trimmedLine);

  }

  /**
   *  Method to parse a line of .vm file into components that can be passed ot the codeRunner for translation.
   *
   * @throws IOException
   * @return
   */
  public HashMap<String, String> parseLine() throws IOException {

    HashMap<String, String> parsedLine = new HashMap<>();
    String currentLine;
    String commandType;

    this.advance();
    currentLine = this.getCurrentLine();
    commandType = this.commandType(currentLine);

    switch(commandType){
      case COMMENT: case EMPTY:
        break;
      default:
        currentLine = this.trimLine(currentLine);
        parsedLine = this.getArgs(currentLine, commandType);
    }

    parsedLine.put("commandType", commandType);
    return parsedLine;

  }

  /**
   * Method to trim any trailing comments and/or whitespace from the currently selected line.
   *
   * @param line
   * @return trimmed line
   */
  private String trimLine(String line){
    String[] lineSplit = line.split("//");
    line = lineSplit[0];
    line = line.trim();
    return line;

  }


  // ===================== Getters and Setters ===================== //

  /**
   * method to set the inputFile of the parser. When the inputFile is set,
   * a new BufferedReader object is created and the currentLine is reset.
   *
   * @param inputFile
   * @throws IOException
   */
  public void setInputFile(File inputFile) throws IOException {
    if(this.reader != null) this.reader.close();
    this.reader = new BufferedReader(new FileReader(inputFile));

  }

  /**
   * Method to get the currentLine of the parser
   * @return String currentLine
   */
  private String getCurrentLine(){
    return this.currentLine;
  }
}
