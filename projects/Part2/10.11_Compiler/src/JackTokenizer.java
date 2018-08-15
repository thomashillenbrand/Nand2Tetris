import java.io.File;

/**
 * Created by thillenbrand on 8/15/2018.
 */
public class JackTokenizer {

  private File inputFile;
  private File outputFile;

  public JackTokenizer(String inputFileString){
    this.inputFile = constructInputFile(inputFileString);
    this.outputFile = constructOutputFile(inputFileString);
    // should open input file/stream and get ready to Tokenize.

  }

  enum tokenType{
    KEYWORD, SYMBOL, IDENTIFIER, INT_CONST, STRING_CONST
  }

  enum keyWord{CLASS, METHOD, FUNCTION, CONSTRUCTOR, INT, BOOLEAN, CHAR, VOID, VAR,
    STATIC, FIELD, LET, DO, IF, ELSE, WHILE, RETURN, TRUE, FALSE, NULL, THIS }

  /**
   * Method to determine if there are more tokens in the input
   *
   * @return boolean indicating whether there are tokens to process
   */
  public static boolean hasMoreTokens(){
    boolean hasMoreTokens = false;
    return hasMoreTokens;
  }

  /**
   * Method to get the next token from the input and makes it the current token.
   * NOTE: This method should only get called when hasMoreTokens() is true.
   */
  public static void advance(){

  }

  /**
   * Returns the type of the current token
   * @return tokenType enum of the current token
   */
  public static tokenType tokenType(){
    return null;
  }

  /**
   * Method that returns the keyword which is the current token.
   * NOTE: This method should only be called when tokenType() is KEYWORD
   *
   * @return String value that is the keyword which is the current token.
   */
  public static keyWord keyWord(){
    return null;
  }

  /**
   * Method that Returns the character which is the current token.
   * NOTE: This method should only be called when tokenType() is SYMBOL
   *
   * @return char which is the current token.
   */
  public static char symbol(){
    return 'x';
  }

  /**
   * Method that returns the identifier which is the current token.
   * NOTE: This method should only be called when tokenType() is IDENTIFIER
   *
   * @return String representation of the identifier which is the current token.
   */
  public static String identifier(){
    return null;
  }

  /**
   * Returns the integer value of the current token.
   * NOTE: This method should only be called when tokenType() is INT_CONST.
   *
   * @return int value of the current token.
   */
  public static int intVal(){
    return 1;
  }

  /**
   * Method that returns the string value of the current token, without double quotes.
   * NOTE: This method should only be called when tokenType() is STRING_CONST.
   *
   * @return String value of the current token.
   */
  public static String stringVal(){
    return null;
  }

  /**
   * Method to retrieve the File object from the String value of the file path.
   *
   * @param inputFileString    String value of the input file path.
   * @return                   File at the location specified by the inputFileString
   */
  private File constructInputFile(String inputFileString) {

    return null;
  }

  /**
   * Method to create the File object to which the JackAnalyzer will write to.
   *
   * @param inputFileString    String value of the input file path
   * @return                   File at the location derived from the inputFileString.
   */
  private File constructOutputFile(String inputFileString) {

    return null;
  }

  /**
   * Setter for the inputFile field.
   *
   * @param input    new inputFile
   */
  public void setInputFile(File input){
    this.inputFile = input;
  }

  /**
   * Setter for the outputFile field
   *
   * @param output    new outputFile
   */
  public void setOutputFile(File output){
    this.outputFile = output;
  }

}
