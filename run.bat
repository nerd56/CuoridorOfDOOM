javac -d bin src\cuoridorofdoom\*.java

jar cfe CuoridorOfDOOM.jar cuoridorofdoom.CuoridorOfDOOM -C bin . src res

java -jar CuoridorOfDOOM.jar
