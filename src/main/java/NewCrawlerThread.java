import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.*;
import java.util.*;

public class NewCrawlerThread extends Thread  implements Runnable {

    private Crawler mycrawler;
    int maxCountUrl=0;
    public String mainsite,extension;

    public NewCrawlerThread (Crawler mycrawler) {
        this.mycrawler = mycrawler;
        maxCountUrl=mycrawler.getMaxCount();
    }

    public void run()
    {
        while (true) {

            try {
                //System.out.println("------ NEW WEBSITE FROM SEED-------");
                repeatForEachPage();
            } catch (MalformedURLException e) {
                 e.printStackTrace();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void repeatForEachPage() throws MalformedURLException, InterruptedException {
        String URLraw=new String();
        boolean isVisited=true;
        Document document=new Document(null);
        String URLnormalized=new String();
        mainsite="";extension="";
        synchronized (mycrawler)
        {
            System.out.println("Count = "+mycrawler.getCount()+" Vector size= "+mycrawler.crawlerStartSeed.size());
            if(!mycrawler.crawlerStartSeed.isEmpty() && mycrawler.getCount()<maxCountUrl)
            {
                URLraw= mycrawler.crawlerStartSeed.get(0).toLowerCase();
                mycrawler.crawlerStartSeed.remove(0);
            }
            else
            {
                System.out.println("finished and recrawling");
                if(!mycrawler.threadEmpty())
                    sleep(10000);

                return;
            }
        }
        URLnormalized = normalize(URLraw);
        isVisited=mycrawler.isVisited(URLnormalized);

        try
        {
            if(URLnormalized!=null && (!isVisited || mycrawler.recrawl==true))
            {
                    URLnormalized=getEnglishVersionWebsite(URLnormalized);
                    document = Jsoup.connect(URLnormalized).get();
                    getSitePlusExtensions(URLnormalized);
                    Long date=getDate(URLnormalized);
                    String location=getLocation(URLnormalized);
                    synchronized (mycrawler)
                    {
                        if (mycrawler.continueMaxCount(mainsite+"/"))
                        {
                            if (!mycrawler.addToVisitedLinks(URLnormalized,date,location))
                            return;
                        } else {mycrawler.removeMainsiteFromVector(mainsite);return;}
                    }

                    System.out.println(Thread.currentThread().getName() + " Added a new link -> " + URLnormalized);
                    if (!readRobotsText(document,URLnormalized))
                    {
                        mycrawler.enterTRUE(URLnormalized);
                        Elements linksOnPage = document.select("a[href]");
                        int outgoingLinks = 0;
                        //For each link found on page go to add in visited links hashset
                        for (Element page : linksOnPage)
                        {
                            String hrefLink=page.attr("abs:href");
                            if(hrefLink!="" && hrefLink!=null && hrefLink!=" ")
                            {
                                mycrawler.addToVector(hrefLink);
                            }
                            outgoingLinks++;
                        }
                        mycrawler.updateOutgoingLinks(URLnormalized, outgoingLinks);
                    }
                        mycrawler.resumeFALSE(URLnormalized);
                       // System.out.println("Website bye bye");
            }
        }catch (IOException  | GeoIp2Exception e)
        {
            System.err.println("For '" + URLnormalized + "': " + e.getMessage()+" or HTML");
            synchronized (mycrawler){mycrawler.enterFALSE(URLnormalized);}
        }
    }

    public long getDate(String url) throws IOException {
        URL conURL = new URL(url);
        HttpURLConnection conH = (HttpURLConnection) conURL.openConnection();
        long date = conH.getLastModified();
        return date;
    }

    public String getLocation(String url) throws IOException, GeoIp2Exception {
        File database = new File("C:/Users/NEW/Documents/GitHub/APT_Project/GeoLite2-Country.mmdb");
        DatabaseReader reader = new DatabaseReader.Builder(database).withCache(new CHMCache()).build();
        InetAddress ip = InetAddress.getByName(new URL(url).getHost());
        CountryResponse response = reader.country(ip);
        Country country = response.getCountry();
        return country.getName();
    }

    public String[] getSitePlusExtensions(String URLraw)
    {
        String extensionsArray[];
        String[] types={".net",".org",".int",".gov",".com"};
        for(int i=0;i<types.length;i++)
        {
            int com=-1;
            com=URLraw.indexOf(types[i]);
            if(com!=-1) {
                mainsite = URLraw.substring(0, com + 4);
                if (URLraw.length() > com + 4)
                    extension = URLraw.substring(com + 5, URLraw.length());
                else
                    extension = "";
            }

        }
        extensionsArray = extension.split("/");
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
        URLnormalized = URLnormalized.replace("/eg-ar/", "/eg-en/");
        return URLnormalized;

    }

    public boolean readRobotsText(Document document,String URLraw)
    {
        int robotDisallowCrawling=0;
        //Robot txt file
        String extensionsArray[]=getSitePlusExtensions(URLraw);

        if(mycrawler.robotsDelay.contains(mainsite))return false;
        try(BufferedReader in = new BufferedReader(new InputStreamReader(new URL(mainsite+"/robots.txt").openStream())))
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
                    if(line.contains("Crawl-delay:"))
                    {System.out.println("e2fesh delay");mycrawler.robotsDelay.add(mainsite);break;}

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
