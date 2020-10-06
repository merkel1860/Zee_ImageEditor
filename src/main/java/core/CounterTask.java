package core;

public class CounterTask implements Runnable{
    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    private Integer range;
    private Integer halfSum = 0;
    // shared resource
    public static volatile Integer sum  = 0;

    public CounterTask(Integer range) {
        this.range = range;
    }

    @Override
    public void run() {

            Integer lowerSide = range <= 50 ? 1 : 51;
            for (int i = lowerSide; i <= range; i++) {
                halfSum +=i;
            }
            synchronized (sum) {
                sum += halfSum;
            }
        System.out.println("Half of sum ["+ lowerSide+ ","+ range+ "]: "+
                halfSum);
    }
}
