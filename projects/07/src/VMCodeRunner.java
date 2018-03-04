import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class VMCodeRunner implements AutoCloseable{

  private static final String LINE = "line";
  private static final String COMMAND_TYPE = "commandType";
  private static final String ARG1 = "arg1";
  private static final String ARG2 = "arg2";
  private static final String CONSTANT = "constant";

  private static final String ADD = "add";
  private static final String SUB = "sub";
  private static final String NEG = "neg";
  private static final String EQ  = "eq";
  private static final String GT  = "gt";
  private static final String LT  = "lt";
  private static final String AND = "and";
  private static final String OR  = "or";
  private static final String NOT = "not";


  private BufferedWriter writer;
  private StringBuffer sb;

  public VMCodeRunner(File outputFile) throws IOException {
    this.writer = new BufferedWriter(new FileWriter(outputFile));
    sb = new StringBuffer();

  }

  public void write(HashMap<String, String> parsedLine) throws IOException {
    // TODO implement writing of each line
    this.sb.setLength(0);

    switch(parsedLine.get(COMMAND_TYPE)){
      case VMParser.C_ARITHMETIC:
        this.sb = buildArithmeticCommand(parsedLine);
        break;
      case VMParser.C_PUSH:
        this.sb = buildPushPopCommand(parsedLine);
    }

    String code = sb.toString();
    this.writer.write(code);
    this.writer.flush();
  }

  /**
   * Method to write an arithmetic command in assembly to the output file.
   *
   * @param parsedLine
   * @return
   */
  private StringBuffer buildArithmeticCommand(HashMap<String, String> parsedLine){
    String operation = parsedLine.get(LINE);
    this.sb.append("// "+operation+"\n");

    switch(operation){
      case ADD:
        sb.append("  @SP\n");
        sb.append("  A=M-1\n");
        sb.append("  D=M\n");
        sb.append("  A=A-1\n");
        sb.append("  M=M+D\n");
        sb.append("  @SP\n");
        sb.append("  M=M-1\n");
        break;
      case SUB:
        sb.append("  @SP\n");
        sb.append("  A=M-1\n");
        sb.append("  D=M\n");
        sb.append("  A=A-1\n");
        sb.append("  M=M-D\n");
        sb.append("  @SP\n");
        sb.append("  M=M-1\n");
        break;
      case NEG:
        sb.append("  @SP\n");
        sb.append("  A=M-1\n");
        sb.append("  M=-M\n");
        break;
      case EQ:

        break;
      case GT:
        break;
      case LT:
        break;
      case AND:
        break;
      case OR:
        break;
      case NOT:
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
    // TODO implement generalized push/pop for any segment.
    String[] splitLine = parsedLine.get(ARG2).split(" ");
    String segment = splitLine[0];
    String i = splitLine[1];

    this.sb.append("// "+parsedLine.get(LINE)+"\n");
    switch(segment){
      case CONSTANT:
        this.sb.append("  @"+i+" \n");
        this.sb.append("  D=A\n");
        this.sb.append("  @SP\n");
        this.sb.append("  A=M\n");
        this.sb.append("  M=D\n");
        this.sb.append("  @SP\n");
        this.sb.append("  M=M+1\n");
        break;
      default:
        // TODO implement non-constant segment push/pop translation

    }

    return this.sb;
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

  }

}
