import org.jsoup.nodes.Document;

import  java.util.*;

public class Indexer {

private DbConnect connect;
private Vector<Document> docsQueue;

public Indexer()
{
    docsQueue=new Vector();
    connect=new DbConnect();
}
    public void addDoc(Document document)
    {
        docsQueue.add(document);
    }


}
