## Proyecto1 AREP
### Framework - Cliente Servidor

En este repositorio, se implementó una aplicación web Cliente Servidor desplegada en Heroku, para lacual se implementó un framework.

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
### Ejecutando las pruebas

Para ejecutar las pruebas puede usar el comando:
>mvn package


___
### Pruebas
Para mostrar la correcta funcionalidad de la aplicación, se realizó una prueba manual.
Primero se ingresaron los números separados por ','.

![](src/main/resources/index.png)

A continuación, se puede ver el resultado generado una vez se dió clic en el botón Calcular.

![](src/main/resources/calculo.png)

___
### Despliegue

El link de la aplicacion web desplegada en heroku es: https://proyecto1-arep.herokuapp.com

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

