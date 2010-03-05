Author: Jay Huang

This directory contains a plugin for Ant that
allows you to run Jumble on all classes within a directory
or classes in all sub-directories of the given directory(recursive scan), and
be able to ouput jumble results to files. 
NOTE: DO NOT modify the "example" folder.

Install the Jumble Plugin for Ant  (for source-only releases)
=====================================================================================================================
NOTE: Before build this plugin, build Jumble first. 

Then run "ant".

This should create "jumble-antplugin.jar".

NOTE: If you get compilation errors, you may need to check whether "jumble.jar"
      exists in the jumble directory.

To perform unit tests, run "ant test".
 
To system test, run "ant systest", this will run jumble in "example" and output results in command line.

Use Jumble Ant Plugin
=======================================================================================================================
NOTE: MUST put a copy of Junit of verson 3.x or above in your ${ANT_HOME}/lib directory.
      Copy "ant.jar" to the "antplugin/lib" directory  

To use the ant plugin, run "ant run". This will run jumble in "example" and create a "jumble_results" folder in the
current directory containing result outputs for every mutated class. This can all be done in ant too(describe later).

In "build.xml", inside the "run" target, you can set jumble flags, the directory you want to run jumble in,   
whether to output results to file, whether to do recursive scan in the directory, and set where to output results.  

To specify a directory to run jumble in (default to be "example"), use "-Drun.dir",
eg. run "ant -Drun.dir=foo run" , where "foo" specifies the directory. 

To specify where jumble results are stored (default to be the current directory), use '-Dresult.dir", 
eg. run "ant -Dresult.dir=foo run" , this will create a "jumble_results" folder in the "foo" directory.

To run jumble in any directory and store results in any location, use composition of the above,
eg. run "ant -Drun.dir=foo -Dresult.dir=result run" , this will run jumble in "foo" and output results in "result"

The following lists all usages of the plugin:

-Dk                     set mutate inline constants, eg."ant -Dk=true run"                               
-Dr                     set mutate return values
-DX	                 set mutate stores
-Di                     set mutate increments
-Dw                     set mutate constant pool
-Dj                     set mutate switches
-Do                     set order tests by runtime
-Dv                     set verbose mode

-Dout                   set whether to output jumble results to files
-Dre                    set whether to do recursive scanning
-Dresult.dir            specify location to store results
-Drun.dir               specify directory to run jumble in


To run this plugin in command line,run
"java -jar jumble-antplugin.jar foo", where "foo" specifies the directory to run jumble in.

