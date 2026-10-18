# RentCar — Parcial 2 de PGII

> *Este archivo hace parte de la entrega.* El equipo debe completarlo y mantenerlo actualizado de acuerdo con los requisitos del enunciado y del documento técnico.

## 1. Integrantes

- Jose Alberto Hernández León
- Alejandra Ibarra Ortiz

## 2. Descripción de la solución

<!-- Describan brevemente qué resuelve la aplicación y qué funcionalidades implementó el equipo. -->

RentCar es una aplicación de escritorio desarrollada en Java 21 
con JavaFX y FXML diseñada para la gestión integral del alquiler de vehículos. 
La aplicación implementa una arquitectura por capas basada en Repository-Service-Controller-View 
coordinada por un Service Locator central (ApplicationServices).

La solución permite gestionar clientes, flota de vehículos, 
modalidades de alquiler personalizables, solicitudes de reserva 
con cálculo recursivo de servicios adicionales, composición 
de paquetes y procesamiento seguro de pagos a través 
de una API HTTP externa. La arquitectura fue construida bajo
principios SOLID y aplica los 7 patrones de diseño
(Creacionales y Estructurales).
## 3. Requisitos y funcionalidades implementadas

<!-- Resuman las funcionalidades implementadas y relacionen cada una con los requisitos del enunciado. -->
### RF-001 - Construcción de solicitudes de alquiler
Permite la preparación y validación rigurosa de una solicitud
(RentalRequest) vinculando un cliente, un vehículo, rango de fechas, 
modalidad y bandera de servicios opcionales. Separado totalmente 
de la UI a través del patrón Builder.

### RF-002 - Gestión del proceso de alquiler y reservación
Administra el ciclo de vida de una reserva (NormalReservation),
comprobando disponibilidad de la flota, calculando tarifas 
según la duración en días con ChronoUnit y registrando el estado 
final de las operaciones confirmadas o rechazadas en memoria.

### RF-003 - Modalidades de alquiler
Ofrece tres modalidades de alquiler (EconomyMode, ExecutiveMode, PremiumMode) 
implementadas con Abstract Factory. Las familias de productos
(Rate, Mileage, MedicalPolicy, RoadsideAssistance) se configuran
y ejecutan dinámicamente mediante RentalStore sin valores fijos en código.


### RF-004 - Procesamiento de pagos mediante un servicio externo
Conecta el sistema con la API REST externa de pagos mediante
el patrón Adapter (PaymentAdapter), traduciendo los modelos 
del dominio a las solicitudes HTTP de PaymentApiClient y 
gestionando respuestas exitosas, rechazos y fallos de conexión.

### RF-005 - Servicios adicionales
Permite adicionar opcionalmente accesorios (GPS, silla de bebé, conductor adicional) 
a una reserva existente mediante el patrón Decorator, 
componiendo la descripción y el precio final de forma 
recursiva sin multiplicación exponencial de clases.

### RF-006 - Verificación previa al pago
Intercepta el flujo de cobro utilizando el patrón Proxy
(PaymentProxy), consultando CustomerVerificationService para 
validar que el cliente esté autorizado antes de invocar
la API externa.
### RF-007 - Paquetes de servicios
Estructura servicios individuales (IndividualService) 
y agrupaciones (ServicePackage) mediante el patrón Composite 
(ServiceComponent), permitiendo el cálculo unificado de 
tarifas para estructuras simples y anidadas.



## 4. Arquitectura y organización del código

<!-- Expliquen la arquitectura utilizada, la responsabilidad de las capas y cómo se relacionan. -->

El proyecto está organizado en diferentes paquetes para 
separar las clases según la función que cumplen dentro de la aplicación.
Dentro del proyecto se encuentran paquetes como:

- model: contiene las clases que representan los diferentes elementos utilizados en la aplicación.
- service: Contiene las clases encargadas de realizar diferentes procesos de la aplicación.
- repository: Contiene las clases utilizadas para manejar los datos.
- client: Contiene las clases relacionadas con la comunicación con servicios externos.
- test: Contiene las pruebas realizadas a las diferentes funcionalidades.

La organización permite encontrar fácilmente cada parte 
del proyecto y trabajar en diferentes funcionalidades sin
tener todo el código mezclado.

