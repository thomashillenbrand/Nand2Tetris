
// Translating new file: SimpleFunction.vm

// function SimpleFunction.test 2
// push local 0
  @0
  D=A
  @LCL
  A=M+D
  D=M
  @SP
  A=M
  M=D
  @SP
  M=M+1
// push local 1
  @1
  D=A
  @LCL
  A=M+D
  D=M
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
// not
  @SP
  A=M-1
  M=!M
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
// add
  @SP
  A=M-1
  D=M
  A=A-1
  M=M+D
  @SP
  M=M-1
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
// sub
  @SP
  A=M-1
  D=M
  A=A-1
  M=M-D
  @SP
  M=M-1
// return
