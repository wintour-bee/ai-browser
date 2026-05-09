const express = require('express');
const router = express.Router();
const axios = require('axios');

// AI API routes
const AI_PROVIDERS = {
    CHATGPT: {
        baseUrl: 'https://api.openai.com/v1',
        apiKey: process.env.OPENAI_API_KEY
    },
    CLAUDE: {
        baseUrl: 'https://api.anthropic.com/v1',
        apiKey: process.env.ANTHROPIC_API_KEY
    },
    GEMINI: {
        baseUrl: 'https://generativelanguage.googleapis.com/v1',
        apiKey: process.env.GOOGLE_AI_API_KEY
    },
    DEEPSEEK: {
        baseUrl: 'https://api.deepseek.com/v1',
        apiKey: process.env.DEEPSEEK_API_KEY
    }
};

// Chat endpoint
router.post('/chat', async (req, res) => {
    try {
        const { provider, model, messages, temperature = 0.7, maxTokens = 4096 } = req.body;

        const config = AI_PROVIDERS[provider];
        if (!config) {
            return res.status(400).json({ error: 'Invalid AI provider' });
        }

        let response;
        
        switch (provider) {
            case 'CHATGPT':
                response = await axios.post(`${config.baseUrl}/chat/completions`, {
                    model,
                    messages,
                    temperature,
                    max_tokens: maxTokens
                }, {
                    headers: {
                        'Authorization': `Bearer ${config.apiKey}`,
                        'Content-Type': 'application/json'
                    }
                });
                res.json({
                    id: response.data.id,
                    content: response.data.choices[0].message.content,
                    model: response.data.model,
                    usage: response.data.usage
                });
                break;

            case 'CLAUDE':
                response = await axios.post(`${config.baseUrl}/messages`, {
                    model,
                    messages: messages.filter(m => m.role !== 'system'),
                    system: messages.find(m => m.role === 'system')?.content,
                    max_tokens: maxTokens
                }, {
                    headers: {
                        'x-api-key': config.apiKey,
                        'Content-Type': 'application/json',
                        'anthropic-version': '2023-06-01'
                    }
                });
                res.json({
                    id: response.data.id,
                    content: response.data.content[0].text,
                    model: response.data.model
                });
                break;

            default:
                res.status(400).json({ error: 'Provider not fully implemented' });
        }
    } catch (error) {
        console.error('AI chat error:', error.response?.data || error.message);
        res.status(500).json({ error: 'AI request failed' });
    }
});

// Summarize endpoint
router.post('/summarize', async (req, res) => {
    try {
        const { provider, url, content, language = 'auto', maxLength = 500 } = req.body;

        // Use ChatGPT for summarization
        const summaryPrompt = `Summarize the following content in ${maxLength} characters or less. Provide a concise summary and key points.

Content: ${content}

Summary:`;

        const response = await axios.post(`${AI_PROVIDERS.CHATGPT.baseUrl}/chat/completions`, {
            model: 'gpt-3.5-turbo',
            messages: [{ role: 'user', content: summaryPrompt }],
            max_tokens: 500
        }, {
            headers: {
                'Authorization': `Bearer ${AI_PROVIDERS.CHATGPT.apiKey}`,
                'Content-Type': 'application/json'
            }
        });

        const summary = response.data.choices[0].message.content;
        const wordCount = content.split(/\s+/).length;
        const estimatedReadTime = Math.ceil(wordCount / 200);

        res.json({
            summary,
            keyPoints: extractKeyPoints(summary),
            estimatedReadTime
        });
    } catch (error) {
        console.error('Summarize error:', error);
        res.status(500).json({ error: 'Summarization failed' });
    }
});

