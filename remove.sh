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
VERSION=2.3.4
NAME_IMAGE=haproxy-kafka-cluster

echo -e "${Yellow}Removendo docker-compose...${NC}"
docker-compose stop
docker-compose rm -v -f
sleep 3
echo -e "${Yellow}Listando docker...${NC}"
docker-compose ps
echo -e "${Yellow}Removendo pasta data...${NC}"
rm -rf data
ls -la

# echo -e "${Yellow}Removendo imagem ${NAME_IMAGE}:${VERSION}...${NC}"
# docker rmi ${NAME_IMAGE}:${VERSION}