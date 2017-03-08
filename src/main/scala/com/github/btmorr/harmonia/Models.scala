package com.github.btmorr.harmonia

import collection.JavaConversions._

/* Models defined in this package should extend the Model trait, and define their input and output formats
 * either in terms of simple types, or define their inputs here with type definitions (a FeatureVector type
 * or case class is probably forthcoming...
 *
 * Follow the pattern demonstrated with SimpleLookupModel--any object extending Model must define an `apply`
 * method, making the object a callable that will take a relatively raw input, perform whatever computations
 * are necessary to generate a final response (including going out to other servers or databases), and then
 * return that response. For any external interactions, queries and responses should be recorded immutably
 * so that there's no question what data the model was operating upon (for instance, if a model does a map
 * search for a location, the query string used to make the request and the response from the map service
 * should be cached, so that if the result of the identical query changes in the future, the initial response
 * could still be reproduced by injeting the prior reponse).
 */
object Models {
  import edu.stanford.nlp.simple._

  type PosTag = String
  type Word = String
  sealed trait Model[Q, R] extends (Q => R)

  case object SimpleLookupModel extends Model[String, String] {
    def apply(in: String) = in.toLowerCase match {
      case i if in contains "weather" => "I'm working on learning how to look up the weather"
      case i if in contains "how are you" => "I'm doing well. How's about you?"
      case _ => "I'm not sure what you're trying to say"
    }
  }

  case object SentenceSegmenter extends Model[String, List[Sentence]] {
  	def apply(in: String) = new Document(in).sentences().toList
  }

  /* Note that this currently uses a simple tokenizer, rather than a stemmer or lemmatizer. This is because
   * the training set I grabbed for the current project was made with un-modified tokens. This could possibly
   * benefit greatly from the dimensionality reduction that comes with lemmatization (or stemming, but this is not
   * provided in Stanford SimpleNLP). Worthwhile to get the model working at any degree with the current data,
   * however, before sinking much effort into optimizing these kinds of things. Also, this will provide a base-line
   * to observe improvements in performance with dimensionality reduction, rather than guessing.
   */
  case object TokenizerTagger extends Model[Sentence, List[(Word, PosTag)]] {
    def apply(in: Sentence) = in.words().toList.zip( in.posTags().toList )
  }

  case object LemmatizerTagger extends Model[Sentence, List[(Word, PosTag)]] {
    def apply(in: Sentence) = in.lemmas().toList.zip( in.posTags().toList )
  }

  /* The Vowpal Wabbit implementation of SEARN search requires input in a particular format. This can be submitted
   * to a stationary vw model (executing vw in a subprocess with a pretrained model and the test flag, to keep the
   * model stationary, equivalent to `echo '| <tag> <word>' | vw -t -i policy.vw -P 1`, or to an active
   */
  case object SearnPredictor extends Model[(Word, PosTag), Boolean] {
    def apply(wt: (Word, PosTag)) = {
      val req = s"| ${wt._2} ${wt._1}"
      println(s"Assembled request for VW: $req")
      // submit req to vw server, get prediction
      false
    }
  }

  /* Pass-thru, just for the lolz. Says the string out loud, if you're on a mac, and then just returns it */
  case object SayIt extends Model[String, String] {
    def apply(in: String) = {
      import scala.sys.process._
      s"say $in".!
      in
    }
  }
}
