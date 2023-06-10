import com.google.gson.Gson;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private final Map<String, List<PageEntry>> initData = new HashMap<>();
    private final Set<String> stopWords = new HashSet<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {

        for (File file : pdfsDir.listFiles()) {
            if (file.isFile()) {
                PdfDocument doc = new PdfDocument(new PdfReader(file));

                for (int i = 1; i <= doc.getNumberOfPages(); i++) {

                    String textFromPage = PdfTextExtractor.getTextFromPage(doc.getPage(i));
                    List<String> wordsFromPage = List.of(textFromPage.split("\\P{IsAlphabetic}+"));

                    Map<String, Integer> freqs = new HashMap<>();
                    for (String word : wordsFromPage)
                        freqs.put(word.toLowerCase(), freqs.getOrDefault(word, 0) + 1);


                    for (Map.Entry<String, Integer> entry : freqs.entrySet()) {
                        PageEntry pageEntry = new PageEntry(file.getName(), i, entry.getValue());
                        initData.computeIfAbsent(entry.getKey(), v -> new ArrayList<>()).add(pageEntry);
                    }
                }
            }
        }

        for (Map.Entry<String, List<PageEntry>> entry : initData.entrySet())
            Collections.sort(entry.getValue());

        String text;
        FileInputStream inputStream = new FileInputStream("stop-ru.txt");
        try {
            text = IOUtils.toString(inputStream);
        } finally {
            inputStream.close();
        }
        String[] words = text.split("\\P{IsAlphabetic}+");
        stopWords.addAll(Arrays.asList(words));

    }

    @Override
    public String search(String word) {
        return new Gson().toJson(initData.get(word.toLowerCase()));
    }

    public String searchLine(String line) {
        List<String> words = new ArrayList<>(List.of(line.toLowerCase().split("\\P{IsAlphabetic}+")));
        words.removeIf(stopWords::contains);

        List<PageEntry> pages = new ArrayList<>();
        for (Map.Entry<String, List<PageEntry>> entry : initData.entrySet())
            if (words.contains(entry.getKey()))
                for (PageEntry pageEntry : entry.getValue()) pages.add(pageEntry);

        List<PageEntry> result = new ArrayList<>();
        for (PageEntry pageEntry : pages) {
            PageEntry temp = new PageEntry(pageEntry.getPdfName(), pageEntry.getPage(), pageEntry.getCount());
            for (PageEntry page : pages)
                if (temp.getPdfName().equals(page.getPdfName())
                        & temp.getPage() == page.getPage()) temp.setCount(temp.getCount() + page.getCount());
            if (!result.contains(temp)) result.add(temp);
        }
        Collections.sort(result);
        return new Gson().toJson(result);

    }

}
