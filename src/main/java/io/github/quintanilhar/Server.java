package io.github.quintanilhar;

import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
 
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
import org.jsoup.nodes.Document;

import io.github.quintanilhar.jobad.Attribute;

public class Server {
    static final Logger log = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {

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
                new File("src/main/resources/pt-BR/model/job.bin")
            );

            // Create a NameFinder using the model
            NameFinderME finder = new NameFinderME(model);
     
            Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
     
            // Split the sentence into tokens
            String[] tokens = tokenizer.tokenize(sentence);

            // Find the names in the tokens and return Span objects
            Span[] nameSpans = finder.find(tokens);

            Attribute[] attributes = new Attribute[nameSpans.length];

            int i = 0;
            for (Span span : nameSpans) {
                attributes[i++] = new Attribute(span.getType(), Server.getStringFromSpan(span, tokens));
            }

            Map<String, Object> variables = new HashMap<>();

            variables.put("url", request.queryParams("url"));
            variables.put("attributes", attributes);

            // The wm files are located under the resources directory
            return new ModelAndView(variables, "view.vm");
        }, new VelocityTemplateEngine());
    }

    private static String getContentFromUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        return doc.body().text();
    }

    private static String getStringFromSpan(final Span reducedSpan,
            final String[] tokens) {

        StringBuilder sb = new StringBuilder();
        for (int si = reducedSpan.getStart(); si < reducedSpan.getEnd(); si++) {
            sb.append(tokens[si]).append(" ");
        }

        return sb.toString().trim();
    }
}
