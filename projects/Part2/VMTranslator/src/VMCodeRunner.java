import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class VMCodeRunner implements AutoCloseable{

  private static final String LINE = "line";
  private static final String COMMAND_TYPE = "commandType";
  private static final String ARG1 = "arg1";
  private static final String ARG2 = "arg2";

  private static final String ADD = "add";
  private static final String SUB = "sub";
  private static final String NEG = "neg";
  private static final String EQ  = "eq";
  private static final String GT  = "gt";
  private static final String LT  = "lt";
  private static final String AND = "and";
  private static final String OR  = "or";
  private static final String NOT = "not";

  private static final String CONSTANT = "constant";
  private static final String LOCAL    = "local";
  private static final String ARGUMENT = "argument";
  private static final String THIS     = "this";
  private static final String THAT     = "that";
  private static final String STATIC   = "static";
  private static final String TEMP     = "temp";
  private static final String POINTER  = "pointer";

  private int eqIndex;
  private String outputFileName;
  private StringBuffer sb;
  private BufferedWriter writer;

  public VMCodeRunner(File outputFile) throws IOException {
    this.writer = new BufferedWriter(new FileWriter(outputFile));
    this.sb = new StringBuffer();
    this.eqIndex = 0;
    this.outputFileName = outputFile.getName().replace(".asm", "");

  }

  /**
   * Method to  translate and write a parsedLine of vmCode into a segment of assembly code
   * in the output file.
   *
   * @param parsedLine
   * @throws IOException
   */
  public void write(HashMap<String, String> parsedLine) throws IOException {
    this.sb.setLength(0);

    switch(parsedLine.get(COMMAND_TYPE)){
      case VMParser.C_ARITHMETIC:
        this.sb = buildArithmeticCommand(parsedLine);
        break;

      case VMParser.C_PUSH: case VMParser.C_POP:
        this.sb = buildPushPopCommand(parsedLine);
    }

    String code = this.sb.toString();
    this.writer.write(code);
    this.writer.flush();
  }

  /**
   * Method to write an arithmetic command in assembly to the output file.
   *
   * @param parsedLine
   * @return this.sb containting the translated arithmetic command.
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
   * Method to write a push/pop command in assembly to the output file.
   *
   * @param parsedLine
   * @return
   */
  private StringBuffer buildPushPopCommand(HashMap<String, String> parsedLine) {
    String arg1 = parsedLine.get(ARG1);
    String arg2 = parsedLine.get(ARG2);
    Objects.requireNonNull(arg1, arg2);

    // command format:
    //  push local 3
    //    push    = arg1
    //    local 3 = arg2
    //    local   = segment
    //    3       = i

    String[] splitLine = arg2.split(" ");
    String segment = splitLine[0];
    int i = Integer.parseInt(splitLine[1]);
    String segmentLabel = this.getSegmentLabel(segment, i);

    this.sb.append("// "+parsedLine.get(LINE)+"\n");

    switch(segment){
      case ARGUMENT: case LOCAL: case THAT: case THIS:
        this.sb = this.buildGeneralPushPop(arg1, segmentLabel, i);
        break;
      case CONSTANT:
        this.sb = this.buildConstantPush(i);
        break;
      case POINTER:
        this.sb = this.buildPointerPushPop(arg1, segmentLabel);
        break;
      case STATIC:
        this.sb = this.buildStaticPushPop(arg1, segmentLabel);
        break;
      case TEMP:
        this.sb = this.buildTempPushPop(arg1, segmentLabel, i);
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
   * @param arg1
   * @param segmentLabel
   * @param i
   * @return this.sb containing the translated push/pop command.
   */
  private StringBuffer buildGeneralPushPop(String arg1, String segmentLabel, int i) {

    switch(arg1){
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
        System.out.println("Arg1 not recognized: "+arg1);
    }

    return this.sb;
  }


  /**
   * Method to build a StringBuffer containing the translated vmCode for push/pop
   * commands relevant to the Pointer segment.
   *
   * @param arg1
   * @param segmentLabel
   * @return this.sb containing translated push/pop command.
   */
  private StringBuffer buildPointerPushPop(String arg1, String segmentLabel) {

    switch(arg1) {
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
        System.out.println("Arg1 not recognized: " + arg1);
    }

    return this.sb;
  }

  /**
   * Method to build a StringBuffer containing the translated vmCode for push/pop
   * commands relevant to the Static segment.
   *
   * @param arg1
   * @param segmentLabel
   * @return this.sb containing translated push/pop command.
   */
  private StringBuffer buildStaticPushPop(String arg1, String segmentLabel) {

    switch(arg1) {
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
        System.out.println("Arg1 not recognized: " + arg1);
    }

    return this.sb;
  }

  /**
   * Method to build a StringBuffer containing the translated vmCode for push/pop
   * commands relevant to the Temp segment.
   *
   * @param arg1
   * @param segmentLabel
   * @param i
   * @return this.sb containing translated push/pop command.
   */
  private StringBuffer buildTempPushPop(String arg1, String segmentLabel, int i) {

    switch(arg1) {
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
        System.out.println("Arg1 not recognized: " + arg1);
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
        return outputFileName+"."+i;
      default:
        return null;
    }
  }

  /**
   * Method to close the CodeRunner's bufferedWriter.
   * @throws IOException
   */
  @Override
  public void close() throws IOException {
    this.writer.close();
  }

  /**
   * Method to set the output file that the CodeRunner is writing to.
   * @param outputFilePath
   * @throws IOException
   */
  public void setFileName(String outputFilePath) throws IOException {
    File outputFile = new File(outputFilePath);
    this.close();
    this.writer = new BufferedWriter(new FileWriter(outputFile));
    this.outputFileName = outputFile.getName();
    this.eqIndex = 0;

  }

}