import java.io.File;
import java.util.List;

/**
 * Created by thillenbrand on 8/15/2018.
 */
public class JackAnalyzer {

    private static String TOKENIZER_INPUT_FILE_PATH;

    public static void main(String[] args) throws Exception {

        // deal with input arguments
        if (args.length == 1) {
            TOKENIZER_INPUT_FILE_PATH = args[0];
        } else {
            TOKENIZER_INPUT_FILE_PATH = "C:\\Users\\thillenbrand\\IntelliJProjects\\Nand2Tetris\\projects\\Part2\\10.11_Compiler\\10\\ExpressionLessSquare";
        }

        System.out.println("Begin");
        System.out.println("Tokenizer input file: " + TOKENIZER_INPUT_FILE_PATH);

        // initialize JackTokenizer and get list of input files
        JackTokenizer tokenizer = new JackTokenizer(TOKENIZER_INPUT_FILE_PATH);
        List<File> inputFiles = tokenizer.getInputFileList();

        // loop thru input file list and tokenize each,
        // writing to a separate output file for each
        for (File input : inputFiles) {
            File output = tokenizer.constructOutputFile(input);
            tokenizer.setOutputFile(output);
            tokenizer.initializeReader(input);
            tokenizer.initializeWriter(output);

            System.out.println("Finished reading input: " + input.getName());
            System.out.println("Finished writing output: " + output.getName());

        }

        System.out.println("Complete");

    }

}
