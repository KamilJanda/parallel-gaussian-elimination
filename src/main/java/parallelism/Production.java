package parallelism;

public abstract class Production extends Thread {

    private MyLock lock;

    public void injectRefs(MyLock lock) {
        this.lock = lock;
    }

    @Override
    public void run() {
        lock.lock();
    }
}