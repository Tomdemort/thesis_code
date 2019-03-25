package controller5_debug1;

import java.util.*;

public class Node {
  /*
   * ほしい物：隣接ノード(ArrayList<Node>)
   * 次数(nodes.length())
   * なんのトポロジーか(1~5でいいか。0は考える
   * 3 Parameters(n,k,m)
   *
  */
  int[] adj_addr;
  int id;
  int type; //Type入れたほうが処理しやすい気がする
  ArrayList<Queue_FIFO> queueList = new ArrayList<Queue_FIFO>();
  private int n,k,m;
  boolean run;
  int N; //送信パケット選択用
  ArrayList<Node> nodeAll; //臨時用(更新作業が必要(?))
  int destCount; //送信元パケットの到達回数

  private final static int QUEUE_CAPACITY = 2;
  final static int DEADLOCK_JUDGE_COUNT = 20; //この回数以上送信できない判定だとデッドロックと判断する
  final boolean inversation = false; //trueならビット反転したものそうでなければ反転ではない
  boolean[] isCapacityFull;
  Random rand = new Random(); //これ固定にするとネタになる
  private int nkType;
  private int CHOICETYPE = 0; //0 -> Random, 1 -> LRU

  /**
   * 結局これが一番簡単に実装できそう
   * @param type
   * @param nkm
   */
  Node(int type,int id,int[] nkm){
    this.id = id;
    this.type = type;
    int[] c, nk;
    int cubeID,nkID;
    run = false;
    conflictCount = 0;

    switch(type) {
    case 1: //HQとCQは0にしかない -> N[0] K[1] M[2]
      m = nkm[2];
      n = -1;
      k = -1;
      adj_addr = HQ_Func.adjacentAll(id,m);
      break;
    case 2:
      m = nkm[2];
      n = -1;
      k = -1;
      adj_addr = CQ_Func.adjacentAll(id,m);
      break;
    case 3: //n -> 0 , k -> 1
      n = nkm[0];
      k = nkm[1];
      m = -1;
      adj_addr = NK_Func.adjacentAll(id,n,k);
      break;
    case 4:
      n = nkm[0];
      k = nkm[1];
      m = nkm[2];
      nkID = GSC_Func.getNKAddr(id,m);
      cubeID = GSC_Func.getCubeAddr(id,m);
      c  = HQ_Func.adjacentAll(cubeID,m);
      nk = NK_Func.adjacentAll(nkID,n,k);
      adj_addr = setAdjacentProduct(id,c,nk);
      break;
    case 5:
      n = nkm[0];
      k = nkm[1];
      m = nkm[2];
      nkID = GSC_Func.getNKAddr(id,m);
      cubeID = GSC_Func.getCubeAddr(id,m);
      c  = CQ_Func.adjacentAll(cubeID,m);
      nk = NK_Func.adjacentAll(nkID,n,k);
      adj_addr = setAdjacentProduct(id,c,nk);
      break;
    case 6:
      n = nkm[0];
      k = -1;
      m = -1;
      adj_addr = SG_Func.adjacentAll(id,n);
    default:break;

    }
    N = Topology.calcN(type, nkm);
    init();
  }


  public static int Queue_Capacity() {
    return QUEUE_CAPACITY;
  }
  public void setNKType(int i) {
    nkType = i;
  }


  /**
   * nkとcubeのアドレスを１つにまとめる
   * @param id
   * @param cubeAddr
   * @param nkAddr
   * @return
   */
  private int[] setAdjacentProduct(int id, int[] cubeAddr,int[] nkAddr){

    //2-patterns
    int nkID = GSC_Func.getNKAddr(id,m);
    int cubeID = GSC_Func.getCubeAddr(id,m);

    int[] address = new int[cubeAddr.length + nkAddr.length];
    for(int i = 0;i<address.length;i++) {
      //NK -> そのまま HQ変える
      if(i < cubeAddr.length) address[i] = (nkID << m) + cubeAddr[i] ;
      else address[i] = (nkAddr[i-cubeAddr.length] << m) + cubeID;
    }

    return address;
  }

  int[] getAdjacentAddr() {
    return adj_addr;
  }


  public void setN(int param) {
    n = param;
  }
  public void setK(int param) {
    k = param;
  }
  public void setM(int param) {
    m = param;
  }




  void init() {
    //making queue
    Queue_FIFO queue;
    int tmp = 0;
    if(m == -1 || n == -1) tmp = 1;
    for(int i = 0; i<m+n+tmp+1;i++) {
     queue = new Queue_FIFO(QUEUE_CAPACITY);
     queueList.add(queue);
    }
    deadlockCount = new int[queueList.size()];
    //input 0
    for(int i = 0;i<deadlockCount.length;i++) {
      deadlockCount[i] = 0;
    }
  }

