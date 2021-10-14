package com.fitsionary.momspt.data.enum

enum class RankEnum(val rankName: String) {
    A_PLUS("A+"), A("A"), B("B"), C("C"), D("D"), NONE("-");

    companion object {
        fun makeScoreToRank(score: Int) = (when (score) {
            in 0..9 -> D
            in 10..29 -> C
            in 30..59 -> B
            in 60..89 -> A
            in 90..100 -> A_PLUS
            else -> NONE
        }).name
    }
}