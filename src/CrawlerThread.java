import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class CrawlerThread extends Thread  implements Runnable {

    private Crawler mycrawler;
    public boolean isVisited;
    public String URLunshortened,URLnormalized,mainsite,extension;

    public CrawlerThread (Crawler mycrawler) {
        this.mycrawler = mycrawler;
    }

    public void run()
    {
        while (true) {

            try {

                repeatForEachPage("");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }


    public void repeatForEachPage(String link) throws MalformedURLException
    {
        String URLraw=new String();
        boolean isVisited=false;
        Document document=new Document(null);
        String URLnormalized;

        if(link==null || link=="") {
            synchronized (mycrawler) {
                if (mycrawler.getCount() >= mycrawler.getMaxCount()) {
                    try {
                        mycrawler.wait();
                    } catch (InterruptedException e) {
                    }
                    ;
                } else {
                    int c = 0;
                    for (int i = 0; i < mycrawler.crawlerStartSeed.size(); i++) {
                        if (mycrawler.crawlerStartSeed.get(i).threadEntered == false) {
                            URLraw = mycrawler.crawlerStartSeed.get(i).url;
                            mycrawler.crawlerStartSeed.get(i).threadEntered = true;
                            isVisited = mycrawler.isVisited(URLraw);
                            URLraw = URLraw.toLowerCase();
                            c++;
                            break;
                        } else c++;

                    }
                    //kolohom false w yebda2 ye3eed ml awel
                    if (c > 0 && c == mycrawler.crawlerStartSeed.size()) {
                        System.out.println("Crawler: Finished seed and started allover again");
                        for (int i = 0; i < mycrawler.crawlerStartSeed.size(); i++) {
                            mycrawler.crawlerStartSeed.get(i).threadEntered = false;
                        }
                    }

                }
            }
        }else URLraw=link;

        URLraw=URLraw.toLowerCase();
        synchronized (mycrawler) {
            URLnormalized = mycrawler.normalize(URLraw);
        }

        try
        {
            if(URLraw!=null) {
                document = Jsoup.connect(URLnormalized).get();

                if (!isVisited) {
                    int outgoingLinks = 0;
                    System.out.println(Thread.currentThread().getName() + " Added a new link -> " + URLnormalized);

                    synchronized (mycrawler) {
                        mycrawler.addToVisitedLinks(URLnormalized);
                    }

                    if (!readRobotsText(document,URLnormalized))
                    {
                        Elements linksOnPage = document.select("a[href]");

                        //For each link found on page go to add in visited links hashset
                        synchronized (mycrawler) {
                            for (Element page : linksOnPage)
                            {
                                String el=page.attr("abs:href");
                                if (!mycrawler.isVisited(el))
                                {
                                    mycrawler.addToVisitedLinks(el);
                                }
                                outgoingLinks++;
                            }
                            mycrawler.updateOutgoingLinks(URLnormalized, outgoingLinks);
                        }
                        for (Element page : linksOnPage)
                        {
                            repeatForEachPage(page.attr("abs:href"));
                        }

                    }

                }
            }
        }catch (IOException e)
        { System.err.println("For '" + URLnormalized + "': " + e.getMessage()+" or HTML"); }


    }


    public void getSitePlusExtension(String URLraw)
    {
        int com=-1;
        System.out.println(URLraw);
        com=URLraw.indexOf(".com");
        if (com !=-1)
        {
            mainsite= URLraw.substring(0 , com+4); //this will give abc
            if(URLraw.length()>com+4)
                extension=URLraw.substring(com+5 , URLraw.length());
            else
                extension="";

        }
        com=-1;
        com=URLraw.indexOf(".net");
        if (com !=-1)
        {
            mainsite= URLraw.substring(0 , com+4); //this will give abc
            if(URLraw.length()>com+4)
                extension=URLraw.substring(com+5 , URLraw.length());
            else
                extension="";

        }
        com=-1;
        com=URLraw.indexOf(".org");
        if (com !=-1)
        {
            mainsite= URLraw.substring(0 , com+4); //this will give abc
            if(URLraw.length()>com+4)
                extension=URLraw.substring(com+5 , URLraw.length());
            else
                extension="";

        }
    }

    public boolean readRobotsText(Document document,String URLraw)
    {
        int robotDisallowCrawling=0;

        //Robot txt file
        getSitePlusExtension(URLraw);
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
