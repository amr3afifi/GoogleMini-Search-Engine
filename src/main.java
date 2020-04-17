import java.util.*;

public class main {

    public static void main(String [] args)
    {
        System.out.print("Hello");
        // Vector URLs = new Vector();
        // URLs.add("https://www.youtube.com/");
        String URL= "https://www.youtube.com";
        Crawler mycrawler=new Crawler();
        Indexer myindexer=new Indexer();

        CrawlerThread crawlerThread=new CrawlerThread(mycrawler,URL);
        Thread tcrawl1 =new Thread(crawlerThread,"tcrawl1");



    }
}
