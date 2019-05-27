package Csci232Program2;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class huffman {
//    Node root;
    huffman thing = new huffman();
    int[] freq = new int[256];
    int count;
    int numberOfSymbols;
    Heap heap;
    String[] symbolTable = new String[256];
    test Mytest = new test();
    public static void main(String[] args){
        try{
            Scanner sc = new Scanner(new File("input.txt"));
            String string;
            while(sc.hasNextLine()){
//                linecount++;
                string = sc.nextLine();
                int choice = 0;
                while(choice!=-1){
                    System.out.println("What would you like to do?");
                    System.out.println("Press 1 to encode the string");
                    System.out.println("Press 2 to print the encoded message");
                    System.out.println("Press 3 to decode the message");
                    System.out.println("Press 4 to print the decoded message");
                    System.out.println("Press -1 to exit");
                    Scanner reader = new Scanner(System.in);
                    choice = reader.nextInt();
                    String encoded = null;
                    String decoded = null;
                    switch (choice){
                        case 1:
                            test.code(string);
                            System.out.println("message encoded");
                            break;
                        case 2:
                            try{
                                Scanner bin = new Scanner(new File("encoded.txt"));
                                while (bin.hasNextLine()){
                                    encoded = bin.nextLine();
                                    System.out.println(encoded);
                                }
                            }catch (FileNotFoundException e){
                                System.out.println(e);
                            }
                            break;
                        case 3:
                            decoded = test.deCode(encoded);
                            break;
                        case 4:
                            System.out.println(decoded);
                            break;
                        case -1:
                            System.out.println("exit");
                            break;
                        default:
                            System.out.println("Invalid choice");
                            choice = 0;
                            break;
                    }
                }
//                System.out.println(string);
//                listForBinary.add(Integer.parseInt(string));
//                if (freqMap.get(Integer.parseInt(string))==null){
//                    freqMap.put(Integer.parseInt(string), 1);
//                }else{
//                    freqMap.put(Integer.parseInt(string), freqMap.get(Integer.parseInt(string))+1);
//                }
            }
        }catch (FileNotFoundException e){
            System.out.println("file not found");
        }

    }
    private void code(String string){
        getFrequencies(string);
        makeHeap();
        generateCode(heap.getMin(), "");
        writeFile(string, symbolTable);
    }
    private void getFrequencies(String string){
        try{
            readFreqsFromFile(string);
        }catch (FileNotFoundException e){
            System.out.println("File not found");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private void readFreqsFromFile(String string)throws FileNotFoundException, IOException{
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(string));
        int b = 0;
        count = 0;
        while ((b = bis.read())!=-1){
            freq[b] = freq[b]+1;
            count++;
        }
        bis.close();
    }
    private void makeHeap(){
        createHeap(freq);
        numberOfSymbols = heap.getSize();
        heap = createTree(heap);
    }
    private void createHeap(int[] freq){
        heap = new Heap(257);
        for(int i = 0; i < freq.length; i++){
            if (freq[i]!=0){
                Node node = new Node(freq[i], i);
                node.setLeaf();
                heap.insert(node);
            }
        }
    }
    private Heap createTree(Heap heap){
        int n = heap.getSize();
        for (int i = 0; i<n-1;i++){
            Node z = new Node();
            z.setLeft(heap.delMin());
            z.setRight(heap.delMin());
            z.setFrequency(z.getLeft().getFrequency()+z.getRight().getFrequency());
            heap.insert(z);
        }
        return heap;
    }
    private void generateCode(Node node, String code){
        if (node!=null){
            if(node.isLeaf()){
                symbolTable[node.getkey()] = code;
            }else{
                generateCode(node.getLeft(), code+"0");
                generateCode(node.getRight(), code +"1");
            }
        }
    }
    private void writeFile(String string, String[] symbolTable){
        try{
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(string));
            string = string+".huff";
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(string));
            writeHeader(bos);
            writeBody(bos, bis);
            bos.close();
        }catch (Exception e){
            System.out.println(e);
        }
    }
    private void writeHeader(BufferedOutputStream bos)throws IOException{
        bos.write(numberOfSymbols);
        Tools.writeFullInt(bos, count);
        writeFreqs(bos);
    }
    private void writeFreqs(BufferedOutputStream bos) throws IOException {
        for (int i = 0; i < symbolTable.length; ++i) {
            if(symbolTable[i] != null) {
                bos.write(i);
                Tools.writeFullInt(bos, freq[i]);
            }
        }
    }

    /**
     * Writes the actual file using Huffman code.
     * @param bos where to write
     * @param bis the file to compress
     * @throws IOException
     */
    private void writeBody(BufferedOutputStream bos, BufferedInputStream bis)
            throws IOException {
        String buffer = writeBodyText(bos, bis);
        writeRemainingBuffer(bos, buffer);
    }

    /**
     * Does the actual writing.
     * @param bos where to write
     * @param bis the file to compress
     * @return buffer that was not dividible by 8
     * @throws IOException
     */
    private String writeBodyText(BufferedOutputStream bos, BufferedInputStream bis)throws IOException {
        String buffer = "";
        boolean[] bits = new boolean[8];
        int byteRead;
        int byteToWrite = 0;

        while ((byteRead = bis.read()) != -1) {
            String charToWrite = symbolTable[byteRead];
            buffer = buffer + charToWrite;
            while(buffer.length() >= 8) {
                for(int i = 0; i < 8; ++i) {
                    if(buffer.charAt(i) == '1')
                        bits[i] = true;
                    else
                        bits[i] = false;
                }
                buffer = buffer.substring(8);
                byteToWrite = Tools.bitsToByte(bits);
                bos.write(byteToWrite);
            }
        }
        return buffer;
    }

    /**
     * Writes the last byte of file if number of bits was not dividible by 8.
     * @param bos where to write
     * @throws IOException
     */
    private void writeRemainingBuffer(BufferedOutputStream bos, String buffer) throws IOException {
        boolean[] bits = new boolean[8];
        int byteToWrite;
        if(buffer.length() > 0) {
            int difference = 8 - buffer.length();
            System.out.println("Number of thrash bits in last byte: " + difference);
            for(int i = 0; i < buffer.length(); ++i) {
                if(i > difference) {
                    bits[i] = false;
                }
                else {
                    if(buffer.charAt(i) == '1')
                        bits[i] = true;
                    else
                        bits[i] = false;
                }
            }
        }
        byteToWrite = Tools.bitsToByte(bits);
        bos.write(byteToWrite);
    }

    private void deCode(String PATH) {
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(PATH));
            String outPutPath = PATH.substring(0, PATH.length()-6);
            BufferedOutputStream bos =
                    new BufferedOutputStream(new FileOutputStream(outPutPath + ".decoded"));
            numberOfSymbols = bis.read();
            count = Tools.readFullLengthInt(bis);
            readSymbols(bis);
            makeHeap();
            writeDecodedFile(bis, bos);
            System.out.println("done");
        }
        catch(Exception e){
            System.out.println("File not found");
            System.exit(0);
        }
    }

    /**
     * Read symbolTable from header
     * @param bis where to read from
     * @throws IOException
     */
    private void readSymbols(BufferedInputStream bis)
            throws IOException {
        symbolTable = new String[256];
        for(int i = 0; i < numberOfSymbols; ++i) {
            int character = bis.read();
            freq[character] = Tools.readFullLengthInt(bis);
        }
    }

    /**
     * Writes the file using Huffman tree generated just before.
     * @param bis where to read from
     * @param bos where to write
     * @throws IOException
     */
    private void writeDecodedFile(BufferedInputStream bis,
                                  BufferedOutputStream bos) throws IOException {
        Node node = heap.getMin();
        int key;
        int character;
        System.out.println("Decompressing file");
        while(true) {
            character = bis.read();
            for(int i = 0; i < 8; ++i) {
                if(node.isLeaf()) {
                    key = node.getkey();
                    bos.write(key);
                    node = heap.getMin();
                    count--;
                    if(count == 0) {
                        break;
                    }
                }
                int bit = (character & 0x80);
                if(bit == 0x80) {
                    node = node.getRight();
                }
                else
                    node = node.getLeft();
                character <<= 1;
            }
            if(count == 0) {
                break;
            }
        }
        bos.close();
    }
}
