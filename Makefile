.PHONY: order-up order-down order-build order-restart order-prune

order-up:
	docker-compose -f order-service/docker-compose.yaml up -d

order-down:
	docker-compose -f order-service/docker-compose.yaml down

order-build:
	docker-compose -f order-service/docker-compose.yaml build

order-restart: order-down order-up

order-prune:
	docker-compose -f order-service/docker-compose.yaml down -v --remove-orphans