El proyecto utiliza una arquitectura limpia desacoplada 
por responsabilidades:

- edu.rentcar.model: Contiene la lógica del dominio, entidades,
interfaces de patrones (Abstract Factory, Decorator, Composite)
y constructores (Builder).
- edu.rentcar.repository: Capa de persistencia temporal en
memoria para gestionar colecciones de datos 
(VehicleRepository, RentalRequestRepository, RentalModeRepository,
ReservationRepository).
- edu.rentcar.service: Lógica de negocio, validaciones,
composición de decoradores y orquestación de servicios 
externos.
- edu.rentcar.controller: Controladores JavaFX que conectan 
los eventos de la vista FXML con la capa de servicios a 
través del Service Locator.
- edu.rentcar.view: Vistas FXML de la interfaz gráfica y hojas
de estilo CSS.
- edu.rentcar.app: Clase central ApplicationServices que
actúa como Service Locator inyectando dependencias entre
módulos.
- edu.rentcar.client / adapter: Clientes HTTP de comunicación 
externa y adaptadores de API.

## 5. Patrones de diseño

<!-- Para cada patrón requerido, documenten el problema que resuelve, las clases participantes y dónde se utiliza. -->

### Builder (Creacional)
Problema que resuelve: La creación de objetos RentalRequest
involucra múltiples parámetros obligatorios y opcionales,
lo que generaría constructores telescópicos o datos 
inconsistentes.

Clases participantes: RentalRequest.Builder, RentalRequest.

Ubicación en código: edu.rentcar.model.RentalRequest y 
consumido en RentalRequestService.

Ventajas: Inmutabilidad del objeto construido y separación
clara entre el proceso de construcción y la representación.

### Abstract Factory (Creacional)
Problema que resuelve: Garantizar la creación coherente de
familias de productos relacionados (tarifa, kilometraje, 
póliza médica y asistencia) según la modalidad de alquiler
seleccionada.

Clases participantes: Interfaz RentalMode, fábricas concretas
(EconomyMode, ExecutiveMode, PremiumMode), interfaces de 
productos (Rate, Mileage, MedicalPolicy, RoadsideAssistance)
y sus implementaciones concretas.

Ubicación en código: edu.rentcar.model.* y orquestado 
en RentalStore.

Ventajas: Facilita la incorporación de nuevas modalidades sin
alterar la lógica de negocio principal.

### Adapter (Estructural)
Problema que resuelve: Incompatibilidad entre la interfaz
interna del dominio PaymentService y el cliente HTTP 
suministrado PaymentApiClient.

Clases participantes: PaymentService (interfaz), PaymentAdapter
(adaptador), PaymentApiClient (adaptee).

Ubicación en código: edu.rentcar.service.PaymentAdapter.

Ventajas: La lógica de la aplicación no depende del formato
específico de solicitudes ni respuestas de la API del 
proveedor externo.
### Proxy (Estructural)
Problema que resuelve: Controlar el acceso al servicio de
pago real, ejecutando verificaciones de autorización previas
para evitar llamadas innecesarias o no autorizadas a la API 
externa.

Clases participantes: PaymentService (interfaz), 
PaymentProxy (proxy), PaymentAdapter (sujeto real), 
CustomerVerificationService.

Ubicación en código: edu.rentcar.service.PaymentProxy.

Ventajas: Añade una capa de seguridad y optimización de
recursos sin modificar el cliente de pago.

### Decorator (Estructural)
Problema que resuelve: Evitar la explosión de clases al
combinar múltiples servicios adicionales opcionales
(GPS, Silla de bebé, Conductor extra) en una reserva.

Clases participantes: Interfaz Reservation, NormalReservation
(componente base), ReservationDecorator (decorador abstracto),
GPSDecorator, CarSeatDecorator, AditionalDriverDecorator.

Ubicación en código: edu.rentcar.model.*Decorator y 
ReservationService.

Ventajas: Cálculo de precio y generación de descripción 
mediante delegación y recursión dinámica usando composición
funcional (Function<Reservation, Reservation>).

### Composite(Estructural)
Problema que resuelve: Tratar componentes individuales de 
servicios y agrupaciones/paquetes complejos de manera
uniforme.

Clases participantes: ServiceComponent (componente abstracto),
IndividualService (hoja), ServicePackage (compuesto).

