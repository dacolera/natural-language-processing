package io.github.quintanilhar;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
 
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
/**
 * Hello world OpenNLP!
 * 
 */
public class BasicNameFinder {
    public static void main(String[] args) throws InvalidFormatException,
            IOException {
 
        Logger log = LoggerFactory.getLogger(BasicNameFinder.class);
 
        String[] sentences = {
            "Catho\nArquiteto \n1 vaga - hoje \nDe R$ 5.001,00 a R$ 6.000,00 São Paulo - SP (1)\nElaboração de planos e projetos associados a arquitetura de hospital universitário em todas as suas etapas, sugerindo materiais, acabamentos, técnicas, metodologias, analisando dados e informações, atendendo todas as legislações vigentes. Prestação de serviços de assessoramento, junto aos gerentes responsáveis pelo projeto, realizando demais atividades inerentes ao trabalho. Desenvolvimento de estudos preliminares com imagens 3D, para aprovação com cliente e posterior desenvolvimento de projeto executivo com quantitativo. Desenvolvimento de projeto e memorial para aprovação na VISA. Experiência em concepção de projeto hospitalar. Vivência em desenvolvimento e detalhamento de projeto executivo. Conhecimentos plenos em legislação hospitalar e legislação pertinente a área de arquitetura e urbanismo, com aprovação de projetos nos órgãos responsáveis. Ensino Superior completo em Arquitetura. Possuir registro no CAU."
        };
 
        // Load the model
        log.info("Loading the model...");
        TokenNameFinderModel model = new TokenNameFinderModel(
            new File("src/main/resources/pt-BR/model/role.bin")
        );

        // Create a NameFinder using the model
        NameFinderME finder = new NameFinderME(model);
 
        Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
 
        log.info("Walking throught the sentences...");
        for (String sentence : sentences) {
 
            // Split the sentence into tokens
            String[] tokens = tokenizer.tokenize(sentence);
 
            // Find the names in the tokens and return Span objects
            Span[] nameSpans = finder.find(tokens);
 
            // Print the names extracted from the tokens using the Span data
            log.info("Found roles: " + Arrays.toString(Span.spansToStrings(nameSpans, tokens)));
        }
    }
}
