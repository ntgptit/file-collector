package com.example.java_js;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ES6FetchExample {
    private static final Logger logger = LoggerFactory.getLogger(ES6FetchExample.class);

    /**
     * Fetches the content from the given URL as a String.
     *
     * @param url   The URL to fetch content from.
     * @param token The token to set in the Authorization header.
     * @return The content of the URL as a String.
     */
    public static String fetchFromJava(String url, String token) {
        HttpURLConnection con = null;
        try {
            // Create URL object and open connection
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            // Set Authorization header if token is provided
            if (token != null && !token.isEmpty()) {
                con.setRequestProperty("Authorization", "Bearer " + token);
            }

            // Check for successful response code
            int responseCode = con.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Request failed with status " + responseCode);
            }

            // Read response into a StringBuilder
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                return response.toString();
            }
        } catch (IOException e) {
            // Log and rethrow exception
            logger.error("Failed to execute fetch", e);
            throw new RuntimeException("Failed to execute fetch", e);
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    public static void main(String[] args) {
        // Create a JavaScript context using GraalVM
        try (Context context = Context.create("js")) {
            // Register the fetchFromJava method as a function in the JavaScript context
            context.getBindings("js").putMember("fetchFromJava", (ProxyExecutable) arguments -> {
                String url = arguments[0].asString();
                String token = arguments.length > 1 ? arguments[1].asString() : null;
                return fetchFromJava(url, token);
            });

            // Polyfill for the fetch function in JavaScript using fetchFromJava method
            String fetchPolyfill = "const fetch = (url, options) => {\n" +
                    "  return new Promise((resolve, reject) => {\n" +
                    "    try {\n" +
                    "      const token = options && options.headers ? options.headers['Authorization'].split(' ')[1] : null;\n"
                    +
                    "      let response = JSON.parse(fetchFromJava(url, token));\n" +
                    "      resolve(response);\n" +
                    "    } catch (error) {\n" +
                    "      reject({ error: error.message });\n" +
                    "    }\n" +
                    "  });\n" +
                    "};";

            // Register the fetch function in the JavaScript context
            context.eval("js", fetchPolyfill);

            // JavaScript code using async/await with fetch to retrieve posts from the JSONPlaceholder API
            String code = "const fetchPosts = async () => {\n" +
                    "  try {\n" +
                    "    const response = await fetch('https://jsonplaceholder.typicode.com/posts', { headers: { 'Authorization': 'Bearer YOUR_TOKEN_HERE' } });\n"
                    +
                    "    const posts = response;\n" +
                    "    console.log('Retrieved posts:');\n" +
                    "    posts.forEach(post => {\n" +
                    "      console.log('- Title: ' + post.title);\n" +
                    "      console.log('  Body: ' + post.body);\n" +
                    "      console.log('---');\n" +
                    "    });\n" +
                    "  } catch (error) {\n" +
                    "    console.error('Error:', JSON.stringify(error));\n" +
                    "  }\n" +
                    "};\n" +
                    "fetchPosts();";

            // Run the JavaScript code
            context.eval("js", code);

            // Get the console object from the JavaScript context
            Value console = context.getBindings("js").getMember("console");

            // Override the log and error methods of the console
            console.invokeMember("log", (Consumer<String>) logger::info);
            console.invokeMember("error", (Consumer<String>) logger::error);
        } catch (Exception e) {
            // Log any exceptions that occur in the main method
            logger.error("Exception in main method", e);
        }
    }
}
