package com.example.boardgamebuddy;

public class AnswerNotRelevantException extends RuntimeException {

    public AnswerNotRelevantException(Question question, Answer answer) {
        super("The answer '" + answer.answer() + "' is not relevant to the question '" + question.question() + "' .");
    }

}
