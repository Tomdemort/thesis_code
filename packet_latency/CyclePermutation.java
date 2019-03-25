package controller5_debug1;

import java.util.ArrayList;

class Cycle extends ArrayList<Integer>{
  static final long serialVersionUID = 1L;

  Cycle(){
    new ArrayList<Integer>();
  }

  public void add(int n) {
   super.add(n);
  }


}


public class CyclePermutation extends ArrayList<Cycle> {
  private ArrayList<Cycle> list;
  static final long serialVersionUID = 1L;

  CyclePermutation(){
    list = new ArrayList<Cycle>();
  }
  /**
   * @return 型考え中。全部もありかな。
   */
  Cycle getCycle(int i) {
    return list.get(i);
  }
  public int size() {
    return list.size();
  }
  int getCycleSize(int i) {
    return list.get(i).size();
  }

  ArrayList<Cycle> getCycleList() {
    return list;
  }
  /**
   * 自動でサイクルを作ってくれる便利なやつ
   * StarGraphに対応させる
   */
  void autoCreate(int sid,int did,int n) {
    int[] sAddr = NK_Func.changeListAddr(sid,n,n);
    int[] dAddr = NK_Func.changeListAddr(did, n, n);
    Console_Out.showArray(sAddr);
    Console_Out.showArray(dAddr);
    autoCreate(sAddr,dAddr,n);
  }
  void autoCreate(int[] sAddr,int[] dAddr,int n) {
    int[] cNum = new int[sAddr.length];
    for(int i= 0;i < cNum.length;i++) cNum[i] = -1;
    int counter = 0;
    while(true) {
      //-1成分がなくなるまで続ける
      boolean b = true;
      for(int c : cNum) {
        if(c == -1)
          b = false;
      }
      if(b) break;


      //int startNum = sAddr[0];
      int min = 0;
      for(int i = 0; i<cNum.length;i++) {
        if(cNum[i] == -1) {
          min = i;
          break;
        }
      }
      int num = dAddr[min]; //２回目はcNum[0]でない最小値
      cNum[min] = counter;//２回目以降は＋１する
      Cycle c = new Cycle();
      c.add(sAddr[min]);
      for(int i = 0;i<dAddr.length;i++)
        if(sAddr[min] == dAddr[i]) cNum[i] = counter;

      if (sAddr[min] != dAddr[min]) c.add(dAddr[min]);
      loop : while(true) {

        for(int i = 0;i<sAddr.length;i++) {
          if(sAddr[i] == num) {
            if(cNum[i] != -1) break loop;
            num = dAddr[i];
            cNum[i] = counter;//ここも同様に＋１する
            c.add(dAddr[i]);
          }

        }
      }
      list.add(c);
      counter++;
    }
    //Cycle c = new Cycle();
   // System.out.println(list);
  }


  public static void main(String[] args) {
    CyclePermutation cp = new CyclePermutation();
   // cp.autoCreate(119, 0, 6);
    int[] sAddr = {1,2,3,5,4,6};
    int[] dAddr = {4,5,1,2,3,6};
    cp.autoCreate(sAddr, dAddr, 6);
  }



}
