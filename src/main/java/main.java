import java.io.IOException;

public class main {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("بسم الله الرحمن الرحيم, ادخل برجلك اليمين");
        DbConnect db=new DbConnect();
        Indexer indexer=new Indexer(db);
        Crawler crawler=new Crawler(db);
        DocIndex docIndex=new DocIndex(db);


    //Steps to run 1.empty database .. 2. run crawler & comment others and close program .. 3. run indexer & comment others and close program then use the web interface

    //To empty the Database     --make sure to comment the next line when running the indexer
       // db.emptyDatabse();

    //1. To run the crawler
    //Add N number of threads to the crawler and run them automatically
       // crawler.addThread(25);
        //Thread.sleep(60000);
        //docIndex.run();


    //2.//To run the indexer
        //indexer.run();
        indexer.addThread(5);


       // QueryProcessor queryProcessor=new QueryProcessor();
//        queryProcessor.searchDatabase("let");
//       // queryProcessor.ranker();
//        queryProcessor.printVector();
       // queryProcessor.getNamesFromTrends("my name is Amr Afifi Ali and he is my Abdo Mota");


//        Document doc= Jsoup.connect("https://www.nytimes.com/").get();
//        Boolean check=false;
//        String description="";
//        int index=-1;
//        String docSmall=doc.toString().toLowerCase();
//        index=docSmall.lastIndexOf("peaceful");
//
//        if(index!=-1)
//        {
//            int lastCol = docSmall.indexOf("\"", index);
//            int lastDot = docSmall.indexOf(".", index);
//            int lastStop=Math.min(lastCol,lastDot);
//
//            String firstSub=docSmall.substring(index-100,lastStop);
//            int firstCol = firstSub.lastIndexOf("\"");
//            int firstDot = firstSub.lastIndexOf(".");
//            int firstStop= Math.max(firstCol,firstDot);
//            description=firstSub.substring(firstStop+1,firstSub.length());
//        }
//
//        System.out.println(description);

//        Document doc = Jsoup.connect("https://www.springer.com/").get();
//        String h1 = doc.getElementsByTag("body").text();
//        String title = doc.title();
//        System.out.println("Title= "+title+"| Body= "+h1);






    }
}
