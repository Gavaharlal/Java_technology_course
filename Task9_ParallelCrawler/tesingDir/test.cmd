cp -r ../lib/* ../artifacts/info.kgeorgiy.java.advanced.base.jar ../artifacts/info.kgeorgiy.java.advanced.crawler.jar 
cp -r ../src/* .
cp -r ../modules/info.kgeorgiy.java.advanced.base/info .
cp -r ../modules/info.kgeorgiy.java.advanced.crawler/info .
javac ru/ifmo/rain/dolgikh/crawler/*.java && java -cp . -p . -m info.kgeorgiy.java.advanced.crawler hard ru.ifmo.rain.dolgikh.crawler.WebCrawler