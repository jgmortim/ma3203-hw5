/**
 * HW5
 * 
 * John Mortimore
 * Homework 5
 * MA3203 (Spring 2018)
 * Professor Tim Wagner
 * 
 * Created: Mar. 02, 2018
 * Modified: Mar. 06, 2018
 * 
 * Performs a simplified version of the DES algorithm. The word process as
 * used in this program, refers to encryption and/or decryption.
 * 
 * !!!          This file has been formated for readability               !!!
 * !!!  Do not use any auto formating on it or readability will decrease  !!!
 * @author John Mortimore
 */
class HW5 {
	private String key;
	private final String[] S1 = { "101", "010", "001", "110", "011", "100", "111", "000", "001", "100", "110", "010", "000", "111", "101", "011" };
	private final String[] S2 = { "100", "000", "110", "101", "111", "001", "011", "010", "101", "011", "000", "111", "110", "010", "001", "100" };

	/**
	 * process
	 * @author John Mortimore
	 */
	private enum process {
		e(1), d(-1);
		private int value;

		private process(int value) {
			this.value = value;
		}
	}

	/**
	 * xor
	 * xors two binary Strings and returns the result.
	 * Returns null if Strings are different lengths.
	 * @param strA {String} - the first String
	 * @param strB	{String} - the second String
	 * @return {String} - the result of strA xor strB
	 * @author John Mortimore
	 */
	private String xor(String strA, String strB) {
		if (strA.length() != strB.length())			// If Strings are different lengths,
			return null;							// return null.
		String c = "";								// The resultant String.
		for (int i = 0; i < strA.length(); i++) {	// Check every letter pair between a and b.
			if (strA.charAt(i) != strB.charAt(i)) 	// If the letter in a and b are not the same,
				c = c.concat("1");					// add a one to the resultant.
			else 									// Otherwise,
				c = c.concat("0");					// add a zero.
		}											// After ever letter pair has been checked,
		return c;									// return the resultant.
	}

	/**
	 * funcE
	 * the expander function.
	 * @param str {String} - the String to expand
	 * @return {String} - the expanded String
	 * @author John Mortimore
	 */
	private String funcE(String str) {
		String bit12 = str.substring(0, 2);	// Bits 1 and 2.
		String bit3 = str.substring(2, 3);	// Bit 3.
		String bit4 = str.substring(3, 4);	// Bit 4.
		String bit56 = str.substring(4, 6);	// Bits 5 and 6.
		String eStr = bit12.concat(bit4).concat(bit3).concat(bit4).concat(bit3).concat(bit56);
		return eStr;						// Return the expanded String.
	}

	/**
	 * funcF
	 * the function F( R(i-1), k(i) ).
	 * @param strR {String} - R(i-1)
	 * @param key {int} - k(i)
	 * @return {String} - the resultant of F
	 * @author John Mortimore
	 */
	private String funcF(String strR, String key) {
		String Boxes = xor(key, funcE(strR));		// Xor E(R) with k(i).
		/* The first char is either 48 or 49. by mod 48 all that is left is either 0 or 1.
		 * If it is 0, just start at index 0, otherwise start at index 8 (The start of row 2
		 * in the s box). The rest is the number of the column so add that as well.*/
		int s1Index = 8 * (Boxes.charAt(0) % 48) + Integer.parseInt(Boxes.substring(1, 4), 2);
		int s2Index = 8 * (Boxes.charAt(4) % 48) + Integer.parseInt(Boxes.substring(5, 8), 2);
		return S1[s1Index].concat(S2[s2Index]);		// Return the results from the two S boxes.
	}

	/**
	 * process
	 * performs the encryption or decryption of String LR.
	 * @param strLR {String} - the String to process
	 * @param rO {int} - the first(onset) round of encryption/decryption
	 * @param rC {int} - the last(completion) round of encryption/decryption
	 * @param pro {process} - the process (either encryption or decryption)
	 * @return {String} - the result of processing LR
	 * @author John Mortimore
	 */
	private String process(String strLR, int rO, int rC, process pro) {
		if (pro == process.e && rO > rC) 								// If all rounds of encryption are complete,
			return strLR;												// return the cipher text.
		else if (pro == process.d && rO < rC) 							// If all rounds of decryption are complete,
			return strLR;												// return the plain text.
		String preL = strLR.substring(0, 6);							// L(i-1).
		String preR = strLR.substring(6);								// R(i-1). 
		String newL = preR;												// L(i).
		String key = this.key.substring((rO - 1) % 9, (rO - 1) % 9 + 8);// k(i).
		String newR = xor(funcF(preR, key), preL);						// R(i) = L(i-1) xor F(R(i-1), k(i)).
		return process(newL.concat(newR), rO + pro.value, rC, pro);		// Recursively process till all rounds complete, then return.
	}

	/**
	 * preProcess
	 * Prepares the text(plain or cipher) to be processed, then sends it to be processed.
	 * Returns the result or null if some parameters are invalid.
	 * @param strLR {String} - the plain text or the cipher text to process
	 * @param rO {int} - the first(onset) round of encryption/decryption
	 * @param rC {int} - the last(completion) round of encryption/decryption
	 * @param pro {process} - the process (either encryption or decryption)
	 * @return {String} - the result of processing LR
	 * @author John Mortimore
	 */
	private String preProcess(String strLR, int rO, int rC, process pro) {
		if (strLR.length() != 12)											// If LR is not the correct length,
			return null;													// return null.
		if (pro.value == process.d.value) {									// If decrypting:
			strLR = strLR.substring(6, 12).concat(strLR.substring(0, 6));	// Swap L and R.
			strLR = process(strLR, rO, rC, pro);							// Process.
			strLR = strLR.substring(6, 12).concat(strLR.substring(0, 6));	// Swap L and R again.
			return strLR;													// Return the result.
		} else if (pro.value == process.e.value) {							// If encrypting:
			strLR = process(strLR, rO, rC, pro);							// Process.
			return strLR;													// Return the result.
		} else 																// If process is neither encrypt or decrypt,
			return null;													// return null.																	
	}

	/**
	 * main
	 * reads in the command line arguments can process the information
	 * @param args {String[]} - the command line arguments
	 * @author John Mortimore
	 */
	public static void main(String[] args) {
		if (args.length != 4) {				// If the wrong number of args is entered, print the syntax.
			System.out.println("jgmortim [d/e] [plaintext/ciphertext] [key] [rounds]");
			return;							// And return.
		}
		HW5 self = new HW5();				// Create a new instance of self.
		self.key = args[2].concat(args[2]);	// Set the key. Double the key to prevent String index out of bounds exception.
		String result = "";					// Initialize the result String.
		if (args[0].compareTo("e") == 0) {	// Encryption.
			result = self.preProcess(args[1], 1, Integer.parseInt(args[3]), process.e);
		}
		if (args[0].compareTo("d") == 0) {	// Decryption.
			result = self.preProcess(args[1], Integer.parseInt(args[3]), 1, process.d);
		}
		System.out.println(result);			// Print the result of the process.
	}
}
