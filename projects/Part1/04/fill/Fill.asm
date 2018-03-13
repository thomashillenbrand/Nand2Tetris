// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm
// Updated by Tom Hillenbrand on 1/28/2017

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.


//Initialize Screen and Keyboard constant values:
@KBD
D=A
@lastValue
M=D					// Set lastValue to be KBD = 24576

@SCREEN
D=A
@address
M=D					// address = 16384

(LOOP)
	//Check address to see if the next register is viable
	@lastValue
	D=M
	@address
	D=D-M
	@RESET
	D;JEQ			//Head back to START section if final pixel register has been reached.

	@KBD
	D=M
	@CLEAR 			
	D;JEQ 			// Jump to CLEAR section if no keyboard input.

	@address
	A=M
	M=-1			// RAM[address] = -1 = 1111111111111111

	@address
	M=M+1			// Increment over one 16-bit column (register)

	@LOOP			
	0;JMP 			// Return to beginning of LOOP

(CLEAR)
	@address
	A=M
	M=0				// RAM[address] = 0 = 0000000000000000

	@address
	M=M+1			// Increment over one 16-bit column (register)

	@LOOP
	0;JMP 			// Return to beginning of LOOP


(RESET)
	@SCREEN
	D=A
	@address
	M=D 			// reset address to first SCREEN register
	@LOOP 			//Return to LOOP section.
	0;JMP

(END)
	@END
	0;JMP



