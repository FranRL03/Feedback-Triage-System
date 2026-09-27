# Feedback Triage System

Sistema de triage de feedback/soporte al cliente en tiempo real. Un pipeline de microservicios asíncronos clasifica automáticamente el feedback entrante (sentimiento, urgencia, categoría) usando un LLM, y expone el resultado en tiempo real a un panel de agentes y al propio usuario, con un frontend en Angular conectado de extremo a extremo.

> Proyecto construido con mensajería asíncrona con **Apache Kafka**, integración de LLMs en producción con **Spring AI**, y comunicación en tiempo real con **WebSocket**, dentro de una arquitectura de microservicios con **Spring Boot** y un frontend en **Angular**.

## Arquitectura

```
Angular ──POST /feedback──▶ ingestion-service
   ▲                              │
   │                      Kafka [feedback-raw]
   │                              │
   │                              ▼
   │                     ai-classifier-service ──▶ LLM (Gemini)
   │                              │
   │                    Kafka [feedback-classified]
   │                              │
   │                              ▼
   │                       dashboard-service
   │                        │            │
   │                   PostgreSQL    WebSocket
   │                  (persistencia)     │
   └──────── /ws/dashboard, /ws/feedback/{id} ────┘
```

La comunicación entre servicios es **asíncrona vía eventos de Kafka**, no síncrona vía REST. Esto aísla al sistema de la latencia e inestabilidad de la llamada al LLM: `ingestion-service` responde `202 Accepted` de inmediato, y el ticket evoluciona de estado `RECEIVED → CLASSIFIED` en segundo plano. El frontend se entera de ese cambio sin recargar, a través de dos canales WebSocket independientes.

## Servicios

| Servicio | Responsabilidad |
|---|---|
| `shared-events` | Librería compartida (sin Spring Boot) con los eventos y enums que forman el contrato entre servicios. |
| `ingestion-service` | Recibe el feedback vía REST, valida y publica el evento en Kafka. |
| `ai-classifier-service` | Consume el evento, clasifica con un LLM (Spring AI, salida estructurada) y publica el resultado. |
| `dashboard-service` | Persiste el ciclo de vida del ticket en Postgres, expone consulta paginada/filtrable y notifica por WebSocket (panel de agente + usuario individual). |
| `feedback-triage-frontend` | Angular: formulario de envío con confirmación en tiempo real, y panel de agente con listado filtrable/paginado y actualizaciones en vivo. |

## Stack

**Backend**
- **Java 21** · **Spring Boot 4.1** · Maven multi-módulo
- **Apache Kafka** (modo KRaft) — mensajería asíncrona
- **Spring AI** + **Google Gemini** — clasificación con salida estructurada
- **PostgreSQL** + Spring Data JPA (Specifications para filtrado dinámico) — persistencia
- **Spring WebSocket** (sin STOMP) — notificaciones en tiempo real
- **Docker Compose** — todo el sistema (Kafka, Postgres, pgAdmin, los 3 microservicios y el frontend) se construye y levanta con un único comando, vía Dockerfiles multi-stage por servicio

**Frontend**
- **Angular** (NgModules) · TypeScript
- `HttpClient` para REST, `WebSocket` nativo envuelto en `Observable` para tiempo real
- Bootstrap para estilos base, Angular Material (iconos) y `ng-bootstrap` (paginación)

## Funcionalidades del frontend

- **Formulario de feedback**: envío del ticket, confirmación inmediata (`202 Accepted`), y un stepper "Recibido → Clasificado" que se actualiza solo en cuanto el ticket termina de clasificarse, vía WebSocket individual (`/ws/feedback/{feedbackId}`).
- **Panel de agente**: listado paginado de tickets con badges de color por urgencia, sentimiento y estado; filtros combinables por urgencia y estado (resueltos en el backend con `JpaSpecificationExecutor`, no en el cliente, para que paginación y filtrado funcionen correctamente juntos); tickets nuevos y reclasificaciones aparecen en vivo vía WebSocket broadcast (`/ws/dashboard`), sin recargar la página.

## Cómo ejecutarlo

### Opción A — Todo con un comando (recomendado)

Todo el sistema (Kafka, Postgres, pgAdmin, los 3 microservicios y el frontend) se construye y levanta junto, sin necesitar IDE ni instalar Maven/Node en el sistema.

