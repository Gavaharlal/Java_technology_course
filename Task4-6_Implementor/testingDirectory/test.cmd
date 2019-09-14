cp -r ../lib/* ../artifacts/info.kgeorgiy.java.advanced.base.jar ../artifacts/info.kgeorgiy.java.advanced.implementor.jar .
cp -r ../src/* .
cp -r ../modules/info.kgeorgiy.java.advanced.base/info .
cp -r ../modules/info.kgeorgiy.java.advanced.implementor/info .
javac ru/ifmo/rain/dolgikh/implementor/*.java && java -cp . -p . -m info.kgeorgiy.java.advanced.implementor jar-interface ru.ifmo.rain.dolgikh.implementor.Implementor