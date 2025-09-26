@echo off
echo ================================
echo Construyendo FFE-Doc-Gen
echo ================================

echo.
echo [1/3] Limpiando y compilando...
call mvn clean package -q
if errorlevel 1 (
    echo Error en la compilacion
    pause
    exit /b 1
)

echo [2/3] Creando ejecutable Windows...
jlink ^
  --module-path "%JAVA_HOME%\jmods;C:\Program Files\Java\javafx-sdk-21.0.8\jmods" ^
  --add-modules java.base,javafx.controls,javafx.fxml ^
  --output runtime

jpackage --type app-image ^
         --input target ^
         --main-jar ffe-doc-gen-1.0-SNAPSHOT-jar-with-dependencies.jar ^
         --main-class org.educa.ffegen.MainApp ^
         --dest dist ^
         --name "FFE-Doc-Gen" ^
         --app-version "1.0.0" ^
         --vendor "Educa FFE" ^
         --description "FFE Document Generator" ^
         --runtime-image runtime

if errorlevel 1 (
    echo Error creando ejecutable
    pause
    exit /b 1
)

echo.
echo [3/3] ¡Construccion completada!


echo [4/3] Copiando recursos adicionales...
xcopy /E /I /Y target\classes\data dist\FFE-Doc-Gen\data

if errorlevel 1 (
    echo Error copiando recursos
    pause
    exit /b 1
)

echo Ejecutable creado en: dist\FFE-Doc-Gen\FFE-Doc-Gen.exe
echo.
