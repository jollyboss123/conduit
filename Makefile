.PHONY: up down build restart prune create-topics get-topics register-connector

up: docker-up create-topics register-connector

docker-up:
	docker-compose up --build -d

down:
	docker-compose down

build:
	docker-compose build

restart: down up

prune:
	docker-compose down -v --remove-orphans

create-topics:
	docker compose exec broker \
	kafka-topics --bootstrap-server broker:29092 \
	--create --if-not-exists --topic orders.private.outbox --partitions 3 --replication-factor 1 && \
	docker compose exec broker \
	kafka-topics --bootstrap-server broker:29092 \
	--create --if-not-exists --topic orders.public.outbox.v1 --partitions 3 --replication-factor 1

get-topics:
	curl -s -X GET http://localhost:8082/topics | jq

register-connector:
	curl -s -X POST http://localhost:8083/connectors \
		-H "Content-Type: application/json" \
		-H "Accept: application/json" \
		-d @debezium/connector.json | jq