#!/bin/bash
RED='\033[0;31m'
Black='\033[0;30m'
Dark_Gray='\033[1;30m'
Light_Red='\033[1;31m'
Green='\033[0;32m'
Light_Green='\033[1;32m'
Brown_Orange='\033[0;33m'
Yellow='\033[1;33m'
Blue='\033[0;34m'
Light_Blue='\033[1;34m'
Purple='\033[0;35m'
Light_Purple='\033[1;35m'
Cyan='\033[0;36m'
Light_Cyan='\033[1;36m'
Light_Gray='\033[0;37m'
White='\033[1;37m'
NC='\033[0m' # No Color
SERVICE_NAME=zookeeper

if [[ !($(docker images | grep haproxy-kafka-cluster)) ]]; then
    echo -e "${RED}Imagem haproxy-kafka-cluster não existe. Criar nova imagem...${NC}"
    ./create-haproxy-rabbitmq-cluster.sh
fi

if [ -z `docker-compose ps -q ${SERVICE_NAME}` ] || [ -z `docker ps -q --no-trunc | grep $(docker-compose ps -q ${SERVICE_NAME})` ]; then
  echo -e "${Yellow}No, it's not running. continuar...${NC}"
else
  echo -e "${Yellow}Serviço já em execução!${NC}"
  exit 0
fi

DIR="data"
if [ ! -d "$DIR" ]; then
    echo -e "${Yellow}Criando pastas de volume do zookeeper...${NC}"

    mkdir -p data/zookeeper/data
    mkdir -p data/zookeeper/datalog

    echo -e "${Yellow}Permissão 1000 nas pastas de volume do zookeeper...${NC}"

    chown -R 1000:1000 data/zookeeper/data
    chown -R 1000:1000 data/zookeeper/datalog

    echo -e "${Yellow}Criando pastas de volume dos Kafka 1, 2 e 3...${NC}"

    mkdir -p data/kafka1/data
    mkdir -p data/kafka2/data
    mkdir -p data/kafka3/data

    echo -e "${Yellow}Permissão 1000 para as pastas de volume do kafka...${NC}"

    chown -R 1000:1000 data/kafka1/data
    chown -R 1000:1000 data/kafka2/data
    chown -R 1000:1000 data/kafka3/data
fi

echo -e "${Yellow}Iniciando docker-compose...${NC}"
docker-compose up -d
sleep 5
docker-compose ps
ls -la