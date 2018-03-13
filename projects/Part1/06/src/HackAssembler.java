import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This is the Main class that will drive the HackAssembler to drive the conversion
 * of .asm programs in binary code.
 *
 * author: Thomas Hillenbrand
 *
 */


public class HackAssembler {

  public static final String FILE_PATH = "/home/thomashillenbrand/Projects/Nand2Tetris/projects/06/add/Add.asm";
  //public static final String FILE_PATH = "/home/thomashillenbrand/Projects/Nand2Tetris/projects/06/max/Max.asm";
  //public static final String FILE_PATH = "/home/thomashillenbrand/Projects/Nand2Tetris/projects/06/pong/Pong.asm";
  //public static final String FILE_PATH = "/home/thomashillenbrand/Projects/Nand2Tetris/projects/06/rect/Rect.asm";

  public static void main(String[] args) throws Exception {

    long startTime = System.currentTimeMillis();
    String inputFilePath = FILE_PATH;
    HackAssembler assembler = new HackAssembler();
    assembler.assemble(inputFilePath);
    long endTime = System.currentTimeMillis();
    System.out.println("Total asembly time: "+(endTime - startTime) +" ms");

  }

  /**
   * HackAssembler default constructor.
   *
   */
  public HackAssembler(){

  }

    /**
     * Method called to assemble a .asm file into binary code.
     * @param filePath
     */
    public void assemble(String filePath) throws Exception {

      System.out.println("Starting");
      System.out.println("Loading Parser . . .");
      File inputFile= new File(filePath);
      Parser parser = new Parser(inputFile);

      System.out.println("Loading SymbolTable . . . ");
      SymbolTable symbolTable = new SymbolTable();

      System.out.println("Parser first pass . . . ");
      parser.firstPass(symbolTable);
      System.out.println("Parser second pass . . . ");
      parser.secondPass(symbolTable);

      System.out.println("Complete");

    }


}
