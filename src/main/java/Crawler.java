import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class Crawler {

    private int URLsCount=0;
    private DbConnect db;
    private final int maxCount=8000;
    private int threadCounter=0;
    int threadsFoundEmpty=0;
    boolean recrawl=false;
    Vector<String> crawlerStartSeed;
    Vector<String> robotsDelay;

    public Crawler(DbConnect db) {
        this.db = db;
        crawlerStartSeed=new Vector();
        robotsDelay=new Vector<>();

        String[] websites = {
                    "https://www.youtube.com/", "https://www.facebook.com/", "https://www.twitter.com/", "https://www.google.com/", "https://www.linkedin.com/", "https://www.instagram.com/",
                    "https://www.ebay.com/", "https://www.stackoverflow.com/", "https://www.bodybuilding.com/", "https://www.bose.com/","https://talksport.com/",
               // "https://www.tesla.com/",
                     "https://www.apple.com/", "https://www.wikipedia.org/", "https://www.forbes.com/", "https://www.netflix.com/","https://nytimes.com/", "https://www.reuters.com/",
                    "https://www.reddit.com/", "https://www.fifa.com/", "https://www.gsmarena.com/", "https://www.amazon.com/", "https://www.bbc.com/","https://www.aliexpress.com/",
                    "https://www.cnn.com/", "https://www.9gag.com/", "https://www.trivago.com/", "https://www.who.int/", "https://arxiv.org/", "https://www.springer.com/",
                     "https://www.goodreads.com/", "https://www.tripadvisor.com/",
                    "https://www.udemy.com/", "https://www.coursera.org/", "https://www.dictionary.com/", "https://www.espn.com/", "https://www.britannica.com/", "https://www.npr.org/",
                    "https://www.craigslist.org/", "https://www.wired.com/", "https://www.rockarchive.com/", "https://www.businessinsider.com/", "https://www.imdb.com/", "https://www.nature.com/",
                    "https://www.merriam-webster.com/", "https://www.ted.com/", "https://www.washingtonpost.com/", "https://www.time.com/", "https://www.economist.com/", "https://www.olympic.org/",
                    "https://www.self.com/", "https://www.insider.com/", "https://www.ieee.org/", "https://www.upwork.com/", "https://www.github.com/","https://www.skysports.com/"
        };

        ResultSet rs=db.getURLSToResume();

        if(rs!=null)
        {

            try {
                while (rs.next())
                {
                    String url=new String();
                    url = rs.getString("url");
                    crawlerStartSeed.add(url);
                }
            } catch (SQLException ex) { }
        }

        for(int i=0;i<websites.length;i++)
        {
            String temp=new String();
            temp=websites[i];
            crawlerStartSeed.add(temp);
        }
    }

    public int getCount () {
        return URLsCount;
    }

    public final int getMaxCount() {
        return maxCount;
    }

    public boolean isVisited(String URL)
    {
        int id=db.findURL_inURL(URL);
        if (id>0) {
            //System.out.println("visited");
            db.updateIngoing_inURL(id);
            return true;
        }
        else {

            //System.out.println("not visited");
            return false;
        }
    }

    public void enterTRUE(String URL)
    {
        int id=db.findURL_inURL(URL);

        if (id>0)
            db.enterTRUE_inURL(id);
    }

    public void enterFALSE(String URL)
    {
        int id=db.findURL_inURL(URL);

        if (id>0)
            db.enterFALSE_inURL(id);
    }

    public void resumeTRUE(String URL)
    {
        int id=db.findURL_inURL(URL);

        if (id>0)
            db.resumeTRUE_inURL(id);
    }

    public void resumeFALSE(String URL)
    {
        int id=db.findURL_inURL(URL);
        if (id>0)
            db.resumeFALSE_inURL(id);
    }

    public boolean continueMaxCount(String URL)
    {
        int id=db.findURL_inURL(URL);
        if(id<=0) return true;
        int count=0;

        if (id>0)
            count=db.getMaxCount_inURL(id);


        if(count<300 && count>=0)
        {
            incrementMaxCount(id);
            return true;
        }
        else
            return false;

    }

    public void incrementMaxCount(int id)
    {
        db.updateMaxCount_inURLS(id);
    }

    public boolean addToVisitedLinks(String URL,long date,String country)
    {
        if(URL=="" || URL==null)return false;
        if(!isVisited(URL))
        {
            URLsCount++;
            db.addURL_toURL(URL,date,country);
            resumeTRUE(URL);
            return true;

        }
        return false;
    }

    public void addThread()
    {
        NewCrawlerThread crawlerThread=new NewCrawlerThread(this);
        threadCounter++;
        String threadname="thread"+Integer.toString(threadCounter);
        Thread t=new Thread(crawlerThread,threadname);
        System.out.println(t);
        t.start();


    }

    public boolean updateOutgoingLinks(String URL,int num)
    {
        int id=db.findURL_inURL(URL);
        if(id<=0)
            return false;

        db.updateOutgoing_inURL(id,num);
        return true;


    }

    public void addThread(int n)
    {
        for (int i=0;i<n;i++)
            addThread();
    }

    public void addToVector(String surl)
    {
       // crawlSite e=new crawlSite();
        //e.url=surl;
        crawlerStartSeed.add(surl);
    }

    public void removeMainsiteFromVector(String mainsite)
    {
        crawlerStartSeed.removeIf(url ->url.contains(mainsite));
    }

    public void recrawling()
    {
        ResultSet rs=db.getAllUrls();
        crawlerStartSeed=new Vector<>();
        if(rs!=null)
        {
            try {
                while (rs.next())
                {
                    String cs = new String();
                    cs= rs.getString("url");
                    crawlerStartSeed.add(cs);
                }
            } catch (SQLException ex) { }
        }
    }

    public boolean threadEmpty()
    {
        threadsFoundEmpty++;
        if(threadsFoundEmpty>=threadCounter)
        {
            recrawling();
            recrawl=true;
            return true;
        }else
        return false;
    }

}