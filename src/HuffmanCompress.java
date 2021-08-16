/**
 * 	@file	HuffmanCompress.java
 * 	@brief	Huffman Coding Algorithm implementation using Minimum
 *		Priority Queue to construct Huffman tree.
 *		An extra frequency file 'freq.txt' is produced which
 *		stores the characters in their binary representation
 *		and their frequencies in the original file.
 * 	@author	Mustafa Siddiqui
 * 	@date	04/02/21
 */

 /* Import required packages */
import java.util.HashMap;
import java.util.NoSuchElementException;	// for using BinaryIn and BinaryOut
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

public class HuffmanCompress implements Huffman {
	public static void main(String[] args) {
		Huffman  huffman = new HuffmanCompress();

		// text files -> alice30.txt same as alice30_dec.txt
		huffman.encode("alice30.txt", "out.enc", "freq.txt");
		huffman.decode("out.enc", "alice30_dec.txt", "freq.txt");

		// jpg files -> ur.jpg same as ur_dec.jpg
		huffman.encode("ur.jpg", "ur.enc", "freq.txt");
		huffman.decode("ur.enc", "ur_dec.jpg", "freq.txt");
	}
	
	/*
	 @fn 	encode()
	 
	 @param output file name (compressed file)
	 @param	input file name	 (to be compressed)
	 @param frequency file	 (needed for decoding)
	 
	 @brief	Encodes the input file using the Huffman Compression Algorithm.
	 	Creates a frequency file containing the frequency of the occurances
	 	of each character with the character represented as a binary string.
	 	Builds a huffman tree by creating a minimum priority queue and then
	 	compresses the file by replacing each character by its huffman
	 	encoding in the compressed file (output file).
	*/
	public void encode(String inputFile, String outputFile, String freqFile){
		// read input file and create freq file
		HashMap<Character, Integer> map = readFile(inputFile);
		createFreqFile(map, freqFile);

		// create minimum priority queue
		MinPQ pq = new MinPQ(map.size());
		for (Character c : map.keySet()) {
			Node newNode = new Node(c, map.get(c), null, null);
			pq.insert(newNode);
		}

		// build huffman tree
		Node root = buildTree(map);

		// create codeword table
		HashMap<Character, String> table = createCodewordTable(root);

		BinaryOut encoded = new BinaryOut(outputFile);
		BinaryIn original = new BinaryIn(inputFile);

		// write encoding for character in new binary file
		char c = 0;
		try {
			while ((c = original.readChar()) != -1) {
				String enc = table.get(c);
				for (int i = 0; i < enc.length(); i++) {
					if (enc.charAt(i) == '0') {
						encoded.write(false);
					}
					else {
						encoded.write(true);
					}
				}
			}
			// add padding to make multiple of a byte
			encoded.flush();
		}
		catch (NoSuchElementException e) {
			// processed through file
		}

		// close file
		encoded.close();
	}

	/*
	 @fn 	decode()
	 @param input file name	 (compressed file)
	 @param	output file name (to be decompressed)
	 @param frequency file	 (needed for decoding)

	 @brief	Decodes the input file which is compressed using the Huffman 
	 	Compression Algorithm.
	 	Reads the frequency file to create the same Huffman tree which is
	 	created during encoding/compression.
	 	Reads the compressed file bit by bit and traverses down the tree
	 	until a leaf node is reached printing the character when it does.

	 => Have 2 options to traverse and print: iterative and recursive.
	 Iterative goes easy on the stack but uses exception handling more
	 often as it checks for EOF after every bit read. Still, it is much 
	 simpler than the recursive implementation - which is more compact. <=
	*/
   	public void decode(String inputFile, String outputFile, String freqFile){
		// read freq file and create hashmap
		HashMap<Character, Integer> map = getHashMap(freqFile);

		// create huffman tree
		Node root = buildTree(map);

		BinaryIn encoded = new BinaryIn(inputFile);
		BinaryOut decoded = new BinaryOut(outputFile);
		
		// read bits and replace encodings with characters until EOF
		Node node = root;
		Boolean EOF = false;
		while (EOF == false) {
			try { 
				/* iterative method to write decompressed file */
				Boolean bit = encoded.readBoolean();
				if (bit == false) {
					node = node.left;
				}
				else {
					node = node.right;
				}
	
				if (node.isLeaf()) {
					decoded.write(node.ch);
					node = root;
				}

				/* if the recursive method is to be used */
				//char c = traverseTree(root, encoded);
				//decoded.write(c);
			}
			catch (NoSuchElementException e) {
				// end of file
				EOF = true;
			}
		}

		// close file
		decoded.close();
   	}
	
