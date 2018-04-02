import com.sun.org.apache.xpath.internal.SourceTree;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class VMCodeRunner implements AutoCloseable{


  /**
   * Outstanding to do list:
   *  1. Implement Call translation
   *  2. Implement Return translation
   *  3. Implement Funtion translation
   *  4. Implement Init function
   *
   */

  private static final String ARG1         = "arg1";
  private static final String ARG2         = "arg2";
  private static final String LINE         = "line";
  private static final String COMMAND_TYPE = "commandType";

  private static final String ADD          = "add";
  private static final String SUB          = "sub";
  private static final String NEG          = "neg";
  private static final String EQ           = "eq";
  private static final String GT           = "gt";
  private static final String LT           = "lt";
  private static final String AND          = "and";
  private static final String OR           = "or";
  private static final String NOT          = "not";

  private static final String CONSTANT = "constant";
  private static final String LOCAL    = "local";
  private static final String ARGUMENT = "argument";
  private static final String THIS     = "this";
  private static final String THAT     = "that";
  private static final String STATIC   = "static";
  private static final String TEMP     = "temp";
  private static final String POINTER  = "pointer";

  private int eqIndex;
  private String currentFileName;
  private StringBuffer sb;
  private BufferedWriter writer;

  public VMCodeRunner(File outputFile) throws IOException {
    this.writer = new BufferedWriter(new FileWriter(outputFile));
    this.sb = new StringBuffer();
    this.eqIndex = 0;

  }

  /**
   * Default constructor
   *
   */
  public VMCodeRunner(){}

  /**
   * Method to write a given string to the output file.
   *
   * @param code
   * @throws IOException
   */
  public void write(String code) throws IOException {
    this.writer.write(code);
    this.writer.flush();

  }

  /**
   * Method to translate a given parsedLine Map into asm and
   * then write it to the output file.
   *
   * @param parsedLine
   * @throws IOException
   */

  public void writeCommand(HashMap<String, String> parsedLine) throws IOException {

    this.sb.setLength(0);

    switch(parsedLine.get(COMMAND_TYPE)){
      case VMParser.C_ARITHMETIC:
        this.sb = buildArithmeticCommand(parsedLine);
        break;
      case VMParser.C_PUSH: case VMParser.C_POP:
        this.sb = buildPushPopCommand(parsedLine);
        break;
      case VMParser.C_LABEL:
        this.sb = buildLabelCommand(parsedLine);
        break;
      case VMParser.C_GOTO:
        this.sb = buildGoToCommand(parsedLine);
        break;
      case VMParser.C_IF:
        this.sb = buildIfCommand(parsedLine);
        break;
      case VMParser.C_CALL:
        this.sb = buildCallCommand(parsedLine);
        break;
      case VMParser.C_FUNCTION:
        this.sb = buildFunctionCommand(parsedLine);
        break;
      case VMParser.C_RETURN:
        this.sb = buildReturnCommand(parsedLine);
        break;
      default:
        break;
    }

    String code = this.sb.toString();
    this.write(code);
  }

  /**
   * Method to build an arithmetic command in assembly to write to the output file.
   *
   * @param parsedLine
   * @return this.sb containing the translated arithmetic command.
   */
  private StringBuffer buildArithmeticCommand(HashMap<String, String> parsedLine){
    String operation = parsedLine.get(LINE);
    this.sb.append("// "+operation+"\n");

    switch(operation){
      case ADD: case SUB: case AND: case OR:
        this.sb.append("  @SP\n");
        this.sb.append("  A=M-1\n");
        this.sb.append("  D=M\n");
        this.sb.append("  A=A-1\n");

        if(operation.equals(ADD)) sb.append("  M=M+D\n"); // x + y
        if(operation.equals(SUB)) sb.append("  M=M-D\n"); // x - y
        if(operation.equals(AND)) sb.append("  M=M&D\n"); // x & y
        if(operation.equals(OR)) sb.append("  M=M|D\n");  // x | y

        this.sb.append("  @SP\n");
        this.sb.append("  M=M-1\n");
        break;

      case EQ: case GT: case LT:
        this.eqIndex++;
        this.sb.append("  @SP\n");
        this.sb.append("  A=M-1\n");
        this.sb.append("  D=M\n"); // D=y
        this.sb.append("  A=A-1\n"); //M=x
        this.sb.append("  D=M-D\n"); // D = x-y
        this.sb.append("  @EQUALITY"+this.eqIndex+"\n");

        if(operation.equals(EQ)) sb.append("  D;JEQ\n"); //Jump if D=0, i.e. y=x
        if(operation.equals(GT)) sb.append("  D;JGT\n"); //Jump if D>0, i.e. x>y
        if(operation.equals(LT)) sb.append("  D;JLT\n"); //Jump if D<0, i.e. x<y

        this.sb.append("  D=0\n"); // false
        this.sb.append("  @CONTINUE_EQ"+this.eqIndex+"\n");
        this.sb.append("  0;JMP\n");
        this.sb.append("(EQUALITY"+this.eqIndex+")\n");
        this.sb.append("  D=-1\n"); // true
        this.sb.append("(CONTINUE_EQ"+this.eqIndex+")\n");
        this.sb.append("  @SP\n");
        this.sb.append("  A=M-1\n"); // M=addr(y)
        this.sb.append("  A=A-1\n"); // A=addr(x); M=x
        this.sb.append("  M=D\n");   // x = D (true=-1;false=0)
        this.sb.append("  @SP\n");
        this.sb.append("  M=M-1\n");
        break;

      case NEG: case NOT:
        this.sb.append("  @SP\n");
        this.sb.append("  A=M-1\n");
        if(operation.equals(NEG)) sb.append("  M=-M\n"); // x = -x
        if(operation.equals(NOT)) sb.append("  M=!M\n"); // x = !x
        break;
    }

    return this.sb;
  }

  /**
   * Method to build a call command in assembly to write to the output file
   * @param parsedLine
   * @return this.sb containing the translated call command.
   */
  private StringBuffer buildCallCommand(HashMap<String, String> parsedLine) {
    String arg1 = parsedLine.get(ARG1);
    String arg2 = parsedLine.get(ARG2);
    Objects.requireNonNull(arg1, arg2);

    int i = Integer.parseInt(arg2);
    String segmentLabel = this.getSegmentLabel(arg1, i);
    String commandType = parsedLine.get(COMMAND_TYPE);
    this.sb.append("// "+parsedLine.get(LINE)+"\n");

    // TODO implement

    return this.sb;
  }

  /**
   * Method to build a Function command in assembly to write to the output file
   * @param parsedLine
   * @return this.sb containing the translated function command.
   */
  private StringBuffer buildFunctionCommand(HashMap<String, String> parsedLine) {
    String arg1 = parsedLine.get(ARG1);
    String arg2 = parsedLine.get(ARG2);
    Objects.requireNonNull(arg1, arg2);

    int i = Integer.parseInt(arg2);
    String segmentLabel = this.getSegmentLabel(arg1, i);
    String commandType = parsedLine.get(COMMAND_TYPE);
    this.sb.append("// "+parsedLine.get(LINE)+"\n");

    // TODO implement

    return this.sb;
  }

  /**
   * method to build a go to command in assemly to write to the output file.
   * @param parsedLine
   * @return this.sb containing the translated goto command.
   */
  private StringBuffer buildGoToCommand(HashMap<String, String> parsedLine){
    this.sb.append("// "+parsedLine.get(LINE)+"\n");

    String jumpLabel = parsedLine.get(ARG1);
    this.sb.append("  @"+jumpLabel+"\n");
    this.sb.append("  0;JMP\n");

    return this.sb;
  }

  /**
   * Method to build an if command in assembly to write to the output file.
   * @param parsedLine
   * @return this.sb containing the translated if command.
   */
  private StringBuffer buildIfCommand(HashMap<String, String> parsedLine) {
    this.sb.append("// "+parsedLine.get(LINE)+"\n");

    String jumpLabel = parsedLine.get(ARG1);

    this.sb.append("  @SP\n");
    this.sb.append("  M=M-1\n");
    this.sb.append("  A=M\n");
    this.sb.append("  D=M\n");
    this.sb.append("  @"+jumpLabel+"\n");
    this.sb.append("  D;JNE\n");

    return this.sb;
  }

  /**
   * Method to build a label command in assembly to write to the output file.
   * @param parsedLine
   * @return this.sb containing the translated Label command.
   */
  private StringBuffer buildLabelCommand(HashMap<String, String> parsedLine) {
    this.sb.append("// "+parsedLine.get(LINE)+"\n");

    String label = parsedLine.get(ARG1);
    this.sb.append("("+label+")\n");
    return this.sb;
  }

  /**
   * Method to build a return command in assembly to be written to the output file.
   * @param parsedLine
   * @return this.sb containing translated return command.
   */
  private StringBuffer buildReturnCommand(HashMap<String, String> parsedLine) {
    this.sb.append("// "+parsedLine.get(LINE)+"\n");

    // TODO implement

    return this.sb;
  }

  /**
   * Method to write a push/pop command in assembly to the output file.
   *
   * @param parsedLine
   * @return
   */
  private StringBuffer buildPushPopCommand(HashMap<String, String> parsedLine) {
    String arg1 = parsedLine.get(ARG1);
    String arg2 = parsedLine.get(ARG2);
    Objects.requireNonNull(arg1, arg2);

    int i = Integer.parseInt(arg2);
    String segmentLabel = this.getSegmentLabel(arg1, i);
    String commandType = parsedLine.get(COMMAND_TYPE);

    // command format:
    //  push local 3
    //    push    = parsedLine.get(COMMAND_TYPE)
    //    local   = arg1
    //    3       = arg2 & i

    this.sb.append("// "+parsedLine.get(LINE)+"\n");

    switch(arg1){
      case ARGUMENT: case LOCAL: case THAT: case THIS:
        this.sb = this.buildGeneralPushPop(commandType, segmentLabel, i);
        break;
      case CONSTANT:
        this.sb = this.buildConstantPush(i);
        break;
      case POINTER:
        this.sb = this.buildPointerPushPop(commandType, segmentLabel);
        break;
      case STATIC:
        this.sb = this.buildStaticPushPop(commandType, segmentLabel);
        break;
      case TEMP:
        this.sb = this.buildTempPushPop(commandType, segmentLabel, i);
        break;
    }

    return this.sb;
  }

  /**
   * Method to build the StringBuffer containing the .asm code translated from
   * the input vmCode command.
   *
   * @param i index of Constant segment we are pushing to.
   * @return this.sb with the translated Constant push command.
   */
  private StringBuffer buildConstantPush(int i) {

    this.sb.append("  @" + i + " \n");
    this.sb.append("  D=A\n");
    this.sb.append("  @SP\n");
    this.sb.append("  A=M\n");
    this.sb.append("  M=D\n");
    this.sb.append("  @SP\n");
    this.sb.append("  M=M+1\n");

    return this.sb;
  }

  /**
   * Method to build a StringBuffer containing the translated vmCode for push/pop
   * commands relevant to the Argument, Local, This, or That segments.
   *
   * @param commandType
   * @param segmentLabel
   * @param i
   * @return this.sb containing the translated push/pop command.
   */
  private StringBuffer buildGeneralPushPop(String commandType, String segmentLabel, int i) {

    switch(commandType){
      case VMParser.C_PUSH:
        this.sb.append("  @" + i + "\n");
        this.sb.append("  D=A\n"); // D = i
        this.sb.append("  @");
        this.sb.append(segmentLabel); // A = SEG
        this.sb.append("\n  A=M+D\n"); // A = RAM[SEG+D] (the base address value for that segment + D, where D=i)
        this.sb.append("  D=M\n"); // D = *RAM[RAM[SEG+i]]
        this.sb.append("  @SP\n");
        this.sb.append("  A=M\n");
        this.sb.append("  M=D\n");
        this.sb.append("  @SP\n");
        this.sb.append("  M=M+1\n");
        break;

      case VMParser.C_POP:
        this.sb.append("  @"+ i + "\n");
        this.sb.append("  D=A\n");
        this.sb.append("  @"+segmentLabel+"\n");
        this.sb.append("  D=D+M\n");
        this.sb.append("  @R15\n");
        this.sb.append("  M=D\n");
        this.sb.append("  @SP\n");
        this.sb.append("  M=M-1\n");
        this.sb.append("  A=M\n");
        this.sb.append("  D=M\n");
        this.sb.append("  @R15\n");
        this.sb.append("  A=M\n");
        this.sb.append("  M=D\n");
        break;

      default:
        System.out.println("Command Type not recognized: "+commandType);
    }

    return this.sb;
  }


  /**
   * Method to build a StringBuffer containing the translated vmCode for push/pop
   * commands relevant to the Pointer segment.
   *
   * @param commandType
   * @param segmentLabel
   * @return this.sb containing translated push/pop command.
   */
  private StringBuffer buildPointerPushPop(String commandType, String segmentLabel) {

    switch(commandType) {
      case VMParser.C_PUSH:
        this.sb.append("  @");
        this.sb.append(segmentLabel); // A = SEG
        this.sb.append("\n");
        this.sb.append("  D=M\n"); // D = RAM[SEG+i]
        this.sb.append("  @SP\n");
        this.sb.append("  A=M\n");
        this.sb.append("  M=D\n");
        this.sb.append("  @SP\n");
        this.sb.append("  M=M+1\n");
        break;

      case VMParser.C_POP:
        this.sb.append("  @SP\n");
        this.sb.append("  M=M-1\n");
        this.sb.append("  A=M\n");
        this.sb.append("  D=M\n");
        this.sb.append("  @");
        this.sb.append(segmentLabel);
        this.sb.append("\n");
        this.sb.append("  M=D\n");
        break;

      default:
        System.out.println("Command Type not recognized: " + commandType);
    }

    return this.sb;
  }

  /**
   * Method to build a StringBuffer containing the translated vmCode for push/pop
   * commands relevant to the Static segment.
   *
   * @param commandType
   * @param segmentLabel
   * @return this.sb containing translated push/pop command.
   */

  //to do switch arg1 argument to commandType
  private StringBuffer buildStaticPushPop(String commandType, String segmentLabel) {

    switch(commandType) {
      case VMParser.C_PUSH:
        this.sb.append("  @");
        this.sb.append(segmentLabel); // A = SEG
        this.sb.append("\n");
        this.sb.append("  D=M\n"); // D = RAM[SEG+i]
        this.sb.append("  @SP\n");
        this.sb.append("  A=M\n");
        this.sb.append("  M=D\n");
        this.sb.append("  @SP\n");
        this.sb.append("  M=M+1\n");
        break;

      case VMParser.C_POP:
        this.sb.append("  @SP\n");
        this.sb.append("  M=M-1\n");
        this.sb.append("  A=M\n");
        this.sb.append("  D=M\n");
        this.sb.append("  @");
        this.sb.append(segmentLabel);
        this.sb.append("\n");
        this.sb.append("  M=D\n");
        break;

      default:
        System.out.println("CommandType not recognized: " + commandType);
    }

    return this.sb;
  }

  /**
   * Method to build a StringBuffer containing the translated vmCode for push/pop
   * commands relevant to the Temp segment.
   *
   * @param commandType
   * @param segmentLabel
   * @param i
   * @return this.sb containing translated push/pop command.
   */
  private StringBuffer buildTempPushPop(String commandType, String segmentLabel, int i) {

    switch(commandType) {
      case VMParser.C_PUSH:
        this.sb.append("  @" + i + "\n");
        this.sb.append("  D=A\n");
        this.sb.append("  @"+segmentLabel+"\n");
        this.sb.append("  A=A+D\n"); // A = SEG + i
        this.sb.append("  D=M\n"); // D = RAM[SEG+i]
        this.sb.append("  @SP\n");
        this.sb.append("  A=M\n");
        this.sb.append("  M=D\n");
        this.sb.append("  @SP\n");
        this.sb.append("  M=M+1\n");
        break;

      case VMParser.C_POP:

        this.sb.append("  @"+ i + "\n");
        this.sb.append("  D=A\n");
        this.sb.append("  @"+segmentLabel+"\n");
        this.sb.append("  D=D+A\n");
        this.sb.append("  @R15\n");
        this.sb.append("  M=D\n");
        this.sb.append("  @SP\n");
        this.sb.append("  M=M-1\n");
        this.sb.append("  A=M\n");
        this.sb.append("  D=M\n");
        this.sb.append("  @R15\n");
        this.sb.append("  A=M\n");
        this.sb.append("  M=D\n");
        break;

        default:
        System.out.println("Command Type not recognized: " + commandType);
    }

    return this.sb;
  }

  /**
   * Method to get the segment Label for assembly code.
   *
   * @param segment
   * @return
   */
  private String getSegmentLabel(String segment, int i) {
    switch(segment){
      case LOCAL:
        return "LCL";
      case ARGUMENT:
        return "ARG";
      case THIS:
        return "THIS";
      case THAT:
        return "THAT";
      case TEMP:
        return "5";
      case POINTER:
        return (i==0) ? "THIS" : "THAT";
      case STATIC:
        String fileName = this.getCurrentFile();
        return fileName+"."+i;
      default:
        return null;
    }
  }

  /**
   * Method to set the name of the current file being translated.
   *  NOTE: Also writes a comment line to the output file indicating
   *        the change in file.
   * @param file
   * @throws IOException
   */
  public void setCurrentFile(File file) throws IOException {

    String fileName = file.getName();
    this.currentFileName = (fileName.endsWith(".asm")) ? fileName.replace(".asm","") : fileName;

    // write comment line into output file indicating a new file is being translated
    StringBuffer newFileBuffer = new StringBuffer();
    newFileBuffer.append("\n// Translating new file: ");
    newFileBuffer.append(fileName+"\n\n");
    this.write(newFileBuffer.toString());

  }

  /**
   * Method to get the name of the current file being translated
   * @return String representation of the curren;t file being translated.
   */
  private String getCurrentFile(){
    return this.currentFileName;
  }

  /**
   * Method to close the CodeRunner's bufferedWriter.
   * @throws IOException
   */
  @Override
  public void close() throws IOException {
    this.writer.close();
  }

}
