import org.jsoup.nodes.Document;

import java.util.HashSet;

public class Crawler {

    private int URLsCount=0;
    private Indexer myindexer;
    private final int maxCount=5000;
    private HashSet<String> visitedLinks;
    public Crawler(Indexer myindexer) {
        visitedLinks = new HashSet<String>();
        this.myindexer = myindexer;
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

    public boolean addToVisitedLinks(Document document,String URL)
    {

        if(!isVisited(URL))
        {

            visitedLinks.add(URL);
            URLsCount++;
            myindexer.addDoc(document);
            return true;

        }
        return false;
    }

}




