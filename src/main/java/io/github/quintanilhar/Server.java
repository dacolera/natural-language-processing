package io.github.quintanilhar;

import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
 
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;
import spark.Request;
import spark.Response;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import org.jsoup.Jsoup;

public class Server {
    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger(BasicNameFinder.class);

		get("/", (request, response) -> {
            Map<String, Object> model = new HashMap<>();

            // The wm files are located under the resources directory
            return new ModelAndView(model, "view.vm");
        }, new VelocityTemplateEngine());

		post("/", (request, response) -> {
            String sentence = Server.getContentFromUrl(request.queryParams("url"));

            log.info("Page: " + sentence);
 
            // Load the model
            log.info("Loading the model...");
            TokenNameFinderModel model = new TokenNameFinderModel(
                new File("src/main/resources/pt-BR/model/role.bin")
            );

            // Create a NameFinder using the model
            NameFinderME finder = new NameFinderME(model);
     
            Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
     
            // Split the sentence into tokens
            String[] tokens = tokenizer.tokenize(sentence);

            // Find the names in the tokens and return Span objects
            Span[] nameSpans = finder.find(tokens);

            log.info("Found: " + Arrays.toString(Span.spansToStrings(nameSpans, tokens)));

            Map<String, Object> variables = new HashMap<>();

            variables.put("url", request.queryParams("url"));
            variables.put("attributes", Span.spansToStrings(nameSpans, tokens));

            // The wm files are located under the resources directory
            return new ModelAndView(variables, "view.vm");
        }, new VelocityTemplateEngine());
    }

    private static String getContentFromUrl(String url) throws IOException {
        HttpRequest request = HTTP_TRANSPORT.createRequestFactory().buildGetRequest(
                new GenericUrl(url)
        );
        HttpResponse response = request.execute();
        System.out.println(response.getStatusCode());

        return Jsoup.parse(response.parseAsString()).text();
    }
}
