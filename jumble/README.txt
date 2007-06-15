Summary
=======
Jumble is a Java mutation tool for measuring the effectiveness and
coverage of JUnit test suites.  Jumble requires at least Java 1.5.

See http://jumble.sourceforge.net for an introduction to Jumble.
See http://sourceforge.net/projects/jumble for source code and downloads.


Details
=======
Jumble is a byte-code level mutation testing tool for Java that
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
If there is a file, jumble.jar, in the same directory as this README
file, then Jumble is already compiled and you can start using it.
Otherwise, you should first build jumble.jar as follows.

There is an ant (http://ant.apache.org) build script, called build.xml,
provided in the directory containing this README.  The Jumble sources
are in the 'src' subdirectory, the self-tests are in the 'test' subdirectory
and the 'lib' subdirectory contains several .jar files used by Jumble.

Assuming you have ant installed, run:

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
An example of a simple application of Jumble to the class example/Mover is:

  java -jar jumble.jar --classpath=. example/Mover

This assumes that class Mover is in the 'example' package, which is relative
to the current directory ('.') and that its JUnit tests are in example/MoverTest.

NOTE: Jumble works on .class files, so you must use javac to 
create example/Mover.class and example/MoverTest.class before you run
Jumble:

  javac -cp jumble.jar example/*.java


Jumble starts by running the unit tests (in MoverTest.class) on the
unmodified Mover class to check that they all pass, and to measure the
time taken by each test.  Then it will mutate Mover and run the tests
again to see if they detect the mutation.  It continues this process
until all mutations of Mover have been tried.  The output might look like
this:

  Mutating example.Mover
  Tests: example.MoverTest
  Mutation points = 10, unit test time limit 2.03s
  ......M FAIL: example.Mover:27: - -> +
  ..M FAIL: example.Mover:30: + -> -

  Score: 80%

This says that Jumble has tried 10 different mutants of Mover and the
unit tests (in MoverTest) correctly detected the changed behaviour in
8/10 cases (indicated by a '.'), but failed to detect the change in
the other 2/10 cases.  Overall, 80% of the mutations were detected by
the unit tests, which is not too bad, but could be improved.


Let's analyze these results a little.
The first failed test is on line 27 of Mover.java, which is
      x -= speed / SLOWER;
The Jumble message is complaining that mutating the '-' operator
to '+' was not detected by the unit tests.  This shows that our test
value of speed=2 is not a good choice, because SLOWER==5 and 2
divided by 5 is zero, so it makes no difference whether we add or
subtract the speed.  This tells us that we should choose a better
test value for speed in the "left" and "right" cases, such as 5.
If we improved our testLeft() and testRight() test cases by using
5 rather than 2, then all mutations would be detected and the 
Jumble score would rise to 100%.

This example also shows that Jumble does not (yet) ensure statement
coverage.  That is, even 100% Jumble coverage does not mean that all
statements of Mover.java have been executed by the tests.  
For example, Jumble has not complained that the prettyString() method
is never tested, or that the 'throw new RuntimeException()' branch
of move() has not been tested.  If we rerun Jumble with the "-r" option
(mutate Return values), then it will mutate the return statements of
all non-void methods, and will detect that the prettyString() result
is not used.

  $ java -jar jumble.jar -r --classpath="." example/Mover
  Mutating example.Mover
  Tests: example.MoverTest
  Mutation points = 12, unit test time limit 2.01s
  ......M FAIL: example.Mover:27: - -> +
  ..M FAIL: example.Mover:30: + -> -
  .M FAIL: example.Mover:44: changed return value (areturn)

  Score: 75%

This tells us that we need to test the prettyString() method.
However, the lack of testing of the throw case of move() is still
not detected.  This illustrates why it can be useful to use code 
coverage tools (such as statement coverage) in addition to Jumble.



The next example shows a more complex usage of Jumble to test a
hypothetical class called Bar, which has two sets of JUnit tests, called
BarTest1 and BarTest2.  Since these names do not follow the usual Jumble
naming convention for JUnit class (which would be BarTest.java), we must
tell Jumble which JUnit test files to use, by listing them on the command
line after the main class Bar.  We assume that all three of these classes
are in a Java package called 'app', but that application source files are
in the 'src' directory, whereas the JUnit files are in the 'test'
directory.

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
