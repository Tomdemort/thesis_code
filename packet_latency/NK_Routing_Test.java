package controller5_debug1;

import java.util.ArrayList;

/**
 * nkじゃなくて Star graphだった
 * 考察します。
 * @author admin2
 *
 */
public class NK_Routing_Test {

  static int n = 7;
  static int k = 5;
  int N = NK_Func.permutation(n, k);
  int SGN = NK_Func.permutation(n, n);//Star Graph用の総ノード数
  int[] sAddr = {1,5,3,2,4};//起点ノード配列(逐次更新)
  int[] dAddr = {1,2,5,6,7};//宛先ノード配列(更新しない)


  void swap(int[] array,int a, int b) {
    int tmp = array[b];
    array[b] = array[a];
    array[a] = tmp;
  }
  void put(int[] array,int a){
    array[0] = a;
  }

  //
  void eStar_Routing1(int source,int dest) {
    if(source == dest) {
      return;
    }else {
      int max = -1;
      for(int i = 0;i<n;i++)
        if(sAddr[i] != dAddr[i]) max = i;

      if(sAddr[0] == dAddr[max]) {
        swap(sAddr,0,max);
        int c = NK_Func.changeListAddr(sAddr, n, n);
        eStar_Routing1(c,dest);
      }else {
        for(int j=0;j<n;j++) {
          if(sAddr[j] == dAddr[max]) {
            swap(sAddr,0,j);
            int c = NK_Func.changeListAddr(sAddr, n, n);
            eStar_Routing1(c,dest);
          }
        }
      }
    }
  }

  //こっちのほうが直径短いらしい
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
  /**
   * nk version
   */
  void cyclemerge_Routing2() {
    //Phase 1はマージ部分
    phase1:while(true) {
      CyclePermutationNK cp = new CyclePermutationNK();
      cp.autoCreate(sAddr, dAddr, n, k);
      for(int i = 0;i < n;i++) {
        if(isTruePhase1(cp,sAddr,dAddr,n,k,i)) {
          //i番目が同じではなくかつ0番目のサイクルに含まれていない場合のルート
          if(i < k) swap(sAddr,0,i);
          else put(sAddr,cp.getSourceReminderList().get(i-k));
          Console_Out.showMessage("swapped:"+ i);
          Console_Out.showArray(sAddr);
          continue phase1;
        }
      }
      break;
    }
  System.out.println("Phase2");
  //phase 2はルーティング部分
    phase2:while(true) {
      CyclePermutationNK cp = new CyclePermutationNK();
      cp.autoCreate(sAddr, dAddr, n, k);
      if(isSameArray(sAddr,dAddr)) break;
      for(int i = 0;i<n;i++) {
        if(isTruePhase2(cp,sAddr,dAddr,n,k,i)) {
          if(i == 0) break phase2;
          if(i < k) swap(sAddr,0,i);
          else put(sAddr,cp.getSourceReminderList().get(i-k));
          Console_Out.showMessage("Swapped:"+i);
          Console_Out.showArray(sAddr);
        }
      }
    }
  }
  /**
   * ブーリアン先生(条件分岐するためのもの)
   * iがk以上の時の判定を分けることによる見やすさ向上するメソッド
   * @param cp Cycle Permutation
   * @param sAddr 起点ノードのアドレス番号
   * @param dAddr 宛先ノードのアドレス番号
   * @param n パラメータ
   * @param k パラメータ
   * @param i Integer
   * @return 条件が成り立てばTrue
   */
  boolean isTruePhase1(CyclePermutationNK cp,int[] sAddr,int[] dAddr,int n,int k,int i) {
    ArrayList<Cycle> cycle = cp.getCycleList();
    if(i < k){
      if(sAddr[i] != dAddr[i] && !(cycle.get(0).contains(sAddr[i])))
        return true;
    }else{
      if(cp.getSourceReminderList().get(i-k) != cp.getDestReminderList().get(i-k)
          && !(cycle.get(0).contains(cp.getSourceReminderList().get(i-k))))
        return true;
    }
    return false;
  }

  /**
   * phase2の判定式
   * @param sAddr 起点ノードのアドレス番号
   * @param dAddr 宛先ノードのアドレス番号
   * @param n パラメータ
   * @param k パラメータ
   * @param i Integer
   * @return 成り立てばTrue
   */
  boolean isTruePhase2(CyclePermutationNK cp,int[] sAddr, int[] dAddr, int n, int k, int i){
    if(i < k){
     if(sAddr[0] == dAddr[i]) return true;
    }else{
      if( sAddr[0] == cp.getDestReminderList().get(i-k)) return true;
    }
    return false;
  }

  void execute1() {
    int source = NK_Func.changeListAddr(sAddr, n, k);
    int dest = NK_Func.changeListAddr(dAddr, n, k);
    eStar_Routing1(source,dest);

    Console_Out.showArray(sAddr);
    Console_Out.showArray(dAddr);
    System.out.println("done");
  }

  void execute2() {
    cyclemerge_Routing2();

    Console_Out.showArray(sAddr);
    Console_Out.showArray(dAddr);
    System.out.println("done");
  }

  public static void main(String[] args) {
    //実行メソッドのみ制作したい
    //new NK_Routing_Test().execute1();
    new NK_Routing_Test().execute2();

  }

}
