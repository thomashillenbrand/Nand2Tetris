import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

/**
 * This class is used to actually convert the individual mnemonic
 * components returned by the parser into the binary code snippets
 * that can be run by the computer.
 *
 * author: Thomas Hillenbrand
 *
 */

public class Translator implements AutoCloseable{

    private static final String COMPUTATION = "comp";
    private static final String DESTINATION = "dest";
    private static final String JUMP        = "jmp";

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

    /**
     * Method to translate a C-instruction into a line of 16--bit binary code
     *
     * @param cMnemonics
     * @throws IOException
     */
    public void translateC(HashMap<String, String> cMnemonics) throws IOException {
        String comp = cMnemonics.get(COMPUTATION);
        String dest = (cMnemonics.get(DESTINATION) != null) ? cMnemonics.get(DESTINATION) : new String();
        String jmp  = (cMnemonics.get(JUMP) != null) ? cMnemonics.get(JUMP) : new String();

        Objects.requireNonNull(comp);

        StringBuffer instruction = new StringBuffer();
        instruction.append("111");
        instruction.append(compMap.get(comp));
        instruction.append(destMap.get(dest));
        instruction.append(jumpMap.get(jmp));

        this.write(instruction.toString());

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
        this.destMap.put("", "000");
        this.destMap.put("M", "001");
        this.destMap.put("D", "010");
        this.destMap.put("MD", "011");
        this.destMap.put("A", "100");
        this.destMap.put("AM", "101");
        this.destMap.put("AD", "110");
        this.destMap.put("AMD", "111");

    }

    private void initializeCompMap() {
        this.compMap.put("0", "0101010");
        this.compMap.put("1", "0111111");
        this.compMap.put("-1", "0111010");
        this.compMap.put("D", "0001100");
        this.compMap.put("A", "0110000");
        this.compMap.put("M", "1110000");
        this.compMap.put("!D", "0001101");
        this.compMap.put("!A", "0110001");
        this.compMap.put("!M", "1110001");
        this.compMap.put("-D", "0001111");
        this.compMap.put("-A", "0110011");
        this.compMap.put("-M", "1110011");
        this.compMap.put("D+1", "0011111");
        this.compMap.put("A+1", "0110111");
        this.compMap.put("M+1", "1110111");
        this.compMap.put("D-1", "0001110");
        this.compMap.put("A-1", "0110010");
        this.compMap.put("M-1", "1110010");
        this.compMap.put("D+A", "0000010");
        this.compMap.put("D+M", "1000010");
        this.compMap.put("D-A", "0010011");
        this.compMap.put("D-M", "1010011");
        this.compMap.put("A-D", "0000111");
        this.compMap.put("M-D", "1000111");
        this.compMap.put("D&A", "0000000");
        this.compMap.put("D&M", "1000000");
        this.compMap.put("D|A", "0010101");
        this.compMap.put("D|M", "1010101");

    }

    private void initializeJumpMap() {
        this.jumpMap.put("", "000");
        this.jumpMap.put("JGT", "001");
        this.jumpMap.put("JEQ", "010");
        this.jumpMap.put("JGE", "011");
        this.jumpMap.put("JLT", "100");
        this.jumpMap.put("JNE", "101");
        this.jumpMap.put("JLE", "110");
        this.jumpMap.put("JMP", "111");

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
