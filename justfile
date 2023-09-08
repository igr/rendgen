
# just displays the receipes
default:
    @just --list

# start the neo4j docker containers
up:
    #!/usr/bin/env bash
    export USER_ID="$(id -u)"
    export GROUP_ID="$(id -g)"
    export NEO4J_DOCKER_IMAGE=neo4j:5-enterprise
    export NEO4J_EDITION=docker_compose
    export EXTENDED_CONF=yes
    export NEO4J_ACCEPT_LICENSE_AGREEMENT=yes
    export NEO4J_AUTH=neo4j/passw0rd
    docker-compose up -d

# stop the neo4j docker containers
down:
    #!/usr/bin/env bash
    export USER_ID="$(id -u)"
    export GROUP_ID="$(id -g)"
    export NEO4J_DOCKER_IMAGE=neo4j:5-enterprise
    export NEO4J_EDITION=docker_compose
    export EXTENDED_CONF=yes
    export NEO4J_ACCEPT_LICENSE_AGREEMENT=yes
    export NEO4J_AUTH=neo4j/passw0rd
    docker-compose down

reset:
	rm -rf neo4j/conf
	rm -rf neo4j/data
	rm -rf neo4j/import
	rm -rf neo4j/logs