  /**
   * runさせるための準備？
   * @param nodeAll
   */
  void init2(ArrayList<Node> nodeAll) {
    destCount = 0;
    this.nodeAll = nodeAll; // copy?
  }


  /**
   * 雑に作らないようにする
   * メソッドだけで管理します
   */
  PacketData[] run() {
    if(run) computeNode(); //runList(computeNode)== trueのときパケット生成
    PacketData[] qValue = getQueue();
    //到着時の処理をする
    //for(PacketData pData : qValue) {
    for(int i = 0;i<qValue.length;i++) {
      if(qValue[i] == null) continue;
      else if(qValue[i].destID == id) {
        nodeAll.get(qValue[i].sourceID).destCount++; //宛先元ノードの到達回数を1増加
      //  System.out.println("Node "+qValue[i].sourceID +" Node "+ qValue[i].destID +" 到着しました");
        queueList.get(i).poll(); //到着したので消す
        qValue[i] = null; //到達データの消去
      }
    }
    PacketData[] data = crossbar(qValue);

   /*
    for(PacketData d : data) {
      if(d == null) System.out.print("null,");
      else {
        System.out.print(d +"("+d.destID+"),");
      }
    }
    System.out.println();
    */
    return data;
  }

  PacketData[] getQueue() {
    int qLength = queueList.size();
    PacketData[] qValue = new PacketData[qLength];
    for(int i = 0;i<qLength;i++) {
      qValue[i] = queueList.get(i).peek();  //わからぬ
    }
    return qValue;
  }

  //ここが最難関
  PacketData[] crossbar(PacketData[] qValue) {
    int[] next = routing(qValue);
    next = decision(next);
   /* System.out.print("Node " + id + " :");
    for(int i = 0;i<next.length;i++) {
      System.out.print(next[i] + ",");
    }
    System.out.println(); */
    PacketData[] pData = new PacketData[qValue.length];
    //ready が未実装
    for(int i = 0;i<pData.length;i++) {
      if(next[i] == -1) continue; //例外出るので先に処理
      else{ //1つ選択になったので消去
        pData[next[i]] = qValue[i];
        queueList.get(i).poll(); //確定したので先頭の要素を消す
      }
    }
    return pData;
  }

  //Typeごとに分岐する
  int[] routing(PacketData[] qValue) {
    int[] next = new int[qValue.length];
    int i = 0;
    for(PacketData data : qValue) {
      if(data == null) {
        next[i] = -1;
        i++;
        continue;
      }
      switch(type) {
        case 1:
          next[i] = HQ_Func.nextRoute(data.nowID, data.destID);
          break;
        case 2:
          next[i] = CQ_Func.nextRoute(data.nowID, data.destID, m);
          break;
        case 3:
          if(nkType == 0) next[i] = NK_Func.nextRoute(data.nowID, data.destID,n,k);
          if(nkType == 1) next[i] = NK_Func.nextRoute(data.nowID, data.destID,n,k);
          if(nkType == 2) next[i] = NK_Func.nextRoute3(data.nowID, data.destID,n,k);
          break;
        case 4:
          next[i] = GSC_Func.nextRoute(data.nowID, data.destID,n,k,m);
          break;
        case 5:
          next[i] = GSCC_Func.nextRoute(data.nowID, data.destID,n,k,m);
          break;
        case 6:
          next[i] = SG_Func.nextRoute(data.nowID, data.destID, n);
        default: break;
      }
      i++;
    }
    return next;
  }
  int[] deadlockCount; // キューが埋まっている状態がしばらく続くならデッドロックと判定する

