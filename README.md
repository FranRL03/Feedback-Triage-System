# Feedback Triage System

Sistema de triage de feedback/soporte al cliente en tiempo real. Un pipeline de microservicios asíncronos clasifica automáticamente el feedback entrante (sentimiento, urgencia, categoría) usando un LLM, y expone el resultado en tiempo real a un panel de agentes y al propio usuario.

> Proyecto personal construido para aprender y demostrar mensajería asíncrona con **Apache Kafka**, integración de LLMs en producción con **Spring AI**, y comunicación en tiempo real con **WebSocket**, dentro de una arquitectura de microservicios con **Spring Boot**.

## Arquitectura

```
Angular ──POST /feedback──▶ ingestion-service
                                   │
                           Kafka [feedback-raw]
                                   │
                                   ▼
                          ai-classifier-service ──▶ LLM (Gemini)
                                   │
                        Kafka [feedback-classified]
                                   │
                                   ▼
                            dashboard-service
                             │            │
                        PostgreSQL    WebSocket
                        (persistencia)  (tiempo real)
```

La comunicación entre servicios es **asíncrona vía eventos de Kafka**, no síncrona vía REST. Esto aísla al sistema de la latencia e inestabilidad de la llamada al LLM: `ingestion-service` responde `202 Accepted` de inmediato, y el ticket evoluciona de estado `RECEIVED → CLASSIFIED` en segundo plano.

## Servicios

| Servicio | Responsabilidad |
|---|---|
| `shared-events` | Librería compartida (sin Spring Boot) con los eventos y enums que forman el contrato entre servicios. |
| `ingestion-service` | Recibe el feedback vía REST, valida y publica el evento en Kafka. |
| `ai-classifier-service` | Consume el evento, clasifica con un LLM (Spring AI, salida estructurada) y publica el resultado. |
| `dashboard-service` | Persiste el ciclo de vida del ticket en Postgres y notifica por WebSocket (panel de agente + usuario individual). |

## Stack

- **Java 21** · **Spring Boot 4.1** · Maven multi-módulo
- **Apache Kafka** (modo KRaft) — mensajería asíncrona
- **Spring AI** + **Google Gemini** — clasificación con salida estructurada
- **PostgreSQL** + Spring Data JPA — persistencia
- **Spring WebSocket** (sin STOMP) — notificaciones en tiempo real
- **Docker Compose** — Kafka, Postgres y pgAdmin en local

## Cómo ejecutarlo

### 1. Requisitos

- JDK 21, Maven, Docker Desktop
- Una API key de [Google AI Studio](https://aistudio.google.com/) (tier gratuito) para Gemini

### 2. Infraestructura local

```bash
docker compose up -d
```

Levanta Kafka (`localhost:9092`), PostgreSQL (`localhost:5434`) y pgAdmin (`localhost:5050`).

### 3. Módulo compartido

```bash
mvn install
```

Desde la raíz del repositorio: compila e instala `shared-events` (y el resto de módulos) en el orden correcto.

### 4. Configurar la API key

Crea `ai-classifier-service/src/main/resources/secrets.properties` (no versionado) con:

```properties
GOOGLE_API_KEY=tu-api-key-aqui
```

### 5. Arrancar los servicios

Desde el IDE, arranca cada uno en su propio puerto:

- `ingestion-service` → `8080`
- `ai-classifier-service` → `8081`
- `dashboard-service` → `8082`

### 6. Probar

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
- **DTO de API ≠ evento de dominio**: el body HTTP (`FeedbackSubmissionRequest`) es distinto del evento de Kafka (`FeedbackRawEvent`) — el cliente no puede enviar campos que genera el backend (`feedbackId`, timestamps).
- **Estado como máquina de dos pasos**: `RECEIVED → CLASSIFIED`, para poder responder al instante sin esperar a la clasificación.
- **`needsReview` como escape valve**: el LLM puede marcar un ticket para revisión humana en vez de forzar una clasificación de baja confianza.
- **WebSocket sin STOMP**: con una única instancia de `dashboard-service`, un broker relay añade complejidad sin resolver ningún problema real; se optó por `WebSocketHandler` plano con colecciones en memoria thread-safe.

## Mejoras pendientes

- Autenticación (JWT) y asociación de tickets a un usuario
- Manejo explícito de eventos fuera de orden (estado `FAILED`)
- Few-shot examples en el prompt para mejorar la consistencia de `needsReview`
- Tests de integración con Testcontainers
- Despliegue en la nube / Kafka gestionado
- Frontend Angular (en desarrollo)

## Estructura del repositorio

```
feedback-triage-system/
├── shared-events/          # Contrato de eventos (sin Spring Boot)
├── ingestion-service/      # Entrada REST + productor Kafka
├── ai-classifier-service/  # Consumidor + clasificación con IA
├── dashboard-service/      # Consumidor + persistencia + WebSocket
├── docker-compose.yml      # Kafka, Postgres, pgAdmin
└── pom.xml                 # POM agregador
```
