import com.google.gson.Gson;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private Map<String, List<PageEntry>> initData = new HashMap<>();


    public BooleanSearchEngine(File pdfsDir) throws IOException {

        for (File file : pdfsDir.listFiles()) {
            if (file.isFile()) {
                PdfDocument doc = new PdfDocument(new PdfReader(file));

                for (int i = 1; i <= doc.getNumberOfPages(); i++) {

                    String textFromPage = PdfTextExtractor.getTextFromPage(doc.getPage(i));
                    List<String> wordsFromPage = List.of(textFromPage.split("\\P{IsAlphabetic}+"));

                    Map<String, Integer> freqs = new HashMap<>();
                    for (String word : wordsFromPage) {
                        freqs.put(word.toLowerCase(), freqs.getOrDefault(word, 0) + 1);
                    }

                    for (Map.Entry<String, Integer> entry : freqs.entrySet()) {
                        PageEntry pageEntry = new PageEntry(file.getName(), i, entry.getValue());
                        initData.computeIfAbsent(entry.getKey(), v -> new ArrayList<>()).add(pageEntry);
                    }
                }
            }
        }
        for (Map.Entry<String, List<PageEntry>> entry : initData.entrySet()) {
            Collections.sort(entry.getValue());
        }

    }

    @Override
    public String search(String word) {
        Gson gson = new Gson();
        return gson.toJson(initData.get(word.toLowerCase()));
    }

}
