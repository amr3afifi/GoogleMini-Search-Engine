import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Vector;


public class Indexer implements  Runnable {

private DbConnect db;
private Stemmer stemmer;
int urlDbIndex;


public Indexer (DbConnect db)
{
    this.db=db;
    stemmer=new Stemmer();
    this.urlDbIndex=1;
}

    public void run()
    {
        while(urlDbIndex<5000)
        {
            parseDoc();
        }
    }

    public String[] getHTMLTags(String url)
    {
        String Alltext[]=new String[]{};
        try{
        if(!url.isEmpty()) {
            Document doc = Jsoup.connect(url).get();
            //db.addURL_toURL(url, 0.0);
            //to get text in html code
            String title = doc.title();
            String h1 = doc.getElementsByTag("h1").text();
            String h2 = doc.getElementsByTag("h2").text();
            String h3 = doc.getElementsByTag("h3").text();
            String h4 = doc.getElementsByTag("h4").text();
            String h5 = doc.getElementsByTag("h5").text();
            String h6 = doc.getElementsByTag("h6").text();
            String p = doc.getElementsByTag("p").text();
            String li = doc.getElementsByTag("li").text();
            String th = doc.getElementsByTag("th").text();
            String td = doc.getElementsByTag("td").text();
            String span = doc.getElementsByTag("span").text();
            String div = doc.getElementsByTag("div").text();

            // Place all test in array:
             Alltext = new String[]{title, h1, h2, h3, h4, h5, h6, p, li, th, td};

        }} catch (Exception e) {
            System.out.println(e);
        }
        return Alltext;
    }

    public void parseDoc() {
        try {
           String url=db.getURLByID_inURL(urlDbIndex);
           int url_id=urlDbIndex;

               String Alltext[];
               Alltext=getHTMLTags(url);

               Vector<String> wordsInSameDoc = new Vector();
               String[] result;
               // SPLIT TEXT TO WORDS:
               for (int i = 0; i < Alltext.length; i++) {
                   //check if tag exists:
                   if (Alltext[i].isEmpty())
                       continue;

                   result = Alltext[i].split("[()+;$*=#, ?.:!\"]+");

                   for (int j = 0; j < result.length; j++) {

                       String LowerWord = result[j].toLowerCase();
                       boolean stoppingCheck = isStoppingWord(LowerWord);

                       if (stoppingCheck)
                           continue;

                       char[] stemArray = LowerWord.toCharArray();
                       stemmer.add(stemArray, LowerWord.length());
                       stemmer.stem();
                       String StemOutput = stemmer.toString();

                       int word_id = db.findWord_inWord(StemOutput);
                       int combined_id=db.findBoth_inCombined(word_id,url_id);

                       if(word_id<0)
                       {word_id=db.addWord_toWord(StemOutput);}
                       else
                       {db.updateWordCount_inWord(word_id);}

                       if(combined_id<0)
                       { combined_id=db.addInCombined(url_id,word_id,i,j); }
                       else
                       { db.updateCombined_numOfOccurences(combined_id); }

                       if (!wordsInSameDoc.contains(StemOutput)) {
                           wordsInSameDoc.add(StemOutput);
                           db.updateWord_numOfDocs(StemOutput);
                       }
                   }
               }
               urlDbIndex++;

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public boolean isStoppingWord(String input)
    {
        String[] stoppingWordArray= {"a", "about", "above", "after", "again", "against", "ain", "all", "am", "an", "and", "any", "are", "aren", "aren't", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can", "couldn", "couldn't", "d", "did", "didn", "didn't", "do", "does", "doesn", "doesn't", "doing", "don", "don't", "down", "during", "each", "few", "for", "from", "further", "had", "hadn", "hadn't", "has", "hasn", "hasn't", "have", "haven", "haven't", "having", "he", "her", "here", "hers", "herself", "him", "himself", "his", "how", "i", "if", "in", "into", "is", "isn", "isn't", "it", "it's", "its", "itself", "just", "ll", "m", "ma", "me", "mightn", "mightn't", "more", "most", "mustn", "mustn't", "my", "myself", "needn", "needn't", "no", "nor", "not", "now", "o", "of", "off", "on", "once", "only", "or", "other", "our", "ours", "ourselves", "out", "over", "own", "re", "s", "same", "shan", "shan't", "she", "she's", "should", "should've", "shouldn", "shouldn't", "so", "some", "such", "t", "than", "that", "that'll", "the", "their", "theirs", "them", "themselves", "then", "there", "these", "they", "this", "those", "through", "to", "too", "under", "until", "up", "ve", "very", "was", "wasn", "wasn't", "we", "were", "weren", "weren't", "what", "when", "where", "which", "while", "who", "whom", "why", "will", "with", "won", "won't", "wouldn", "wouldn't", "y", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves", "could", "he'd", "he'll", "he's", "here's", "how's", "i'd", "i'll", "i'm", "i've", "let's", "ought", "she'd", "she'll", "that's", "there's", "they'd", "they'll", "they're", "they've", "we'd", "we'll", "we're", "we've", "what's", "when's", "where's", "who's", "why's", "would"};
        int found=0;
        for(int i=0;i<stoppingWordArray.length;i++)
        {
            if(stoppingWordArray[i]==input)
                found++;
        }
        if(found>0)
            return true;
        else
            return false;
    }


}
