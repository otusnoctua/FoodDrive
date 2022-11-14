@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  FoodDrive startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and FOOD_DRIVE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\FoodDrive.jar;%APP_HOME%\lib\java-jwt-3.19.2.jar;%APP_HOME%\lib\http4k-client-okhttp-4.33.1.0.jar;%APP_HOME%\lib\http4k-contract-4.33.1.0.jar;%APP_HOME%\lib\http4k-server-undertow-4.33.1.0.jar;%APP_HOME%\lib\http4k-format-jackson-4.33.1.0.jar;%APP_HOME%\lib\http4k-format-core-4.33.1.0.jar;%APP_HOME%\lib\http4k-template-pebble-4.33.1.0.jar;%APP_HOME%\lib\http4k-template-core-4.33.1.0.jar;%APP_HOME%\lib\http4k-realtime-core-4.33.1.0.jar;%APP_HOME%\lib\http4k-core-4.33.1.0.jar;%APP_HOME%\lib\okhttp-4.10.0.jar;%APP_HOME%\lib\okio-jvm-3.0.0.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-1.7.20.jar;%APP_HOME%\lib\jackson-annotations-2.13.4.jar;%APP_HOME%\lib\jackson-core-2.13.4.jar;%APP_HOME%\lib\jackson-module-kotlin-2.13.4.jar;%APP_HOME%\lib\jackson-databind-2.13.4.2.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-1.7.20.jar;%APP_HOME%\lib\kotlin-reflect-1.5.30.jar;%APP_HOME%\lib\kotlin-stdlib-1.7.20.jar;%APP_HOME%\lib\undertow-servlet-2.3.0.Final.jar;%APP_HOME%\lib\undertow-core-2.3.0.Final.jar;%APP_HOME%\lib\pebble-3.1.6.jar;%APP_HOME%\lib\kotlin-stdlib-common-1.7.20.jar;%APP_HOME%\lib\annotations-13.0.jar;%APP_HOME%\lib\xnio-nio-3.8.8.Final.jar;%APP_HOME%\lib\xnio-api-3.8.8.Final.jar;%APP_HOME%\lib\wildfly-client-config-1.0.1.Final.jar;%APP_HOME%\lib\jboss-threads-3.5.0.Final.jar;%APP_HOME%\lib\jboss-logging-3.4.3.Final.jar;%APP_HOME%\lib\jakarta.servlet-api-6.0.0.jar;%APP_HOME%\lib\jakarta.annotation-api-2.1.1.jar;%APP_HOME%\lib\unbescape-1.1.6.RELEASE.jar;%APP_HOME%\lib\slf4j-api-1.7.25.jar;%APP_HOME%\lib\wildfly-common-1.5.4.Final.jar


@rem Execute FoodDrive
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %FOOD_DRIVE_OPTS%  -classpath "%CLASSPATH%" ru.ac.uniyar.FoodDriveKt %*

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable FOOD_DRIVE_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%FOOD_DRIVE_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
