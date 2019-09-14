cp -r ../java-advanced-2019/lib/* ../java-advanced-2019/artifacts/info.kgeorgiy.java.advanced.base.jar ../java-advanced-2019/artifacts/info.kgeorgiy.java.advanced.walk.jar .
cp -r ../src/* .
javac ru/ifmo/rain/dolgikh/walk/*.java && java -cp . -p . -m info.kgeorgiy.java.advanced.walk Walk ru.ifmo.rain.dolgikh.walk.Walk



