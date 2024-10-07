public class Compt {
    public int[] t;
    public int tempo;

    public Compt(int size) {
        t = new int[size];
        for (int i = 0; i < size; i++) {
            t[i] = i;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Compt state : ");
        for (int value : t) {
            sb.append(value).append(" ");
        }
        return sb.toString();
    }
}