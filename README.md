# Voice RAG Customer Care Assistant

Minimal voice-enabled assistant with RAG over a small FAQ set.

## Quick Start (≤ 15 min)

Prerequisites: Java 17+, Maven, a Gemini API key.

1) Clone
```bash
git clone <your-repo-url>
cd voice-rag-assistant
```

2) Configure env
```bash
cp env.example .env
echo GEMINI_API_KEY=your_api_key_here >> .env
```

3) Run
```bash
./mvnw spring-boot:run
# or
mvn spring-boot:run
```

4) Open UI
```
http://localhost:8080
```

## What it does
- Speak or type a question; hear a spoken answer (you can interrupt speech).
- RAG over ~15 FAQs with source citations.
- Two flows: FAQ and Order Status (stub).

## API (quick test)
```bash
curl http://localhost:8080/api/assistant/health
curl -X POST http://localhost:8080/api/assistant/chat -H "Content-Type: application/json" -d '{"message":"What is your return policy?"}'
```

## Configuration
- Set `GEMINI_API_KEY` in environment or `.env`.
- Port: `SERVER_PORT` or default 8080.

## Paths
- FAQs: `src/main/resources/data/sample-faqs.json`
- UI: `src/main/resources/static/index.html`
- Backend: `src/main/java/com/customercare`

## Deploy
Deploy on any PaaS (Render/Heroku/Railway). Set `GEMINI_API_KEY` in the platform env.
