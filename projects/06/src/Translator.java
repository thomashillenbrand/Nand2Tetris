import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to actually convert the individual mnemonic
 * components returned by the parser into the binary code snippets
 * that can be run by the computer.
 *
 * author: Thomas Hillenbrand
 *
 */

public class Translator implements AutoCloseable{

    private BufferedWriter writer;
    private HashMap<String, String> compMap;
    private HashMap<String, String> destMap;
    private HashMap<String, String> jumpMap;

    /**
     * Defualt constructor that initializes a BufferedWriter for the given output file path and creates reference
     * maps for c-instruction translation.
     *
     * @param outputFilePath
     * @throws IOException
     */
    public Translator(String outputFilePath) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(outputFilePath));
        this.compMap = new HashMap<String, String>();
        this.destMap = new HashMap<String, String>();
        this.jumpMap = new HashMap<String, String>();

        this.initializeCompMap();
        this.initializeDestMap();
        this.initializeJumpMap();

    }

    /**
     * Method to translate a given A-instruction into a 16-bit binary representation
     * that is then written to the .hack output file given in the constructor's input arguments.
     *
     * @param aCommand
     * @throws IOException
     */
    public void translateA(String aCommand) throws IOException {
        int address = Integer.parseInt(aCommand);
        StringBuffer instruction = new StringBuffer();
        instruction.insert(0,Integer.toBinaryString(address));
        while(instruction.length() < 16) instruction.insert(0, "0");
        this.write(instruction.toString());

    }

    public void translateC(Map cMnemonics) throws IOException {
        StringBuffer instruction = new StringBuffer();
        instruction.append("111");

    }

    /**
     * Method to write to write the given line to the output file.
     *
     * @param line
     * @throws IOException
     */
    private void write(String line) throws IOException {
        this.writer.write(line);
        this.writer.write("\n");
        this.writer.flush();

    }

    private void initializeDestMap() {

    }

    private void initializeCompMap() {
        this.compMap.put("0", "0101010");
        this.compMap.put("1", "0111111");
        this.compMap.put("-1", "0111010");
        this.compMap.put("D", "0001100");
        this.compMap.put("A", "0110000");
        this.compMap.put("M", "1110000");
        this.compMap.put("!D", "0001101");
    }

    private void initializeJumpMap() {
    }

    /**
     * Method to close the Translator's bufferedWriter.
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        this.writer.close();
    }

}
