javac -cp ..\artifacts\* -d . ..\src\ru\ifmo\rain\dolgikh\implementor\Implementor.java
javac -d . ..\modules\info.kgeorgiy.java.advanced.implementor\info\kgeorgiy\java\advanced\implementor\ImplerException.java
javac -d . ..\modules\info.kgeorgiy.java.advanced.implementor\info\kgeorgiy\java\advanced\implementor\Impler.java
javac -d . ..\modules\info.kgeorgiy.java.advanced.implementor\info\kgeorgiy\java\advanced\implementor\JarImpler.java

jar cvfe Implementor.jar ru.ifmo.rain.dolgikh.implementor.Implementor ru\ifmo\rain\dolgikh\implementor\*.class info\kgeorgiy\java\advanced\implementor\*.class
