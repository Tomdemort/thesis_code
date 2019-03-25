package controller5_debug1;

import java.util.ArrayList;

public class SG_Func{

  static private int[] sAddr;
  static private int[] dAddr;
  static int n;

  static int[] adjacentAll(int id, int n) {
    int[] addr = NK_Func.changeListAddr(id, n, n);
    int[] adjacents = new int[n-1];
    int head = addr[0];
    int[] addr2 = new int[addr.length];

    for(int i = 0;i<adjacents.length;i++) {
      System.arraycopy(addr, 0, addr2, 0, addr.length);
      int tmp = (head <= i+1) ? i + 2 : i + 1;
      addr2[0] = tmp;
      for(int j = 1;j<addr.length;j++) {
        //headと同じものを探しぶっこむ
        if(addr2[j] == tmp) {
          addr2[j] = head;
          break;
        }
      }
      //後処理
      adjacents[i] = NK_Func.changeListAddr(addr2,n,n);

    }
    return adjacents;
  }
  //残り -> イコールのときの処理

  private static int searchHeadNext1() {
    int max = -1;
    for(int i = 0;i<n;i++)
      if(sAddr[i] != dAddr[i]) max = i;

    if(sAddr[0] == dAddr[max]) {
      return sAddr[max];
    }else {
      for(int j=0;j<n;j++) {
        if(sAddr[j] == dAddr[max]) {
          return sAddr[j];
        }
      }
    }
    return -1;
}

  /**
   * nkと同様にキューのものを返すもの
   * @param sid 起点ノード
   * @param did 宛先ノード
   * @param n パラメータ
   * @return 送信するキュー番号
   */
  static int nextRoute(int sid,int did,int n) {
    SG_Func.n = n;
    sAddr = NK_Func.changeListAddr(sid, n, n);
    dAddr = NK_Func.changeListAddr(did, n, n);
    int tmp = searchHeadNext2();
    tmp = (sAddr[0] < tmp) ? tmp - 2 : tmp - 1;
    return tmp;

  }
  static int nextRoute2(int sid,int did,int n) {
    SG_Func.n = n;
    sAddr = NK_Func.changeListAddr(sid, n, n);
    dAddr = NK_Func.changeListAddr(did, n, n);
    int tmp = searchHeadNext2();
    tmp = (sAddr[0] < tmp) ? tmp - 2 : tmp - 1;
    return tmp;
  }

  /**
   * cyclemerge base
   * @return
   */
  static int searchHeadNext2() {
    while(true){
      CyclePermutation cp = new CyclePermutation();
      cp.autoCreate(sAddr, dAddr, n);
      ArrayList<Cycle> cycle = cp.getCycleList();
      for(int i = 0;i<n;i++) {
        if(sAddr[i] != dAddr[i] && !(cycle.get(0).contains(sAddr[i]))){
          return sAddr[i];
        }
      }
      break;
    }
  phase2:while(true) {
    //if(isSameArray(sAddr,dAddr)) break;
    for(int i = 0;i<n;i++) {
      if(sAddr[0] == dAddr[i]) {
        if(i == 0) break phase2;
        return sAddr[i];
      }
    }
  }
    return -1;
  }
  void cyclemerge_Routing1() {
    //Phase 1はマージ部分
    phase1:while(true) {
      CyclePermutation cp = new CyclePermutation();
      cp.autoCreate(sAddr, dAddr, n);
      ArrayList<Cycle> cycle = cp.getCycleList();
      for(int i = 0;i < n;i++) {
        if(sAddr[i] != dAddr[i] && !(cycle.get(0).contains(sAddr[i]))) {
          //i番目が同じではなくかつ0番目のサイクルに含まれていない場合のルート
          swap(sAddr,0,i);
          System.out.println("swapped");
          continue phase1;
        }
      }
      break;
    }
  System.out.println("Phase2");
  //phase 2はルーティング部分
    phase2:while(true) {
      if(isSameArray(sAddr,dAddr)) break;
      for(int i = 0;i<n;i++) {
        if(sAddr[0] == dAddr[i]) {
          if(i == 0) break phase2;
          swap(sAddr,0,i);
        }
      }
    }
  }
  boolean isSameArray(int[] a, int[] b) {
    for(int i = 0;i < a.length;i++) {
      if(a[i] != b[i]) return false;
    }
    return true;
  }

  void swap(int[] array,int a, int b) {
    int tmp = array[b];
    array[b] = array[a];
    array[a] = tmp;
  }

  static int NextRoute2(int sid, int did, int n) {


    return 0;
  }


  public static void main(String[] args) {
    int n = 6;
    int[] addr = {5,1,4,3,2,6};
    int id = NK_Func.changeListAddr(addr,n,n);
    int[] adjacent_all = adjacentAll(id,n);
    for(int i = 0;i<n-1;i++) {
      int[] tmp = NK_Func.changeListAddr(adjacent_all[i], n, n);
      Console_Out.showArray(tmp);
    }
  }

}