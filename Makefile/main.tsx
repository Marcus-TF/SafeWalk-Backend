# SafeWalk Backend - Makefile
# Facilita comandos comuns do projeto

.PHONY: help build run test clean install docker-build docker-up docker-down docker-logs

# Variáveis
JAR_FILE := target/safewalk-backend-1.0.0.jar
DOCKER_IMAGE := safewalk-backend
DOCKER_COMPOSE := docker-compose

# Comando padrão
help:
	@echo "SafeWalk Backend - Comandos Disponíveis:"
	@echo ""
	@echo "  make install       - Instalar dependências"
	@echo "  make build         - Compilar o projeto"
	@echo "  make run           - Executar a aplicação"
	@echo "  make test          - Executar testes"
	@echo "  make clean         - Limpar arquivos de build"
	@echo "  make package       - Gerar JAR"
	@echo ""
	@echo "Docker:"
	@echo "  make docker-build  - Build da imagem Docker"
	@echo "  make docker-up     - Iniciar containers"
	@echo "  make docker-down   - Parar containers"
	@echo "  make docker-logs   - Ver logs dos containers"
	@echo "  make docker-clean  - Remover containers e volumes"
	@echo ""
	@echo "Utilitários:"
	@echo "  make format        - Formatar código"
	@echo "  make check         - Verificar código"
	@echo "  make db-migrate    - Executar migrations"
	@echo "  make db-seed       - Popular banco com dados"

# Instalação de dependências
install:
	@echo "📦 Instalando dependências..."
	mvn clean install -DskipTests

# Build do projeto
build:
	@echo "🔨 Compilando projeto..."
	mvn clean compile

# Executar aplicação
run:
	@echo "🚀 Iniciando aplicação..."
	mvn spring-boot:run

# Executar testes
test:
	@echo "🧪 Executando testes..."
	mvn test

# Limpar projeto
clean:
	@echo "🧹 Limpando arquivos de build..."
	mvn clean
	rm -rf logs/

# Gerar JAR
package:
	@echo "📦 Gerando JAR..."
	mvn clean package -DskipTests

# Executar JAR
run-jar: package
	@echo "🚀 Executando JAR..."
	java -jar $(JAR_FILE)

# Verificar código
check:
	@echo "🔍 Verificando código..."
	mvn verify

# Formatar código
format:
	@echo "✨ Formatando código..."
	mvn formatter:format

# Docker build
docker-build:
	@echo "🐳 Building Docker image..."
	docker build -t $(DOCKER_IMAGE) .

# Docker compose up
docker-up:
	@echo "🐳 Iniciando containers..."
	$(DOCKER_COMPOSE) up -d

# Docker compose down
docker-down:
	@echo "🐳 Parando containers..."
	$(DOCKER_COMPOSE) down

# Docker logs
docker-logs:
	@echo "📋 Logs dos containers..."
	$(DOCKER_COMPOSE) logs -f

# Docker clean
docker-clean:
	@echo "🧹 Limpando Docker..."
	$(DOCKER_COMPOSE) down -v
	docker rmi $(DOCKER_IMAGE) || true

# Docker rebuild
docker-rebuild: docker-clean docker-build docker-up

# Database migrations (se usar Flyway/Liquibase)
db-migrate:
	@echo "📊 Executando migrations..."
	mvn flyway:migrate || echo "Flyway não configurado"

# Seed database
db-seed:
	@echo "🌱 Populando banco de dados..."
	psql -U postgres -d safewalk_db -f database-schema.sql

# Backup database
db-backup:
	@echo "💾 Fazendo backup do banco..."
	pg_dump -U postgres safewalk_db > backup_$$(date +%Y%m%d_%H%M%S).sql

# Development mode (com hot reload)
dev:
	@echo "🔥 Modo desenvolvimento..."
	mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Production build
prod-build:
	@echo "🏭 Build de produção..."
	mvn clean package -Pprod -DskipTests

# Production run
prod-run: prod-build
	@echo "🚀 Executando em produção..."
	java -jar -Dspring.profiles.active=prod $(JAR_FILE)

# Ver info do projeto
info:
	@echo "ℹ️  Informações do Projeto:"
	@echo "Nome: SafeWalk Backend"
	@echo "Versão: 1.0.0"
	@echo "Java: $$(java -version 2>&1 | head -n 1)"
	@echo "Maven: $$(mvn -version | head -n 1)"
	@echo "Docker: $$(docker --version)"

# Health check
health:
	@echo "🏥 Verificando saúde da aplicação..."
	@curl -s http://localhost:8080/actuator/health | grep -q "UP" && echo "✅ Backend está UP" || echo "❌ Backend está DOWN"

# Tail logs
logs:
	@tail -f logs/safewalk.log 2>/dev/null || echo "Arquivo de log não encontrado"

# Watch tests (requer entr)
watch-test:
	@echo "👀 Watching tests..."
	find src/test -name "*.java" | entr -c mvn test

# Generate coverage report
coverage:
	@echo "📊 Gerando relatório de cobertura..."
	mvn clean test jacoco:report

# All (clean, install, test, package)
all: clean install test package
	@echo "✅ Todas as etapas concluídas!"

# Dependency tree
deps:
	@echo "📦 Árvore de dependências..."
	mvn dependency:tree

# Update dependencies
update-deps:
	@echo "⬆️  Atualizando dependências..."
	mvn versions:display-dependency-updates

# Security check
security:
	@echo "🔒 Verificando vulnerabilidades..."
	mvn dependency-check:check || echo "OWASP Dependency Check não configurado"

# Quick start (para novos desenvolvedores)
quickstart: install docker-up
	@echo "✅ Setup concluído! Backend rodando em http://localhost:8080"
	@echo "📊 pgAdmin em http://localhost:5050"
	@echo "🔍 Health: http://localhost:8080/actuator/health"
