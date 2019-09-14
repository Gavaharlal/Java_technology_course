cp -r ../java-advanced-2019/lib/* ../java-advanced-2019/artifacts/info.kgeorgiy.java.advanced.base.jar ../java-advanced-2019/artifacts/info.kgeorgiy.java.advanced.arrayset.jar .
cp -r ../src/* .
javac ru/ifmo/rain/dolgikh/arrayset/*.java && java -cp . -p . -m info.kgeorgiy.java.advanced.arrayset SortedSet ru.ifmo.rain.dolgikh.arrayset.ArraySet



