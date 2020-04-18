import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.html.HTMLDocument;

import javax.swing.text.html.HTML;
import java.io.IOException;
import java.util.HashSet;

public class Crawler {

    private int URLsCount=0;
    private final int maxCount=5000;
    public HashSet<String> visitedLinks;
    private Document document;
    public Crawler() {
        visitedLinks = new HashSet<String>();
    }

    public int getCount () {
        return URLsCount;
    }

    public final int getMaxCount() {
        return maxCount;
    }

    public boolean isVisited(String URL)
    {
        if (visitedLinks.contains(URL)) {

            System.out.println("visited");
            return true;
        }
        else {

            System.out.println("not");
            return false;
        }
    }

    public boolean addToVisitedLinks(String URL)
    {

        if(!isVisited(URL))
        {

            visitedLinks.add(URL);
            URLsCount++;
            return true;

        }
        return false;
    }

}




