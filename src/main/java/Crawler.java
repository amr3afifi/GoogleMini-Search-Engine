import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class Crawler {

    private int URLsCount=0;
    private DbConnect db;
    private final int maxCount=5000;
    private int threadCounter=0;
    Vector<crawlSite> crawlerStartSeed;

    public class crawlSite
    {
        public String url="";
        public boolean threadEntered=false;
    }

    public Crawler(DbConnect db) {
        this.db = db;
        crawlerStartSeed=new Vector();

        String[] websites = {
                "https://www.youtube.com/", "https://www.facebook.com/", "https://www.twitter.com/", "https://www.google.com/", "https://www.linkedin.com/", "https://www.instagram.com/",
                "https://www.amazon.com/", "https://www.ebay.com/", "https://www.gsmarena.com/", "https://www.stackoverflow.com/", "https://www.365scores.com/", "https://www.bodybuilding.com/",
                 "https://www.bose.com/", "https://www.tesla.com/", "https://www.apple.com/", "https://www.wikipedia.org/", "https://www.nytimes.com/", "https://www.forbes.com/",
                "https://www.netflix.com/"
        };

        ResultSet rs=db.getURLSToResume();

        if(rs!=null)
        {
            crawlSite cs;
            try {
                while (rs.next())
                {
                    cs = new crawlSite();
                    cs.threadEntered = false;
                    cs.url = rs.getString("url");
                    crawlerStartSeed.add(cs);
                }
            } catch (SQLException ex) { }
        }

        for(int i=0;i<websites.length;i++)
        {
            crawlSite temp=new crawlSite();
            temp.url=websites[i];
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
            System.out.println("visited");
            db.updateIngoing_inURL(id);
            return true;
        }
        else {

            System.out.println("not visited");
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


        if(count<100 && count>=0)
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

    public boolean addToVisitedLinks(String URL)
    {
        if(URL=="" || URL==null)return false;
        if(!isVisited(URL))
        {
            URLsCount++;
            db.addURL_toURL(URL);
            resumeTRUE(URL);
            return true;

        }
        return false;
    }

    public void addThread()
    {
        CrawlerThread crawlerThread=new CrawlerThread(this);
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

}