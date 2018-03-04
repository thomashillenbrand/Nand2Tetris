import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class VMCodeRunner implements AutoCloseable{

  private BufferedWriter writer;
  private StringBuffer sb;

  public VMCodeRunner(File outputFile) throws IOException {
    this.writer = new BufferedWriter(new FileWriter(outputFile));

  }

  public void write(HashMap<String, String> parsedLine) throws IOException {
    // TODO implement writing of each line
    this.sb.setLength(0);

    switch(parsedLine.get("commandType")){
      case VMParser.C_ARITHMETIC:
        this.sb = writeArithmetic(parsedLine);
        break;
      case VMParser.C_PUSH:
        this.sb = writePushPop(parsedLine);
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
  public StringBuffer writeArithmetic(HashMap<String, String> parsedLine){


    return this.sb;
  }

  /**
   * Method to write a push/pop command in assembly to the output file.
   *
   * @param parsedLine
   * @return
   */
  private StringBuffer writePushPop(HashMap<String, String> parsedLine) {

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
