package kr.sweetcase.harmoassist.modules.technicDictionary.labels

import kr.sweetcase.amclib.midiManager.midiController.data.Pitch

enum class TechnicLabel(val degreeArr : ByteArray, val str : String) {

    MAJ (byteArrayOf(
        Pitch.C.dec.toByte(),
        Pitch.E.dec.toByte(),
        Pitch.G.dec.toByte()
    ), ""),
    MIN (byteArrayOf(
        Pitch.C.dec.toByte(),
        Pitch.E_FLAT.dec.toByte(),
        Pitch.G.dec.toByte()
    ), "m"),
    DIM (byteArrayOf(
        Pitch.C.dec.toByte(),
        Pitch.E_FLAT.dec.toByte(),
        Pitch.G_FLAT.dec.toByte()
    ), "dim"),
    AUG (byteArrayOf(
        Pitch.C.dec.toByte(),
        Pitch.E.dec.toByte(),
        Pitch.G_SHARP.dec.toByte()
    ), "aug"),
    MAJ_MAJ_7 (byteArrayOf(
        Pitch.C.dec.toByte(),
        Pitch.E.dec.toByte(),
        Pitch.G.dec.toByte(),
        Pitch.B.dec.toByte()
    ), "M7"),
    MAJ_7 (byteArrayOf(
        Pitch.C.dec.toByte(),
        Pitch.E.dec.toByte(),
        Pitch.G.dec.toByte(),
        Pitch.B_FLAT.dec.toByte()
    ), "7"),
    MIN_7 (byteArrayOf(
        Pitch.C.dec.toByte(),
        Pitch.E_FLAT.dec.toByte(),
        Pitch.G.dec.toByte(),
        Pitch.B_FLAT.dec.toByte()
    ), "m7"),
    MIN_7_FIFTH (byteArrayOf(
        Pitch.C.dec.toByte(),
        Pitch.E_FLAT.dec.toByte(),
        Pitch.G_FLAT.dec.toByte(),
        Pitch.B_FLAT.dec.toByte()
    ), "m7(-5)"),
    DIM_7 (byteArrayOf(
        Pitch.C.dec.toByte(),
        Pitch.E_FLAT.dec.toByte(),
        Pitch.F_SHARP.dec.toByte(),
        Pitch.A.dec.toByte()
    ), "dim7"),
    _7_SUS_4 (byteArrayOf(
        Pitch.C.dec.toByte(),
        Pitch.F.dec.toByte(),
        Pitch.G.dec.toByte(),
        Pitch.B_FLAT.dec.toByte()
    ), "7sus4"),
    MAJ_6 (byteArrayOf(
        Pitch.C.dec.toByte(),
        Pitch.E.dec.toByte(),
        Pitch.G.dec.toByte(),
        Pitch.A.dec.toByte()
    ), "6"),
    MIN_6 (byteArrayOf(
        Pitch.C.dec.toByte(),
        Pitch.E_FLAT.dec.toByte(),
        Pitch.G.dec.toByte(),
        Pitch.A.dec.toByte()
    ), "m6")
}