/*
 * Created on Apr 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jumble.batch;

import jumble.*;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;

public class HTMLRunner {
    private SortedMap mPackages;
    public HTMLRunner(JumbleResult [] results) {
        mPackages = new TreeMap();
        for(int i = 0; i < results.length; i++) {
            String className = results[i].getClassName();
            String packageName = getPackage(className);
            
            if(mPackages.containsKey(packageName)) {
                Set s = (Set)mPackages.get(packageName);
                s.add(results[i]);
            } else {
                Set s = new HashSet();
                s.add(results[i]);
                mPackages.put(packageName, s);
            }
        }
    }
    
    public static String produceHTML(JumbleResult result) {
        StringBuffer buf = new StringBuffer();
        buf.append("<HTML>\n");
        buf.append("<HEAD>\n");
        buf.append("<TITLE>\n");
        buf.append("Jumble Results for " + result.getClassName()
                + " tested by " + result.getTestName() + "\n");
        buf.append("</TITLE>\n");
        buf.append("</HEAD>\n");
        buf.append("<BODY>\n");
        buf.append("<H1> Jumble Results </H1> <BR>");
        buf.append("<B>Class: </B>" + result.getClassName() + "<BR>\n");
        buf.append("<B>Test: </B>" + result.getTestName() + "<BR><BR>\n");
        buf.append("<B>Run: </B><BR>\n");
        if(result.testFailed()) {
            buf.append("JUnit test failed\n");
        } else {
            for(int i = 0; i < result.getAllMutations().length; i++) {
                Mutation m = result.getAllMutations()[i];
                if(m.isPassed())
                    buf.append(".");
                else if(m.isFailed())
                    buf.append("M");
                else if(m.isTimedOut())
                    buf.append("T");
            }
            buf.append("<BR><BR>\n");
        
            buf.append("<B>Failures: </B><BR>\n");
        
            for(int i = 0; i < result.getFailed().length; i++) {
                buf.append("" + result.getFailed()[i].getDescription()
                        + "<BR>\n");
            }	
        
            buf.append("<BR><B> Coverage: </b> " + (result.getPassed().length
                    + result.getTimeouts().length) 
                    + "/" + result.getAllMutations().length + 
                    " (" + result.getCoverage() + "%)<BR>");
        }
        buf.append("</BODY>\n");
        buf.append("</HTML>");
        return buf.toString();
    }
    
    public static void main(String[] args) throws Exception {
        HashSet ignore = new HashSet();
        ignore.add("main");
        ignore.add("integrity");
        
        ClassTestPair [] pairs = new TextFilePairProducer(args[0]).producePairs();
        JumbleResult [] res = JumbleBatchRunner.runBatch(pairs, true, true, true, ignore);
        
        HTMLRunner html = new HTMLRunner(res);
        html.writeWebSite(new File(args[1]), new File(args[2]));
        
    }
    private String getPackage(String className) {
        for(int i = className.length() - 1; i >= 0; i--) {
            if(className.charAt(i) == '.')
                return className.substring(0, i);
        }
        return "(default)";
    }
    public void writeWebSite(File directory, File pics) throws IOException {
        copyPicDir(pics, directory);
        
        StringBuffer buf = new StringBuffer(); //index web site
        
        if(!directory.isDirectory())
            throw new FileNotFoundException("Directory " + directory + " not found");
        Set packages = mPackages.keySet();
        
        buf.append("<HTML>\n");
        buf.append("<HEAD>\n");
        buf.append("<TITLE>\n");
        buf.append("Jumble Results\n");
        buf.append("</TITLE>\n");
        buf.append("</HEAD>\n");
        buf.append("<BODY>\n");
        buf.append("<H1> Jumble Results </H1> <BR>\n");
        Iterator it = packages.iterator();
        while(it.hasNext()) {
            String packageName = (String)it.next();
            Set s = (Set)mPackages.get(packageName);
            buf.append("<A HREF=\"" + packageName + ".html\">"
                    + packageName + "</A> <BR>\n");
            
            writePackagePage(directory, packageName, s);
        }
        
        buf.append("</BODY>\n");
        buf.append("</HTML>");
        
        PrintWriter writer = 
            new PrintWriter(new FileWriter(directory.getCanonicalPath() 
                    + "/index.html"));
        writer.println(buf);
        writer.close();
        
    }
    
    private static void writePackagePage(File dir, String name, Set s) throws IOException {
        if(!dir.isDirectory())
            throw new FileNotFoundException("Directory " + dir + " not found");

        StringBuffer buf = new StringBuffer();
        
        buf.append("<HTML>\n");
        buf.append("<HEAD>\n");
        buf.append("<TITLE>\n");
        buf.append("Jumble Results\n");
        buf.append("</TITLE>\n");
        buf.append("</HEAD>\n");
        buf.append("<BODY>\n");
        buf.append("<H1> Jumble Results for " + name + " </H1> <BR>\n");
        Iterator it = s.iterator();
        while(it.hasNext()) {
            JumbleResult r = (JumbleResult)it.next();
            String filename = r.getClassName() + "-" + r.getTestName();
            PrintWriter curWriter = 
                new PrintWriter(new FileWriter(dir.getCanonicalPath() 
                        + "/" + filename + ".html"));
            
            curWriter.println(produceHTML(r));
            curWriter.close();
            final String pic;
            if(r.testFailed())
                pic = "bad.gif";
            else if(r.getAllMutations().length == 0)
                pic = "free.gif";
            else {
                int coverage = r.getCoverage();
                pic = "" + (coverage/10) + ".gif";
            }
                
            buf.append("<IMG SRC=" + pic + "></IMG>\n");
            buf.append("" + r.getCoverage() + "% " + 
                    "<A HREF=" + filename + ".html>" + r.getClassName() +
                    "</A> <BR> \n");
        }
        buf.append("</BODY>\n");
        buf.append("</HTML>");
        PrintWriter writer = 
            new PrintWriter(new FileWriter(dir.getCanonicalPath() 
                    + "/" + name + ".html"));
        writer.println(buf);
        writer.close();
    }
    
    private void copyPicDir(File fromDir, File toDir) throws IOException {
       for(int i = 0; i <= 10; i++) {
           copyFile(new File(fromDir.getCanonicalPath()+"/" +i + ".gif")
                   , toDir);
       }
       copyFile(new File(fromDir.getCanonicalPath()+"/bad.gif"), toDir);
       copyFile(new File(fromDir.getCanonicalPath()+"/free.gif"), toDir);
    }
    private void copyFile(File from, File toDir) throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(
                new FileOutputStream(
                toDir.getCanonicalPath()+"/" + from.getName()));
        BufferedInputStream in = new BufferedInputStream(
                new FileInputStream(from));
        
        int cur;
        while((cur = in.read()) != -1) {
            out.write(cur);
        }
        in.close();
        out.close();
            
    }
}