Ubicación en código: edu.rentcar.model.ServiceComponent, 
IndividualService, ServicePackage.

Ventajas: Permite construir estructuras jerárquicas de 
servicios (paquetes dentro de paquetes) calculando costos 
totales de forma transparente.


## 6. Ejecución de la aplicación

### Requisitos

- JDK 21.
- Conexión a Internet la primera vez que Gradle necesite descargar la distribución o dependencias.

### Ejecutar

Desde la raíz del proyecto:

- *Windows PowerShell:* ` .\gradlew.bat run `
- *Git Bash / macOS / Linux:* ` ./gradlew run `

En IntelliJ IDEA también se puede ejecutar la tarea application > run desde la ventana Gradle.

### Ejecutar las pruebas

- *Windows PowerShell:* ` .\gradlew.bat test `
- *Git Bash / macOS / Linux:* ` ./gradlew test `

## 7. Integración de pagos

<!-- Documenten cómo configurar y probar la integración que implementó el equipo, según el enunciado. No incluyan claves, contraseñas ni tokens reales. -->

La aplicación cuenta con una parte encargada de manejar los pagos mediante una API.

El *PaymentAdapter* se encarga de comunicarse con
la API y convertir sus respuestas para que
puedan ser utilizadas por la aplicación.
Se tienen en cuenta diferentes situaciones:

- Pago aprobado.
- Pago rechazado.
- Error de la API.
- Problemas de comunicación.
- Solicitud interrumpida.

Antes de procesar el pago también se utiliza
*PaymentProxy*, que verifica si el cliente 
está autorizado.
De esta manera, un cliente que no esté autorizado
no puede continuar con el proceso de pago.

Escenarios Manejados:

Aprobado: Pago procesado exitosamente por
la API.

Rechazado: Transacción declinada por fondos 
o reglas de la API.

Error de Conexión / HTTP: Manejo de excepciones
sin detener abruptamente la interfaz gráfica.

## 8. Pruebas realizadas

<!-- Describan las pruebas unitarias y funcionales, cómo ejecutarlas y qué resultados obtuvieron. -->

Se diseñó una suite de pruebas automatizadas 
con JUnit 5 cubriendo los requisitos exigidos:

### RentalRequestTest (RF-001):

Verificación de construcción exitosa mediante 
Builder con datos válidos.
Validación de rechazo ante fechas inconsistentes
o campos obligatorios nulos.

### ReservationServiceTest (RF-002, RF-005):

Cálculo de días entre fechas usando ChronoUnit.
Verificación de la pila del patrón Decorator
sumando la tarifa base de RentalStore con 
montos dinámicos de accesorios.

### RentalModeTest (RF-003):

Creación de familias de productos mediante
Abstract Factory y mapeo de nombres de clase 
con Function.

### PaymentIntegrationTest (RF-004, RF-006):

Verificación de bloqueo de pago en PaymentProxy 
cuando el cliente no está autorizado.
Prueba de transformación y adaptación de 
respuestas mediante PaymentAdapter.

### ServicePackageTest (RF-007):

Cálculo de precios en hojas (IndividualService)
y compuestos anidados (ServicePackage).

## 9. Limitaciones y decisiones de diseño

<!-- Registren las limitaciones conocidas y justifiquen las decisiones relevantes. -->
Persistencia en memoria: Toda la información almacenada 
en los repositorios (List<T>) se mantiene durante el 
ciclo de vida de la ejecución y se reinicia al cerrar 
la aplicación, conforme a los términos del parcial.

Cálculo de tarifas: Se utilizó RentalStore como contenedor
dinámico para almacenar las tarifas parametrizadas por 
el usuario, evitando valores hardcoded en los algoritmos 
de costos.

Reflexión para desenrollar decoradores: En la capa de 
presentación se implementó un desempaquetado de decoradores
vía reflexión para extraer metadatos de la reserva base
(NormalReservation) sin romper la encapsulación de las 
clases decoradoras.

## 10. Evidencias

<!-- Agreguen enlaces o referencias a las evidencias solicitadas en el enunciado. -->
Repositorio Git: https://github.com/AlejandraIbarraOrtiz/Parcial-II.git

Video de sustentación:

Documento Técnico: Diagrama de clases UML 