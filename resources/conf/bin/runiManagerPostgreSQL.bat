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

set INV_CLASSPATH=conf;lib\iManager.jar;lib\dom4j\dom4j-1.6.1.jar;lib\dom4j\jaxen-1.1-beta-7.jar;lib\hibernate\antlr-2.7.6.jar;lib\hibernate\asm-attrs.jar;lib\hibernate\asm.jar;lib\hibernate\c3p0-0.9.1.jar;lib\hibernate\cglib-2.1.3.jar;lib\hibernate\commons-collections-2.1.1.jar;lib\hibernate\ejb3-persistence.jar;lib\hibernate\hibernate-annotations.jar;lib\hibernate\hibernate-commons-annotations.jar;lib\hibernate\hibernate-entitymanager.jar;lib\hibernate\hibernate-validator.jar;lib\hibernate\hibernate3.jar;lib\hibernate\javassist.jar;lib\hibernate\jboss-archive-browsing.jar;lib\hibernate\jta.jar;lib\postgresql\postgresql.jdbc.jar;lib\logback\logback-classic-0.9.8.jar;lib\logback\logback-core-0.9.8.jar;lib\slf4j\jcl104-over-slf4j-1.4.3.jar;lib\slf4j\log4j-over-slf4j-1.4.3.jar;lib\slf4j\slf4j-api-1.4.3.jar;lib\sun\javamail\lib\mailapi.jar;lib\sun\javamail\lib\smtp.jar;lib\sun\javamail\lib\dsn.jar;lib\sun\javamail\lib\imap.jar;lib\sun\javamail\lib\pop3.jar;lib\netbeans\swing-layout-1.0.4;lib\netbeans\swing-worker-1.1.jar;lib\swinglabs\swingx-0.9.2.jar;lib\postgresql\postgresql-jdbc.jar;lib\jfreechart\jcommon-1.0.16.jar;lib\jfreechart\jfreechart-1.0.13.jar;lib\javax\jce1_2_2.jar;lib\javax\local_policy.jar;lib\javax\US_export_policy.jar;lib\javax\sunjce_provider.jar

set VM_OPTIONS=-Xms128m -Xmx1024m -Dimanager.persistence.unit=imanager-PostgreSQL -Dlogback.include.file=stdout-include-logback.xml -Dlogback.appender.file.name=iManager -splash:./conf/splash.png

set MAIN_CLASS=com.feuersoft.imanager.ui.InvManagerMainFrame

REM To set a different GUI look and feel, use one of the following below: "nimbus", "motif", "gtk", "windows"

start "" "%JAVA_BIN%" -classpath "%INV_CLASSPATH%" %VM_OPTIONS% %MAIN_CLASS% "windows"
:exit
popd

