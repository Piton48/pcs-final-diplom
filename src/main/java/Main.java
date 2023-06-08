import java.io.File;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        //BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
        //System.out.println(engine.search("бизнес"));

        // здесь создайте сервер, который отвечал бы на нужные запросы
        // слушать он должен порт 8989

        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs\\"));
        String word = "бизнес";
        System.out.println(engine.search(word));


    }
}