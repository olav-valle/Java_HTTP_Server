package HTTPServer;

public class TestMultithread {

    static final int NUM_THREADS = 1;
    public static void main(String[] args) {
        for (int i = 0; i < NUM_THREADS; i++){
            Thread t = new Thread(new ServerTestClient());
            t.start();
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
