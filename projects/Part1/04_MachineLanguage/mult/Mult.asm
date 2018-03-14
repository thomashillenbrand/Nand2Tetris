// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm
// Updated by Tom Hillenbrand on 1/28/2018

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

// Put your code here.

@R0
D=M
@j
M=D			// j = R0

@R1
D=M
@k
M=D			// k = R1

@R2
M=0			// initialize R2 to 0

@i
M=0			// initialize i = 0

@total
M=0			// initialize total = 0


(LOOP)
	@j
	D=M
	@i
	D=M-D
	@STOP
	D;JEQ	// if i == j, got to STOP

	@k
	D=M
	@total
	M=D+M 	// Add k to the total

	@i
	M=M+1	// incerement i

	@LOOP
	0;JMP


(STOP)			// Stop section
	@total
	D=M
	@R2
	M=D			//R2 = RAM[2] = total


(END)			// End section
	@END
	0;JMP