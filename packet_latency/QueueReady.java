package controller5_debug1;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


/**
 * キューを一括管理したい
 * @author admin2
 *
 */
public class QueueReady{
  BlockingQueue queue = new ArrayBlockingQueue(8);
  boolean ready = false;


}