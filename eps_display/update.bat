@ECHO OFF
%1 mshta vbscript:CreateObject("WScript.Shell").Run("%~s0 ::",0,FALSE)(window.close)&&exit
@ping 127.0.0.1 -n 4
FOR /f "skip=1 delims=" %%i In ('dir /o-d /b /s "%cd%\*.jar"') do (
del "%%i"
)
FOR /f "delims=" %%i In ('dir *.jar /a/s/b') do (
java -jar "%%i"
)