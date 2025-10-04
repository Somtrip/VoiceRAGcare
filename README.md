# Voice RAG Customer Care Assistant

A smart, voice-enabled customer service assistant that uses Retrieval-Augmented Generation (RAG) to provide accurate answers from your FAQ knowledge base. Speak or type your questions and get instant, context-aware responses with source citations.

![Live Demo](https://img.shields.io/badge/Demo-Live%20Now-brightgreen.svg)
![Customer Support Assistant](https://img.shields.io/badge/Version-1.0.0-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-green.svg)
![Java](https://img.shields.io/badge/Java-21-orange.svg)

##  Live Demo

**Try it now:** [https://voiceragcare-production.up.railway.app/](https://voiceragcare-production.up.railway.app/)


## Features

- **Voice & Text Input**: Speak naturally or type your questions
- **Smart RAG System**: Retrieves relevant information from FAQs with semantic search
- **Order Status Check**: Real-time order tracking (with mock data)
- **Text-to-Speech**: Hear spoken responses with interrupt capability
- **Beautiful Web UI**: Modern, responsive interface
- **Fast Responses**: Optimized for low latency
- **Source Citations**: Always shows where information comes from

## 🚀 Quick Setup

### Prerequisites
- **Java 21** or later
- **Maven** (included with wrapper)
- **Gemini API Key** from [Google AI Studio](https://makersuite.google.com/app/apikey)

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/Somtrip/VoiceRAGcare.git
cd VoiceRAGcare
```

2. **Set up environment**
```bash
# Copy the example environment file
cp env.example .env

# Add your Gemini API key
echo "GEMINI_API_KEY=your_actual_api_key_here" >> .env
```

3. **Run the application**
```bash
# Using Maven wrapper (recommended)
./mvnw spring-boot:run

# Or with installed Maven
mvn spring-boot:run
```

4. **Open your browser**
```
http://localhost:8080
```

That's it! The application will automatically:
- Load sample FAQs into the database
- Generate embeddings for semantic search
- Start the web server

## 🎯 How It Works

### Ask Anything
- **"What's your return policy?"** 
- **"How do I track my order?"**
- **"What payment methods do you accept?"**
- **"What's the status of order 12345?"**

### Two Smart Modes
1. **FAQ Mode**: Answers questions using your knowledge base with source citations
2. **Order Status Mode**: Checks order tracking and delivery estimates

### Intelligent Processing
- **Intent Classification**: Automatically detects if you're asking about orders or general questions
- **Semantic Search**: Finds the most relevant FAQs using AI embeddings
- **Keyword Fallback**: Works even when embeddings aren't available
- **Context-Aware Responses**: Generates natural, helpful answers

## API Usage

### Quick Testing
```bash
# Health check
curl http://localhost:8080/api/assistant/health

# Chat endpoint
curl -X POST http://localhost:8080/api/assistant/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"What is your return policy?"}'

# Test order status
curl -X POST http://localhost:8080/api/assistant/test-order \
  -H "Content-Type: application/json" \
  -d '{"orderId":"12345"}'
```

### Debug Endpoints
```bash
# Check loaded FAQs
curl http://localhost:8080/api/assistant/debug/faqs

# Test search
curl "http://localhost:8080/api/assistant/debug/search?query=returns"
```

## ⚙️ Configuration

### Environment Variables
Create a `.env` file in the root directory:
```properties
GEMINI_API_KEY=your_gemini_api_key_here
SERVER_PORT=8080  # Optional, defaults to 8080
```

### Application Properties
Key settings in `src/main/resources/application.properties`:
```properties
# Server
server.port=8080

# Database (H2 in-memory)
spring.datasource.url=jdbc:h2:mem:testdb

# Gemini AI
gemini.api.key=${GEMINI_API_KEY}

# CORS (enabled for all origins in development)
spring.web.cors.allowed-origins=*
```

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/customercare/
│   │   ├── controller/          # REST API endpoints
│   │   ├── service/            # Business logic
│   │   ├── model/              # Data models
│   │   └── repository/         # Database operations
│   └── resources/
│       ├── static/             # Web UI (HTML, CSS, JS)
│       ├── data/               # Sample FAQs
│       └── application.properties
```

### Key Components
- **`VoiceAssistantController`**: Handles HTTP requests and responses
- **`RAGService`**: Implements retrieval-augmented generation
- **`GeminiService`**: Communicates with Google's Gemini AI
- **`OrderStatusService`**: Mock order tracking system
- **`DataIngestionService`**: Loads FAQs and generates embeddings



## 🚢 Deployment

### Already Deployed!
This project is live at: [https://voiceragcare-production.up.railway.app/](https://voiceragcare-production.up.railway.app/)

### Deploy Your Own

The app is ready for deployment on any Java-compatible platform:

**Railway** (Used for current deployment)
```bash
# Connect your GitHub repo and set GEMINI_API_KEY in environment variables
```

## 🔧 Customization

### Adding Your Own FAQs
Edit `src/main/resources/data/sample-faqs.json`:
```json
{
  "question": "Your question here?",
  "answer": "Your detailed answer here.",
  "category": "category-name",
  "sourceDocument": "Source Document Name"
}
```



## 📊 Monitoring

The application includes built-in health checks and debugging endpoints:
- `GET /api/assistant/health` - Service status
- `GET /api/assistant/debug/faqs` - FAQ statistics
- `GET /api/assistant/debug/search?query=...` - Search debugging






**Need help?** 
- Try the live demo: [https://voiceragcare-production.up.railway.app/](https://voiceragcare-production.up.railway.app/)
- Open an issue on GitHub
- Check the debug endpoints to see what's happening under the hood! 🎯

---

*⭐ Star this repo if you find it helpful!*
