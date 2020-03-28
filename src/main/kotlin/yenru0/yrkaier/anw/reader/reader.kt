package yenru0.yrkaier.anw.reader

import java.lang.IllegalArgumentException


val ANW_VERSION = "ANW0.91"

private val PARSER_LIST_COND_BOOL_STRING_TRUE = listOf("true", "yes")
private val PARSER_LIST_COND_BOOL_STRING_FALSE = listOf("false", "no")

private val pattern_ignore_sharp = Regex("""\\#""")
private val SSHARP = """%§%SHP%§%"""

private val pattern_line_comment = Regex("""###.*""")
private val pattern_block_comment = Regex("""/##/(.|[\n])*/##/""")

private val pattern_def_dvar = Regex("""(?:\n|^)##\$([ \t]*\{[^{}]*\}|[ \t]*[^{}\n]*)""")
private val sep_def_dvar = """;"""

private val pattern_def_stage = Regex("""(?:\n|^)##\@[ \t]*(.*)""")
private val pattern_def_stage_without_group = Regex("""(?:\n|^)##\@[ \t]*.*""")

private val pattern_ignore_L1 = Regex("""\\:""")
private val pattern_ignore_L2 = Regex("""\\\/""")
private val pattern_ignore_L3 = Regex("""\\;""")

private val pattern_ignore_bracket_left = Regex("""\\\{""")
private val pattern_ignore_bracket_right = Regex("""\\\}""")

private val SL3 = """%§%SL3%§%"""
private val SL2 = """%§%SL2%§%"""
private val SL1 = """%§%SL1%§%"""

private val LBR = """%§%LBR%§%"""
private val RBR = """%§%RBR%§%"""

// ind type = "&§%ind%§&"

private val seps_L3 = ":|;" + "{}"
private val seps_L2 = ":|" + "{}"
private val seps_L1 = ":" + "{}"

private val pattern_iobject_L3 = Regex("""[ \t]*\{[^${seps_L3}]*\}[ \t]*|[^${seps_L3}\n]+""")
private val pattern_iobject_L2 = Regex("""[ \t]*\{[^${seps_L2}]*\}[ \t]*|[^${seps_L2}\n]+""")
private val pattern_iobject_L1 = Regex("""[ \t]*\{[^${seps_L1}]*\}[ \t]*|[^${seps_L1}\n]+""")

class AnwReader(anwstring: String){
    fun except_comment(string: String): String {
        var conv_string : String =
            pattern_line_comment.replace(
                pattern_block_comment.replace(
                        pattern_ignore_sharp.replace(string, SSHARP), ""
                ), ""
            )

        return conv_string.replace(SSHARP, "#", false)

    }

    fun define_default_variable(string: String): Pair<String, MutableMap<String, String>>{
        var temp1 : MutableMap<String, String> = mutableMapOf()
        var temp_string = string
        for (t in pattern_def_dvar.findAll(string)){
            temp_string = temp_string.substring(0, t.range.first) + temp_string.substring(t.range.last, temp_string.length)

            var temp_matched = Regex("""\{((?:.|[\n])*)\}""").replace(t.groupValues[1]) { m->m.groupValues[1]}
            var temp_seped = temp_matched.split(';')
            for (i in temp_seped){
                if(i.trim() == ""){
                    continue
                }
                var temp_seped_kv = i.split("=")
                temp1[temp_seped_kv[0].trim()] = temp_seped_kv[1].trim()

            }
        }
        
        return Pair(temp_string, temp1)
    }

    fun define_variable(string: String): Pair<String, MutableMap<String, String>> {
        return Pair(string, mutableMapOf())
    }

    fun define_stage(string: String): MutableMap<String, String>{
        var seped_by_stage = pattern_def_stage_without_group.split(string)
        var t = pattern_def_stage.findAll(string) as List<MatchResult>

        var temp1 : MutableMap<String, String> = mutableMapOf()

        val ft = seped_by_stage[0].trim()
        if (ft == ""){
            temp1["main"] = ft
        }

        if (seped_by_stage.size == 1) {
            return temp1
        }

        //
        seped_by_stage.subList(1, seped_by_stage.size).forEachIndexed {
                i, st ->
            if (st.trim() == "") {return@forEachIndexed}
        else{
                if (t[i].groupValues[1].trim() in temp1.keys){
                    temp1[t[i].groupValues[1].trim()] += st.trim()
                } else{
                    temp1[t[i].groupValues[1].trim()] = st.trim()
                }

            }}

        return temp1


    }
}

data class AnwWBR(val name: String, val detail_infile:AnwWBR_D,
                  val description: String?, val stages: Map<String, AnwElement>){

}

data class AnwWBR_D(val wil: Int?, val recent: Int?, val recentValue: Double?){
    init{
        wil?.takeIf{wil < 0}?.apply{throw IllegalArgumentException("'wil' must >= 0")}
        recent?.takeIf{recent<0}?.apply{throw IllegalArgumentException("'recent' must >= 0")}
        recentValue?.takeIf{recentValue<0&&recentValue>1}?.apply{throw IllegalArgumentException("'recentValue' must >= 0 and <= 1")}
    }
}

data class AnwElement(val emode: Int,
                      val answers: MutableList<MutableList<MutableList<String>>>,
                      val questions: MutableList<MutableList<MutableList<String>>>){

}



fun main(){
    val t = pattern_ignore_bracket_right.find("\\}")
    print(t?.value)
}