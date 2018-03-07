import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class VMTranslator {
  
  public static void main(String[] args) throws IOException {

    String INPUT_FILE_PATH = args[0];
    String OUTPUT_FILE_PATH = args[0].replace(".vm", ".asm");

    System.out.println("Start");
    File input = new File(INPUT_FILE_PATH);
    File output = new File(OUTPUT_FILE_PATH);
    VMTranslator translator = new VMTranslator();
    translator.translate(input, output);
    System.out.println("Complete");

  }

  /**
   * Default constructor
   */
  public VMTranslator(){}

  /**
   * Method to drive translation of the input .vm file into the output .asm file.
   *
   * @param inputFile
   * @param outputFile
   * @throws IOException
   */
  public void translate(File inputFile, File outputFile) throws IOException {

    VMParser parser = new VMParser(inputFile);
    VMCodeRunner codeRunner = new VMCodeRunner(outputFile);
    HashMap<String, String> parsedLine = new HashMap<>();

    while(parser.hasMoreCommands()){
      parsedLine = parser.parseLine();
      codeRunner.write(parsedLine);

    }

  }


}
