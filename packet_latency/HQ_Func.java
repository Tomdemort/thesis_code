package controller5_debug1;

public class HQ_Func{

  static int[] adjacentAll(int id, int m) {
   int[] adjacents = new int[m];
   for (int i = 0;i<adjacents.length;i++) {
     adjacents[i] = id ^ (1 << i);
   }
   return adjacents;
  }
  static int adjacent(int id,int m) {
    return id ^ (1 << (m-1));
  }

  static int nextRoute(int sid,int did) {
    int c = sid ^ did;
    int count = 0;
    while(c != 0) {
      if(c % 2 == 1) return count;
      else {
        c = c >> 1;
        count++;
      }
    }
    return -1; //0だといろいろとまずい
  }

  public static void main(String[] args) {
   int id = 1;
   int m = 10;
   int[] adjacents = adjacentAll(id,m);
   System.out.println(adjacent(id,m));
   for (int adjacent: adjacents) {
     System.out.println(adjacent);
   }
  }

}