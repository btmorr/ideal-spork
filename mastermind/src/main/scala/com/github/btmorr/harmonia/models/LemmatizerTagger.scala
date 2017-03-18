package com.github.btmorr.harmonia.models

// see http://stanfordnlp.github.io/CoreNLP/simple.html
import edu.stanford.nlp.simple._
import collection.JavaConversions._

case object LemmatizerTagger extends Model[Sentence, List[(Word, PosTag)]] {
  def apply(in: Sentence) = in.lemmas().toList.zip( in.posTags().toList )
}
