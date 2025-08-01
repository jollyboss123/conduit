.PHONY: up down build logs restart prune

up:
	docker-compose up -d

down:
	docker-compose down

build:
	docker-compose build

restart: down up

prune:
	docker-compose down -v --remove-orphans