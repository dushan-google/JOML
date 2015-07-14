:: Make sure to have
:: - MinGW-64's gcc.exe in PATH
:: - upx.exe in PATH
:: - JAVA_HOME pointing to a JDK

minilua.exe dynasm\dynasm.lua -o codegen.win64.c -D X64 codegen.dasc
gcc -O3 -shared -o joml.dll -I"%JAVA_HOME%\include" -I"%JAVA_HOME%\include\win32" codegen.win64.c NativeOps.c
strip -x -s joml.dll
upx joml.dll
