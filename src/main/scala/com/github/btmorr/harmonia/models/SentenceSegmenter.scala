package com.github.btmorr.harmonia.models

// see http://stanfordnlp.github.io/CoreNLP/simple.html
import edu.stanford.nlp.simple._
import collection.JavaConversions._

case object SentenceSegmenter extends Model[String, List[Sentence]] {
  def apply(in: String) = new Document(in).sentences().toList
}