**Requisitos:** Docker Desktop, y una API key de [Google AI Studio](https://aistudio.google.com/) (tier gratuito) para Gemini.

1. Crea un archivo `.env` en la raíz del repositorio con:
   ```
   GOOGLE_API_KEY=tu-api-key-aqui
   ```
2. Levanta todo:
   ```bash
   docker compose up -d --build
   ```
3. Verifica que los 7 contenedores están arriba:
   ```bash
   docker ps
   ```
4. Abre `http://localhost:4200`.

Cada servicio Java se construye con un `Dockerfile` multi-stage propio (`ingestion-service/Dockerfile`, etc.): una primera fase compila con Maven (instalando primero `shared-events`, ya que los otros tres módulos dependen de él), y una segunda fase copia solo el `.jar` final a una imagen ligera de JRE. El frontend se compila con Node y se sirve con Nginx.

**Nota sobre la red interna de Docker:** dentro de la red de Compose, los servicios se resuelven por su nombre (`kafka`, no `localhost`), inyectado vía variables de entorno (`SPRING_KAFKA_BOOTSTRAP_SERVERS`, `SPRING_DATASOURCE_URL`) que sobreescriben los valores de `application.properties` pensados para desarrollo local.

### Opción B — Desarrollo local, servicio a servicio

Útil para depurar un servicio concreto desde el IDE mientras el resto corre en Docker.

1. **Infraestructura**: `docker compose up -d kafka pg_develop pgadmin` — levanta Kafka (`localhost:9092`), PostgreSQL (`localhost:5434`) y pgAdmin (`localhost:5050`).
2. **Módulo compartido**: `mvn install` desde la raíz — compila e instala `shared-events` (y el resto de módulos Java) en el orden correcto.
3. **API key**: crea `ai-classifier-service/src/main/resources/secrets.properties` (no versionado) con `GOOGLE_API_KEY=tu-api-key-aqui`.
4. **Backend**: arranca cada servicio desde el IDE, en su propio puerto — `ingestion-service` (8080), `ai-classifier-service` (8081), `dashboard-service` (8082).
5. **Frontend**:
   ```bash
   cd feedback-triage-frontend
   npm install
   ng serve
   ```
   Disponible en `http://localhost:4200`.

### Probar

Desde `http://localhost:4200`, rellena y envía el formulario de feedback — verás la confirmación inmediata y, unos segundos después, el estado pasando a "Clasificado" sin recargar. En el panel de agente verás el ticket aparecer con su urgencia, sentimiento y categoría en tiempo real.

También puede probarse directamente contra el backend:

```http
POST http://localhost:8080/feedback
Content-Type: application/json

{
  "message": "Llevo 3 días sin poder iniciar sesión, es urgente",
  "emailContact": "usuario@example.com",
  "channel": "WEB"
}
```

Respuesta inmediata `202 Accepted` con un `feedbackId`. Conéctate a `ws://localhost:8082/ws/dashboard` (o `ws://localhost:8082/ws/feedback/{feedbackId}`) para ver la clasificación llegar en tiempo real.

## Decisiones de diseño destacadas

- **Módulo compartido mínimo**: `shared-events` solo contiene el contrato entre servicios (eventos y enums), nunca configuración ni lógica específica de un servicio.
- **DTO de API ≠ evento de dominio**: el body HTTP (`FeedbackSubmissionRequest`) es distinto del evento de Kafka (`FeedbackRawEvent`) — el cliente no puede enviar campos que genera el backend (`feedbackId`, timestamps). Lo mismo aplica al DTO de respuesta del panel de agente, distinto de la entidad JPA.
- **Estado como máquina de dos pasos**: `RECEIVED → CLASSIFIED`, para poder responder al instante sin esperar a la clasificación.
- **`needsReview` como escape valve**: el LLM puede marcar un ticket para revisión humana en vez de forzar una clasificación de baja confianza.
- **WebSocket sin STOMP**: con una única instancia de `dashboard-service`, un broker relay añade complejidad sin resolver ningún problema real; se optó por `WebSocketHandler` plano con colecciones en memoria thread-safe.
- **Filtrado con `Specification`, no en el cliente**: filtrar y paginar en el frontend son incompatibles entre sí (el filtro solo vería la página ya recortada); por eso el filtrado combinable por urgencia/estado se resuelve en el backend con `JpaSpecificationExecutor`, aplicado junto con la paginación en la misma consulta.
- **Secretos fuera de la imagen**: la API key de Gemini se inyecta en tiempo de ejecución vía `.env` + variable de entorno, nunca se copia dentro de un `Dockerfile` ni queda incrustada en una imagen construida.

## Mejoras pendientes

- Autenticación (JWT) y asociación de tickets a un usuario
- Manejo explícito de eventos fuera de orden (estado `FAILED`)
- Few-shot examples en el prompt para mejorar la consistencia de `needsReview`
- Tests de integración con Testcontainers
- Despliegue en la nube / Kafka gestionado
- Endpoint `GET /feedback/{id}` individual, para no depender de recargar la primera página cuando llega un ticket completamente nuevo por WebSocket

## Estructura del repositorio

```
feedback-triage-system/
├── shared-events/             # Contrato de eventos (sin Spring Boot)
├── ingestion-service/         # Entrada REST + productor Kafka (+ Dockerfile)
├── ai-classifier-service/     # Consumidor + clasificación con IA (+ Dockerfile)
├── dashboard-service/         # Consumidor + persistencia + WebSocket + filtrado (+ Dockerfile)
├── feedback-triage-frontend/  # Angular: formulario y panel de agente (+ Dockerfile)
│   └── src/app/
│       ├── core/               # Modelos, enums y servicios transversales
│       ├── features/           # Formulario de feedback y panel de agente
│       └── shared/             # Componentes reutilizables (header, card)
├── docker-compose.yaml        # Kafka, Postgres, pgAdmin + los 4 servicios de la app
├── .env                       # GOOGLE_API_KEY (no versionado)
└── pom.xml                    # POM agregador (módulos Java)
```
