Prueba técnica para desarrollador backend springboot
Autor: Miguel Antonio Hernandez Martinez - Email: m_hernandez_89@outlook.com
Realizado el 16/11/2025

API en Java con Spring Boot que integra una API externa de productos (fakestoreapi) y gestiona órdenes, pagos simulados y mapeos de rutas. 
El proyecto muestra patrones de diseño orientados a buenas prácticas (se aplicaron mejoras para seguir principios SOLID: 
separación de responsabilidades, inversión de dependencias, mappers, adaptadores).

Estado actual
------------
- Spring Boot (WebFlux presente para integración reactiva con WebClient).
- Endpoints HTTP REST para productos, órdenes y pagos.
- Persistencia JPA para entidades Order/OrderItem (repositorio `OrderRepository`).
- Cliente HTTP reactivo para obtener productos desde https://fakestoreapi.com.
- Manejo global de excepciones (RestExceptionHandler).
- Tests básicos de contexto (PruebaApiSpringBootApplicationTests).

Estructura principal del proyecto
-------------------------------
- src/main/java/com/ejemplo/PruebaAPISpringBoot
  - controller
    - ProductController — expone endpoints para listar productos y obtener por id (reactivo).
    - PaymentController — endpoint para procesar pagos (simulado).
    - MappingController — endpoint utilitario `/__mappings` para ver mappings registrados.
  - service
    - ProductService — capa de negocio/consulta de productos (implementa ProductQueryService).
    - OrderService — orquestador para creación y consulta de órdenes.
    - PaymentService — orquesta el procesamiento de pagos (usa un procesador simulado).
  - service.client
    - ProductApiClient — interfaz para acceder a fuente externa de productos (reactiva).
    - ProductWebClientImpl — implementación con WebClient (falla con fallback).
    - ProductBlockingAdapter — adaptador que encapsula `.block()` para compatibilidad sin propagar bloqueo por todo el código.
  - mapper
    - OrderMapper — convierte OrderRequestDto -> Order (separando responsabilidad del service).
  - repository
    - OrderRepository — repositorio JPA para Order y OrderItem.
  - entity
    - Order, OrderItem, Client — entidades JPA.
  - dto
    - ProductDto, OrderRequestDto, PaymentRequestDto, PaymentResponseDto — objetos de transferencia.
  - exception
    - RestExceptionHandler — captura errores globales y formatea respuestas.
    - (posible) OrderNotFoundException — excepción específica para órdenes no encontradas.

Principales funcionalidades (endpoints)
---------------------------------------

Productos (integración externa / reactivo)
- GET /api/products
  - Descripción: devuelve la lista de productos obtenidos desde fakestoreapi (reactivo, Mono<List<ProductDto>>).
  - Ejemplo:
    curl -s http://localhost:8080/api/products

- GET /api/products/{id}
  - Descripción: devuelve un producto por id. Si no existe, responde 404.
  - Ejemplo:
    curl -s http://localhost:8080/api/products/1

Órdenes
- POST /api/orders  (si existe el controlador que expone creación, o endpoint equivalente)
  - Descripción: crea una nueva orden a partir de OrderRequestDto. El flujo:
    1. Se valida y mapea el request a una entidad Order (OrderMapper).
    2. Se consultan los datos de producto (title, price) usando el adaptador/cliente de productos.
    3. Se calculan totales y se persiste la orden con OrderRepository.
  - Notas: Para evitar N bloqueos secuenciales, el código actual hace prefetch por ids y completa items con un mapa; si se desea throughput más alto, se recomienda migrar createOrder a flujo reactivo o usar paralelismo controlado.

- GET /api/orders
  - Descripción: lista de órdenes (OrderRepository.findAll).

- GET /api/orders/{id}
  - Descripción: obtiene información completa de una orden (con items).

- POST /api/orders/{id}/pay  (o endpoint de pago centralizado)
  - En este proyecto, PaymentController expone /api/payments/pay para procesar pagos sobre una orden existente.

Pagos (simulados)
- POST /api/payments/pay
  - Request: PaymentRequestDto { orderId, cardNumber, cardHolder, expiry, cvv }
  - Comportamiento: existe un PaymentProcessor simulado. La lógica actual: si el número de tarjeta termina en '0' se simula rechazo (se cancela la orden), en otro caso se marca como pagada.
  - Respuesta: PaymentResponseDto { success, message, orderId } (200 OK o 400 Bad Request según resultado).

Manejo de errores
-----------------
- RestExceptionHandler captura excepciones y devuelve un payload con timestamp, status, error y message.
- Recomendación: añadir ErrorResponse DTO para estandarizar formatos y manejar OrderNotFoundException → 404.

  

Cómo ejecutar
-------------
Prerequisitos:
- Java 17+ (o versión definida en pom.xml)
- Maven (o usar ./mvnw incluido)
- Base de datos configurada (si se usan datos persistentes). El proyecto usa JPA; por defecto puede usar H2 o la configuración del application.properties.

Peticiones principales y ejemplos

A) Crear orden (si existe endpoint POST /api/orders)
- URL: {{base_url}}/api/orders
- Method: POST
- Headers: Content-Type: application/json
- Body (raw JSON) ejemplo:
{
  "clientName": "Juan Perez",
  "items": [
    { "productId": 1, "quantity": 2 },
    { "productId": 5, "quantity": 1 }
  ]
}
- Respuesta esperada: JSON con la orden creada y su id. Copia el id a la variable `orderId` del Environment para usarlo en pagos.

B) Procesar pago (POST /api/payments/pay)
- URL: {{base_url}}/api/payments/pay
- Method: POST
- Headers: Content-Type: application/json
- Body (raw JSON) ejemplo para pago aprovado:
{
  "orderId": {{orderId}},
  "cardNumber": "4242424242424242",
  "cardHolder": "Juan Perez",
  "expiry": "12/26",
  "cvv": "123"
}
- Body ejemplo para pago rechazado (simulado):
{
  "orderId": {{orderId}},
  "cardNumber": "4111111111111110",
  "cardHolder": "Juan Perez",
  "expiry": "12/26",
  "cvv": "123"
}
- Comportamiento aplicado en el proyecto: si `cardNumber` termina en "0" → pago rechazado (se responde 400 y la orden se marca CANCELLED). Si no termina en "0" → pago aprobado (200) y la orden se marca PAID.
- Respuesta ejemplo:
  - Aprobado: { "success": true, "message": "Payment approved (simulated)", "orderId": 123 }
  - Rechazado: { "success": false, "message": "Payment declined (simulated)", "orderId": 123 }

C) Consultar orden
- URL: {{base_url}}/api/orders/{{orderId}}
- Method: GET
- Devuelve la orden con items y estado. Útil para verificar que el estado cambió a PAID o CANCELLED tras el pago.
