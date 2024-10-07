// Compt.java
public class Compt {
    public int[] t;

    public Compt(int size) {
        t = new int[size];
        for (int i = 0; i < size; i++) {
            t[i] = i;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Compt state: ");
        for (int value : t) {
            if (value != -1) {
                sb.append(value).append(" ");
            }
        }
        return sb.toString();
    }
} 
