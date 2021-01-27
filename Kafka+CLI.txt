#Criar tópico
kafka-topics --bootstrap-server localhost:9092 --topic <nome_topico> --create --partitions 3 --replication-factor 1

#Acrescentar partições em um tópico
kafka-topics --alter --bootstrap-server localhost:9092 --topic <nome_topico> --partitions <qtd>

#Listar tópicos
kafka-topics --bootstrap-server localhost:9092 --list

#Detalhes do tópico
kafka-topics --bootstrap-server localhost:9092 --topic <nome_topico> --describe

#Deletar tópico (Não funciona no Windows) 
kafka-topics --bootstrap-server localhost:9092 --topic <nome_topico> --delete

#Enviar mensagem via linha de comando:
kafka-console-producer --broker-list 127.0.0.1:9092 --topic <nome_topico>

#Consumir mensagens via linha de comando:
kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic <nome_topico>

#Consumir mensagens via linha de comando (desde o inicio):
kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic <nome_topico> --from-beginning

#Consumir mensagens em grupo
kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic <nome_topico> --group <group-name>

#Mostrar grupos
kafka-consumer-groups --bootstrap-server localhost:9092 --list

#Visualizar status das entregas (lag) por grupo:
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group <group-name>

#Reiniciar o offset do grupo para tópico específico
kafka-consumer-groups --bootstrap-server localhost:9092 --group <group-name> --reset-offsets --to-earliest --execute --topic <nome_topico>

#Reiniciar o offset do grupo para todos os tópicos
kafka-consumer-groups --bootstrap-server localhost:9092 --group <group-name> --reset-offsets --to-earliest --execute --all-topics