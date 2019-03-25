package fault_tolerance2;

import java.util.*;

public class Hypercube extends Topology{
  
  
  Hypercube(ArrayList<Node> node,int[] nkm){
    super(node,1,nkm);
    super.degree = nkm[0]; //書き換えていく
    super.diameter = nkm[0];
  }
  
  public Node[] getAdjacents(int id) {
    Node[] node = super.getAdjacents(id);
    return node;
  }
    
  
  public static void main(String[] args) {
    int type = 1; //
    int N = 16;
    int[] nkm = {4,4,4};
    ArrayList<Node> nodes = new ArrayList<Node>();
    for(int i = 0;i<N;i++) {
      Node node = new Node(type,i,nkm);
      nodes.add(node);
    }
    Topology topo = new Hypercube(nodes,nkm);
    Node[] adjacent = topo.getAdjacents(0);
    for(Node adj:adjacent) {
      System.out.println(adj.id);
    }
  }}