cp -r ../lib/* ../artifacts/info.kgeorgiy.java.advanced.base.jar ../artifacts/info.kgeorgiy.java.advanced.hello.jar
cp -r ../src/* .
cp -r ../modules/info.kgeorgiy.java.advanced.base/info .
cp -r ../modules/info.kgeorgiy.java.advanced.hello/info .
javac ru/ifmo/rain/dolgikh/hello/*.java && java -cp . -p . -m info.kgeorgiy.java.advanced.hello client ru.ifmo.rain.dolgikh.hello.HelloUDPClient