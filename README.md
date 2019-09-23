## Proyecto1 AREP
### Framework - Cliente Servidor

En este repositorio, se implementó una aplicación web Cliente Servidor desplegada en Heroku. Implementa un framework de IoC (inversión de control) para ofrecer un modelo de software Cliente-Servidor. El servidor recibe peticiones del cliente por el navegador desde la aplicación desplegada en heroku. Puede solicitar recursos estáticos: imágenes jpg y archivos html, como también permite responder a peticiones de funciones implementadas en el servidor propiamente, como generar un html a través de un método sin parámetros y con parámetros. Para el caso de parámetros, se le puede pasar un número y el servidor le responderá el cuadrado del mismo.

___
### Instalación - Uso del proyecto como librería
Si desea usar éste repositorio como librería en su proyecto, realice los siguientes pasos:

• Descargue o clone él repositorio Proyecto1-AREP: <https://github.com/acai-bjca/Proyecto1-AREP.git>

• Agregue la siguiente dependencia al pom de su proyecto:
``` xml
 <dependency>
	<groupId>apps</groupId>
    <artifactId>Proyecto1-AREP</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

• Importe el proyecto en la clase que lo requiera:
import apps.*;

___
### Documentación

Para leer la documentación diríjase a: <https://github.com/acai-bjca/Proyecto1-AREP/tree/master/src/main/resources/documentacion/apidocs/apps>

___
### Despliegue

El link de la aplicacion web desplegada en heroku es: https://proyecto1-arep.herokuapp.com
[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/heroku/node-js-sample)
___
### Construido con

• Java  
• [Maven] (https://maven.apache.org/) - Gestión de dependencias

___
### Autor

**Amalia Inés Alfonso Campuzano** 

Estudiante de la Escuela Colombiana de Ingeniería Julio Garavito

Ingeniería de Sistemas
___
### Licencia

Este proyecto está licenciado bajo la Licencia GNU - vea el archivo [LICENSE.md] (LICENSE.md) para más detalles.

