import java.io.*;
import java.net.*;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Simple login test script that can be run from the container
 * Usage: java LoginTestScript [username_or_email] [password] [host]
 *
 * Credentials are loaded from environment variables (TEST_DEFAULT_USERNAME, TEST_DEFAULT_PASSWORD, TEST_DEFAULT_HOST)
 * or from a .env.test file in the project root (for development only).
 *
 * WARNING: Test credentials are for development/testing purposes only and should NEVER be used in production.
 * If these credentials were ever exposed in version control, they should be rotated immediately.
 */
public class LoginTestScript {

    private static final String ENV_USERNAME = "TEST_DEFAULT_USERNAME";
    private static final String ENV_PASSWORD = "TEST_DEFAULT_PASSWORD";
    private static final String ENV_HOST = "TEST_DEFAULT_HOST";
    private static final String CONFIG_FILE = ".env.test";

    private static String getDefaultUsername() {
        String username = System.getenv(ENV_USERNAME);
        if (username != null && !username.isEmpty()) {
            return username;
        }
        // Try loading from .env.test file
        Properties props = loadConfigFile();
        if (props != null && props.containsKey("TEST_DEFAULT_USERNAME")) {
            return props.getProperty("TEST_DEFAULT_USERNAME");
        }
        throw new IllegalStateException(
            "TEST_DEFAULT_USERNAME not set. Please set environment variable " + ENV_USERNAME +
            " or create a .env.test file with TEST_DEFAULT_USERNAME=<value>"
        );
    }

    private static String getDefaultPassword() {
        String password = System.getenv(ENV_PASSWORD);
        if (password != null && !password.isEmpty()) {
            return password;
        }
        // Try loading from .env.test file
        Properties props = loadConfigFile();
        if (props != null && props.containsKey("TEST_DEFAULT_PASSWORD")) {
            return props.getProperty("TEST_DEFAULT_PASSWORD");
        }
        throw new IllegalStateException(
            "TEST_DEFAULT_PASSWORD not set. Please set environment variable " + ENV_PASSWORD +
            " or create a .env.test file with TEST_DEFAULT_PASSWORD=<value>"
        );
    }

    private static String getDefaultHost() {
        String host = System.getenv(ENV_HOST);
        if (host != null && !host.isEmpty()) {
            return host;
        }
        // Try loading from .env.test file
        Properties props = loadConfigFile();
        if (props != null && props.containsKey("TEST_DEFAULT_HOST")) {
            return props.getProperty("TEST_DEFAULT_HOST");
        }
        // Default fallback for host only
        return "localhost:8080";
    }

    private static Properties loadConfigFile() {
        // Try project root first (for local development)
        File configFile = new File(CONFIG_FILE);
        // Try container working directory (for Docker containers)
        if (!configFile.exists()) {
            configFile = new File("/app/" + CONFIG_FILE);
        }
        // Try current directory as fallback
        if (!configFile.exists()) {
            configFile = new File(System.getProperty("user.dir") + "/" + CONFIG_FILE);
        }

        if (!configFile.exists()) {
            return null;
        }

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            props.load(fis);
            return props;
        } catch (IOException e) {
            System.err.println("Warning: Could not load " + configFile.getPath() + ": " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        String usernameOrEmail = args.length > 0 ? args[0] : getDefaultUsername();
        String password = args.length > 1 ? args[1] : getDefaultPassword();
        String host = args.length > 2 ? args[2] : getDefaultHost();

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
