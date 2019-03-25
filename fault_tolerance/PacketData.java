

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