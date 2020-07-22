# Requisitos

Para construir e executar a aplicação, são necessários:

1. Java 11 com JAVA_HOME configurada
2. docker
3. docker-compose
4. Sistema Linux

O desenvolvimento foi desenvolvido e testado com:

1. Linux Mint 19.3 Tricia
2. OpenJDK 11.0.5
3. Docker 19.03.5
4. docker-compose 1.25.3

# Execução

Para a execução do projeto, são necessários três containers:

1. MySQL
2. Back-end
3. Front-end

Na raiz do projeto já existem dois scripts (bash/Linux) para a construção e execução do projeto:

1. `build.sh`

   Esse é responsável por gerar as imagens do back-end e front-end, incluindo a compilação
   e geração de pacotes.

2. `run.sh`

   Apenas executa a aplicação toda, com os três containers, via docker-compose

Talvez seja necessário dar permissão de execução aos scripts:

```
chmod +x *.sh
```

Ao executar o scrit `run.sh`, pode levar algum tempo até que o back-end esteja pronto, já que
ele depende do MySQL estar pronto.

Quando todos os containers estiverem rodando, basta acessar http://localhost:8080

# Extras

Foi utilizado o Flyway para criação das tabelas no banco de dados, apesar de saber que poderia ter 
sido feito sem esta ferramenta, além de ser um bom padrão em muitas aplicações, já estou habituado 
com seu uso.

Também foi usado o BootstrapVue para componentes no front-end.

# TODO

Poderia ter alguma validação nos formulários, mas não era o foco aqui, e melhor componentização no 
front-end. Também apenas um modelo de teste está implementado, servindo de referência, mas não 
completo.