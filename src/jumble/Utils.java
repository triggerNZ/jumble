/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    Utils.java
 *    Copyright (C) 1999-2004 University of Waikato
 *    Originally from the Weka Project
 *
 */

package jumble;

import java.util.Vector; //I think this is better to use than Weka's FastVector here

/**
 * Class implementing simple command line parsing methods. A subset of the weka.core.Utils 
 * class (taking all command-line related code), with some new code added.
 *
 * @author Tin Pavlinic
 * The authors of weka.core.Utils are:

 * @author Eibe Frank 
 * @author Yong Wang 
 * @author Len Trigg 
 * @author Julien Prados
 * @version $Revision: 1.0
 */

public final class Utils {

  /**
   * Checks if the given array contains any non-empty options.
   *
   * @param options an array of strings
   * @exception Exception if there are any non-empty options
   */

  public static void checkForRemainingOptions(String [] options) 
    throws Exception {
    
    int illegalOptionsFound = 0;
    StringBuffer text = new StringBuffer();

    if (options == null) {
      return;
    }
    for (int i = 0; i < options.length; i++) {
      if (options[i].length() > 0) {
	illegalOptionsFound++;
	text.append(options[i] + ' ');
      }
    }
    if (illegalOptionsFound > 0) {
      throw new Exception("Illegal options: " + text);
    }
  }
  
  /**
   * Checks if the given array contains the flag "-Char". Stops
   * searching at the first marker "--". If the flag is found,
   * it is replaced with the empty string.
   *
   * @param flag the character indicating the flag.
   * @param options the array of strings containing all the options.
   * @return true if the flag was found
   * @exception Exception if an illegal option was found
   */
  public static boolean getFlag(char flag, String [] options) 
    throws Exception {
       return getFlag("" + flag, options);
  }
  
  /**
   * Checks if the given array contains the flag "-String". Stops
   * searching at the first marker "--". If the flag is found,
   * it is replaced with the empty string.
   *
   * @param flag the String indicating the flag.
   * @param options the array of strings containing all the options.
   * @return true if the flag was found
   * @exception Exception if an illegal option was found
   */
  public static boolean getFlag(String flag, String [] options) 
    throws Exception {

    if (options == null) {
      return false;
    }
    for (int i = 0; i < options.length; i++) {
      if ((options[i].length() > 1) && (options[i].charAt(0) == '-')) {
	try {
	  Double dummy = Double.valueOf(options[i]);
	} catch (NumberFormatException e) {
	  if (options[i].equals("-" + flag)) {
	    options[i] = "";
	    return true;
	  }
	  if (options[i].charAt(1) == '-') {
	    return false;
	  }
	}
      }
    }
    return false;
  }

  /**
   * Gets an option indicated by a flag "-Char" from the given array
   * of strings. Stops searching at the first marker "--". Replaces 
   * flag and option with empty strings.
   *
   * @param flag the character indicating the option.
   * @param options the array of strings containing all the options.
   * @return the indicated option or an empty string
   * @exception Exception if the option indicated by the flag can't be found
   */
    public static /*@non_null@*/ String getOption(char flag, String [] options) 
	throws Exception {
	return getOption("" + flag, options);
    }

  /**
   * Gets an option indicated by a flag "-String" from the given array
   * of strings. Stops searching at the first marker "--". Replaces 
   * flag and option with empty strings.
   *
   * @param flag the String indicating the option.
   * @param options the array of strings containing all the options.
   * @return the indicated option or an empty string
   * @exception Exception if the option indicated by the flag can't be found
   */
    public static /*@non_null@*/ String getOption(String flag, String [] options) 
    throws Exception {
	
    String newString;

    if (options == null)
      return "";
    for (int i = 0; i < options.length; i++) {
      if ((options[i].length() > 0) && (options[i].charAt(0) == '-')) {
	
	// Check if it is a negative number
	try {
	  Double dummy = Double.valueOf(options[i]);
	} catch (NumberFormatException e) {
	  if (options[i].equals("-" + flag)) {
	    if (i + 1 == options.length) {
	      throw new Exception("No value given for -" + flag + " option.");
	    }
	    options[i] = "";
	    newString = new String(options[i + 1]);
	    options[i + 1] = "";
	    return newString;
	  }
	  if (options[i].charAt(1) == '-') {
	    return "";
	  }
	}
      }
    }
    return "";
  }