	/*
	   Recursive method to traverse down the tree while decompression.
	   Returns the character at the leaf node after traversing the tree
	   according to the bits read from the encoded file.

	   => TESTED: works perfectly, however iterative method preferred (much simpler) <=
	*/
	public static char traverseTree(Node root, BinaryIn in) {
		char c;
		if (!root.isLeaf()) {
			boolean bit = in.readBoolean();
			if (bit == false) {
				c = traverseTree(root.left, in);
			}
			else {
				c = traverseTree(root.right, in);
			}
		}
		else {
			c = root.ch;
		}
		
		return c;
	}

	/*
	   Write every character's binary representation along with its frequency
	   contained in the hashmap/dictionary to a file in the format:
	   <binary_representation>:<frequency>
	*/
	public static void createFreqFile(HashMap<Character, Integer> map, String filename) {
		// write freq table to file
		try {
			PrintStream out = new PrintStream(new File(filename));
			PrintStream console = System.out;
			System.setOut(out);

			for (Character c : map.keySet()) {
				System.out.print(charToBinary(c));
				System.out.println(":" + map.get(c));
			}

			out.close();
			System.setOut(console);
		}	
		catch (FileNotFoundException e) {
			System.out.println("Error opening " + filename + ".");
			System.exit(0);
		}
	}
	
	/*
	   Returns a hashmap with each key representing a character in 
	   the input file and the value representing its frequency.
	*/
	public static HashMap<Character, Integer> readFile(String inputFile) throws NoSuchElementException {
		HashMap<Character, Integer> map = new HashMap<Character, Integer>();
		
		BinaryIn in = new BinaryIn(inputFile);

		// read until EOF
		char c = 0;
		try {
			while ((c = in.readChar()) != -1) {
				if (map.containsKey(c)) {
					map.put(c, map.get(c).intValue() + 1);
				}
				else {
					map.put(c, 1);
				}
			}
		}	
		catch (NoSuchElementException e) {
			//System.out.println("Processed through file.");
		}
		return map;
	}

	/*
	 Returns the binary representation of a character as a string.
	*/
	public static String charToBinary(char c) {
		int n = c;
		int bin = 0;
		int rem, i = 1;

		while (n != 0) {
			rem = n % 2;
			n /= 2;
			bin += rem * i;
			i *= 10;
		}

		return Integer.toString(bin);
	}

	/*
	 Returns the character value of a binary number represented as a string.
	*/
	public static char BinaryToChar(String bin) {
		int l = bin.length();
		int num = 0;
		for (int i = 0; i < l; i++) {
			if (bin.charAt(i) == '1') {
				num += Math.pow(2, (l-1) - i);
			}
		}

		return (char)num;
	}

	/*
	 Returns a hashmap with keys corresponding to characters and the
	 values corresponding to their frequencies as stored in the 
	 frequency file. 
	*/
	public static HashMap<Character, Integer> getHashMap(String freqFile) {
		HashMap<Character, Integer> map = new HashMap<Character, Integer>();
		try {
			FileInputStream in = new FileInputStream(new File(freqFile));
			Scanner input = new Scanner(in);

			while (input.hasNextLine()) {
				String[] line = input.nextLine().split(":");
				char c = BinaryToChar(line[0]);
				int freq = Integer.parseInt(line[1]);
				map.put(c, freq);
			}

			input.close();
		}
		catch (FileNotFoundException f) {
			System.out.println(freqFile + " not found. Exiting..");
			System.exit(0);
		}

		return map;
	}

	/*
	 Create Huffman tree from hashmap. A priority queue is first created
	 from the hashmap and then is used to make the huffman tree. The two
	 minimum nodes are joined to make a 'litte tree' with the root node
	 having frequency = sum of the child nodes' frequencies, and is 
	 inserted back into the priority queue. This process is continued
	 until no nodes remain in the priority queue. 
	*/
	public static Node buildTree(HashMap<Character, Integer> map) {
		MinPQ pq = new MinPQ(map.size());
		for (Character c : map.keySet()) {
			pq.insert(new Node(c, map.get(c), null, null));
		}

		while (pq.getSize() > 1) {
			Node x = pq.getNode();
			Node y = pq.getNode();
			Node parent = new Node('\0', x.freq + y.freq, x, y);
			pq.insert(parent);
		}

		return pq.getNode();
	}

	/*
		In-order traversal and printing of tree.
	*/
	public static void printTree(Node root) {
		if (root.left != null) {
			printTree(root.left);
		}

		if (root.isLeaf())
			System.out.println(root.ch + ": " + root.freq);

		if (root.right != null) {
			printTree(root.right);
		}
	}

	/*
	 Building codeword table from the Huffman tree.
	 This stores the huffman encodings for each character.
	*/
	public static HashMap<Character, String> createCodewordTable(Node root) {
		HashMap<Character, String> map = new HashMap<Character, String>();
		map = buildTable(map, root, "");

		return map;
	}

