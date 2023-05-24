package com.epacca.recognizer;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
//import com.kg.shared.KgConstants;

/**
 * @author Scott Siedow/Sudipta Tripathy
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class Converter {
	
	private byte[] delimeterByte = new byte[1];
	private String delimeterStr = null;
	public HashMap lexicon = new HashMap();
	public HashMap rycqWords = new HashMap();
	private int MIN_TOKENS = 9;
	private int MAX_TOKENS = 10;
	int lexiconCacheIndex = 0;

	public static void main(String[] args) {
	
		Converter instance = new Converter();

		instance.buildLexicon();
		instance.addXrycqWordsToLexicon();
		instance.createCombinedMoleculesFileAndLinkFile();
		System.out.println("Done.");

}

public void createCombinedMoleculesFileAndLinkFile() {
	
		String oldWord = "";
		boolean isFirstWordFound = true;
		String line = null;
		String tmp = null;
		String x = null;
		String r = null;
		String y = null;
		String c = null;
		String q = null;
		int wtVal = 0;
		String qAttr = null;
		int rfFactor = 0;
		int flag = 0;
		
		int counter = 0;

		long moleculeOffset = 0;
		long idiomOffset = 0;
		int moleculeId = 1;
		
		System.out.println("Creating Molecules file and Linkfile...");
		
		populateLexiconCache();

		// Build ArrayList of ArrayLists for the Idiom references
		// ArrayList idiomArrayList = new ArrayList(lexicon.size());
		// for (int i=0; i< lexicon.size(); i++) {
		//	idiomArrayList.add(i, new ArrayList());	
		// }
		
		HashMap idiomHashMap = new HashMap();
		
		// Open input file
		RandomAccessFile dis = null;
		try {
			dis = new RandomAccessFile(KgConstants.FILE_MOLECULE_TEXT, "r");
		} catch (FileNotFoundException e) {}
		
		// Create the combined molecules output file
		RandomAccessFile moleculesFile = null;
		try {
			moleculesFile = new RandomAccessFile("C:\\kgfiles\\NEWCombinedMolecules.kg", "rw");
		} catch (FileNotFoundException e) {}
		
		// Create the idiom map output file
		RandomAccessFile idiomMapFile = null;
		try {
			idiomMapFile = new RandomAccessFile("C:\\kgfiles\\NEWIdiomMap.kg", "rw");
		} catch (FileNotFoundException e) {}
    
      	// Create linkfile output file
		FileOutputStream fos2 = null;
		try {
			fos2 = new FileOutputStream("C:\\kgfiles\\NEWLinkfile.kg");
		} catch (FileNotFoundException e) {}
      	DataOutputStream linkfile = new DataOutputStream(fos2);
      	
      	// Create IdiomLinkfile output file
		FileOutputStream fos3 = null;
		try {
			fos3 = new FileOutputStream("C:\\kgfiles\\NEWIdiomLinkfile.kg");
		} catch (FileNotFoundException e) {}
      	DataOutputStream idiomLinkfile = new DataOutputStream(fos3);
		
		try {
			moleculeOffset = moleculesFile.getFilePointer();
			
			while ((line = dis.readLine()) != null) {
				x = "_";
				r = "_";
				y = "_";
				c = "_";
				q = "_";
				wtVal = 0;
				qAttr = "-";
				rfFactor = 0;
				flag = 0;
				
				if (counter % 1000 == 0) {
					// System.out.println(line + " [" + counter + "]");	
				}
				
				StringTokenizer st = new StringTokenizer(line, delimeterStr, false);
	
				if (st.countTokens() < MIN_TOKENS || st.countTokens() > MAX_TOKENS) {
					System.out.println("createCombinedMoleculesFileAndLinkFile(): Bad Line: --> " + line);
					continue;
				}
				
				if (st.hasMoreTokens()) {
					x = st.nextToken();
					x = x.toLowerCase();
					x = x.substring(1, x.length() -1).trim();
				}
				
				if (st.hasMoreTokens()) {
					r = st.nextToken();
					r = r.toLowerCase();
					r = r.substring(1, r.length() -1).trim();
				}
				
				if (st.hasMoreTokens()) {
					y = st.nextToken();
					int pos = y.indexOf("<");
					if (pos < 0) {
						y = y.toLowerCase();
					}
					y = y.substring(1, y.length() -1).trim();
				}
				
				if (st.hasMoreTokens()) {
					c = st.nextToken();
					c = c.toLowerCase();
					c = c.substring(1, c.length() -1).trim();
				}
				
				if (st.hasMoreTokens()) {
					q = st.nextToken();
					q = q.toLowerCase();
					if (q.length() -1 > 0) {
						q = q.substring(1, q.length() -1).trim();
					}
					else {
						q = "_";
					}
				}
				
				if (st.hasMoreTokens()) {
					tmp = st.nextToken();
					try {
						wtVal = new Integer(tmp).intValue();
					}
					catch (Exception e) {}
				}
				if (st.hasMoreTokens()) {
					tmp = st.nextToken();
					tmp = tmp.toLowerCase();
					qAttr = tmp.substring(1, tmp.length() -1).trim();
				}
				
				if (st.hasMoreTokens()) {
					tmp = st.nextToken();
					try {
						rfFactor = new Integer(tmp).intValue();
					}
					catch (Exception e) {}
				}
				
				if (st.hasMoreTokens()) {
					tmp = st.nextToken();
					// System.out.println("[" + tmp +"]");
					try {
						flag = new Integer(tmp).intValue();
					}
					catch (Exception e) {}
				}
				
				Integer tmpX = (Integer) lexicon.get(x);				
				Integer tmpR = (Integer) lexicon.get(r);				
				Integer tmpY = (Integer) lexicon.get(y);				
				Integer tmpC = (Integer) lexicon.get(c);				
				Integer tmpQ = (Integer) lexicon.get(q);
				Integer tmpQAttr = (Integer) lexicon.get(qAttr);				
				
				if (tmpX == null) { tmpX = new Integer(0); }
				if (tmpR == null) { tmpR = new Integer(0); }
				if (tmpY == null) { tmpY = new Integer(0); }
				if (tmpC == null) { tmpC = new Integer(0); }
				if (tmpQ == null) { tmpQ = new Integer(0); }
				if (tmpQAttr == null) { tmpQAttr = new Integer(0); }
								
				int xVal = tmpX.intValue();
				int rVal = tmpR.intValue();
				int yVal = tmpY.intValue();
				int cVal = tmpC.intValue();
				int qVal = tmpQ.intValue();
				int qAttrVal = tmpQAttr.intValue();
				
				if (!oldWord.equals(x)) {

					moleculeOffset = moleculesFile.getFilePointer();
					linkfile.writeLong(moleculeOffset);

					// System.out.println(counter + " " + x + " " + moleculeOffset);

					// If idiom, add idiom reference to all words in idiom
					int pos = x.indexOf(" ");
					if (pos > 0) {
						StringTokenizer st2 = new StringTokenizer(x, " ", false);
						while (st2.hasMoreTokens()) {
							String word = st2.nextToken().trim();
							Integer index = (Integer) lexicon.get(word);
							
							if (index != null) {
								if (index.intValue() >= 0) {
									ArrayList ar = (ArrayList) idiomHashMap.get(index);
									if (ar == null) {
										ar = new ArrayList();
										idiomHashMap.put(index, ar);
									}
									ar.add(new Integer(xVal));
								}
							}
						}	
					}
				
				
					counter++;
					oldWord = x;
				}
				
				moleculesFile.writeInt(moleculeId++);
				moleculesFile.writeInt(xVal);
				moleculesFile.writeInt(rVal);
				moleculesFile.writeInt(yVal);
				moleculesFile.writeInt(cVal);
				moleculesFile.writeInt(qVal);
				moleculesFile.writeInt(wtVal);
				moleculesFile.writeInt(qAttrVal);
				moleculesFile.writeInt(rfFactor);
				moleculesFile.writeInt(flag);
			}
			
			// Write idiomHashMap list to idiomLinkFile and IdiomMapFile
			for (int i=0; i< lexicon.size(); i++) {
				
				idiomOffset = idiomMapFile.getFilePointer();
				idiomLinkfile.writeLong(idiomOffset);
				
				ArrayList ar = (ArrayList) idiomHashMap.get(new Integer(i));
				if (ar == null) {
					continue;
				}
				Iterator it2 = ar.iterator();
				
				while (it2.hasNext()) {
					Integer val = (Integer) it2.next();
					
					idiomMapFile.writeInt(val.intValue());	
				}
			}
			
			dis.close();
			moleculesFile.close();
			linkfile.close();
			idiomMapFile.close();
			idiomLinkfile.close();
			
		} catch (IOException e) {
			System.out.println("IOException 2: " + e);
		}
}

public void addXrycqWordsToLexicon() {
		FileOutputStream fos = null;
		String cr = "\n";
		String line = null;
		int counter = 0;
		RandomAccessFile dis = null;
		String tmp = null;
		String x = null;
		String r = null;
		String y = null;
		String c = null;
		String q = null;
		String w = null;
		String qAttr = null;
		
		// if (lexicon.size() == 0) {
			populateLexiconCache();
		// }
		
		try {
			dis = new RandomAccessFile(KgConstants.FILE_MOLECULE_TEXT, "r");
		} catch (FileNotFoundException e) {}
		
		try {
			fos = new FileOutputStream("C:\\kgfiles\\NEWLexicon.kg", true);
		} catch (FileNotFoundException e) {}
      	DataOutputStream dos = new DataOutputStream(fos);
      	
		try {
			while ((line = dis.readLine()) != null) {
				x = "_";
				r = "_";
				y = "_";
				c = "_";
				q = "_";
				w = "_";
				qAttr = "_";
		
				StringTokenizer st = new StringTokenizer(line, delimeterStr, false);
				
				if (st.countTokens() < MIN_TOKENS || st.countTokens() > MAX_TOKENS) {
					System.out.println("addXrycqWordsToLexicon(): Bad Line: " + line);
					continue;
				}
						
				if (counter % 10000 == 0) {
					// System.out.println(line + " [" + counter + "]");	
				}
				counter++;
				
				if (st.hasMoreTokens()) {
					x = st.nextToken();
					x = x.toLowerCase();
					x = x.substring(1, x.length() -1).trim();
				}
				
				if (st.hasMoreTokens()) {
					r = st.nextToken();
					r = r.toLowerCase();
					r = r.substring(1, r.length() -1).trim();
				}
				
				if (st.hasMoreTokens()) {
					y = st.nextToken();
					int pos = y.indexOf("<");
					if (pos < 0) {
						y = y.toLowerCase();	
					}
					y = y.substring(1, y.length() -1).trim();
				}
				
				if (st.hasMoreTokens()) {
					c = st.nextToken();
					c = c.toLowerCase();
					c = c.substring(1, c.length() -1).trim();
				}
				
				if (st.hasMoreTokens()) {
					// System.out.println("x = " + x);
					q = st.nextToken();
					// System.out.println("q = " + q);
					q = q.toLowerCase();
					if (q.length() - 1 > 0) {
						q = q.substring(1, q.length() -1).trim();
					}
					else {
						q = "_";
					}
				}
					
				if (st.hasMoreTokens()) {
					w = st.nextToken();
				}
				if (st.hasMoreTokens()) {
					// System.out.println("x = " + x);
					qAttr = st.nextToken();
					// System.out.println("qAttr = " + qAttr);
					qAttr = qAttr.toLowerCase();
					qAttr = qAttr.substring(1, qAttr.length() -1).trim();
				}	
							
				Integer wordId = (Integer) lexicon.get(r);
				if (wordId == null) {
					lexicon.put(r, new Integer(lexiconCacheIndex++));
					dos.write(r.getBytes());
					dos.write(cr.getBytes());
				}
				wordId = (Integer) lexicon.get(y);
				if (wordId == null) {
					lexicon.put(y, new Integer(lexiconCacheIndex++));
					dos.write(y.getBytes());
					dos.write(cr.getBytes());
				}
				wordId = (Integer) lexicon.get(c);
				if (wordId == null) {
					lexicon.put(c, new Integer(lexiconCacheIndex++));
					dos.write(c.getBytes());
					dos.write(cr.getBytes());
				}
				wordId = (Integer) lexicon.get(q);
				if (wordId == null) {
					lexicon.put(q, new Integer(lexiconCacheIndex++));
					dos.write(q.getBytes());
					dos.write(cr.getBytes());
				}
				wordId = (Integer) lexicon.get(qAttr);
				if (wordId == null) {
					lexicon.put(qAttr, new Integer(lexiconCacheIndex++));
					dos.write(qAttr.getBytes());
					dos.write(cr.getBytes());
				}
			}
		} catch (IOException e) { 
			System.out.println("IOException: " + e);
		}
      			
		rycqWords = null;
		
		try {
			dis.close();
			dos.close();
		} catch (IOException e) {}
	
}


public void populateLexiconCache() {
	
		RandomAccessFile dis = null;
		String line = null;
		lexiconCacheIndex = 0;
		lexicon = new HashMap();
		
		try {
			dis = new RandomAccessFile("C:\\kgfiles\\NEWLexicon.kg", "r");
		} catch (FileNotFoundException e) {}
		
		try {
			while ((line = dis.readLine()) != null) {
				line = line.trim();
				
				// System.out.println(line + " = " + lexiconCacheIndex);
				if (line.length() == 0) { 
					System.out.println("LINE IS ZERO LENGTH");
				}
					//if (counter < 100) {
					//	System.out.println(counter + " " + line);
					//}
					
				Integer val = (Integer) lexicon.get(line);
				if (val != null) {
					System.out.println("THE PROBLEM WORD IS: " + line);
				}
				
				lexicon.put(line, new Integer(lexiconCacheIndex++));			
			}
			System.out.println("SIZE = " + lexicon.size());
		} catch (IOException e) {
			System.out.println("IOException 4: " + e);
		}
		
		try {
			dis.close();
		} catch (IOException e) {}
	
}

public void buildLexicon() {
	    FileInputStream fstream = null;
		BufferedReader in = null;
		String cr = "\n";
		String oldWord = "";
		int counter = 0;
		delimeterByte[0] = (byte) 222;
		delimeterStr = new String(delimeterByte);
				
		System.out.println("Creating Lexicon...");
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("C:\\kgfiles\\NEWLexicon.kg");
		} catch (FileNotFoundException e) {}
      	DataOutputStream dos = new DataOutputStream(fos);
      
        int index = 1;
        String line = null;

        try {
			fstream = new FileInputStream(KgConstants.FILE_MOLECULE_TEXT);
			in = new BufferedReader(new InputStreamReader(fstream));
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + e);
		}
		
		try {
			while ((line = in.readLine()) != null) {
				
				StringTokenizer st = new StringTokenizer(line, delimeterStr, false);
	
				if (st.countTokens() < MIN_TOKENS || st.countTokens() > MAX_TOKENS) {
					System.out.println("buildLexicon(): BAD Line: " + line);
					continue;
				}
				
										
				int pos = line.indexOf(delimeterStr);
				
				/*
				if (pos <= 1) {
					System.out.println("buildLexicon(): POS <= 1: " + line);
					continue;
				}
				*/
				
				// System.out.println(pos + " " + line);
				String word = line.substring(1, pos - 1);
				word = word.toLowerCase().trim();
				
				if (!word.equals(oldWord)) {
					fos.write(word.getBytes());
					fos.write(cr.getBytes());
					oldWord = word;
					// System.out.println(word + " = " + counter++);
				}
			}
		} catch (IOException e) {
			System.out.println("IOException 5: " + e);
		}
		
		try {
			in.close();
			fos.close();
		} catch (IOException e) {}
}

}


