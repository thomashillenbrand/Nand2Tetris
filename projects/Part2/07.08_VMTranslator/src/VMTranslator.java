import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("JavaDoc")
public class VMTranslator {

    private File inputFile;
    private List<File> inputFileList;
    private File outputFile;

    /**
     * Default constructor
     */
    public VMTranslator(String inputFilePath) {
        this.inputFile = new File(inputFilePath);
        this.outputFile = this.constructOutputFile(inputFilePath);
        this.inputFileList = new ArrayList<>();
        this.parseInputStream(this.inputFile);

    }

    public static void main(String[] args) throws IOException {

        String INPUT_FILE_PATH = args[0];

        System.out.println("Start: \n" + INPUT_FILE_PATH);

        VMTranslator translator = new VMTranslator(INPUT_FILE_PATH);
        translator.translate();

        System.out.println("Complete");

    }

    /**
     * Method to drive translation of the input .vm file into the output .asm file.
     *
     * @throws IOException
     */
    public void translate() throws IOException {

        VMParser parser = new VMParser();
        HashMap<String, String> parsedLine;
        VMCodeRunner codeRunner = new VMCodeRunner(this.outputFile);
        codeRunner.writeInit();

        for (File file : this.inputFileList) {
            parser.setInputFile(file);
            codeRunner.setCurrentFile(file);

            while (parser.hasMoreCommands()) {
                parsedLine = parser.parseLine();
                codeRunner.writeCommand(parsedLine);

            } // end while loop

        } // end for-each loop

    }

    /**
     * Recursive method to parse the input file to and populates inputFilesList
     * to be translated.
     *
     * @param inputFile
     */
    private void parseInputStream(File inputFile) {
        try {
            if (inputFile.isDirectory()) {
                for (File childFile : Objects.requireNonNull(inputFile.listFiles())) {
                    this.parseInputStream(childFile);
                }

            } else {
                String pathName = inputFile.getAbsolutePath();
                if (pathName.endsWith(".vm")) {
                    this.inputFileList.add(inputFile);
                }

            }

        } catch (Exception e) {
            System.out.println("Error caught while parsing input stream: " + e.getMessage());
            throw e;
        }

    }


    /**
     * Method to return File object to be used as the output file when translating.
     *
     * @param INPUT_FILE_PATH
     * @return outputFile File
     */
    private File constructOutputFile(String INPUT_FILE_PATH) {
        String outputFilePath;

        if (INPUT_FILE_PATH.endsWith(".vm")) {
            outputFilePath = INPUT_FILE_PATH.replace(".vm", ".asm");

        } else {
            outputFilePath = INPUT_FILE_PATH + ".asm";

        }

        return new File(outputFilePath);
    }

    // ================== Getters and Setters ======================== //

    /**
     * Method to return the inputFile object.
     *
     * @return inputFile
     */
    public File getInputFile() {
        return this.inputFile;

    }

    /**
     * Method to set the inputFile for the translator. This will cause the output file and
     * inputFileList to be updated.
     *
     * @param inputFile
     */
    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
        this.outputFile = constructOutputFile(inputFile.getAbsolutePath());
        this.inputFileList.clear();
        this.parseInputStream(inputFile);
    }

    /**
     * Method to return the current outputFile object
     *
     * @return outputFile
     */
    public File getOutputFile() {
        return this.outputFile;

    }

    /**
     * Method to get the current list of inputFiles to translate
     *
     * @return inputFileList
     */
    public List<File> getInputFileList() {
        return this.inputFileList;

    }


}
