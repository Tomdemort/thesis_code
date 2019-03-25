package fault_tolerance2;


/**
 * nextRouteを主に扱う
 * 1:普通のバージョン
 * 2:cubeとnkを入れ替えたVersion()
 * 3:cube部分探索Part2
 * 4:ちょっと耐性つけてみました
 * @author admin
 *
 */
public class GSCC_Func {

 static int nextRoute(int sid, int did, int nn, int kk, int mm) {

    int cube_sid = getCubeAddr(sid,mm);
    int cube_did = getCubeAddr(did,mm);
    int nk_sid = getNKAddr(sid,mm);
    int nk_did = getNKAddr(did,mm);


    if(cube_sid != cube_did) {
      return CQ_Func.nextRoute(cube_sid, cube_did,mm);
    }else if(nk_sid != nk_did) {
      return mm + NK_Func.nextRoute(nk_sid, nk_did, nn, kk);
    }else {
      System.out.println("どっちもあってるのにエラー出てるよ");
      return -1;
    }

  }

  static int nextRoute2(int sid, int did, int nn, int kk, int mm) {

    int cube_sid = getCubeAddr(sid,mm);
    int cube_did = getCubeAddr(did,mm);
    int nk_sid = getNKAddr(sid,mm);
    int nk_did = getNKAddr(did,mm);


    if(nk_sid != nk_did) {
      return mm + NK_Func.nextRoute(nk_sid, nk_did, nn, kk);
    }else if(cube_sid != cube_did) {
      return CQ_Func.nextRoute(cube_sid, cube_did,mm);
    }else {
      System.out.println("どっちもあってるのにエラー出てるよ");
      return -1;
    }


  }

  static int nextRoute3(int sid, int did, int nn, int kk, int mm) {

    int cube_sid = getCubeAddr(sid,mm);
    int cube_did = getCubeAddr(did,mm);
    int nk_sid = getNKAddr(sid,mm);
    int nk_did = getNKAddr(did,mm);


    if(cube_sid != cube_did) {
      return CQ_Func.nextRoute2(cube_sid, cube_did,mm);
    }else if(nk_sid != nk_did) {
      return mm + NK_Func.nextRoute(nk_sid, nk_did, nn, kk);
    }else {
      System.out.println("どっちもあってるのにエラー出てるよ");
      return -1;
    }

  }

  static int nextRoute4(int sid, int did, int nn, int kk, int mm,int state){

    int cube_sid = getCubeAddr(sid,mm);
    int cube_did = getCubeAddr(did,mm);
    int nk_sid = getNKAddr(sid,mm);
    int nk_did = getNKAddr(did,mm);

    if(cube_sid != cube_did && state != 1) {
      return CQ_Func.nextRoute(cube_sid, cube_did,mm);
    }else if(nk_sid != nk_did || (nk_sid != nk_did && state == 1)) {
      return mm + NK_Func.nextRoute(nk_sid, nk_did, nn, kk);
    }else if(cube_sid != cube_did && nk_sid == nk_did){
      //無理やりcqの値返してエラーにする
      return CQ_Func.nextRoute(cube_sid, cube_did, mm);
    }else {
      System.out.println("どっちもあってるのにエラー出てるよ");
      return -1;
    }
  }
  static int nextRoute5(int sid, int did, int nn, int kk, int mm,int count) {

    int cube_sid = getCubeAddr(sid,mm);
    int cube_did = getCubeAddr(did,mm);
    int nk_sid = getNKAddr(sid,mm);
    int nk_did = getNKAddr(did,mm);

    if(cube_sid != cube_did) {
    //  System.out.println("CQ");
      //System.out.println(count);
      if(count == 0) {
        return CQ_Func.nextRoute(cube_sid, cube_did,mm);
      }
      else {
      //  System.out.println(CQ_Func.nextRoute3(cube_sid, cube_did, mm,count));
        return CQ_Func.nextRoute3(cube_sid, cube_did, mm,count);
      }
      //else return -1;
    }else if(nk_sid != nk_did) {
      if(count == 0) return mm + NK_Func.nextRoute(nk_sid, nk_did, nn, kk);
      else {
        int next = -2;
        int counter = 1;
       // while(next != -1) {
        next = NK_Func.nextRoute2(nk_sid, nk_did, nn, kk, counter);
        if(next == -1) return -1;//強制的に失敗
        else return mm + next;
       // }
      }
    }else {
      System.out.println("どっちもあってるのにエラー出てるよ");
      return -1;
    }
  }

  static int nextRoute6(int sid, int did, int nn, int kk, int mm,int state,int count) {

    int cube_sid = getCubeAddr(sid,mm);
    int cube_did = getCubeAddr(did,mm);
    int nk_sid = getNKAddr(sid,mm);
    int nk_did = getNKAddr(did,mm);

    if(cube_sid != cube_did && state != 1) {
    //  System.out.println("CQ");
      if(count == 0) {
        return CQ_Func.nextRoute(cube_sid, cube_did,mm);
      }
      else {
        return CQ_Func.nextRoute3(cube_sid, cube_did, mm,count);
      }
      //else return -1;
    }else if(nk_sid != nk_did || (nk_sid != nk_did && state == 1)) {
      if(count == 0) return mm + NK_Func.nextRoute(nk_sid, nk_did, nn, kk);
      else {
        int next = -2;
        int counter = 1;
       // while(next != -1) {
          next = NK_Func.nextRoute2(nk_sid, nk_did, nn, kk, counter);
          if(next == -1) return -1;//強制的に失敗
          else return mm + next;
       // }
      }
    }else if(cube_sid != cube_did && nk_sid == nk_did) {
      return -1;
    }else {
      System.out.println("どっちもあってるのにエラー出てるよ");
      return -1;
    }

  }

  

  static int getNKAddr(int id, int m){
    return (id >> m);
  }
  static int getCubeAddr(int id, int m){
    return id % (int)(Math.pow(2, m));
  }

  public static void main(String[] args) {

  }

}
