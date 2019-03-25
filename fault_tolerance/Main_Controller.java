
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * リンク故障について考察してみる
 * @author admin2
 *
 */
public class Main_Controller {

  double faultRatio = 0.00;
  int testCount = 10000;
  Random rand = new Random();

  public static void main(String[] args) {
   new Main_Controller().start();
  }
 // long ListTime;
  void start() {
   // ListTime = 0;
    //long start = System.currentTimeMillis();
    int type = 5;
    int[] nkm = {5,3,7};
    int N = Topology.calcN(type, nkm);
    ArrayList<Node> nodes = new ArrayList<Node>();
     for(int i = 0; i<N;i++) {
        Node node = new Node(type,i,nkm);
        nodes.add(node);
      }
      Topology topo = new Topology(nodes,type,nkm);
     //long endCreateTopology = System.currentTimeMillis();

      System.out.println("#Type 1");
      while(true) {
        int AvgHopCount = 0;
        if(faultRatio >= 1.00) break;
        int successCount = 0;
        for(int i = 0;i<testCount;i++) {
          //Create source and destination node
          int source,dest;
          while(true) {
            source = rand.nextInt(N);
            dest = rand.nextInt(N);
            if(source != dest) break;
          }
          ArrayList<Integer> faultList = faultList(N,source,dest);
     //   long lstart = System.currentTimeMillis();
          topo.faultInit(faultList);
          int result = topo.faultRun5(source,dest);
      //  long lend = System.currentTimeMillis();
      //  ListTime += (lend-lstart);
          if(result >= 1) {
            AvgHopCount += result;
            successCount++;
          }
          }
       //  long end = System.currentTimeMillis();
        System.out.println(faultRatio +",\t"+ (successCount * 1.0) / testCount+",\t"+ (AvgHopCount * 1.0) /successCount);
        /* System.out.println("ノード生成時間: "+(endCreateTopology -start) + "ms");
        System.out.println("探索時間: "+(end - endCreateTopology) + "ms");
        System.out.println("合計時間: "+(end -start) + "ms");
        System.out.println(ListTime);*/
        BigDecimal a = new BigDecimal(faultRatio);
        BigDecimal b = new BigDecimal("0.05");
        b = a.add(b).setScale(2,RoundingMode.HALF_UP);
        faultRatio = b.doubleValue();
        }
    }
  /**
   * 動かす方その２
   */
  void start2() {
    int type = 5;
    int[] nkm = {5,3,7};
    int N = Topology.calcN(type, nkm);
    int degree;
    ArrayList<Node> nodes = new ArrayList<Node>();
     for(int i = 0; i<N;i++) {
        Node node = new Node(type,i,nkm);
        nodes.add(node);
      }
      Topology topo = new Topology(nodes,type,nkm);
      degree = topo.getDegree();
      System.out.println("#Type 5");
      while(true) {
        int AvgHopCount = 0;
        if(faultRatio >= 1.00) break;
        int successCount = 0;
        for(int i = 0;i<testCount;i++) {
          int source,dest;
          while(true) {
            source = rand.nextInt(N);
            dest = rand.nextInt(N);
            if(source != dest) break;
          }
          //この辺処理しないと5%の処理してしまう
          ArrayList<Integer> faultList = linkFaultList(N,degree,source,dest); //壊れているリンク番号の指定
     //   long lstart = System.currentTimeMillis();
          topo.faultLinkInit(faultList); //準備するもの(
          int result = topo.faultLinkRun5(source,dest);
      //  long lend = System.currentTimeMillis();
      //  ListTime += (lend-lstart);
          if(result >= 1) {
            AvgHopCount += result;
            successCount++;
          }
          }
       //  long end = System.currentTimeMillis();
        System.out.println(faultRatio +",\t"+ (successCount * 1.0) / testCount+",\t"+ (AvgHopCount * 1.0) /successCount);
        /* System.out.println("ノード生成時間: "+(endCreateTopology -start) + "ms");
        System.out.println("探索時間: "+(end - endCreateTopology) + "ms");
        System.out.println("合計時間: "+(end -start) + "ms");
        System.out.println(ListTime);*/
        BigDecimal a = new BigDecimal(faultRatio);
        BigDecimal b = new BigDecimal("0.05");
        b = a.add(b).setScale(2,RoundingMode.HALF_UP);
        faultRatio = b.doubleValue();
        }
  }
 /**
  * リンク故障について軽く調べてみる
  * GSCCしか考慮しない
  * @param N
  * @param source
  * @param dest
  * @return
  */
  ArrayList<Integer> linkFaultList(int N,int degree,int source,int dest){
    int faultLinkCount = (int)(((N  * degree) / 2) * faultRatio);
    ArrayList<Integer> faultLinkList = new ArrayList<Integer>();
    ArrayList<Integer> list = new ArrayList<Integer>();
    
    for(int i =0;i< (N * degree /2);i++) list.add(i);
    Collections.shuffle(list);
    for(int i =0;i<faultLinkCount;i++) {
      faultLinkList.add(list.get(i));
    }
    Collections.sort(faultLinkList);
    return faultLinkList;
  }

  ArrayList<Integer> faultList(int N,int source,int dest) {
    int faultNodeCount = (int)(N * faultRatio);
    ArrayList<Integer> faultList = new ArrayList<Integer>();
    ArrayList<Integer> list = new ArrayList<Integer>();
    for (int i = 0;i<N; i++) list.add(i);
    //ノードのとき送信元と送信先を消去
    if(dest > source) {
      list.remove(dest);
      list.remove(source);
    }else {
      list.remove(source);
      list.remove(dest);
    }
    Collections.shuffle(list);
    for(int i = 0;i<faultNodeCount;i++) {
     faultList.add(list.get(i));
    }

    return faultList;
  }

}
