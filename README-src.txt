OpenSocial Java Client API Source
=======================================

Table of Contents
------------------
  1. Package Contents
  2. Requirements
  3. Installation


PACKAGE CONTENTS:
-----------------
OpenSocial Java client provides source code and libraries for interacting with server-to-server APIs for various OpenSocial containers. This package includes
  1. OpenSocial Java client source under "opensocial/java/src".
  2. Dependent libraries under "opensocial/java/lib".


REQUIREMENTS:
-------------
OpenSocial Java Client depends on following external utilities/libraries on top of standard Java installation.
1. Java Development Kit version 5.0 or greater.  Latest version of JDK
   available for download from http://java.sun.com.
2. core-20081027.jar -- OAuth signing and verification library, included in
   opensocial/java/lib directory. Latest version available for download
   from http://code.google.com/p/oauth/.
3. commons-codec-1.3.jar -- Apache Commons codec library for encoding and
   decoding, included in opensocial/java/lib directory. Latest
   version available for download from
   http://commons.apache.org/codec/.
4. javax.servlet_2.4.0.v200806031604.jar -- J2EE archive necessary for
   building OpenSocialRequestValidator.java, which relies on the
   HttpServletRequest class.


INSTALLATION:
-------------
1. Check out source code from Subversion repository and navigate to newly
   checked-out opensocial-java-client folder.
2. To recompile source files, execute the ant build target "ant" from
   folder "opensocial-java-client/java".
3. To generate documentation and Java archive file, execute the ant build
   target "ant dist" from folder "opensocial-java-client/java". Docs and
   archive file are generated in opensocial-java-client/dist.
