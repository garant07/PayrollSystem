@echo off
set TMP_CLASSPATH=%CLASSPATH%

for %%i in (%0) do cd /d %%~dpi\..


set CLASSPATH=%CLASSPATH%
rem Add all jars....
for %%i in (".\library\*.jar") do call ".\batchFile\cpappend.bat" %%i

set ICLASSPATH=%CLASSPATH%
set CLASSPATH=%TMP_CLASSPATH%



cls
java -cp "%ICLASSPATH%" -Xms128m -Xmx256m  maintenance.bin.src.core.mainform

