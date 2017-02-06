# Natural Language Processing Prototype with Java

This is a simple prototype to learn how to work with natural language processing 
using the library [Apache OpenNLP](https://opennlp.apache.org).


Execute this command to start the container:

```
$ docker run --rm -it -v $(pwd):/usr/src/app -p "4567:4567" maven:alpine ash
```

Inside the container, you can start the web application version with the command below:

```
$ mvn compile exec:java -Dexec.mainClass="io.github.quintanilhar.Server"
```

Now, you can access the url ``` http://$DOCKER_HOST:4567/ ```
