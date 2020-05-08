

public class main {

    public static void main(String [] args)
    {
        System.out.println("Hello");
        DbConnect db=new DbConnect();
        String URL= "https://www.youtube.com/";
        //Indexer indexer=new Indexer(db);
        Crawler crawler=new Crawler(db);
        CrawlerThread crawlerThread=new CrawlerThread(crawler,URL);
        Thread tcrawl1 =new Thread(crawlerThread,"tcrawl1");
        tcrawl1.start();
  //      indexer.parseDoc();



    }
}
