package stringsearch.constructs

import daffodil.dsom.DFDLEscapeScheme
import daffodil.schema.annotation.props.gen.EscapeKind
import daffodil.exceptions.Assert
import daffodil.dsom.EntityReplacer
import daffodil.dsom.StringValueAsLiteral
import daffodil.dsom.SingleCharacterLiteral

object SearchResult extends Enumeration {
  type SearchResult = Value
  val FullMatch, PartialMatch, NoMatch = Value
}

object SearchState extends Enumeration {
  type SearchState = Value
  val WSPNoMatch, WSPMatch, WSPPlusNoMatch, WSPPlusMatch, WSPStarNoMatch, WSPStarMatch = Value
  val WSPModeAndSpace, NLCrlfExists, NLCrlfPartial, NLCrlfNotFound, NLNoMatch = Value
  val SpaceAndNotWSPMode, SpaceAndWSPMode, OtherMatch, OtherNoMatch, NoMatch = Value
}

object CRLFState extends Enumeration {
  type CRLFState = Value
  val Exists, NotFound, Partial = Value
}

object EscapeSchemeKind extends Enumeration {
  type EscapeSchemeKind = Value
  val Character, Block, None = Value
}

import SearchState._
import CRLFState._
import EscapeSchemeKind.EscapeSchemeKind

object EscapeScheme {

  class EscapeSchemeObj {
    var escapeSchemeKind: EscapeSchemeKind = EscapeSchemeKind.None
    var escapeCharacter = ""
    var escapeEscapeCharacter = ""
    var escapeBlockStart = ""
    var escapeBlockEnd = ""
  }

  def getEscapeScheme(pEs: Option[DFDLEscapeScheme]): EscapeSchemeObj = {
    var escapeSchemeKind = EscapeSchemeKind.None
    var escapeCharacter = ""
    var escapeEscapeCharacter = ""
    var escapeBlockStart = ""
    var escapeBlockEnd = ""

    pEs match {
      case None => escapeSchemeKind = EscapeSchemeKind.None
      case Some(obj) => {
        obj.escapeKind match {
          case EscapeKind.EscapeBlock => {
            escapeSchemeKind = EscapeSchemeKind.Block
            escapeEscapeCharacter = {
              val l = new daffodil.dsom.SingleCharacterLiteralES(obj.escapeEscapeCharacterRaw)
              l.cooked
            }
            escapeBlockStart = {
              val l = new StringValueAsLiteral(obj.escapeBlockStart)
              l.cooked
            }
            escapeBlockEnd = {
              val l = new StringValueAsLiteral(obj.escapeBlockEnd)
              l.cooked
            }
          }
          case EscapeKind.EscapeCharacter => {
            escapeSchemeKind = EscapeSchemeKind.Character
            escapeEscapeCharacter = {
              val l = new daffodil.dsom.SingleCharacterLiteralES(obj.escapeEscapeCharacterRaw)
              l.cooked
            }
            escapeCharacter = {
              val l = new daffodil.dsom.SingleCharacterLiteralES(obj.escapeCharacterRaw)
              l.cooked
            }
          }
          case _ => Assert.SDE("Unrecognized Escape Scheme!")
        }
      }
    }

    println("EscapeSchemeKind: " + escapeSchemeKind)
    println("\tEscapeCharacter: " + escapeCharacter)
    println("\tEscapeEscapeCharacter: " + escapeEscapeCharacter)
    println("\tEscapeBlockStart: " + escapeBlockStart)
    println("\tEscapeBlockEnd: " + escapeBlockEnd)

    val result = new EscapeSchemeObj
    result.escapeSchemeKind = escapeSchemeKind
    result.escapeCharacter = escapeCharacter
    result.escapeEscapeCharacter = escapeEscapeCharacter
    result.escapeBlockStart = escapeBlockStart
    result.escapeBlockEnd = escapeBlockEnd
    result
  }
}