  /**
   * 最初に来たものを優先的に入れてみる
   * Ready制御はここで行いたい
   * @param next
   */
  int[] decision(int[] next) {
    int[] tmps = new int[next.length];
    for(int i = 0;i<tmps.length;i++) {
      tmps[i] = -1;
    }
    ArrayList<PacketData> lostData = new ArrayList<PacketData>();
   //カウンタ的なもの
    Map<Integer,ArrayList<Integer>> map = new LinkedHashMap<Integer,ArrayList<Integer>>();
    for(int i = 0;i<next.length;i++) {
      if(next[i] == -1) continue;
      int test = nodeAll.get(adj_addr[next[i]]).getQueueAddr(id);//隣接ノードのキュー
      //else if(!nodeAll.get(adj_addr[next[i]]).isQueueFull(next[i])) { //送信先のキューが満杯のとき
      if(!nodeAll.get(adj_addr[next[i]]).isQueueFull(test)) { //送信先のキューが満杯のとき
        if(DEADLOCK_JUDGE_COUNT > 0) deadlockCount[i]++; // not run if n<=0
        if(deadlockCount[i] >= DEADLOCK_JUDGE_COUNT) {
          PacketData tmp = queueList.get(i).poll(); //Lostさせる
          //boolean b = queueList.get(i).offer(tmp); //入れ直しする
          //if(!b) System.out.println("boo");
          lostData.add(tmp);
          deadlockCount[i] = 0;
          //リストからくるくるさせて１週したらそうだと判定する
//          DebugData dData = new DebugData();
//          int source = tmp.sourceID;
//          int now = tmp.nowID;
//          int dest = tmp.destID;
//          String sourceS = NK_Func.showListChar(source, n, k);
//          String nowS = NK_Func.showListChar(now, n, k);
//          String destS = NK_Func.showListChar(dest, n, k);
//          int adjacentID = nodeAll.get(now).adj_addr[next[i]];
//          String adjacentIDS = NK_Func.showListChar(adjacentID,n,k);
//          System.out.println("Source:"+sourceS+", Now:"+nowS+", Dest:"+destS+",pnum:"+next[i]);
//          System.out.println("Adjacent Node:"+ adjacentIDS);
//          dData.add(adjacentID,i);
//          int loopCount = 1;
//          int queueIn = nodeAll.get(adjacentID).getQueueAddr(tmp.nowID);
//          tmp = nodeAll.get(adjacentID).queueList.get(queueIn).peek();
//          while(loopCount <= 100) { //ループを見つけるまであるいは一定数超えるまで;
//            source = tmp.sourceID;
//            now    = tmp.nowID;
//            dest   = tmp.destID;
//            sourceS = NK_Func.showListChar(source, n, k);
//            nowS = NK_Func.showListChar(now, n, k);
//            destS = NK_Func.showListChar(dest, n, k);
//            int next2 = NK_Func.nextRoute(now, dest, n, k);
//            adjacentID = nodeAll.get(now).adj_addr[next2];
//            adjacentIDS = NK_Func.showListChar(adjacentID,n,k);
//           if(now == adjacentID) {
//              System.out.println("目的ノードです");
//              break;
//            }
//            System.out.println("Source:"+sourceS+", Now:"+nowS+", Dest:"+destS+",pnum:"+next2);
//            boolean isTrue = true;
//            isTrue = dData.isSameID(adjacentID);
//            dData.add(adjacentID,next2);
//            System.out.println("Adjacent Node:"+ adjacentIDS);
//            queueIn = nodeAll.get(adjacentID).getQueueAddr(now);
//            tmp = nodeAll.get(adjacentID).queueList.get(queueIn).peek();
//            //deadlockCount[i] = 0;
//            if(isTrue) {
//              System.out.println("loopしています");
//              break;
//            }
//            loopCount++;
//          }
//          System.out.println();
        }
        continue;
      }
      else if(map.containsKey(next[i])) {
        ArrayList<Integer> list = map.get(next[i]);
        list.add(i);
        map.put(next[i],list);
      }else {
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(i);
        map.put(next[i], list); //左次元、右かぶり確認用（キューnumber)
      }
    }

    //一時的にノード内の2つ以上空いているキューに避難させる(2以下のときは知らん)
    //この案は却下されました
    // processingLostData(lostData);


    for(int i = 0;i< next.length;i++) {
      //ここからは帰ってからやりましょう(明日学校行くか未定)
      ArrayList<Integer> l = map.get(i);
      if(l == null) ; //i番目に行きたいという候補がない
      else if(l.size() >= 2) {
        int c = (CHOICETYPE == 0) ?  randomChoice(l) : LRUChoice(l) ;
        tmps[c] = next[c];
        deadlockCount[i] = 0; //0にもどす
        //roundRobinChoice();
      }else if(l.size() == 1) {
        int c = l.get(0);
        tmps[c] = next[c];
        deadlockCount[i] = 0;
      }else { //null
        // nothing
      }
    }
   // Console_Out.showArray(tmps);
    return tmps;
  }
  /**
   * 複数選択肢があったときに順番で選ぶもの
   * グローバル変数が必要そう()
   * @return
   */
  int[] roundRobinChoice() {
    conflictCount++;
    return null;
  }
  /**
   * Least Recently Used
   * @param list 配列の番号が入ってるはず
   * @return
   */
  int LRUChoice(ArrayList<Integer> list) {
    int max = -1;
    int maxsc = -1;
    for(Integer i:list) {
      if(queueList.get(i).stuckCount > maxsc) {
        max = i;
        maxsc = queueList.get(i).stuckCount;
      }
      queueList.get(i).scCountup();
    }
    conflictCount += list.size();
    queueList.get(max).scReset();
    return max;
  }