	/*
	 Recursive method which traverses the tree in-order and
	 gets the encodings for each character. A traversal down
	 the left child adds a '0' while a traversal down the right
	 child adds a '1'.
	*/
	public static HashMap<Character, String> buildTable(HashMap<Character, String> map, Node root, String s) {
		if (root.left != null) {
			buildTable(map, root.left, s + '0');
		}

		if (root.isLeaf()) {
			map.put(root.ch, s);
		}

		if (root.right != null) {
			buildTable(map, root.right, s + '1');
		}

		return map;
	}

	/*
	 Class to store a node in the Huffman tree.
	*/
   	private static class Node implements Comparable<Node> {
		private char ch;
		private int freq;
		private final Node left;
		private final Node right;

		/*
		 Constructor for Node class.
		*/
		Node(char character, int frequency, Node leftNode, Node rightNode) {
			ch = character;
			freq = frequency;
			left = leftNode;
			right = rightNode;
		}

		/*
		 Method which checks if the node is a leaf node or not.
		 Returns True or False.
		*/
		public boolean isLeaf() {
			return (left == null && right == null);
		}

		/*
		 Method which compares the current with another node.
		 Returns a negative value if the current node has less frequency
		 than the other node and a positive value if the current node has
		 more frequency than the other node.
		 If the return value = 0, the nodes have the same frequency.
		*/
		public int compareTo(Node otherNode) {
			return (freq - otherNode.freq);
		}
   	}
	
	/*
         Class for implementing a Minimum Priority Queue. 
         This is used to build the Huffman Tree.
	*/
	private static class MinPQ {
		/*
		   pq = array representing queue.
		   size = number of elements currently in the queue.
	
		   pq[0] is unused.
	
		   Valid Heap: Parent is less than both of its children.
		*/
		
		private Node[] pq;
		private int size;
	
		/*
		   Constructor for the minimum priority queue.
		*/
		MinPQ(int n) {
			//pq = (Node[]) new Comparable[n + 1];
			pq = new Node[n + 1];
			size = 0;
		}
	
		/*
		 Returns if the queue is empty or not.
		*/
		public Boolean isEmpty() {
			return (size == 0);
		}

		/*
		 Returns the number of elements currently in the queue.
		*/
		public int getSize() {
			return size;
		}

		/*
		 Returns the node at the index i.
		 Does not remove the node from the queue.
		*/
		public Node at(int i) {
			return (pq[i]);
		}

		/*
		 Add node to the priority queue.
		*/
		public void insert(Node newNode) {
			if (newNode == null) {
				return;
			}
	
			pq[++size] = newNode;
			bubbleUp(size);
		}

		/*
		 Remove node from queue.
		*/
		public Node getNode() {
			Node min = pq[1];
			swap(1, size--);
			bubbleDown(1);
	
			return min;
		}

		/*
		 Print queue with characters and their frequencies.
		*/
		public void printQueue() {
			for (int i = 1; i < getSize(); i++) {
				System.out.println(at(i).ch + ": " + at(i).freq);
			}

		}

		/*
		 Print Node frequencies in heap order.
		*/
		public void printQueue_freq() {
			for (int i = 0; i < getSize(); i++)
				System.out.println(at(i + 1).freq);
		}

		/*
		 Print Node characters in heap order.
		*/
		public void printQueue_char() {
			for (int i = 0; i < getSize(); i++)
				System.out.println(at(i + 1).ch);
		}

		/***
		  Helper methods
		 ***/
		
		/*
		 Returns true if element at i is less than element at j.
		*/
		private Boolean less(int i, int j) {
			return (pq[i].compareTo(pq[j]) < 0);
		}
		
		/*
		 Swaps elements at indexes i and j.
		*/
		private void swap(int i, int j) {
			Node temp = pq[i];
			pq[i] = pq[j];
			pq[j] = temp;
		}
	
		/*
		 Heapify if a node's key (freq) is smaller than its parent.
		 Replace a node with its parent and repeat until heap is valid.
		*/
		private void bubbleUp(int k) {
			// k/2 is the index of the parent
			while (k > 1 && !less(k/2, k)) {
				swap(k/2, k);
				k = k / 2;
			}
		}
	
		/*
		 Heapify if a node's key (freq) is greater than its children.
		 Replace node with the larger of its children and repeat 
		 until heap is valid.
		*/
		private void bubbleDown(int k) {
			while (2*k <= size) {
				int j = 2 * k;
	
				if (j < size && !less(j, j + 1))
					j++;
	
				// if parent is less than child
				if (!less(k, j) == false)
					break;
	
				swap(k, j);
				k = j;
			}
		}
	}
}
