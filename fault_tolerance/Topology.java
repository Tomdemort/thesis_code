

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
  FaultLinkList fll;

  Topology(){

  }

  Topology(ArrayList<Node> nodes, int type,int[] nkm){
    nodeAll = nodes;
    this.type = type;
    n = (type >= 3) ? nkm[0] : -1;
    k = (type >= 3) ? nkm[1] : -1;
    m = (type != 3) ? nkm[2] : -1;
    dandD();
    N = nodeAll.size(); //すっとぼけ
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
    default:break;
    }
  }

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

  void faultInit(ArrayList<Integer> faultList) {
    for(int i = 0;i<N;i++) {
      nodeAll.get(i).faultNode = false;
    }
    for(Integer i:faultList) {
      nodeAll.get(i).faultNode = true;
    }

  }
  /**
   * 何が必要なのか微妙に理解してない
   * 1.隣接ノードを読み込む
   * 2.その配列の長さ分、nullをぶっこむ
   * 以上
   * ブーリアンリストのリスト製作予定
   * @param faultLinkList
   */
  void faultLinkInit(ArrayList<Integer> faultLinkList) {
    ArrayList<FaultLink> list = new ArrayList<FaultLink>();
    FaultLink fl;
    int[] adj_addr;
    ArrayList<Boolean> ab = new ArrayList<Boolean>();
    for(int i =0;i<degree;i++) {
      ab.add(null);
    }
    for(Node n:nodeAll) {
      adj_addr = n.getAdjacentAddr();
      fl = new FaultLink(adj_addr,ab);
      list.add(fl);
    }
    fll = new FaultLinkList(list,faultLinkList);
    fll.setFaultLinkAll();
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


 // ArrayList<Node> getAdjacents(int id){
 //   return null;
 // }

  /**
   * shortest Path 1
   * @param source
   * @param dest
   */
  int faultRun(int sourceID,int destID) {
    int nowID = sourceID;
    int hopCount = 1;
     while(nowID != destID) {
      //1回ずついどうさせてみますか
      if(type != 5) return -1;
      int next = GSCC_Func.nextRoute(nowID, destID, n, k, m);
      if(adjacentNode.get(nowID).get(next).faultNode) return -1;
      nowID = adjacentNode.get(nowID).get(next).id;
      hopCount++;
    }
     return hopCount;
  }
  /**
   * shortest Path 2
   * @param sourceID
   * @param destID
   * @return
   */
  int faultRun2(int sourceID,int destID) {
    int nowID = sourceID;
    int hopCount = 1;
     while(nowID != destID) {
      //1回ずついどうさせてみますか
      if(type != 5) return -1;
      int next = GSCC_Func.nextRoute3(nowID, destID, n, k, m);
      if(adjacentNode.get(nowID).get(next).faultNode) return -1;
      nowID = adjacentNode.get(nowID).get(next).id;
      hopCount++;
    }
     return hopCount;
  }
  /**
   * Cube <-> NK changeable
   * @param sourceID
   * @param destID
   * @return
   */
  int faultRun3(int sourceID,int destID) {
    //System.out.println("#Cube <-> nk reversible");
    int nowID = sourceID;
    int state = 0; //0 -> CrossedCube部分探索時, 1 -> CrossedCube部分故障時、NK探索時
    int hopCount = 1;
     while(nowID != destID) {
      //1回ずついどうさせてみますか
      if(type != 5) return -1; //といってもType5以外存在しないけど
      int next = GSCC_Func.nextRoute4(nowID, destID, n, k, m,state);
      if(adjacentNode.get(nowID).get(next).faultNode && (state == 1 || ( state == 0 && next >= m))){
        //両方とも故障しているときまたはnk探索部分が故障
        return -1;
      }
      else if(adjacentNode.get(nowID).get(next).faultNode && state == 0){
        //Cube部分が故障しているとき
        state = 1;
        continue;
      }
      nowID = adjacentNode.get(nowID).get(next).id;
      state = 0;
      hopCount++;
    }
     return hopCount;
  }
  /**
   * CQ and NK Fault Tolerance Only 
   * @param sourceID
   * @param destID
   * @return
   */
  int faultRun4(int sourceID,int destID) {
    int nowID = sourceID;
    int hopCount = 1;
    int counter = 0;
     while(nowID != destID) {
      //1回ずついどうさせてみますか
      if(type != 5) return -1;
      int next = GSCC_Func.nextRoute5(nowID, destID, n, k, m,counter);
      if(next == -1) return -1;//失敗
    //  System.out.println(adjacentNode.get(nowID).get(next).id+", next:" + next +"count:" + counter);
      if(adjacentNode.get(nowID).get(next).faultNode) {
        counter++;
        continue;
      }
      nowID = adjacentNode.get(nowID).get(next).id;
      hopCount++;
      counter = 0;
    }
     return hopCount;

  }
  /**
   * Reversible and cqnk fault tolerance 
   * @param sourceID
   * @param destID
   * @return
   */
  int faultRun5(int sourceID,int destID) {
    //System.out.println("#Cube <-> nk reversible");
    int nowID = sourceID;
    int state = 0; //0 -> CrossedCube部分探索時, 1 -> CrossedCube部分故障時、NK探索時
    int hopCount = 1;
    int counter = 0;
     while(nowID != destID) {
      //1回ずついどうさせてみますか
      if(type != 5) return -1; //といってもType5以外存在しないけど
      int next = GSCC_Func.nextRoute6(nowID, destID, n, k, m,state,counter);
      if(next == -1 && state == 0) { //CQで耐部分がだめ
        state = 1; //NKに切り替え
        counter = 0;
        continue;
        // break; //-1のときはcounterが1以上しか存在しない
      }else if(next == -1 && state == 1) return -1; //ぜんぶだめ
      else if(adjacentNode.get(nowID).get(next).faultNode && (state == 1 || ( state == 0 && next >= m))){
        //両方とも故障しているときまたはnk探索部分が故障
        counter++;
        //state = 0;
        continue;
      }
      else if(adjacentNode.get(nowID).get(next).faultNode && state == 0){
        //Cube部分が故障しているとき
        counter++;
        continue;
      }else if(adjacentNode.get(nowID).get(next).faultNode) {
        counter++;
        continue;
      }
      counter = 0;
      nowID = adjacentNode.get(nowID).get(next).id;
      state = 0;
      hopCount++;
    }
     return hopCount;
  }
  
  
  int faultLinkRun(int sourceID,int destID) {
    int nowID = sourceID;
    int hopCount = 1;
     while(nowID != destID) {
      //1回ずついどうさせてみますか
      if(type != 5) return -1;
      int next = GSCC_Func.nextRoute(nowID, destID, n, k, m);
      //
      boolean b = fll.getFaultLink(nowID).get(next);
      if(b) return -1; //ここの行をリンク用にする
      nowID = adjacentNode.get(nowID).get(next).id;
      hopCount++;
    }
     return hopCount;
  }

  /**
   * Cube <-> NK changeable
   * @param sourceID
   * @param destID
   * @return
   */
  int faultLinkRun3(int sourceID,int destID) {
    //System.out.println("#Cube <-> nk reversible");
    int nowID = sourceID;
    int state = 0; //0 -> CrossedCube部分探索時, 1 -> CrossedCube部分故障時、NK探索時
    int hopCount = 1;
     while(nowID != destID) {
      //1回ずついどうさせてみますか
      if(type != 5) return -1; //といってもType5以外存在しないけど
      int next = GSCC_Func.nextRoute4(nowID, destID, n, k, m,state);
      boolean b = fll.getFaultLink(nowID).get(next);
      if(b && (state == 1 || ( state == 0 && next >= m))){
        //両方とも故障しているときまたはnk探索部分が故障
        return -1;
      }
      else if(b && state == 0){
        //Cube部分が故障しているとき
        state = 1;
        continue;
      }
      nowID = adjacentNode.get(nowID).get(next).id;
      state = 0;
      hopCount++;
    }
     return hopCount;
  }
  int faultLinkRun4(int sourceID,int destID) {
    int nowID = sourceID;
    int hopCount = 1;
    int counter = 0;
     while(nowID != destID) {
      //1回ずついどうさせてみますか
      if(type != 5) return -1;
      int next = GSCC_Func.nextRoute5(nowID, destID, n, k, m,counter);
      if(next == -1) return -1;//失敗
      boolean b = fll.getFaultLink(nowID).get(next);
      if(b) {
        counter++;
        continue;
      }
      nowID = adjacentNode.get(nowID).get(next).id;
      hopCount++;
      counter = 0;
    }
     return hopCount;

  }
  int faultLinkRun5(int sourceID,int destID) {
    //System.out.println("#Cube <-> nk reversible");
    int nowID = sourceID;
    int state = 0; //0 -> CrossedCube部分探索時, 1 -> CrossedCube部分故障時、NK探索時
    int hopCount = 1;
    int counter = 0;
     while(nowID != destID) {
      //1回ずついどうさせてみますか
      if(type != 5) return -1; //といってもType5以外存在しないけど
      int next = GSCC_Func.nextRoute6(nowID, destID, n, k, m,state,counter);
      if(next == -1 && state == 0) { //CQで耐部分がだめ
        state = 1; //NKに切り替え
        counter = 0;
        continue;
        // break; //-1のときはcounterが1以上しか存在しない
      }else if(next == -1 && state == 1) return -1; //ぜんぶだめ
      boolean b = fll.getFaultLink(nowID).get(next);
      if(b) {
        counter++;
        continue;
      }
      counter = 0;
      nowID = adjacentNode.get(nowID).get(next).id;
      state = 0;
      hopCount++;
    }
     return hopCount;
  }


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
