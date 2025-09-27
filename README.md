# Generador de documentos para las FFE

FFE-doc-gen es un proyecto para agilizar la documentación necesaria a enviar a la comunidad de Madrid para la realización de la fase de formación en empresa para el alumnado de formación profesional.


## Campos del Excel
A continuación se describe los campos del Excel que alimenta la aplicación JavaFX para la generación de la documentación oficial de la Fase de Formación en Empresa (FFE) en la Comunidad de Madrid.

Cada tabla incluye:

- Campo en Excel → nombre exacto de la columna/atributo.
- Descripción → explicación del uso del campo.
- Autogenerado → si el campo se calcula automáticamente en el Excel (Sí/No).
- Uso en documentos → marcado con ✔️ si se usa en al generación de ese documento.

📑 Pestañas del Excel

- Tutoria: contiene los datos individuales de cada alumno y de la empresa asociada, incluyendo información personal, contacto, fechas, horario y evaluaciones.
- Datos_Extra: datos comunes para todos los alumnos (curso, ciclo formativo, centro educativo, profesor tutor, etc.).
- Datos_RA: información sobre los módulos y resultados de aprendizaje (RA) asociados a la FFE, y si son impartidos en la empresa.
- Días Libres: calendario de días no lectivos que se usan para calcular la fecha final de las prácticas.

### 📝 Tutoria
En esta pestaña se recoge la información principal para realizar el seguimiento del alumno y poder generar la documentación necesaria.

| Campo en Excel | Descripción | Autogenerado | Relación de alumno | Plan de formación | Ficha de seguimiento periódico | Valoración final del tutor |
|----------------|-------------|--------------|----------------|------------------|--------------------------------|-----------------------------|
| Nombre Mostrado | Nombre completo en formato apellidos, nombre | Si |️ ✔️ ||||
| Nombre | Nombre del alumno | No |️||||
| Apellidos | Apellidos del alumno | No |||||
| NIA | Número de Identificación del Alumno | No |||||
| DNI/Pasaporte | Documento de identidad del alumno | No | ✔️ ||||
| Num SS | Número de la Seguridad Social | No |||||
| Tlfn | Teléfono de contacto del alumno | No |||||
| Email educamadrid | Correo oficial del alumno | No |||||
| Email personal | Correo personal alternativo | No |||||
| Fecha Nacimiento | Fecha de nacimiento del alumno | No | ✔️ ||||
| Empresa | Nombre de la empresa colaboradora. Si este campo no está informado, no se generan documentos. | No | ✔️ ||||
| Dirección centro de trabajo | Dirección postal de la empresa | No | ✔️ ||||
| Ciudad | Localidad donde se sitúa la empresa. Este campo se usa para determinar los días libres y por tanto se usará para autocalcular la fecha de fin. | No |||||
| Empresa CIF | Código de identificación fiscal de la empresa | No |||||
| Empresa Email | Correo electrónico de la empresa | No |||||
| Empresa Tlfn | Teléfono de contacto de la empresa | No |||||
| Status | Estado del alumno en la FFE (activo, finalizado, etc.) | No |||||
| Tipo Firma | Indica si la firma es manual o digital | No |||||
| Convenio | Estado en el que está el convenio (Pendiente de firma de la empresa, Pendiente de firma del director...) | No | | | | |
| Relación Alum. | Estado en el que está la relación de alumno (Pendiente de firma de la empresa, Pendiente de firma del director...) | No | | | | |
| Plan Formativo | Estado en el que está el plan formativo (Pendiente de firma de la empresa, Pendiente de firma del director...)  | No | | | | |
| Num Convenio | Número o identificador del convenio | No | ✔️ ||||
| Fecha Convenio | Fecha de firma del convenio | No | ✔️ ||||
| Num Rel Alum | Número o identificador de la relación de alumno | No | ✔️ ||||
| Tutor Emp Nombre | Nombre del tutor de la empresa | No | ✔️ ||||
| Tutor Emp Apellidos | Apellidos del tutor de la empresa | No | ✔️ ||||
| Tutor Emp NIF | Documento de identidad del tutor de la empresa | No | ✔️ ||||
| Tutor Emp Email | Correo electrónico del tutor de la empresa | No | ✔️ ||||
| Tutor Emp Teléfono | Teléfono de contacto del tutor de la empresa | No |||||
| Otros Contactos | Información de contacto adicional en la empresa | No |||||
| Fecha ini | Fecha de inicio de la FFE | No | ✔️ ||||
| Horas | Total de horas de la FFE | No | ✔️ ||||
| L | Horas de asistencia los lunes | No |||||
| M | Horas de asistencia los martes | No |||||
| X | Horas de asistencia los miércoles | No |||||
| J | Horas de asistencia los jueves | No |||||
| V | Horas de asistencia los viernes | No |||||
| S | Horas de asistencia los sábados | No |||||
| D | Horas de asistencia los domingos | No |||||
| D/s | Total de días por semana de asistencia | Sí | ✔️ ||||
| H/s | Total de horas por semana de asistencia | Sí | ✔️ ||||
| Fecha fin | Fecha calculada de fin (teniendo en cuenta las horas totales a realizar, las horas que asiste los días de la semana y teniendo en cuenta los días festivos) | Sí | | | | |
| Fecha fin val | Fecha final. En este campo hay que copiar manualmente el valor de la columna Fecha fin. El calculo de Fecha fin es un calculo con formula muy complejas de Excel que la librería usada para obtener dicha información no es capaz de calcularla, por lo que se hace necesario copiar el valor. Tampoco vale usar una referencia, porque la referencia es a la formula.  | No | ✔️ ||||
| Hora ini | Hora de inicio de jornada | No | ✔️ ||||
| Hora fin | Hora de fin de jornada | No | ✔️ |||||
| Resumen Horario | Descripción resumida del horario | No ||||||
| Enero | Registro de asistencia en enero, para registrar en Raices la información de la seguridad social | Sí | | | | |
| Febrero | Registro de asistencia en febrero, para registrar en Raices la información de la seguridad social | Sí | | | | |
| Marzo | Registro de asistencia en marzo, para registrar en Raices la información de la seguridad social | Sí | | | | |
| Abril | Registro de asistencia en abril, para registrar en Raices la información de la seguridad social | Sí | | | | |
| Mayo | Registro de asistencia en mayo, para registrar en Raices la información de la seguridad social | Sí | | | | |
| Comentarios | Observaciones adicionales del tutor o alumno | No | | | | |


