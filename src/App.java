import java.util.Scanner;

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

        String[] hostAndpath = parseURL(url);
        String host = hostAndpath[0];
        String path = hostAndpath[1];
        System.out.println(host);
        System.out.println(path);
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
}
