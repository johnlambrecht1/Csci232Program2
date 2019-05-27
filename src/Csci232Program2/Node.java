package Csci232Program2;

public class Node {
    Node left, right, parent;
    private int frequency = -1;
    private int key = -1;
    private boolean isLeaf = false;
    public Node(){}
    Node(int frequency, int key){
        this.key = key;
        this.frequency = frequency;
    }
    public void setParent(Node node){
        parent = node;
    }
    public void setLeft(Node node){
        left = node;
    }
    public void setRight(Node node){
        right = node;
    }
    public void setFrequency(int n){
        frequency = n;
    }
    public void setkey(int n){
        key = n;
    }
    public Node getParent(){
        return parent;
    }
    public Node getLeft(){
        return left;
    }
    public Node getRight(){
        return right;
    }
    public int getFrequency(){
        return frequency;
    }
    public int getkey(){
        return key;
    }
    public int compare(Node node){
        return frequency-node.frequency;
    }
    public void setLeaf(){
        isLeaf = true;
    }
    public boolean isLeaf(){
        return isLeaf;
    }
}
