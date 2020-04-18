import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.html.HTMLDocument;

import javax.swing.text.html.HTML;
import java.io.IOException;
import java.util.HashSet;

public class CrawlerThread extends Thread  implements Runnable {

    private Crawler mycrawler;
    private Document document;
    public String URL;

    public CrawlerThread (Crawler mycrawler,String URL) {
        this.mycrawler = mycrawler;
        this.URL = URL;
    }

    public void run()
    {
        while (true) {

                repeatForEachPage(URL);
        }
    }

    public void repeatForEachPage(String URL)
    {
        synchronized (mycrawler) {
            while(mycrawler.getCount()>=mycrawler.getMaxCount())
            {
                try {
                    mycrawler.wait();
                } catch (InterruptedException e) {
                    System.out.println (Thread.currentThread().getName() + " is awaken");
                }
            }

            try {
                document = Jsoup.connect(URL).get();



//               if (document instanceof HTMLDocument)
//                {

                   if(mycrawler.addToVisitedLinks(URL))
                   {
                       System.out.println (Thread.currentThread().getName() + " Added a new link -> " +URL);
                       Elements metaTags= document.getElementsByTag("meta");
                       int robotDisallowCrawling=0;
                       for (Element metaTag:metaTags)
                       {
                           String content = metaTag.attr("content");

                           if(content.contains("NOFOLLOW") || content.contains("nofollow"))
                               robotDisallowCrawling++;

                       }

                       if(robotDisallowCrawling==0)
                       {
                           Elements linksOnPage = document.select("a[href]");

                           //For each link found on page go to add in visited links hashset
                           for (Element page : linksOnPage) {

                               repeatForEachPage(page.attr("abs:href"));
                           }
                       }
                   }
               // }

            }catch (IOException e) {
                System.err.println("For '" + URL + "': " + e.getMessage()+" or HTML");
            }

            mycrawler.notifyAll();

        }
    }
}
