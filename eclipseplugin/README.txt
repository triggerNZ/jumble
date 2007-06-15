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
It should contain three files:

    plugin-export/jumble/jumble-eclipse.jar    (about 7Kb)
    plugin-export/jumble/jumble.jar            (about 780Kb)
    plugin-export/jumble/plugin.xml            (about 3Kb)

If 'jumble.jar' is missing, then you should copy '../jumble/jumble.jar' 
to 'plugin-export/jumble/jumble.jar'.  (The binary-only release of 
Jumble contains just one copy of jumble.jar, to save space).

Now, to install the Jumble plugin into Eclipse, you must copy the
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

