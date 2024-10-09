//暂时没用处，之后可以用作共享资源或者排行榜，可以追踪连接到服务器的客户数量
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
