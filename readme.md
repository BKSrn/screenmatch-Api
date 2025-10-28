# 📺 ScreenMatch API - Documentação

## Visão Geral

ScreenMatch é uma API REST desenvolvida em Spring Boot para gerenciamento de séries de TV e episódios. A aplicação integra-se com a API OMDB para buscar informações sobre séries e permite armazenar e consultar dados em um banco PostgreSQL.

## 🛠️ Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.1.1**
- **Spring Data JPA**
- **PostgreSQL**
- **Jackson** (serialização JSON)
- **Maven** (gerenciamento de dependências)
- **OpenAI GPT-3** (tradução de sinopses - opcional)

## 📋 Requisitos

- Java 17 ou superior
- PostgreSQL instalado e configurado
- Maven 3.9.3
- Chave de API do OMDB (gratuita)

## ⚙️ Configuração

### Variáveis de Ambiente

Configure as seguintes variáveis de ambiente:

```properties
DB_USER=seu_usuario_postgres
DB_PASSWORD=sua_senha_postgres
DB_HOST=localhost:5432
```

### Arquivo application.properties

```properties
spring.datasource.username=${DB-USER}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.url=jdbc:postgresql://${DB_HOST}/screnmatch
spring.jpa.hibernate.ddl-auto=update
```

### CORS

A aplicação está configurada para aceitar requisições de:
- `http://127.0.0.1:5500`

Para adicionar outras origens, edite o arquivo `CorsConfiguration.java`.

## 🗂️ Estrutura do Projeto

```
src/main/java/br/com/alura/screenmatch/
├── config/              # Configurações (CORS)
├── controller/          # Controllers REST
├── dto/                 # Data Transfer Objects
├── model/               # Entidades JPA
├── repository/          # Repositórios JPA
├── service/             # Serviços de negócio
└── principal/           # Interface console (legado)
```

## 📊 Modelo de Dados

### Entidade Serie

| Campo | Tipo | Descrição |
|-------|------|-----------|
| id | Long | Identificador único |
| titulo | String | Título da série (único) |
| totalTemporadas | Integer | Número total de temporadas |
| avaliacao | Double | Avaliação IMDB |
| genero | Categoria | Gênero da série (enum) |
| atores | String | Lista de atores |
| sinopse | String | Sinopse da série |
| poster | String | URL do poster |
| episodios | List<Episodio> | Lista de episódios |

### Entidade Episodio

| Campo | Tipo | Descrição |
|-------|------|-----------|
| id | Long | Identificador único |
| temporada | Integer | Número da temporada |
| titulo | String | Título do episódio |
| numeroEpisodio | Integer | Número do episódio |
| avaliacao | Double | Avaliação do episódio |
| dataLancamento | LocalDate | Data de lançamento |
| serie | Serie | Série relacionada (FK) |

### Enum Categoria

- ACAO (Action, Ação)
- COMEDIA (Comedy, Comédia)
- DRAMA (Drama, Drama)
- CRIME (Crime, Crime)
- ROMANCE (Romance, Romance)
- ANIMACAO (Animation, Animação)
- AVENTURA (Adventure, Aventura)

## 🔌 Endpoints da API

### Séries

#### **GET** `/series`
Retorna todas as séries cadastradas.

**Resposta:**
```json
[
  {
    "id": 1,
    "titulo": "Breaking Bad",
    "totalTemporadas": 5,
    "avaliacao": 9.5,
    "genero": "DRAMA",
    "atores": "Bryan Cranston, Aaron Paul",
    "sinopse": "A high school chemistry teacher...",
    "poster": "https://..."
  }
]
```

---

#### **GET** `/series/top5`
Retorna as 5 séries com melhor avaliação.

**Resposta:** Array de `SerieDTO`

---

#### **GET** `/series/lancamentos`
Retorna as séries lançadas mais recentemente (baseado na data de lançamento dos episódios).

**Resposta:** Array de `SerieDTO`

---

#### **GET** `/series/{id}`
Retorna os detalhes de uma série específica.

**Parâmetros:**
- `id` (path) - ID da série

**Resposta:** `SerieDTO`

---

#### **GET** `/series/{id}/temporadas/todas`
Retorna todos os episódios de uma série.

**Parâmetros:**
- `id` (path) - ID da série

