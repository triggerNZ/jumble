Welcome to Jumble
=================

Do you want to know how good your JUnit tests are?

Jumble will tell you: from 0% (worthless) to 100% (angelic!).

Jumble is a class level mutation testing tool that works in
conjunction with JUnit.  The purpose of mutation testing is to provide
a measure of the effectiveness of test cases.  A single mutation is
performed on the code to be tested, the corresponding test cases are
then executed.  If the modified code fails the tests, then this
increases confidence in the tests.  Conversely, if the modified code
passes the tests this indicates a testing deficiency.


Contents
========
This distribution contains these subdirectories:

* jumble
      Source code for Jumble.  Go here to build jumble.jar, 
      which contains the command line interface for Jumble.

* jumble-annotations
      This defines a Java 1.5+ annotation (@TestClass), which
      can be used to specify the connection between your main
      Java class and its unit tests.  The use of this annotation
      is optional, but is useful for allowing the Eclipse plugin
      to support more flexible naming conventions for unit test files.

* eclipseplugin
      A simple plugin for running Jumble within Eclipse.

See the README.txt file in each subdirectory for more detail.


Source Code
===========
If this distribution does not contain the Java source code,
you can obtain it from the Sourceforge SVN repository like this:

  svn co https://jumble.svn.sourceforge.net/svnroot/jumble/tags/release_1_1_0 jumble1.1.0

Or for the latest development version of Jumble, like this:
  svn co https://jumble.svn.sourceforge.net/svnroot/jumble/trunk trunk


Links
=====
Jumble Web Site:      http://jumble.sourceforge.net
Jumble Mailing Lists: http://sourceforge.net/mail/?group_id=193434
Jumble Forums:        https://sourceforge.net/forum/?group_id=193434
