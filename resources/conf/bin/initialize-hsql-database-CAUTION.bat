@echo off
set WORKING_DIR=..\..

pushd %WORKING_DIR%

if "%JAVA_BIN%"=="" goto jrehome
echo Setting Java to %JAVA_BIN% location.
goto runprogram
:jrehome
if "%JRE_HOME%"=="" goto jdkhome
echo Setting Java to %JRE_HOME% location.
set JAVA_BIN=%JRE_HOME%\bin\java
goto runprogram
:jdkhome
if "%JDK_HOME%"=="" goto javahome
echo Setting Java to %JDK_HOME% location.
set JAVA_BIN=%JDK_HOME%\jre\bin\java
goto runprogram
:javahome
if "%JAVA_HOME%"=="" goto nojavavar
echo Setting Java to %JAVA_HOME% location.
set JAVA_BIN=%JAVA_HOME%\jre\bin\java
goto runprogram
:nojavavar
echo Did not find JAVA, JDK_HOME, JRE_HOME, or JAVA_HOME enivronment variables.
echo Please install Java JRE 8.x to run this program.
echo.
popd
goto exit
:runprogram
echo .
echo .
echo *************  C  A  U  T  I  O  N  *************
echo .
echo THIS OPERATION WILL DELETE ALL OF YOUR CURRENT DATA !!!!!!!
echo .
echo Do you really want to proceed with this operation?
echo .
rmdir /S .\db

:exit

echo .
echo .
echo *** DONE ***, PLEASE CLOSE THIS WINDOW.
echo .
echo .

popd
pause
