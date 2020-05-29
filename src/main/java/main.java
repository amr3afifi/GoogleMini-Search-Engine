public class main {

    public static void main(String[] args)
    {
        System.out.println("بسم الله الرحمن الرحيم, ادخل برجلك اليمين");
        DbConnect db=new DbConnect();
        Indexer indexer=new Indexer(db);
        Crawler crawler=new Crawler(db);

    //Steps to run 1.empty database .. 2. run crawler & comment others and close program .. 3. run indexer & comment others and close program then use the web interface

    //To empty the Database     --make sure to comment the next line when running the indexer
        //db.emptyDatabse();

    //1. To run the crawler
    //Add N number of threads to the crawler and run them automatically
        //crawler.addThread(5);

    //2.//To run the indexer
        //indexer.run();
        //indexer.addThread(3);


        QueryProcessor queryProcessor=new QueryProcessor();
//        queryProcessor.searchDatabase("let");
//       // queryProcessor.ranker();
//        queryProcessor.printVector();
        queryProcessor.getNamesFromTrends("my name is Amr Afifi Ali and he is my Abdo Mota");




    }
}
