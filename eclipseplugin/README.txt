This directory contains a simple plugin for Eclipse that
allows you to run Jumble on a single Java class, and
provides a preferences page for turning on/off the optional
kinds of mutations.


Building the Jumble Plugin for Eclipse
======================================

Edit build.xml to set your ECLIPSE_HOME.
You may also need to update the "Plug-in Dependencies.libraryclasspath"
to reflect the .jar files that come with your version of Eclipse.

Then run 'ant'.  

This should create a directory ../../plugin-export that contains
a directory called 'jumble'.  To install the Jumble plugin into
Eclipse, you can just copy this 'jumble' directory into
the 'plugins' directory within your Eclipse installation.
Then restart Eclipse.
(Start it with the -clean option, if you have installed Jumble before).


Using the Jumble Plugin
=======================
Within Eclipse, you can just right-click on a Java file and
choose the "Jumble / Jumble Class" command to run Jumble.


