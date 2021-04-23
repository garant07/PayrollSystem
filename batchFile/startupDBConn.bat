@echo off
set TMP_CLASSPATH=%CLASSPATH%

for %%i in (%0) do cd /d %%~dpi\..


set CLASSPATH=%CLASSPATH%
rem Add all jars....
for %%i in (".\library\*.jar") do call ".\batchFile\cpappend.bat" %%i

set ICLASSPATH=%CLASSPATH%
set CLASSPATH=%TMP_CLASSPATH%




java -cp "%ICLASSPATH%" maintenance.bin.src.DBConn.DbaseConnectorFrame update

