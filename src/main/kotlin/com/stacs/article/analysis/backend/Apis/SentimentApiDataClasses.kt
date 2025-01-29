package com.stacs.article.analysis.Sentiment

data class Document(
    val type: String,
    val content: String
)

data class SentimentRequest(
    val encodingType: String,
    val document: Document
)

data class SentimentResponse(
    val documentSentiment: DocumentSentiment,
    val language: String?,
    val sentences: List<Sentence>
)

data class DocumentSentiment(
    val magnitude: Double,
    val score: Double
)

data class Sentence(
    val text: Text,
    val sentiment: Sentiment
)

data class Text(
    val content: String,
    val beginOffset: Int
)

data class Sentiment(
    val magnitude: Double,
    val score: Double
)
