@echo off
set JAVA_HOME=C:\Users\Wissen\.jdk\jdk-21.0.8
set PATH=%JAVA_HOME%\bin;%PATH%
cd /d "c:\Users\Wissen\Desktop\Calendar_wissen"
"C:\Users\Wissen\.maven\maven-3.9.12(1)\bin\mvn.cmd" exec:java -Dexec.mainClass=com.example.CalendarWidget