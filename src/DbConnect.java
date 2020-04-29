import java.sql.*;


public class DbConnect {
    private Connection con;
    private Statement st;
    private ResultSet rs;

    public DbConnect() {

        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3305/google", "root", "root");
            st = con.createStatement();
            if (con != null) {
                System.out.println("Successfully connected to MySQL database test");
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    public int findURL_inURL(String url)
    {
        try{
            String query="SELECT id FROM google.urls WHERE url='"+url+"';";
            rs=st.executeQuery(query);
            return rs.getInt("id");
        }
        catch (Exception e)
        {
            System.out.println(e);
            return 0;
        }
    }

    public int findWord_inWord(String word)
    {
        try{
            String query="SELECT id FROM google.words WHERE word='"+word+"';";
            rs=st.executeQuery(query);
            return rs.getInt("id");
        }
        catch (Exception e)
        {
            System.out.println(e);
            return 0;
        }
    }

    public int findBoth_inCombined(int word,int url)
    {
        try{
            String query="SELECT id FROM google.combined WHERE word_id="+word+" and url_id="+url+";";
            rs=st.executeQuery(query);
            return rs.getInt("id");
        }
        catch (Exception e)
        {
            System.out.println(e);
            return 0;
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
            return 0;
        }
    }

//    public int findBoth_inCombined(String url,String word)
//    {
//        int wordID=findWord_inWord(word);
//        if(wordID<=0)
//            return 0;
//        int urlID=findURL_inURL(url);
//        if(urlID<=0)
//            return 0;
//        try{
//            String query="SELECT id FROM google.combined WHERE url_id="+urlID+" and word_id="+wordID+";";
//            rs=st.executeQuery(query);
//            return rs.getInt("id");
//        }
//        catch (Exception e)
//        {
//            System.out.println(e);
//            return 0;
//        }
//    }

    public int addWord_toWord(String word)
    {
        try {
            int id=findWord_inWord(word);
            if(id<=0)
            {//insert if not available
                String query = "INSERT INTO google.words (word,count,num_of_docs_occurred) VALUES ('"+word+"',"+1+","+1+");";
                st.executeUpdate(query);
            }
            else
            {//update if available
                String query = "UPDATE google.words SET count = count + 1 WHERE id="+id+";";
                st.executeUpdate(query);
            }

            return findWord_inWord(word);
        }
        catch (Exception e)
        {
            System.out.println(e);
            return 0;
        }
    }

    public int addInCombined(int url_id,int word_id,int i,int j)
    {
        try {
            int id=findBoth_inCombined(word_id,url_id);
            if(id<=0)
            {//insert if not available
                String query = "INSERT INTO google.combined (url_id,word_id,importance,importance_index,num_of_occurrences) VALUES ("+url_id+","+word_id+","+i+","+j+","+1+");";
                st.executeUpdate(query);
            }
            else
            {//update if available
                String query = "UPDATE google.combined SET num_of_occurrences = num_of_occurrences + 1 WHERE id="+id+";";
                st.executeUpdate(query);
            }

            return 1;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return 0;
        }
    }

    public int addURL_toURL(String url,Double popularity)
    {
        try {
            int id=findURL_inURL(url);
            if(id<=0) {
                String query = "INSERT INTO google.urls (url,popularity) VALUES ('" + url + "'," + popularity + ");";
                st.executeUpdate(query);
            }
            else return 0;

            return findURL_inURL(url);
        }
        catch (Exception e)
        {
            System.out.println(e);
            return 0;
        }

    }
}
