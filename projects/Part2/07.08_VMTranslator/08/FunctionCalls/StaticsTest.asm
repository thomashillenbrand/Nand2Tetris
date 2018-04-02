
// Translating new file: Sys.vm

// function Sys.init 0
// push constant 6
  @6 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// push constant 8
  @8 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// call Class1.set 2
// pop temp 0
  @0
  D=A
  @5
  D=D+A
  @R15
  M=D
  @SP
  M=M-1
  A=M
  D=M
  @R15
  A=M
  M=D
// push constant 23
  @23 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// push constant 15
  @15 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// call Class2.set 2
// pop temp 0
  @0
  D=A
  @5
  D=D+A
  @R15
  M=D
  @SP
  M=M-1
  A=M
  D=M
  @R15
  A=M
  M=D
// call Class1.get 0
// call Class2.get 0
// label WHILE
(WHILE)
// goto WHILE
  @WHILE
  0;JMP

// Translating new file: Class1.vm

// function Class1.set 0
// push argument 0
  @0
  D=A
  @ARG
  A=M+D
  D=M
  @SP
  A=M
  M=D
  @SP
  M=M+1
// pop static 0
  @SP
  M=M-1
  A=M
  D=M
  @Class1.0
  M=D
// push argument 1
  @1
  D=A
  @ARG
  A=M+D
  D=M
  @SP
  A=M
  M=D
  @SP
  M=M+1
// pop static 1
  @SP
  M=M-1
  A=M
  D=M
  @Class1.1
  M=D
// push constant 0
  @0 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// return
// function Class1.get 0
// push static 0
  @Class1.0
  D=M
  @SP
  A=M
  M=D
  @SP
  M=M+1
// push static 1
  @Class1.1
  D=M
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
// return

// Translating new file: Class2.vm

// function Class2.set 0
// push argument 0
  @0
  D=A
  @ARG
  A=M+D
  D=M
  @SP
  A=M
  M=D
  @SP
  M=M+1
// pop static 0
  @SP
  M=M-1
  A=M
  D=M
  @Class2.0
  M=D
// push argument 1
  @1
  D=A
  @ARG
  A=M+D
  D=M
  @SP
  A=M
  M=D
  @SP
  M=M+1
// pop static 1
  @SP
  M=M-1
  A=M
  D=M
  @Class2.1
  M=D
// push constant 0
  @0 
  D=A
  @SP
  A=M
  M=D
  @SP
  M=M+1
// return
// function Class2.get 0
// push static 0
  @Class2.0
  D=M
  @SP
  A=M
  M=D
  @SP
  M=M+1
// push static 1
  @Class2.1
  D=M
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
// return
