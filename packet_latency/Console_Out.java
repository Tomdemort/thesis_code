package controller5_debug1;

/**
 * 引数持ってくるのめんどいし使うかは未定
 * @author admin2
 *
 */
public class Console_Out{

  static void showArray(int[] a) {
    System.out.print("[");
    for(int i = 0;i<a.length;i++) {
      if(i == a.length - 1) {
        System.out.print(a[i] +" ]");
      }else {
        System.out.print(a[i] + ", ");
      }
    }
    System.out.println();
  }

  static void showArray(byte[] a) {
    System.out.print("[");
    for(int i = 0;i<a.length;i++) {
      if(i == a.length - 1) {
        System.out.print(a[i] +" ]");
      }else {
        System.out.print(a[i] + ", ");
      }
    }
    System.out.println();
  }
  public static void showArrayChar(int[] a) {
    StringBuilder sb = new StringBuilder();
    for(int i=0;i<a.length;i++) {
      sb.append(a[i]);
    }
    System.out.println(sb.toString());
  }


 static void showMessage(String s) {
   System.out.println(s);

 }


  public static void main(String[] args) {

  }
}