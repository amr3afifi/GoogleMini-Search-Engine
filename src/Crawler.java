import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;

public class Crawler {

    private int URLsCount=0;
    private DbConnect db;
    private final int maxCount=5000;
    private int threadCounter=0;
    Vector<crawlSite> crawlerStartSeed;

    public class crawlSite
    {
        public String url="";
        public boolean threadEntered=false;
    }


    public Crawler(DbConnect db) {
        this.db = db;
        crawlerStartSeed=new Vector();

        String[] websites = {
                "https://www.youtube.com/", "https://www.facebook.com/", "https://www.twitter.com/", "https://www.google.com/", "https://www.linkedin.com/", "https://www.instagram.com/",
                "https://www.amazon.com/", "https://www.ebay.com/", "https://www.gsmarena.com/", "https://www.stackoverflow.com/", "https://www.365scores.com/", "https://www.bodybuilding.com/",
                 "https://www.bose.com/", "https://www.tesla.com/", "https://www.apple.com/", "https://www.wikipedia.org/", "https://www.nytimes.com/", "https://www.forbes.com/",
                "https://www.netflix.com/"
        };

        for(int i=0;i<websites.length;i++)
        {
            crawlSite temp=new crawlSite();
            temp.url=websites[i];
            crawlerStartSeed.add(temp);
        }
    }

    public int getCount () {
        return URLsCount;
    }

    public final int getMaxCount() {
        return maxCount;
    }

    public boolean isVisited(String URL)
    {
        int id=db.findURL_inURL(URL);
        if (id>0) {
            System.out.println("visited");
            db.updateIngoing_inURL(id);
            return true;
        }
        else {

            System.out.println("not visited");
            return false;
        }
    }

    public boolean addToVisitedLinks(String URL)
    {

        if(!isVisited(URL))
        {
            URLsCount++;
            db.addURL_toURL(URL);
            return true;

        }
        return false;
    }

    public String normalize( String taintedURL) throws MalformedURLException
    {
        final URL url;
        try
        {
            url = new URI(taintedURL).normalize().toURL();
        }
        catch (URISyntaxException e) {
            throw new MalformedURLException(e.getMessage());
        }

        final String path = url.getPath().replace("/$", "");
        final SortedMap<String, String> params = createParameterMap(url.getQuery());
        final int port = url.getPort();
        final String queryString;

        if (params != null)
        {
            // Some params are only relevant for user tracking, so remove the most commons ones.
            for (Iterator<String> i = params.keySet().iterator(); i.hasNext();)
            {
                final String key = i.next();
                if (key.startsWith("utm_") || key.contains("session"))
                {
                    i.remove();
                }
            }
            queryString = "?" + canonicalize(params);
        }
        else
        {
            queryString = "";
        }

        return url.getProtocol() + "://" + url.getHost()
                + (port != -1 && port != 80 ? ":" + port : "")
                + path + queryString;
    }

    /**
     * Takes a query string, separates the constituent name-value pairs, and
     * stores them in a SortedMap ordered by lexicographical order.
     * @return Null if there is no query string.
     */
    private static SortedMap<String, String> createParameterMap(final String queryString)
    {
        if (queryString == null || queryString.isEmpty())
        {
            return null;
        }

        final String[] pairs = queryString.split("&");
        final Map<String, String> params = new HashMap<String, String>(pairs.length);

        for (final String pair : pairs)
        {
            if (pair.length() < 1)
            {
                continue;
            }

            String[] tokens = pair.split("=", 2);
            for (int j = 0; j < tokens.length; j++)
            {
                try
                {
                    tokens[j] = URLDecoder.decode(tokens[j], "UTF-8");
                }
                catch (UnsupportedEncodingException ex)
                {
                    ex.printStackTrace();
                }
            }
            switch (tokens.length)
            {
                case 1:
                {
                    if (pair.charAt(0) == '=')
                    {
                        params.put("", tokens[0]);
                    }
                    else
                    {
                        params.put(tokens[0], "");
                    }
                    break;
                }
                case 2:
                {
                    params.put(tokens[0], tokens[1]);
                    break;
                }
            }
        }

        return new TreeMap<String, String>(params);
    }

    /**
     * Canonicalize the query string.
     *
     * @param sortedParamMap Parameter name-value pairs in lexicographical order.
     * @return Canonical form of query string.
     */
    private static String canonicalize(final SortedMap<String, String> sortedParamMap)
    {
        if (sortedParamMap == null || sortedParamMap.isEmpty())
        {
            return "";
        }

        final StringBuffer sb = new StringBuffer(350);
        final Iterator<Map.Entry<String, String>> iter = sortedParamMap.entrySet().iterator();

        while (iter.hasNext())
        {
            final Map.Entry<String, String> pair = iter.next();
            sb.append(percentEncodeRfc3986(pair.getKey()));
            sb.append('=');
            sb.append(percentEncodeRfc3986(pair.getValue()));
            if (iter.hasNext())
            {
                sb.append('&');
            }
        }

        return sb.toString();
    }

    /**
     * Percent-encode values according the RFC 3986. The built-in Java URLEncoder does not encode
     * according to the RFC, so we make the extra replacements.
     *
     * @param string Decoded string.
     * @return Encoded string per RFC 3986.
     */
    private static String percentEncodeRfc3986(final String string)
    {
        try
        {
            return URLEncoder.encode(string, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
        }
        catch (UnsupportedEncodingException e)
        {
            return string;
        }
    }

    public boolean updateOutgoingLinks(String URL,int num)
    {
        int id=db.findURL_inURL(URL);
        if(id<0)
            return false;

        db.updateOutgoing_inURL(id,num);
            return true;


    }

    public void addThread()
    {
        CrawlerThread crawlerThread=new CrawlerThread(this);
        threadCounter++;
        String threadname="thread"+Integer.toString(threadCounter);
        Thread t=new Thread(crawlerThread,threadname);
        System.out.println(t);
        t.start();


    }

    public void addThread(int n)
    {
        System.out.println(this);
        for (int i=0;i<n;i++)
            addThread();
    }

}