### 📝 Datos_Extra
En esta pestaña hay información común parala generación de documentos para todos los alumnos.

| Campo en Excel | Descripción                                                    | Autogenerado | Relación de alumno | Plan de formación | Ficha de seguimiento periódico | Valoración final del tutor |
|----------------|-------------|--------------|----------------|------------------|--------------------------------|-----------------------------|
| Nombre | Nombre del profesor tutor                                      | No | ✔️ |||||
| Apellidos | Apellidos del profesor tutor                                   | No | ✔️ |||||
| NIF | Documento de identidad del profesor tutor                      | No | ✔️ |||||
| Email | Correo electrónico (EducaMadrid) del profesor tutor            | No | ✔️ |||||
| Telefono | Teléfono de contacto del profesor tutor                        | No ||||||
| Curso | Curso académico en el que se desarrolla la FFE (ej. 2025-2026) | No ||||||
| Centro | Nombre completo del centro educativo                           | No | ✔️ |||||
| Telefono Centro | Teléfono de contacto del centro                                | No ||||||
| Email Centro | Correo electrónico del centro educativo                        | No ||||||
| Ciclo | Nombre completo del ciclo formativo                            | No | ✔️ |||||
| Codigo | Código oficial del ciclo formativo                             | No | ✔️ |||||
| Nivel | Curso o nivel académico (1º, 2º)                               | No | ✔️ |||||
| Grado | Grado del ciclo (Medio, Superior)                              | No | ✔️ |||||
| Regimen | Régimen de enseñanza (General, Intensivo, etc.)                | No | ✔️ |||||
| Codigo grupo | Identificador del grupo de alumnos                             | No ||||||


### 📝 Datos_RA
En la pestaña Datos_RA se recoge información sobre los Resultados de Aprendizaje (RA).

| Campo en Excel | Descripción | Autogenerado | Relación de alumno | Plan de formación | Ficha de seguimiento periódico | Valoración final del tutor |
|----------------|-------------|--------------|--------------------|------------------|--------------------------------|-----------------------------|
| Módulo | Nombre del módulo profesional al que pertenece el resultado de aprendizaje | No | | | | |
| Codigo | Código oficial del módulo | No | | | | |
| RA | Identificador del resultado de aprendizaje (ej. RA1, RA2, etc.) | No | | | | |
| Impartido integramente en la empresa | Indica si el resultado de aprendizaje se desarrolla totalmente en la empresa | No | | | | |

### 📝 Días Libres
En la pestaña Días libres, deberás crear una columna en la tabla por cada población a la que envíes un alumno, esto es porque puede haber empresas que dependiendo de dónde tengan la sede, tengan días libres diferentes. El nombre de la columna deberá coincidir con el campo ciudad que este informado en la pestaña Tutoría.
El Excel hay dos ciudades de ejemplo Madrid, Móstoles.

| Campo en Excel | Descripción                                                | Autogenerado |
|----------------|------------------------------------------------------------|--------------|
| Madrid | Días no lectivo en el calendario de la Comunidad de Madrid | No |
| Móstoles | Días no lectivo específico para el municipio de Móstoles   | No |



