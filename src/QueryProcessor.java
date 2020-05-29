import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class QueryProcessor extends HttpServlet {

    @Override
    public void init() throws ServletException {
        this.stemmer=new Stemmer();
        this.db=new DbConnect();
    }
    public QueryProcessor(){
        this.db=new DbConnect();
        this.stemmer=new Stemmer();
    }

    public class ResultText
    {
        int id=0;
        int word_id=0;
        int url_id=0;
        int i=0;
        int j=0;
        int num=0;
    }
    public class ResultImage
    {
        int id=0;
        int url_id=0;
        String src="";
        String alt="";
    }

    public class ResultTrend
    {
        int count=0;
        String text="";
    }

    private Stemmer stemmer;
    private DbConnect db;
    Vector<ResultText> resultsText;
    Vector<ResultImage> resultsImages;
    Vector<Integer> distinctWords;
    Vector<Integer> distinctUrls;

    public void searchDatabaseText(String searchBox)
    {
        resultsText=new Vector<ResultText>();
        String[] words=searchBox.split("[()+;$*=#, ?.:!\"]+");
        int word_id=0;
        for(int i=0;i<words.length;i++)
        {
            char[] stemArray = words[i].toCharArray();
            stemmer.add(stemArray, words[i].length());
            stemmer.stem();
            words[i] = stemmer.toString();

            System.out.println(words[i]);

            word_id=db.findWord_inWord(words[i]);
            System.out.println(word_id);
            if(word_id<=0)continue;
            ResultSet rs=db.getURLS_inCombined(word_id);
            if(rs==null)continue;
            ResultText result;
            int count = 0;
            try {
                while(rs.next())
                {
                    result=new ResultText();
                    count++;
                    result.id = rs.getInt("id");
                    result.word_id = rs.getInt("word_id");
                    result.url_id = rs.getInt("url_id");
                    result.i = rs.getInt("importance");
                    result.j = rs.getInt("importance_index");
                    result.num = rs.getInt("num_of_occurrences");
                    resultsText.add(result);
                }
            }catch (SQLException ex)
            {

            }

        }

    }

    public void searchDatabaseImages(String searchBox)
    {
        resultsImages=new Vector<ResultImage>();
        String[] words=searchBox.split("[()+;$*=#, ?.:!\"]+");
        int word_id=0;
        for(int i=0;i<words.length;i++)
        {
            char[] stemArray = words[i].toCharArray();
            stemmer.add(stemArray, words[i].length());
            stemmer.stem();
            words[i] = stemmer.toString();

            ResultSet rs=db.getImages(words[i]);
            if(rs==null)continue;
            ResultImage result;
            try {
                while(rs.next())
                {
                    result=new ResultImage();
                    result.src = rs.getString("src");
                    result.alt = rs.getString("alt");
                    result.url_id = rs.getInt("url_id");
                    result.id = rs.getInt("id");
                    resultsImages.add(result);
                }
            }catch (SQLException ex)
            {

            }

        }

    }

    public void printVector()
    {
        for(int i=0;i<resultsText.size();i++)
        {
            System.out.println("Id="+resultsText.get(i).id+" urlID="+resultsText.get(i).url_id+" wordID="+resultsText.get(i).word_id +" I="+resultsText.get(i).i+" J="+resultsText.get(i).j+" Num="+resultsText.get(i).num);
        }
    }

    public void getDistinctWords()
    {
        distinctWords=new Vector<Integer>();
        int num;
        for(int i=0;i<resultsText.size();i++)
        {
            num=resultsText.get(i).word_id;
            if(!distinctWords.contains(num))
                distinctWords.add(num);
        }
    }

    public void getDistinctUrls()
    {
        distinctUrls=new Vector<Integer>();
        int num;
        for(int i=0;i<resultsText.size();i++)
        {
            num=resultsText.get(i).url_id;
            if(!distinctUrls.contains(num))
                distinctUrls.add(num);
        }
    }

    public void sortImportance()
    {
        Vector<ResultText> newVector=new Vector<ResultText>();

        while (!resultsText.isEmpty())
        {
            int min=9999;int minIndex=-1;int minj=9999;
            for (int i = 0; i < resultsText.size(); i++)
            {
                if (min > resultsText.get(i).i && minj > resultsText.get(i).j)
                {
                    min = resultsText.get(i).i;
                    minj= resultsText.get(i).j;
                    minIndex = i;
                }
            }
            newVector.add(resultsText.get(minIndex));
            resultsText.remove(minIndex);
        }
        resultsText=newVector;
    }

    public void sortCount()
    {
        Vector<ResultText> newVector=new Vector<ResultText>();

        while (!resultsText.isEmpty())
        {
            int max=0;int maxIndex=-1;
            for (int i = 0; i < resultsText.size(); i++)
            {
                if (max < resultsText.get(i).num)
                {
                    max = resultsText.get(i).num;
                    maxIndex = i;
                }
            }
            newVector.add(resultsText.get(maxIndex));
            resultsText.remove(maxIndex);
        }
        resultsText=newVector;
    }

    public void sortPosition()
    {
        Vector<ResultText> newVector=new Vector<ResultText>();

        while (!resultsText.isEmpty())
        {
            int min=9999;int minIndex=-1;
            for (int i = 0; i < resultsText.size(); i++)
            {
                if (min > resultsText.get(i).j)
                {
                    min = resultsText.get(i).j;
                    minIndex = i;
                }
            }
            newVector.add(resultsText.get(minIndex));
            resultsText.remove(minIndex);
        }
        resultsText=newVector;
    }

    public void ranker()
    {
        sortPosition();
        sortCount();
        sortImportance();
    }

    public void createJSONText(String searchBox)
    {

        JSONArray list=new JSONArray();
        for (int i=0;i<resultsText.size();i++)
        {
            JSONObject obj=new JSONObject();
            int url_id=resultsText.get(i).url_id;
            String url=db.getURLByID_inURL(url_id);
            int indexWWW=-1;int indexCOM=-1;
            indexWWW=url.indexOf("www.");
            indexCOM=url.lastIndexOf(".");
            String mainsite=url;
            if(indexCOM>indexWWW)
                mainsite=url.substring(indexWWW+4,indexCOM);

            mainsite.toUpperCase();
            System.out.println(url);
            System.out.println(mainsite);
             url.replace("\\","");
            obj.put("url",url);
            obj.put("mainsite",mainsite);
            list.add(obj);
        }

        try(FileWriter file=new FileWriter("tomcat/webapps/ROOT/"+searchBox+"_text.json"))
        {
            file.write(list.toString());
            file.flush();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void createJSONImages(String searchBox)
    {
        JSONArray list=new JSONArray();
        for (int i=0;i<resultsImages.size();i++)
        {
            JSONObject obj=new JSONObject();
            String url=db.getURLByID_inURL(resultsImages.get(i).url_id);
            String src=resultsImages.get(i).src;
            String alt=resultsImages.get(i).alt;
            src.replace("\\","");
            url.replace("\\","");
            alt.replace("\\","");

            obj.put("url",url);
            obj.put("src",src);
            obj.put("alt",alt);
            list.add(obj);

        }

        try(FileWriter file=new FileWriter("tomcat/webapps/ROOT/"+searchBox+"_images.json"))
        {
            file.write(list.toString());
            file.flush();
        }catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public void createJSONTrends(String searchBox)
    {
        String[] countries = {"Afghanistan", "Aland Islands", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antarctica", "Antigua And Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia And Herzegovina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory", "Brunei Darussalam", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island", "Cocos (keeling) Islands", "Colombia", "Comoros", "Congo", "Congo, The Democratic Republic Of The", "Cook Islands", "Costa Rica", "Cote Divoire", "Croatia", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Falkland Islands (Malvinas)", "Faroe Islands", "Fiji", "Finland", "France", "French Guiana", "French Polynesia", "French Southern Territories", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guernsey", "Guinea", "Guinea-bissau", "Guyana", "Haiti", "Heard Island And Mcdonald Islands", "Holy See (Vatican City State)", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran, Islamic Republic Of", "Iraq", "Ireland", "Isle Of Man", "Italy", "Jamaica", "Japan", "Jersey", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea, Democratic Peoples Republic Of", "Korea, Republic Of", "Kuwait", "Kyrgyzstan", "Lao Peoples Democratic Republic", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libyan Arab Jamahiriya", "Liechtenstein", "Lithuania", "Luxembourg", "Macao", "Macedonia, The Former Yugoslav Republic Of", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia, Federated States Of", "Moldova, Republic Of", "Monaco", "Mongolia", "Montenegro", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands","Netherlands Antilles", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau", "Palestinian Territory, Occupied", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russian Federation", "Rwanda", "Saint Helena", "Saint Kitts And Nevis", "Saint Lucia", "Saint Pierre And Miquelon", "Saint Vincent And The Grenadines", "Samoa", "San Marino", "Sao Tome And Principe", "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Georgia And The South Sandwich Islands", "Spain", "Sri Lanka", "Sudan", "Suriname", "Svalbard And Jan Mayen", "Swaziland", "Sweden", "Switzerland", "Syrian Arab Republic", "Taiwan, Province Of China", "Tajikistan", "Tanzania, United Republic Of", "Thailand", "Timor-leste", "Togo", "Tokelau", "Tonga", "Trinidad And Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks And Caicos Islands", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "United States Minor Outlying Islands", "Uruguay", "Uzbekistan", "Vanuatu", "Venezuela", "Viet Nam", "Virgin Islands, British", "Virgin Islands, U.S.", "Wallis And Futuna", "Western Sahara", "Yemen", "Zambia", "Zimbabwe"};
        JSONArray list=new JSONArray();

        for (int i=0;i<countries.length;i++)
        {
            JSONObject obj=new JSONObject();
            obj.put("country",countries[i]);
            ResultSet rs=db.getTrends_inCountry(countries[i]);
            Vector<ResultTrend> resultsTrends= new Vector<>();
            ResultTrend result;
        if(rs!=null) {
            try {
                while (rs.next()) {
                    result = new ResultTrend();
                    result.text = rs.getString("text");
                    result.count = rs.getInt("count");
                    resultsTrends.add(result);
                }
            } catch (SQLException ex) {
            }
        }

            for(int j=0;j<10;j++)
            {
                if(j<resultsTrends.size())
                {
                    obj.put("trend_"+j+"_text",resultsTrends.get(j).text);
                    obj.put("trend_"+j+"_count",resultsTrends.get(j).count);
                }
                else
                {
                    obj.put("trend_"+j+"_text","empty");
                    obj.put("trend_"+j+"_count",0);
                }
            }
            list.add(obj);
        }

        try(FileWriter file=new FileWriter("tomcat/webapps/ROOT/trends.json"))
        {
            file.write(list.toString());
            file.flush();
        }catch (IOException e) { e.printStackTrace(); }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String searchBox = request.getParameter("searchBox");
        String geographicalLocation = request.getParameter("geographicalLocation");

        response.setContentType("text/html");
        String pageFooter="";
        String searchType=request.getParameter("searchType");
        System.out.println(searchType);
        if(searchType.equals("text") || searchType.equals("trends"))
        {
            searchDatabaseText(searchBox);
            //ranker();
            createJSONText(searchBox);
            pageFooter=insertPageTextFooter();
        }
        else if(searchType.equals("images"))
        {
            searchDatabaseImages(searchBox);
            createJSONImages(searchBox);
            pageFooter=insertPageImagesFooter();
        }

        String pageHeader=insertPageHeader();
        String pageBody=insertPageBody(searchBox,geographicalLocation);
        String page=pageHeader+pageBody+pageFooter;
        response.getWriter().println(page);

        if(searchType.equals("text"))
        {
            searchDatabaseImages(searchBox);
            createJSONImages(searchBox);
        }
        else if(searchType.equals("images"))
        {
            searchDatabaseText(searchBox);
            //ranker();
            createJSONText(searchBox);
        }

        int exits=db.findQuery_inQueries(searchBox,geographicalLocation);
        if(exits>0)
            db.updateQueryCount_inQuery(searchBox,geographicalLocation);
        else
            db.addQuery_query(searchBox,geographicalLocation);

        createJSONTrends(searchBox);
    }

    public String insertPageHeader()
    {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "<link rel=\"icon\" href=\"https://cdn2.iconfinder.com/data/icons/social-icons-33/128/Google-512.png\">\n" +
                "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css\">\n" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"index.css\">\n" +
                "<title>Google</title>\n" +
                "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.6.0/Chart.min.js\"></script>"+
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "<a href=\"http://localhost:8080/homepage.html\">\n" +
                "    <img alt=\"Google image\" class=\"img2\"\n" +
                "            src=\"./google.png\"/></a>"+
                "            \n" +
                "            <div class=\"searchbox2\">\n" +
                "                \n";
    }

    public String insertCountries()
    {
        return "                <option value=\"Afghanistan\">Afghanistan</option>\n" +
                "                <option value=\"Aland Islands\">Aland Islands</option>\n" +
                "                <option value=\"Albania\">Albania</option>\n" +
                "                <option value=\"Algeria\">Algeria</option>\n" +
                "                <option value=\"American Samoa\">American Samoa</option>\n" +
                "                <option value=\"Andorra\">Andorra</option>\n" +
                "                <option value=\"Angola\">Angola</option>\n" +
                "                <option value=\"Anguilla\">Anguilla</option>\n" +
                "                <option value=\"Antarctica\">Antarctica</option>\n" +
                "                <option value=\"Antigua and Barbuda\">Antigua and Barbuda</option>\n" +
                "                <option value=\"Argentina\">Argentina</option>\n" +
                "                <option value=\"Armenia\">Armenia</option>\n" +
                "                <option value=\"Aruba\">Aruba</option>\n" +
                "                <option value=\"Australia\">Australia</option>\n" +
                "                <option value=\"Austria\">Austria</option>\n" +
                "                <option value=\"Azerbaijan\">Azerbaijan</option>\n" +
                "                <option value=\"Bahamas\">Bahamas</option>\n" +
                "                <option value=\"Bahrain\">Bahrain</option>\n" +
                "                <option value=\"Bangladesh\">Bangladesh</option>\n" +
                "                <option value=\"Barbados\">Barbados</option>\n" +
                "                <option value=\"Belarus\">Belarus</option>\n" +
                "                <option value=\"Belgium\">Belgium</option>\n" +
                "                <option value=\"Belize\">Belize</option>\n" +
                "                <option value=\"Benin\">Benin</option>\n" +
                "                <option value=\"Bermuda\">Bermuda</option>\n" +
                "                <option value=\"Bhutan\">Bhutan</option>\n" +
                "                <option value=\"Bolivia\">Bolivia</option>\n" +
                "                <option value=\"Bosnia and Herzegovina\">Bosnia and Herzegovina</option>\n" +
                "                <option value=\"Botswana\">Botswana</option>\n" +
                "                <option value=\"Bouvet Island\">Bouvet Island</option>\n" +
                "                <option value=\"Brazil\">Brazil</option>\n" +
                "                <option value=\"British Indian Ocean Territory\">British Indian Ocean Territory</option>\n" +
                "                <option value=\"Brunei Darussalam\">Brunei Darussalam</option>\n" +
                "                <option value=\"Bulgaria\">Bulgaria</option>\n" +
                "                <option value=\"Burkina Faso\">Burkina Faso</option>\n" +
                "                <option value=\"Burundi\">Burundi</option>\n" +
                "                <option value=\"Cambodia\">Cambodia</option>\n" +
                "                <option value=\"Cameroon\">Cameroon</option>\n" +
                "                <option value=\"Canada\">Canada</option>\n" +
                "                <option value=\"Cape Verde\">Cape Verde</option>\n" +
                "                <option value=\"Cayman Islands\">Cayman Islands</option>\n" +
                "                <option value=\"Central African Republic\">Central African Republic</option>\n" +
                "                <option value=\"Chad\">Chad</option>\n" +
                "                <option value=\"Chile\">Chile</option>\n" +
                "                <option value=\"China\">China</option>\n" +
                "                <option value=\"Christmas Island\">Christmas Island</option>\n" +
                "                <option value=\"Cocos (Keeling) Islands\">Cocos (Keeling) Islands</option>\n" +
                "                <option value=\"Colombia\">Colombia</option>\n" +
                "                <option value=\"Comoros\">Comoros</option>\n" +
                "                <option value=\"Congo\">Congo</option>\n" +
                "                <option value=\"Congo, The Democratic Republic of The\">Congo, The Democratic Republic of The</option>\n" +
                "                <option value=\"Cook Islands\">Cook Islands</option>\n" +
                "                <option value=\"Costa Rica\">Costa Rica</option>\n" +
                "                <option value=\"Cote Divoire\">Cote D'ivoire</option>\n" +
                "                <option value=\"Croatia\">Croatia</option>\n" +
                "                <option value=\"Cuba\">Cuba</option>\n" +
                "                <option value=\"Cyprus\">Cyprus</option>\n" +
                "                <option value=\"Czech Republic\">Czech Republic</option>\n" +
                "                <option value=\"Denmark\">Denmark</option>\n" +
                "                <option value=\"Djibouti\">Djibouti</option>\n" +
                "                <option value=\"Dominica\">Dominica</option>\n" +
                "                <option value=\"Dominican Republic\">Dominican Republic</option>\n" +
                "                <option value=\"Ecuador\">Ecuador</option>\n" +
                "                <option value=\"Egypt\">Egypt</option>\n" +
                "                <option value=\"El Salvador\">El Salvador</option>\n" +
                "                <option value=\"Equatorial Guinea\">Equatorial Guinea</option>\n" +
                "                <option value=\"Eritrea\">Eritrea</option>\n" +
                "                <option value=\"Estonia\">Estonia</option>\n" +
                "                <option value=\"Ethiopia\">Ethiopia</option>\n" +
                "                <option value=\"Falkland Islands (Malvinas)\">Falkland Islands (Malvinas)</option>\n" +
                "                <option value=\"Faroe Islands\">Faroe Islands</option>\n" +
                "                <option value=\"Fiji\">Fiji</option>\n" +
                "                <option value=\"Finland\">Finland</option>\n" +
                "                <option value=\"France\">France</option>\n" +
                "                <option value=\"French Guiana\">French Guiana</option>\n" +
                "                <option value=\"French Polynesia\">French Polynesia</option>\n" +
                "                <option value=\"French Southern Territories\">French Southern Territories</option>\n" +
                "                <option value=\"Gabon\">Gabon</option>\n" +
                "                <option value=\"Gambia\">Gambia</option>\n" +
                "                <option value=\"Georgia\">Georgia</option>\n" +
                "                <option value=\"Germany\">Germany</option>\n" +
                "                <option value=\"Ghana\">Ghana</option>\n" +
                "                <option value=\"Gibraltar\">Gibraltar</option>\n" +
                "                <option value=\"Greece\">Greece</option>\n" +
                "                <option value=\"Greenland\">Greenland</option>\n" +
                "                <option value=\"Grenada\">Grenada</option>\n" +
                "                <option value=\"Guadeloupe\">Guadeloupe</option>\n" +
                "                <option value=\"Guam\">Guam</option>\n" +
                "                <option value=\"Guatemala\">Guatemala</option>\n" +
                "                <option value=\"Guernsey\">Guernsey</option>\n" +
                "                <option value=\"Guinea\">Guinea</option>\n" +
                "                <option value=\"Guinea-bissau\">Guinea-bissau</option>\n" +
                "                <option value=\"Guyana\">Guyana</option>\n" +
                "                <option value=\"Haiti\">Haiti</option>\n" +
                "                <option value=\"Heard Island and Mcdonald Islands\">Heard Island and Mcdonald Islands</option>\n" +
                "                <option value=\"Holy See (Vatican City State)\">Holy See (Vatican City State)</option>\n" +
                "                <option value=\"Honduras\">Honduras</option>\n" +
                "                <option value=\"Hong Kong\">Hong Kong</option>\n" +
                "                <option value=\"Hungary\">Hungary</option>\n" +
                "                <option value=\"Iceland\">Iceland</option>\n" +
                "                <option value=\"India\">India</option>\n" +
                "                <option value=\"Indonesia\">Indonesia</option>\n" +
                "                <option value=\"Iran, Islamic Republic of\">Iran, Islamic Republic of</option>\n" +
                "                <option value=\"Iraq\">Iraq</option>\n" +
                "                <option value=\"Ireland\">Ireland</option>\n" +
                "                <option value=\"Isle of Man\">Isle of Man</option>\n" +
                "                <option value=\"Italy\">Italy</option>\n" +
                "                <option value=\"Jamaica\">Jamaica</option>\n" +
                "                <option value=\"Japan\">Japan</option>\n" +
                "                <option value=\"Jersey\">Jersey</option>\n" +
                "                <option value=\"Jordan\">Jordan</option>\n" +
                "                <option value=\"Kazakhstan\">Kazakhstan</option>\n" +
                "                <option value=\"Kenya\">Kenya</option>\n" +
                "                <option value=\"Kiribati\">Kiribati</option>\n" +
                "                <option value=\"Korea, Democratic Peoples Republic of\">Korea, Democratic People's Republic of</option>\n" +
                "                <option value=\"Korea, Republic of\">Korea, Republic of</option>\n" +
                "                <option value=\"Kuwait\">Kuwait</option>\n" +
                "                <option value=\"Kyrgyzstan\">Kyrgyzstan</option>\n" +
                "                <option value=\"Lao Peoples Democratic Republic\">Lao People's Democratic Republic</option>\n" +
                "                <option value=\"Latvia\">Latvia</option>\n" +
                "                <option value=\"Lebanon\">Lebanon</option>\n" +
                "                <option value=\"Lesotho\">Lesotho</option>\n" +
                "                <option value=\"Liberia\">Liberia</option>\n" +
                "                <option value=\"Libyan Arab Jamahiriya\">Libyan Arab Jamahiriya</option>\n" +
                "                <option value=\"Liechtenstein\">Liechtenstein</option>\n" +
                "                <option value=\"Lithuania\">Lithuania</option>\n" +
                "                <option value=\"Luxembourg\">Luxembourg</option>\n" +
                "                <option value=\"Macao\">Macao</option>\n" +
                "                <option value=\"Macedonia, The Former Yugoslav Republic of\">Macedonia, The Former Yugoslav Republic of</option>\n" +
                "                <option value=\"Madagascar\">Madagascar</option>\n" +
                "                <option value=\"Malawi\">Malawi</option>\n" +
                "                <option value=\"Malaysia\">Malaysia</option>\n" +
                "                <option value=\"Maldives\">Maldives</option>\n" +
                "                <option value=\"Mali\">Mali</option>\n" +
                "                <option value=\"Malta\">Malta</option>\n" +
                "                <option value=\"Marshall Islands\">Marshall Islands</option>\n" +
                "                <option value=\"Martinique\">Martinique</option>\n" +
                "                <option value=\"Mauritania\">Mauritania</option>\n" +
                "                <option value=\"Mauritius\">Mauritius</option>\n" +
                "                <option value=\"Mayotte\">Mayotte</option>\n" +
                "                <option value=\"Mexico\">Mexico</option>\n" +
                "                <option value=\"Micronesia, Federated States of\">Micronesia, Federated States of</option>\n" +
                "                <option value=\"Moldova, Republic of\">Moldova, Republic of</option>\n" +
                "                <option value=\"Monaco\">Monaco</option>\n" +
                "                <option value=\"Mongolia\">Mongolia</option>\n" +
                "                <option value=\"Montenegro\">Montenegro</option>\n" +
                "                <option value=\"Montserrat\">Montserrat</option>\n" +
                "                <option value=\"Morocco\">Morocco</option>\n" +
                "                <option value=\"Mozambique\">Mozambique</option>\n" +
                "                <option value=\"Myanmar\">Myanmar</option>\n" +
                "                <option value=\"Namibia\">Namibia</option>\n" +
                "                <option value=\"Nauru\">Nauru</option>\n" +
                "                <option value=\"Nepal\">Nepal</option>\n" +
                "                <option value=\"Netherlands\">Netherlands</option>\n" +
                "                <option value=\"Netherlands Antilles\">Netherlands Antilles</option>\n" +
                "                <option value=\"New Caledonia\">New Caledonia</option>\n" +
                "                <option value=\"New Zealand\">New Zealand</option>\n" +
                "                <option value=\"Nicaragua\">Nicaragua</option>\n" +
                "                <option value=\"Niger\">Niger</option>\n" +
                "                <option value=\"Nigeria\">Nigeria</option>\n" +
                "                <option value=\"Niue\">Niue</option>\n" +
                "                <option value=\"Norfolk Island\">Norfolk Island</option>\n" +
                "                <option value=\"Northern Mariana Islands\">Northern Mariana Islands</option>\n" +
                "                <option value=\"Norway\">Norway</option>\n" +
                "                <option value=\"Oman\">Oman</option>\n" +
                "                <option value=\"Pakistan\">Pakistan</option>\n" +
                "                <option value=\"Palau\">Palau</option>\n" +
                "                <option value=\"Palestinian Territory, Occupied\">Palestinian Territory, Occupied</option>\n" +
                "                <option value=\"Panama\">Panama</option>\n" +
                "                <option value=\"Papua New Guinea\">Papua New Guinea</option>\n" +
                "                <option value=\"Paraguay\">Paraguay</option>\n" +
                "                <option value=\"Peru\">Peru</option>\n" +
                "                <option value=\"Philippines\">Philippines</option>\n" +
                "                <option value=\"Pitcairn\">Pitcairn</option>\n" +
                "                <option value=\"Poland\">Poland</option>\n" +
                "                <option value=\"Portugal\">Portugal</option>\n" +
                "                <option value=\"Puerto Rico\">Puerto Rico</option>\n" +
                "                <option value=\"Qatar\">Qatar</option>\n" +
                "                <option value=\"Reunion\">Reunion</option>\n" +
                "                <option value=\"Romania\">Romania</option>\n" +
                "                <option value=\"Russian Federation\">Russian Federation</option>\n" +
                "                <option value=\"Rwanda\">Rwanda</option>\n" +
                "                <option value=\"Saint Helena\">Saint Helena</option>\n" +
                "                <option value=\"Saint Kitts and Nevis\">Saint Kitts and Nevis</option>\n" +
                "                <option value=\"Saint Lucia\">Saint Lucia</option>\n" +
                "                <option value=\"Saint Pierre and Miquelon\">Saint Pierre and Miquelon</option>\n" +
                "                <option value=\"Saint Vincent and The Grenadines\">Saint Vincent and The Grenadines</option>\n" +
                "                <option value=\"Samoa\">Samoa</option>\n" +
                "                <option value=\"San Marino\">San Marino</option>\n" +
                "                <option value=\"Sao Tome and Principe\">Sao Tome and Principe</option>\n" +
                "                <option value=\"Saudi Arabia\">Saudi Arabia</option>\n" +
                "                <option value=\"Senegal\">Senegal</option>\n" +
                "                <option value=\"Serbia\">Serbia</option>\n" +
                "                <option value=\"Seychelles\">Seychelles</option>\n" +
                "                <option value=\"Sierra Leone\">Sierra Leone</option>\n" +
                "                <option value=\"Singapore\">Singapore</option>\n" +
                "                <option value=\"Slovakia\">Slovakia</option>\n" +
                "                <option value=\"Slovenia\">Slovenia</option>\n" +
                "                <option value=\"Solomon Islands\">Solomon Islands</option>\n" +
                "                <option value=\"Somalia\">Somalia</option>\n" +
                "                <option value=\"South Africa\">South Africa</option>\n" +
                "                <option value=\"South Georgia and The South Sandwich Islands\">South Georgia and The South Sandwich Islands</option>\n" +
                "                <option value=\"Spain\">Spain</option>\n" +
                "                <option value=\"Sri Lanka\">Sri Lanka</option>\n" +
                "                <option value=\"Sudan\">Sudan</option>\n" +
                "                <option value=\"Suriname\">Suriname</option>\n" +
                "                <option value=\"Svalbard and Jan Mayen\">Svalbard and Jan Mayen</option>\n" +
                "                <option value=\"Swaziland\">Swaziland</option>\n" +
                "                <option value=\"Sweden\">Sweden</option>\n" +
                "                <option value=\"Switzerland\">Switzerland</option>\n" +
                "                <option value=\"Syrian Arab Republic\">Syrian Arab Republic</option>\n" +
                "                <option value=\"Taiwan, Province of China\">Taiwan, Province of China</option>\n" +
                "                <option value=\"Tajikistan\">Tajikistan</option>\n" +
                "                <option value=\"Tanzania, United Republic of\">Tanzania, United Republic of</option>\n" +
                "                <option value=\"Thailand\">Thailand</option>\n" +
                "                <option value=\"Timor-leste\">Timor-leste</option>\n" +
                "                <option value=\"Togo\">Togo</option>\n" +
                "                <option value=\"Tokelau\">Tokelau</option>\n" +
                "                <option value=\"Tonga\">Tonga</option>\n" +
                "                <option value=\"Trinidad and Tobago\">Trinidad and Tobago</option>\n" +
                "                <option value=\"Tunisia\">Tunisia</option>\n" +
                "                <option value=\"Turkey\">Turkey</option>\n" +
                "                <option value=\"Turkmenistan\">Turkmenistan</option>\n" +
                "                <option value=\"Turks and Caicos Islands\">Turks and Caicos Islands</option>\n" +
                "                <option value=\"Tuvalu\">Tuvalu</option>\n" +
                "                <option value=\"Uganda\">Uganda</option>\n" +
                "                <option value=\"Ukraine\">Ukraine</option>\n" +
                "                <option value=\"United Arab Emirates\">United Arab Emirates</option>\n" +
                "                <option value=\"United Kingdom\">United Kingdom</option>\n" +
                "                <option value=\"United States\">United States</option>\n" +
                "                <option value=\"United States Minor Outlying Islands\">United States Minor Outlying Islands</option>\n" +
                "                <option value=\"Uruguay\">Uruguay</option>\n" +
                "                <option value=\"Uzbekistan\">Uzbekistan</option>\n" +
                "                <option value=\"Vanuatu\">Vanuatu</option>\n" +
                "                <option value=\"Venezuela\">Venezuela</option>\n" +
                "                <option value=\"Viet Nam\">Viet Nam</option>\n" +
                "                <option value=\"Virgin Islands, British\">Virgin Islands, British</option>\n" +
                "                <option value=\"Virgin Islands, U.S.\">Virgin Islands, U.S.</option>\n" +
                "                <option value=\"Wallis and Futuna\">Wallis and Futuna</option>\n" +
                "                <option value=\"Western Sahara\">Western Sahara</option>\n" +
                "                <option value=\"Yemen\">Yemen</option>\n" +
                "                <option value=\"Zambia\">Zambia</option>\n" +
                "                <option value=\"Zimbabwe\">Zimbabwe</option>\n";
    }

    public  String insertPageBody(String searchBox,String geo)
    {
        String countries=insertCountries();
        return "            <form action=\"SearchRequest\" method=\"POST\" id=\"SearchRequest\"><input id=\"searchBox\" value='"+searchBox+"' required name=\"searchBox\" class=\"input2\" type=\"text\">\n" +
                "\n" +
                "           <img id=\"voiceSearch\" src=\"./mic.png\" style=\"width:20px;margin-top:-18px;\" alt=\"mic image\" >\n" +
                "           <img src=\"./search.png\" style=\"width:24px;margin-top:-18px;\" alt=\"search image\" > \n" +
                "           \n" +
                "        </div>\n" +
                "        <div id='message' style=\"text-align: left;display: block;margin: 0px 0 0px 180px;\"></div>\n" +
                "            <select id=\"geographicalLocation\" name=\"geographicalLocation\" style=\"width: 350px;margin: -60px 0 0 720px;height: 40px;color: grey;position: absolute;border:none;\">\\n\">"+
                countries+
                "</select>\n" +
                "        <div class=\"toolbar\">\n" +
                "            <div onClick=\"toggleText()\" class=\"page btn-sm btn-info toolbar-item\">Text   </div>\n" +
                "            <div onClick=\"toggleImages()\" class=\"page btn-sm btn-info toolbar-item \"> Images </div>\n" +
                "            <div onClick=\"toggleTrends()\" class=\"page btn-sm btn-info toolbar-item\"> Trends </div>\n" +
                "            \n" +
                "        </div>\n" +
                "        <button type=\"submit\" class=\"page btn-sm btn-info btn-danger\" style=\"margin-left: 250px;height: 30px;\" >Search</button>\n" +
                "<input type=\"text\" id=\"searchType\" name=\"searchType\" value=\"text\" style=\"visibility: hidden;\"/>"+
                "        \n" +
                "      </form> <div id=\"result-text\">\n" +
        "                <div id=\"result-table-text\"></div>     \n" +
                "                <div id=\"pagination-wrapper-text\"></div>\n" +
                "        </div>\n" +
                "\n" +
                "        <div id=\"result-images\">\n" +
                "                    \n" +
                "                <div id=\"result-table-images\"></div>     \n" +
                "                <div id=\"pagination-wrapper-images\"></div>\n" +
                "        </div>\n" +
                "\n" +
                "        <div id=\"result-trends\">\n" +
                "\n" +
                "        <div id=\"countries\">\n" +
                "            <label for=\"country\">Country</label><span style=\"color: red !important; display: inline; float: none;\">*</span>      \n" +
                "            <select id=\"country\" onChange=\"countryTrendChange()\" name=\"country\" class=\"form-control\">\n" +
                countries+
                "            </select>\n" +
                "        </div>\n" +
                "        <div id=\"rank\">\n" +
                "            <h4>Rank #</h4>\n" +
                "            <h5>1.</h5>\n" +
                "            <h5>2.</h5>\n" +
                "            <h5>3.</h5>\n" +
                "            <h5>4.</h5>\n" +
                "            <h5>5.</h5>\n" +
                "            <h5>6.</h5>\n" +
                "            <h5>7.</h5>\n" +
                "            <h5>8.</h5>\n" +
                "            <h5>9.</h5>\n" +
                "            <h5>10.</h5>\n" +
                "        </div>\n" +
                "\n" +
                "        <div id=\"histogram\">\n" +
                "            <h4>Histogram</h4>\n" +
                "<br><br><br><br><br>\n" +
                "            <canvas id=\"myChart\"></canvas>"+
                "            \n" +
                "        </div>\n" +
                "            \n" +
                "        </div>\n" +
                "       \n" +
                "        \n" +
                "        \n" +
                "        \n" +
                "    <div class=\"footer2\">\n" +
                "    <a class=\"footer-item\" href=\"https://www.google.com/preferences?hl=en\">Settings</a>\n" +
                "    <a class=\"footer-item\" href=\"https://www.google.com/intl/en_eg/ads/?subid=ww-ww-et-g-awa-a-g_hpafoot1_1!o2&amp;utm_source=google.com&amp;utm_medium=referral&amp;utm_campaign=google_hpafooter&amp;fg=1\" >Help</a>\n" +
                "    <a class=\"footer-item\" href=\"https://www.google.com/services/?subid=ww-ww-et-g-awa-a-g_hpbfoot1_1!o2&amp;utm_source=google.com&amp;utm_medium=referral&amp;utm_campaign=google_hpbfooter&amp;fg=1\">Send feeback</a>\n" +
                "    <a class=\"footer-item\" href=\"https://policies.google.com/privacy?fg=1\">Privacy</a>\n" +
                "    <a class=\"footer-item\" href=\"https://policies.google.com/terms?fg=1\">Terms</a>              \n" +
                "    </div>\n" +
                "\n" +
                "    <script src=\"./jQuery.js\"></script>\n" +
                "    <script src=\"./script.js\"></script>\n" +
                "    <script src=\"./voice.js\"></script>\n" +
                "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js\"></script>\n" +
                "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js\"></script>\n"
                +"<script>document.getElementById(\"geographicalLocation\").value='"+geo+"';</script>";
    }

    public String insertPageTextFooter()
    {
        return "<script>"+
                "document.getElementById(\"result-images\").style.display=\"none\";\n" +
                "document.getElementById(\"result-trends\").style.display=\"none\";\n" +
                "document.getElementById(\"result-text\").style.display=\"block\";"+
                "</script></body>\n" +
                "\n" +
                "</html>";
    }

    public String insertPageImagesFooter()
    {
        return "<script>" +
                "document.getElementById(\"result-images\").style.display=\"block\";\n" +
                "document.getElementById(\"result-trends\").style.display=\"none\";\n" +
                "document.getElementById(\"result-text\").style.display=\"none\";"+
                "</script></body>\n" +
                "\n" +
                "</html>";
    }



}
