This folder contains a number of simple Java samples that use the accompanying library to retrieve information from various OpenSocial containers. These samples

TO RUN:
-------
1. Generate a Java archive of the library and compile the samples: open a terminal window, navigate to trunk/java, and execute the "compile-samples" Ant target (requires Ant, which you can download from http://ant.apache.org/):

ant compile-samples


2. Navigate to trunk/java/samples/bin (the location of the compiled samples) and set your class path:

2a) UNIX (bash):
export CLASSPATH=../../dist/opensocial.jar:../../lib/commons-codec-1.3.jar:../../lib/core-20081027.jar:.

2b) Windows:
set CLASSPATH=..\..\dist\opensocial.jar;..\..\lib\commons-codec-1.3.jar;..\..\lib\core-20081027.jar;.


3. Execute the sample by typing "java" followed by the name of the class without the .class extension:

java ListFriends