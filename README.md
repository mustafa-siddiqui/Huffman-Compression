# Huffman-Compression

Program works by compressing a file using the Huffman Compression Algorithm by creating the Huffman tree using a minimum priority queue based on the frequencies of characters in the original file. The priority queue is implemented with a heap implemented using arrays. The tree is made by taking the two minimum nodes in the queue, combining them into a 'mini-tree', and inserting the parent node back into the queue. This process is repeated until only one node - the root node of the tree - is remaining in the queue.

A frequency file is produced which stores the frequency of each character along with its binary representation. This is needed for decompression. An alternative is to write an encoding string at the top of the encoded/compressed file and read accordingly when decompressing.

### To compile and run:
```
javac HuffmanSubmit.java
java HuffmanSubmit
```

### Results
The text file `alice30.txt` is compressed by almost 47% while the `ur.jpg` file is compressed with a difference of some ~50 Bytes. 
