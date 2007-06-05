Summary
=======
Jumble is a Java mutation tool for measuring the effectiveness and
coverage of JUnit test suites.


Details
=======
Jumble is a byte-code level mutation testing tool for Java which
inter-operates with JUnit.  Given a class file and its JUnit test
classes, Jumble performs a series of byte-code mutations on the class
file (using the BCEL library, see http://jakarta.apache.org/bcel), and 
runs the unit tests on each mutation to see if they detect the mutation.
This tells you how thoroughly your unit tests test the class.

Jumble was developed in 2003-2006 by a commercial company in New
Zealand, ReelTwo (www.reeltwo.com), and is now available as open
source under the GPL licence.  Jumble has been designed to operate in
an industrial setting with large projects.  Heuristics have been
included to speed the checking of mutations, for example, noting which
test fails for each mutation and running this first in subsequent
mutation checks.  Significant effort has been put into ensuring that it
can test code which runs in environments such as the Apache
webserver.  This requires careful attention to class path handling and
co-existence with foreign class-loaders.  At ReelTwo, Jumble is used
on a continuous basis within an agile programming environment with
approximately 400,000 lines of Java code under source control.  This
checks out project code every fifteen minutes and runs an incremental
set of unit tests and mutation tests for modified classes.


How to Compile Jumble
=====================

There is an ant (http://ant.apache.org) build script provided in the
directory containing this README.  Assuming you have ant installed,
run:

  ant jar

This will produce the jumble.jar file, which you can use to run Jumble.
You may also want to run 'ant test' to run Jumble's unit and system
tests.

NOTE: the lib/bcel.jar library used by Jumble is actually a patched version
of BCEL release 5.2, which adds some extra functions to their ClassPath
object.  These patches were accepted by the BCEL project and have been
committed to their repository, but as yet there is no release that includes
them.  The patched version of BCEL is in lib/bcel.jar, and is included
into jumble.jar when you build Jumble.


How to Use Jumble
=================
An example of a simple application of Jumble to a class called Foo is:

  java -jar jumble.jar --classpath=. Foo

This assumes that class Foo is in the default package and in the current 
directory ('.'), and that its JUnit tests are in FooTest.java
(note that Jumble works on class files, so you must use javac to 
create Foo.class and FooTest.class before you run Jumble).

Jumble will start by running the unit tests (in FooTest.class) on the
unmodified Foo class to check that they all pass, and to measure the
time taken by each test.  Then it will mutate Foo and run the tests
again to see if they detect the mutation.  It continues this process
until all mutations of Foo have been tried.  The output might look like
this:

  Mutating Foo
  Tests: FooTest
  Mutation points = 12, unit test time limit 2.02s
  ..M FAIL: Foo:31: negated conditional
  M FAIL: Foo:33: negated conditional
  M FAIL: Foo:34: - -> +
  M FAIL: Foo:35: negated conditional
  ......
  Score: 67%

This says that Jumble has tried 12 different mutants of Foo and the
unit tests (in FooTest) correctly detected the changed behaviour in
8/12 cases (indicated by a '.'), but failed to detect the change in
the other 4/12 cases.  Overall, 67% of the mutations were detected by
the unit tests, which means that they probably need to be improved.


The next example shows a more complex usage of Jumble to test a class
called Bar, which has two sets of JUnit tests, called BarTest1 and
BarTest2.  Since these names do not follow the usual Jumble naming
convention for JUnit class (which would be BarTest.java), we must tell
Jumble which JUnit test files to use, by listing them on the command
line after the main class Bar.  We assume that all three of these
classes are in a Java package called 'app', but that application
source files are in the 'src' directory, whereas the JUnit files are
in the 'test' directory.

  java -jar jumble.jar --classpath=src;test;. app.Bar app.BarTest1 app.BarTest2

So the files involved in this usage are organised as follows:

  ./jumble.jar
  ./src/app/Bar.java        (not used by Jumble)
  ./src/app/Bar.class       (the class file that will be mutated)
  ./test/app/BarTest1.java  (not used by Jumble)
  ./test/app/BarTest1.class (the first set of JUnit tests for Bar)
  ./test/app/BarTest2.java  (not used by Jumble)
  ./test/app/BarTest2.class (the second set of JUnit tests for Bar)


Jumble accepts many other command line options, which allow you to
further customize its behaviour.  Use the '--help' option to see
a brief summary of all the options that it accepts.

  java -jar jumble.jar --help
