import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class DocIndex implements Runnable {
    protected DbConnect db;
    int urlDbIndex=30944;
    int countIndexed=0;
    int countWaits=0;

    public DocIndex(DbConnect db) {

        this.db=db;
    }

    public void run()
    {
        while (true)
        {
            try {
                if(urlDbIndex==-1)
                    urlDbIndex=db.getfirstURL_inURL();

                if(urlDbIndex==-1 && countWaits<=15)
                {Thread.sleep(5000);
                    countWaits++;return;}
                else if(urlDbIndex==-1 || (countIndexed>7000))
                {System.exit(0);}

                String url="";int Enter=-1;int url_id=0;

                Enter=db.getEnter_inURL(urlDbIndex);
                System.out.println(Enter +" "+urlDbIndex);
                if(Enter==1)
                {
                    url = db.getURLByID_inURL(urlDbIndex);
                   // System.out.println(url);
                    int doc_id=db.findURL_inDOCURL(urlDbIndex);
                    //System.out.println(doc_id);
                    if(doc_id<=0)
                    {
                        Document doc= Jsoup.connect(url).get();
                        String title = doc.title();
                        title=title.replaceAll("[^a-zA-Z0-9]", " ");
                        title=title.replaceAll("\\s{2,}", " ").trim();

                        String h1 = doc.getElementsByTag("h1").text().toLowerCase();
                        h1=h1.replaceAll("[^a-zA-Z0-9]", " ");
                        h1=h1.replaceAll("\\s{2,}", " ").trim();

                        String h2 = doc.getElementsByTag("h2").text().toLowerCase();
                        h2=h2.replaceAll("[^a-zA-Z0-9]", " ");
                        h2=h2.replaceAll("\\s{2,}", " ").trim();

                        String h3 = doc.getElementsByTag("h3").text().toLowerCase();
                        h3=h3.replaceAll("[^a-zA-Z0-9]", " ");
                        h3=h3.replaceAll("\\s{2,}", " ").trim();

                        String h4 = doc.getElementsByTag("h4").text().toLowerCase();
                        h4=h4.replaceAll("[^a-zA-Z0-9]", " ");
                        h4=h4.replaceAll("\\s{2,}", " ").trim();

                        String h5 = doc.getElementsByTag("h5").text().toLowerCase();
                        h5=h5.replaceAll("[^a-zA-Z0-9]", " ");
                        h5=h5.replaceAll("\\s{2,}", " ").trim();

                        String h6 = doc.getElementsByTag("h6").text().toLowerCase();
                        h6=h6.replaceAll("[^a-zA-Z0-9]", " ");
                        h6=h6.replaceAll("\\s{2,}", " ").trim();

                        String p = doc.getElementsByTag("p").text().toLowerCase();
                        p=p.replaceAll("[^a-zA-Z0-9]", " ");
                        p=p.replaceAll("\\s{2,}", " ").trim();

                        //replace
                        String body = doc.getElementsByTag("body").text().toLowerCase();
                        body=body.replaceAll("[^a-zA-Z0-9]", " ");
                        body=body.replaceAll("\\s{2,}", " ").trim();

//                        String li = doc.getElementsByTag("li").text().toLowerCase();
//                        li=li.replaceAll("[^a-zA-Z0-9]", " ");
//                        li=li.replaceAll("\\s{2,}", " ").trim();

                        title+="=|="+h1+"."+h2+"."+h3+"."+h4+"."+h5+"."+h6+"."+p+"."+body;
//                        if(title.contains("=|=......."))
//                        {
//                             title.replace("=|=.......","");
//                            String body = doc.getElementsByTag("body").text().toLowerCase();
//                            body=body.replaceAll("[^a-zA-Z0-9]", " ");
//                            title+=body.replaceAll("\\s{2,}", " ").trim();
//                        }
                       // System.out.println(title);
                        doc_id=db.findDoc_inDocs(title);

                        if(doc_id<=0)
                            doc_id=db.addDocument_ToDoc(title);
                        else {
                            db.enterFALSE_inURL(urlDbIndex);
                            System.out.println("Changed Enter to false");
                        }


                    }else
                    {
                        db.enterFALSE_inURL(urlDbIndex);
                        System.out.println("Changed Enter to false");
                    }
                    System.out.println("Linked URL= "+urlDbIndex+" To Doc= "+doc_id);
                    db.addInDoc_URL(urlDbIndex,doc_id);

                }
                urlDbIndex++;



            } catch (Exception e) {
                System.out.println(e);
               // db.enterFALSE_inURL(urlDbIndex);
            }
        }
    }


    


}
