package rules

interface RuleSet {
    val onsets:Map<String, String>
    val nuclei:Map<String, String>
    val offglides:Map<String, String>
    val onglides:Map<String, String>
    val onoffglides:Map<String, String>
    val codas:Map<String, String>
    val tones:Map<String, String>
    val tones_p:Map<String, String>
    val gi:Map<String, String>
    val qu:Map<String, String>
}