public class MinPQ<Node extends Comparable<Node>> {
    public static void main(String[] args) {
        MinPQ<Node> pQueue = new MinPQ<Node>(10);
        for (int i = 0; i <= 7; i++) {;
            Node temp = new Node('a', 9 + i, null, null);
            pQueue.insert(temp);
            System.out.println("Printing Queue");
            pQueue.printQueue();
        }

        System.out.println("Final Queue:");
        pQueue.printQueue();

        System.out.println("Removed item");
        System.out.println(pQueue.getNode().freq);
        System.out.println(pQueue.getNode().freq);

        System.out.println("Last:");
        pQueue.printQueue();

        Node newNode = new Node('c', 99, null, null);
        pQueue.insert(newNode);

        Node newNode2 = new Node('c', 1, null, null);
        pQueue.insert(newNode2);

        System.out.println("Last2:");
        pQueue.printQueue();
        pQueue.printQueue_char();
    }

    /*
	    Class to store a node in the Huffman tree.
	*/
    public static class Node implements Comparable<Node> {
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
			Returns a negative number if the current node has less frequency
			than the other node and a positive node if the current node has
			more frequency than the other node.
			If the return value = 0, the nodes have the same frequency.
		*/
		public int compareTo(Node otherNode) {
			return (freq - otherNode.freq);
		}
   	}

    /*
       Start of Min PQ class.
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
        Node t = pq[1];
        return t;
    }

    /* Helper methods */

    private Boolean less(int i, int j) {
        return (pq[i].compareTo(pq[j]) < 0);
    }

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
    
    /*
        Add node to the priority queue.
    */
    private void insert(Node newNode) {
        if (newNode == null) {
            return;
        }

        pq[++size] = newNode;
        bubbleUp(size);
    }

    /*
        Remove node from queue.
    */
    private Node getNode() {
        Node min = pq[1];
        swap(1, size--);
        bubbleDown(1);

        return min;
    }

    private void printQueue() {
        for (int i = 0; i < getSize(); i++) {
            System.out.println(pq[i + 1].freq);
        }
    }

    private void printQueue_char() {
        for (int i = 0; i < getSize(); i++) {
            System.out.println(pq[i + 1].ch);
        }
    }
}

    
