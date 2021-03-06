/*
 * (C) Copyright 2017 JOML

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

 */
import java.io.File;
import java.io.FilenameFilter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 * Check for invalid URLs in Java sources.
 * 
 * @author Kai Burjack
 */
public class UrlChecker {

    private static final Pattern URL_PATTERN = Pattern.compile("\"(http[^\"]+)\"");

    private static void walk(File root, List urls) throws Exception {
        File[] list = root.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                File f = new File(dir, name);
                return f.isDirectory() || name.endsWith(".java");
            }
        });
        if (list == null)
            return;
        for (int i = 0; i < list.length; i++) {
            File f = list[i];
            if (f.isDirectory()) {
                walk(f, urls);
            } else {
                process(f, urls);
            }
        }
    }

    private static void process(File file, List urls) throws Exception {
        byte[] encoded = Files.readAllBytes(file.toPath());
        String str = new String(encoded, Charset.forName("UTF-8"));
        Matcher m = URL_PATTERN.matcher(str);
        while (m.find()) {
            urls.add(m.group(1));
        }
    }

    public static void main(String[] args) throws Exception {
        List urls = new ArrayList();
        Set alreadyCheckedUrls = new HashSet();
        int numChecked = 0;
        walk(new File("src"), urls);
        System.out.println("Checking " + urls.size() + " URLs...");
        HttpClient client = HttpClientBuilder.create().build();
        for (int i = 0; i < urls.size(); i++) {
            String url = (String) urls.get(i);
            numChecked++;
            if (alreadyCheckedUrls.contains(url))
                continue;
            int statusCode;
            try {
                HttpResponse response = client.execute(new HttpGet((String) urls.get(i)));
                statusCode = response.getStatusLine().getStatusCode();
                EntityUtils.consume(response.getEntity());
            } catch (Exception e) {
                System.err.println("Found invalid URL (" + e.getClass().getSimpleName() + ": " + e.getMessage() + "): " + url);
                throw new AssertionError();
            }
            if (statusCode != 200) {
                System.err.println("Found invalid URL (" + statusCode + "): " + url);
                throw new AssertionError();
            }
            alreadyCheckedUrls.add(url);
        }
        System.out.println("Checked " + numChecked + " URLs.");
    }

}
