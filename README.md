# 🧪 Pruebas con Postman y ADB

Para poder acceder al servidor desde el ordenador mientras la aplicación se ejecuta en un dispositivo Android o emulador, es necesario redirigir los puertos mediante ADB.

## Comando de redirección HTTP

```powershell
adb forward tcp:8080 tcp:8080
```

## Comando de redirección HTTPS

```powershell
adb forward tcp:8443 tcp:8443
```

---

# 🖥️ Uso con ruta completa de ADB

Si ADB no se encuentra añadido al PATH del sistema:

```powershell
& "C:\Ruta\A\Android\Sdk\platform-tools\adb.exe" forward tcp:8080 tcp:8080
```

```powershell
& "C:\Ruta\A\Android\Sdk\platform-tools\adb.exe" forward tcp:8443 tcp:8443
```

---
