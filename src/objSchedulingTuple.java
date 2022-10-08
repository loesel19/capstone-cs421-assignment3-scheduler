import java.util.ArrayList;

public class objSchedulingTuple {
    private ArrayList<objReport> courses;
    private ArrayList<Integer> seenTUIDS;

    public objSchedulingTuple(ArrayList<objReport> courses, ArrayList<Integer> seenTUIDS) {
        this.courses = courses;
        this.seenTUIDS = seenTUIDS;
    }

    public ArrayList<objReport> getCourses() {
        return courses;
    }

    public ArrayList<Integer> getSeenTUIDS() {
        return seenTUIDS;
    }
}

