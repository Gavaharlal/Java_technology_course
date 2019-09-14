cp -r ../lib/* ../artifacts/info.kgeorgiy.java.advanced.base.jar ../artifacts/info.kgeorgiy.java.advanced.concurrent.jar .../artifacts/info.kgeorgiy.java.advanced.mapper.jar
cp -r ../src/* .
cp -r ../modules/info.kgeorgiy.java.advanced.base/info .
cp -r ../modules/info.kgeorgiy.java.advanced.concurrent/info .
cp -r ../modules/info.kgeorgiy.java.advanced.mapper/info .
javac ru/ifmo/rain/dolgikh/concurrent/*.java && java -cp . -p . -m info.kgeorgiy.java.advanced.mapper list ru.ifmo.rain.dolgikh.concurrent.ParallelMapperImpl,ru.ifmo.rain.dolgikh.concurrent.IterativeParallelism ada