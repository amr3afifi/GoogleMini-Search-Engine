public class Indexer {

protected DbConnect db;
int threadCounter=0;


public Indexer (DbConnect db)
{
    this.db=db;
//    db.emptyCombinedTable();
//    db.emptyImagesTable();
//    db.emptyWordsTable();
//    db.setIndexedToFalse();
}

    public void addThread()
    {
        IndexerThread indexerThread=new IndexerThread(this);
        threadCounter++;
        String threadname="thread"+Integer.toString(threadCounter);
        Thread t=new Thread(indexerThread,threadname);
        System.out.println(t);
        t.start();
    }

    public void addThread(int n)
    {
        for (int i=0;i<n;i++)
            addThread();
    }

}
