import java.util.ArrayList;

/**
 * ノード遷移とかそういうの
 * 
 * @author admin2
 *
 */
public class CQ_Func {
  
  static private boolean[] b_sid;
  static private boolean[] b_did;
  static private int[] rho;
  static private int m;
  
  static int nextRoute(int sid,int did,int dim) {
    m = dim;
    b_sid = changeTypetoBool(sid);
    b_did = changeTypetoBool(did);
    defineRho();
    int k = routing(); //step 1-2
    //int k = routing2(); // step 4-5
    if(k == -2) System.out.println("Output -2");
    return k;
  }
  /*
   * DeadLockにならないか確認したいだけ 
   */
  static int nextRoute2(int sid, int did,int dim) {
    m = dim;
    b_sid = changeTypetoBool(sid);
    b_did = changeTypetoBool(did);
    int k = -2;
    for(int i = b_sid.length-1; i >= 0; i--) {
      if(b_sid[i] != b_did[i]) {
        k = i;
        break;
      }
    }
    //int k = routing();
    if(k== -2) System.out.println("output -2");
    return k;
  }
  static int nextRoute3(int sid, int did, int dim,int count) {
    m = dim;
    b_sid = changeTypetoBool(sid);
    b_did = changeTypetoBool(did);
    int count2 = 1;
    for(int i = ((b_sid.length-1)/2)-1;i>=0;i--) {
      if(b_sid[2*i+1] != b_did[2*i+1] && !b_sid[2*i] && !b_did[2*i]) {
        if(count2 == count) return 2*i+1;
        else count2++;
      }
    } 
    //ない場合最上位ビットを変化させる
    for(int i = (b_sid.length-1);i>=0;i--) {
     if(b_sid[i] != b_did[i]) {
       if(count2 == count) return i;
       //else count2++;
       else return -1;
     }
    }
   return -1;
  }
  
  static private void defineRho() {
    rho = new int[b_did.length/2 + b_did.length%2];//繰り上がり
    int l = 0; //most signficant double bit
    for(int i = b_did.length-1; i >= 0; i--) {
      if(b_sid[i] != b_did[i]) {//上から数えているので一致しなかったらmsd-double bit
        //これはしたをとる
        for(l = i/2+1;l<rho.length;l++){
          rho[l] = 0;//pattern 1
        }
        l = i/2;
        if(i%2 == 1) {
          if( b_sid[2*l] != b_did[2*l] && b_sid[2*l+1] != b_did[2*l+1]) //両方とも一致しない
            rho[l] = 2; //pattern 2
          else rho[l] = 1;
        }else rho[l] = 1; //片方のみ異なる
        break;
      }
    }
    //下位ビットの探索します
    for(int i = l-1;i>=0;i--){
      if(dppair(i,0)) rho[i] = 0;
      else rho[i] = 1; //pattern 3
    }


  }


/**
 * Crossed Cubeを探索する上で最も大事なメソッド
 * 3つ存在する，Caseは使わない（使えない）
 * 0はそのまま、１は左が逆、2は右が逆
 * @param j
 * @param caseInt
 * @return
 */
  static private boolean dppair(int j,int caseInt){
    boolean b = false;
    int count = 0;
    for (int i = j+1;i<rho.length;i++){
      count += rho[i];
    }
    int four = -1; // 最上位ビットが1個しかないとき
    four = addrFourth(j);
    if(caseInt == 0){
      if((four == 5 || four == 15) && count % 2 == 0) b = true;
      else if ((four == 7 || four == 13) && count % 2 == 1) b = true;
      else if (four == 0 || four == 10 ) b = true;
    }else if(caseInt == 1){
      if((four == 13 || four == 7) && count % 2 == 0) b = true;
      else if ((four == 15 || four == 5) && count % 2 == 1) b = true;
      else if (four == 8 || four == 2 ) b = true;
    }else if(caseInt == 2){
       if((four == 1 || four == 11) && count % 2 == 0) b = true;
        else if ((four == 3 || four == 9) && count % 2 == 1) b = true;
        else if (four == 4 || four == 14 ) b = true;
    }
     return b;
  }

  static private int addrFourth(int j){
    int a = (b_sid[2*j+1] ? 8 : 0) + (b_sid[2*j] ? 4 : 0) +
        (b_did[2*j+1] ? 2 : 0) + (b_did[2*j] ? 1 : 0);
    return a;

  }

  /**
   * intがたの整数をbool型の配列に直す
   *
   * @param n
   */
  static private boolean[] changeTypetoBool(int n) {
    String form = "%"+ m + "s";
    String s = Integer.toBinaryString(n);
    s = String.format(form, s).replace(' ', '0');
    boolean[] b = new boolean[s.length()];
    for(int i = 0;i<b.length;i++) {
      b[i] = s.charAt(s.length()-i-1) == '1' ? true : false;
    }
    return b;
  }

