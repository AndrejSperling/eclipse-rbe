/*
 * Copyright (C) 2003-2014  Pascal Essiembre
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.essiembre.eclipse.rbe.model.bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Pattern;

import com.essiembre.eclipse.rbe.RBEPlugin;
import com.essiembre.eclipse.rbe.model.bundle.entries.BundleKeyValueEntry;
import com.essiembre.eclipse.rbe.model.bundle.entries.BundleValueEntry;
import com.essiembre.eclipse.rbe.model.workbench.RBEPreferences;

/**
 * Bundle-related utility methods. 
 * @author Pascal Essiembre
 */
public final class PropertiesParser {

    /** System line separator. */
    public static final String SYSTEM_LINE_SEPARATOR = System.getProperty("line.separator");
    
    /** Characters accepted as key value separators. */
    private static final String KEY_VALUE_SEPARATORS = "=:";
    private static final Pattern PATTERN_IS_REGULAR_LINE = Pattern.compile("^[^#!].*");
    private static final Pattern PATTERN_IS_COMMENTED_LINE = Pattern.compile("^##[^#].*");
    private static final Pattern PATTERN_BACKSLASH_R = Pattern.compile("\\\\r");
    private static final Pattern PATTERN_BACKSLASH_N = Pattern.compile("\\\\n");
    
    /**
     * Constructor.
     */
    private PropertiesParser() {
        super();
    }

    /**
     * Parses a string and converts it to a <code>Bundle</code>.  The string is 
     * expected to match the documented structure of a properties file.
     * The returned bundle will have no <code>Locale</code> and no
     * <code>BundleGroup</code> associated to it.
     * @param properties the string containing the properties to parse
     * @return a new bundle
     */
    public static Bundle parse(String properties) {
    	
        final Bundle bundle = new Bundle();
        final BufferedReader bf = new BufferedReader(new StringReader(properties));
        final StringBuffer lineBuf = new StringBuffer();
        
        String line;
        try {
			while ((line = bf.readLine()) != null) {
				
	            final int equalPosition = findKeyValueSeparator(line);
	            final boolean isRegularLine = PATTERN_IS_REGULAR_LINE.matcher(line).matches();
	            final boolean isCommentedLine = PATTERN_IS_COMMENTED_LINE.matcher(line).matches();
	            
	            // parse regular and commented lines
	            if (equalPosition >= 1 && (isRegularLine || isCommentedLine)) {

	            	lineBuf.setLength(0);
	            	lineBuf.append(line);
	            	
	                String key = lineBuf.substring(0, equalPosition).trim();
	                key = unescapeKey(key);
	                
	                String value = lineBuf.substring(equalPosition + 1).trim();

	                // Unescape leading spaces
	                if (value.startsWith("\\ ")) {
	                    value = value.substring(1);
	                }
	                
	                if (RBEPreferences.getConvertEncodedToUnicode()) {
	                    key = PropertiesParser.convertEncodedToUnicode(key);
	                    value = PropertiesParser.convertEncodedToUnicode(value);
	                } else {
	                    value = PATTERN_BACKSLASH_R.matcher(value).replaceAll("\r");
	                    value = PATTERN_BACKSLASH_N.matcher(value).replaceAll("\n");
	                }
	                
	                bundle.addEntry(new BundleKeyValueEntry(key, value, isCommentedLine));
	                
	            } else {
	                bundle.addEntry(new BundleValueEntry((line == null || line.trim().isEmpty()) ? "" : line));
	            }
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

        return bundle;
    }
    
    
    /**
     * Converts encoded &#92;uxxxx to unicode chars
     * and changes special saved chars to their original forms
     * @param str the string to convert
     * @return converted string
     * @see java.util.Properties
     */
    public static String convertEncodedToUnicode(String str) {
        char aChar;
        int len = str.length();
        StringBuffer outBuffer = new StringBuffer(len);

        for (int x = 0; x < len;) {
            aChar = str.charAt(x++);
            if (aChar == '\\' && x + 1 <= len) {
                aChar = str.charAt(x++);
                if (aChar == 'u' && x + 4 <= len) {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = str.charAt(x++);
                        switch (aChar) {
                        case '0': case '1': case '2': case '3': case '4':
                        case '5': case '6': case '7': case '8': case '9':
                            value = (value << 4) + aChar - '0';
                            break;
                        case 'a': case 'b': case 'c':
                        case 'd': case 'e': case 'f':
                            value = (value << 4) + 10 + aChar - 'a';
                            break;
                        case 'A': case 'B': case 'C':
                        case 'D': case 'E': case 'F':
                            value = (value << 4) + 10 + aChar - 'A';
                            break;
                        default:
                            value = aChar;
                            System.err.println(RBEPlugin.getString(
                                 "error.init.badencoding") + str);
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't') {
                        aChar = '\t';
                    } else if (aChar == 'r') {
                        aChar = '\r';
                    } else if (aChar == 'n') {
                        aChar = '\n';
                    } else if (aChar == 'f') {
                        aChar = '\f';
                    } else if (aChar == 'u') {
                        outBuffer.append("\\");
                    }
                    outBuffer.append(aChar);
                }
            } else {
                outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }
    
    /**
     * Finds the separator symbol that separates keys and values.
     * @param str the string on which to find seperator
     * @return the separator index or -1 if no separator was found
     */
    private static int findKeyValueSeparator(String str) {
        int index = -1;
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char currentChar = str.charAt(i);
            if (currentChar == '\\') {
                i++;
            } else if (KEY_VALUE_SEPARATORS.indexOf(currentChar) != -1) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    private static String unescapeKey(String key) {
        int length = key.length();
        StringBuffer buf = new StringBuffer();
        for (int index = 0; index < length; index++) {
            char currentChar = key.charAt(index);
            if (currentChar != '\\') {
                buf.append(currentChar);
            }
        }
        return buf.toString();
    }
}
