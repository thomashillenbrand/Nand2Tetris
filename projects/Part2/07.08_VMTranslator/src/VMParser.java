import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


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

  public static final String COMMENT      = "comment";
  public static final String EMPTY        = "empty";

  private BufferedReader reader;
  private String currentLine;


  /**
   * Default constructor.
   *
   * @param inputFile
   */
  public VMParser(File inputFile) throws IOException{
    this.reader = new BufferedReader(new FileReader(inputFile));
    this.currentLine = new String();

  }

  /**
   * Method to advance the parser by one line in the .vm input file.
   *
   * @throws IOException
   */
  public void advance() throws IOException{
    this.currentLine = reader.readLine();

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
  public String commandType(String currentLine){

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
  public HashMap<String, String> getArgs(String currentLine, String commandType){
    HashMap<String, String> args = new HashMap<>();
    args.put("line", currentLine);
    String[] splitLine = currentLine.split(" ", 3);

    // pop local 3
    // push temp 4
    // label LOOP_START
    // if-goto LOOP_START
    // goto LOOP_START
    // function SimpleFunction.test 2
    // return
    // call
    // add,sub,lt,gt etc

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

    }

    return args;
  }

  /**
   * Method to return a boolean indicating whether or not the parser
   * has more lines to read from the .vm input file.
   *
   * @return
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
  public boolean isArithmetic(String currentLine){
    List<String> arithmeticCommands = Arrays.asList("add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not");
    String trimmedLine = this.trimLine(currentLine);
    return arithmeticCommands.contains(trimmedLine);

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
  public String trimLine(String line){
    String[] lineSplit = line.split("//");
    line = lineSplit[0];
    line = line.trim();
    return line;

  }

  /**
   * Getters and Setters
   */

  public String getCurrentLine(){
    return this.currentLine;
  }
}
