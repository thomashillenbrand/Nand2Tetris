import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This is the Main class that will drive the HackAssembler to drive the conversion
 * of .asm programs in binary code.
 *
 * author: Thomas Hillenbrand
 * last update: 2/22/2018
 *
 */


public class HackAssembler {

    public static final String FILE_PATH = "C:/Users/thillenbrand/IntellijProjects/Nand2Tetris/projects/06/rect/Rect.asm";
    //public static final String FILE_PATH = "/home/thomashillenbrand/Projects/Nand2Tetris/projects/06/add/Add.asm";
    //public static final String FILE_PATH = "/home/thomashillenbrand/Projects/Nand2Tetris/projects/06/rect/Rect.asm";
    public static void main(String[] args) throws Exception {

        String inputFilePath = FILE_PATH;
        HackAssembler assembler = new HackAssembler();
        assembler.assemble(inputFilePath);

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

        SymbolTable symbolTable = new SymbolTable();
        File inputFile= new File(filePath);
        Parser parser = new Parser(inputFile);
        System.out.println("Start");
        parser.firstPass(symbolTable);
        System.out.println("First pass complete: ");
        System.out.println(symbolTable.toString());
        System.out.println("Start second pass");
        parser.secondPass(symbolTable);
        System.out.println(symbolTable.toString());
        System.out.println("complete");

    }


}
