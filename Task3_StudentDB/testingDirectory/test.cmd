cp -r ../lib/* ../artifacts/info.kgeorgiy.java.advanced.base.jar ../artifacts/info.kgeorgiy.java.advanced.student.jar .
cp -r ../src/* .
cp -r ../modules/info.kgeorgiy.java.advanced.base/info .
cp -r ../modules/info.kgeorgiy.java.advanced.student/info .
javac ru/ifmo/rain/dolgikh/student/*.java && java -cp . -p . -m info.kgeorgiy.java.advanced.student StudentQuery ru.ifmo.rain.dolgikh.student.StudentDB
