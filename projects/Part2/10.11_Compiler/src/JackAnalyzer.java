import com.sun.deploy.util.StringUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by thillenbrand on 8/15/2018.
 */
public class JackAnalyzer {

  private static String TOKENIZER_INPUT_FILE_PATH;

  public static void main(String[] args) throws Exception {

    // deal with input arguments
    if(args.length ==1 ) {
      TOKENIZER_INPUT_FILE_PATH = args[0];
    } else {
      TOKENIZER_INPUT_FILE_PATH = "C:\\Users\\thillenbrand\\IntelliJProjects\\Nand2Tetris\\projects\\Part2\\10.11_Compiler\\10\\ExpressionLessSquare\\Main.jack";
    }
    int lineNum = 0;
//    Stream<String> stream = Files.lines(Paths.get(TOKENIZER_INPUT_FILE_PATH));
//    //Stream<String> stream = reader.lines();
//    //System.out.println(stream.toString());
//    Iterator<String> it = stream.map(line -> line.trim())
//                                .filter(line -> !line.startsWith("//"))
//                                .iterator();

//    while(it.hasNext()) {
//      lineNum ++;
//      System.out.println(lineNum+" "+it.next());
//    }
//    Object[] streamArray = stream.toArray();
//    System.out.println(streamArray.toString());
//    while(reader.ready()){
//      lineNum ++;
//
//      System.out.println(lineNum+" "+reader.readLine());
//    }



    System.out.println("Begin");
    System.out.println("Tokenizer input: "+TOKENIZER_INPUT_FILE_PATH);

    // initialize JackTokenizer and get list of input files
    JackTokenizer tokenizer = new JackTokenizer(TOKENIZER_INPUT_FILE_PATH);
    List<File> inputFiles = tokenizer.getInputFileList();

    // loop thru input file list and tokenize each,
    // writing to a separate output file for each
    for(File input : inputFiles){
      File output = tokenizer.constructOutputFile(input);
      tokenizer.setOutputFile(output);
      tokenizer.initializeReader(input);
      tokenizer.initializeWriter(output);

      System.out.println("Finished reading input: "+input.getName());
      System.out.println("Finished writing output: "+output.getName());

    }

    System.out.println("Complete");

  }

}
