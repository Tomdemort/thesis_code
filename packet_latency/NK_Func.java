package controller5_debug1;

import java.util.ArrayList;
import java.util.Collections;


public class NK_Func {

  static byte[] addr;
  static byte[] goal;
  static int n;
  static int k;
  static int[] sAddr; //ごちゃごちゃ
  static int[] dAddr; //ごちゃごちゃ2


  private void swap(int[] array,int a, int b) {
    int tmp = array[b];
    array[b] = array[a];
    array[a] = tmp;
  }
  private void put(int[] array,int a){
    array[0] = a;
  }

  static boolean isSameArray(int[] a, int[] b) {
    for(int i = 0;i < a.length;i++) {
      if(a[i] != b[i]) return false;
    }
    return true;
  }
  /**
   * nk version
   */
  static int cyclemerge_Routing2() {
    //Phase 1はマージ部分
    phase1:while(true) {
      CyclePermutationNK cp = new CyclePermutationNK();
      cp.autoCreate(sAddr, dAddr, n, k);
      for(int i = 0;i < n;i++) {
        if(isTruePhase1(cp,sAddr,dAddr,n,k,i)) {
          //i番目が同じではなくかつ0番目のサイクルに含まれていない場合のルート
          if(i < k) return sAddr[i];
          else return cp.getSourceReminderList().get(i-k);
        }
      }
      break;
    }
  //phase 2はルーティング部分
    phase2:while(true) {
      CyclePermutationNK cp = new CyclePermutationNK();
      cp.autoCreate(sAddr, dAddr, n, k);
      if(isSameArray(sAddr,dAddr)) return -1;
      for(int i = 0;i<n;i++) {
        if(isTruePhase2(cp,sAddr,dAddr,n,k,i)) {
          if(i == 0) break phase2;
          if(i < k) return sAddr[i];
          else return cp.getSourceReminderList().get(i-k);
        }
      }
    }
    return -1;
  }
 static boolean isTruePhase1(CyclePermutationNK cp,int[] sAddr,int[] dAddr,int n,int k,int i) {
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

 static boolean isTruePhase2(CyclePermutationNK cp,int[] sAddr, int[] dAddr, int n, int k, int i){
    if(i < k){
     if(sAddr[0] == dAddr[i]) return true;
    }else{
      if( sAddr[0] == cp.getDestReminderList().get(i-k)) return true;
    }
    return false;
  }


  /*
   * ここからしばらくNKNextRouteです
   */
  public static int nextRoute(int sid, int did,int nn,int kk) {
    n = nn;
    k = kk;
    addr = changeListAddr(sid);
    int headSourceAddr = (int)(addr[0]);
    goal = changeListAddr(did);
    int headDestAddr = (int)searchHeadNext(sid,did);

    if(headSourceAddr >= headDestAddr ) {
      return headDestAddr - 1;
    }else {
      return headDestAddr - 2;
    }
  }
  public static int nextRoute3(int sid, int did,int nn,int kk) {
    n = nn;
    k = kk;
    sAddr = changeListAddr(sid,n,k);
    int headSourceAddr = sAddr[0];
    dAddr = changeListAddr(did,n,k);
    int headDestAddr = cyclemerge_Routing2();
    if(headSourceAddr >= headDestAddr) return headDestAddr -1;
    else return headDestAddr - 2;
  }


  public static byte[] routing2(){
  if(!isSameArray(addr,goal)) return null;
    //まず先頭の数を見る.先頭が1だった時も場合分けするか?
  if(addr[0] == goal[0]){
      for(int i = 0;i<goal.length;i++){
        if(addr[i]!= goal[i]){
          addr = swap(addr,0,i);
        //  System.out.println(addr[0]+"と"+addr[i] + "をスワップ");
          return addr;
        }
      }
    }
    for(int i = 1;i<addr.length;i++){
      if(addr[0] == goal[i]){
        addr = swap(addr,0,i);
      //  System.out.println(addr[0]+"と"+addr[i] + "をスワップ");
        return addr;
      }
    }//end for
      int maxj = Integer.MIN_VALUE;
      outside : for(int i =0;i<goal.length;i++){
        for(int j = 0; j < addr.length;j++){
          if(goal[i]== addr[j]) continue outside;
        }
        if(goal[i] != addr[i]){
          maxj = i;
        }
      }
      addr[0] = goal[maxj];
    //  System.out.println(addr[0] + "を先頭に配置");
      return addr;
  }

  /**
   * 最小の順番の方にしてみる
   * @return
   */
  public static byte[] routing(){
  if(!isSameArray(addr,goal)) return null;
    //まず先頭の数を見る.先頭が1だった時も場合分けするか?
  if(addr[0] == goal[0]){
      for(int i = 0;i<goal.length;i++){
        if(addr[i]!= goal[i]){
          addr = swap(addr,0,i);
        //  System.out.println(addr[0]+"と"+addr[i] + "をスワップ");
          return addr;
        }
      }
    }
    for(int i = 1;i<addr.length;i++){
      if(addr[0] == goal[i]){
        addr = swap(addr,0,i);
      //  System.out.println(addr[0]+"と"+addr[i] + "をスワップ");
        return addr;
      }
    }//end for
      int minj = Integer.MAX_VALUE;
      outside : for(int i =0;i<goal.length;i++){
        for(int j = 0; j < addr.length;j++){
          if(goal[i]== addr[j]) continue outside;
        }
        if(goal[i] != addr[i]){
          minj = i;
        }
      }
      addr[0] = goal[minj];
    //  System.out.println(addr[0] + "を先頭に配置");
      return addr;
  }


  private static boolean isSameArray(byte[] a,byte[] b){
    for(int i = 0;i<a.length;i++){
      if(a[i]!=b[i]){
        return true;
      }
    }
    return false;
  }

  private static byte[] swap(byte[] a,int i,int j){
    byte x = a[i];
    a[i] = a[j];
    a[j] = x;
    return a;
  }
  int[] random(){
    int an = n; //random
    int ak = k;
    ArrayList<Integer> list = new ArrayList<Integer>();
    for(int i = 1 ; i <= an; i++){
      list.add(i);
    }
    Collections.shuffle(list);
    int[] array = new int[ak];
    for(int i = 0; i < ak; i++){
      array[i] = list.get(i);
    }

    return array;
  }
  /**
      変換かけて次のアドレス探して再変換かけて返す
      次の行先を探していきます。
      Static対応させようと思ったけどいるのかこれ？
  */
  private static int searchNext(int sid, int did){
  //  byte[] did_array = NK_Func.changeListAddr(did); //staticは(ry)
    byte[] b_array = routing(); //これから作る

    sid = NK_Func.changeListAddr(b_array); //Static(ry)

    return sid;
  }
  /**
   * アドレスの先頭の番号を返すもの
   * -1はしていない
   * @return
   */
  private static int searchHeadNext(int sid, int did){
  //  byte[] did_array = NK_Func.changeListAddr(sid);
    byte[] b_array = routing();

    return b_array[0];
  }

   /**
   * アドレスの先頭の番号を返すもの
   * -1はする(-2もする)
   * @return
   */
  int searchNext_Queue(){
  //  byte[] did_array = NK_Func.changeListAddr(sid);

    byte[] b_array = routing();
    int a = (b_array[0] > addr[0]) ? 2 : 1;
    addr = b_array;
    return b_array[0] - a;
  }

  /**
   * 多分なくても返せるでしょこれ
   * @return 次へ進むノードアドレス
   */
  int searchNext() {
    byte[] nextid = routing();
    return NK_Func.changeListAddr(nextid);
  }

  static byte[] changeListAddr(int a) {
    int tmp = a;
    byte[] list = new byte[k];
   // System.out.println("修正前:");
    for(int i =0;i<k;i++) {
      list[i] = (byte)((tmp / (permutation(n-i-1,k-i-1)))+1);
  //    System.out.print(list[i]+", ");
      tmp = tmp % permutation(n-i-1,k-i-1);
    }
    for(int i = 1;i<k;i++) { //右側
      boolean[] b = new boolean[i+1];
      for(int l = 0;l<=i;l++) b[l] = true;
      for(int j = 0;j<i;j++) { //ある一定数繰り返しをする
        for(int l = 0;l<i;l++) { //ただループさせたいだけ、whileでいいかも
          if(list[i] >= list[l] && b[l]) {
            list[i]++;
            b[l] = false;
          }
        }
      }
    }
    return list;
  }
  static int changeListAddr(byte[] a) {
    int aNum = 0;
    for(int i = 0; i< a.length;i++) {
      int tmp = 0; //これ入るの負の数か？
      //現在の配列番地の数値を確認する
      for(int j = 1;j<=n;j++) {
        if(a[i] == j) {
           for(int k = 0;k<i;k++){
             if(a[k] <= j) tmp++;
          }
           aNum = aNum + permutation(n-i-1,k-i-1) * (j-1-tmp);
        }
      }
    }
    return aNum;
  }

  /*
   * ここまでRouting
   */

  public static int permutation(int n, int k) {
    if(k > 1) return n * permutation(n-1,k-1);
    else if(k==1) return n;
    else return 1;
  }
  static int[] changeListAddr(int a,int n,int k) {
    int tmp = a;
    int[] list = new int[k];
   // System.out.println("修正前:");
    for(int i =0;i<k;i++) {
      list[i] = (int)((tmp / (permutation(n-i-1,k-i-1)))+1);
  //    System.out.print(list[i]+", ");
      tmp = tmp % permutation(n-i-1,k-i-1);
    }
    for(int i = 1;i<k;i++) { //右側
      boolean[] b = new boolean[i+1];
      for(int l = 0;l<=i;l++) b[l] = true;
      for(int j = 0;j<i;j++) { //ある一定数繰り返しをする
        for(int l = 0;l<i;l++) { //ただループさせたいだけ、whileでいいかも
          if(list[i] >= list[l] && b[l]) {
            list[i]++;
            b[l] = false;
          }
        }
      }
    }
    return list;
  }
  static int changeListAddr(int[] a,int n,int k) {
    int aNum = 0;
    for(int i = 0; i< a.length;i++) {
      int tmp = 0; //これ入るの負の数か？
      //現在の配列番地の数値を確認する
      for(int j = 1;j<=n;j++) {
        if(a[i] == j) {
           for(int l = 0;l<i;l++){
             if(a[l] <= j) tmp++;
          }
           aNum = aNum + permutation(n-i-1,k-i-1) * (j-1-tmp);
        }
      }
    }
    return aNum;
  }
  /**
   *
   * @param id
   * @param n
   * @param k
   * @param num パケットのやつ
   * @return
   */
  static int adjacent(int id, int n,int k,int num) {
    int[] aAddr = changeListAddr(id,n,k);
    int num2 = (aAddr[0] <= num + 1) ? num + 2  : num + 1 ; //補正値を加えたもの
    for(int i = 1;i<k;i++){
      if(aAddr[i] == num2 ) {
        int tmp = aAddr[0];
        aAddr[0] = aAddr[i];
        aAddr[i] = tmp;
        id = changeListAddr(aAddr,n,k);
        return id;
      }
    }
    aAddr[0] = num2;
    id = changeListAddr(aAddr,n,k);
    return id;
  }
  /**
   * -1とか指定していなくて飛ばしていたはず
   * @param id
   * @param n
   * @param k
   * @return
   */
  static int[] adjacentAll(int id,int n,int k) {
    int[] adjacents = new int[n-1]; //次数はk-1
    for(int i =0;i < adjacents.length;i++) {
      //ちょっとだけ不安定(iパラメータをいじる）
      adjacents[i] = adjacent(id,n,k,i);
    }
    return adjacents;
  }

  static int bitInversation(int n, int k, int id) {

    int[] nkAddr = changeListAddr(id,n,k);
    int[] dnkAddr = new int[k];

    //k=1について雑に処理する
    if(k==1) {
      byte addr = 1;
      while(true) {
        if(nkAddr[0] != addr) {
          dnkAddr[0] = addr;
          return changeListAddr(dnkAddr,n,k);
        }else n++;
        if(n > Byte.MAX_VALUE) {
          System.out.println("Error: Byte.MAX_VALUE");
          break;
        }
      }
    }

    dnkAddr[0] = nkAddr[0]; //先頭は固定する

    int tmp = nkAddr[1];
    //1つだけずらす
    for(int i = 1;i<k-1;i++) {
      dnkAddr[i] = nkAddr[i+1];
    }
    dnkAddr[k-2] = tmp;

    //最初に使用している数をふるい落とす
    byte[] check = new byte[n];
    for(int i = 0;i<k;i++) {
      check[nkAddr[i]-1] = -1;
    }
    ArrayList<Integer> noList = new ArrayList<Integer>();
    for(int i = 0;i<check.length;i++) {
      if(check[i] != -1) noList.add(i+1);
    }
    int counter = 1;
    while(true) {
      if(counter >= k || noList.size() < counter) break; //リスト全部使用したら終了
      dnkAddr[counter] = noList.get(counter-1);
      counter++;
    }


    return changeListAddr(dnkAddr,n,k);
  }
  
  public static String showListChar(int[] a) {
    StringBuilder sb = new StringBuilder();
    for(int i =0;i<a.length;i++) {
      sb.append(a[i]);
    }
    return sb.toString();
  }
  public static String showListChar(int id,int n,int k) {
    int[] a = changeListAddr(id,n,k);
    return showListChar(a);
  }


  public static void main(String[] args) {
    n = 7;
    k = 5;
    int id = 13;

    Console_Out.showArray(changeListAddr(id));
    int result = bitInversation(n,k,id);

    byte[] result2 = changeListAddr(result);

    Console_Out.showArray(result2);
  }

  public static void main2(String[] args) {
    n = 5;
    k = 3;
    int id = 14;
    int[] aid = changeListAddr(id,n,k);
    for(int a: aid){
      System.out.print(a+",");
    }
    System.out.println("\n");
    int[] adjacents = adjacentAll(id,n,k);
    for(int adjacent : adjacents){
      System.out.println(adjacent);
      aid = changeListAddr(adjacent,n,k);
        for(int a: aid){
          System.out.print(a+",");
        }
      System.out.println();
    }
  }


}
