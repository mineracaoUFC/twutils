# Twutils

Um simples utilitário Java para pesquisa e obtenção de tweets.

## Get Started

1 - Baixe esse projeto.

2 - Você precisa adicionar as informações da sua conta de desenvolvedor do Twitter no arquivo twutils/src/main/resources/social_networking.properties

```java
	# Twitter oAuth
	twutils.twitter.access.token =    XXXXXXXXXXXXXXXXXXXXXX
	twutils.twitter.access.secret=    XXXXXXXXXXXXXXXXXXXXXX
	twutils.twitter.consumer.key =    XXXXXXXXXXXXXXXXXXXXXX
	twutils.twitter.consumer.secret=  XXXXXXXXXXXXXXXXXXXXXX
```

3 - No arquivo utils/DefautValues.java, você pode configurar a quantidade de tweets que você quer pesquisar por requisição, o separador do arquivo, e o tempo de execução entre as execuções.

```java
	public static final int DEFAULT_COUNT = 100;
	
	public static final String DEFAULT_CSV_SEPARATOR = ";";

	public static final int DEFAULT_THREAD_WAIT_TIME = 15000;
```
## Build

Para criar um arquivo executavel jar.

	mvn clean install assembly:single

## Opções

Opções disponíveis:

    --help           Show help.
    --output <out>   Output dir of tweets.
    --stweets <tt>   Collect tweets that contains the given keywords.


## Running

	java -jar --stweets "#Xfactor;#BakeOffBrazil" --output C:\opt	

## Contact

	https://www.linkedin.com/in/adail-carvalho-a34343106
