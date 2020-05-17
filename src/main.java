public class main {



    public static void main(String[] args)
    {
        System.out.println("Hello");
        DbConnect db=new DbConnect();
        Indexer indexer=new Indexer(db);
        Crawler crawler=new Crawler(db);
        //db.emptyDatabse();
       // crawler.addThread(3);
        indexer.run();

//        int h1=db.addWord_toWord("hello");
//        int h2=db.addWord_toWord("hello2");
//        int h3=db.addWord_toWord("hello3");
//        int u1=db.addURL_toURL("www.hello.com");
//        int u2=db.addURL_toURL("www.hello.com");
//        int u3=db.addURL_toURL("www.hello.com");
//        db.addInCombined(u1,h1,0,1);
//        db.addInCombined(u2,h2,0,1);
//        db.addInCombined(u3,h3,0,1);
//        db.addInCombined(u3,h2,0,1);
//        db.addInCombined(u3,h1,0,1);

//        QueryProcessor queryProcessor=new QueryProcessor();
//        queryProcessor.searchDatabase("hello");
//        queryProcessor.printVector();

        //indexer.parseDoc();



    }
}
