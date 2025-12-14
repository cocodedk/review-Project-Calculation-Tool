import java.io.*;
import java.net.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Simple login test script that can be run from the container
 * Usage: java LoginTestScript [username_or_email] [password] [host]
 */
public class LoginTestScript {

    private static final String DEFAULT_USERNAME = "your.email+fakedata11617@gmail.com";
    private static final String DEFAULT_PASSWORD = "jUfmv580rz6sOU";
    private static final String DEFAULT_HOST = "localhost:8080";

    public static void main(String[] args) {
        String usernameOrEmail = args.length > 0 ? args[0] : DEFAULT_USERNAME;
        String password = args.length > 1 ? args[1] : DEFAULT_PASSWORD;
        String host = args.length > 2 ? args[2] : DEFAULT_HOST;

        System.out.println("=== Login Test Script ===");
        System.out.println("Username/Email: " + usernameOrEmail);
        System.out.println("Password: " + password.substring(0, Math.min(3, password.length())) + "***");
        System.out.println("Host: " + host);
        System.out.println();

        try {
            // Step 1: Get login page to establish session and get CSRF token
            System.out.println("Step 1: Fetching login page to get CSRF token...");
            String csrfToken = getCsrfToken(host);

            if (csrfToken == null || csrfToken.isEmpty()) {
                System.out.println("Warning: Could not extract CSRF token, trying without it...");
            } else {
                System.out.println("CSRF token extracted: " + csrfToken.substring(0, Math.min(10, csrfToken.length())) + "...");
            }

            // Step 2: Attempt login
            System.out.println();
            System.out.println("Test: Login with email/username");
            boolean success = testLogin(host, usernameOrEmail, password, csrfToken);

            if (success) {
                System.out.println("SUCCESS: Login successful!");
                System.exit(0);
            } else {
                System.out.println("FAILED: Login unsuccessful");
                System.exit(1);
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static String getCsrfToken(String host) throws IOException {
        URL url = new URL("http://" + host + "/login");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setInstanceFollowRedirects(false);

        // Read cookies from response
        String cookies = conn.getHeaderField("Set-Cookie");

        // Read HTML content
        StringBuilder html = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                html.append(line).append("\n");
            }
        }

        // Extract CSRF token from HTML
        Pattern pattern = Pattern.compile("name=\"_csrf\"[^>]*value=\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(html.toString());
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    private static boolean testLogin(String host, String usernameOrEmail,
                                    String password, String csrfToken) throws IOException {
        URL url = new URL("http://" + host + "/validate-login");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // Build POST data
        StringBuilder postData = new StringBuilder();
        if (csrfToken != null && !csrfToken.isEmpty()) {
            postData.append("_csrf=").append(URLEncoder.encode(csrfToken, "UTF-8")).append("&");
        }
        postData.append("username=").append(URLEncoder.encode(usernameOrEmail, "UTF-8"));
        postData.append("&password=").append(URLEncoder.encode(password, "UTF-8"));

        // Send POST data
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = postData.toString().getBytes("UTF-8");
            os.write(input, 0, input.length);
        }

        // Check response
        int responseCode = conn.getResponseCode();
        String location = conn.getHeaderField("Location");

        System.out.println("HTTP Response Code: " + responseCode);
        if (location != null) {
            System.out.println("Redirect Location: " + location);
        }

        // Success if we get a 302 redirect to project/list
        return responseCode == 302 && location != null && location.contains("project/list");
    }
}
