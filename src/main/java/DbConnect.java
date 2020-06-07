import java.sql.*;
import java.util.Vector;

public class DbConnect {
    private Connection con;
    private Statement st;

    public DbConnect() {

        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/google?serverTimezone=UTC&jdbcCompliantTruncation=false&zeroDateTimeBehavior=convertToNull&characterEncoding=UTF-8", "root", "");
            st = con.createStatement();
            if (con != null)
                System.out.println("Successfully connected to MySQL database test");
            else
                System.out.println("Cannot connect to MySQL database");

            System.out.println(con);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    public int findURL_inURL(String url)
    {
        try{
            ResultSet rs;
            String query="SELECT id FROM google.urls WHERE url='"+url+"';";
            rs=st.executeQuery(query);
            int value=0;
            //if (rs==null)return -1;
            while(rs.next())
            {
                value=rs.getInt("id");
            }
            return value;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public String getURLByID_inURL(int id)
    {
        try{
            ResultSet rs;
            if(id<=0)
            {
               // System.out.println("Error id=-1");
                return "";
            }
            String query="SELECT url FROM google.urls WHERE id="+id+";";
            rs=st.executeQuery(query);
            if(rs==null)return "";
            String value="";
            while(rs.next())
            {
                value=rs.getString("url");
            }
            return value;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return "";
        }
    }

    public String getWordByID_inWord(int id)
    {
        try{
            ResultSet rs;
            if(id<=0)
            {
                // System.out.println("Error id=-1");
                return "";
            }
            String query="SELECT word FROM google.words WHERE id="+id+";";
            rs=st.executeQuery(query);
            if(rs==null)return "";
            String value="";
            while(rs.next())
            {
                value=rs.getString("word");
            }
            return value;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return "";
        }
    }

    public int getfirstURL_inURL()
    {
        int value=-1;
        try{

            ResultSet rs;
            String query="SELECT `id` FROM `urls` LIMIT 1;";
            rs=st.executeQuery(query);
            if(rs==null) return value;
            while(rs.next())
            {
                value=rs.getInt("id");
            }

        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return value;
    }

    public int findWord_inWord(String word)
    {
        try{
            ResultSet rs;
            String query="SELECT id FROM google.words WHERE word='"+word+"';";

            rs=st.executeQuery(query);

            int value=0;
            //if (rs==null)return -1;

            while(rs.next())
            {
                value=rs.getInt("id");
            }
            return value;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public int getNumOfOccurrences_inCombined(int word,int url)
    {
        try{
            ResultSet rs;
            if(word<=0 || url<=0)
            {
                //System.out.println("Error id=-1");
                return -1;
            }
            String query="SELECT COUNT(*) FROM google.combined WHERE word_id="+word+" and url_id="+url+";";
            rs=st.executeQuery(query);
            int value=0;
           // if (rs==null)return -1;
            while(rs.next())
            {
                value=rs.getInt(1);
            }
            return value;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public ResultSet getURLS_inCombined(int word)
    {
        ResultSet rs=null;
        try{
            if(word<=0)
            {
                //System.out.println("Error id=-1");
                return rs;
            }
            String query="SELECT * FROM google.combined WHERE word_id="+word+";";
            return st.executeQuery(query);

        }
        catch (Exception e)
        {
            System.out.println(e);
            return rs;
        }
    }

    public ResultSet getTrends_inCountry(String country)
    {
        ResultSet rs=null;
        try{
            String query="SELECT * FROM google.queries WHERE location='"+country+"' ORDER BY count DESC";
            return st.executeQuery(query);
        }

          catch (Exception e)
        {
            System.out.println(e);
            return rs;
        }
    }

    public int updateWord_numOfDocs(String word)
    {
        try {
                int id=findWord_inWord(word);
                String query = "UPDATE google.words SET num_of_docs_occurred = num_of_docs_occurred + 1 WHERE id="+id+";";
                st.executeUpdate(query);

            return id;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public int updateWordCount_inURLS(int url_id,int num)
    {
        try {
            if(url_id<=0 || num<=0)
                return -1;

            String query = "UPDATE google.urls SET word_count ="+num+" WHERE id="+url_id+";";
            st.executeUpdate(query);
            return 1;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public int addWord_toWord(String word)
    {
        try {
            //insert if not available
                String query = "INSERT INTO google.words (word,count,num_of_docs_occurred) VALUES ('"+word+"',"+1+","+0+");";
                st.executeUpdate(query);

            return findWord_inWord(word);
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public int addImage_toImage(int url_id,String src ,String alt)
    {
        try {
            //insert if not available
            if(src.isEmpty() || alt.isEmpty())
                return 0;

            String query = "INSERT INTO google.images (url_id,src,alt) VALUES ("+url_id+",'"+src+"','"+alt+"');";
            st.executeUpdate(query);

            return 1;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public int updateWordCount_inWord(int id)
    {
        try {
            if(id<=0)
            {
               // System.out.println("Error id=-1");
                return -1;
            }
                String query = "UPDATE google.words SET count = count + 1 WHERE id="+id+";";
                st.executeUpdate(query);
            return id;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public int addInCombined(int url_id,int word_id,int i,int j)
    {
        try {
            //insert if not available
            if(word_id<=0 || url_id<=0)
            {
                //System.out.println("Error id=-1");
                return -1;
            }
            String query = "INSERT INTO google.combined (url_id,word_id,importance,importance_index) VALUES ("+url_id+","+word_id+","+i+","+j+");";
            st.executeUpdate(query);
            return 1;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public int addURL_toURL(String url,long date,String country)
    {
        try {
            int id=findURL_inURL(url);
            if(id<=0) {
                String query = "INSERT INTO google.urls (url,popularity,out_going,in_going,date,country) VALUES ('" + url + "'," + 0.0 + ","+0+","+0+","+date+",'"+country+"');";
                st.executeUpdate(query);
            }

            return findURL_inURL(url);
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }

    }

    public int addQuery_query(String searchBox,String geographicalLocation,String qname)
    {
        try {
            if(searchBox.isEmpty() || geographicalLocation.isEmpty())
                return -1;

                String query = "INSERT INTO google.queries (query,location,name) VALUES ('" + searchBox + "','"+geographicalLocation+"','"+qname+"');";
                st.executeUpdate(query);

            return 1;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }

    }

    public int findQuery_inQueries(String text,String geo,String name)
    {
        try{
            ResultSet rs=null;
            if(text.isEmpty() || geo.isEmpty())
                return -1;

//          String query="SELECT * FROM google.queries WHERE name LIKE '%"+name+"%' and location='"+geo+"';";

            String query="SELECT * FROM google.queries WHERE query LIKE '%"+text+"%' and location='"+geo+"';";
            rs=st.executeQuery(query);

            int value=0;
            // if (rs==null)return -1;
            while(rs.next())
            {
                value=rs.getInt("count");
            }
            return value;

        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public int updateQueryCount_inQuery(String text,String geo,String name)
    {
        try {
            if(text.isEmpty() || geo.isEmpty())
                return -1;

           // String query = "UPDATE google.queries SET count = count + 1 WHERE name LIKE '%"+name+"%' and location='"+geo+"';";
            String query = "UPDATE google.queries SET count = count + 1 WHERE query LIKE '%"+text+"%' and location='"+geo+"';";
            st.executeUpdate(query);
            return 1;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public int updatePopularity_inURL(int id,double pop)
    {
        try {
            if(id<=0)
            {
               // System.out.println("Error id=-1");
                return -1;
            }
            //update if available
            String query = "UPDATE google.urls SET popularity = "+pop+" WHERE id="+id+";";
            st.executeUpdate(query);
            return id;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public int updateIngoing_inURL(int id)
    {
        try {
            if(id<=0)
            {
               // System.out.println("Error id=-1");
                return -1;
            }
            //update if available
            String query = "UPDATE google.urls SET in_going = in_going+1 WHERE id="+id+";";
            st.executeUpdate(query);
            return id;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public int updateOutgoing_inURL(int id,int num)
    {
        try {
            if(id<=0)
            {
               // System.out.println("Error id=-1");
                return -1;
            }
            //update if available
            String query = "UPDATE google.urls SET out_going = "+num+" WHERE id="+id+";";
            st.executeUpdate(query);
            return id;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public void emptyUrlsTable()
    {
        try {

            String query = "DELETE FROM google.urls";
            st.executeUpdate(query);

        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public void emptyImagesTable()
    {
        try {

            String query = "DELETE FROM google.images";
            st.executeUpdate(query);

        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public void emptyWordsTable()
    {
        try {

            String query = "DELETE FROM google.words";
            st.executeUpdate(query);

        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public void emptyCombinedTable()
    {
        try {

            String query = "DELETE FROM google.combined";
            st.executeUpdate(query);

        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public void emptyQueriesTable()
    {
        try {

            String query = "DELETE FROM google.queries";
            st.executeUpdate(query);

        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public void emptyDatabse()
    {
        emptyImagesTable();
        emptyCombinedTable();
        emptyUrlsTable();
        emptyWordsTable();
        emptyQueriesTable();
    }

    public int indexerThreadEntered_inURL(int id)
    {
        try {
            if(id<=0)
            {
                System.out.println("URL does not exist in database");
                return -1;
            }
            //update if available
            String query = "UPDATE google.urls SET indexed = true WHERE id="+id+";";
            st.executeUpdate(query);
            return id;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public int getIndexed_inURL(int id)
    {

        try{
            ResultSet rs=null;
            if(id<=0)
            {
                System.out.println("URL does not exist in database");
                return -1;
            }

            String query="SELECT indexed FROM google.urls WHERE id="+id+";";

            rs=st.executeQuery(query);

            int value=-1;
            //if (rs==null)return -1;
            while(rs.next())
            {
                if(rs.getInt("indexed")==1)
                    value=1;
                else
                    value=0;
            }
            return value;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public int resumeFALSE_inURL(int id)
    {
        try {
            if(id<=0)
            {
                System.out.println("URL does not exist in database");
                return -1;
            }
            //update if available
            String query = "UPDATE google.urls SET resume = false WHERE id="+id+";";
            st.executeUpdate(query);
            return id;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public int resumeTRUE_inURL(int id)
    {
        try {
            if(id<=0)
            {
                System.out.println("URL does not exist in database");
                return -1;
            }
            //update if available
            String query = "UPDATE google.urls SET resume = true WHERE id="+id+";";
            st.executeUpdate(query);
            return id;
        }
        catch (Exception e)
        {
             System.out.println(e);
            return -1;
        }
    }

    public int enterFALSE_inURL(int id)
    {
        try {
            if(id<=0)
            {
                System.out.println("URL does not exist in database");
                return -1;
            }
            //update if available
            String query = "UPDATE google.urls SET enter = false WHERE id="+id+";";
            st.executeUpdate(query);
            return id;
        }
        catch (Exception e)
        {
             System.out.println(e);
            return -1;
        }
    }

    public int enterTRUE_inURL(int id)
    {
        try {
            if(id<=0)
            {
                System.out.println("URL does not exist in database");
                return -1;
            }
            //update if available
            String query = "UPDATE google.urls SET enter = true WHERE id="+id+";";
            st.executeUpdate(query);
            return id;
        }
        catch (Exception e)
        {
             System.out.println(e);
            return -1;
        }

    }

    public int getEnter_inURL(int id)
    {

        try{
            ResultSet rs=null;
            if(id<=0)
            {
                System.out.println("URL does not exist in database");
                return -1;
            }

            String query="SELECT enter FROM google.urls WHERE id="+id+";";

            rs=st.executeQuery(query);

            int value=0;
            //if (rs==null)return -1;
            while(rs.next())
            {
                if(rs.getInt("enter")==1)
                    value=1;
                else
                    value=0;
            }
            return value;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public int getMaxCount_inURL(int id)
    {

        try{
            ResultSet rs=null;
            if(id<=0)
            {
                System.out.println("URL does not exist in database");
                return -1;
            }

            String query="SELECT max_count FROM google.urls WHERE id="+id+";";

            rs=st.executeQuery(query);

            int value=0;
            //if (rs==null)return -1;
            while(rs.next())
            {
                value=rs.getInt("max_count");
            }
            return value;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public int getResume_inURL(int id)
    {
        try{
            if(id<=0)
            {
                System.out.println("URL does not exist in database");
                return -1;
            }
            ResultSet rs;
            String query="SELECT resume FROM google.urls WHERE id="+id+";";

            rs=st.executeQuery(query);

            int value=0;
            //if (rs==null)return -1;
            while(rs.next())
            {
                if(rs.getBoolean("resume")==true)
                    value=1;
                else
                    value=0;
            }
            return value;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public int getWordCount_inURL(int id)
    {
        try{
            if(id<=0)
            {
                System.out.println("URL does not exist in database");
                return -1;
            }
            ResultSet rs;
            String query="SELECT word_count FROM google.urls WHERE id="+id+";";

            rs=st.executeQuery(query);

            int value=0;
            //if (rs==null)return -1;
            while(rs.next())
            {
                value=rs.getInt("word_count");
            }
            return value;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public int updateMaxCount_inURLS(int id)
    {
        try {
            if(id<=0)
            {
                System.out.println("URL does not exist in database");
                return -1;
            }
            //update if available
            String query = "UPDATE google.urls SET max_count = max_count + 1 WHERE id="+id+";";
            st.executeUpdate(query);
            return id;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public ResultSet getURLSToResume()
    {
        ResultSet rs=null;
        try{

            String query="SELECT url FROM google.urls WHERE resume=1 ;";
            return st.executeQuery(query);

        }
        catch (Exception e)
        {
             System.out.println(e);
            return rs;
        }
    }

    public ResultSet getInGoingAndOutgoing(int id)
    {
        ResultSet rs=null;
        try{

            String query="SELECT * FROM google.urls WHERE id="+id+";";
            return st.executeQuery(query);

        }
        catch (Exception e)
        {
            System.out.println(e);
            return rs;
        }
    }

    public ResultSet getImages(String word)
    {
        try{
            String query="SELECT * FROM google.images WHERE alt LIKE '%"+word+"%';";
            return st.executeQuery(query);

        }
        catch (Exception e)
        {
            System.out.println(e);
            return null;
        }
    }

    public ResultSet getAllUrls()
    {
        try{
            String query="SELECT * FROM google.urls ORDER BY popularity DESC";
            return st.executeQuery(query);
        }
        catch (Exception e)
        {
            System.out.println(e);
            return null;
        }
    }

    public void setIndexedToFalse()
    {
        try {

            String query = "UPDATE urls SET indexed=false;";
            st.executeUpdate(query);

        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public int closeConnection()
    {
        try {
            if (con != null) {
                con.close();
                System.out.println("Connection closed");
            }
            return 1;
        } catch (SQLException se) {
        }
        return 0;
    }

    public int addDocument_ToDoc(String doc)
    {
        try {
            if(doc==null ||doc=="")return -1;

            String query = "INSERT INTO google.documents (document) VALUES ('"+doc+"');";
            st.executeUpdate(query);

            return findDoc_inDocs(doc);
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }

    }

    public int findURL_inDOCURL(int id)
    {
        try{
            ResultSet rs;
            String query="SELECT * FROM doc_url WHERE url_id="+id+";";
            rs=st.executeQuery(query);
            int value=0;
            //if (rs==null)return -1;
            while(rs.next())
            {
                    value=rs.getInt("doc_id");
            }
            return value;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public int findDoc_inDocs(String doc)
    {
        try{
            ResultSet rs;
            String query="SELECT id FROM google.documents WHERE document='"+doc+"';";
            rs=st.executeQuery(query);

            int value=0;
            //if (rs==null)return -1;
            while(rs.next())
            {
                    value=rs.getInt("id");
            }

            return value;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public String getDoc_BYID(int doc)
    {
        try{
            ResultSet rs;
            String query="SELECT document FROM google.documents WHERE id="+doc+";";
            rs=st.executeQuery(query);

            String value="";
            //if (rs==null)return -1;
            while(rs.next())
            {
                value=rs.getString("document");
            }

            return value;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return "";
        }
    }

    public int addInDoc_URL(int url,int doc)
    {
        try {
            //insert if not available
            if(url<=0 || doc<=0)
            {
                //System.out.println("Error id=-1");
                return -1;
            }
            String query = "INSERT INTO google.doc_url (url_id,doc_id) VALUES ("+url+","+doc+");";
            st.executeUpdate(query);
            return 1;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public int GetNumberOfURLS()
    {
        String query = "SELECT COUNT(*) AS rowcount FROM google.urls;";
        try {

            ResultSet rs = st.executeQuery(query);
            int count = -1;
            while(rs.next())
                count = rs.getInt("rowcount");
            return count;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public ResultSet GetURLsByWords(Vector<WordStruct> wordsIDs)
    {
        StringBuilder condition= new StringBuilder();

        for (int i=0; i<wordsIDs.size(); i++)
        {
            condition.append(" word_id=");
            condition.append(wordsIDs.get(i).id);
            if (i!=wordsIDs.size()-1)
                condition.append(" OR");

        }

        String query = "SELECT * FROM google.combined WHERE"+condition.toString()+" ORDER BY url_id,importance,importance_index;";
        try
        {
            return st.executeQuery(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public ResultSet GetImagesByWords(Vector<String> words)
    {
        StringBuilder condition= new StringBuilder();

        for (int i=0; i<words.size(); i++)
        {
            condition.append(" alt LIKE ");
            condition.append("'%"+words.get(i)+"%'");
            if (i!=words.size()-1)
                condition.append(" OR");

        }

        String query = "SELECT * FROM google.images WHERE"+condition.toString()+";";

        try
        {
            return st.executeQuery(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet GetEssentialURLInfoById(int urlid)
    {
        String query = "SELECT url, in_going, out_going, word_count, date, country FROM google.urls WHERE id="+urlid+";"; //add geographic location and date to the query

        try {
            return st.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String GetWordByWordID(int wordID)
    {
        String query = "SELECT word FROM google.words WHERE id="+wordID+";";
        try {
            ResultSet rs = st.executeQuery(query);
            while(rs.next())
            {
                return rs.getString("word");
            }
            return "";
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }

    }

    public String GetSnippetFromResult(Result toBeSnipped)
    {
        ResultSet rs;
        String query = "SELECT word_id FROM google.combined WHERE url_id="+toBeSnipped.url_id+" AND importance="+toBeSnipped.i+";";
        try {
            rs=st.executeQuery(query);
            StringBuilder snippet = new StringBuilder();
            Vector<Integer> wordsIDs = new Vector<Integer>();
            int wordsAdded = 0;
            boolean wordReached = false;
            while (rs.next() && wordsAdded<5)
            {
                wordsAdded++;
                wordsIDs.add(rs.getInt("word_id"));
                if (rs.getInt("word_id")==toBeSnipped.word_id)
                {
                    wordReached=true;
                    break;
                }
            }

            while(rs.next() && wordsAdded<11)
            {
                if(!wordReached)
                {
                    wordsIDs.remove(0);
                    wordsAdded--;
                }

                wordsIDs.add(rs.getInt("word_id"));
                wordsAdded++;
                if(rs.getInt("word_id")==toBeSnipped.word_id) wordReached=true;

            }

            for (Integer wordsID : wordsIDs) {
                snippet.append(GetWordByWordID(wordsID));
                snippet.append(" ");
            }

            return snippet.toString();

        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }

    }

    public WordStruct GetWordStructFromString(String word)
    {
        ResultSet rs;
        String query = "SELECT id, num_of_docs_occurred FROM google.words WHERE word='"+word+"';";
        try {
            rs=st.executeQuery(query);
            WordStruct desiredValue=null;
            while(rs.next())
            {
                desiredValue = new WordStruct(rs.getInt("id"),rs.getInt("num_of_docs_occurred"));

            }
            return desiredValue;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


}
