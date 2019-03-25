
import java.util.*;

public class FaultLinkList {

  ArrayList<FaultLink> faultLinkAll;
  ArrayList<Integer> faultLinkNumber;

  FaultLinkList(ArrayList<FaultLink> links,ArrayList<Integer> faultLinkNumber){
    faultLinkAll = links;
    this.faultLinkNumber = faultLinkNumber;
  }
  ArrayList<Integer> getAdjacent(int i){
    return faultLinkAll.get(i).adjacent;
  }
  ArrayList<Boolean> getFaultLink(int i){
    return faultLinkAll.get(i).faultLink;
  }
  /**
   * 全部nullにもどすもの
   */
  void resetFaultLinkAll() {
    int size = getFaultLink(0).size();
    ArrayList<Boolean> tmp = new ArrayList<Boolean>();
    for(int i=0;i<size;i++) tmp.add(null);
    for(int i =0;i<faultLinkAll.size();i++) {
      faultLinkAll.get(i).getFaultLinks().clear();
      faultLinkAll.get(i).getFaultLinks().addAll(tmp);
    }
  }
 void setFaultLinkAll() {
    //１個ずつ見ていくのが一番簡単そう
    //int p = 0; int q = 0; int r = 0; //pはリストの中身、yはリンク内、rはわからん
    int p = 0; int q = 0; int qj = 0; int r = 0;
    if(faultLinkNumber.size() == 0) {
      for(int i = 0;i<faultLinkAll.size();i++) {
        for(int j=0;j<getFaultLink(j).size();j++) {
          faultLinkAll.get(i).setFaultLink(j, false); //ここなんだろう
        }
      }
    }
    for(int i = 0;i<faultLinkAll.size();i++) {
      for(int j = 0;j<getFaultLink(j).size();j++) {
         if(faultLinkAll.get(i).getFaultLink(j) == null) {
           if(faultLinkNumber.get(p) == r) {
             p = (p != faultLinkNumber.size()-1) ? p + 1 : p; //カウンタ
             faultLinkAll.get(i).setFaultLink(j, true);
             q = faultLinkAll.get(i).getAdjacent(j);
             qj = getAdjacent(q).indexOf(i);//隣接先の元アドレス探索
             faultLinkAll.get(q).setFaultLink(qj, true); //ここなんだろう
           }else {
             faultLinkAll.get(i).setFaultLink(j, false);
             q = faultLinkAll.get(i).getAdjacent(j);
             qj = getAdjacent(q).indexOf(i);//隣接先の元アドレス探索
             faultLinkAll.get(q).setFaultLink(qj, false); //ここなんだろう
           }//end r
           r++;
         }else{

         }//end null
       }//end j
    }//end i
  }


  void setAll(ArrayList<?> faultLinkList,int n,int k,int m) {

  }
  public static void main(String[] args) {
   //ただの3-cube
   int[] array0 = {1,2,4};
   int[] array1 = {0,3,5};
   int[] array2 = {3,0,6};
   int[] array3 = {2,1,7};
   int[] array4 = {5,6,0};
   int[] array5 = {4,7,1};
   int[] array6 = {7,4,2};
   int[] array7 = {6,5,3};
   FaultLink fl0 = new FaultLink(array0);
   FaultLink fl1 = new FaultLink(array1);
   FaultLink fl2 = new FaultLink(array2);
   FaultLink fl3 = new FaultLink(array3);
   FaultLink fl4 = new FaultLink(array4);
   FaultLink fl5 = new FaultLink(array5);
   FaultLink fl6 = new FaultLink(array6);
   FaultLink fl7 = new FaultLink(array7);
   ArrayList<FaultLink> fl = new ArrayList<FaultLink>();
   fl.add(fl0);
   fl.add(fl1);
   fl.add(fl2);
   fl.add(fl3);
   fl.add(fl4);
   fl.add(fl5);
   fl.add(fl6);
   fl.add(fl7);
   ArrayList<Integer> list = new ArrayList<Integer>();
   //list.add(0);
   list.add(1);
   list.add(2);
   list.add(6);
   list.add(8);
   list.add(11);
   FaultLinkList fll = new FaultLinkList(fl,list);
   fll.setFaultLinkAll();
   for(int i = 0;i<fll.faultLinkAll.size();i++) {
     System.out.println(fll.faultLinkAll.get(i).getFaultLinks());
   }
   System.out.println("Finished");

  }

}
class FaultLink{
  ArrayList<Integer> adjacent;
  ArrayList<Boolean> faultLink;

  FaultLink(ArrayList<Integer> list){
    adjacent = new ArrayList<Integer>();
    faultLink = new ArrayList<Boolean>();
    for(Integer i: list) adjacent.add(i);
    for(int i=0;i<adjacent.size();i++) faultLink.add(null);
  }
  FaultLink(int[] arrays){
    adjacent = new ArrayList<Integer>();
    faultLink = new ArrayList<Boolean>();
    for(Integer i: arrays) adjacent.add(i);
    for(int i = 0;i<adjacent.size();i++) faultLink.add(null);
  }
  FaultLink(int[] arrays,ArrayList<Boolean> blist){
    adjacent = new ArrayList<Integer>();
    faultLink = new ArrayList<Boolean>();
    for(Integer i: arrays) adjacent.add(i);
    faultLink.addAll(blist);
  }
  void setFaultLink(int i,Boolean b) {
    faultLink.set(i, b);
  }
  ArrayList<Boolean> getFaultLinks(){
    return faultLink;
  }
  Boolean getFaultLink(int i) {
    return faultLink.get(i);
  }
  ArrayList<Integer> getAdjacents(){
    return adjacent;
  }
  Integer getAdjacent(int i) {
    return adjacent.get(i);
  }


}
