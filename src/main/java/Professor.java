public class Professor extends Person implements Runnable{

    public Professor(String fname, String lname, String gender) {
        super(fname, lname, gender);
    }

    @Override
    public String toString() {
        return super.toString();
    }


    @Override
    public void run(){
        System.out.println(this.toString());
        System.out.println("Hello :"+Thread.currentThread().getName());
    }
}
