OpenSocial Java Client API Source
=======================================

Table of Contents
------------------
  1. Package Contents
  2. Requirements
  3. Installation


PACKAGE CONTENTS:
-----------------
OpenSocial Java client provides source code and libraries for interacting with
server-to-server APIs for various OpenSocial containers. This package includes:
  1. Java client source under "opensocial/java/src".
  2. Android client source under "opensocial/android/src".
  3. Java client dependent libraries under "opensocial/java/lib".
  4. Android client dependent libraries under "opensocial/android/lib".


REQUIREMENTS:
-------------
OpenSocial Java Client and Android Client depend on the following external
libraries on top of a standard Java installation.
  1. Java Development Kit version 5.0 or greater.  Latest version of JDK
     available for download from http://java.sun.com.
  2. commons-codec-1.3.jar -- Apache Commons codec library for Base64 encoding
     and decoding. Latest version available for download from
     http://commons.apache.org/codec/.
  3. javax.servlet_2.4.0.v200806031604.jar -- J2EE archive necessary for
     building OpenSocialRequestValidator.java, which relies on the
     HttpServletRequest class.
  4. json.jar -- JSON parsing library developed by JSON.org. Source code
     available for download at http://www.json.org/java/index.html.
  5. junit-4.5.jar -- Java unit testing framework necessary for building and
     running test classes. Latest version available for download from
     http://www.junit.org/.
  6. oauth-core-20090105.jar -- OAuth signing and verification library. Latest
     version available for download from http://code.google.com/p/oauth/.


INSTALLATION:
-------------
  1. Check out source code from Subversion repository and navigate to newly
     checked-out opensocial-java-client folder.
  2. To recompile source files, execute the default ant target by typing "ant"
     at the command line after changing to the "opensocial-java-client/java"
     directory.
  3. To generate documentation and Java archive file for OpenSocial Client,
     execute the ant "dist" target "dist" from "opensocial-java-client/java".
     Docs are regenerated in opensocial-java-client/doc and archive file is
     built in opensocial-java-client/dist.
  4. To run sample applications for OpenSocial Client, see README-samples.txt
     file in opensocial-java-client/samples.
  5. To run the sample Android application, the Android SDK is required,
     available for download from http://developer.android.com/sdk/. Modify the
     "sdk-folder" and "android-tools" properties in
     opensocial-java-client/android/build.xml to reference your local SDK. Then
     launch the Android emulator and load the sample application by typing
     "ant install" at the command line after changing to the
     opensocial-java-client/android directory. You can uninstall and reinstall
     the application by using the uninstall and reinstall Ant targets
     respectively.