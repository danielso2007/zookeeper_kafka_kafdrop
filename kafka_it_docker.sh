#!/bin/bash
if [[ -z "$1" ]]
then
    echo -e "${Yellow}informe o número do cluster!${NC}"
    exit 0
else
    docker exec -it kafka-node-${1} bash
fi
