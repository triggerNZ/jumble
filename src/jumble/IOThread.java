package jumble;

import java.util.List;
import java.util.LinkedList;

import java.io.*;

public class IOThread extends Thread {
       private Process mProcess;
       private BufferedReader mOut;
       private BufferedReader mErr;
       private List mBuffer;
       
       public IOThread(InputStream out) {
           mOut = new BufferedReader(new InputStreamReader(out));
           mBuffer = new LinkedList();
       }
       
       public void run() {
           String curLine;
           try {
               while((curLine = mOut.readLine())!= null) {
                   synchronized(this) {
                       mBuffer.add(0, curLine);
                   }
               }
               //Finished here
               //System.out.println("Finished with null");
           } catch(IOException e) {
               e.printStackTrace();
           }
       }
       
       public String getNext() {
           synchronized(this) {
               if(mBuffer.size() == 0)
                   return null;
               String ret = (String)mBuffer.get(mBuffer.size() - 1);
               mBuffer.remove(mBuffer.size() - 1);
               return ret;
           }
       }
   }