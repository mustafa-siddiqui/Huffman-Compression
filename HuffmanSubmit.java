import java.util.HashMap;
import java.util.NoSuchElementException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import java.util.Scanner;

// temporary stuff
import java.util.Arrays;

// Import any package as required

public class HuffmanSubmit implements Huffman {
	public static void main(String[] args) {
		Huffman  huffman = new HuffmanSubmit();
		huffman.encode("alice30.txt", "out.enc", "freq.txt");
		//huffman.encode("ur.jpg", "ur.enc", "freq.txt");
		//huffman.decode("ur.enc", "ur_dec.jpg", "freq.txt");
		// After decoding, both ur.jpg and ur_dec.jpg should be the same. 
		// On linux and mac, you can use `diff' command to check if they are the same. 

		// read file and store chars and their frequencies
		HashMap<Character, Integer> map = readFile("alice30.txt");
		/*
		for (Character c : map.keySet()) {
			System.out.println(c + ": " + map.get(c));
		}*/

		// write freq table to file
		createFreqFile(map, "freq.txt");

		// create minimum priority queue
		MinPQ pq = new MinPQ(map.size());
		for (Character c : map.keySet()) {
			Node newNode = new Node(c, map.get(c), null, null);
			pq.insert(newNode);
		}

		// print tree in a separate file
		try {
			PrintStream out = new PrintStream(new File("tree.txt"));
			PrintStream console = System.out;
			System.setOut(out);

			System.out.println("Size: " + pq.getSize());
			
			pq.dump();

			System.out.println();
			pq.printQueue();

			out.close();
			System.setOut(console);

		}
		catch (FileNotFoundException e) {
			;
		}

		HashMap<Character, Integer> cmap = getHashMap("freq.txt");
		//createFreqFile(cmap, "test.txt");
		System.out.println("Printing Hashmap:");
		for (Character c : cmap.keySet()) {
			System.out.println(c + ": " + cmap.get(c));
		}

		// create huffman tree
		//Node root = createTree(pq);
		Node root = buildTree(map);
		System.out.println("Printing Tree:\n");
		printTree(root);

		//String[] codewordTable = buildCode(root);
		/*
		for (int i = 0; i < codewordTable.length; i++) {
			try {
				if (codewordTable[i].length() > 0)
				System.out.println((char)i + ": " + codewordTable[i]);
			}
			catch (NullPointerException n) {
				continue;
			}

		}*/
	}
  
	// Feel free to add more methods and variables as required. 
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
		//HashMap<Character, String> table = createCodewordTable(root);
		String[] table = buildCode(root);

		BinaryOut encoded = new BinaryOut(outputFile);
		BinaryIn original = new BinaryIn(inputFile);

		// write encoding for character in new file
		char c = 0;
		try {
			while ((c = original.readChar()) != -1) {
				String enc = table[c];
				for (int i = 0; i < enc.length(); i++) {
					if (enc.charAt(i) == 0) {
						encoded.write(false);
					}
					else {
						encoded.write(true);
					}
				}
				encoded.flush();
			}
		}
		catch (NoSuchElementException e) {
			// processed through file
		}

		// close file
		encoded.close();
	}

	/*
	public void encode(String inputFile, String outputFile, String freqFile){
		// Your code here
   	}*/

   	public void decode(String inputFile, String outputFile, String freqFile){
		// Your code here
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

	/*	// HAS SOME ERROR //
		Create Huffman Tree from minimum priority queue.
		Returns the root of the tree.
	*//*
	public static Node createTree(MinPQ pq) {
		Node min = pq.getNode();
		Node secondMin = pq.getNode();

		Node top = new Node('@', (min.freq + secondMin.freq), min, secondMin);
		Node lastTop = top;

		while (!pq.isEmpty()) {
			min = pq.getNode();
			top = new Node('@', (lastTop.freq + min.freq), min, lastTop);
			lastTop = top;
		}
		
		return lastTop;
	}
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

	/* Building codeword table according to book */
	public static String[] buildCode(Node root) {
		// ASCII alphabet
		int R = 256;

		String[] st = new String[R];
		buildCode(st, root, "");

		return st;
	}

	public static void buildCode(String[] st, Node x, String s) {
		if (x.isLeaf()) {
			st[x.ch] = s;
			return;
		}

		buildCode(st, x.left, s + '0');
		buildCode(st, x.right, s + '1');
	}

	/*
		Building codeword table
	*/

	public static HashMap<Character, String> createCodewordTable(Node root) {
		HashMap<Character, String> map = new HashMap<Character, String>();
		map = buildTable(map, root, "");

		return map;
	}

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
	//static class MinPQ<T extends Comparable<T>> {
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
			Returns the node at the top/front of the queue.
			Does not remove the node from the queue.
		*/
		public Node peek() {
			return (pq[1]);
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

		public void dump() {
			int height = log2(getSize()) + 1;
	
			for (int i = 1, len = getSize(); i < len; i++) {
				int x = at(i).freq;
				int level = log2(i) + 1;
				int spaces = (height - level + 1) * 2;
	
				System.out.print(stringOfSize(spaces, ' '));
				System.out.print(x + ": " + at(i).ch);
	
				if((int)Math.pow(2, level) - 1 == i) System.out.println();
			}
		}
	
		private String stringOfSize(int size, char ch) {
			char[] a = new char[size];
			Arrays.fill(a, ch);
			return new String(a);
		}
	
		// log with base 2
		private int log2(int x) {
			return (int)(Math.log(x) / Math.log(2)); // = log(x) with base 10 / log(2) with base 10
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
	
		/**
		 * Helper methods
		 */
		
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

	/*
	public static void print(MinPQ pq) {
		for (int i = 0; i < pq.getSize(); i++) {
			System.out.println(pq.at(i + 1).freq);
		}
	}
	*/
}