  /**
   * Returns the secondary set of options (if any) contained in
   * the supplied options array. The secondary set is defined to
   * be any options after the first "--". These options are removed from
   * the original options array.
   *
   * @param options the input array of options
   * @return the array of secondary options
   */
  public static String [] partitionOptions(String [] options) {

    for (int i = 0; i < options.length; i++) {
      if (options[i].equals("--")) {
	options[i++] = "";
	String [] result = new String [options.length - i];
	for (int j = i; j < options.length; j++) {
	  result[j - i] = options[j];
	  options[j] = "";
	}
	return result;
      }
    }
    return new String [0];
  }
    
 
  /**
   * Split up a string containing options into an array of strings,
   * one for each option.
   *
   * @param quotedOptionString the string containing the options
   * @return the array of options
   */
  public static String [] splitOptions(String quotedOptionString) throws Exception{

    Vector optionsVec = new Vector();
    String str = new String(quotedOptionString);
    int i;
    
    while (true){

      //trimLeft 
      i = 0;
      while ((i < str.length()) && (Character.isWhitespace(str.charAt(i)))) i++;
      str = str.substring(i);
      
      //stop when str is empty
      if (str.length() == 0) break;
      
      //if str start with a double quote
      if (str.charAt(0) == '"'){
	
	//find the first not anti-slached double quote
	i = 1;
	while(i < str.length()){
	  if (str.charAt(i) == str.charAt(0)) break;
	  if (str.charAt(i) == '\\'){
	    i += 1;
	    if (i >= str.length()) 
	      throw new Exception("String should not finish with \\");
	    if (str.charAt(i) != '\\' &&  str.charAt(i) != '"') 
	      throw new Exception("Unknow character \\" + str.charAt(i));
	  }
	  i += 1;
	}
	if (i >= str.length()) throw new Exception("Quote parse error.");
	
	//add the founded string to the option vector (without quotes)
	String optStr = str.substring(1,i);
	optStr = unbackQuoteChars(optStr);
	optionsVec.addElement(optStr);
	str = str.substring(i+1);
      } else {
	//find first whiteSpace
	i=0;
	while((i < str.length()) && (!Character.isWhitespace(str.charAt(i)))) i++;
	
	//add the founded string to the option vector
	String optStr = str.substring(0,i);
	optionsVec.addElement(optStr);
	str = str.substring(i);
      }
    }
    
    //convert optionsVec to an array of String
    String [] options = new String[optionsVec.size()];
    for (i = 0; i < optionsVec.size(); i++) {
      options[i] = (String)optionsVec.elementAt(i);
    }
    return options;
  }    

    /** Get the next non-empty argument in the arguments list.
     *  The returned argument is cleared to the empty string in the args
     * array.
     * @param args the arguments array
     * @return the first non-empty string in the array or the empty string if 
     *         all strings are empty
     */

    public static String getNextArgument(String [] args) {
	for(int i = 0; i < args.length; i++) {
	    if(!args[i].equals("")) {
		String retVal = args[i];
		args[i] = "";
		return retVal;
	    }
	}
	return "";
    }

  /**
   * Joins all the options in an option array into a single string,
   * as might be used on the command line.
   *
   * @param optionArray the array of options
   * @return the string containing all options.
   */
  public static String joinOptions(String [] optionArray) {

    String optionString = "";
    for (int i = 0; i < optionArray.length; i++) {
      if (optionArray[i].equals("")) {
	continue;
      }
      if (optionArray[i].indexOf(' ') != -1) {
	optionString += '"' + optionArray[i] + '"';
      } else {
	optionString += optionArray[i];
      }
      optionString += " ";
    }
    return optionString.trim();
  }

    /**
     * The inverse operation of backQuoteChars().
     * Converts back-quoted carriage returns and new lines in a string 
     * to the corresponding character ('\r' and '\n').
     * Also "un"-back-quotes the following characters: ` " \ \t and %
     * @param string the string
     * @return the converted string
     */
    public static String unbackQuoteChars(String string) {
	
	int index;
	StringBuffer newStringBuffer;
    
	// replace each of the following characters with the backquoted version
	String charsFind[]    = {"\\\\", "\\'", "\\t", "\\\"", "\\%"};
	char   charsReplace[] = {'\\',   '\'',  '\t',  '"',    '%'};
	
	for(int i = 0; i < charsFind.length; i++) {
	    if (string.indexOf(charsFind[i]) != -1 ) {
		newStringBuffer = new StringBuffer();
		while ((index = string.indexOf(charsFind[i])) != -1) {
		    if (index > 0) {
			newStringBuffer.append(string.substring(0, index));
		    }
		    newStringBuffer.append(charsReplace[i]);
		    if ((index + charsFind[i].length()) < string.length()) {
			string = string.substring(index + charsFind[i].length());
		    } else {
			string = "";
		    }
		}
		newStringBuffer.append(string);
		string = newStringBuffer.toString();
	    }
	}
	return Utils.convertNewLines(string);
    }    
    
    /**
     * Converts carriage returns and new lines in a string into \r and \n.
     * @param string the string
     * @return the converted string
     */
    public static /*@pure@*/ String convertNewLines(String string) {
	
	int index;

    // Replace with \n
    StringBuffer newStringBuffer = new StringBuffer();
    while ((index = string.indexOf('\n')) != -1) {
      if (index > 0) {
	newStringBuffer.append(string.substring(0, index));
      }
      newStringBuffer.append('\\');
      newStringBuffer.append('n');
      if ((index + 1) < string.length()) {
	string = string.substring(index + 1);
      } else {
	string = "";
      }
    }
    newStringBuffer.append(string);
    string = newStringBuffer.toString();
    
    // Replace with \r
    newStringBuffer = new StringBuffer();
    while ((index = string.indexOf('\r')) != -1) {
      if (index > 0) {
	newStringBuffer.append(string.substring(0, index));
      }
      newStringBuffer.append('\\');
      newStringBuffer.append('r');
      if ((index + 1) < string.length()){
	string = string.substring(index + 1);
      } else {
	string = "";
      }
    }
    newStringBuffer.append(string);
    return newStringBuffer.toString();
  }
    


}
  
