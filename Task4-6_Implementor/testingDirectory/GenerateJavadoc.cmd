SET DEPS=..\modules\info.kgeorgiy.java.advanced.implementor\info\kgeorgiy\java\advanced\implementor
SET LINK=https://docs.oracle.com/en/java/javase/11/docs/api/
SET PACKAGE=ru.ifmo.rain.dolgikh.implementor

javadoc -html5 -cp ..\src -link %LINK% -private %PACKAGE% %DEPS%\Impler.java %DEPS%\JarImpler.java %DEPS%\ImplerException.java -d javadoc