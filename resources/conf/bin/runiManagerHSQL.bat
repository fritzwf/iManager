@echo off
set WORKING_DIR=..

pushd %WORKING_DIR%

cls
title %0

if "%JAVA_BIN%"=="" goto jrehome
echo Setting Java to %JAVA_BIN% location.
goto runprogram
:jrehome
if "%JRE_HOME%"=="" goto jdkhome
echo Setting Java to %JRE_HOME% location.
set JAVA_BIN=%JRE_HOME%\bin\javaw
goto runprogram
:jdkhome
if "%JDK_HOME%"=="" goto javahome
echo Setting Java to %JDK_HOME% location.
set JAVA_BIN=%JDK_HOME%\jre\bin\javaw
goto runprogram
:javahome
if "%JAVA_HOME%"=="" goto nojavavar
echo Setting Java to %JAVA_HOME% location.
set JAVA_BIN=%JAVA_HOME%\jre\bin\javaw
goto runprogram
:nojavavar
echo Did not find JAVA, JDK_HOME, JRE_HOME, or JAVA_HOME enivronment variables.
echo Please install Java JRE 8.x to run this program.
echo.
popd
goto exit
:runprogram

set HIBERNATE=lib\hibernate\antlr-2.7.7.jar;lib\hibernate\byte-buddy-1.8.12.jar;lib\hibernate\classmate-1.3.4.jar;lib\hibernate\dom4j-1.6.1.jar;lib\hibernate\hibernate-commons-annotations-5.0.4.Final.jar;lib\hibernate\hibernate-core-5.3.2.Final.jar;lib\hibernate\jandex-2.0.5.Final.jar;lib\hibernate\javassist-3.22.0-GA.jar;lib\hibernate\javax.activation-api-1.2.0.jar;lib\hibernate\javax.persistence-api-2.2.jar;lib\hibernate\jboss-logging-3.3.2.Final.jar;lib\hibernate\jboss-transaction-api_1.2_spec-1.1.1.Final.jar;lib\hibernate\c3p0-0.9.5.2.jarlib\hibernate\hibernate-c3p0-5.3.2.Final.jar;lib\hibernate\mchange-commons-java-0.2.11.jar
set INV_CLASSPATH=conf;lib\iManager.jar;lib\dom4j\dom4j-1.6.1.jar;lib\dom4j\jaxen-1.1-beta-7.jar;%HIBERNATE%;lib\hsqldb\hsqldb.jar;lib\logback\logback-classic-0.9.8.jar;lib\logback\logback-core-0.9.8.jar;lib\slf4j\jcl104-over-slf4j-1.4.3.jar;lib\slf4j\log4j-over-slf4j-1.4.3.jar;lib\slf4j\slf4j-api-1.4.3.jar;lib\sun\javamail\lib\mailapi.jar;lib\sun\javamail\lib\smtp.jar;lib\sun\javamail\lib\dsn.jar;lib\sun\javamail\lib\imap.jar;lib\sun\javamail\lib\pop3.jar;lib\netbeans\swing-layout-1.0.4;lib\netbeans\swing-worker-1.1.jar;lib\swinglabs\swingx-0.9.2.jar;lib\jfreechart\jcommon-1.0.16.jar;lib\jfreechart\jfreechart-1.0.13.jar;lib\javax\jce1_2_2.jar;lib\javax\local_policy.jar;lib\javax\US_export_policy.jar;lib\javax\sunjce_provider.jar

echo %INV_CLASSPATH%
set VM_OPTIONS=-Xms128m -Xmx1024m -Dimanager.persistence.unit=imanager-HSQL -Dlogback.include.file=stdout-include-logback.xml -Dlogback.appender.file.name=iManager -splash:./conf/splash.png

set MAIN_CLASS=com.feuersoft.imanager.ui.InvManagerMainFrame

REM To set a different GUI look and feel, use one of the following below: "nimbus", "motif", "gtk", "windows"
pause
start "" "%JAVA_BIN%" -classpath "%INV_CLASSPATH%" %VM_OPTIONS% %MAIN_CLASS% "windows"
:exit
popd


