import rules.Central
import rules.North
import rules.South

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//       vPhonKotlin, based on vPhon version 0.2.6
//       Original Work Copyright 2008-2016 James Kirby <j.kirby@ed.ac.uk>
//       Modifications Copyright 2020 Adam Howard <github.com/medavox>
//
//       vPhonKotlin is free software: you can redistribute it and/or modify
//       it under the terms of the GNU General Public License as published by
//       the Free Software Foundation, either version 3 of the License, or 
//       (at your option) any later version.                  
//
//       vPhonKotlin is distributed in the hope that it will be useful,
//       but WITHOUT ANY WARRANTY; without even the implied warranty of 
//       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
//       GNU General Public License for more details.            
//
//       You should have received a copy of the GNU General Public License 
//       along with vPhonKotlin.  If not, see <http://www.gnu.org/licenses/>.
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/*
import sys, codecs, re, StringIO
from optparse import OptionParser
from string import punctuation*/

fun trans(word:String, dialect:String, glottal:Boolean, pham:Boolean, cao:Boolean, palatals:Boolean):Results? {

    // This looks ugly, but newer versions of python complain about "from x import *" syntax
    val rules = when (dialect) {
        "n" -> North
        "c" -> Central
        "s" -> South
        else -> throw Exception("dialect must be one of 'n', 'c', or 's'")
    }

    var ons:String? = ""
    var nuc:String? = ""
    var cod:String? = ""
    var ton:String? = ""
    var oOffset = 0
    var cOffset = 0
    val l = word.length

    if(l <= 0) return null
    with (rules) {
        val tonez = if (pham || cao) {
             tones_p
        }else { tones }
        if (word.substring(0, 3) in onsets) {         // if onset is 'ngh'
            ons = onsets[word.substring(0, 3)] ?: ""
            oOffset = 3
        } else if (word.substring(0, 2) in onsets) { // if onset is 'nh', 'gh', 'kʷ' etc
            ons = onsets[word.substring(0, 2)] ?: ""
            oOffset = 2
        } else if (word.ket(0) in onsets) {         // if single onset
            ons = onsets[word.ket(0)]
            oOffset = 1
        }

        if (word.substring(l-2, l) in codas) {        // if two-character coda
            cod = codas[word.substring(l-2, l)]
            cOffset = 2
        } else if (word.ket(l-1) in codas) {        // if one-character coda
            cod = codas[word.ket(l-1)]
            cOffset = 1
        }


        //if word.substring(0, 2) == "gi" and cod and word.length == 3:  // if you just have 'gi' and a coda...
        val nucl = if (word.substring(0, 2) in gi && cod?.isNotEmpty() == true && word.length == 3) {// if you just have 'gi' and a coda...
            ons = "z"
            "i"
        } else {
            word.substring(oOffset, l-cOffset)
        }

        if (nucl in nuclei) {
            if (oOffset == 0) {
                if (glottal) {
                    if (word.ket(0) !in onsets) {   // if there isn't an onset....
                        ons = "ʔ"+nuclei[nucl] // add a glottal stop
                    } else {
                        nuc = nuclei[nucl]      // there's your nucleus
                    }
                } else {
                    nuc = nuclei[nucl]          // there's your nucleus
                }
            } else {
                nuc = nuclei[nucl]              // there's your nucleus
            }

        } else if (nucl in onglides && ons != "kw") { // if there is an onglide...
            nuc = onglides[nucl]                // modify the nuc accordingly
            if (ons?.isNotEmpty() == true) {                             // if there is an onset...
                ons = ons+"w"                  // labialize it, but...
            } else {
                ons = "w"                      // add a labiovelar onset
            }
        } else if (nucl in onglides && ons == "kw") {
            nuc = onglides[nucl]
        } else if (nucl in onoffglides) {
            cod = onoffglides[nucl]?.ket(-1)
            nuc = onoffglides[nucl]?.get(0, -1)
            if (ons != "kw") {
                if (ons?.isNotEmpty() == true) {
                    ons = ons+"w"
                } else {
                    ons = "w"
                }
            }
        } else if (nucl in offglides) {
            //cod = offglides[nucl][-1]
            cod = offglides[nucl]?.ket(-1)
            //nuc = offglides[nucl][:-1]
            nuc = offglides[nucl]?.get(0, -1)

        } else if (word in gi) {      // if word == 'gi', 'gì',...
            //ons = gi[word][0]
            ons = gi[word]?.ket(0)
            //nuc = gi[word][1]
            nuc = gi[word]?.ket(1)

        } else if (word in qu) {      // if word == 'quy', 'qúy',...
            //ons = qu[word][:-1]
            ons = qu[word]?.get(0, -1)
            //nuc = qu[word][-1]
            nuc = qu[word]?.ket(-1)
        } else {
            // Something is non-Viet
            return null
        }

        // Velar Fronting (Northern dialect)
        if (dialect == "n") {
            if (nuc == "a") {
                if (cod == "k" && cOffset == 2) { nuc = "ɛ"}
                if (cod == "ɲ" && nuc == "a") { nuc = "ɛ"}
            }

            // Final palatals (Northern dialect)
            if (nuc !in arrayOf("i", "e", "ɛ")) {
                if (cod == "ɲ") { cod = "ŋ"}
            } else if (!palatals && nuc in arrayOf("i", "e", "ɛ")) {
                if (cod == "ɲ") { cod = "ŋ"}
            }

            if (palatals) {
                if (cod == "k" && nuc in arrayOf("i", "e", "ɛ")) {
                    cod = "c"
                }
            }

        // Velar Fronting (Southern and Central dialects)
        } else {
            if (nuc in arrayOf("i", "e")) {
                if (cod == "k") { cod = "t"}
                if (cod == "ŋ") { cod = "n"}
            // There is also this reverse fronting, see Thompson 1965:94 ff.
            } else if (nuc in arrayOf("iə", "ɯə", "uə", "u", "ɯ", "ɤ", "o", "ɔ", "ă", "ɤ̆")) {
                if (cod == "t") {
                    cod = "k"
                }
                if (cod == "n") {
                    cod = "ŋ"
                }
            }
        }
        // Monophthongization (Southern dialects: Thompson 1965: 86; Hoàng 1985: 181)
        if (dialect == "s") {
            if (cod in arrayOf("m", "p")) {
                if (nuc == "iə") { nuc = "i"}
                if (nuc == "uə") { nuc = "u"}
                if (nuc == "ɯə") { nuc = "ɯ"}
            }
        }

        // Tones
        // Modified 20 Sep 2008 to fix aberrant 33 error
        //tonelist = [tones[word[i]] for i in xrange(0,l) if word[i] in tones]
        val tonelist = word.map { char -> tonez[char.toString()] }.filterNotNull()
        if (tonelist.isNotEmpty()) {
            ton = tonelist[tonelist.size-1]
        } else if (!(pham || cao)) {
            if (dialect == "c") {
                ton = "35"
            } else {
                ton = "33"
            }
        } else {
            ton = "1"
        }

        // Modifications for closed syllables
        if (cOffset !=0) {
            // Obstruent-final nang tones are modal voice
            if ((dialect == "n" || dialect == "s") && ton == "21g" && cod in arrayOf("p", "t", "k")) {
                //if ton == "21\u02C0" and cod in ["p", "t", "k"]: // fixed 8 Nov 2016
                ton = "21"
            }

            // Modification for sắc in closed syllables (Northern and Central only)
            if (((dialect == "n" && ton == "24") || (dialect == "c" && ton == "13")) && cod in arrayOf("p", "t", "k")) {
                ton = "45"
            }

            // Modification for 8-tone system
            if (cao) {
                if (ton == "5" && cod in arrayOf("p", "t", "k")) {
                    ton = "5b"
                }
                if (ton == "6" && cod in arrayOf("p", "t", "k")) {
                    ton = "6b"
                }
            }

            // labialized allophony (added 17.09.08)
            if (nuc in arrayOf("u", "o", "ɔ")) {
                if (cod == "ŋ") {
                    cod = "ŋ͡m"
                }
                if (cod == "k") {
                    cod = "k͡p"
                }
            }
        }
        val ons2 = ons
        val nuc2 = nuc
        val cod2 = cod
        val ton2 = ton
        if(ons2 != null && nuc2 != null && cod2 != null && ton2 != null) {
            return Results(ons2, nuc2, cod2, ton2)
        }else {return null}
    }
}

