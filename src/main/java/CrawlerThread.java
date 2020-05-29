import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;

public class CrawlerThread extends Thread  implements Runnable {

    private Crawler mycrawler;
    public String mainsite,extension;

    public CrawlerThread (Crawler mycrawler) {
        this.mycrawler = mycrawler;
    }

    public void run()
    {
        while (true) {

            try {

                repeatForEachPage(0,"");
                System.out.println("------ NEW WEBSITE FROM SEED -------");
            } catch (MalformedURLException e) {
                // e.printStackTrace();
            }
        }
    }


    public void repeatForEachPage(int level,String link) throws MalformedURLException
    {
        String URLraw=new String();
        boolean isVisited=false;
        Document document=new Document(null);
        String URLnormalized;

        if (level>=2) {synchronized (mycrawler) {mycrawler.resumeFALSE(link);}return;}


        if(link==null || link=="")
        {
            synchronized (mycrawler) {
                if (mycrawler.getCount() >= mycrawler.getMaxCount())
                {
                    Thread.currentThread().stop();
                }
                else
                {
                    int c = 0;

                    for (int i = 0; i < mycrawler.crawlerStartSeed.size(); i++)
                    {
                        if (mycrawler.crawlerStartSeed.get(i).threadEntered == false)
                        {
                            URLraw = mycrawler.crawlerStartSeed.get(i).url;
                            mycrawler.crawlerStartSeed.get(i).threadEntered = true;
                            isVisited = mycrawler.isVisited(URLraw);
                            URLraw = URLraw.toLowerCase();
                            c++;
                            break;
                        } else c++;


                    }
                    //kolohom true w yebda2 ye3eed ml awel
                    if (c >= mycrawler.crawlerStartSeed.size())
                    {
                        System.out.println("Crawler: Finished seed and started allover again");
                        for (int i = 0; i < mycrawler.crawlerStartSeed.size(); i++) {
                            mycrawler.crawlerStartSeed.get(i).threadEntered = false;
                        }
                    }

                }
            }
        }else URLraw=link;

        URLraw=URLraw.toLowerCase();
        URLnormalized = normalize(URLraw);

        try
        {
            if(URLnormalized!=null)
            {
                URLnormalized=getEnglishVersionWebsite(URLnormalized);
                document = Jsoup.connect(URLnormalized).get();

                if (!isVisited)
                {
                    int outgoingLinks = 0;

                    getSitePlusExtensions(URLnormalized);
                    synchronized (mycrawler) {
                        if (mycrawler.continueMaxCount(mainsite)) {
                            if (mycrawler.addToVisitedLinks(URLnormalized))
                                System.out.println(Thread.currentThread().getName() + " Added a new link -> " + URLnormalized);
                        } else {mycrawler.resumeFALSE(URLnormalized);return;}
                    }

                    if (!readRobotsText(document,URLnormalized))
                    {
                        synchronized (mycrawler) {mycrawler.enterTRUE(URLnormalized); }
                        Elements linksOnPage = document.select("a[href]");

                        //For each link found on page go to add in visited links hashset

                        for (Element page : linksOnPage)
                        {
                            String hrefLink=page.attr("abs:href");
                            if(hrefLink!="" && hrefLink!=null && hrefLink!=" ")
                            {
                                synchronized (mycrawler)
                                {
                                    if (!mycrawler.isVisited(hrefLink)) {
                                        hrefLink = normalize(hrefLink);
                                        hrefLink = getEnglishVersionWebsite(hrefLink);
                                        if (mycrawler.continueMaxCount(mainsite)) {
                                            if (mycrawler.addToVisitedLinks(hrefLink))
                                                System.out.println(Thread.currentThread().getName() + " Added a new link -> " + hrefLink);
                                        } else {mycrawler.resumeFALSE(URLnormalized);return;}
                                        mycrawler.enterTRUE(hrefLink);
                                    }
                                }
                                outgoingLinks++;
                            }
                        }

                        synchronized (mycrawler)
                        {
                            mycrawler.updateOutgoingLinks(URLnormalized, outgoingLinks);
                            mycrawler.resumeFALSE(URLnormalized);
                        }

                        for (Element page : linksOnPage)
                        {
                            repeatForEachPage(level+1,page.attr("abs:href"));
                        }

                    }
                }
                {synchronized (mycrawler) {mycrawler.resumeFALSE(URLnormalized);}return;}
            }

        }catch (IOException e) {

            System.err.println("For '" + URLnormalized + "': " + e.getMessage()+" or HTML");
            synchronized (mycrawler){mycrawler.enterFALSE(URLnormalized);}

        }
    }


