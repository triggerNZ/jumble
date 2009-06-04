This directory contains a simple plugin for Eclipse that
allows you to run Jumble on a single Java class, and
provides a preferences page for turning on/off the optional
kinds of mutations.


Building the Jumble Plugin for Eclipse  (for source-only releases)
======================================
NOTE: If this directory already contains a 'plugin-export' directory,
then the Jumble plugin is already built, so you can probably go straight
to the "Installing Jumble into Eclipse" instructions below.

Edit build.xml to set your ECLIPSE_HOME.

Then run 'ant'.  

This should create a directory called 'plugin-export' that contains
a directory called 'jumble'.  

NOTE: If you get compilation errors, you may need to update the
"Plug-in Dependencies.libraryclasspath" to reflect the versions of
the .jar files that come with your version of Eclipse.


Installing Jumble into Eclipse
==============================
NOTE: check the contents of your plugin-export/jumble directory.
It should contain four files:

    plugin-export/jumble/jumble-eclipse.jar      (about 7Kb)
    plugin-export/jumble/jumble.jar              (about 780Kb)
    plugin-export/jumble/jumble-annotations.jar  (about 1Kb)
    plugin-export/jumble/plugin.xml              (about 3Kb)

To install the Jumble plugin into Eclipse, you must copy the
whole plugin-export/jumble directory into the 'plugins' directory
of your Eclipse installation.

Then restart Eclipse.
(Start it with the -clean option, if you have installed Jumble before).


Using the Jumble Plugin
=======================
Within Eclipse, you can just right-click on a Java file and
choose the "Jumble / Jumble Class" command to run Jumble.

You can use the "Window / Preferences / Jumble" preferences
page to turn on and off various Jumble flags.

If you use non-standard naming conventions for your JUnit test classes
(eg. not <xxx>Test.java), then you can use the 
com.reeltwo.jumble.annotations.TestClass annotation in the main Java
class to tell Jumble which unit tests to run.
Eg. @TestClass({"my.package.MyUnitTestsWithStrangeName"})


TODO
====
Add 'Mutate assignments' to the Eclipse Preferences page for Jumble.
