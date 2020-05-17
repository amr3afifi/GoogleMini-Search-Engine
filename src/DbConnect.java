import java.sql.*;

public class DbConnect {
    private Connection con;
    private Statement st;

    public DbConnect() {

        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/google?serverTimezone=UTC", "root", "");
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
                System.out.println("Error id=-1");
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

    public int findBoth_inCombined(int word,int url)
    {
        try{
            ResultSet rs;
            if(word<=0 || url<=0)
            {
                System.out.println("Error id=-1");
                return -1;
            }
            String query="SELECT id FROM google.combined WHERE word_id="+word+" and url_id="+url+";";
            rs=st.executeQuery(query);
            int value=0;
           // if (rs==null)return -1;
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

    public ResultSet getURLS_inCombined(int word)
    {
        ResultSet rs=null;
        try{
            if(word<=0)
            {
                System.out.println("Error id=-1");
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

    public int addWord_toWord(String word)
    {
        try {
            //insert if not available
                String query = "INSERT INTO google.words (word,count,num_of_docs_occurred) VALUES ('"+word+"',"+1+","+1+");";
                st.executeUpdate(query);

            return findWord_inWord(word);
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
                System.out.println("Error id=-1");
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
                System.out.println("Error id=-1");
                return -1;
            }
            String query = "INSERT INTO google.combined (url_id,word_id,importance,importance_index,num_of_occurrences) VALUES ("+url_id+","+word_id+","+i+","+j+","+1+");";
            st.executeUpdate(query);
            return findBoth_inCombined(word_id,url_id);
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public int updateCombined_numOfOccurences(int id)
    {
        try {
            if(id<=0)
            {
                System.out.println("Error id=-1");
                return -1;
            }
            //update if available
            String query = "UPDATE google.combined SET num_of_occurrences = num_of_occurrences + 1 WHERE id="+id+";";
            st.executeUpdate(query);
            return id;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1;
        }
    }

    public int addURL_toURL(String url)
    {
        try {
            int id=findURL_inURL(url);
            if(id<=0) {
                String query = "INSERT INTO google.urls (url,popularity,out_going,in_going) VALUES ('" + url + "'," + 0.0 + ","+0+","+0+");";
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

    public int updatePopularity_inURL(int id,double pop)
    {
        try {
            if(id<=0)
            {
                System.out.println("Error id=-1");
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
                System.out.println("Error id=-1");
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
                System.out.println("Error id=-1");
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

    public void emptyDatabse()
    {
        emptyCombinedTable();
        emptyUrlsTable();
        emptyWordsTable();
    }

}
