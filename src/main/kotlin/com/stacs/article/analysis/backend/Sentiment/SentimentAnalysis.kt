package com.stacs.article.analysis.backend.Sentiment

import com.stacs.article.analysis.backend.Apis.SentimentApiService
import kotlin.random.Random

enum class Sentiment {
    SCORE, MAGNITUDE
}

class SentimentAnalysis(sentimentService: SentimentApiService = SentimentApiService()) {

    fun analyzeSentiment(content: String?, sentiment: Sentiment): Float {
        if (!content.isNullOrBlank() && content != "Not available") {
            // TODO: sentimentService für die Analyse verwenden
            if(sentiment == Sentiment.SCORE) {
                return Random.nextFloat() * 2 - 1
            } else {
                return Random.nextFloat() * 2 - 1
            }
        }
        return 0.0f
    }

    fun analyzeSentiment(content: List<String?>, sentiment: Sentiment): Float {
        val sentiments = content.map { analyzeSentiment(it, sentiment) }

        val averageScore = sentiments.average().toFloat()
        val averageMagnitude = sentiments.average().toFloat()

        if(sentiment == Sentiment.SCORE) {
            return averageScore
        } else {
            return averageMagnitude
        }
    }
}