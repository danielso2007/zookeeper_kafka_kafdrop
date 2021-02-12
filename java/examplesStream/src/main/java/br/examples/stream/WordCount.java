package br.examples.stream;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Demonstra, usando o DSL KStream de alto nível, como implementar o programa WordCount que
 * calcula um histograma de ocorrência de palavra simples a partir de um texto de entrada. Este exemplo usa lambda
 * expressões e, portanto, funciona apenas com Java 8+.
 * <p>
 * Neste exemplo, o fluxo de entrada lê a partir de um tópico denominado "streams-plaintext-input", onde os valores de
 * mensagens representam linhas de texto; e a saída do histograma é escrita no tópico
 * "streams-wordcount-output", onde cada registro é uma contagem atualizada de uma única palavra, ou seja, { @code word (String) -> currentCount (Long) }.
 * <p>
 * Observação: antes de executar este exemplo, você deve 1) criar o tópico de origem (por exemplo, via { @code kafka-topics --create ... }),
 * então 2) inicie este exemplo e 3) grave alguns dados no tópico de origem (por exemplo, via { @code kafka-console-producer }).
 * Caso contrário, você não verá nenhum dado chegando ao tópico de saída.
 * <p>
 * < Br >
 * COMO EXECUTAR ESTE EXEMPLO
 * <p>
 * 1) Inicie o Zookeeper e o Kafka. Por favor, consulte < a  href = ' http://docs.confluent.io/current/quickstart.html#quickstart ' > QuickStart </a>.
 * <p>
 * 2) Crie os tópicos de entrada e saída usados ​​por este exemplo.
 * < pré >
 * { @code
 * $ bin / kafka-topics --create --topic streams-plaintext-input \
 * --zookeeper localhost: 2181 --partições 1 --fator de replicação 1
 * $ bin / kafka-topics --create --topic streams-wordcount-output \
 * --zookeeper localhost: 2181 --partições 1 --fator de replicação 1
 * } </pre>
 * Observação: os comandos acima são para a plataforma Confluent. Para Apache Kafka, deve ser { @code bin / kafka-topics.sh ... }.
 * <p>
 * 3) Inicie este aplicativo de exemplo em seu IDE ou na linha de comando.
 * <p>
 * Se via linha de comando, consulte < a  href = ' https://github.com/confluentinc/kafka-streams-examples#packaging-and-running ' > Embalagem </a>.
 * Depois de embalado, você pode executar:
 * < pré >
 * { @code
 * $ java -cp target / kafka-streams-examples-6.1.0-standalone.jar io.confluent.examples.streams.WordCountLambdaExample
 * }
 * </pre>
 * 4) Grave alguns dados de entrada no tópico de origem "streams-plaintext-input" (por exemplo, via { @code kafka-console-producer }).
 * O aplicativo de exemplo já em execução (etapa 3) processará automaticamente esses dados de entrada e gravará o
 * resultados para o tópico de saída "streams-wordcount-output".
 * < pré >
 * { @code
 * # Inicie o produtor do console. Você pode então inserir dados de entrada escrevendo alguma linha de texto, seguido por ENTER:
 * #
 * # hello kafka streams <ENTER>
 * # todos os streams levam a kafka <ENTER>
 * # junte-se ao kafka Summit <ENTER>
 * #
 * # Cada linha que você inserir se tornará o valor de uma única mensagem Kafka.
 * $ bin / kafka-console-producer --broker-list localhost: 9092 --topic streams-plaintext-input
 * } </pre>
 * 5) Inspecione os dados resultantes no tópico de saída, por exemplo, via { @code kafka-console-consumer }.
 * < pré >
 * { @code
 * $ bin / kafka-console-consumer --topic streams-wordcount-output --from-begin \
 * --bootstrap-server localhost: 9092 \
 * --property print.key = true \
 * --property value.deserializer = org.apache.kafka.common.serialization.LongDeserializer
 * } </pre>
 * Você deve ver os dados de saída semelhantes aos abaixo. Observe que a saída exata
 * a sequência dependerá da rapidez com que você digita as frases acima. Se você os digitar
 * lentamente, é provável que você obtenha cada atualização de contagem, por exemplo, kafka 1, kafka 2, kafka 3.
 * Se você digitá-los rapidamente, provavelmente obterá menos atualizações de contagem, por exemplo, apenas kafka 3.
 * Isso ocorre porque o intervalo de confirmação é definido como 10 segundos. Qualquer coisa digitada em
 * esse intervalo será compactado na memória.
 * < pré >
 * { @code
 * olá 1
 * kafka 1
 * streams 1
 * todos 1
 * streams 2
 * lead 1
 * para 1
 * junte-se a 1
 * kafka 3
 * cimeira 1
 * } </pre>
 * 6) Depois de concluir seus experimentos, você pode interromper este exemplo via { @code Ctrl-C }. Se necessário,
 * também pare o corretor Kafka ({ @code Ctrl-C }) e só então pare a instância ZooKeeper (`{ @code Ctrl-C }).
 */
public class WordCount {

    static final String inputTopic = "streams-plaintext-input";
    static final String outputTopic = "streams-wordcount-output";

    /**
     * The Streams application as a whole can be launched like any normal Java application that has a `main()` method.
     */
    public static void main(final String[] args) {
        final String bootstrapServers = args.length > 0 ? args[0] : "localhost:9090";

        // Configure o aplicativo Streams.
        final Properties streamsConfiguration = getStreamsConfiguration(bootstrapServers);

        // Defina a topologia de processamento do aplicativo Streams.
        final StreamsBuilder builder = new StreamsBuilder();
        createWordCountStream(builder);
        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);

        // Sempre (e incondicionalmente) limpe o estado local antes de iniciar a topologia de processamento.
        // Optamos por esta chamada incondicional aqui porque isso tornará mais fácil para você brincar com o exemplo
        // ao redefinir o aplicativo para fazer uma nova execução (por meio da ferramenta de redefinição do aplicativo,
        // http://docs.confluent.io/current/streams/developer-guide.html#application-reset-tool).
        //
        // A desvantagem de limpar o estado local antes é que seu aplicativo deve reconstruir seu estado local do zero, o que
        // levará tempo e exigirá a leitura de todos os dados relevantes ao estado do cluster Kafka pela rede.
        // Assim, em um cenário de produção, você normalmente não deseja limpar sempre como fazemos aqui, mas apenas quando
        // é realmente necessário, ou seja, apenas sob certas condições (por exemplo, a presença de um sinalizador de linha de comando para seu aplicativo).
        // Veja `ApplicationResetExample.java` para um exemplo de produção.
        streams.cleanUp();

        // Agora execute a topologia de processamento via `start ()` para começar a processar seus dados de entrada.
        streams.start();

        // Adicione o gancho de desligamento para responder ao SIGTERM e feche normalmente o aplicativo Streams.
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    /**
     * Configure o aplicativo Streams.
     * <p>
     * Várias configurações relacionadas ao Kafka Streams são definidas aqui, como a localização do cluster Kafka de destino a ser usado.
     * Além disso, você também pode definir as configurações do Kafka Producer e Kafka Consumer quando necessário.
     *
     * @param bootstrapServers Kafka cluster address
     * @return Properties getStreamsConfiguration
     */
    static Properties getStreamsConfiguration(final String bootstrapServers) {
        final Properties streamsConfiguration = new Properties();
        // Dê ao aplicativo Streams um nome exclusivo. O nome deve ser único no cluster Kafka no qual o aplicativo é executado.
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-lambda-example");
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "wordcount-lambda-example-client");
        // Onde encontrar o broker Kafka.
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // Especifique serializadores padrão para chaves de registro e para valores de registro.
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        // Os registros devem ser limpos a cada 10 segundos. Isso é menor que o padrão para manter este exemplo interativo.
        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
        // Para fins ilustrativos, desativamos os caches de registro.
        streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        return streamsConfiguration;
    }

    /**
     * Define the processing topology for Word Count.
     *
     * @param builder StreamsBuilder to use
     */
    static void createWordCountStream(final StreamsBuilder builder) {
        // Construir um `KStream` a partir do tópico de entrada" streams-plaintext-input ", onde os valores da mensagem
        // representam linhas de texto (para o propósito deste exemplo, nós ignoramos tudo o que pode ser armazenado
        // nas chaves de mensagem). A chave padrão e serdes de valor serão usados.
        final KStream<String, String> textLines = builder.stream(inputTopic);

        final Pattern pattern = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS);

        final KTable<String, Long> wordCounts = textLines
                // Divida cada linha de texto, por espaço em branco, em palavras. As linhas de texto são o registro
                // valores, ou seja, podemos ignorar quaisquer dados que estejam nas chaves de registro e, assim, invocar
                // `flatMapValues ()` em vez do mais genérico `flatMap ()`.
                .flatMapValues(value -> {
                    System.out.println("----------------------------------------------------------");
                    System.out.println("Value: " + value);
                    return Arrays.asList(pattern.split(value.toLowerCase()));
                })
                // Agrupe os dados divididos por palavra para que possamos subsequentemente contar as ocorrências por palavra.
                // Esta etapa redefine (particiona novamente) os dados de entrada, com a nova chave de registro sendo as palavras.
                // Nota: Não há necessidade de especificar serdes explícitos porque os tipos de chave e valor resultantes
                // (String e String) correspondem aos serdes padrão do aplicativo.
                .groupBy((keyIgnored, word) -> {
                    System.out.println("Word: " + word);
                    return word;
                })
                // Conte as ocorrências de cada palavra (chave de registro).
                .count(Materialized.as("counts-store"));

        // Escreva `KTable <String, Long>` no tópico de saída.
        wordCounts.toStream().to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()));
    }
}
