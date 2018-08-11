import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;


@SuppressWarnings({"StringConcatenationInsideStringBufferAppend", "JavaDoc"})
public class VMCodeRunner implements AutoCloseable {

    private static final String ARG1 = "arg1";
    private static final String ARG2 = "arg2";
    private static final String LINE = "line";
    private static final String COMMAND_TYPE = "commandType";

    private static final String ADD = "add";
    private static final String SUB = "sub";
    private static final String NEG = "neg";
    private static final String EQ = "eq";
    private static final String GT = "gt";
    private static final String LT = "lt";
    private static final String AND = "and";
    private static final String OR = "or";
    private static final String NOT = "not";

    private static final String CONSTANT = "constant";
    private static final String LOCAL = "local";
    private static final String ARGUMENT = "argument";
    private static final String THIS = "this";
    private static final String THAT = "that";
    private static final String STATIC = "static";
    private static final String TEMP = "temp";
    private static final String POINTER = "pointer";

    private static final String NO_FUNCTION = "";

    private int eqIndex;
    private int labelCnt = 0;
    private String currentFileName;
    private StringBuffer sb;
    private BufferedWriter writer;
    private boolean inFunction = false;
    private String currentFunction;


    public VMCodeRunner(File outputFile) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(outputFile));
        this.currentFunction = NO_FUNCTION;
        this.sb = new StringBuffer();
        this.eqIndex = 0;

    }

    /**
     * Default constructor
     */
    public VMCodeRunner() {
    }

    /**
     * Method to write a given string to the output file.
     *
     * @param code
     * @throws IOException
     */
    public void write(String code) throws IOException {
        this.writer.write(code);
        this.writer.flush();

    }

    /**
     * Method to write the bootstrap code for the VM Translator
     *
     * @throws IOException
     */
    public void writeInit() throws IOException {

        this.sb.setLength(0);
        this.sb.append("@256\n");
        this.sb.append("D=A\n");
        this.sb.append("@SP\n");
        this.sb.append("M=D\n");

        this.write(this.sb.toString());

        this.sb.setLength(0);
        HashMap<String, String> callInit = new HashMap<>();
        callInit.put("line", "Call Sys.init 0");
        callInit.put("arg1", "Sys.init");
        callInit.put("arg2", "0");
        this.sb = buildCallCommand(callInit);

        this.write(this.sb.toString());

    }

    /**
     * Method to translate a given parsedLine Map into asm and
     * then write it to the output file.
     *
     * @param parsedLine
     * @throws IOException
     */

    public void writeCommand(HashMap<String, String> parsedLine) throws IOException {

        this.sb.setLength(0);

        switch (parsedLine.get(COMMAND_TYPE)) {
            case VMParser.C_ARITHMETIC:
                this.sb = buildArithmeticCommand(parsedLine);
                break;
            case VMParser.C_PUSH:
            case VMParser.C_POP:
                this.sb = buildPushPopCommand(parsedLine);
                break;
            case VMParser.C_LABEL:
                this.sb = buildLabelCommand(parsedLine, getInFunction(), getCurrentFunctionName());
                break;
            case VMParser.C_GOTO:
                this.sb = buildGoToCommand(parsedLine, getInFunction(), getCurrentFunctionName());
                break;
            case VMParser.C_IF:
                this.sb = buildIfCommand(parsedLine, getInFunction(), getCurrentFunctionName());
                break;
            case VMParser.C_CALL:
                this.sb = buildCallCommand(parsedLine);
                break;
            case VMParser.C_FUNCTION:
                this.sb = buildFunctionCommand(parsedLine);
                break;
            case VMParser.C_RETURN:
                this.sb = buildReturnCommand(parsedLine);
                break;
            default:
                break;
        }

        this.write(this.sb.toString());
    }

    /**
     * Method to build an arithmetic command in assembly to write to the output file.
     *
     * @param parsedLine
     * @return this.sb containing the translated arithmetic command.
     */
    private StringBuffer buildArithmeticCommand(HashMap<String, String> parsedLine) {
        String operation = parsedLine.get(LINE);
        StringBuffer sb = new StringBuffer();
        sb.append("// " + operation + "\n");

        switch (operation) {
            case ADD:
            case SUB:
            case AND:
            case OR:
                sb.append("  @SP\n");
                sb.append("  A=M-1\n");
                sb.append("  D=M\n");
                sb.append("  A=A-1\n");

                if (operation.equals(ADD)) sb.append("  M=M+D\n"); // x + y
                if (operation.equals(SUB)) sb.append("  M=M-D\n"); // x - y
                if (operation.equals(AND)) sb.append("  M=M&D\n"); // x & y
                if (operation.equals(OR)) sb.append("  M=M|D\n");  // x | y

                sb.append("  @SP\n");
                sb.append("  M=M-1\n");
                break;

            case EQ:
            case GT:
            case LT:
                this.eqIndex++;
                sb.append("  @SP\n");
                sb.append("  A=M-1\n");
                sb.append("  D=M\n"); // D=y
                sb.append("  A=A-1\n"); //M=x
                sb.append("  D=M-D\n"); // D = x-y
                sb.append("  @EQUALITY" + this.eqIndex + "\n");

                if (operation.equals(EQ)) sb.append("  D;JEQ\n"); //Jump if D=0, i.e. y=x
                if (operation.equals(GT)) sb.append("  D;JGT\n"); //Jump if D>0, i.e. x>y
                if (operation.equals(LT)) sb.append("  D;JLT\n"); //Jump if D<0, i.e. x<y

                sb.append("  D=0\n"); // false
                sb.append("  @CONTINUE_EQ" + this.eqIndex + "\n");
                sb.append("  0;JMP\n");
                sb.append("(EQUALITY" + this.eqIndex + ")\n");
                sb.append("  D=-1\n"); // true
                sb.append("(CONTINUE_EQ" + this.eqIndex + ")\n");
                sb.append("  @SP\n");
                sb.append("  A=M-1\n"); // M=addr(y)
                sb.append("  A=A-1\n"); // A=addr(x); M=x
                sb.append("  M=D\n");   // x = D (true=-1;false=0)
                sb.append("  @SP\n");
                sb.append("  M=M-1\n");
                break;

            case NEG:
            case NOT:
                sb.append("  @SP\n");
                sb.append("  A=M-1\n");
                if (operation.equals(NEG)) sb.append("  M=-M\n"); // x = -x
                if (operation.equals(NOT)) sb.append("  M=!M\n"); // x = !x
                break;
        }

        return sb;
    }

    /**
     * Method to build a call command in assembly to write to the output file
     *
     * @param parsedLine
     * @return this.sb containing the translated call command.
     */
    private StringBuffer buildCallCommand(HashMap<String, String> parsedLine) {
        StringBuffer sb = new StringBuffer();
        String functionName = parsedLine.get(ARG1);
        String numArgs = parsedLine.get(ARG2);
        Objects.requireNonNull(functionName, numArgs);

        int i = Integer.parseInt(numArgs);
        sb.append("// " + parsedLine.get(LINE) + "\n");

        labelCnt++;
        String returnLabel = "RETURN_LABEL" + labelCnt;

        sb.append("@" + returnLabel + "\n");
        sb.append("D=A\n");
        sb.append("@SP\n");
        sb.append("A=M\n");
        sb.append("M=D\n");
        sb.append("@SP\n");
        sb.append("M=M+1\n");
        sb.append(pushTemplate1("LCL", 0, true));
        sb.append(pushTemplate1("ARG", 0, true));
        sb.append(pushTemplate1("THIS", 0, true));
        sb.append(pushTemplate1("THAT", 0, true));
        sb.append("@SP\n");
        sb.append("D=M\n");
        sb.append("@5\n");
        sb.append("D=D-A\n");
        sb.append("@" + i + "\n");
        sb.append("D=D-A\n");
        sb.append("@ARG\n");
        sb.append("M=D\n");
        sb.append("@SP\n");
        sb.append("D=M\n");
        sb.append("@LCL\n");
        sb.append("M=D\n");
        sb.append("@" + functionName + "\n");
        sb.append("0;JMP\n");
        sb.append("(" + returnLabel + ")\n");

        return sb;
    }

    /**
     * Method to build a Function command in assembly to write to the output file
     *
     * @param parsedLine
     * @return this.sb containing the translated function command.
     */
    private StringBuffer buildFunctionCommand(HashMap<String, String> parsedLine) {

        StringBuffer sb = new StringBuffer();
        String functionName = parsedLine.get(ARG1);
        String numArgs = parsedLine.get(ARG2);
        Objects.requireNonNull(functionName, numArgs);

        int i = Integer.parseInt(numArgs);

        setInFunction(true);
        if (!getCurrentFunctionName().equals(functionName)) setCurrentFunctionName(functionName);

        sb.append("// " + parsedLine.get(LINE) + "\n");

        sb.append("(" + functionName + ")\n");

        for (int j = 0; j < i; j++) {
            StringBuffer tempBuffer = buildConstantPush(0);
            sb.append(tempBuffer);

        }

        return sb;
    }

    /**
     * method to build a go to command in assemly to write to the output file.
     *
     * @param parsedLine
     * @return this.sb containing the translated goto command.
     */
    private StringBuffer buildGoToCommand(HashMap<String, String> parsedLine, boolean inFunction, String currentFunction) {
        StringBuffer sb = new StringBuffer();
        sb.append("// " + parsedLine.get(LINE) + "\n");

        // String jumpLabel = parsedLine.get(ARG1);
        String jumpLabel = (inFunction) ? (currentFunction + "$" + parsedLine.get(ARG1)) : parsedLine.get(ARG1);
        sb.append("  @" + jumpLabel + "\n");
        sb.append("  0;JMP\n");

        return sb;
    }

    /**
     * Method to build an if command in assembly to write to the output file.
     *
     * @param parsedLine
     * @return this.sb containing the translated if command.
     */
    private StringBuffer buildIfCommand(HashMap<String, String> parsedLine, boolean inFunction, String currentFunction) {
        StringBuffer sb = new StringBuffer();
        sb.append("// " + parsedLine.get(LINE) + "\n");

        // String jumpLabel = parsedLine.get(ARG1);
        String jumpLabel = (inFunction) ? (currentFunction + "$" + parsedLine.get(ARG1)) : parsedLine.get(ARG1);

        sb.append("  @SP\n");
        sb.append("  M=M-1\n");
        sb.append("  A=M\n");
        sb.append("  D=M\n");
        sb.append("  @" + jumpLabel + "\n");
        sb.append("  D;JNE\n");

        return sb;
    }

    /**
     * Method to build a label command in assembly to write to the output file.
     *
     * @param parsedLine
     * @return this.sb containing the translated Label command.
     */
    private StringBuffer buildLabelCommand(HashMap<String, String> parsedLine, boolean inFunction, String functionName) {
        StringBuffer sb = new StringBuffer();
        sb.append("// " + parsedLine.get(LINE) + "\n");

        String label = (inFunction) ? (functionName + "$" + parsedLine.get(ARG1)) : parsedLine.get(ARG1);
        sb.append("(" + label + ")\n");
        return sb;
    }

    /**
     * Method to build a return command in assembly to be written to the output file.
     *
     * @param parsedLine
     * @return this.sb containing translated return command.
     */
    private StringBuffer buildReturnCommand(HashMap<String, String> parsedLine) {
        StringBuffer sb = new StringBuffer();
        sb.append("// " + parsedLine.get(LINE) + "\n");

        sb.append("@LCL\n");
        sb.append("D=M\n");
        sb.append("@R11\n");
        sb.append("M=D\n");
        sb.append("@5\n");
        sb.append("A=D-A\n");
        sb.append("D=M\n");
        sb.append("@R12\n");
        sb.append("M=D\n");

        sb.append("@ARG\n");
        sb.append("D=M\n");
        sb.append("@0\n");
        sb.append("D=D+A\n");
        sb.append("@R13\n");
        sb.append("M=D\n");
        sb.append("@SP\n");
        sb.append("AM=M-1\n");
        sb.append("D=M\n");
        sb.append("@R13\n");
        sb.append("A=M\n");
        sb.append("M=D\n");


        sb.append("@ARG\n");
        sb.append("D=M\n");
        sb.append("@SP\n");
        sb.append("M=D+1\n");
        sb.append(preFrameTemplate("THAT"));
        sb.append(preFrameTemplate("THIS"));
        sb.append(preFrameTemplate("ARG"));
        sb.append(preFrameTemplate("LCL"));
        sb.append("@R12\n");
        sb.append("A=M\n");
        sb.append("0;JMP\n");

        return sb;
    }

    /**
     * Method to write a push/pop command in assembly to the output file.
     *
     * @param parsedLine
     * @return
     */
    private StringBuffer buildPushPopCommand(HashMap<String, String> parsedLine) {
        StringBuffer sb = new StringBuffer();
        String arg1 = parsedLine.get(ARG1);
        String arg2 = parsedLine.get(ARG2);
        Objects.requireNonNull(arg1, arg2);

        int i = Integer.parseInt(arg2);
        String segmentLabel = this.getSegmentLabel(arg1, i);
        String commandType = parsedLine.get(COMMAND_TYPE);

        // command format:
        //  push local 3
        //  --> push    = parsedLine.get(COMMAND_TYPE)
        //  --> local   = arg1
        //  --> 3       = arg2 & i

        sb.append("// " + parsedLine.get(LINE) + "\n");

        switch (arg1) {
            case ARGUMENT:
            case LOCAL:
            case THAT:
            case THIS:
                sb = this.buildGeneralPushPop(commandType, segmentLabel, i);
                break;
            case CONSTANT:
                sb = this.buildConstantPush(i);
                break;
            case POINTER:
                sb = this.buildPointerPushPop(commandType, segmentLabel);
                break;
            case STATIC:
                sb = this.buildStaticPushPop(commandType, segmentLabel);
                break;
            case TEMP:
                sb = this.buildTempPushPop(commandType, segmentLabel, i);
                break;
        }

        return sb;
    }

    /**
     * Method to build the StringBuffer containing the .asm code translated from
     * the input vmCode command.
     *
     * @param i index of Constant segment we are pushing to.
     * @return this.sb with the translated Constant push command.
     */
    private StringBuffer buildConstantPush(int i) {
        StringBuffer sb = new StringBuffer();
        sb.append("  @" + i + " \n");
        sb.append("  D=A\n");
        sb.append("  @SP\n");
        sb.append("  A=M\n");
        sb.append("  M=D\n");
        sb.append("  @SP\n");
        sb.append("  M=M+1\n");

        return sb;
    }

    /**
     * Method to build a StringBuffer containing the translated vmCode for push/pop
     * commands relevant to the Argument, Local, This, or That segments.
     *
     * @param commandType
     * @param segmentLabel
     * @param i
     * @return this.sb containing the translated push/pop command.
     */
    private StringBuffer buildGeneralPushPop(String commandType, String segmentLabel, int i) {

        StringBuffer sb = new StringBuffer();
        switch (commandType) {
            case VMParser.C_PUSH:
                sb.append("  @" + i + "\n");
                sb.append("  D=A\n"); // D = i
                sb.append("  @");
                sb.append(segmentLabel); // A = SEG
                sb.append("\n  A=M+D\n"); // A = RAM[SEG+D] (the base address value for that segment + D, where D=i)
                sb.append("  D=M\n"); // D = *RAM[RAM[SEG+i]]
                sb.append("  @SP\n");
                sb.append("  A=M\n");
                sb.append("  M=D\n");
                sb.append("  @SP\n");
                sb.append("  M=M+1\n");
                break;

            case VMParser.C_POP:
                sb.append("  @" + i + "\n");
                sb.append("  D=A\n");
                sb.append("  @" + segmentLabel + "\n");
                sb.append("  D=D+M\n");
                sb.append("  @R15\n");
                sb.append("  M=D\n");
                sb.append("  @SP\n");
                sb.append("  M=M-1\n");
                sb.append("  A=M\n");
                sb.append("  D=M\n");
                sb.append("  @R15\n");
                sb.append("  A=M\n");
                sb.append("  M=D\n");
                break;

            default:
                System.out.println("Command Type not recognized: " + commandType);
        }

        return sb;
    }


    /**
     * Method to build a StringBuffer containing the translated vmCode for push/pop
     * commands relevant to the Pointer segment.
     *
     * @param commandType
     * @param segmentLabel
     * @return this.sb containing translated push/pop command.
     */
    private StringBuffer buildPointerPushPop(String commandType, String segmentLabel) {

        StringBuffer sb = new StringBuffer();
        switch (commandType) {
            case VMParser.C_PUSH:
                sb.append("  @");
                sb.append(segmentLabel); // A = SEG
                sb.append("\n");
                sb.append("  D=M\n"); // D = RAM[SEG+i]
                sb.append("  @SP\n");
                sb.append("  A=M\n");
                sb.append("  M=D\n");
                sb.append("  @SP\n");
                sb.append("  M=M+1\n");
                break;

            case VMParser.C_POP:
                sb.append("  @SP\n");
                sb.append("  M=M-1\n");
                sb.append("  A=M\n");
                sb.append("  D=M\n");
                sb.append("  @");
                sb.append(segmentLabel);
                sb.append("\n");
                sb.append("  M=D\n");
                break;

            default:
                System.out.println("Command Type not recognized: " + commandType);
        }

        return sb;
    }

    /**
     * Method to build a StringBuffer containing the translated vmCode for push/pop
     * commands relevant to the Static segment.
     *
     * @param commandType
     * @param segmentLabel
     * @return this.sb containing translated push/pop command.
     */

    //to do switch arg1 argument to commandType
    private StringBuffer buildStaticPushPop(String commandType, String segmentLabel) {

        StringBuffer sb = new StringBuffer();
        switch (commandType) {
            case VMParser.C_PUSH:
                sb.append("  @");
                sb.append(segmentLabel); // A = SEG
                sb.append("\n");
                sb.append("  D=M\n"); // D = RAM[SEG+i]
                sb.append("  @SP\n");
                sb.append("  A=M\n");
                sb.append("  M=D\n");
                sb.append("  @SP\n");
                sb.append("  M=M+1\n");
                break;

            case VMParser.C_POP:
                sb.append("  @SP\n");
                sb.append("  M=M-1\n");
                sb.append("  A=M\n");
                sb.append("  D=M\n");
                sb.append("  @");
                sb.append(segmentLabel);
                sb.append("\n");
                sb.append("  M=D\n");
                break;

            default:
                System.out.println("CommandType not recognized: " + commandType);
        }

        return sb;
    }

    /**
     * Method to build a StringBuffer containing the translated vmCode for push/pop
     * commands relevant to the Temp segment.
     *
     * @param commandType
     * @param segmentLabel
     * @param i
     * @return this.sb containing translated push/pop command.
     */
    private StringBuffer buildTempPushPop(String commandType, String segmentLabel, int i) {

        StringBuffer sb = new StringBuffer();
        switch (commandType) {
            case VMParser.C_PUSH:
                sb.append("  @" + i + "\n");
                sb.append("  D=A\n");
                sb.append("  @" + segmentLabel + "\n");
                sb.append("  A=A+D\n"); // A = SEG + i
                sb.append("  D=M\n"); // D = RAM[SEG+i]
                sb.append("  @SP\n");
                sb.append("  A=M\n");
                sb.append("  M=D\n");
                sb.append("  @SP\n");
                sb.append("  M=M+1\n");
                break;

            case VMParser.C_POP:

                sb.append("  @" + i + "\n");
                sb.append("  D=A\n");
                sb.append("  @" + segmentLabel + "\n");
                sb.append("  D=D+A\n");
                sb.append("  @R15\n");
                sb.append("  M=D\n");
                sb.append("  @SP\n");
                sb.append("  M=M-1\n");
                sb.append("  A=M\n");
                sb.append("  D=M\n");
                sb.append("  @R15\n");
                sb.append("  A=M\n");
                sb.append("  M=D\n");
                break;

            default:
                System.out.println("Command Type not recognized: " + commandType);
        }

        return sb;
    }

    /**
     * Method to get the segment Label for assembly code.
     *
     * @param segment
     * @return
     */
    private String getSegmentLabel(String segment, int i) {
        switch (segment) {
            case LOCAL:
                return "LCL";
            case ARGUMENT:
                return "ARG";
            case THIS:
                return "THIS";
            case THAT:
                return "THAT";
            case TEMP:
                return "5";
            case POINTER:
                return (i == 0) ? "THIS" : "THAT";
            case STATIC:
                return this.getCurrentFile() + "." + i;
            default:
                return null;
        }
    }

    /**
     * Method to build the asm code to save the value
     * of pre fram to given position.
     *
     * @param position
     * @return
     */
    public String preFrameTemplate(String position) {

        return "@R11\n" +
                "D=M-1\n" +
                "AM=D\n" +
                "D=M\n" +
                "@" + position + "\n" +
                "M=D\n";

    }

    /**
     * Template for push local,this,that,argument,temp,pointer,static
     *
     * @param segment
     * @param index
     * @param isDirect Is this command a direct addressing?
     * @return
     */
    private String pushTemplate1(String segment, int index, boolean isDirect) {

        //When it is a pointer, just read the data stored in THIS or THAT
        String noPointerCode = (isDirect) ? "" : "@" + index + "\n" + "A=D+A\nD=M\n";

        return "@" + segment + "\n" +
                "D=M\n" +
                noPointerCode +
                "@SP\n" +
                "A=M\n" +
                "M=D\n" +
                "@SP\n" +
                "M=M+1\n";

    }


    /**
     * Method to return the inFunction value.
     *
     * @return
     */
    private boolean getInFunction() {
        return this.inFunction;
    }

    /**
     * Method to set the flag indicating whether we are translating a function
     *
     * @param inFunction
     */
    private void setInFunction(boolean inFunction) {
        this.inFunction = inFunction;
    }

    /**
     * Method to retrieve the name of the function we are currently translating.
     *
     * @return
     */
    private String getCurrentFunctionName() {
        return this.currentFunction;
    }

    /**
     * Method to set the name of hte function we are currently translating.
     *
     * @param functionName
     */
    private void setCurrentFunctionName(String functionName) {
        this.currentFunction = functionName;
    }

    private void resetCurrentFunctionName() {
        this.currentFunction = NO_FUNCTION;
    }

    /**
     * Method to get the name of the current file being translated
     *
     * @return String representation of the curren;t file being translated.
     */
    private String getCurrentFile() {
        return this.currentFileName;
    }

    /**
     * Method to set the name of the current file being translated.
     * NOTE: Also writes a comment line to the output file indicating
     * the change in file.
     *
     * @param file
     * @throws IOException
     */
    public void setCurrentFile(File file) throws IOException {

        String fileName = file.getName();
        this.currentFileName = (fileName.endsWith(".vm")) ? fileName.replace(".vm", "") : fileName;

        // write comment line into output file indicating a new file is being translated
        String newFileComment = "\n// Translating new file: " + fileName + "\n\n";
        this.write(newFileComment);
        setInFunction(false);
        resetCurrentFunctionName();

    }

    /**
     * Method to close the CodeRunner's bufferedWriter.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        this.writer.close();
    }

}
