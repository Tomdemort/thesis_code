package controller5_debug1;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * もうわけわからないしほとんど最初から書き直す感じでやりましょう
 * Top module
 * 動かすノードは管理しておきましょう。
 * @author admin2
 *
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Controller5 {

  Random rand = new Random(5L);
  static double lambda = 0.05;
  static int nkType = 0;

  public static void main(String[] args) {
   //なぐり書き（べたがきVersion)
    int conflictCount = 0;
    for(int type = 1;type <= 5; type++) {
      //if(type == 4 || type == 5) continue; //臨時用
      int[] nkm = new int[3];
      if(type == 5 || type == 4) {
        nkm[0] = 5; nkm[1] = 3; nkm[2] = 5;
      }
      else if(type == 3) {
        nkm[0] = 8; nkm[1] = 4; nkm[2] = 1;
      }
      else if(type == 1 || type == 2) {
        nkm[0] = 1; nkm[1] = 1; nkm[2] = 11;
      }
      else if(type == 6) {
        nkm[0] = 5; nkm[1] = 1; nkm[2] = 1;
      }
      int N = Topology.calcN(type, nkm);
      System.out.println("#Type: " + type+",\t Latency,\t Conflict");
      ArrayList<Node> nodes = new ArrayList<Node>();
      while(lambda <= 1.0) {
        int count = 0;
        int eCount = 0; //errorCount;
        conflictCount = 0;
        for(int i = 0;i<100;i++) {
          nodes  = new ArrayList<Node>();
          for (int j = 0;j<N;j++) {
            Node node = new Node(type,j,nkm);
            node.setNKType(nkType);
            nodes.add(node);
          }
          Topology topo = new Topology(nodes,type,nkm);
          topo.setNKType(nkType);
          int[] runList = new Controller5().makeRunList(N);
          topo.init(runList);
          int tmp = topo.run();
          if(tmp == -1) {
            eCount++;
            i--; //なかったことにしてみる(lambda=1.0はきつそう)
          }
          conflictCount += topo.getAllConflictCount();
          count += tmp;
        }
        System.out.print(lambda +", \t " + (count * 1.0) /(100 - eCount));
        System.out.println(", \t " + (conflictCount * 1.0) /(100 - eCount));
        if(eCount >= 1) System.out.println("lambda=" + lambda +": ErrorCount:" + eCount);
        BigDecimal a = new BigDecimal(lambda);
        BigDecimal b = new BigDecimal("0.05");
        b = a.add(b).setScale(2,RoundingMode.HALF_UP);
        lambda = b.doubleValue();
        }
      lambda = 0.05;
/*      if(type == 3 && nkType == 0) {
        //やり直し
        type--; nkType = 2;
      }*/
      }

  }

  public static void main2(String[] args) {

    int type = 3;
    int[] nkm = {65,2,12};
    int N = Topology.calcN(type, nkm);
    ArrayList<Node> nodes = new ArrayList<Node>();
    for (int i = 0;i<N;i++) {
      Node node = new Node(type,i,nkm);
      nodes.add(node);
    }
    Topology topo = new Topology(nodes,type,nkm); //Nodes,type,nkm
    //int[] runList = {0,1,4,5,6,10,13,16,27}; // 0.25くらいで
    int[] runList = new Controller5().makeRunList(N);
    //int[] runList = {0,1};
    topo.init(runList); //送ったほうがいい気がする
    topo.run(); //うごかすやつ

  }
  int[] makeRunList(int n) {
    BigDecimal a = new BigDecimal(lambda + "");
    ArrayList<Integer> list = new ArrayList<Integer>();
    for(int i = 0;i<n;i++) list.add(i);
    Collections.shuffle(list); //1~(n-1)作ってシャッフル
    lambda = a.doubleValue();
    int nlambda = (int)(n * lambda);
    int[] runList = new int[nlambda];
    for(int i=0;i<nlambda;i++) {
      runList[i] = list.get(i);
    }
  //  System.out.print("RunList :");
  //  Console_Out.showArray(runList);
    return runList;
  }

}
