# Raspberry IO Expander JNI

## Setup

### Compiling C++ code:
```
g++ -fPIC -I$JAVA_HOME/include -I$JAVA_HOME/include/linux -shared -o libwire.o Wire.cpp
```

### Compiling Java class
```
javac Main.java
```

## Running it

### Run it
```
java -Djava.library.path=. Main
```
