import core.CounterTask;

public class PlayBook2 {

    public static void main(String[] argv) {
//        Professor aProfessor = new Professor("Muller",
//                "Merkel",
//                "M");
//        System.out.println("MainThread : "+Thread.currentThread().getName());
//        Professor aProfessor2 = new Professor("Moise",
//                "Dessalines",
//                "M");
//
//       Thread a = new Thread(aProfessor);
//       a.start();
//       new Thread(aProfessor2).start();
//        System.out.println("MainThread ending: "+Thread.currentThread().getName());

        CounterTask ct1 = new CounterTask(50);
        CounterTask ct2 = new CounterTask(100);
        Thread a = new Thread(ct1);
        Thread b = new Thread(ct2);

        a.start();
        b.start();

        try {
            b.join();
            a.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("the sum of [1, 100]is :" + CounterTask.sum.toString());
    }
}
