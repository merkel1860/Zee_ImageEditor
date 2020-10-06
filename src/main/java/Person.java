public class Person {
    private String lname;
    private String fname;
    private String gender;

    public Person(String lname, String fname, String gender) {
        this.lname = lname;
        this.fname = fname;
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "Person{" +
                "lname='" + lname + '\'' +
                ", fname='" + fname + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}
