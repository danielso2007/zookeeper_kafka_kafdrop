#!/bin/bash
RED='\033[1;31m'
BLACK='\033[0;30m'
DARK_GRAY='\033[1;30m'
LIGHT_RED='\033[0;31m'
GREEN='\033[0;32m'
LIGHT_GREEN='\033[1;32m'
BROWN_ORANGE='\033[0;33m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
LIGHT_BLUE='\033[1;34m'
PURPLE='\033[0;35m'
LIGHT_PURPLE='\033[1;35m'
CYAN='\033[0;36m'
LIGHT_CYAN='\033[1;36m'
LIGHT_GRAY='\033[0;37m'
WHITE='\033[1;37m'
NC='\033[0m' # No Color

function install_image() {
    echo -e "${YELLOW}Removendo imagem haproxy-rabbitmq-cluster...${NC}"
    docker image rm haproxy-kafka-cluster:2.3.1
    echo -e "${YELLOW}Criando imagem haproxy-rabbitmq-cluster...${NC}"
    cd images-haproxy
    docker build -t haproxy-kafka-cluster:2.3.1 .
}

if [[ $(systemctl --type=service | grep 'docker') ]]; then
    install_image
else
    echo -e "${RED}Serviço docker não está ativo.${NC}"
    exit 0
fi
