del /q bootstrap.jar

jar cvf0 bootstrap.jar -C  target/classes com/eryingzhang/tomcat/BootStrap.class  -C target/classes com/eryingzhang/tomcat/classloader/CommonClassLoader.class

del /q  src/main/resources/lib/diytomcat.jar
cd target/classes
jar cvf0 ../../src/main/resources/lib/diytomcat.jar *
cd ../../
java -cp bootstrap.jar   com.eryingzhang.tomcat.BootStrap
pause