  static private int routing() {
    ArrayList<Integer> T = getT();
    ArrayList<Integer> Q = getQ();
  //  System.out.println(T);
    if(!T.isEmpty()) {
   //   System.out.println("Step 1");
      for(int t : T) {
        return ONE_STEP_ROUTE(t,Q);
      }
    }
    //STEP2 --------------------------------------
    while(!Q.isEmpty()) {
    //  System.out.println(Q);
   //   System.out.println("Step 2");
      int i = Integer.MAX_VALUE;
      for(int q: Q) {
        if(!(q == m/2 && m%2 == 1)) {
          if((dppair(q,1) || dppair(q,2)) && q < i) {
            i = q;
          }
        }
      }
      if(i == Integer.MAX_VALUE) {
       //Qの最大値を探す
        i = Integer.MIN_VALUE;
        for(int q : Q) {
          if(i < q)  i = q;
        }
      }
     return ONE_STEP_ROUTE(i,Q);
    }
    return -1;
  }
  
  //Step4とStep5
  static private int routing2() {
  ArrayList<Integer> Q = getQ();
  
  //step5
  if(!Q.isEmpty()) {
    int l = -2;
    int m = -2;
    for(Integer q: Q) {
      if(l < q) {
        m = l;
        l = q;
      }else if(m < q) m = q;
    }
    if(m == -2) return ONE_STEP_ROUTE(l,Q);
    if( rho[l] == 1 && dppair(m,1)) {
     return ONE_STEP_ROUTE(m,Q); 
    }else {
      if( rho[l] == 1 && dppair(m,2)) {
       return ONE_STEP_ROUTE(l,Q);
       //return ONE_STEP_ROUTE(m,Q);
       
      }
      else return ONE_STEP_ROUTE(l,Q);
    }
  }
    
    return -1;
  }


  static boolean[] b_calc(boolean[] a,int m) {
    if(m >=2) {
      for(int i = 0;i<m/2;i++) {
        if(a[i*2] == true) {//doublebit下位が1
          a[i*2+1] = !a[i*2+1];
        }
      }
    }
    a[m] = !a[m]; //ビット変換
    return a;
  }

  static private ArrayList<Integer> getT(){
    ArrayList<Integer> t = new ArrayList<Integer>();
    for(int i = 0;i<rho.length;i++){
      if(i == m/2 && m%2 == 1) break;
      if((dppair(i,1) || dppair(i,2)) && rho[i] != 0){
        t.add(i);
      }
    }
    return t;
  }

  static private ArrayList<Integer> getQ(){
    ArrayList<Integer> q = new ArrayList<Integer>();
    for(int i = 0;i<rho.length;i++){
      if(rho[i] != 0) q.add(i);
    }
    return q;
  }

  static private int ONE_STEP_ROUTE(int i,ArrayList<Integer> Q) {
  //  System.out.println("ONE_STEP_ROUTE");
    if(rho[i] == 2){
      return 2*i;//;calc(b_sid,2*i);
    }else{ //rho = 1
      if(i == m/2 && m%2 == 1) return 2*i;//calc(b_sid,2*i);
      else if(dppair(i,1)) return 2*i+1;//calc(b_sid,2*i+1);
      else if(dppair(i,2)) return 2*i;//calc(b_sid,2*i);

      for(int j = 0;j<Q.size();j++) {
        if(Q.get(j) == i) {
          Q.remove(j);
          break;
        }
      }
    }
    rho[i] = rho[i] - 1; //0になったら終了
    return -2;
  }

  
  
  
  /**
   * 隣接ノードを1つずつ求める
   * @param id CQのID
   * @param m 反転させるビット
   * @return
   */
  static int adjacent(int id,int m,int dim) {
    boolean[] b_nodeid = changeTypeToBool(id,dim);
    calc(b_nodeid,m);
    int a_nodeid = 0;
    int l = 1;
    for(int i = 0;i<b_nodeid.length;i++) {
      if(b_nodeid[i]) a_nodeid +=l;
      l = l * 2;
    }
    return a_nodeid;
  }
  
  static private boolean[] changeTypeToBool(int n,int dim) {
    String form = "%"+ dim + "s";
    String s = Integer.toBinaryString(n);
    s = String.format(form, s).replace(' ', '0');
    boolean[] b = new boolean[s.length()];
    for(int i = 0;i<b.length;i++) {
      b[i] = s.charAt(s.length()-i-1) == '1' ? true: false;
    }
    return b;    
  }
  static private void calc(boolean[] a, int m) {
    if(m >=2) {
      for(int i=0;i<m/2;i++) {
        if(a[i*2])
          a[i*2+1] = !a[i*2+1];
      }
    }
    a[m] = !a[m];
  }
  
  static int[] adjacentAll(int id,int m) {
    int[] adjacents = new int[m];
    for(int i = 0;i < m;i++) {
      adjacents[i] = adjacent(id,i,m);
    }
    return adjacents;
  }
  public static void main(String[] args) {
    int id = 63;
    int m = 10;
    System.out.println(adjacent(id,5,m));
    int[] adjacents = adjacentAll(id,m);
    for(int adjacent: adjacents) {
      System.out.println(adjacent);
    }
  }

}
