// author: Ben Yang
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.*;

public class App {
    public static void main(String[] args) throws Exception 
    {

        if (args.length != 2) 
        {
            System.err.println("Syntax error: only takes in url and maxhop");
            return;
        }

        int maxHop;
        String url;
        try {
            url = args[0];
            maxHop = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e) {
            System.err.println("Error: cannot convert second arg to int");
            return;
        }

        //String getReq = url2GetRequest(url);
        //System.out.println(getReq);
        String currWeb = new String();
        int currIndex = 1;
        for(int i = 0; i < maxHop; i++)
        {
            System.out.println("Hop " + i + " :\nURL: " + url);
            String response = sendHttpGETRequest(url);
            if (!response.equals("invalid webpage")) 
            {
                currWeb = response;
                currIndex = 1;
            }
            else if (i == 0)
            {
                System.err.println("invalid initial url");
                System.exit(0);
            }
            String nextURL = findNextHref(currWeb,currIndex);
            if (!nextURL.equals(new String("notFound")))
            {
                url = nextURL;
            }
            currIndex++;
        }
        String response = sendHttpGETRequest(url);
    }

    public static String findNextHref(String response, int index)
    {
        String regex = "<a[^>]*?href=\"([^\"]+)\"";
        // Compile the pattern
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        // Create a matcher to find matches in the HTML string
        Matcher matcher = pattern.matcher(response);
        
        // Loop through matches and print the captured URLs
        String url = new String();
        for (int i = 0; i < index; i++)
        {
            if (matcher.find())
            {
                url = matcher.group(1);
                while (!url.contains("http"))
                {
                    if (matcher.find())
                    {
                        url = matcher.group(1);
                    }
                    else
                    {
                        url = new String("notFound");
                        break;
                    }
                }
            }
            else 
            {
                url = new String("notFound");
            }
        }
        System.out.println("Extracted URL: " + url);  
        return url;
    }

    private static String sendHttpGETRequest(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();
        try
        {
            httpURLConnection.setRequestMethod("GET");
            int responseCode = httpURLConnection.getResponseCode();
            System.out.println("GET Response Code :: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == 301) { // success
                System.out.println("successfully get response");
                BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
    
                while ((inputLine = in .readLine()) != null) 
                {
                    response.append(inputLine);
                } 
                in .close();
                String responseString = response.toString();
                return responseString;
            } else {
                if(responseCode == 404) 
                {
                    System.out.println("The requested page does not exist");
                    return new String("invalid webpage");
                }
                System.out.println("GET request is not valid");
                return new String("invalid webpage");
            }
        }
        catch(Exception e)
        {
            System.out.println("Unexpected error: " + e.toString());
            return new String("invalid webpage");
        }
    }

    public static String[] parseURL(String url)
    {
        String[] content = new String[2];
        String protocol = new String("https://");
        if (!url.contains("https://")){
            System.err.println("parseURL Error: wrong protocol name");
            System.exit(0);
        }
        Scanner scanner = new Scanner(url);
        scanner.useDelimiter(protocol);
        String hostWithPath = scanner.next();

        String defaultPath = new String("index.html");

        if (!hostWithPath.contains("/")) {
            content[0] = hostWithPath;
            content[1] = new String(defaultPath);
            return content;
        }
        
        scanner = new Scanner(hostWithPath);
        scanner.useDelimiter("/");
        String host = scanner.next();
        content[0] = host;
        if (host.length() == hostWithPath.length()) // use default path which is index.html
        {
            content[1] = new String(defaultPath);
        }
        else{
            String path = hostWithPath.substring(host.length());
            content[1] = path;
        }
        return content;
    }

    public static String url2GetRequest(String url)
    {
        String[] hostAndpath = parseURL(url);
        String host = hostAndpath[0];
        String path = hostAndpath[1];
        String getReq = new String("GET " + path + " HTTP/1.1\nHost: " + host);
        return getReq;
    }
}