  /**
   * 競合したときに１増やすもの(ノード毎)
   */
  private int conflictCount;
  /**
   * 複数選択があったときランダムで選ぶもの
   * 1つだけにしよう()
   * @return
   */
  int randomChoice(ArrayList<Integer> list) {
    conflictCount+= list.size();
    Random rand = new Random();
    int num = rand.nextInt(list.size());
    return list.get(num);
  }
  /**
   * 複数選択肢があったとき低次元から来たものを選択するもの
   * 消去済み。やらかし
   * @return
   */
  int[] defaultChoice() {
    /*
     *    for(int i = 0;i<next.length;i++) {
      b = true;
      if(next[i] == -1) b = false;
      else if(!nodeAll.get(getAdjacentAddr()[next[i]]).isQueueFull(i))  b = false; //宛先のキューが埋まっているならばfalse
      else {
        for(int j = 0;j<i;j++) {
          if(tmps[j] == next[i])  b = false;
        }
      }
      if(b) tmps[i] = next[i];
      else tmps[i] = -1;
    }
     *
     */
    return null;
  }
  void processingLostData(ArrayList<PacketData> lostData) {
    if(lostData.isEmpty()) return;
    ArrayList<Integer> notFullCapacity = new ArrayList<Integer>();
    //ノードのキューから空き数が２以上のものを見ていく
    for(int i = 0;i<queueList.size();i++) {
      if(queueList.get(i).size() <= (QUEUE_CAPACITY - 2)) {
        notFullCapacity.add(i);
      }
    }
    while(!lostData.isEmpty()) {
      Collections.shuffle(notFullCapacity);
      int num = notFullCapacity.remove(0);
      queueList.get(num).offer(lostData.remove(0));
    }

  }

  int getQueueAddr(int addr) {
    for(int i = 0;i<adj_addr.length;i++) {
      if(adj_addr[i] == addr ) {
        return i;
      }
    }
    System.out.println("Error");
    return -1;
  }

  /**
   * キューが満杯ならばfalseを返すもの
   * @return true or false
   */
  boolean isQueueFull(int i) {
    if(queueList.get(i).size() < QUEUE_CAPACITY) return true;
    return false;
  }


  int qFullCount = 0;
  //実行するごとに１つだけパケットに入れる
  void computeNode() {
    int did;
    while(true) {
      if(inversation) did = bitInversation();
      else did = rand.nextInt(N);
      if(id != did) break;//同じだったらやり直し
    }
   // System.out.println("Packet" + id +" gets " + did);
    PacketData pdata = new PacketData(id,did);
    boolean b =queueList.get(queueList.size()-1).offer(pdata);
    if(!b) {
   //   System.out.println("Queue is Full - Node "+ id ); //何もしない
      qFullCount++;
    }
  }
  /**
   * ビット反転したものを変えすもの
   * @return
   */
  int bitInversation() {
    int did = -1;
    if(type == 1 || type == 2) {
      did = ((~id) << (32 - m)) >>> (32 - m);
    }else if(type == 3){
      did = NK_Func.bitInversation(n, k, id);
    }else if(type == 4 || type == 5) {
      int mid = GSC_Func.getCubeAddr(id, m);
      int nkid = GSC_Func.getNKAddr(id, m);
      did = (((~mid) << (32 - m)) >>> (32 - m))
          + (NK_Func.bitInversation(n, k, nkid) << m);
    }
    return did;
  }

  public int getConflictCount() {
    return conflictCount;
  }

  public static void main(String[] args){
    int[] nkm = {5,3,4};
    Node node = new Node(1,0,nkm);
    node.init();
    Queue_FIFO q = new Queue_FIFO(8);
    PacketData data = new PacketData(0,2);
    q.add(data);
    node.queueList.add(0,q);
    q = new Queue_FIFO(8);
    data = new PacketData(0,3);
    q.add(data);
    node.queueList.add(1,q);
    q = new Queue_FIFO(8);
    data = new PacketData(0,4);
    q.add(data);
    node.queueList.add(2,q);
    PacketData[] result =node.run();
    //System.out.println(node.queueList.get(2).peek().destID);
    int i = 0;
    for(PacketData pd : result) {
      if(pd == null) { System.out.println("i:"+i + " null"); i++; continue;}
      System.out.println("i:"+i+"destNode:"+pd.destID);
      i++;
    }

  }
  //俗に言う選択器



}
