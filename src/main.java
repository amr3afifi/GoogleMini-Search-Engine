public class main {

    public static void main(String [] args)
    {
        System.out.println("Hello");

        String URL= "https://www.youtube.com/";
        Indexer myindexer=new Indexer();
        Crawler mycrawler=new Crawler(myindexer);


        CrawlerThread crawlerThread=new CrawlerThread(mycrawler,URL);
        Thread tcrawl1 =new Thread(crawlerThread,"tcrawl1");

        tcrawl1.start();

    }
}
