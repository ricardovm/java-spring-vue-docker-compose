#/bin/bash

echo "Iniciando a construção do back-end"
cd back
chmod +x mvnw
./mvnw package -DskipTests

echo "Gerando imagem do back-end"
docker build -t springvue/back .

echo "Iniciando a construção do front-end"
cd ../front
echo "Gerando imagem do front-end"
docker build -t springvue/front .

cd ..

