package controller5_debug1;

import java.math.*;
import java.util.*;

/**
 * Hyper-debugMode
 * @author admin2
 *
 */
public class Controller5_3 {
  Random rand = new Random(5L);
  static double lambda = 0.05;
  static int nkType = 2;

  public static void main2(String[] args){
    int type = 6;
    int[] nkm = new int[3];
    nkm[0] = 5; nkm[1] = 3; nkm[2] = 1;
    int N = Topology.calcN(type,nkm);

    ArrayList<Node> nodes = new ArrayList<Node>();
    while(true) {
      int count = 0;
      int eCount = 0;

      //1回のみ実験を行う
      nodes = new ArrayList<Node>();
      for(int j = 0;j<N;j++) {
        Node node = new Node(type,j,nkm);
        node.setNKType(nkType);
        nodes.add(node);
      }
      //トポロジの指定
      Topology topo = new Topology(nodes,type,nkm);
      topo.setNKType(nkType);
      int[] runList = new Controller5_3().makeRunList(N);
      topo.init(runList);
      //tmpということで動作させる部分
      int tmp = topo.run();
      if(tmp == -1) {
        eCount++;
        //ここで試走してみる
      }
      break; //脱出るーと
    }
    System.out.println("A");
  }

  public static void main(String[] args) {
   //なぐり書き（べたがきVersion)
    int conflictCount = 0;
    int type = 6;
    for(int p = 5;p <= 10; p++) {
      int[] nkm = new int[3];
       nkm[0] = p; nkm[1] = 3; nkm[2] = 1;
      int N = Topology.calcN(type, nkm);
      System.out.println("#Type: " + type + "(NKType:"+nkType+",n = "+p + "),\t Latency,\t Conflict");
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
          int[] runList = new Controller5_3().makeRunList(N);
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
      }
    if(nkType == 0) {
      nkType = 2;
      String[] s = {};
      main(s);
    }

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
