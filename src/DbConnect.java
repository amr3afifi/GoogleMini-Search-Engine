import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbConnect {
    private Connection con;
    private Statement st;
    private ResultSet rs;

    public DbConnect()
    {
        try
        {

            con=DriverManager.getConnection("jdbc::mysql://localhost/google","root","");
            st=con.createStatement();


        }
        catch(Exception e)
        {
System.out.println(e);
        }

    }

    public void getData()
    {
        try
        {
            String query ="select * from urls";
            rs=st.executeQuery(query);
            System.out.println("success");

        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
}
