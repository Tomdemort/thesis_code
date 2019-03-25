package controller5_debug1;

import java.util.*;
import java.util.concurrent.*;

public class PacketData{

  int nowID;
  int sourceID;
  int destID;
  String data;


  PacketData(int sid,int did){
    sourceID = sid;
    nowID = sid;
    destID = did;
  }


  //使わない気がする
  public static void main(String[] args) {

  }
}
class Queue_FIFO extends ArrayBlockingQueue<PacketData>{
  BlockingQueue<PacketData> queue;
  final static long serialVersionUID = 1L;
  int stuckCount;
  
  public Queue_FIFO(final int CAPACITY){
    super(CAPACITY);
    queue = new ArrayBlockingQueue<PacketData>(CAPACITY);
    stuckCount = 0;
  }
  void scCountup() {
    stuckCount++;
  }
  void scReset() {
    stuckCount = 0;
  }
  
}