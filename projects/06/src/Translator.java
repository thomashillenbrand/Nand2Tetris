import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map; /**
 * This class is used to actually convert the individual mnemonic
 * components returned by the parser into the binary code snippets
 * that can be run by the computer.
 *
 * author: Thomas Hillenbrand
 * last updated: 2/22/2018
 *
 */

public class Translator implements AutoCloseable{

    private BufferedWriter writer;

    /**
     * D
     */

    public Translator(String outputFilePath) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(outputFilePath));

    }

    public void translateA(String aCommand) throws IOException {


    }

    public void translateC(Map cMnemonics) {

    }

    public void write(String line) throws IOException {
        this.writer.write(line);
        this.writer.write("\n");
        this.writer.flush();

    }

    @Override
    public void close() throws IOException {
        this.writer.close();
    }

}
