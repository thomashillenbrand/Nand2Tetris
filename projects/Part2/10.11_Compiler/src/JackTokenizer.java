import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thillenbrand on 8/15/2018.
 */
public class JackTokenizer {

  private File outputFile;
  private List<File> inputFileList;
  private BufferedReader reader;
  private BufferedWriter writer;

  public JackTokenizer(String inputFileString) throws Exception {
    this.inputFileList = new ArrayList<>();
    constructInputFileList(inputFileString);
    // should open input file/stream and get ready to Tokenize.

  }

  enum tokenType {
    KEYWORD, SYMBOL, IDENTIFIER, INT_CONST, STRING_CONST
  }

  enum keyWord {
    CLASS, METHOD, FUNCTION, CONSTRUCTOR, INT, BOOLEAN, CHAR, VOID, VAR,
    STATIC, FIELD, LET, DO, IF, ELSE, WHILE, RETURN, TRUE, FALSE, NULL, THIS
  }

  /**
   * Method to determine if there are more tokens in the input
   *
   * @return boolean indicating whether there are tokens to process
   */
  public static boolean hasMoreTokens() {
    boolean hasMoreTokens = false;
    return hasMoreTokens;
  }

  /**
   * Method to get the next token from the input and makes it the current token.
   * NOTE: This method should only get called when hasMoreTokens() is true.
   */
  public static void advance() {

  }

  /**
   * Returns the type of the current token
   *
   * @return tokenType enum of the current token
   */
  public static tokenType tokenType() {
    return null;
  }

  /**
   * Method that returns the keyword which is the current token.
   * NOTE: This method should only be called when tokenType() is KEYWORD
   *
   * @return String value that is the keyword which is the current token.
   */
  public static keyWord keyWord() {
    return null;
  }

  /**
   * Method that Returns the character which is the current token.
   * NOTE: This method should only be called when tokenType() is SYMBOL
   *
   * @return char which is the current token.
   */
  public static char symbol() {
    return 'x';
  }

  /**
   * Method that returns the identifier which is the current token.
   * NOTE: This method should only be called when tokenType() is IDENTIFIER
   *
   * @return String representation of the identifier which is the current token.
   */
  public static String identifier() {
    return null;
  }

  /**
   * Returns the integer value of the current token.
   * NOTE: This method should only be called when tokenType() is INT_CONST.
   *
   * @return int value of the current token.
   */
  public static int intVal() {
    return 1;
  }

  /**
   * Method that returns the string value of the current token, without double quotes.
   * NOTE: This method should only be called when tokenType() is STRING_CONST.
   *
   * @return String value of the current token.
   */
  public static String stringVal() {
    return null;
  }

  /**
   * Method to retrieve the list of File objects from the String value of the file path.
   *
   * @param inputFileString String value of the input file path.
   * @return List of files contained in the location specified by the inputFileString
   */
  private void constructInputFileList(String inputFileString) throws Exception {
    try {
      //System.out.println("Input file: \n" + inputFileString);
      File input = new File(inputFileString);

      if (input != null) {
        // if input is a directory, recursively call method to get to files
        if (input.isDirectory()) {
          //System.out.println("Directory found, parsing children files");
          for (File file : input.listFiles()) {
            constructInputFileList(file.getAbsolutePath());

          }

        } else {
          if (input.getAbsolutePath().endsWith(".jack")) {
            //System.out.println(".jack file found");
            this.inputFileList.add(input);
          }

        }

      } else {
        throw new Exception("No file found for: " + inputFileString);
      }

    } catch (Exception e) {
      System.out.println("Exception caught while constructing input file list: " + e.getMessage());
      throw e;
    }

  }

  /**
   * Method to create the File object to which the JackAnalyzer will write to based
   * on the path of the input file.
   *
   * @param inputFile File Object of the input file
   * @return File at the location derived from the inputFile.
   */
  public File constructOutputFile(File inputFile) {
    try {
      String outputFilePath;
      String inputFilePath = inputFile.getAbsolutePath();
      outputFilePath = inputFilePath.replace(".jack", "T.xml");

      //System.out.println("Output file path: \n" + outputFilePath);
      return new File(outputFilePath);

    } catch (Exception e) {
      System.out.println("Exceptin thrown when trying to create output file: " + e.getMessage());
      throw e;
    }
  }

  /**
   * Method to initialize a BufferedReader for the given input file to be able to read its contents.
   * If another BufferedReader is already open, it will be closed prior to teh new one being opened.
   *
   * @param input         File we are tokenizing
   * @throws IOException
   */
  public void initializeReader(File input) throws IOException {
    try {
      if (this.reader != null) {
        this.reader.close();
      }
      this.reader = new BufferedReader(new FileReader(input));

    } catch (IOException e) {
      System.out.println("Error handling the BufferedReader: " + e.getMessage());
      throw e;
    }
  }

  /**
   * Method used to initialize the BufferedWriter used to write the output for the file were are tokenizing.
   * If another BufferedWriter is already open, it will be closed prior to teh new one being opened.
   *
   * @param output       File to which we are writing our tokens
   * @throws IOException
   */
  public void initializeWriter(File output) throws IOException {
    try {
      if (this.writer != null) {
        this.writer.close();
      }
      this.writer = new BufferedWriter(new FileWriter(output));

    } catch (IOException e) {
      System.out.println("Error handling the BufferedWriter: " + e.getMessage());
      throw e;
    }
  }

  /**
   * Method to call the BufferedWriter's write method from outside the tokenizer.
   *
   * @param str          String value we want to write to the output file
   * @throws IOException
   */
  public void write(String str) throws IOException {
    try {
      this.writer.write(str);

    } catch (IOException e){
      System.out.println("Error writing to file: "+e.getMessage());
      throw e;
    }
  }

  /**
   * Setter for the outputFile field
   *
   * @param output new outputFile
   */
  public void setOutputFile(File output) {
    this.outputFile = output;
  }

  /**
   * Getter method for inputFileList
   *
   * @return List of files to be tokenized
   */
  public List<File> getInputFileList() {
    return this.inputFileList;
  }

}