**Resposta:**
```json
[
  {
    "titulo": "Pilot",
    "numeroEpisodio": 1,
    "temporada": 1
  }
]
```

---

#### **GET** `/series/{id}/temporadas/{numeroTemporada}`
Retorna os episódios de uma temporada específica.

**Parâmetros:**
- `id` (path) - ID da série
- `numeroTemporada` (path) - Número da temporada

**Resposta:** Array de `EpisodioDTO`

---

#### **GET** `/series/{id}/temporadas/top`
Retorna os 5 episódios com melhor avaliação de uma série.

**Parâmetros:**
- `id` (path) - ID da série

**Resposta:** Array de `EpisodioDTO`

---

#### **GET** `/series/categoria/{categoriaEscolhida}`
Retorna séries de uma categoria específica.

**Parâmetros:**
- `categoriaEscolhida` (path) - Nome da categoria (Action, Comedy, Drama, etc.)

**Resposta:** Array de `SerieDTO`

---

#### **POST** `/series/add/{nomeSerie}`
Busca e adiciona uma nova série da API OMDB.

**Parâmetros:**
- `nomeSerie` (path) - Nome da série a ser buscada

**Resposta:** `SerieDTO` da série adicionada

**Exemplo:**
```
POST /series/add/Breaking Bad
```

---

#### **POST** `/series/add/episodios/{nomeSerie}`
Busca e adiciona os episódios de uma série já cadastrada.

**Parâmetros:**
- `nomeSerie` (path) - Nome da série

**Resposta:** Array de `EpisodioDTO` com os episódios adicionados

**Exemplo:**
```
POST /series/add/episodios/Breaking Bad
```

---

## 🔍 Consultas Personalizadas

O repositório `SerieRepository` oferece consultas personalizadas:

### Derived Queries
- `findFirstByTituloContainsIgnoreCase(String nomeSerie)`
- `findByAtoresContainsIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, double avaliacao)`
- `findTop5ByOrderByAvaliacaoDesc()`
- `findByGenero(Categoria categoria)`

### JPQL Queries
- `seriesPorTemporadaEAvaliacao(int totalTemporadas, double avaliacao)`
- `epiosdiosPorTrecho(String trecho)`
- `topEpisodiosPorSerie(Serie serie)`
- `episodioPorSerieEAno(Serie serie, int anoLancamento)`

## 🎯 Exemplos de Uso

### Adicionar uma série

```bash
curl -X POST http://localhost:8080/series/add/Friends
```

### Buscar séries por categoria

```bash
curl http://localhost:8080/series/categoria/Comedy
```

### Obter episódios de uma temporada

```bash
curl http://localhost:8080/series/1/temporadas/2
```

## 🔐 Integração com OMDB

A API utiliza a chave `a4a34e18` (hardcoded) para consultas ao OMDB. Para uso em produção, considere:

1. Mover a chave para variáveis de ambiente
2. Implementar rate limiting
3. Adicionar cache para reduzir chamadas à API

## 📝 Observações

- A aplicação cria automaticamente as tabelas no banco (ddl-auto=update)
- O relacionamento Serie-Episodio é EAGER (carrega episódios automaticamente)
- Títulos de séries são únicos no banco de dados
- A funcionalidade de tradução via GPT-3 está comentada no código

## 🚀 Executando a Aplicação

```bash
# Via Maven
./mvnw spring-boot:run

# Via JAR compilado
./mvnw clean package
java -jar target/stream-0.0.1-SNAPSHOT.jar
```

A aplicação estará disponível em: `http://localhost:8080`

## 🐛 Tratamento de Erros

A aplicação possui tratamento básico para:
- Séries não encontradas (retorna null)
- Erros de parsing de dados da OMDB
- Valores inválidos de avaliação (converte para 0.0)
- Datas de lançamento inválidas (define como null)

## 📈 Melhorias Futuras

- Implementar tratamento de exceções global (@ControllerAdvice)
- Adicionar paginação nos endpoints de listagem
- Implementar autenticação e autorização
- Adicionar validação de dados com Bean Validation
- Criar documentação Swagger/OpenAPI
- Implementar testes unitários e de integração
- Adicionar cache (Redis/Caffeine) para consultas frequentes
- Migrar chave da API OMDB para variáveis de ambiente

---

**Desenvolvido para fins de estudo** | Projeto Alura