@echo off
set WORKING_DIR=..

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

set DB_CLASSPATH=conf;lib\hsqldb\hsqldb.jar

set VM_OPTIONS=-Xms32m -Xmx512m

set MAIN_CLASS=org.hsqldb.Server

"%JAVA_BIN%" -classpath "%DB_CLASSPATH%" %VM_OPTIONS% %MAIN_CLASS% -database.0 file:db/db

:exit
popd
