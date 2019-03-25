package controller5_debug1;

import java.util.ArrayList;

public class Topology {

  ArrayList<Node> nodeAll;
  ArrayList<ArrayList<Node>> adjacentNode;
  ArrayList<Integer> computeNode;
  int type;
  int degree,diameter; // d and D
  int n,k,m;
  int N; //Total node
  private final int END_COUNT = 200; //終了条件
  final int QUEUE_CAPACITY = Node.Queue_Capacity(); //8に固定しておく
  private int allConflictCount;
  Topology(){

  }
  void setNKType(int i) {
  }

  Topology(ArrayList<Node> nodes, int type,int[] nkm){
    nodeAll = nodes;
    this.type = type;
    n = (type >= 3 || type != 6) ? nkm[0] : -1;
    k = (type >= 3) ? nkm[1] : -1;
    m = (type != 3 && type == 6) ? nkm[2] : -1;
    dandD();
    N = nodeAll.size(); //すっとぼけ
    allConflictCount = 0;
    computeNode = new ArrayList<Integer>();
    makeAdjacent();
  }
  int getDiameter() {
    return diameter;
  }
  int getDegree() {
    return degree;
  }
  private void makeAdjacent() {
    adjacentNode = new ArrayList<ArrayList<Node>>();
    for(int i = 0;i<nodeAll.size();i++) {
      ArrayList<Node> adj = new ArrayList<Node>();
      Node n = nodeAll.get(i);
      int[] nAddr = n.getAdjacentAddr();
      for(int addr: nAddr) {
        //get -> add
        adj.add(nodeAll.get(addr));
      }//end j?
      adjacentNode.add(adj);
    }//end i
  }
  /**
   * 次数と直径を定義する
   *
   */
  private void dandD() {
    switch(type) {
    case 1:
      degree = m;
      diameter = m;
      break;
    case 2:
      degree = m;
      diameter = (m+1) / 2 + (m+1) % 2;
      break;
    case 3:
      degree = n - 1;
      diameter = (n / 2 >= k) ? 2 * k - 1 : k + (n-1)/2;
      break;
    case 4:
      degree = n + m -1;
      diameter = (n / 2 >= k) ? m + 2 * k - 1 : m + k * (n-1)/2 ;
      break;
    case 5:
      degree = n + m - 1;
      diameter = (n / 2 >= k) ? (m+1)/2 + (m+1)%2 + 2 * k - 1 : (m+1)/2 + (m+1)%2 + k + (n-1)/2;
      break;
    case 6:
      degree = n - 1;
      diameter = 3 * (n - 1) / 2;
      break;
    default:break;
    }
  }
  //ここからは明日(7/27)やる
  //Static(?)
  static int calcN(int type, int[] nkm) {
    int N = 0;
    switch(type) {
    case 1:
    case 2:
      N = (int)Math.pow(2, nkm[2]);
      break;
    case 3:
      N = NK_Func.permutation(nkm[0], nkm[1]);
      break;
    case 4:
    case 5:
      N = (int)(Math.pow(2, nkm[2])) * NK_Func.permutation(nkm[0], nkm[1]);
      break;
    case 6:
      N = NK_Func.permutation(nkm[0], nkm[0]);
    default: break;
    }
    return N;
  }



  Node[] getAdjacents(int id) {
    Node[] node = new Node[degree];
    int[] adj_id = nodeAll.get(id).getAdjacentAddr();
    for(int i = 0;i<degree;i++) {
      node[i] = nodeAll.get(adj_id[i]);
    }
    return node;
  }

  void init(int[] runList) {
    for(int list : runList) {
      computeNode.add(list);
      nodeAll.get(list).run = true;
    }
    for(Node n: nodeAll) {
      n.init2(nodeAll);
    }
  }
  void init(ArrayList<Integer> runList) {
    computeNode = runList;
    for(int list: runList) {
      nodeAll.get(list).run = true;
    }
    for(Node n: nodeAll) {
      n.init2(nodeAll);
    }
  }

  //2次元配列作りましょう
  ArrayList<PacketData[]> allData = new ArrayList<PacketData[]>();
  int errorCount = 0;
  /**
   * メソッドだけにしたいところではある
   * if(list.contain(~)はあるかも)
   */
  int run() {
    int z = 1;
    while(z <= 50000) { //dead loopになっても困るので制御
    //  System.out.println("z="+z);
      allData.clear(); //初期化のつもり
      PacketData[] data;
      for(Node n : nodeAll) {
        data = n.run();
        allData.add(data);
      }
      int nodeID = 0; // これなんだろう
      for(PacketData[] d: allData) {
        for(int i = 0; i< d.length;i++) {
          if(d[i] == null) continue;
          int c = nodeAll.get(nodeID).getAdjacentAddr()[i]; //毎回呼び出すのもどうかと思うが・・・。
          int oldc = d[i].nowID;
          d[i].nowID = c; //書き換えています
          int qi = nodeAll.get(c).getQueueAddr(oldc);
          //String message = "現在地: " + oldc + ", SourceID: " + d[i].sourceID + ", DestID: " +d[i].destID +
          //    ", QueueNumber: " + qi;
        //  Console_Out.showMessage(message);
          boolean b =nodeAll.get(c).queueList.get(qi).offer(d[i]); //キューにぶっこんでいるができていない
          if(!b) {
    //       System.out.println(d[i].sourceID+","+d[i].nowID+","+d[i].destID);
    //        System.out.println(nodeAll.get(c).queueList.get(i));
            errorCount++;
          }
        }
        nodeID++;
      }
      //このあとReady判定の更新が終わったので到着したときの処理をする
      int destCountAll = 0;
      for(Node n: nodeAll) {
        if(n.destCount >= END_COUNT) destCountAll++;
      }
      if(destCountAll == computeNode.size()) {
        //終了時のコンソール出力など
       // System.out.println(z+"回で終了");
       // System.out.println("エラーカウント数："+ errorCount);
       // fullQCount();
       // result();
        for(Node n:nodeAll) allConflictCount += n.getConflictCount();
        return z; //どうreturnするか。
      }
      z++;
    }
   // fullQCount();
   // result();
    return -1;
  }
  /**
   * Ready信号の制御作成したいけどやる気が出ない
   * 全部の信号を管理するもの(眠いです)
   */
  void modifyReady() {

  }
  private void result() {
    for(int i : computeNode) {
    //for(int i = 0;i<nodeAll.size();i++) {
      System.out.println("Node " + i + " :" + nodeAll.get(i).destCount);
    }
  }
  private void fullQCount() {
    int count = 0;
    for(int i : computeNode) {
      count += nodeAll.get(i).qFullCount;
    }
    System.out.println("QueueFullCount: " + count );
  }
  public int getAllConflictCount() {
    return allConflictCount;
  }


 // ArrayList<Node> getAdjacents(int id){
 //   return null;
 // }


  public static void main(String[] args) {
    int type = 5; //
    int[] nkm = {5,3,4};
    int N = calcN(type,nkm);
    ArrayList<Node> nodes = new ArrayList<Node>();
    for(int i = 0;i<N;i++) {
      Node node = new Node(type,i,nkm);
      nodes.add(node);
    }
    Topology topo = new Topology(nodes,type,nkm);
    Node[] adjacent = topo.getAdjacents(59);
    for(Node adj:adjacent) {
      System.out.println(adj.id);
    }
    System.out.println("Nodes:" + topo.N + ", Parameter(n,k,m): " + topo.n +"," +topo.k +"," + topo.m +
        "\ndegree:" + topo.degree + " diameter:" + topo.diameter );
  }

}
