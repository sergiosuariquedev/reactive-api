# Reactive JAVA API
## Descripción del Proyecto
Este proyecto es una API desarrollada en Spring Boot utilizando Spring WebFlux para programación reactiva. Está diseñado para manejar franquicias, sucursales y productos dentro de esas sucursales. Se aprovecha la reactividad para hacer que los endpoints respondan de manera eficiente y escalable.

## Tecnologías Utilizadas:
- Spring Boot: Framework principal para el desarrollo de la API.

- Spring WebFlux: Para manejar las rutas de manera reactiva.

- Docker: Para empaquetar la aplicación y facilitar su despliegue en cualquier entorno.

- Reactive MongoDB

- Java 21: Versión utilizada para el desarrollo del proyecto.

## Requisitos
Antes de ejecutar la aplicación, asegúrate de tener instalados los siguientes programas:

- Docker

- JDK 21 (si no estás usando Docker para ejecutar la aplicación)

##Despliegue Local
1. Clonar el Repositorio
 - git clone https://github.com/sergiosuariquedev/reactive-api.git
 - cd reactive-api
2. Construir y Ejecutar con Docker Compose
Construye y ejecuta la aplicación con el siguiente comando:
 - docker-compose up --build
Esto descargará las imágenes necesarias, construirá la aplicación y la ejecutará en el puerto 8080 de tu máquina local.
3. Configuración de puerto maquina host, el puesto por defecto es 8080, pero si se requiere modificar se debe modifcar el vaor de la variable HOST_PORT en el arcivo.env
4. Acceder a la API
La API estará disponible en http://localhost:8080.

## Notas
- La aplicación está dockerizada, por lo que es fácil de desplegar en cualquier entorno que soporte contenedores.
- Se utilizó Spring WebFlux para la creación de la API de manera reactiva, lo que permite una mayor escalabilidad y rendimiento en entornos de alta demanda.
- El archivo api-doc.yml contiene la documentación de los endpoints del api
