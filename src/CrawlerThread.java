import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class CrawlerThread extends Thread  implements Runnable {

    private Crawler mycrawler;
    private Document document;
    public String URLraw;
    public String URLunshortened,URLnormalized,mainsite,extension;

    public CrawlerThread (Crawler mycrawler,String URLraw) {
        this.mycrawler = mycrawler;
        this.URLraw = URLraw;
    }

    public void run()
    {
        while (true) {

                repeatForEachPage(URLraw);
        }
    }

    public void repeatForEachPage(String URLraw)
    {
//        try
//        {
            URLraw=URLraw.toLowerCase();
//            URLunshortened= UrlCleaner.unshortenUrl(URLraw);
//            URLnormalized= UrlCleaner.normalizeUrl(URLunshortened);
//
//        }
//        catch (IOException e)
//        {
//            System.err.println( e.getMessage());
//        }


        synchronized (mycrawler)
        {
            while(mycrawler.getCount()>=mycrawler.getMaxCount())
            {
                try {
                    mycrawler.wait();
                } catch (InterruptedException e) {
                    System.out.println (Thread.currentThread().getName() + " is awaken");
                }
            }

            try
            {
                document = Jsoup.connect(URLraw).get();

                   if(!mycrawler.isVisited(URLraw))
                   {
                       System.out.println (Thread.currentThread().getName() + " Added a new link -> " +URLraw);
                        mycrawler.addToVisitedLinks(document,URLraw);
                       if(!readRobotsText())
                       {
                           Elements linksOnPage = document.select("a[href]");

                           //For each link found on page go to add in visited links hashset
                           for (Element page : linksOnPage)
                           {
                               repeatForEachPage(page.attr("abs:href"));
                           }
                       }
                   }
            }catch (IOException e)
            {
                System.err.println("For '" + URLraw + "': " + e.getMessage()+" or HTML");
            }

            mycrawler.notifyAll();
        }
    }

    public void getSitePlusExtension()
    {
        int com=-1;
        com=URLraw.indexOf("com");
        if (com !=-1)
        {
            mainsite= URLraw.substring(0 , com+3); //this will give abc
            extension=URLraw.substring(com+4 , URLraw.length());
        }
    }

    public boolean readRobotsText()
    {
        int robotDisallowCrawling=0;

        //Robot txt file
        getSitePlusExtension();
        try(BufferedReader in = new BufferedReader(
                new InputStreamReader(new URL(mainsite+"/robots.txt").openStream())))
        {
            String line = null;
            while((line = in.readLine()) != null)
            {
                if(line=="Disallow: /"+extension)
                    robotDisallowCrawling++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Robot metadata
        Elements metaTags= document.getElementsByTag("meta");

        for (Element metaTag:metaTags)
        {
            String content = metaTag.attr("content");
            if(content.contains("NOFOLLOW") || content.contains("nofollow"))
                robotDisallowCrawling++;
        }

        if(robotDisallowCrawling>0)
            return true;
        else
            return false;
    }

}