    public String[] getSitePlusExtensions(String URLraw)
    {

        String extensionsArray[]=new String[]{};

        int com=-1;
        com=-1;
        com=URLraw.indexOf(".net");
        if (com !=-1)
        {
            mainsite= URLraw.substring(0 , com+4); //this will give abc
            if(URLraw.length()>com+4)
                extension=URLraw.substring(com+5 , URLraw.length());
            else
                extension="";

        }
        com=-1;
        com=URLraw.indexOf(".org");
        if (com !=-1)
        {
            mainsite= URLraw.substring(0 , com+4); //this will give abc
            if(URLraw.length()>com+4)
                extension=URLraw.substring(com+5 , URLraw.length());
            else
                extension="";

        }
        com=URLraw.indexOf(".com");
        if (com !=-1)
        {
            mainsite= URLraw.substring(0 , com+4); //this will give abc
            if(URLraw.length()>com+4)
                extension=URLraw.substring(com+5 , URLraw.length());
            else
                extension="";

        }
        extensionsArray = extension.split("[\"]+");
        return extensionsArray;

    }

    public String getEnglishVersionWebsite(String URLnormalized)
    {
        int firstDot = -1;
        firstDot = URLnormalized.indexOf(".");
        int lastDot = firstDot;
        lastDot = URLnormalized.lastIndexOf(".");
        if (firstDot != -1 && lastDot != firstDot) {
            URLnormalized = URLnormalized.substring(firstDot + 1, URLnormalized.length());
            URLnormalized = "https://www." + URLnormalized;
        }

        URLnormalized = URLnormalized.replace("/ar/", "/");
        return URLnormalized;

    }

    public boolean readRobotsText(Document document,String URLraw)
    {
        int robotDisallowCrawling=0;

        //Robot txt file
        String extensionsArray[]=getSitePlusExtensions(URLraw);
        try(BufferedReader in = new BufferedReader(

                new InputStreamReader(new URL(mainsite+"/robots.txt").openStream())))
        {
            String line = null;boolean talkingToMyAgent=false;

            while((line = in.readLine()) != null)
            {

                if(line.contains("User-agent: *"))
                    talkingToMyAgent=true;
                else
                {
                    if(line.contains("User-agent:"))
                        talkingToMyAgent=false;
                }


                if(talkingToMyAgent==true)
                {
                    //DISALLOW ALL
                    if(line.equals("Disallow: /"))
                    {
                        robotDisallowCrawling++;
                        break;
                    }
                    //DISALLOW CERTAIN PATHS
                    for (int i=0;i<extensionsArray.length;i++)
                    {
                        if (line.equals("Disallow: /"+ extensionsArray[i]) )
                        {
                            robotDisallowCrawling++;
                            break;
                        }
                        if(extension!=null && extension!="" && extension!=" " )
                        {
                            line=line.substring(line.indexOf("/")+1,line.length());
                            if(extension.contains(line))
                            {
                                robotDisallowCrawling++;
                                break;
                            }
                        }
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Robot metadata
        if(document!=null ) {
            if(robotDisallowCrawling<=0) {
                Elements metaTags = document.getElementsByTag("meta");

                for (Element metaTag : metaTags) {
                    String content = metaTag.attr("content");
                    if (content.contains("NOFOLLOW") || content.contains("nofollow")) {
                        robotDisallowCrawling++;
                        break;
                    }
                }
            }
        }

        if(robotDisallowCrawling>0)
            return true;
        else
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



}