// Translate endpoint
router.post('/translate', async (req, res) => {
    try {
        const { provider, text, sourceLanguage = 'auto', targetLanguage } = req.body;

        const translatePrompt = `Translate the following text from ${sourceLanguage === 'auto' ? 'the detected language' : sourceLanguage} to ${targetLanguage}:

${text}

Translation:`;

        const response = await axios.post(`${AI_PROVIDERS.CHATGPT.baseUrl}/chat/completions`, {
            model: 'gpt-3.5-turbo',
            messages: [{ role: 'user', content: translatePrompt }],
            max_tokens: 2000
        }, {
            headers: {
                'Authorization': `Bearer ${AI_PROVIDERS.CHATGPT.apiKey}`,
                'Content-Type': 'application/json'
            }
        });

        res.json({
            translatedText: response.data.choices[0].message.content,
            detectedLanguage: sourceLanguage === 'auto' ? 'en' : sourceLanguage
        });
    } catch (error) {
        console.error('Translate error:', error);
        res.status(500).json({ error: 'Translation failed' });
    }
});

// OCR endpoint (using vision model)
router.post('/ocr', async (req, res) => {
    try {
        const { provider, imageUrl, imageBase64 } = req.body;

        const ocrPrompt = 'Extract all text from this image. Return only the text content.';

        const response = await axios.post(`${AI_PROVIDERS.CHATGPT.baseUrl}/chat/completions`, {
            model: 'gpt-4o',
            messages: [{
                role: 'user',
                content: [
                    { type: 'text', text: ocrPrompt },
                    { 
                        type: 'image_url', 
                        image_url: { url: imageUrl || `data:image/jpeg;base64,${imageBase64}` }
                    }
                ]
            }],
            max_tokens: 4000
        }, {
            headers: {
                'Authorization': `Bearer ${AI_PROVIDERS.CHATGPT.apiKey}`,
                'Content-Type': 'application/json'
            }
        });

        res.json({
            text: response.data.choices[0].message.content,
            confidence: 0.95
        });
    } catch (error) {
        console.error('OCR error:', error);
        res.status(500).json({ error: 'OCR failed' });
    }
});

// Get available models
router.get('/models', async (req, res) => {
    const { provider } = req.query;

    const models = {
        CHATGPT: [
            { id: 'gpt-4o', name: 'GPT-4o', maxTokens: 128000, provider: 'CHATGPT' },
            { id: 'gpt-4-turbo', name: 'GPT-4 Turbo', maxTokens: 128000, provider: 'CHATGPT' },
            { id: 'gpt-4', name: 'GPT-4', maxTokens: 8192, provider: 'CHATGPT' },
            { id: 'gpt-3.5-turbo', name: 'GPT-3.5 Turbo', maxTokens: 16385, provider: 'CHATGPT' }
        ],
        CLAUDE: [
            { id: 'claude-3-opus', name: 'Claude 3 Opus', maxTokens: 200000, provider: 'CLAUDE' },
            { id: 'claude-3-sonnet', name: 'Claude 3 Sonnet', maxTokens: 200000, provider: 'CLAUDE' },
            { id: 'claude-3-haiku', name: 'Claude 3 Haiku', maxTokens: 200000, provider: 'CLAUDE' }
        ],
        GEMINI: [
            { id: 'gemini-1.5-pro', name: 'Gemini 1.5 Pro', maxTokens: 1000000, provider: 'GEMINI' },
            { id: 'gemini-pro', name: 'Gemini Pro', maxTokens: 32768, provider: 'GEMINI' }
        ],
        DEEPSEEK: [
            { id: 'deepseek-chat', name: 'DeepSeek Chat', maxTokens: 16384, provider: 'DEEPSEEK' },
            { id: 'deepseek-coder', name: 'DeepSeek Coder', maxTokens: 16384, provider: 'DEEPSEEK' }
        ]
    };

    if (provider) {
        return res.json(models[provider] || []);
    }

    res.json(Object.values(models).flat());
});

function extractKeyPoints(text) {
    // Simple extraction - in production, use more sophisticated methods
    const sentences = text.split(/[.!?]+/).filter(s => s.trim().length > 10);
    return sentences.slice(0, 5).map(s => s.trim());
}

module.exports = router;
