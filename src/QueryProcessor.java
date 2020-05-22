import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    public class Result
    {
        int id=0;
        int word_id=0;
        int url_id=0;
        int i=0;
        int j=0;
        int num=0;
    }


    private Stemmer stemmer;
    private DbConnect db;
    Vector<Result> results;
    Vector<Integer> distinctWords;
    Vector<Integer> distinctUrls;

    public void searchDatabase(String searchBox)
    {
        results=new Vector<Result>();

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
            if(word_id<=0)continue;
            ResultSet rs=db.getURLS_inCombined(word_id);
            if(rs==null)continue;
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
            System.out.println("Id="+results.get(i).id+" urlID="+results.get(i).url_id+" wordID="+results.get(i).word_id +" I="+results.get(i).i+" J="+results.get(i).j+" Num="+results.get(i).num);
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
        sortPosition();
        sortCount();
        sortImportance();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String searchBox = request.getParameter("searchBox");
        searchDatabase(searchBox);
        ranker();
        String resultsString = "";
        for (int i=0;i<results.size();i++)
        {
            int url_id=results.get(i).url_id;
            String url=db.getURLByID_inURL(url_id);
            int indexWWW=-1;int indexCOM=-1;
            indexWWW=url.indexOf("www.");
            indexCOM=url.lastIndexOf(".");
            String mainsite=url;
            if(indexCOM>indexWWW)
                mainsite=url.substring(indexWWW+1,indexCOM);

            resultsString+="<div class=\"component\">\n" +
                "            <h3> "+url+" </h3>\n" +
                "            <h1> "+mainsite+" </h1>\n" +
                "            <h2> Put info here ... </h2>\n" +
                "            </div>\n" ;

        }

        response.setContentType("text/html");
        String page = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<style>\n" +
                "    .M6hT6 {\n" +
                "        left: 0;\n" +
                "        right: 0;\n" +
                "        -webkit-text-size-adjust: none\n" +
                "    }\n" +
                "\n" +
                "    .fbar p {\n" +
                "        display: inline\n" +
                "    }\n" +
                "\n" +
                "    .fbar a,\n" +
                "    #fsettl {\n" +
                "        text-decoration: none;\n" +
                "        white-space: nowrap\n" +
                "    }\n" +
                "\n" +
                "    .fbar {\n" +
                "        margin-left: -27px\n" +
                "    }\n" +
                "\n" +
                "    .Fx4vi {\n" +
                "        padding-left: 27px;\n" +
                "        margin: 0 !important\n" +
                "    }\n" +
                "\n" +
                "    #fbarcnt {\n" +
                "        display: block;\n" +
                "    }\n" +
                "\n" +
                "    .fmulti {\n" +
                "        text-align: center\n" +
                "    }\n" +
                "\n" +
                "    .fmulti #fsr {\n" +
                "        display: block;\n" +
                "        float: none\n" +
                "    }\n" +
                "\n" +
                "    .fmulti #fuser {\n" +
                "        display: block;\n" +
                "        float: none\n" +
                "    }\n" +
                "\n" +
                "    #fuserm {\n" +
                "        line-height: 25px\n" +
                "    }\n" +
                "\n" +
                "    #fsr {\n" +
                "        float: right;\n" +
                "        white-space: nowrap\n" +
                "    }\n" +
                "\n" +
                "    #fsl {\n" +
                "        white-space: nowrap\n" +
                "    }\n" +
                "\n" +
                "    #fsett {\n" +
                "        background-color: transparent;\n" +
                "        border: 1px solid #999;\n" +
                "        bottom: 30px;\n" +
                "        padding: 10px 0;\n" +
                "        box-shadow: 0 2px 4px rgba(0, 0, 0, .2);\n" +
                "        box-shadow: 0 2px 4px rgba(0, 0, 0, .2);\n" +
                "        text-align: left;\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    #fsett a {\n" +
                "        display: block;\n" +
                "        line-height: 44px;\n" +
                "        padding: 0 20px;\n" +
                "        text-decoration: none;\n" +
                "        white-space: nowrap\n" +
                "    }\n" +
                "\n" +
                "    #fbar {\n" +
                "        background-color: transparent;\n" +
                "        line-height: 40px;\n" +
                "        min-width: 980px;\n" +
                "        border-top: 1px solid #e4e4e4\n" +
                "    }\n" +
                "\n" +
                "    .B4GxFc {\n" +
                "        margin-left: 166px\n" +
                "    }\n" +
                "\n" +
                "    .fbar p,\n" +
                "    .fbar a,\n" +
                "    #fsettl,\n" +
                "    #fsett a {\n" +
                "        color: #5f6368\n" +
                "    }\n" +
                "\n" +
                "    .fbar a:hover,\n" +
                "    #fsett a:hover {\n" +
                "        color: #333\n" +
                "    }\n" +
                "\n" +
                "    .fbar {\n" +
                "        font-size: 14px\n" +
                "    }\n" +
                "\n" +
                "    #fuser {\n" +
                "        float: right\n" +
                "    }\n" +
                "\n" +
                "    .EvHmz {\n" +
                "        bottom: 0;\n" +
                "        left: 0;\n" +
                "        position: absolute;\n" +
                "        right: 0\n" +
                "    }\n" +
                "\n" +
                "    .hRvfYe #fsettl:hover {\n" +
                "        text-decoration: underline\n" +
                "    }\n" +
                "\n" +
                "    .hRvfYe #fsett a:hover {\n" +
                "        text-decoration: underline\n" +
                "    }\n" +
                "\n" +
                "    .hRvfYe a:hover {\n" +
                "        text-decoration: underline\n" +
                "    }\n" +
                "\n" +
                "    #fsl {\n" +
                "        margin-left: 30px;\n" +
                "    }\n" +
                "\n" +
                "    #fsr {\n" +
                "        margin-right: 30px\n" +
                "    }\n" +
                "\n" +
                "    .fmulti #fsl {\n" +
                "        margin-left: 0;\n" +
                "    }\n" +
                "\n" +
                "    .fmulti #fsr {\n" +
                "        margin-right: 0\n" +
                "    }\n" +
                "\n" +
                "    .b0KoTc {\n" +
                "        color: rgba(0, 0, 0, .54);\n" +
                "        padding-left: 27px\n" +
                "    }\n" +
                "\n" +
                "    .Q8LRLc {\n" +
                "        font-size: 15px\n" +
                "    }\n" +
                "\n" +
                "    .b0KoTc {\n" +
                "        margin-left: 30px;\n" +
                "        text-align: left\n" +
                "    }\n" +
                "\n" +
                "    .b2hzT {\n" +
                "        border-bottom: 1px solid #e4e4e4\n" +
                "    }\n" +
                "   \n" +
                "</style>\n" +
                "<link rel=\"icon\" href=\"https://cdn2.iconfinder.com/data/icons/social-icons-33/128/Google-512.png\">\n" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"index.css\">\n" +
                "<link href=\"../Web-Interface/fontawesome/css/all.css\" rel=\"stylesheet\">\n" +
                "<title>Google</title>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "    <img alt=\"Google image\" class=\"img2\"\n" +
                "            src=\"./google.png\">\n" +
                "<form action=\"SearchRequest\" method=\"POST\" id=\"SearchRequest\">"+
                "    <div class=\"searchbox2\">\n" +

                "           <input name=\"searchBox\" value="+searchBox+" id=\"searchBox\" class=\"input2\" type=\"text\">\n"
        +" <img src=\"./search.png\" style=\"width:20px;margin-top:2px\" alt=\"search image\" >"
        +" <img id=\"voiceSearch\" src=\"./mic.png\" style=\"width:18px;\" alt=\"mic image\" >"+

                "        </div>\n"

                +"<div id=\"message2\"></div>"+
                "<div class=\"toolbar\">"
                +" <h2>Text   </h2>"
                +"  <h2> Images </h2>"
                +" </div>"
                +"<button style=\"margin-left: 320px;\" " +
                "type=\"submit\" value=\"Submit\">Search</button>"
                +"</form>"
                +"        <div class=\"result\">\n" +
                "            <div class=\"component\">\n" +
                "            <h3> www.google.com </h3>\n" +
                "            <h1> Google </h1>\n" +
                "            <h2> Search the world's information, including webpages, images, videos and more. Google has many special features to help you find exactly what you're looking ... </h2>\n" +
                "            </div>\n" +
                "\n" +resultsString+
                "        </div>\n" +
                "    <div class=\"footer2\">\n" +
                "\n" +
                "        <div id=\"fbarcnt\" style=\"height: auto; visibility: visible;\">\n" +
                "            <div id=\"footcnt\">\n" +
                "                <div class=\"EvHmz hRvfYe\" id=\"fbar\">\n" +
                "                    <div class=\"fbar\">\n" +
                "                        <span id=\"fsr\">\n" +
                "                      \n" +
                "                           \n" +
                "                                <a class=\"Fx4vi\" href=\"https://www.google.com/preferences?hl=en\" id=\"fsettl\" aria-controls=\"fsett\"aria-expanded=\"false\" aria-haspopup=\"true\" role=\"button\" jsaction=\"foot.cst\">Settings</a>\n" +
                "                                <span id=\"fsett\" aria-labelledby=\"fsettl\" role=\"menu\" style=\"display:none\">\n" +
                "                                    <a href=\"https://www.google.com/preferences?hl=en-EG&amp;fg=1\"\n" +
                "                                        role=\"menuitem\">Search settings</a><a href=\"/advanced_search?hl=en-EG&amp;fg=1\"\n" +
                "                                        role=\"menuitem\">Advanced search</a><a\n" +
                "                                        href=\"//myactivity.google.com/privacyadvisor/search?utm_source=googlemenu&amp;fg=1\"\n" +
                "                                        role=\"menuitem\">Your data in Search</a><a\n" +
                "                                        href=\"//myactivity.google.com/product/search?utm_source=google&amp;hl=en-EG&amp;fg=1\"\n" +
                "                                        role=\"menuitem\">History</a><a\n" +
                "                                        href=\"//support.google.com/websearch/?p=ws_results_help&amp;hl=en-EG&amp;fg=1\"\n" +
                "                                        role=\"menuitem\">Search help</a><a href=\"#\" data-bucket=\"websearch\"\n" +
                "                                        role=\"menuitem\" id=\"dk2qOd\" target=\"_blank\" jsaction=\"gf.sf\"\n" +
                "                                        data-ved=\"0ahUKEwjk4q3b4e7oAhVNCxoKHZGRDzcQLggW\">Send feedback</a></span></span><span id=\"fsl\">\n" +
                "                                <a class=\"Fx4vi\" href=\"https://www.google.com/intl/en_eg/ads/?subid=ww-ww-et-g-awa-a-g_hpafoot1_1!o2&amp;utm_source=google.com&amp;utm_medium=referral&amp;utm_campaign=google_hpafooter&amp;fg=1\" >Help</a>\n" +
                "                                <a class=\"Fx4vi\" href=\"https://www.google.com/services/?subid=ww-ww-et-g-awa-a-g_hpbfoot1_1!o2&amp;utm_source=google.com&amp;utm_medium=referral&amp;utm_campaign=google_hpbfooter&amp;fg=1\">Send feeback</a>\n" +
                "                                \n" +
                "                                <a class=\"Fx4vi\" href=\"https://policies.google.com/privacy?fg=1\">Privacy</a>\n" +
                "                            <a class=\"Fx4vi\" href=\"https://policies.google.com/terms?fg=1\">Terms</a>\n" +
                "                                </span>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "<script>\n" +
                "        var message = document.querySelector('#message2');\n" +
                "\n" +
                "        var SpeechRecognition = SpeechRecognition || webkitSpeechRecognition;\n" +
                "        var SpeechGrammarList = SpeechGrammarList || webkitSpeechGrammarList;\n" +
                "\n" +
                "        var grammar = '#JSGF V1.0;'\n" +
                "\n" +
                "        var recognition = new SpeechRecognition();\n" +
                "        var speechRecognitionList = new SpeechGrammarList();\n" +
                "        speechRecognitionList.addFromString(grammar, 1);\n" +
                "        recognition.grammars = speechRecognitionList;\n" +
                "        recognition.lang = 'en-US';\n" +
                "        recognition.interimResults = false;\n" +
                "\n" +
                "        recognition.onresult = function(event) {\n" +
                "            var last = event.results.length - 1;\n" +
                "            var command = event.results[last][0].transcript;\n" +
                "\n" +
                "            document.getElementById(\"searchBox\").value = command.toLowerCase();\n" +
                "        };\n" +
                "\n" +
                "        recognition.onspeechend = function() {\n" +
                "            recognition.stop();\n" +
                "            document.getElementById(\"voiceSearch\").src = \"./mic.png\";\n" +
                "        };\n" +
                "\n" +
                "        recognition.onerror = function(event) {\n" +
                "            message.textContent = 'Error occurred in recognition: ' + event.error;\n" +
                "            document.getElementById(\"voiceSearch\").src = \"./mic.png\";\n" +
                "        }        \n" +
                "\n" +
                "        document.querySelector('#voiceSearch').addEventListener('click', function(){\n" +
                "            document.getElementById(\"searchBox\").value = \"\";\n" +
                "            message.textContent = '';\n" +
                "            recognition.start();\n" +
                "            document.getElementById(\"voiceSearch\").src = \"./dots.png\";\n" +
                "\n" +
                "        });\n" +
                "\n" +
                "        document.querySelector('#searchBox').addEventListener('click', function(){\n" +
                "            message.textContent = '';\n" +
                "            \n" +
                "        });\n" +
                "\n" +
                "\n" +
                "    </script>"+
                "</body>\n" +
                "\n" +
                "</html>";

        response.getWriter().println(page);

    }



}
