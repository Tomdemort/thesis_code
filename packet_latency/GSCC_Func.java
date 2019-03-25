package controller5_debug1;

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

  static int getNKAddr(int id, int m){
    return (id >> m);
  }
  static int getCubeAddr(int id, int m){
    return id % (int)(Math.pow(2, m));
  }

  public static void main(String[] args) {

  }

}
