package controller5_debug1;

import java.util.ArrayList;

public class CyclePermutationNK extends CyclePermutation {
  ArrayList<Cycle> list;
  ArrayList<Integer> sReminder;
  ArrayList<Integer> dReminder; // 未使用のなにか

  static final long serialVersionUID = 2L;

  CyclePermutationNK() {
    super();
    list = new ArrayList<Cycle>();
    sReminder = new ArrayList<Integer>();
    dReminder = new ArrayList<Integer>();
  }

  Cycle getCycle(int i) {
    return list.get(i);
  }

  int getCycleSize(int i) {
    return list.get(i).size();
  }

  ArrayList<Cycle> getCycleList() {
    return list;
  }

  ArrayList<Integer> getSourceReminderList() {
    return sReminder;
  }

  ArrayList<Integer> getDestReminderList() {
    return dReminder;
  }

  void autoCreate(int sid, int did, int n, int k) {
    int[] sAddr = NK_Func.changeListAddr(sid, n, k);
    int[] dAddr = NK_Func.changeListAddr(did, n, k);
    autoCreate(sAddr, dAddr, n, k);
  }

  /**
   * nk対応版 現在reminder部分について考察中
   *
   * @param sAddr
   * @param dAddr
   * @param n
   * @param k
   */
  void autoCreate(int[] sAddr, int[] dAddr, int n, int k) {
    int[] cAddr = new int[n];
    // sのみ1、dのみ2、両方3,未使用0
    for (int i = 0; i < k; i++) {
      cAddr[sAddr[i] - 1] += 1;
      cAddr[dAddr[i] - 1] += 2;
    }

    // 1と２どうするんだ全部やってみるか？
    // 3は考えなくてよさそう
    // とりあえず一例として考えてみる
    //Console_Out.showArray(cAddr);

    // ここからSGと同じなので変更部分をよくよく見ていく
    int[] sAddr2 = new int[n];
    int[] dAddr2 = new int[n];
    System.arraycopy(sAddr, 0, sAddr2, 0, sAddr.length);
    System.arraycopy(dAddr, 0, dAddr2, 0, dAddr.length);
    // 1と２の要素を加える
    int p = k;
    int q = k;
    for (int i = 0; i < n; i++) {
      if (cAddr[i] == 2) { // sのみある
        sAddr2[p] = i + 1;
        sReminder.add(i + 1);
        p++;
      } else if (cAddr[i] == 1) { // dのみある
        dAddr2[q] = i + 1;
        q++;
        dReminder.add(i + 1);
      }
    }
    for (int i = 0; i < n; i++) { // 両方ともない
      if (cAddr[i] == 0) {
        sAddr2[p] = i + 1;
        dAddr2[q] = i + 1;
        sReminder.add(i + 1);
        dReminder.add(i + 1);
        p++;
        q++;
      }
    }
    //Console_Out.showArray(sAddr2);
    //Console_Out.showArray(dAddr2);

    int[] cNum = new int[sAddr2.length];
    for (int i = 0; i < cNum.length; i++)
      cNum[i] = -1;
    int counter = 0;
    while (true) {
      // -1成分がなくなるまで続ける
      boolean b = true;
      for (int c : cNum) {
        if (c == -1)
          b = false;
      }
      if (b)
        break;

      // int startNum = sAddr[0];
      int min = 0;
      for (int i = 0; i < cNum.length; i++) {
        if (cNum[i] == -1) {
          min = i;
          break;
        }
      }
      int num = dAddr2[min]; // ２回目はcNum[0]でない最小値
      cNum[min] = counter;// ２回目以降は＋１する
      Cycle c = new Cycle();
      c.add(sAddr2[min]);
      for (int i = 0; i < dAddr2.length; i++)
        if (sAddr2[min] == dAddr2[i])
          cNum[i] = counter;

      if (sAddr2[min] != dAddr2[min])
        c.add(dAddr2[min]);
      loop: while (true) {

        for (int i = 0; i < sAddr2.length; i++) {
          if (sAddr2[i] == num) {
            if (cNum[i] != -1)
              break loop;
            num = dAddr2[i];
            cNum[i] = counter;// ここも同様に＋１する
            c.add(dAddr2[i]);
          }

        }
      }
      list.add(c);
      counter++;
    }
    // Cycle c = new Cycle();
    // System.out.println(list);

  }

  public static void main(String[] args) {
    CyclePermutationNK cnk = new CyclePermutationNK();
    int[] sAddr = { 7, 1, 2, 6, 5 };
    int[] dAddr = { 1, 2, 3, 4, 5 };
    cnk.autoCreate(sAddr, dAddr, 7, 5);
  }

}
