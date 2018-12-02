package parallelism;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConcurentBlockRunner {

    private final MyLock lock = new MyLock();

    private final AbstractQueue<Production> list = new ConcurrentLinkedQueue<>();

    //starts all threads
    public void startAll() {
        Iterator<Production> iter = list.iterator();
        while (iter.hasNext()) {
            Production p = iter.next();
            runOne(p);
        }
        wakeAll();
        iter = list.iterator();
        while (iter.hasNext()) {
            try {
                Production p = iter.next();
                p.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(ConcurentBlockRunner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        list.clear();
    }

    //adds a thread to poll
    public void addThread(Production _pThread) {
        list.add(_pThread);
    }

    void runOne(Production _pOne) {
        _pOne.injectRefs(lock);
        _pOne.start();
    }

    void wakeAll() {
        lock.unlock();
    }
}
