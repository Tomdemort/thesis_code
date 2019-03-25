package controller5_debug1;

import java.util.*;

public class DebugData {
  private ArrayList<Integer> loopList;
  private ArrayList<PacketData> loopPList;
  private int nextQNum;
  
  DebugData(){
    loopList = new ArrayList<Integer>();
    loopPList = new ArrayList<PacketData>();
    nextQNum = -1;
  }
  public void add(PacketData d) {
    loopPList.add(d);
    loopList.add(d.nowID);
  }
  public void add(int id,int num) {
    loopList.add(id);
    nextQNum = num;
  }
  public int getNextQNum() {
    return nextQNum;
  }
  
  /**
   * ループしているIDを探す
   * @param i
   * @return
   */
  public boolean isSameID(int i) {
    if(loopList.contains(i)) return true;
    else return false;
  }

}
