package rules

interface RuleSet {
    val onsets:Map<String, String>
    val nuclei:Map<String, String>
    val offglides:Map<String, String>
    val onglides:Map<String, String>
    val onoffglides:Map<String, String>
    val codas:Map<String, String>
    val tones:Map<String, Any>
    val tones_p:Map<String, Int>
    val gi:Map<String, String>
    val qu:Map<String, String>
}