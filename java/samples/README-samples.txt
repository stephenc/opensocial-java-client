This folder contains a number of simple Java samples that use the accompanying library to retrieve
information from various OpenSocial containers.

TO RUN (WITH ECLIPSE):
----------------------
1. Open Eclipse and create a new Java project (File->New->Java Project).
2. Enter a project name and click the radio button next to "Create project from existing source".
Click the "Browse" button directly below and navigate to the opensocial-java-client/java directory
in the file browser that appears. Once you've highlighted the java directory, click "Choose".
3. Click "Finish" in Eclipse's "New Java Project" window.
4. Expand the new project folder in Eclipse's Package Explorer tab and the samples directory
beneath that.
5. Click a sample under the default package and click the Run button in the toolbar or choose
Run->Run. If a dialog appears, choose "Java application". The output should be displayed under
Eclipse's Console tab.



TO RUN (WITHOUT ECLIPSE):
-------------------------
1. Generate a Java archive of the library and compile the samples: open a terminal window,
navigate to trunk/java, and execute the "compile-samples" Ant target (requires Ant, which you can
download from http://ant.apache.org/):

ant compile-samples


2. Navigate to trunk/java/samples/bin (the location of the compiled samples) and set your class path:

2a) UNIX (bash) w/ JDK 6:
export CLASSPATH=../../dist/opensocial.jar:../../lib/*:.

2b) UNIX (bash) w/ JDK 5
export CLASSPATH=../../dist/opensocial.jar:../../lib/commons-codec-1.3.jar:../../lib/oauth-core-20090105.jar:../../lib/json.jar:.

2c) Windows:
set CLASSPATH=..\..\dist\opensocial.jar;..\..\lib\*;.


3. Execute the sample by typing "java" followed by the name of the class without the .class extension:

java DisplayFriends