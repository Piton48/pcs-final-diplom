import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws Exception {

        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs\\"));
        try (ServerSocket serverSocket = new ServerSocket(8989)) {
            while (true) {
                try (Socket socket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream())) {

                    out.println("Введите запрос");
                    out.flush();
                    String word = in.readLine();
                    if (isWord(word)) {
                        out.println(engine.search(word));
                    } else out.println(engine.searchLine(word));

                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }

    private static boolean isWord(String word){
        String[] words = word.split("\\P{IsAlphabetic}+");
        if (words.length == 1) return true;
        return false;
    }

}