// push constant 17
  @17 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// push constant 17
  @17 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// eq
  @SP
  A=M-1
  D=M
  A=A-1
  D=M-D
  @EQUALITY1
  D;JEQ
  D=0
  @CONTINUE_EQ1
  0;JMP
(EQUALITY1)
  D=-1
(CONTINUE_EQ1)
  @SP
  A=M-1
  A=A-1
  M=D
  @SP
  M=M-1
// push constant 17
  @17 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// push constant 16
  @16 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// eq
  @SP
  A=M-1
  D=M
  A=A-1
  D=M-D
  @EQUALITY2
  D;JEQ
  D=0
  @CONTINUE_EQ2
  0;JMP
(EQUALITY2)
  D=-1
(CONTINUE_EQ2)
  @SP
  A=M-1
  A=A-1
  M=D
  @SP
  M=M-1
// push constant 16
  @16 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// push constant 17
  @17 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// eq
  @SP
  A=M-1
  D=M
  A=A-1
  D=M-D
  @EQUALITY3
  D;JEQ
  D=0
  @CONTINUE_EQ3
  0;JMP
(EQUALITY3)
  D=-1
(CONTINUE_EQ3)
  @SP
  A=M-1
  A=A-1
  M=D
  @SP
  M=M-1
// push constant 892
  @892 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// push constant 891
  @891 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// lt
  @SP
  A=M-1
  D=M
  A=A-1
  D=M-D
  @EQUALITY4
  D;JLT
  D=0
  @CONTINUE_EQ4
  0;JMP
(EQUALITY4)
  D=-1
(CONTINUE_EQ4)
  @SP
  A=M-1
  A=A-1
  M=D
  @SP
  M=M-1
// push constant 891
  @891 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// push constant 892
  @892 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// lt
  @SP
  A=M-1
  D=M
  A=A-1
  D=M-D
  @EQUALITY5
  D;JLT
  D=0
  @CONTINUE_EQ5
  0;JMP
(EQUALITY5)
  D=-1
(CONTINUE_EQ5)
  @SP
  A=M-1
  A=A-1
  M=D
  @SP
  M=M-1
// push constant 891
  @891 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// push constant 891
  @891 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// lt
  @SP
  A=M-1
  D=M
  A=A-1
  D=M-D
  @EQUALITY6
  D;JLT
  D=0
  @CONTINUE_EQ6
  0;JMP
(EQUALITY6)
  D=-1
(CONTINUE_EQ6)
  @SP
  A=M-1
  A=A-1
  M=D
  @SP
  M=M-1
// push constant 32767
  @32767 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// push constant 32766
  @32766 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// gt
  @SP
  A=M-1
  D=M
  A=A-1
  D=M-D
  @EQUALITY7
  D;JGT
  D=0
  @CONTINUE_EQ7
  0;JMP
(EQUALITY7)
  D=-1
(CONTINUE_EQ7)
  @SP
  A=M-1
  A=A-1
  M=D
  @SP
  M=M-1
// push constant 32766
  @32766 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// push constant 32767
  @32767 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// gt
  @SP
  A=M-1
  D=M
  A=A-1
  D=M-D
  @EQUALITY8
  D;JGT
  D=0
  @CONTINUE_EQ8
  0;JMP
(EQUALITY8)
  D=-1
(CONTINUE_EQ8)
  @SP
  A=M-1
  A=A-1
  M=D
  @SP
  M=M-1
// push constant 32766
  @32766 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// push constant 32766
  @32766 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// gt
  @SP
  A=M-1
  D=M
  A=A-1
  D=M-D
  @EQUALITY9
  D;JGT
  D=0
  @CONTINUE_EQ9
  0;JMP
(EQUALITY9)
  D=-1
(CONTINUE_EQ9)
  @SP
  A=M-1
  A=A-1
  M=D
  @SP
  M=M-1
// push constant 57
  @57 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// push constant 31
  @31 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// push constant 53
  @53 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// add
  @SP
  A=M-1
  D=M
  A=A-1
  M=M+D
  @SP
  M=M-1
// push constant 112
  @112 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// sub
  @SP
  A=M-1
  D=M
  A=A-1
  M=M-D
  @SP
  M=M-1
// neg
  @SP
  A=M-1
  M=-M
// and
  @SP
  A=M-1
  D=M
  A=A-1
  M=M&D
  @SP
  M=M-1
// push constant 82
  @82 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// or
  @SP
  A=M-1
  D=M
  A=A-1
  M=M|D
  @SP
  M=M-1
// not
  @SP
  A=M-1
  M=!M
