⚠️ Nota importante

Para poder realizar pruebas desde herramientas externas como Postman
 mientras la aplicación se ejecuta en un dispositivo Android o emulador, es necesario redirigir el puerto mediante ADB.

Ejecutar el siguiente comando en terminal:

adb forward tcp:8080 tcp:8080

Si adb no está añadido al PATH del sistema, puede utilizarse la ruta completa del ejecutable:

& "C:\Ruta\A\Android\Sdk\platform-tools\adb.exe" forward tcp:8080 tcp:8080

Tras realizar la redirección, el servidor podrá ser accedido desde el ordenador utilizando:

http://localhost:8080

o en caso de HTTPS:

https://localhost:8443
