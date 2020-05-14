import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class QueryProcessor {
    private Stemmer stemmer;
    private DbConnect db;
    public String searchBox;

    public class Result
    {
        int id=0;
        int word_id=0;
        int url_id=0;
        int i=0;
        int j=0;
        int num=0;
    }

    Vector<Result> results;
    Vector<Integer> distinctWords;
    Vector<Integer> distinctUrls;
    public QueryProcessor(DbConnect db)
    {
        this.stemmer=new Stemmer();
        this.db=db;
    }

    public String[] splitWords()
    {
        String[] words = searchBox.split(" ");
        return words;
    }

    public void searchDatabase()
    {
        results=new Vector<Result>();

        String[] words=splitWords();
        int word_id;
        for(int i=0;i<words.length;i++)
        {
           word_id=db.findWord_inWord(words[i]);
           ResultSet rs=db.getURLS_inCombined(word_id);
           Result result;
            int count = 0;

           try {
               while(rs.next())
               {
                   result=new Result();
                   count++;
                   result.id = rs.getInt("id");
                   result.word_id = rs.getInt("word_id");
                   result.url_id = rs.getInt("url_id");
                   result.i = rs.getInt("importance");
                   result.j = rs.getInt("importance_index");
                   result.num = rs.getInt("num_of_occurrences");
                   System.out.println("Id= "+result.id+" urlID= "+result.url_id+" wordID= "+result.word_id);
                   results.add(result);
               }
           }catch (SQLException ex)
           {

           }

        }

    }

    public void printVector()
    {
        for(int i=0;i<results.size();i++)
        {
            System.out.println("Id= "+results.get(i).id+" urlID= "+results.get(i).url_id+" wordID= "+results.get(i).word_id);
        }
    }

    public void getDistinctWords()
    {
        distinctWords=new Vector<Integer>();
        int num;
        for(int i=0;i<results.size();i++)
        {
            num=results.get(i).word_id;
            if(!distinctWords.contains(num))
                distinctWords.add(num);
        }
    }

    public void getDistinctUrls()
    {
        distinctUrls=new Vector<Integer>();
        int num;
        for(int i=0;i<results.size();i++)
        {
            num=results.get(i).url_id;
            if(!distinctUrls.contains(num))
                distinctUrls.add(num);
        }
    }

    public void sortImportance()
    {
        Vector<Result> newVector=new Vector<Result>();

        while (!results.isEmpty())
        {
            int min=9999;int minIndex=-1;int minj=9999;
            for (int i = 0; i < results.size(); i++)
            {
                if (min > results.get(i).i && minj > results.get(i).j)
                {
                    min = results.get(i).i;
                    minj= results.get(i).j;
                    minIndex = i;
                }
            }
            newVector.add(results.get(minIndex));
            results.remove(minIndex);
        }
        results=newVector;
    }

    public void sortCount()
    {
        Vector<Result> newVector=new Vector<Result>();

        while (!results.isEmpty())
        {
            int max=0;int maxIndex=-1;
            for (int i = 0; i < results.size(); i++)
            {
                if (max < results.get(i).num)
                {
                    max = results.get(i).num;
                    maxIndex = i;
                }
            }
            newVector.add(results.get(maxIndex));
            results.remove(maxIndex);
        }
        results=newVector;
    }

    public void sortPosition()
    {
        Vector<Result> newVector=new Vector<Result>();

        while (!results.isEmpty())
        {
            int min=9999;int minIndex=-1;
            for (int i = 0; i < results.size(); i++)
            {
                if (min > results.get(i).j)
                {
                    min = results.get(i).j;
                    minIndex = i;
                }
            }
            newVector.add(results.get(minIndex));
            results.remove(minIndex);
        }
        results=newVector;
    }

    public void ranker()
    {
        sortCount();
        sortImportance();
        //sortPosition();
    }

}