/**Convert a single orthographic string to IPA.*/
//word:String, dialect:String, glottal:Boolean, pham:Boolean, cao:Boolean, palatals:Boolean
fun convert(word:String, dialect:String, glottal:Boolean, pham:Boolean, cao:Boolean, palatals:Boolean,
            delimit:String):String {

    val results = trans(word, dialect, glottal, pham, cao, palatals)
    val seq = if(results != null) {
        val (ons, nuc, cod, ton) = results

        delimit + delimit.join(filter(None, (ons, nuc, cod, ton)))+delimit
    } else {
        "[" + word + "]"
    }

    return seq
}
fun main() {
    val usage = "python vPhon.py <input> -d, --dialect N|C|S"

    var glottal = false
    var pham = false
    var cao = false
    var palatals = false
    var tokenize = false
    var output_ortho = false
    var delimit = ""

    // Command line options
    /*parser.add_option("-g", "--glottal", action="store_true", dest="glottal", help="prepend glottal stops to onsetless syllables")
    parser.add_option("-6", "--pham", action="store_true", dest="pham", help="phonetize tones as 1-6")
    parser.add_option("-8", "--cao", action="store_true", dest="cao", help="phonetize tones as 1-4 + 5, 5b, 6, 6b")
    parser.add_option("-p", "--palatals", action="store_true", dest="palatals", help="use word-final palatal velars in Northern dialects")
    parser.add_option("-t", "--tokenize", action="store_true", dest="tokenize", help="preserve underscores or hyphens in tokenized inputs (e.g., anh_ta = anh1_ta1)")
    parser.add_option("-o", "--ortho", action="store_true", dest="output_ortho", help="output orthography as well as IPA")
    parser.add_option("-m", "--delimit", action="store", type="string", dest="delimit", help="produce explicitly delimited output (e.g., bi ca = .b.i.33. .k.a.33.")
    parser.add_option("-d", "--dialect", action="store", type="string", dest="dialect", help="specify dialect region ([N]orthern, [C]entral, [S]outhern)")*/
    val options = Options("", "c")

    if (options.glottal) {
        glottal = true
    }
    if (options.pham) {
        pham = true
    }
    if (options.cao) {
        cao = true
    }
    if (options.palatals) {
        palatals = true
    }
    if (options.tokenize) {
        tokenize = true
    }
    if (options.output_ortho) {
        output_ortho = true
    }/*
    if (options.delimit.isNotEmpty()) {
        delimit = options.delimit[0]
    }
    if (options.dialect) {
        dialect = options.dialect[0].toLowerCase()
    } else {
        parser.error("Please enter a valid dialect.")
    }
    if (dialect !in arrayOf("n", "c", "s")) {
        parser.error('Please enter a valid dialect.')
    }*/

    // read from stdin, parse the input
/*    var line = readLine()
    while (line != null) {
        if (line != "\n") {
            var compound = ""
            var ortho = ""
            var words = line.split()
            // toss len==0 junk
            words = [word for word in words if word.length>0]
            // hack to get rid of single hyphens or underscores
            words = [word for word in words if word != "-"]
            words = [word for word in words if word != "_"]
            for (i in xrange(0,words.length)) {
                val word = words[i].strip()
                ortho += word
                word = word.strip(punctuation).lower()
                // 29.03.16: check if tokenize is true
                // if true, call this routine for each substring
                // and re-concatenate
                if ((tokenize && "-" in word) || (tokenize && "_" in word)) {
                    val substrings = re.split(r'(_|-)', word)
                    val values = substrings[::2]
                    val delimiters = substrings[1::2] + [""]
                    val ipa = [convert(x, dialect, glottal, pham, cao, palatals, delimit).strip() for x in values]
                    val seq = "".join(v+d for v,d in zip(ipa, delimiters))
                } else {
                    val seq = convert(word, dialect, glottal, pham, cao, palatals, delimit).strip()
                }
                // concatenate
                if (words.length >= 2) {
                    ortho += " "
                }
                if (i < words.length-1) {
                    seq = seq+" "
                }
                compound = compound + seq
            }
            //// entire line has been parsed
            if (ortho != "") {
                ortho = ortho.strip()
                //// print orthography?
                if (output_ortho) { print(ortho.encode('utf-8'), end=",")}
                print(compound.encode('utf-8'))
            }
        }
        line = readLine()
    }*/
}

